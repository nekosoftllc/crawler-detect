package org.nekosoft.utils.crawlerdetect.data;

import org.nekosoft.utils.crawlerdetect.AbstractDataProvider;
import org.nekosoft.utils.crawlerdetect.Detector;

import java.io.IOException;
import java.net.URL;
import java.util.stream.Collectors;

/**
 * This {@link AbstractDataProvider} is preconfigured to load Headers To Check (see {@link Detector#getHeadersToCheck()})
 * from the <a href="https://crawlerdetect.io">Github repository</a> of the original PHP project.
 * <p>
 * This class needs to make some changes to the original data to account for differences in the way
 * that Java and PHP access HTTP header information (see {@link #reloadData()}).
 */
public class Headers extends AbstractDataProvider {

    /**
     * Creates a new instance of this {@link AbstractDataProvider} that loads data from the
     * <a href="https://raw.githubusercontent.com/JayBizzle/Crawler-Detect/master/raw/Headers.txt">Github repository</a>
     * of the original PHP project.
     * @throws IOException if there were issues downloading data from the source URL
     */
    public Headers() throws IOException {
        super(new URL("https://raw.githubusercontent.com/JayBizzle/Crawler-Detect/master/raw/Headers.txt"));
    }

    /**
     * This class needs to make some changes to the original data to account for differences in the way
     * that Java and PHP access HTTP header information.
     * <p>
     * The {@code reloadData} method strips every value of the {@code HTTP_} prefix, replaces {@code _} with {@code -},
     * and turns the string to lower case. E.g.
     * <pre>
     *     "HTTP_USER_AGENT" becomes "user-agent"
     * </pre>
     */
    @Override
    public void reloadData() throws IOException {
        super.reloadData();
        data = data.stream().map(s ->
                (s.startsWith("HTTP_") ? s.substring(5) : s).toLowerCase().replace('_','-')
        ).collect(Collectors.toList());
    }

}
