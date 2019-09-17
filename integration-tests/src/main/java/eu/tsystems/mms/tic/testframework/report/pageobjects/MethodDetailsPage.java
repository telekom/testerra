package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractMethodDetailsPage;
import eu.tsystems.mms.tic.testframework.report.model.IReportAnnotationVerifier;
import eu.tsystems.mms.tic.testframework.report.model.ReportAnnotationType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;


/**
 * Created by riwa on 27.10.2016.
 */
public class MethodDetailsPage extends AbstractMethodDetailsPage implements IReportAnnotationVerifier {

    private String HISTORY_ELEMENT_LOCATOR = ("//div[@class='gitgraph-inner']//div[%d]");
    private String HISTORY_DATE_LOCATOR = ("");

    @Check
    private GuiElement backTab = new GuiElement(this.driver, By.xpath("//div[@class='detailsmenu']"), mainFrame);
    private GuiElement detailsTab = new GuiElement(this.driver, By.id("buttondetails"), mainFrame);
    private GuiElement stepsTab = new GuiElement(this.driver, By.id("buttonlogs"), mainFrame);
    private GuiElement stackTab = new GuiElement(this.driver, By.id("buttonstack"), mainFrame);
    private GuiElement screenShotTab = new GuiElement(this.driver, By.id("buttonscreen"), mainFrame);
    private GuiElement minorErrorTab = new GuiElement(this.driver, By.id("buttonminor"), mainFrame);
    private GuiElement dependenciesTab = new GuiElement(this.driver, By.id("buttondeps"), mainFrame);
    private GuiElement evolutionTab = new GuiElement(this.driver, By.id("buttonhistory"), mainFrame);

    private GuiElement historyElementsGraph = new GuiElement(this.driver, By.id("gitGraph"), mainFrame);

    /**
     * Method
     */
    //TODO  IDs einfügen -> Jira-Ticket: XETA-524
    private GuiElement methodNameString = new GuiElement(this.driver, By.xpath("(//*[@class='dashboardTextSmall'])[1]"), mainFrame);
    private GuiElement classNameString = new GuiElement(this.driver, By.xpath("//tbody/tr[1]/td[3]/*[4]"), mainFrame);
    private GuiElement methodResultString = new GuiElement(this.driver, By.xpath("//tbody/tr[1]/td[3]/*[5]/*[1]"), mainFrame);
    private GuiElement stepString = new GuiElement(this.driver, By.xpath("//tbody/tr[1]/td[3]/*[5]/*[2]"), mainFrame);

    /**
     * Context
     */
    private GuiElement contextButton = new GuiElement(this.driver, By.xpath("//*[@title=\"Show Fingerprint\" and @onclick=\"toggleElement('context');\"]"), mainFrame);
    private GuiElement context = new GuiElement(this.driver, By.id("context"), mainFrame);

    private GuiElement repairedFailsIndication = new GuiElement(this.driver, By.xpath("//div[@class='skipped']"), mainFrame);

    //TODO  IDs einfügen -> Jira-Ticket: XETA-524
    public String durationLocator = "//*[@class='cellTop']//*[contains(text(), 'Duration')]/..";
    private GuiElement duration = new GuiElement(this.driver, By.id("actualRunDuration"), mainFrame);
    private GuiElement startTime = new GuiElement(this.driver, By.xpath(durationLocator + "//div[@class='dashboardTextSmall'][1]"), mainFrame);
    private GuiElement finishTime = new GuiElement(this.driver, By.xpath(durationLocator + "//div[@class='dashboardTextSmall'][2]"), mainFrame);

    //TODO  IDs einfügen -> Jira-Ticket: XETA-524
    private GuiElement evolutionEntry1 = new GuiElement(this.driver, By.xpath("//*[@class='highcharts-markers highcharts-series-0 highcharts-tracker']/*[1]"), mainFrame);
    private GuiElement evolutionEntry2 = new GuiElement(this.driver, By.xpath("//*[@class='highcharts-markers highcharts-series-0 highcharts-tracker']/*[2]"), mainFrame);

    //TODO  IDs einfügen
    private GuiElement minorCount = new GuiElement(this.driver, By.xpath("//td[@class='cellTop']//div[@class='error clickable']"), mainFrame);

