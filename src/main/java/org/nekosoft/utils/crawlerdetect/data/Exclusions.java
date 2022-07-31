package org.nekosoft.utils.crawlerdetect.data;

import org.nekosoft.utils.crawlerdetect.AbstractDataProvider;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class Exclusions extends AbstractDataProvider {

    public Exclusions(URL source) throws IOException {
        super(source);
    }

    public Exclusions(List<String> data) {
        this.data = data;
    }

}
