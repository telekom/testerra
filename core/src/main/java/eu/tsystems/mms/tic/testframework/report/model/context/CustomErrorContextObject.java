package eu.tsystems.mms.tic.testframework.report.model.context;

/**
 * Implement that interface and define some fields, like
 *     String name;
 *     String imageFileNameReference;
 *     String imageFileNameActual;
 *     String imageFileNameDistance;
 *  and implement a render algorithm for local report.
 */
public interface CustomErrorContextObject {

    void renderHtml();

}
