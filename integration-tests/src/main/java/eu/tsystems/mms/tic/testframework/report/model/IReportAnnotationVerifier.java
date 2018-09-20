package eu.tsystems.mms.tic.testframework.report.model;

/**
 * Created by fakr on 30.08.2017
 */
public interface IReportAnnotationVerifier {


    String LOCATOR_FONT_ANNOTATION = ".//font[contains(text(),'%s')]";
    String RETRIED_NAME = "Retried";

    /**
     * Asserts that an expected annotation mark is displayed for a given method
     *
     * @param annotationType
     * @param methodName
     */
    void assertAnnotationMarkIsDisplayed(ReportAnnotationType annotationType, String methodName);

    /**
     * Asserts that all annotation marks are displayed for a given method
     *
     * @param methodName
     */
    void assertAllAnnotationMarksAreDisplayed(String methodName);

    /**
     * Asserts that the 'Retried' mark is displayed for a given method
     *
     * @param methodName
     */
    void assertRetryMarkerIsDisplayed(String methodName);
}
