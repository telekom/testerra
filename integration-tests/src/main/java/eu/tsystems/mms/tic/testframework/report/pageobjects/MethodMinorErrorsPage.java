package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by riwa on 04.01.2017.
 */
public class MethodMinorErrorsPage extends MethodDetailsPage {
    //TODO need IDs here, the xpaths are terrible -> Jira-Ticket: XETA-573
    IGuiElement assertion = new GuiElement(this.driver, By.cssSelector(".standardTable.tr>td>a"), mainFrame);
    IGuiElement assertionMessage = new GuiElement(this.driver, By.cssSelector("#non-functional-exception-0>div"), mainFrame);

    public MethodMinorErrorsPage(WebDriver driver) {
        super(driver);
        checkPage();
    }

    public IGuiElement getAssertion() {
        return assertion;
    }

    public MethodMinorErrorsPage clickAssertion() {
        assertion.click();
        return PageFactory.create(MethodMinorErrorsPage.class, this.driver);
    }

    public String getAssertionMessage() {
        return assertionMessage.getText();
    }
}
