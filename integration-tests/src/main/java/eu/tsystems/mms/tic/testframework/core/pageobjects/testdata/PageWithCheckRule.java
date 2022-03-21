package eu.tsystems.mms.tic.testframework.core.pageobjects.testdata;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created on 21.03.2022
 *
 * @author mgn
 */
public class PageWithCheckRule extends Page {

    @Check(checkRule = CheckRule.IS_PRESENT)
    private GuiElement isDisplayed = new GuiElement(this.getWebDriver(), By.id("notDisplayedElement"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public PageWithCheckRule(WebDriver driver) {
        super(driver);
    }

}
