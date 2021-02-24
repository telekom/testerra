package eu.tsystems.mms.tic.testframework.core.pageobjects.testdata;

import eu.tsystems.mms.tic.testframework.pageobjects.AbstractComponent;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InvalidComponentPage extends Page {


    public static class InvalidComponent extends AbstractComponent<InvalidComponent> {

        @Check
        private UiElement inexistentElement = find(By.tagName("inexistent-element"));

        public InvalidComponent(UiElement rootElement) {
            super(rootElement);
        }
    }

    @Check
    private InvalidComponent invalidComponent = createComponent(InvalidComponent.class, find(By.className("box")));

    public InvalidComponentPage(WebDriver webDriver) {
        super(webDriver);
    }
}
