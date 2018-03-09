package at.wrk.coceso.plugin.geobroker.utils;

import org.apache.commons.lang3.RandomStringUtils;

public final class Strings {
    public static String randomString() {
        return randomString("");
    }

    public static String randomString(final String prefix) {
        return prefix + RandomStringUtils.randomAlphabetic(10);
    }
}
