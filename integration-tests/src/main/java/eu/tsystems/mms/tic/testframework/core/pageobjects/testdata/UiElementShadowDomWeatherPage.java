package eu.tsystems.mms.tic.testframework.core.pageobjects.testdata;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.TestableUiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.XPath;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created on 01.07.2022
 *
 * @author mgn
 */
public class UiElementShadowDomWeatherPage extends Page {

    @Check(checkRule = CheckRule.IS_PRESENT)
    private UiElement shadowRoot = find(By.xpath("//section[@class = 'weather']")).shadowRoot();

    private UiElement shadowContent = shadowRoot.find(By.xpath("//span//main"));

    private UiElement weatherDesc = shadowContent.find(By.xpath("//h1"));
    private UiElement weatherTemp = shadowContent.find(By.xpath("//h2"));

    public UiElementShadowDomWeatherPage(WebDriver webDriver) {
        super(webDriver);
    }

    public void changeCity(String name) {
        this.shadowContent.find(XPath.from("a").text().contains(name)).click();
    }

    public String getWeather() {
        return String.format("%s with %s", this.weatherDesc.waitFor().text().getActual(), this.weatherTemp.waitFor().text().getActual());
    }

    public TestableUiElement getTemperature() {
        return this.weatherTemp;
    }
}
