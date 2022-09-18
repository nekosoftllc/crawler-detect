package org.nekosoft.utils.crawlerdetect;

import org.nekosoft.utils.CrawlerDetect;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

// https://github.com/JayBizzle/Crawler-Detect

/**
 * The default implementation of the {@link CrawlerDetect} interface. This class is instantiated automatically
 * by the {@link CrawlerDetect#newInstance()} method, which also configures it to load all data from the PHP CrawlerDetect
 * GitHub repository, by setting the {@link #setCrawlerPatterns(AbstractDataProvider)}, {@link #setUaExclusions(AbstractDataProvider)}
 * and {@link #setHeadersToCheck(AbstractDataProvider)} properties.
 * <p>
 * Alternatively, custom instances can be defined that pass specialized data providers that point to their own data
 * sources or even just directly take the list of {@code String}s to be used in the detection process.
 * <pre>
 * Detector detector = new Detector();
 * detector.setCrawlerPatterns(yourOwnAbstractDataProviderForCrawlers);
 * detector.setUaExclusions(yourOwnAbstractDataProviderForExclusions);
 * detector.setHeadersToCheck(yourOwnAbstractDataProviderForHeaders);
 * </pre>
 */
public class Detector implements CrawlerDetect {

    private AbstractDataProvider crawlerPatterns;
    private List<Pattern> crawlerPatternRE;
    private AbstractDataProvider headersToCheck;
    private AbstractDataProvider uaExclusions;
    private List<Pattern> uaExclusionsRE;

    /**
     * Creates an instance of the Detector. In the recommended use of this class, only one instance is needed per JVM,
     * and it is created with {@link CrawlerDetect#newInstance()}.
     */
    public Detector() {
    }

    /**
     * The Crawler Patterns are regular expression patterns that are used to check whether a certain user agent string
     * could potentially be a crawler, spider or bot.
     * @return the {@link AbstractDataProvider} that contains the crawler patterns for this instance
     */
    public AbstractDataProvider getCrawlerPatterns() {
        return crawlerPatterns;
    }

    /**
     * Sets the crawler patterns for this instance.
     * <p>
     * See {@link #getCrawlerPatterns()}
     * @param crawlerPatterns the {@link AbstractDataProvider} that contains the crawler patterns to be set for this instance
     */
    public void setCrawlerPatterns(AbstractDataProvider crawlerPatterns) {
        this.crawlerPatterns = crawlerPatterns;
        this.crawlerPatternRE = crawlerPatterns.getAllValues().stream()
                .map(s -> Pattern.compile(s, CASE_INSENSITIVE))
                .toList();
    }

    /**
     * The list of Headers To Check is a list of HTTP headers that should be checked in order to find user agent strings
     * and test to determine whether they potentially represent a crawler, spider or bot.
     * @return the {@link AbstractDataProvider} that contains the list of headers to check for this instance
     */
    public AbstractDataProvider getHeadersToCheck() {
        return headersToCheck;
    }

    /**
     * Sets the list of headers to check for this instance.
     * <p>
     * See {@link #getHeadersToCheck()}
     * @param headersToCheck the {@link AbstractDataProvider} that contains the list of headers to check to be set for this instance
     */
    public void setHeadersToCheck(AbstractDataProvider headersToCheck) {
        this.headersToCheck = headersToCheck;
    }

    /**
     * The User Agent Exclusions are regular expression patterns that are used to remove parts of a user agent string
     * that definitely does not represent a crawler, spider or bot, and therefore does not need to be tested.
     * @return the {@link AbstractDataProvider} that contains the list of user agent exclusions for this instance
     */
    public AbstractDataProvider getUaExclusions() {
        return uaExclusions;
    }

    /**
     * Sets the user agent exclusions to be applied by this instance.
     * <p>
     * See {@link #getUaExclusions()}
     * @param uaExclusions the {@link AbstractDataProvider} that contains the user agent exclusions to be applied by this instance
     */
    public void setUaExclusions(AbstractDataProvider uaExclusions) {
        this.uaExclusions = uaExclusions;
        this.uaExclusionsRE = uaExclusions.getAllValues().stream()
                .map(s -> Pattern.compile(s, CASE_INSENSITIVE))
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCrawler(Map<String,String> headers) {
        String uaString = headersToUAString(headers);
        return isCrawler(uaString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCrawler(String userAgent) {
        String finalUA = prepareUAString(userAgent);
        return crawlerPatternRE.stream().anyMatch(
                pattern -> pattern.matcher(finalUA).find()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMatchingCrawler(Map<String,String> headers) {
        String uaString = headersToUAString(headers);
        return getMatchingCrawler(uaString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMatchingCrawler(String userAgent) {
        String finalUA = prepareUAString(userAgent);
        for (Pattern pattern : crawlerPatternRE) {
            Matcher m = pattern.matcher(finalUA);
            if (m.find()) {
                return m.group();
            }
        }
        return null;
    }

    private String headersToUAString(Map<String,String> headers) {
        if (headersToCheck == null) {
            throw new IllegalStateException("Cannot check for bots in headers without crawler header data");
        }
        StringBuilder uaBuilder = new StringBuilder();
        for (String altHeader : headersToCheck.getAllValues()) {
            String header = headers.get(altHeader);
            if (header != null) {
                uaBuilder.append(" ").append(header);
            }
        }
        return uaBuilder.toString();
    }

    private String prepareUAString(String uaString) {
        if (crawlerPatternRE == null) {
            throw new IllegalStateException("Cannot check for bots without crawler detection data");
        }
        if (uaExclusionsRE != null) for (Pattern pattern : uaExclusionsRE) {
            uaString = pattern.matcher(uaString).replaceAll("");
        }
        return uaString;
    }

}
