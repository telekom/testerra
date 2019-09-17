package eu.tsystems.mms.tic.testframework.layout.reporting;

import eu.tsystems.mms.tic.testframework.report.model.context.CustomErrorContextObject;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;

public class LayoutErrorContext implements CustomErrorContextObject {

    public String name;
    public String mode;
    public String msg;
    public Screenshot expectedScreenshot;
    public Screenshot actualScreenshot;
    public Screenshot distanceScreenshot;
    public Screenshot annotatedScreenshot;

    @Override
    public void renderHtml() {

    }


}
