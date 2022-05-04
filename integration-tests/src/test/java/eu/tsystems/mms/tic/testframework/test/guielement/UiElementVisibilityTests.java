package eu.tsystems.mms.tic.testframework.test.guielement;

import eu.tsystems.mms.tic.testframework.AbstractExclusiveTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.UiElementAssertion;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import org.testng.annotations.Test;

/**
 * Created on 04.05.2022
 *
 * @author mgn
 */
public class UiElementVisibilityTests extends AbstractExclusiveTestSitesTest<WebTestPage> implements AssertProvider {

    @Override
    public Class<WebTestPage> getPageClass() {
        return WebTestPage.class;
    }

    @Test
    public void testT01_UiElement_displayed_false() {
        WebTestPage page = getPage();
        UiElementAssertion expect = page.notDisplayedElement().expect();
        expect.attribute(Attribute.STYLE).contains("display: none").is(true);
        expect.displayed(false);
        expect.hasClasses("button").is(false);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT02_UiElement_displayed_false_fails() {
        WebTestPage page = getPage();
        page.notDisplayedElement().expect().displayed(true);
    }

    @Test()
    public void testT03_UiElement_displayed_false_fails_withMessage() {
        WebTestPage page = getPage();
        try {
            page.notDisplayedElement().expect().displayed().is(true, "Important element visibility");
        } catch (AssertionError e) {
            ASSERT.assertContains(e.getMessage(), "Important element visibility");
        }
    }

    @Test
    public void testT04_UiElement_visible_false() {
        WebTestPage page = getPage();
        page.notVisibleElement().expect().attribute(Attribute.STYLE).contains("hidden").is(true);
        page.notVisibleElement().expect().attribute("style").contains("hidden").is(true);
        page.notVisibleElement().expect().visible(true).is(false);
        page.notVisibleElement().expect().visible(false).is(false);
        page.notDisplayedElement().expect().css("display").is("none");
        page.notDisplayedElement().expect().hasClasses("mumu").is(false);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT05_UiElement_visible_false_fails() {
        WebTestPage page = getPage();
        page.notVisibleElement().expect().visible(true).is(true);
    }

    @Test()
    public void testT06_inexistent_UiElement_displayed_fails() {
        WebTestPage page = getPage();
        page.inexistentElement().expect().displayed().is(false);
    }
}
