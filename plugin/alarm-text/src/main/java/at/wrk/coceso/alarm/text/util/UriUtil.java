package at.wrk.coceso.alarm.text.util;

import java.net.URI;
import java.util.Objects;

public final class UriUtil {
    private UriUtil() {
    }

    public static URI appendPath(final URI originalUri, final String pathToAppend) {
        Objects.requireNonNull(originalUri, "URI to modify must not be null.");
        Objects.requireNonNull(pathToAppend, "Path to append to URI must not be null.");

        String normalizedUriString = stripEndingSlash(originalUri);
        String normalizedPathToAppend = stripStartingSlash(pathToAppend);

        return URI.create(normalizedUriString + "/" + normalizedPathToAppend);
    }

    private static String stripStartingSlash(final String path) {
        return path.startsWith("/") ? path.substring(1) : path;
    }

    private static String stripEndingSlash(final URI uri) {
        String originalUriString = uri.toString();
        return originalUriString.endsWith("/")
                ? originalUriString.substring(0, originalUriString.length() - 1)
                : uri.toString();
    }
}
