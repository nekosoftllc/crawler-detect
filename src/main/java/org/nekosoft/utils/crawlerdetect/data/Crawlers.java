package org.nekosoft.utils.crawlerdetect.data;

import org.nekosoft.utils.crawlerdetect.AbstractDataProvider;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class Crawlers extends AbstractDataProvider {

    public Crawlers(URL source) throws IOException {
        super(source);
    }

    public Crawlers(List<String> data) {
        super(data);
    }

}
