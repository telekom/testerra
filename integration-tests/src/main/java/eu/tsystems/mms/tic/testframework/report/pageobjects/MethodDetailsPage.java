package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
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
    private IGuiElement backTab = new GuiElement(this.driver, By.xpath("//div[@class='detailsmenu']"), mainFrame);
    private IGuiElement detailsTab = new GuiElement(this.driver, By.id("buttondetails"), mainFrame);
    private IGuiElement stepsTab = new GuiElement(this.driver, By.id("buttonlogs"), mainFrame);
    private IGuiElement stackTab = new GuiElement(this.driver, By.id("buttonstack"), mainFrame);
    private IGuiElement screenShotTab = new GuiElement(this.driver, By.id("buttonscreen"), mainFrame);
    private IGuiElement minorErrorTab = new GuiElement(this.driver, By.id("buttonminor"), mainFrame);
    private IGuiElement dependenciesTab = new GuiElement(this.driver, By.id("buttondeps"), mainFrame);
    private IGuiElement evolutionTab = new GuiElement(this.driver, By.id("buttonhistory"), mainFrame);

    private IGuiElement historyElementsGraph = new GuiElement(this.driver, By.id("gitGraph"), mainFrame);

    /**
     * Method
     */
    //TODO  IDs einfügen -> Jira-Ticket: XETA-524
    private IGuiElement methodNameString = new GuiElement(this.driver, By.xpath("(//*[@class='dashboardTextSmall'])[1]"), mainFrame);
    private IGuiElement classNameString = new GuiElement(this.driver, By.xpath("//tbody/tr[1]/td[3]/*[4]"), mainFrame);
    private IGuiElement methodResultString = new GuiElement(this.driver, By.xpath("//tbody/tr[1]/td[3]/*[5]/*[1]"), mainFrame);
    private IGuiElement stepString = new GuiElement(this.driver, By.xpath("//tbody/tr[1]/td[3]/*[5]/*[2]"), mainFrame);

    /**
     * Context
     */
    private IGuiElement contextButton = new GuiElement(this.driver, By.xpath("//*[@title=\"Show Fingerprint\" and @onclick=\"toggleElement('context');\"]"), mainFrame);
    private IGuiElement context = new GuiElement(this.driver, By.id("context"), mainFrame);

    private IGuiElement repairedFailsIndication = new GuiElement(this.driver, By.xpath("//div[@class='skipped']"), mainFrame);

    //TODO  IDs einfügen -> Jira-Ticket: XETA-524
    public String durationLocator = "//*[@class='cellTop']//*[contains(text(), 'Duration')]/..";
    private IGuiElement duration = new GuiElement(this.driver, By.id("actualRunDuration"), mainFrame);
    private IGuiElement startTime = new GuiElement(this.driver, By.xpath(durationLocator + "//div[@class='dashboardTextSmall'][1]"), mainFrame);
    private IGuiElement finishTime = new GuiElement(this.driver, By.xpath(durationLocator + "//div[@class='dashboardTextSmall'][2]"), mainFrame);

    //TODO  IDs einfügen -> Jira-Ticket: XETA-524
    private IGuiElement evolutionEntry1 = new GuiElement(this.driver, By.xpath("//*[@class='highcharts-markers highcharts-series-0 highcharts-tracker']/*[1]"), mainFrame);
    private IGuiElement evolutionEntry2 = new GuiElement(this.driver, By.xpath("//*[@class='highcharts-markers highcharts-series-0 highcharts-tracker']/*[2]"), mainFrame);

    //TODO  IDs einfügen
    private IGuiElement minorCount = new GuiElement(this.driver, By.xpath("//td[@class='cellTop']//div[@class='error clickable']"), mainFrame);

    //TODO  IDs einfügen
    private IGuiElement errorMessageString = new GuiElement(this.driver, By.xpath("//div[@style='color: red; font-size: 30px; padding: 25px; line-height: 40px;']"), mainFrame);
    private IGuiElement fingerprintButton = new GuiElement(this.driver, By.xpath("//*[@title=\"Show Fingerprint\" and @onclick=\"toggleElement('fingerprint');\"]"), mainFrame);
    private IGuiElement fingerprintString = new GuiElement(this.driver, By.xpath("//div[@id='fingerprint']//div[@class='error']"), mainFrame);

    public IGuiElement durationEvo = new GuiElement(this.driver, By.xpath("//*[@class=' highcharts-background']"), mainFrame);
    public String DurEvoPoint_LOCATOR = ("//*[@class='highcharts-markers highcharts-series-0 highcharts-tracker']//*[%d]");

    public MethodDetailsPage(WebDriver driver) {

        super(driver);
    }

    public IGuiElement getBackTab() {
        return backTab;
    }

    public IGuiElement getDetailsTab() {
        return detailsTab;
    }

    public IGuiElement getStepsTab() {
        return stepsTab;
    }

    public IGuiElement getStackTab() {
        return stackTab;
    }

    public IGuiElement getScreenShotTab() {
        return screenShotTab;
    }

    public IGuiElement getMinorErrorTab() {
        return minorErrorTab;
    }

    public IGuiElement getDependenciesTab() {
        return dependenciesTab;
    }

    public IGuiElement getEvolutionTab() {
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

    public IGuiElement getRepairedFailsIndication() {
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

    public IGuiElement getEvolutionEntry1() {
        return evolutionEntry1;
    }

    public IGuiElement getEvolutionEntry2() {
        return evolutionEntry2;
    }

    public IGuiElement getMinorCount() {
        return minorCount;
    }

    public IGuiElement getErrorMessageString() {
        return errorMessageString;
    }

    public IGuiElement getFingerprintString() {
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
        IGuiElement historyDateElement = new GuiElement(this.driver, By.xpath(dateLocator), mainFrame);
        return historyDateElement.getText();
    }

    public String getTextforHistoryElementByPosition(int position) {
        return getHistoryElementByPosition(position).getText();
    }

    public IGuiElement getHistoryElementByPosition(int position) {
        final int positionOffsetDOM = 1; // First history position is 2, second is 3, etc.
        if (position < 1) {
            throw new TesterraRuntimeException("Invalid position in HISTORY of " + MethodDetailsPage.class.getSimpleName() + ": " + position);
        }
        String elementLocator = String.format(HISTORY_ELEMENT_LOCATOR, position + positionOffsetDOM);
        IGuiElement historyElement = new GuiElement(this.driver, By.xpath(elementLocator), mainFrame);
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
        IGuiElement DurEvoPoint = new GuiElement(this.driver, By.xpath(LOCATOR));
        DurEvoPoint.mouseOver();
        return PageFactory.create(MethodDetailsPage.class, driver);
    }

    public String getMethodNameString() {
        return methodNameString.getText();
    }

    public IGuiElement getMethodNameElement() {
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

    public IGuiElement getDurEvo() {
        return durationEvo;
    }

    public IGuiElement getMinorErrors() {
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
        IGuiElement methodNameElement = getMethodNameElement();
        IGuiElement annotationElement = methodNameElement.getSubElement(By.xpath(String.format(LOCATOR_FONT_ANNOTATION, annotationType.getAnnotationDisplayedName())));
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
        IGuiElement methodNameElement = getMethodNameElement();
        IGuiElement annotationElement = methodNameElement.getSubElement(By.xpath(String.format(LOCATOR_FONT_ANNOTATION, RETRIED_NAME)));
        annotationElement.setName("annotationElementFor" + RETRIED_NAME);
        annotationElement.asserts().assertIsDisplayed();
    }*/
}
