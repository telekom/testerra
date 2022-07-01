package eu.tsystems.mms.tic.testframework.core.pageobjects.testdata;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.XPath;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created on 01.07.2022
 *
 * @author mgn
 */
public class UiElementShadowDomMenuPage extends Page {

    @Check(checkRule = CheckRule.IS_PRESENT)
    private UiElement shadowRoot = find(By.xpath("//smart-ui-menu")).shadowRoot();

    private UiElement shadowContent = shadowRoot.find(By.xpath("//smart-menu[@ref = 'menu']"));

    public UiElementShadowDomMenuPage(WebDriver webDriver) {
        super(webDriver);
    }

    public void openMenu(String name) {
        UiElement menuElem = this.shadowContent.find(XPath.from("smart-menu-items-group").select(XPath.from("span").text().contains(name)));
        menuElem.click();


    }
}
