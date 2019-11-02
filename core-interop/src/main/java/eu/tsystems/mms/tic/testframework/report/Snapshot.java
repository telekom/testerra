package eu.tsystems.mms.tic.testframework.report;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

/**
 * A Webdriver Snapshot
 */
public class Snapshot implements Loggable {
    private static HashMap<String, Integer> counter = new HashMap<>();
    public final File screenshotFile;
    public final File pageSourceFile;
    private Screenshot screenshot;
    private Date date;

    public Snapshot(String name) {
        date = new Date();
        int count = counter.get(name);
        String fileName = String.format("%s-%03d-", name, counter.get(name));
        File screenshotFile = null;
        File pageSourceFile = null;
        try {
            screenshotFile = File.createTempFile(fileName, "png");
            pageSourceFile = File.createTempFile(fileName, "html");
        } catch (IOException e) {
            log().error(e.getMessage());
        }
        this.screenshotFile = screenshotFile;
        this.pageSourceFile = pageSourceFile;
        counter.put(name, ++count);
    }

    public Snapshot setScreenshot(Screenshot screenshot) {
        this.screenshot = screenshot;
        screenshot.meta().put(Screenshot.Meta.DATE.toString(), date.toString());
        return this;
    }

    public Screenshot getScreenshot() {
        return this.screenshot;
    }
}
