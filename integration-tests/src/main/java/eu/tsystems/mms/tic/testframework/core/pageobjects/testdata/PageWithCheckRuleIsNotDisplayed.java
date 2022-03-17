package eu.tsystems.mms.tic.testframework.core.pageobjects.testdata;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created on 16.03.2022
 *
 * @author mgn
 */
public class PageWithCheckRuleIsNotDisplayed extends Page {

    @Check(checkRule = CheckRule.IS_NOT_DISPLAYED)
    private UiElement isNotDisplayed = find(By.id("notDisplayedElement"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public PageWithCheckRuleIsNotDisplayed(WebDriver driver) {
        super(driver);
    }
}
