package io.testerra.report.test.pages.report.methodReport;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LastScreenshotOverlay extends Page {

    @Check
    private final UiElement overlayContainer = find(By.xpath("//mdc-dialog"));
    @Check
    private final UiElement screenshotPathElement = overlayContainer.find(By.xpath("//li[./span[text()='PageSource']]/a"));

    private final UiElement nextScreenshotButton = overlayContainer.find(By.xpath("//button[@icon='keyboard_arrow_right']"));

    public LastScreenshotOverlay(WebDriver webDriver) {
        super(webDriver);
    }

    public LastScreenshotOverlay swipeToNextScreenshot() {
        nextScreenshotButton.click();
        return createPage(LastScreenshotOverlay.class);
    }

    public String getPathToScreenshot() {
        return screenshotPathElement.expect().text().getActual();
    }

    public void assertSingleScreenshot() {
        nextScreenshotButton.assertThat().displayed(false);
    }
}
