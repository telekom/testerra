package eu.tsystems.mms.tic.testframework.report.model;

import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractMethodStateEntry;

import java.util.List;

/**
 * Created by fakr on 19.09.2017
 */
public class MethodStateAcknowledgementEntry extends AbstractMethodStateEntry {

    private List<ReportAnnotationType> annotationTypes;

    public List<ReportAnnotationType> getAnnotationTypeList() {
        return annotationTypes;
    }

    public void setAnnotationType(List<ReportAnnotationType> annotationType) {
        this.annotationTypes = annotationType;
    }

    public MethodStateAcknowledgementEntry(TestResultHelper.TestResultChangedMethodState previousState, TestResultHelper.TestResultChangedMethodState currentState, String methodName, String testUnderTestClass, List<ReportAnnotationType> annotationTypes) {
        super(previousState, currentState, methodName, testUnderTestClass);
        this.annotationTypes = annotationTypes;
    }

}
