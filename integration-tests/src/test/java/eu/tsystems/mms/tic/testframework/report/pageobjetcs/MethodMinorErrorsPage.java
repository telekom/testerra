package eu.tsystems.mms.tic.testframework.report.pageobjetcs;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MethodMinorErrorsPage extends MethodDetailsPage {

    GuiElement assertion = new GuiElement(this.getWebDriver(), By.cssSelector(".standardTable.tr>td>a"), mainFrame);
    GuiElement assertionMessage = new GuiElement(this.getWebDriver(), By.cssSelector("#non-functional-exception-0>div"), mainFrame);

    public MethodMinorErrorsPage(WebDriver driver) {
        super(driver);
        checkPage();
    }

    public GuiElement getAssertion() {
        return assertion;
    }

    public MethodMinorErrorsPage clickAssertion() {
        assertion.click();
        return PageFactory.create(MethodMinorErrorsPage.class, this.getWebDriver());
    }

    public String getAssertionMessage() {
        return assertionMessage.getText();
    }
}
