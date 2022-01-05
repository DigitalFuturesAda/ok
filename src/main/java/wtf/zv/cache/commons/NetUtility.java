package wtf.zv.cache.commons;

import org.apache.commons.io.FilenameUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class NetUtility {
    public static boolean assertUrlValid(String url){
        try {
            new URL(url).toURI();
        } catch (Exception e){
            return false;
        }

        return true;
    }

    public static String extractFileExtensionFromUrl(URL url){
        return FilenameUtils.getExtension(url.getPath());
    }

    public static String extractEntireBaseUrl(URL url){
        /* Taken from {@code java.net.URLStreamHandler#toExternalForm */
        String s;
        return url.getProtocol()
                + ':'
                + ((s = url.getAuthority()) != null && !s.isEmpty()
                ? "//" + s : "")
                + ((s = url.getPath()) != null ? s : "");
    }

    public static String extractEntireBaseUrl(URI uri){
        return extractEntireBaseUrl(unsafeConvertUriToUrl(uri));
    }

    public static URL unsafeConvertUriToUrl(URI uri){
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(String.format("Unexpected MalformedURLException: %s", e));
        }
    }
}
