package eu.tsystems.mms.tic.testframework.pageobjects.location;

import org.json.JSONObject;
import org.openqa.selenium.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class ByMulti extends By {

    private final static Charset ENCODING = StandardCharsets.ISO_8859_1;


    private final JSONObject selector;

    public ByMulti(String encodedSelector) {
        this.selector = deserialize(encodedSelector);
    }

    public static String serialize(JSONObject json) {
        byte[] compressed = compress(json.toString());

        return new String(compressed, ENCODING);
    }

    public static byte[] compress(String text) {
        byte[] bArray = text.getBytes();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            DeflaterOutputStream dos = new DeflaterOutputStream(os);
            dos.write(bArray);
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[]{};
        }
        return os.toByteArray();
    }

    public static JSONObject deserialize(String s) {
        byte[] decompressed = decompress(s.getBytes(ENCODING));

        return new JSONObject(new String(decompressed));
    }

    public static byte[] decompress(byte[] compressedTxt) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            OutputStream ios = new InflaterOutputStream(os);
            ios.write(compressedTxt);
            ios.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[]{};
        }
        return os.toByteArray();
    }

    @Override
    public List<WebElement> findElements(SearchContext searchContext) {
        return new ByXPath(selector.getString("xpath")).findElements(searchContext);
    }
}