    //TODO  IDs einfügen
    private GuiElement errorMessageString = new GuiElement(this.driver, By.xpath("//div[@style='color: red; font-size: 30px; padding: 25px; line-height: 40px;']"), mainFrame);
    private GuiElement fingerprintButton = new GuiElement(this.driver, By.xpath("//*[@title=\"Show Fingerprint\" and @onclick=\"toggleElement('fingerprint');\"]"), mainFrame);
    private GuiElement fingerprintString = new GuiElement(this.driver, By.xpath("//div[@id='fingerprint']//div[@class='error']"), mainFrame);

    public GuiElement durationEvo = new GuiElement(this.driver, By.xpath("//*[@class=' highcharts-background']"), mainFrame);
    public String DurEvoPoint_LOCATOR = ("//*[@class='highcharts-markers highcharts-series-0 highcharts-tracker']//*[%d]");

    public MethodDetailsPage(WebDriver driver) {

        super(driver);
    }

    public GuiElement getBackTab() {
        return backTab;
    }

    public GuiElement getDetailsTab() {
        return detailsTab;
    }

    public GuiElement getStepsTab() {
        return stepsTab;
    }

    public GuiElement getStackTab() {
        return stackTab;
    }

    public GuiElement getScreenShotTab() {
        return screenShotTab;
    }

    public GuiElement getMinorErrorTab() {
        return minorErrorTab;
    }

    public GuiElement getDependenciesTab() {
        return dependenciesTab;
    }

    public GuiElement getEvolutionTab() {
        return evolutionTab;
    }

    public MethodDetailsPage toggleContext() {
        contextButton.click();
        return PageFactory.create(MethodDetailsPage.class, this.driver);
    }

    public String getContextClassString() {
        String classString = context.getText().split("Class: ")[1].split("\n")[0];
        return classString;
    }

    public String getContextSuiteString() {
        String suiteString = context.getText().split("Suite: ")[1].split("\n")[0];
        return suiteString;
    }

    public String getContextTestString() {
        String testString = context.getText().split("Test: ")[1].split("\n")[0];
        return testString;
    }

    public GuiElement getRepairedFailsIndication() {
        return repairedFailsIndication;
    }

    public String getDuration() {
        return duration.getText().toString();
    }

    public String getStartTime() {
        return startTime.getText().toString();
    }

    public String getFinishTime() {
        return finishTime.getText().toString();
    }

    public GuiElement getEvolutionEntry1() {
        return evolutionEntry1;
    }

    public GuiElement getEvolutionEntry2() {
        return evolutionEntry2;
    }

    public GuiElement getMinorCount() {
        return minorCount;
    }

    public GuiElement getErrorMessageString() {
        return errorMessageString;
    }

    public GuiElement getFingerprintString() {
        return fingerprintString;
    }

    public MethodDetailsPage toggleFingerprint() {
        fingerprintButton.click();
        return PageFactory.create(MethodDetailsPage.class, this.driver);
    }

    /**
     * History Timeline
     */

    public int getNumberOfAllEntries() {
        driver.switchTo().defaultContent();
        driver.switchTo().frame(0);
        List<WebElement> elements = driver.findElements(By.xpath("//div[@class='gitgraph-inner']/div[@class='gitgraph-detail']"));
        int numberOfAllEntries = elements.size();
        return numberOfAllEntries;
    }

    public int historyTimelineFirstTestCounter() {
        String historyEntry = getTextforHistoryElementByPosition(1);
        String subStr = historyEntry.substring(0, (historyEntry.indexOf("t")) - 1);
        int numberofTests = Integer.parseInt(subStr.replace("Passed ", ""));
        return numberofTests;
    }

    public String getDateforHistoryElementByPosition(int position) {
        String dateLocator = String.format(HISTORY_DATE_LOCATOR, position + 1);
        GuiElement historyDateElement = new GuiElement(this.driver, By.xpath(dateLocator), mainFrame);
        return historyDateElement.getText();
    }

    public String getTextforHistoryElementByPosition(int position) {
        return getHistoryElementByPosition(position).getText();
    }

