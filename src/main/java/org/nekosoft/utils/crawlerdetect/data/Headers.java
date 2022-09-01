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
     * This class needs to make some changes to the original data to account for differences in the way
     * that Java and PHP access HTTP header information.
     * <p>
     * The reloadData method strips every value of the HTTP_ prefix, replaces _ with -, and turns the string
     * to lower case. E.g.
     * <pre>
     *     "HTTP_USER_AGENT" becomes "user-agent"
     * </pre>
     */
    @Override
    public void reloadData() throws IOException {
        super.reloadData();
        data = data.stream().map(s ->
                (s.startsWith("HTTP_") ? s.substring(5) : s).toLowerCase().replace('_','-')
        ).toList();
    }

}
