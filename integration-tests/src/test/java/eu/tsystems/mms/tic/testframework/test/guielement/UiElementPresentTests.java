package eu.tsystems.mms.tic.testframework.test.guielement;

import eu.tsystems.mms.tic.testframework.AbstractExclusiveTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import org.testng.annotations.Test;

/**
 * Created on 04.05.2022
 *
 * @author mgn
 */
public class UiElementPresentTests extends AbstractExclusiveTestSitesTest<WebTestPage> implements AssertProvider {

    @Override
    public Class<WebTestPage> getPageClass() {
        return WebTestPage.class;
    }

    @Test
    public void test_inexistent_UiElement_present() {
        WebTestPage page = getPage();
        page.inexistentElement().expect().present().is(false);
    }

    @Test
    public void test_inexistent_UiElement_present_fails() {
        WebTestPage page = getPage();
        String msg = null;
        try {
            page.inexistentElement().expect().present().is(true);
        } catch (AssertionError e) {
            msg = e.getMessage();
        }
        ASSERT.assertEndsWith(msg, "is true", AssertionError.class.toString());
    }

    @Test
    public void test_inexistent_UiElement_present_fails_fast() {
        CONTROL.withTimeout(0, this::test_inexistent_UiElement_present_fails);
    }


}
