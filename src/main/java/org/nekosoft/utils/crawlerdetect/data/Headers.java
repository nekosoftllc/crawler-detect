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
        super(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reloadData() throws IOException {
        super.reloadData();
        data = data.stream().map(s -> s.startsWith("HTTP_") ? s.substring(5) : s).toList();
    }

}
