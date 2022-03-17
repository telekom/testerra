package eu.tsystems.mms.tic.testframework.core.pageobjects.testdata;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created on 16.03.2022
 *
 * @author mgn
 */
public class PageWithCheckRuleIsDisplayed extends Page {

    @Check(checkRule = CheckRule.IS_DISPLAYED)
    private UiElement isDisplayed = find(By.id("notDisplayedElement"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public PageWithCheckRuleIsDisplayed(WebDriver driver) {
        super(driver);
    }
}
