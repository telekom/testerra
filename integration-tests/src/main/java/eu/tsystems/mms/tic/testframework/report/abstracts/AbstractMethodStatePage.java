package eu.tsystems.mms.tic.testframework.report.abstracts;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.model.IReportAnnotationVerifier;
import eu.tsystems.mms.tic.testframework.report.model.TestNumberHelper;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ClassesDetailsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodDetailsPage;
import eu.tsystems.mms.tic.testframework.report.model.ReportAnnotationType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

/**
 * Created by fakr on 18.09.2017
 */
public abstract class AbstractMethodStatePage extends AbstractReportPage implements IReportAnnotationVerifier {

    @Check
    private final GuiElement numberOfListedMethodsElement = new GuiElement(driver, By.xpath(".//div[contains(text(),'Methods: ')]"), mainFrame);

    private final GuiElement methodTable = new GuiElement(this.driver, By.xpath("//table[@class='resultsTable']/tbody"), mainFrame);

    private static String LOCATOR_PREVIOUS = ".//a[text()[contains(.,'%s')]]/../..//td[1]/div"; // method name
    private static String LOCATOR_ACTUAL = ".//a[text()[contains(.,'%s')]]/../..//td[2]/div"; // method name
    private static String LOCATOR_METHOD = ".//a[text()[contains(.,'%s')]]"; // method name
    private static String LOCATOR_CLASS = LOCATOR_METHOD + "/../..//a[contains(@title, 'Jump to %s')]"; // method name, classname
    private static String LOCATOR_ROW = LOCATOR_METHOD + "/../..";

    private static String LOCATOR_LEGEND_ITEM = ".//div[@title='%s']";

    public AbstractMethodStatePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void assertAnnotationMarkIsDisplayed(ReportAnnotationType annotationType, String methodName) {
        GuiElement methodBody = new GuiElement(driver, By.xpath(String.format(LOCATOR_METHOD, methodName)), mainFrame);
        GuiElement annotationElement = methodBody.getSubElement(By.xpath("./..//" + String.format(LOCATOR_FONT_ANNOTATION, annotationType.getAnnotationDisplayedName())));
        annotationElement.setName("annotationElementFor_" + annotationType.getAnnotationDisplayedName());
        annotationElement.asserts().assertIsDisplayed();
    }

    @Override
    public void assertAllAnnotationMarksAreDisplayed(String methodName) {
        for (ReportAnnotationType annotationType : ReportAnnotationType.values()) {
            assertAnnotationMarkIsDisplayed(annotationType, methodName);
        }
    }

    @Override
    public void assertRetryMarkerIsDisplayed(String methodName) {
        GuiElement methodBody = new GuiElement(driver, By.xpath(String.format(LOCATOR_METHOD, methodName)), mainFrame);
        GuiElement annotationElement = methodBody.getSubElement(By.xpath("./." + String.format(LOCATOR_FONT_ANNOTATION, RETRIED_NAME)));
        annotationElement.setName("annotationElementFor_" + RETRIED_NAME);
        annotationElement.asserts().assertIsDisplayed();
    }

    public void assertNumberOfMethodsByMethodsString(int expectedNumberOfMethods) {
        String wholeMethodsString = numberOfListedMethodsElement.getText().trim();
        int actualNumberOfMethods = Integer.parseInt(wholeMethodsString.split(":")[1].trim());
        Assert.assertEquals(actualNumberOfMethods, expectedNumberOfMethods, "The displayed number of methods is NOT correct");
    }

    public void assertNumberOfMethodsByCountingRows(int expectedNumberOfMethods) {
        int actualNumberOfMethods = getNumberOfMethodsByCountingRows();
        Assert.assertEquals(actualNumberOfMethods, expectedNumberOfMethods, "The displayed number of methods is NOT correct");
    }

    public int getNumberOfMethodsByCountingRows() {
        return methodTable.getSubElement(By.xpath("./tr")).getNumberOfFoundElements();
    }

    public TestResultHelper.TestResultChangedMethodState getPreviousTestResultForMethod(AbstractMethodStateEntry entry) {
        GuiElement previousElement = new GuiElement(driver, By.xpath(String.format(LOCATOR_PREVIOUS, entry.getMethodName())), mainFrame);
        previousElement.setName("previousElement");
        return TestResultHelper.TestResultChangedMethodState.valueOf(previousElement.getAttribute("title"));
    }

