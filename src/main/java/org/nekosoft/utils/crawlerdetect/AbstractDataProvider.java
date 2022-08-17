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

public class AbstractDataProvider {

    protected URL source;

    protected List<String> data;

    public AbstractDataProvider(List<String> data) {
        this.data = data;
    }

    public AbstractDataProvider(URL source) throws IOException {
        this.source = source;
        reloadData();
    }

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
            data = reader.lines().toList();
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

    public List<String> getAllValues() {
        return Collections.unmodifiableList(data);
    }

}
