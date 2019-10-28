/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.core.test.pageobjects.guielement;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.test.ClassicTestPage;
import eu.tsystems.mms.tic.testframework.core.test.FluentTestPage;
import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import eu.tsystems.mms.tic.testframework.layout.LayoutCheck;
import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IImagePropertyAssertion;
import eu.tsystems.mms.tic.testframework.utils.AssertUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

/**
 * Created by pele on 31.08.2015.
 */
public class GuiElementNewApiTest extends AbstractTestSitesTest {

    @Test
    public void test_NewApi_Page_call_url_endsWith() {
        FluentTestPage page = pageFactory.create(FluentTestPage.class);
        page.call(TestPage.INPUT_TEST_PAGE.getUrl());
        page.url().endsWith("input.html");
    }

    @Test(expectedExceptions = TimeoutException.class)
    public void test_NewApi_Page_call_url_endsWith_failed() {
        FluentTestPage page = pageFactory.create(FluentTestPage.class);
        page.call(TestPage.INPUT_TEST_PAGE.getUrl());
        page.url().endsWith("nonexistingfile.html");
        //page.url().endsWith("input.html");
    }

    public void test_NewApi_GuiElement() {
        FluentTestPage page = pageFactory.create(FluentTestPage.class);
        page.call("https://www.google.de");
        // Expect GuiElement(By.id("11")).value(attribute: value) [Hausmaus] contains not [maus]


        // given    | when           | then
        page.input().sendKeys("affe").text().contains("affe").nonFunctional().containsNot("maus");

        // Expect GuiElement(By.id("11")).text.length [20] lower than 5
        page.input().text().length().lowerThan(5);


        // Expect GuiElement(By.qa("action/submit")).visible(complete: false) [false] is true
        page.submit().scrollTo().visible(false).isTrue();
        // Expect GuiElement(By.qa("action/submit")).value(attribute: style) [display:none] equals [display:block]
        page.submit().value(Attribute.STYLE).equals("display:block");

        final IImagePropertyAssertion screenshot = page.submit().screenshot();
        // Expect GuiElement(By.qa("action/submit")).screenshot.pixelDistance(referenceImageName: "SubmitButton") [10] between [3] and [5]
        screenshot.pixelDistance("SubmitButton").between(3,5);
        // Expect GuiElement(By.qa("action/submit")).screenshot.file.name [SomeImage] contains [screenshot]
        screenshot.file().name().contains("screenshot");
        // Expect GuiElement(By.qa("action/submit")).screenshot.file.extension [jpg] equals [png]
        screenshot.file().extension().equals("png");
        screenshot.file().bytes().greaterThan(5000);

        // Expect GuiElement(By.qa("action/submit")).present [false] is true
        page.input().mouseOver().clear();

        // Expect TestPageObject.title [SomePageTitle] contains [Form]
        page.title().contains("Form").endsWith("Page");

        // Expect TestPageObject.screenshot.pixelDistance(referenceImageName: "Google") [10] lower than [5]
        page.screenshot().pixelDistance("Google").lowerThan(5);

        page.input().type("maus");
        page.input().click();
        page.submit().text().contains("Done");
    }

    public void test_OldApi_GuiElement() {
        ClassicTestPage page = PageFactory.create(ClassicTestPage.class, WebDriverManager.getWebDriver());
        page.getWebDriver().navigate().to("http://www.google.de");
        page.input().sendKeys("affe");
        page.input().nonFunctionalAsserts().assertTextContains("affe");
        page.submit().scrollToElement();
        page.submit().asserts().assertVisible(false);
        page.submit().asserts().assertAttributeValue("style", "display:block");
        page.submit().asserts().assertScreenshot("SubmitButton", 5);    // Not possible
        page.input().mouseOver();
        page.input().clear();

        AssertUtils.assertContains("Form", page.getWebDriver().getTitle());   // Expected [SomePageTitle] contains [Form]
        Assert.assertTrue(page.getWebDriver().getTitle().endsWith("Page"));   // Expected [true] is false

        double dist = LayoutCheck.run(page.getWebDriver(), "Google");
        AssertUtils.assertLowerThan(
            new BigDecimal(dist),
            new BigDecimal(5),
            String.format("Pixel distance of page %s", "Google")
        );
    }
}
