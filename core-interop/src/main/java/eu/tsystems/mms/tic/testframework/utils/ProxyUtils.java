package eu.tsystems.mms.tic.testframework.utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ProxyUtils {

    /**
     * @return null If there is no URL configured
     */
    public static URL getSystemHttpProxyUrl() {
        return getSystemProxyUrlWithPrefix("http");
    }

    /**
     * @return null If there is no URL configured
     */
    public static URL getSystemHttpsProxyUrl() {
        return getSystemProxyUrlWithPrefix("https");
    }

    /**
     * @return null If there is no URL configured
     */
    public static URL getSystemFtpProxyUrl() {
        return getSystemProxyUrlWithPrefix("ftp");
    }

    private static URL getSystemProxyUrlWithPrefix(String prefix) {


        final String urlEncoding = "UTF-8";
        String proxyUrlString = prefix + "://";
        try {
            String user = System.getProperty(prefix + ".proxyUser");
            if (user != null) {
                proxyUrlString += URLEncoder.encode(user, urlEncoding);
            }
            String password = System.getProperty(prefix + ".proxyPassword");
            if (password != null) {
                proxyUrlString += ":" + URLEncoder.encode(password, urlEncoding);
            }

            if (user != null) {
                proxyUrlString += "@";
            }

            final String proxyHost = System.getProperty(prefix + ".proxyHost");
            if (proxyHost != null) {
                proxyUrlString += proxyHost;
            } else {
                return null;
            }

            final String proxyPort = System.getProperty(prefix + ".proxyPort");
            if (proxyPort != null) {
                proxyUrlString += ":" + proxyPort;
            }

            return new URL(proxyUrlString);
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
