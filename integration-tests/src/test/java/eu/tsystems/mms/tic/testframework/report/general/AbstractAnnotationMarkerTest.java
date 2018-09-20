package eu.tsystems.mms.tic.testframework.report.general;

import eu.tsystems.mms.tic.testframework.report.model.IReportAnnotationVerifier;
import eu.tsystems.mms.tic.testframework.report.model.ReportAnnotationType;

import java.util.List;
import java.util.Map;

/**
 * Created by fakr on 11.09.2017
 */
public abstract class AbstractAnnotationMarkerTest extends AbstractTest {

    protected void checkAnnotationsAreDisplayed(IReportAnnotationVerifier verifier, Map<String,List<ReportAnnotationType>> methodTestObjects) {
        for (String methodName: methodTestObjects.keySet()) {
            for (ReportAnnotationType annotationType:methodTestObjects.get(methodName)) {
                verifier.assertAnnotationMarkIsDisplayed(annotationType, methodName);
            }
        }
    }

    protected void checkRetryAnnotationIsDisplayed(IReportAnnotationVerifier verifier, String retryMethodName) {
        verifier.assertRetryMarkerIsDisplayed(retryMethodName);
    }

}
