package org.nekosoft.crawlerdetect;

import org.junit.jupiter.api.Test;
import org.nekosoft.utils.CrawlerDetect;
import org.nekosoft.utils.crawlerdetect.AbstractDataProvider;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CrawlerDetectTests {

    CrawlerDetect detector = CrawlerDetect.newInstance();

    // Create static class so cache files have reliable names
    static class TestCrawlerList extends AbstractDataProvider {
        TestCrawlerList(URL src) throws IOException { super(src); }
    }

    // Create static class so cache files have reliable names
    static class TestDeviceList extends AbstractDataProvider {
        TestDeviceList(URL src) throws IOException { super(src); }
    }

    AbstractDataProvider crawlerUAs = new TestCrawlerList(
            new URL("https://raw.githubusercontent.com/JayBizzle/Crawler-Detect/master/tests/crawlers.txt")
    );

    AbstractDataProvider deviceUAs = new TestDeviceList(
            new URL("https://raw.githubusercontent.com/JayBizzle/Crawler-Detect/master/tests/devices.txt")
    );

    public CrawlerDetectTests() throws IOException { }

    @Test
    public void testAgainstBots() {
        testWithFileForResult(crawlerUAs.getAllValues(), true);
    }

    @Test
    public void testAgainstNonBots() {
        testWithFileForResult(deviceUAs.getAllValues(), false);
    }

    private void testWithFileForResult(List<String> testData, boolean isCrawler) {
        boolean matches = testData.stream().anyMatch( line -> {
            System.out.printf("Detecting bot in UA %s%n", line);
            boolean isBot = detector.isCrawler(line);
            return isBot != isCrawler;
        });
        assertFalse(matches);
    }

}
