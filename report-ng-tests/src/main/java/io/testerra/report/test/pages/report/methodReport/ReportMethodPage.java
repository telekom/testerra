package io.testerra.report.test.pages.report.methodReport;

import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.ReportMethodPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportThreadsPage;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class ReportMethodPage extends AbstractReportPage {

    private AbstractReportMethodPage currentPage;

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public ReportMethodPage(WebDriver driver) {
        super(driver);
        currentPage = getActivePage();
    }

    public AbstractReportMethodPage getActivePage() {
        // TODO: better solution than exception catching to distinguish Steps and Details Tab
        try {
            return PageFactory.create(ReportDetailsTab.class, getWebDriver());
        } catch (Exception ignored) {

        }
        try {
            return PageFactory.create(ReportStepsTab.class, getWebDriver());
        } catch (Exception ignored) {

        }
        throw new RuntimeException("Details- or Step-Tab should be selected!");
    }


    public <T extends AbstractReportMethodPage> void navigateBetweenTabs(ReportMethodPageType reportMethodPageType, Class<T> reportsTabClass) {
        switch (reportMethodPageType) {
            case DETAILS:
                currentPage.getTestDetailsTab().click();
                break;
            case STEPS:
                currentPage.getTestStepsTab().click();
                break;
            case SESSIONS:
                currentPage.getTestSessionsTab().click();
                break;
            case DEPENDENCIES:
                currentPage.getTestDependenciesTab().click();
                break;
        }
        currentPage = PageFactory.create(reportsTabClass, getWebDriver());
    }

    public void assertMethodOverviewContainsCorrectContent(String methodClass, String status, String name) {
        currentPage.assertMethodOverviewContainsCorrectContent(methodClass, status, name);
    }

    public ReportThreadsPage clickThreadLink() {
        return currentPage.clickThreadLink();
    }

    public String getTestDuration() {
        return currentPage.getTestDuration();
    }

    public ReportMethodPageType getCurrentPageType() {
        return currentPage.getCurrentPageType();
    }

    public void detailPageAsserts_FailureAspectsCorrespondsToCorrectStatus(String expectedStatusTitle) {
        if (getCurrentPageType() != ReportMethodPageType.DETAILS) {
            Assert.fail("Details tab should be selected");
        }
        ((ReportDetailsTab) currentPage).detailPageAssertsFailureAspectsCorrespondsToCorrectStatus(expectedStatusTitle);
    }

    public void detailsPageAssertsTestMethodContainsCorrectFailureAspect(String... correctFailureAspects) {
        if (getCurrentPageType() != ReportMethodPageType.DETAILS) {
            Assert.fail("Details tab should be selected");
        }
        ((ReportDetailsTab) currentPage).detailsPageAssertsTestMethodContainsCorrectFailureAspect(correctFailureAspects);
    }

    public void detailsPageAssertSkippedTestContainsCorrespondingFailureAspect() {
        if (getCurrentPageType() != ReportMethodPageType.DETAILS) {
            Assert.fail("Details tab should be selected");
        }
        ((ReportDetailsTab) currentPage).detailsPageAssertSkippedTestContainsCorrespondingFailureAspect();
    }

    public void stepsPageAssertsTestStepsContainFailureAspectMessage(String failureAspectMessage) {
        if (getCurrentPageType() != ReportMethodPageType.STEPS) {
            Assert.fail("Steps tab should be selected");
        }
        ((ReportStepsTab) currentPage).assertsTestStepsContainFailureAspectMessage(failureAspectMessage);
    }

    public void assertPageIsValid(ReportMethodPageType reportMethodPageType) {
        if (getCurrentPageType() != reportMethodPageType) Assert.fail(reportMethodPageType + " tab should be selected!");
        currentPage.assertPageIsValid();
    }

    public void assertTestMethodeReportContainsFailsAnnotation() {
        currentPage.assertTestMethodeReportContainsFailsAnnotation();
    }

    public String getFailureAspectFromDetailsPage(){
        if (getCurrentPageType() != ReportMethodPageType.DETAILS) Assert.fail("Details tab should be selected!");
        return ((ReportDetailsTab) currentPage).getFailureAspect();
    }

    public void stepsPageAssertsEachFailureAspectContainsExpectedStatement(String expectedStatement) {
        if (getCurrentPageType() != ReportMethodPageType.STEPS) {
            Assert.fail("Steps tab should be selected");
        }
        ((ReportStepsTab) currentPage).assertEachFailureAspectContainsExpectedStatement(expectedStatement);
    }

}
