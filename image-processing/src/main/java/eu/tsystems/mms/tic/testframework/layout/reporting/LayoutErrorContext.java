package eu.tsystems.mms.tic.testframework.layout.reporting;

import eu.tsystems.mms.tic.testframework.report.model.context.CustomErrorContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;

public class LayoutErrorContext implements CustomErrorContext {

    public String name;
    public String mode;
    public double distance;
    public Screenshot expectedScreenshot;
    public Screenshot actualScreenshot;
    public Screenshot distanceScreenshot;
    public Screenshot annotatedScreenshot;

}
