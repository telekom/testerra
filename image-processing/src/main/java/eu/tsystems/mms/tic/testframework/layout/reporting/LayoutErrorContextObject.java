package eu.tsystems.mms.tic.testframework.layout.reporting;

import eu.tsystems.mms.tic.testframework.report.model.context.CustomErrorContextObject;

public class LayoutErrorContextObject implements CustomErrorContextObject {

    public String name;
    public String mode;
    public String msg;
    public String imageFileNameReference;
    public String imageFileNameActual;
    public String imageFileNameDistance;

    @Override
    public void renderHtml() {

    }


}
