package eu.tsystems.mms.tic.testframework.report.general;

import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractMethodStateEntry;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractMethodStatePage;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractTestReportNumbers;
import eu.tsystems.mms.tic.testframework.report.model.MethodStateAcknowledgementEntry;
import eu.tsystems.mms.tic.testframework.report.model.TestReportOneAcknowledgements;
import eu.tsystems.mms.tic.testframework.report.model.TestReportOneNumbers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by fakr on 19.09.2017
 */
public abstract class AbstractMethodStateTest extends AbstractTest {

    protected List<? extends AbstractMethodStateEntry> methodStateTestObjects;

    @BeforeMethod(alwaysRun = true)
    public abstract void initTestObjects();

    /**
     * Checks whether the amount of methods - displayed in the upper right corner - is as expected
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, enabled=false)
    public void testT01_checkNumberOfMethods() {
        AbstractMethodStatePage methodStatePage = openMethodStatePage(ReportDirectory.REPORT_DIRECTORY_1);
        final TestReportOneNumbers numbers = new TestReportOneNumbers();
        int expectedMethodNumber = getExpectedNumberOfMethods(numbers);
        methodStatePage.assertNumberOfMethodsByMethodsString(expectedMethodNumber);
        methodStatePage.assertNumberOfMethodsByCountingRows(expectedMethodNumber);
    }

    /**
     * Checks whether all expected states are displayed in the legend row
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, enabled=false)
    public void testT02_checkLegendItems() {
        AbstractMethodStatePage methodStatePage = openMethodStatePage(ReportDirectory.REPORT_DIRECTORY_1);
        methodStatePage.assertAllLegendItemsAreDisplayed();
    }

    /**
     * Checks whether the listed methods have got the expected markers
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, enabled=false)
    public void testT03_checkAnnotationMarkers() {
        List<MethodStateAcknowledgementEntry> ackEntries = TestReportOneAcknowledgements.getAllMethodStateAcknowledgementEntries();
        MethodStateAcknowledgementEntry newFailedMarkersEntry = ackEntries.get(0);
        MethodStateAcknowledgementEntry allMarkersEntry = ackEntries.get(1);
        MethodStateAcknowledgementEntry readyForApprovalMarkerEntry = ackEntries.get(2);
        MethodStateAcknowledgementEntry newSuccessMarkersEntry = ackEntries.get(3);
        AbstractMethodStatePage methodStatePage = openMethodStatePage(ReportDirectory.REPORT_DIRECTORY_1);
        methodStatePage.assertAnnotationMarkIsDisplayed(newFailedMarkersEntry.getAnnotationTypeList().get(0), newFailedMarkersEntry.getMethodName());
        methodStatePage.assertAllAnnotationMarksAreDisplayed(allMarkersEntry.getMethodName());
        methodStatePage.assertAnnotationMarkIsDisplayed(readyForApprovalMarkerEntry.getAnnotationTypeList().get(0), readyForApprovalMarkerEntry.getMethodName());
        methodStatePage.assertAnnotationMarkIsDisplayed(newSuccessMarkersEntry.getAnnotationTypeList().get(0), newSuccessMarkersEntry.getMethodName());
    }

    /**
     * This test assures that the "bubbles" that indicate the current and previous test state of a passed test without
     * history are displayed correctly
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, enabled=false)
    public void testT04_checkIndicationsForTestsWithoutHistory() {
        List<? extends AbstractMethodStateEntry> testObjects = getMethodStateTestObjectForReport(ReportDirectory.REPORT_DIRECTORY_1);
        AbstractMethodStatePage methodStatePage = openMethodStatePage(ReportDirectory.REPORT_DIRECTORY_1);
        for (AbstractMethodStateEntry entry : testObjects) {
            methodStatePage.assertTestIndications(entry);
        }
    }

    /**
     * This test assures that the link of a method name leads to its according method details page
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, enabled=false)
    public void testT05_checkMethodDetailsLink() {
        List<? extends AbstractMethodStateEntry> testObjects = getMethodStateTestObjectForReport(ReportDirectory.REPORT_DIRECTORY_1);
        AbstractMethodStatePage methodStatePage = openMethodStatePage(ReportDirectory.REPORT_DIRECTORY_1);
        for (AbstractMethodStateEntry entry : testObjects) {
            methodStatePage.assertMethodDetailsLink(entry);
            methodStatePage = openMethodStatePage(ReportDirectory.REPORT_DIRECTORY_1);
        }
    }

    /**
     * This test assures that the link of a class name leads to a test methods page
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, enabled=false)
    public void testT06_checkClassesDetailsLink() {
        List<? extends AbstractMethodStateEntry> testObjects = getMethodStateTestObjectForReport(ReportDirectory.REPORT_DIRECTORY_1);
        AbstractMethodStatePage methodStatePage = openMethodStatePage(ReportDirectory.REPORT_DIRECTORY_1);
        for (AbstractMethodStateEntry entry : testObjects) {
            methodStatePage.assertClassesDetailsLink(entry);
            methodStatePage = openMethodStatePage(ReportDirectory.REPORT_DIRECTORY_1);
        }
    }

    protected abstract List<? extends AbstractMethodStateEntry> getMethodStateTestObjectForReport(ReportDirectory reportDirectory);

    protected abstract AbstractMethodStatePage openMethodStatePage(ReportDirectory reportDirectory);

    protected abstract int getExpectedNumberOfMethods(AbstractTestReportNumbers expectedNumbers);

}
