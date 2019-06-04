package eu.tsystems.mms.tic.testframework.layout.reporting;

import eu.tsystems.mms.tic.testframework.layout.LayoutCheck;
import eu.tsystems.mms.tic.testframework.layout.matching.error.LayoutFeature;
import eu.tsystems.mms.tic.testframework.report.model.Serial;

import java.io.Serializable;
import java.util.List;

/**
 * Holds the information of a screen reference.
 *
 * @author mibu
 */
@Deprecated
public class ScreenReferenceReportItem implements Serializable {

    private static final long serialVersionUID = Serial.SERIAL;

    /**
     * Name.
     */
    private String name;

    /**
     * Calculated distance.
     */
    private double distance;

    /**
     * Comparison mode of the Screenshot Referencer.
     */
    private LayoutCheck.Mode mode;

    /**
     * Path to reference screenshot.
     */
    private String referenceScreenshotPath;

    /**
     * Path to annotated screenshot.
     */
    private String annotatedScreenshotPath;

    /**
     * Path to actual screenshot.
     */
    private String actualScreenshotPath;

    /**
     * Path to distance screenshot.
     */
    private String distanceScreenshotPath;

    /**
     * List of Layout Error produced when running in annotated mode.
     */
    private List<LayoutFeature> annotatedModeCriticalMatches;

    /**
     * Public constructor.
     *
     * @param name Name
     * @param distance Calculated distance
     * @param referenceScreenshotPath Path to reference screenshot
     * @param actualScreenshotPath Path to actual screenshot
     * @param distanceScreenshotPath Path to distance screenshot
     */
    public ScreenReferenceReportItem(String name, LayoutCheck.Mode mode, double distance, String referenceScreenshotPath,
                                     String actualScreenshotPath, String distanceScreenshotPath) {
        super();
        this.name = name;
        this.mode = mode;
        this.distance = distance;
        this.referenceScreenshotPath = referenceScreenshotPath;
        this.actualScreenshotPath = actualScreenshotPath;
        this.distanceScreenshotPath = distanceScreenshotPath;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the distance
     */
    public String getDistance() {
        return String.format("%.2f", distance);
    }

    /**
     * @return the referenceScreenshotPath
     */
    public String getReferenceScreenshotPath() {
        return referenceScreenshotPath;
    }

    /**
     * @return the annotatedScreenshotPath
     */
    public String getAnnotatedScreenshotPath() {
        return annotatedScreenshotPath;
    }

    /**
     * @return the actualScreenshotPath
     */
    public String getActualScreenshotPath() {
        return actualScreenshotPath;
    }

    /**
     * @return the distanceScreenshotPath
     */
    public String getDistanceScreenshotPath() {
        return distanceScreenshotPath;
    }

    public boolean isAnnotatedMode() {
        return mode == LayoutCheck.Mode.ANNOTATED;
    }

    public List<LayoutFeature> getAnnotatedModeCriticalMatches() {
        return annotatedModeCriticalMatches;
    }

    public void setAnnotatedModeCriticalMatches(List<LayoutFeature> annotatedModeCriticalMatches) {
        this.annotatedModeCriticalMatches = annotatedModeCriticalMatches;
    }

    /**
     * Getter
     * @param annotatedScreenshotPath the path to the annotated Screenshot
     */
    public void setAnnotatedScreenshotPath(String annotatedScreenshotPath) {
        this.annotatedScreenshotPath = annotatedScreenshotPath;
    }
}
