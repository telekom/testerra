package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.exceptions.PageFactoryException;
import eu.tsystems.mms.tic.testframework.test.utils.AbstractSourceCodeTests;
import org.testng.annotations.Test;

/**
 * Created on 2022-10-07
 *
 * @author mgn
 */
public class SourceCodeSnippetPlaygroundTests extends AbstractSourceCodeTests {

    // These tests need the following property:
    // tt.report.source.exclusion.regex=<nothing>
    @Test(expectedExceptions = AssertionError.class)
    public void T05_default_source_code_exclusion_pretest() {
        this.doSimpleFail();
    }

    @Test(dependsOnMethods = "T05_default_source_code_exclusion_pretest")
    public void T06_default_source_code_snippet() {
        this.verifySourceCodeSnippet("T05_default_source_code_exclusion_pretest", "AbstractSourceCodeTests.java", "ASSERT.fail(\"Simple failed\");");
    }

    @Test(expectedExceptions = PageFactoryException.class)
    public void T07_default_source_code_exclusion_with_page_pretest() {
        this.doPageFactoryFail();
    }

    @Test(dependsOnMethods = "T07_default_source_code_exclusion_with_page_pretest")
    public void T08_default_source_code_exclusion_with_page() {
        this.verifySourceCodeSnippet("T07_default_source_code_exclusion_with_page_pretest", "AbstractSourceCodeTests.java",
                "PAGE_FACTORY.createPage(PageWithNotExistingElement.class, this.getWebDriver());");
    }
}
