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
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.core.test.ClassicTestPage;
import eu.tsystems.mms.tic.testframework.core.test.FluentTestPage;
import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.layout.LayoutCheck;
import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IImagePropertyAssertion;
import eu.tsystems.mms.tic.testframework.utils.AssertUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;

public class GuiElementStandardFunctionsTest_NewApi extends AbstractTestSitesTest {

    private final static InstantAssertion instantAssertion = Testerra.ioc().getInstance(InstantAssertion.class);
    private FluentTestPage page;

    @BeforeClass
    private FluentTestPage prepareTestPage() {
        page = pageFactory.create(FluentTestPage.class);
        return page.call(TestPage.INPUT_TEST_PAGE.getUrl());
    }

    @Test
    public void test_Page_title() {
        page.title()
            .is("Input test")
            .contains("Input")
            .containsNot("SuperTestPage");

        page.title().length()
            .is(10)
            .lowerThan(100)
            .greaterThan(5)
            .between(1,11)
            .greaterEqualThan(-10)
            .lowerEqualThan(10);
    }

    @Test
    public void test_Page_title_length_fails() {
        try {
            page.title().length().greaterThan(10);
        } catch (AssertionError e) {
            instantAssertion.assertEndsWith(e.getCause().getMessage(), "[10] is greater than [10]", e.getCause().getMessage());
        }
    }

    @Test
    public void test_Page_title_length_fails_collected() {
        collectAssertions(()->{
            page.title().length().greaterThan(10);
        });
    }

    @Test
    public void test_Page_title_length_fails_nonFunctional() {
        nonFunctionalAssertions(()->{
            page.title().length().greaterThan(10);
        });
    }

    @Test
    public void test_Page_url() {
        page.url()
            .beginsWith("http")
            .endsWith("input.html");
    }

    @Test()
    public void test_Page_url_endsWith_fails() {
        try {
            page.url().endsWith("nonexistingfile.html");
        } catch (AssertionError e) {
            instantAssertion.assertEndsWith(e.getCause().getMessage(), "ends with [nonexistingfile.html]", e.getCause().getMessage());
        }
    }

    @Test
    public void test_GuiElement_displayed_false() {
        page.notDisplayedElement().value(Attribute.STYLE).contains("display: none");
        page.notDisplayedElement().displayed().isFalse();
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_GuiElement_displayed_false_fails() {
        page.notDisplayedElement().displayed().isTrue();
    }

    @Test
    public void test_GuiElement_visible_false() {
        page.notVisibleElement().value(Attribute.STYLE).contains("hidden");
        page.notVisibleElement().value("style").contains("hidden");
        page.notVisibleElement().visible(true).isFalse();
        page.notVisibleElement().visible(false).isFalse();
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_GuiElement_visible_false_fails() {
        page.notVisibleElement().visible(true).isTrue();
    }

    @Test
    public void test_NonExistent_GuiElement_present() {
        page.nonExistentElement().present().isFalse();
    }

    @Test
    public void test_NonExistent_GuiElement_fails() {
        try {
            page.nonExistentElement().present().isTrue();
        } catch (AssertionError e) {
            instantAssertion.assertEndsWith(e.getMessage(), "is one of [true, 'on', '1', 'yes']", e.getClass().toString());
        }
    }

    @Test
    public void test_NonExistent_GuiElement_displayed_fails() {
        try {
            page.nonExistentElement().displayed().isFalse();
        } catch (AssertionError e) {
            instantAssertion.assertEndsWith(e.getMessage(), "not found", e.getClass().toString());
        }
    }

    @Test
    public void test_GuiElement_screenshot() {
        IImagePropertyAssertion screenshot = page.notVisibleElement().screenshot();
        screenshot.file().exists().isTrue();
    }

    @Test
    public void test_NonExistent_GuiElement_screenshot_fails() {
        try {
            IImagePropertyAssertion screenshot = page.nonExistentElement().screenshot();
            screenshot.file().exists().isTrue();
        } catch (AssertionError e) {
            instantAssertion.assertEndsWith(e.getMessage(), "not found", e.getClass().toString());
        }
    }

    public void test_component() {
        page.header().screenshot().file().bytes().lowerThan(10);
    }

    public void test_GuiElement() {
        FluentTestPage page = pageFactory.create(FluentTestPage.class);
        page.call("https://www.google.de");
        // Expect GuiElement(By.id("11")).value(attribute: value) [Hausmaus] contains not [maus]

        nonFunctionalAssertions(() -> {
            page.input().sendKeys("affe").text().contains("affe").containsNot("maus");
        });

        // Expect GuiElement(By.id("11")).text.length [20] lower than 5
        page.input().text().length().lowerThan(5);


        // Expect GuiElement(By.qa("action/submit")).visible(complete: false) [false] is true
        page.submit().scrollTo().visible(false).isTrue();
        // Expect GuiElement(By.qa("action/submit")).value(attribute: style) [display:none] equals [display:block]
        page.submit().value(Attribute.STYLE).is("display:block");

        final IImagePropertyAssertion screenshot = page.submit().screenshot();
        // Expect GuiElement(By.qa("action/submit")).screenshot.pixelDistance(referenceImageName: "SubmitButton") [10] between [3] and [5]
        screenshot.pixelDistance("SubmitButton").between(3,5);
        // Expect GuiElement(By.qa("action/submit")).screenshot.file.name [SomeImage] contains [screenshot]
        screenshot.file().name().contains("screenshot");
        // Expect GuiElement(By.qa("action/submit")).screenshot.file.extension [jpg] equals [png]
        screenshot.file().extension().is("png");
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
        page.input().nonFunctionalAsserts().assertAttributeContains("style", "affe");
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
