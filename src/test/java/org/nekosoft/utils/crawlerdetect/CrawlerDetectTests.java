package org.nekosoft.utils.crawlerdetect;

import org.junit.jupiter.api.Test;
import org.nekosoft.utils.CrawlerDetect;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Test
    public void testGetMatchingCrawler() {
        Detector myDetector = new Detector();
        myDetector.setCrawlerPatterns(new AbstractDataProvider(List.of("(google|amazon|microsoft)[b-w]{3}(light|dark)")));
        String ua = myDetector.getMatchingCrawler("Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 5 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko; googleweblight) Chrome/38.0.1025.166 Mobile Safari/535.19");
        assertEquals("googleweblight", ua);
    }

    @Test
    public void testNoMatchingCrawler() {
        Detector myDetector = new Detector();
        myDetector.setCrawlerPatterns(new AbstractDataProvider(List.of("(google|amazon|microsoft)[b-w]{3}(light|dark)")));
        String ua = myDetector.getMatchingCrawler("Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 5 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko; nokiamobiledark) Chrome/38.0.1025.166 Mobile Safari/535.19");
        assertNull(ua);
    }

    @Test
    public void testHeadersMethodsBot() {
        Map<String,String> testHeaders = new HashMap<>();
        testHeaders.put("user-agent", "Zermelo");
        boolean res = detector.isCrawler(testHeaders);
        assertTrue(res);
    }

    @Test
    public void testHeadersMethodsNonBot() {
        Map<String,String> testHeaders = new HashMap<>();
        testHeaders.put("user-agent", "Safari");
        boolean res = detector.isCrawler(testHeaders);
        assertFalse(res);
    }

    @Test
    public void testHeadersMethodsBotGetMatching() {
        Map<String,String> testHeaders = new HashMap<>();
        testHeaders.put("user-agent", "Zermelo");
        String res = detector.getMatchingCrawler(testHeaders);
        assertEquals("Zermelo", res);
    }

    @Test
    public void testHeadersMethodsNonBotGetMatching() {
        Map<String,String> testHeaders = new HashMap<>();
        testHeaders.put("user-agent", "Safari");
        String res = detector.getMatchingCrawler(testHeaders);
        assertNull(res);
    }

    @Test
    public void testExceptionIfNoPatterns() {
        Detector myDetector = new Detector();
        assertThrows(IllegalStateException.class,
                () -> myDetector.getMatchingCrawler("Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 5 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko; googleweblight) Chrome/38.0.1025.166 Mobile Safari/535.19")
        );
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
