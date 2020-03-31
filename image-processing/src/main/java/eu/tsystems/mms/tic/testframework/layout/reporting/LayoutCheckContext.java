package eu.tsystems.mms.tic.testframework.layout.reporting;

import eu.tsystems.mms.tic.testframework.report.model.context.CustomContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;

public class LayoutCheckContext implements CustomContext {

    public String image;
    public String mode;
    public double distance;
    public Screenshot expectedScreenshot;
    public Screenshot actualScreenshot;
    public Screenshot distanceScreenshot;
    public Screenshot annotatedScreenshot;

}
