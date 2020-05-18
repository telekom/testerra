package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractReportPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ThreadsPage extends AbstractReportPage {

    @Check
    private GuiElement headLine = new GuiElement(this.getWebDriver(), By.xpath("//h2[text()='Threads']"), mainFrame);
    private GuiElement threadsDataTable = new GuiElement(this.getWebDriver(), By.id("mytimeline"), mainFrame);

    private String LOCATOR_METHOD_NAME_LINK = "(//font[text()='%s']/..)[last()]";

    public ThreadsPage(WebDriver driver) {
        super(driver);
    }

    public void assertPageIsDisplayedCorrectly(){
        threadsDataTable.asserts().assertIsDisplayed();
    }

    public MethodDetailsPage clickMethodAndOpenMethodDetailsPage(String methodName) {
        GuiElement methodNameLink = new GuiElement(this.getWebDriver(), By.xpath(String.format(LOCATOR_METHOD_NAME_LINK, methodName)), mainFrame);
        methodNameLink.scrollToElement();
        methodNameLink.click();

        return PageFactory.create(MethodDetailsPage.class, this.getWebDriver());
    }
}