    public GuiElement getHistoryElementByPosition(int position) {
        final int positionOffsetDOM = 1; // First history position is 2, second is 3, etc.
        if (position < 1) {
            throw new TesterraRuntimeException("Invalid position in HISTORY of " + MethodDetailsPage.class.getSimpleName() + ": " + position);
        }
        String elementLocator = String.format(HISTORY_ELEMENT_LOCATOR, position + positionOffsetDOM);
        GuiElement historyElement = new GuiElement(this.driver, By.xpath(elementLocator), mainFrame);
        historyElement.setName("historyEntry Position # " + position);
        return historyElement;
    }

    /**
     * Duration Evolution - Only Passed Tests
     */

    public int countTestsInDurEvo() {
        List<WebElement> CountedTests = driver.findElements(By.xpath("//*[@class='highcharts-markers highcharts-series-0 highcharts-tracker']//*"));
        return CountedTests.size();
    }

    public MethodDetailsPage mouseOverDurEvoPoint(int parameter) {
        String LOCATOR = String.format(DurEvoPoint_LOCATOR, parameter);
        GuiElement DurEvoPoint = new GuiElement(this.driver, By.xpath(LOCATOR));
        DurEvoPoint.mouseOver();
        return PageFactory.create(MethodDetailsPage.class, driver);
    }

    public String getMethodNameString() {
        return methodNameString.getText();
    }

    public GuiElement getMethodNameElement() {
        return methodNameString;
    }

    public String getClassNameString() {
        return classNameString.getText();
    }

    public String getMethodResultString() {
        return methodResultString.getText();
    }

    public String getStepString() {
        return stepString.getText();
    }

    public GuiElement getDurEvo() {
        return durationEvo;
    }

    public GuiElement getMinorErrors() {
        return minorErrorButton;
    }

    public DashboardPage clickBackTab() {
        backTab.click();
        return PageFactory.create(DashboardPage.class, this.driver);
    }

    public MethodDetailsPage clickDetailsTab() {
        detailsTab.click();
        return PageFactory.create(MethodDetailsPage.class, this.driver);
    }

    public MethodStepsPage clickStepsTab() {
        stepsTab.click();
        return PageFactory.create(MethodStepsPage.class, this.driver);
    }

    public MethodStackPage clickStackTab() {
        stackTab.click();
        return PageFactory.create(MethodStackPage.class, this.driver);
    }

    public MethodScreenshotPage clickScreenShotTab() {
        screenShotTab.click();
        return PageFactory.create(MethodScreenshotPage.class, this.driver);
    }

    public MethodMinorErrorsPage clickMinorErrorsTab() {
        minorErrorTab.click();
        return PageFactory.create(MethodMinorErrorsPage.class, this.driver);
    }

    public MethodDependenciesPage clickDependenciesTab() {
        dependenciesTab.click();
        return PageFactory.create(MethodDependenciesPage.class, this.driver);
    }

    public MethodEvolutionPage clickEvolutionTab() {
        evolutionTab.click();
        return PageFactory.create(MethodEvolutionPage.class, this.driver);
    }

    @Override
    public void assertAnnotationMarkIsDisplayed(ReportAnnotationType annotationType, String methodName) {
        GuiElement methodNameElement = getMethodNameElement();
        GuiElement annotationElement = methodNameElement.getSubElement(By.xpath(String.format(LOCATOR_FONT_ANNOTATION, annotationType.getAnnotationDisplayedName())));
        annotationElement.setName("annotationElementFor" + annotationType);
        annotationElement.asserts().assertIsDisplayed();
    }

    @Override
    public void assertAllAnnotationMarksAreDisplayed(String methodName) {
        for (ReportAnnotationType annotationType : ReportAnnotationType.values()) {
            assertAnnotationMarkIsDisplayed(annotationType, methodName);
        }
    }

    //@Override
    /*public void assertRetryMarkerIsDisplayed(String methodName) {
        GuiElement methodNameElement = getMethodNameElement();
        GuiElement annotationElement = methodNameElement.getSubElement(By.xpath(String.format(LOCATOR_FONT_ANNOTATION, RETRIED_NAME)));
        annotationElement.setName("annotationElementFor" + RETRIED_NAME);
        annotationElement.asserts().assertIsDisplayed();
    }*/
}