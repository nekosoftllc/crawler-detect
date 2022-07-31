package org.nekosoft.utils.crawlerdetect.data;

import org.nekosoft.utils.crawlerdetect.AbstractDataProvider;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class Headers extends AbstractDataProvider {

    public Headers(URL source) throws IOException {
        super(source);
    }

    public Headers(List<String> data) {
        this.data = data;
    }

}
