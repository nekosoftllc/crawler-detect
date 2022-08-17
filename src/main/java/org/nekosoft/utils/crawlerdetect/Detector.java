package org.nekosoft.utils.crawlerdetect;

import org.nekosoft.utils.CrawlerDetect;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

// https://github.com/JayBizzle/Crawler-Detect

public class Detector implements CrawlerDetect {

    private AbstractDataProvider crawlerPatterns;
    private List<Pattern> crawlerPatternRE;
    private AbstractDataProvider headersToCheck;
    private AbstractDataProvider uaExclusions;
    private List<Pattern> uaExclusionsRE;

    public Detector() {
    }

    public AbstractDataProvider getCrawlerPatterns() {
        return crawlerPatterns;
    }

    public void setCrawlerPatterns(AbstractDataProvider crawlerPatterns) {
        this.crawlerPatterns = crawlerPatterns;
        this.crawlerPatternRE = crawlerPatterns.getAllValues().stream()
                .map(s -> Pattern.compile(s, CASE_INSENSITIVE))
                .toList();
    }

    public AbstractDataProvider getHeadersToCheck() {
        return headersToCheck;
    }

    public void setHeadersToCheck(AbstractDataProvider headersToCheck) {
        this.headersToCheck = headersToCheck;
    }

    public AbstractDataProvider getUaExclusions() {
        return uaExclusions;
    }

    public void setUaExclusions(AbstractDataProvider uaExclusions) {
        this.uaExclusions = uaExclusions;
        this.uaExclusionsRE = uaExclusions.getAllValues().stream()
                .map(s -> Pattern.compile(s, CASE_INSENSITIVE))
                .toList();
    }

    @Override
    public boolean isCrawler(Map<String,String> headers) {
        if (headersToCheck == null) {
            throw new IllegalStateException("Cannot check for bots in headers without crawler header data");
        }
        StringBuilder uaString = new StringBuilder();
        for (String altHeader : headersToCheck.getAllValues()) {
            String header = headers.get(altHeader);
            if (header != null) {
                uaString.append(" ").append(header);
            }
        }
        return isCrawler(uaString.toString());
    }

    @Override
    public boolean isCrawler(String userAgent) {
        if (uaExclusionsRE == null || crawlerPatternRE == null) {
            throw new IllegalStateException("Cannot check for bots without crawler detection data");
        }
        for (Pattern pattern : uaExclusionsRE) {
            userAgent = pattern.matcher(userAgent).replaceAll("");
        }
        String finalUA = userAgent;
        return crawlerPatternRE.stream().anyMatch(
                pattern -> pattern.matcher(finalUA).find()
        );
    }

}