    public TestResultHelper.TestResultChangedMethodState getActualTestResultForMethod(AbstractMethodStateEntry entry) {
        GuiElement actualElement = new GuiElement(driver, By.xpath(String.format(LOCATOR_ACTUAL, entry.getMethodName())), mainFrame);
        actualElement.setName("actualElement");
        return TestResultHelper.TestResultChangedMethodState.valueOf(actualElement.getAttribute("title"));
    }

    public String getClassNameForMethod(AbstractMethodStateEntry entry) {
        GuiElement classNameElement = new GuiElement(driver, By.xpath(String.format(LOCATOR_CLASS, entry.getMethodName(), entry.getTestUnderTestClassName())), mainFrame);
        classNameElement.setName("classElement");
        return classNameElement.getText();
    }

    public void assertAllLegendItemsAreDisplayed() {
        for (TestResultHelper.TestResultChangedMethodState ackType : TestResultHelper.TestResultChangedMethodState.values()) {
            GuiElement legendElement = new GuiElement(driver, By.xpath(String.format(LOCATOR_LEGEND_ITEM, ackType.toString())), mainFrame);
            legendElement.setName("LegendElementFor_" + ackType);
            legendElement.asserts().assertIsDisplayed();
        }
    }

    public void assertTestIndications(AbstractMethodStateEntry entry) {
        Assert.assertEquals(
                getPreviousTestResultForMethod(entry),
                entry.getPreviousState(),
                "The 'Previous' TestResult for method " + entry.getMethodName() + " is NOT correct"
        );
        Assert.assertEquals(
                getActualTestResultForMethod(entry),
                entry.getActualState(),
                "The 'Actual' TestResult for method " + entry.getMethodName() + " is NOT correct"
        );
    }

    public void assertNumberOfListedStateChanges(String reportDirectory, TestNumberHelper testNumberHelper) {
        Assert.assertEquals(getNumberOfListedStateChanges(), testNumberHelper.getNumberOfStateChanges(), "The number of listed state changes is correct in the " + reportDirectory);
    }

    public void assertMethodDetailsLink(AbstractMethodStateEntry entry) {
        MethodDetailsPage methodDetailsPage = gotoMethodDetailsForEntry(entry);
        Assert.assertTrue(methodDetailsPage.getMethodNameString().contains(entry.getMethodName()), "The link of " + entry.getMethodName() + " does NOT lead to the correct method details page in the 1st report.");
    }

    public void assertClassesDetailsLink(AbstractMethodStateEntry entry) {
        ClassesDetailsPage testMethodsPage = gotoClassesDetailsByMethodName(entry);
        testMethodsPage.assertMethodNameIsDisplayedForTestMethod(entry.getMethodName());
    }

    public int getNumberOfListedStateChanges() {
        return methodTable.getSubElement(By.xpath("./tr")).getNumberOfFoundElements();
    }

    private MethodDetailsPage gotoMethodDetailsForEntry(AbstractMethodStateEntry entry) {
        GuiElement methodLink = new GuiElement(driver, By.xpath(String.format(LOCATOR_METHOD, entry.getMethodName())), mainFrame);
        methodLink.setName("methodLinkFor_" + entry.getMethodName());
        methodLink.click();
        return PageFactory.create(MethodDetailsPage.class, this.driver);
    }

    private ClassesDetailsPage gotoClassesDetailsByMethodName(AbstractMethodStateEntry entry) {
        GuiElement classesLink = new GuiElement(driver, By.xpath(String.format(LOCATOR_CLASS, entry.getMethodName(), entry.getTestUnderTestClassName())), mainFrame);
        classesLink.setName("classesLinkFor_" + entry.getMethodName());
        classesLink.click();
        return PageFactory.create(ClassesDetailsPage.class, this.driver);
    }

    private GuiElement getMethodStateElementRow(AbstractMethodStateEntry entry) {
        GuiElement methodStateElementRow = new GuiElement(driver, By.xpath(String.format(LOCATOR_ROW, entry.getMethodName())), mainFrame);
        methodStateElementRow.setName("methodStateElementRowFor_" + entry.getMethodName());
        return methodStateElementRow;
    }

}
