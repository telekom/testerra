package eu.tsystems.mms.tic.testframework.utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ProxyUtils {

    public static URL getSystemHttpProxyUrl() {
        return getSystemProxyUrlWithPrefix("http");
    }

    public static URL getSystemHttpsProxyUrl() {
        return getSystemProxyUrlWithPrefix("https");
    }

    public static URL getSystemFtpProxyUrl() {
        return getSystemProxyUrlWithPrefix("ftp");
    }

    private static URL getSystemProxyUrlWithPrefix(String prefix) {
        final String urlEncoding = "UTF-8";
        String proxyUrlString = prefix + "://";
        try {
            String user = System.getProperty(prefix + ".proxyUser");
            if (user.length() > 0) {
                proxyUrlString += URLEncoder.encode(user, urlEncoding);
            }
            String password = System.getProperty(prefix + ".proxyPassword");
            if (password.length() > 0) {
                proxyUrlString += ":" + URLEncoder.encode(password, urlEncoding);
            }

            if (user.length() > 0) {
                proxyUrlString += "@";
            }
            proxyUrlString += System.getProperty(prefix + ".proxyHost") + ":" + System.getProperty(prefix + ".proxyPort");
            return new URL(proxyUrlString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
