package eu.tsystems.mms.tic.testframework.test.utils;

import eu.tsystems.mms.tic.testframework.exceptions.PageFactoryException;
import org.testng.annotations.Test;

/**
 * Created on 2022-10-07
 *
 * @author mgn
 */
public class SourceCodeSnippetTests extends AbstractSourceCodeTests {

    // These tests need the following property:
    // tt.report.source.exclusion.regex=AbstractSourceCodeTests

    @Test(expectedExceptions = AssertionError.class)
    public void T01_updated_source_code_exclusion_pretest() {
        this.doSimpleFail();
    }

    @Test(dependsOnMethods = "T01_updated_source_code_exclusion_pretest")
    public void T02_updated_source_code_exclusion() {
        this.verifySourceCodeSnippet("T01_updated_source_code_exclusion_pretest", "SourceCodeSnippetTests.java", "this.doSimpleFail();");
    }

    @Test(expectedExceptions = PageFactoryException.class)
    public void T03_updated_source_code_exclusion_with_page_pretest() {
        this.doPageFactoryFail();
    }

    @Test(dependsOnMethods = "T03_updated_source_code_exclusion_with_page_pretest")
    public void T04_updated_source_code_exclusion_with_page() {
        this.verifySourceCodeSnippet("T03_updated_source_code_exclusion_with_page_pretest", "SourceCodeSnippetTests.java", "this.doPageFactoryFail();");
    }
}
