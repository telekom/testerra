package eu.tsystems.mms.tic.testframework.test.guielement;

import eu.tsystems.mms.tic.testframework.AbstractExclusiveTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.UiElementShadowRoot2Page;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.exceptions.UiElementAssertionError;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

/**
 * Created on 2023-12-05
 *
 * @author mgn
 */
public class UiElementShadowRoot2Test extends AbstractExclusiveTestSitesTest<UiElementShadowRoot2Page> {

    @Override
    protected TestPage getTestPage() {
        return TestPage.SHADOW_ROOT3;
    }

    @Override
    public Class<UiElementShadowRoot2Page> getPageClass() {
        return UiElementShadowRoot2Page.class;
    }

    @Test
    public void testT01_ShadowRoot2_visibility() {
        UiElementShadowRoot2Page page = getPage();

        page.text1.assertThat().displayed(true);
        page.text1.assertThat().present(true);
        page.text1.assertThat().text("Text as shadow content");
    }

    @Test
    public void testT02_ShadowRoot2_TextInput() {
        final String string = "Text in shadowed input";
        UiElementShadowRoot2Page page = getPage();

        page.textInput.type(string);
        page.textInput.assertThat().value().is(string);
    }

    @Test
    public void testT03_ShadowRoot2_Nested_ShadowRoot() {
        UiElementShadowRoot2Page page = getPage();

        page.nestedShadowContent.assertThat().present(true);
        page.text2.assertThat().displayed(true);
        page.text2.assertThat().text("Text in nested shadow dom");
    }

    @Test(expectedExceptions = UiElementAssertionError.class)
    public void testT04_ShadowRoot2_WrongSelector() {
        UiElementShadowRoot2Page page = getPage();

        UiElement text1 = page.shadowRootElement1.find(By.xpath("//*[@id = 'shadow_content']"));
        text1.assertThat().displayed(true);
    }


}
