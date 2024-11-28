package io.testerra.report.test.pages.report.methodReport;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.PreparedLocator;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;

public class ComparisonDialogOverlay extends Page implements AssertProvider {
    @Check
    private final UiElement contrastIcon = findIconInDialog("compare");
    @Check
    private final UiElement switchIcon = findIconInDialog("compare_arrows");
    @Check
    private final UiElement leftSelection = find(By.xpath("//mdc-select[@label='Left']"));
    @Check
    private final UiElement rightSelection = find(By.xpath("//mdc-select[@label='Right']"));
    @Check
    private final UiElement slider= find(By.xpath("//div[contains(@class,'slider')]"));
    private final UiElement selectedItemLeft = leftSelection.find(By.xpath("//span[contains(@class,'selected-text')]"));
    private final UiElement selectedItemRight = rightSelection.find(By.xpath("//span[contains(@class,'selected-text')]"));
    PreparedLocator itemLocator = LOCATE.prepare("//span[text()='%s']");

    public ComparisonDialogOverlay(WebDriver webDriver) {
        super(webDriver);
    }

    private UiElement findIconInDialog(String icon){
        return find(By.xpath(String.format("//mdc-icon[text()='%s']",icon)));
    }

    public ReportDetailsTab closeDialog() {
        UiElement closeBtn = find(By.xpath("//button//span[text()='clear']"));
        closeBtn.click();
        return createPage(ReportDetailsTab.class);
    }

    public void checkSelectedAndContentFromStartingMatched(String state) {
        HashMap<String, String> stateMapping = new HashMap<String, String>();
        stateMapping.put("Actual", "Difference");
        stateMapping.put("Difference", "Expected");
        stateMapping.put("Expected", "Difference");

        HashMap<String, String> screenshotMapping = new HashMap<String, String>();
        screenshotMapping.put("Actual", "actual_difference_referenceImage");
        screenshotMapping.put("Difference", "difference_expected_referenceImage");
        screenshotMapping.put("Expected", "expected_difference_referenceImage");

        CONTROL.collectAssertions(()->{
            UiElement defaultSelectedItemLeft = selectedItemLeft.find(itemLocator.with(state));
            UiElement imgContent = find(By.xpath("//div[contains(@class,'img-comp-container')]"));
            defaultSelectedItemLeft.expect().displayed(true);
            String expectedRightState = stateMapping.get(state);
            String screenshotReference = screenshotMapping.get(state);
            ASSERT.assertNotNull(expectedRightState);

            UiElement defaultSelectedItemRight = selectedItemRight.find(itemLocator.with(expectedRightState));
            defaultSelectedItemRight.expect().displayed(true);
            imgContent.expect().screenshot().pixelDistance(screenshotReference).isLowerThan(10);

        });

    }
}
