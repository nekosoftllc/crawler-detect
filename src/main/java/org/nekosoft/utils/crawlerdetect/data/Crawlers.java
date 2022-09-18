package org.nekosoft.utils.crawlerdetect.data;

import org.nekosoft.utils.crawlerdetect.AbstractDataProvider;
import org.nekosoft.utils.crawlerdetect.Detector;

import java.io.IOException;
import java.net.URL;

/**
 * This {@link AbstractDataProvider} is preconfigured to load Crawler Patterns (see {@link Detector#getCrawlerPatterns()})
 * from the <a href="https://crawlerdetect.io">Github repository</a> of the original PHP project.
 */
public class Crawlers extends AbstractDataProvider {

    /**
     * Creates a new instance of this {@link AbstractDataProvider} that loads data from the
     * <a href="https://raw.githubusercontent.com/JayBizzle/Crawler-Detect/master/raw/Crawlers.txt">Github repository</a>
     * of the original PHP project
     * @throws IOException if there were issues downloading data from the source URL
     */
    public Crawlers() throws IOException {
        super(new URL("https://raw.githubusercontent.com/JayBizzle/Crawler-Detect/master/raw/Crawlers.txt"));
    }

}
