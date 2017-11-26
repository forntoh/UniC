package utils;

import java.util.regex.Pattern;

public class Validate {
    private static final Pattern PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");//to validate ip address

    public static boolean validateIP(final String ip) {
        return PATTERN.matcher(ip).matches();
    }

    public static boolean validatePort(String portNumber) {
        if ((portNumber != null) && (portNumber.length() == 4) && (portNumber.matches(".*\\d.*"))) {
            return (Integer.parseInt(portNumber) > 1023);
        } else
            return false;
    }
}
