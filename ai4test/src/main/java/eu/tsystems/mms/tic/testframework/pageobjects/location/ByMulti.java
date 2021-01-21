package eu.tsystems.mms.tic.testframework.pageobjects.location;

import eu.tsystems.mms.tic.testframework.layout.core.Point2D;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.*;

import java.util.List;

public class ByMulti extends By {

    private final JSONObject selector;

    public ByMulti(String encodedSelector) {
        this.selector = deserialize(encodedSelector);
    }

    public static String serialize(JSONObject json) {
        return json.toString();
    }

    public static JSONObject deserialize(String s) {
        return new JSONObject(s);
    }

    public Rectangle getBoundingBox(){
        JSONArray bb = selector.getJSONArray("bb");
        return new Rectangle(bb.getInt(0), bb.getInt(1), bb.getInt(3), bb.getInt(2));
    }

    public Point2D getStartPoint(){
        JSONArray bb = selector.getJSONArray("bb");
        return new Point2D(bb.getInt(0), bb.getInt(1));
    }

    public Point2D getEndPoint(){
        JSONArray bb = selector.getJSONArray("bb");
        return new Point2D(bb.getInt(0) + bb.getInt(2), bb.getInt(1) + bb.getInt(3));
    }

    @Override
    public List<WebElement> findElements(SearchContext searchContext) {
        return new ByXPath(selector.getString("xpath")).findElements(searchContext);
    }
}
