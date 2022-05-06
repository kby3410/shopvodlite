package com.ayst.adplayer.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shenhaibo on 2018/5/6.
 */

public class IPAddressValidator {
    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    private static Matcher matcher;
    private static Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);

    public static boolean validate(final String ip) {
        matcher = pattern.matcher(ip);
        return matcher.matches();
    }
}
