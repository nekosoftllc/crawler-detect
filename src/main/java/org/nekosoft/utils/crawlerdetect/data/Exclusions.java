package org.nekosoft.utils.crawlerdetect.data;

import org.nekosoft.utils.crawlerdetect.AbstractDataProvider;
import org.nekosoft.utils.crawlerdetect.Detector;

import java.io.IOException;
import java.net.URL;

/**
 * This {@link AbstractDataProvider} is preconfigured to load User Agent Exclusions (see {@link Detector#getUaExclusions()})
 * from the <a href="https://crawlerdetect.io">Github repository</a> of the original PHP project.
 */
public class Exclusions extends AbstractDataProvider {

    /**
     * Creates a new instance of this {@link AbstractDataProvider} that loads data from the
     * <a href="https://raw.githubusercontent.com/JayBizzle/Crawler-Detect/master/raw/Exclusions.txt">Github repository</a>
     * of the original PHP project
     * @throws IOException if there were issues downloading data from the source URL
     */
    public Exclusions() throws IOException {
        super(new URL("https://raw.githubusercontent.com/JayBizzle/Crawler-Detect/master/raw/Exclusions.txt"));
    }

}
