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
public class PageWithCheckRuleIsNotPresent extends Page {

    @Check(checkRule = CheckRule.IS_NOT_PRESENT)
    private UiElement isNotPresent = find(By.id("unknown_element"));

    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public PageWithCheckRuleIsNotPresent(WebDriver driver) {
        super(driver);
    }
}
