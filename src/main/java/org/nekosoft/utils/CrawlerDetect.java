package org.nekosoft.utils;

import org.nekosoft.utils.crawlerdetect.Detector;
import org.nekosoft.utils.crawlerdetect.data.Crawlers;
import org.nekosoft.utils.crawlerdetect.data.Exclusions;
import org.nekosoft.utils.crawlerdetect.data.Headers;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public interface CrawlerDetect {

    static CrawlerDetect newInstance() {
        Detector detector = new Detector();
        try {
            detector.setCrawlerPatterns(new Crawlers(new URL("https://raw.githubusercontent.com/JayBizzle/Crawler-Detect/master/raw/Crawlers.txt")));
            detector.setUaExclusions(new Exclusions(new URL("https://raw.githubusercontent.com/JayBizzle/Crawler-Detect/master/raw/Exclusions.txt")));
            detector.setHeadersToCheck(new Headers(new URL("https://raw.githubusercontent.com/JayBizzle/Crawler-Detect/master/raw/Headers.txt")));
        } catch (IOException e) {
            System.err.printf("Could not load configuration data for Crawler Detector");
        }
        return detector;
    }

    boolean isCrawler(Map<String,String> headers);
    boolean isCrawler(String userAgent);
}
