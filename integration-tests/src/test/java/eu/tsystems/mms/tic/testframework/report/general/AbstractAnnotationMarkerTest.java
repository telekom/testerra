package eu.tsystems.mms.tic.testframework.report.general;

import eu.tsystems.mms.tic.testframework.AbstractReportTest;
import eu.tsystems.mms.tic.testframework.report.model.IReportAnnotationVerifier;
import eu.tsystems.mms.tic.testframework.report.model.ReportAnnotationType;

import java.util.List;
import java.util.Map;

public abstract class AbstractAnnotationMarkerTest extends AbstractReportTest {

    protected void checkAnnotationsAreDisplayed(IReportAnnotationVerifier verifier, Map<String,List<ReportAnnotationType>> methodTestObjects) {
        for (String methodName: methodTestObjects.keySet()) {
            for (ReportAnnotationType annotationType:methodTestObjects.get(methodName)) {
                verifier.assertAnnotationMarkIsDisplayed(annotationType, methodName);
            }
        }
    }
}
