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
loads all the necessary definitions and configuration from the original repository of the
[PHP CrawlerDetect library](https://github.com/JayBizzle/Crawler-Detect).

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

## Internals

This library uses the original PHP project as the source of truth for the following

1. the list of 
[regular expressions for positive matches](https://github.com/JayBizzle/Crawler-Detect/blob/master/raw/Crawlers.txt)
(i.e. matching headers that indicate the presence of bots, spiders and crawlers)
2. the list of 
[regular expressions for exclusions](https://github.com/JayBizzle/Crawler-Detect/blob/master/raw/Exclusions.txt)
(i.e., matching headers that definitely do not indicate the presence of a bot, spider or crawler)
3. the list of 
[relevant headers](https://github.com/JayBizzle/Crawler-Detect/blob/master/raw/Headers.txt) 
when testing for bots, spiders and crawlers
4. the test data, consisting of
   - [user agents that should match](https://github.com/JayBizzle/Crawler-Detect/blob/master/tests/crawlers.txt) 
   as a bot, spider or crawler
   - [user agents that should not match](https://github.com/JayBizzle/Crawler-Detect/blob/master/tests/devices.txt)
   as a bot, spider or crawler

When creating a new instance of `CrawlerDetect` with `CrawlerDetect::newInstance`, the default implementation will load
the data from the URLs in the above links and store the data in local files in a temporary folder. The files will be 
reused in subsequent runs of the application, unless they are older than 31 days: in that case the data is reloaded 
again from the original URLs.

The location of the cache files can be changed with the following system property

```
-Dcrawlerdetect.cfg.baseDir=<stringPath>
```

The frequency for refreshing the cache files is expressed in number of days and set with the following system property

```
-Dcrawlerdetect.cfg.refreshDays=<intNumberOfDays>
```

If the value of `crawlerdetect.cfg.refreshDays` is <= 0, the data is never refreshed, and the cached files will be used
indefinitely until manually deleted.

This whole behaviour is implemented in the `AbstractDataProvider` class, which is the parent of

- `Crawlers`
- `Exclusions`
- `Headers`

as well as the following inner classes in the `test` code

- `CrawlerDetectTests$TestCrawlerList`
- `CrawlerDetectTests$TestDeviceList`

NB: The subclasses do not really add anything to the logic of the superclass and exist merely to reflect the API of the
original PHP library. The only one that has some additional behaviour is `Headers`, which removes the `HTTP_` prefix 
from all lines, as it is not necessary as part of an HTTP request header name in Java code.

## Customization

The library is meant to be simple to use and its standard usage, described above, is meant to reproduce the behaviour of
the original PHP library.

However, the classes can be used with custom or proprietary data.

### `AbstractDataProvider`

This class can be manually instantiated with any URL or even directly with a list of `String`s. In the latter case, the
data reloading mechanism will be completely disabled.

```java
public AbstractDataProvider(List<String> data) { /* ... */ }

public AbstractDataProvider(URL source) throws IOException { /* ... */ }
```

The three standard subclasses `Crawlers`, `Exclusions`, and `Headers` all provide the same constructor overloads.

### `Detector`

The `Detector` class is the default implementation of the `CrawlerDetect` interface. The `CrawlerDetect::newInstance`
method creates a new instance of it and initializes it with the URLs of the original data from the PHP library, with the 
links given above.

Custom instances can pass specialized data providers that define their own data sources or even just directly take the 
list of `String`s to be used in the detection process.

```java
Detector detector = new Detector();
detector.setCrawlerPatterns(yourOwnAbstractDataProviderForCrawlers);
detector.setUaExclusions(yourOwnAbstractDataProviderForExclusions);
detector.setHeadersToCheck(yourOwnAbstractDataProviderForHeaders);
```

## TODO

- implement the `getMatch` functionality available in the PHP code base
- add JavaDoc comments where applicable
