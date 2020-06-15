package eu.tsystems.mms.tic.testframework.report.model.steps;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.pageobjects.MyVariables;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestStepTest extends AbstractTestSitesTest {

    public WebTestPage getPage() {
        return PageFactory.create(WebTestPage.class, WebDriverManager.getWebDriver(), new MyVariables(1));
    }

    @BeforeTest
    private void beforeTest() {

    }

    @BeforeClass
    private void beforeClass() {

    }

    @AfterTest
    private void afterTest() {

    }

    @AfterClass
    private void afterClass() {

    }

    @Test
    public void test_Steps() {
        WebTestPage page = getPage();
        TestStep.begin("Explicit test step");
        page.assertFunctionalityOfButton1();
    }
}
