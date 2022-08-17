# CrawlerDetect

A Java port of [crawlerdetect.io](https://github.com/JayBizzle/Crawler-Detect), a PHP class for detecting bots/crawlers/spiders via the User Agent and `http_from` header

## Usage

Add a dependency on this library to your Maven configuration

```xml
<dependency>
    <groupId>org.nekosoft.utils</groupId>
    <artifactId>crawler-detect</artifactId>
    <version>0.9.0</version>
</dependency>
```

Get a new instance of the detector with the `CrawlerDetect::newInstance` method. The instance that is returned
loads all the necessary definitions and configuration from the original repository of the PHP CrawlerDetect library.

```java
CrawlerDetect detector = CrawlerDetect.newInstance();
```

The instance should be cached and reused within the same application. There is usually no need to create more than 
one instance in the same JVM process.

In line with the original API, there are two ways to test for a spider/crawler/bot using this library.

- directly using the User Agent string that you want to check

```java
boolean isBot = detector.isCrawler(uaString);
```

- passing the map of all headers of the request you are checking and letting CrawlerDetect work out the user agent based
on the headers provided

```java
boolean isBot = detector.isCrawler(headersMap);
```
