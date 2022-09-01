package org.nekosoft.utils;

import org.nekosoft.utils.crawlerdetect.Detector;
import org.nekosoft.utils.crawlerdetect.data.Crawlers;
import org.nekosoft.utils.crawlerdetect.data.Exclusions;
import org.nekosoft.utils.crawlerdetect.data.Headers;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * CrawlerDetect is a Java port of <a href="https://crawlerdetect.io">crawlerdetect.io</a>, a PHP class for detecting
 * bots/crawlers/spiders via the User Agent and http_from header.
 * <p>
 * <b>Usage</b>
 * <p>
 * Add a dependency on this library to your Maven configuration
 * <pre>
 * &lt;dependency>
 *     &lt;groupId>org.nekosoft.utils&lt;/groupId>
 *     &lt;artifactId>crawler-detect&lt;/artifactId>
 *     &lt;version>1.0.0&lt;/version>
 * &lt;/dependency>
 * </pre>
 * Get a new instance of the detector with the {@link CrawlerDetect#newInstance()} method. The instance that is
 * returned loads all the necessary definitions and configuration from the original repository of the PHP CrawlerDetect
 * library (see {@link org.nekosoft.utils.crawlerdetect.AbstractDataProvider}).
 * <pre>
 * CrawlerDetect detector = CrawlerDetect.newInstance();
 * </pre>
 * The instance should be cached and reused within the same application. There is usually no need to create more than
 * one instance in the same JVM process.
 * <p>
 * In line with the original API, there are two ways to test for a spider/crawler/bot using this library.
 * <ul>
 * <li>
 * directly using the User Agent string that you want to check
 * <pre>
 * boolean isBot = detector.isCrawler(uaString);
 * </pre>
 * </li>
 * <li>
 * passing the map of all headers of the request you are checking and letting CrawlerDetect work out the user agent based on the headers provided
 * <pre>
 * boolean isBot = detector.isCrawler(headersMap);
 * </pre>
 * </li>
 * </ul>
 * If you prefer to also have the matching crawler string, you can use the alternative methods
 * <pre>
 * boolean isBot = detector.getMatchingCrawler(uaString);
 * boolean isBot = detector.getMatchingCrawler(headersMap);
 * </pre>
 */
public interface CrawlerDetect {

    /**
     * Returns a new instance of the default implementation of CrawlerDetect. This instance is fully configured to load
     * and reload data from source URLs within the original repository of the PHP CrawlerDetect project, as follows
     * <ul>
     *     <li>the list of regular expressions for positive matches (<a href="https://github.com/JayBizzle/Crawler-Detect/blob/master/raw/Crawlers.txt">here</a>)
     *      (i.e. matching headers that indicate the presence of bots, spiders and crawlers)</li>
     *     <li>the list of regular expressions for exclusions (<a href="https://github.com/JayBizzle/Crawler-Detect/blob/master/raw/Exclusions.txt">here</a>)
     *      (i.e., matching headers that definitely do not indicate the presence of a bot, spider or crawler)</li>
     *     <li>the list of relevant headers (<a href="https://github.com/JayBizzle/Crawler-Detect/blob/master/raw/Headers.txt">here</a>)
     *      when testing for bots, spiders and crawlers</li>
     * </ul>
     * @return the default instance of CrawlerDetect configured with the PHP CrawlerDetect project as the source of truth
     */
    static CrawlerDetect newInstance() {
        Detector detector = new Detector();
        try {
            detector.setCrawlerPatterns(new Crawlers(new URL("https://raw.githubusercontent.com/JayBizzle/Crawler-Detect/master/raw/Crawlers.txt")));
            detector.setUaExclusions(new Exclusions(new URL("https://raw.githubusercontent.com/JayBizzle/Crawler-Detect/master/raw/Exclusions.txt")));
            detector.setHeadersToCheck(new Headers(new URL("https://raw.githubusercontent.com/JayBizzle/Crawler-Detect/master/raw/Headers.txt")));
        } catch (IOException e) {
            System.err.print("Could not load configuration data for Crawler Detector");
        }
        return detector;
    }

    /**
     * Tests whether the given HTTP request headers indicate that the request might come from a crawler, bot or spider.
     * @param headers a map of the HTTP request headers to test. The application will choose the appropriate ones to be
     *                included in the test, so it is ok to pass all headers from the original HTTP request.
     * @return {@code true} if the headers indicate this request might have been generated by a crawler, bot or spider,
     * {@code false} otherwise.
     */
    boolean isCrawler(Map<String,String> headers);

    /**
     * Tests whether the given user agent string indicates that the request might come from a crawler, bot or spider.
     * @param userAgent the user agent string to test. This could come from the HTTP User Agent header, or from
     *                  any other source that the caller sees fit.
     * @return {@code true} if the user agent string indicates this request might have been generated by a crawler,
     * bot or spider, {@code false} otherwise.
     */
    boolean isCrawler(String userAgent);

    /**
     * Finds the crawler, bot or spider that the given HTTP request headers indicate the request might be coming from.
     * @param headers a map of the HTTP request headers to test against. The application will choose the appropriate
     *                ones to be included in the test, so it is ok to pass all headers from the original HTTP request.
     * @return a string indicating which crawler, bot or spider the request might have been generated by, or
     * {@code null} if this request does not seem to come from a bot/spider/crawler.
     */
    String getMatchingCrawler(Map<String,String> headers);

    /**
     * Finds the crawler, bot or spider that the given user agent string indicates the request might be coming from.
     * @param userAgent the user agent string to test. This could come from the HTTP User Agent header, or from
     *                  any other source that the caller sees fit.
     * @return a string indicating which crawler, bot or spider the request might have been generated by, or
     * {@code null} if this request does not seem to come from a bot/spider/crawler.
     */
    String getMatchingCrawler(String userAgent);
}
