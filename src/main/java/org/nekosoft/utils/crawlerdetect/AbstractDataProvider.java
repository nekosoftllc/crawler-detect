package org.nekosoft.utils.crawlerdetect;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is the abstract parent of
 * <ul>
 * <li>{@link org.nekosoft.utils.crawlerdetect.data.Crawlers}</li>
 * <li>{@link org.nekosoft.utils.crawlerdetect.data.Exclusions}</li>
 * <li>{@link org.nekosoft.utils.crawlerdetect.data.Headers}</li>
 * </ul>
 * as well as the following inner classes in the {@code test} code
 * <ul>
 * <li>{@code org.nekosoft.utils.crawlerdetect.CrawlerDetectTests$TestCrawlerList}</li>
 * <li>{@code org.nekosoft.utils.crawlerdetect.CrawlerDetectTests$TestDeviceList}</li>
 * </ul>
 * NB: The subclasses do not really add anything to the logic of the superclass and exist merely to reflect the API of the
 * original PHP library. The only one that has some additional behaviour is {@code Headers}, which removes the {@code HTTP_} prefix
 * from all lines, as it is not necessary as part of an HTTP request header name in Java code.
 * <p>
 * This abstract class implements the data loading behaviour that is used by the default implementation of the
 * CrawlerDetect interface and that will load the data from the URLs in the original PHP CrawlerDetect repository and
 * store the data in local files in a temporary folder. The files are then reused in subsequent runs of the application,
 * unless they are older than 31 days: in that case the data is reloaded again from the original URLs.
 * <p>
 * The location of the cache files can be changed with the following system property
 * <pre>
 * -Dcrawlerdetect.cfg.baseDir=&lt;stringPath>
 * </pre>
 * The frequency for refreshing the cache files is expressed in number of days and set with the following system property
 * <pre>
 * -Dcrawlerdetect.cfg.refreshDays=&lt;intNumberOfDays>
 * </pre>
 * If the value of {@code crawlerdetect.cfg.refreshDays} is &lt;= 0, the data is never refreshed, and the cached files
 * will be used indefinitely until manually deleted.
 */
public class AbstractDataProvider {

    /**
     * The URL representing the source of data for this data provider. It is available in subclasses
     * in case they need to alter the source URL before data is downloaded. Usually done as part of an
     * override of the {@link #reloadData()} method, before a call to the superclass.
     * <pre>
     *     public void reloadData() throws IOException {
     *         // do something with the source variable
     *         super.reloadData();
     *     }
     * </pre>
     */
    protected URL source;

    /**
     * The data contained in this data provider. It is available in subclasses
     * for further processing after the standard loading process. Usually done as part of an
     * override of {@link #reloadData()}, after a call to the superclass.
     * <pre>
     *     public void reloadData() throws IOException {
     *         super.reloadData();
     *         // do something with the data variable
     *     }
     * </pre>
     * See {@link org.nekosoft.utils.crawlerdetect.data.Headers}.
     */
    protected List<String> data;

    /**
     * Creates a new data provider that uses a fixed data set, given as the constructor parameter.
     * No data loading will happen and no data reload happens over the lifetime of the instance.
     * The data set is fixed.
     * @param data the data to be used by this instance
     */
    public AbstractDataProvider(List<String> data) {
        this.data = data;
    }

    /**
     * Creates a new data provider from the data at the given URL. The URL should point to a list of strings, one per line.
     * The data will be loaded from the URL on construction and can be reloaded on demand at any point over the lifecycle
     * of the instance by calling the {@link #reloadData()} method.
     * @param source the URL from which this instance will load data initially and reload data on request
     * @throws IOException if there were issues downloading data from the source URL
     */
    public AbstractDataProvider(URL source) throws IOException {
        this.source = source;
        reloadData();
    }

    /**
     * This method refreshes the data of this instance if the instance was created with a source URL.
     * If the instance was created with a list of strings, this method does nothing.
     * The behaviour of reloading from source is controlled by two system properties. The first specifies the folder
     * where the downloaded data is stored. If not set, the method will use the system's temporary folder:
     * <pre>
     * -Dcrawlerdetect.cfg.baseDir=&lt;stringPath>
     * </pre>
     * The second property determines the frequency for refreshing the cache files and is expressed in number of days:
     * <pre>
     * -Dcrawlerdetect.cfg.refreshDays=&lt;intNumberOfDays>
     * </pre>
     * If the value of {@code crawlerdetect.cfg.refreshDays} is &lt;= 0, the data is never refreshed, and the cached files
     * will be used indefinitely, unless manually deleted from the filesystem.
     * @throws IOException if there is an issue opening and reading from the source URL, or writing to and reading from
     * the cache files
     */
    public void reloadData() throws IOException {
        URL source = this.source; // copy into local variable, so I can replace it with cached file if one is present
        if (source == null) return;
        String baseDir = System.getProperty("crawlerdetect.cfg.baseDir", System.getProperty("java.io.tmpdir"));
        int refreshDays = Integer.parseInt(System.getProperty("crawlerdetect.cfg.refreshDays", "31"));
        Path cacheDir = Path.of(baseDir, "CrawlerDetectCache");
        if (!cacheDir.toFile().isDirectory()) {
            Files.createDirectory(cacheDir);
        }
        File file = cacheDir.resolve(getClass().getName() + ".txt").toFile();
        if (file.isFile()) {
            BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            if (refreshDays > 0 && attr.lastModifiedTime().toInstant().isBefore(Instant.now().minus(refreshDays, ChronoUnit.DAYS))) {
                if (!file.delete()) {
                    throw new IllegalStateException("Could not delete cache file " + file);
                }
            } else {
                source = file.toURI().toURL();
            }
        }
        try (InputStream in = source.openStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            data = reader.lines().collect(Collectors.toList());
        }
        if (!file.exists()) {
            try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
                for (String line : data) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
    }

    /**
     * Returns the data held by this instance, as a list of Strings. The data can be loaded at construction and on demand
     * from the provided URL, or set directly on the instance at construction time.
     * @return the list of strings held by this instance
     */
    public List<String> getAllValues() {
        return Collections.unmodifiableList(data);
    }

}
