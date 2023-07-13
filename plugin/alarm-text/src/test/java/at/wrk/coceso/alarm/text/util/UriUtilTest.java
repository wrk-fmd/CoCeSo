package at.wrk.coceso.alarm.text.util;

import org.junit.Test;

import java.net.URI;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class UriUtilTest {
    @Test
    public void appendPath_emptyPath_returnSameUri() {
        URI originalUri = URI.create("http://localhost:8090/testing");

        URI uri = UriUtil.appendPath(originalUri, "");

        assertThat(uri, equalTo(URI.create("http://localhost:8090/testing/")));
    }

    @Test
    public void appendPath_path_pathIsAdded() {
        String originalUriString = "http://localhost:8090/testing";
        URI originalUri = URI.create(originalUriString);

        String addedPath = "/addedPath";
        URI uri = UriUtil.appendPath(originalUri, addedPath);

        assertThat(uri, equalTo(URI.create("http://localhost:8090/testing/addedPath")));
    }

    @Test
    public void appendPath_pathConfiguredWithSlash_pathIsAdded() {
        String originalUriString = "http://localhost:8090/testing/";
        URI originalUri = URI.create(originalUriString);

        String addedPath = "/addedPath";
        URI uri = UriUtil.appendPath(originalUri, addedPath);

        assertThat(uri, equalTo(URI.create("http://localhost:8090/testing/addedPath")));
    }
}
