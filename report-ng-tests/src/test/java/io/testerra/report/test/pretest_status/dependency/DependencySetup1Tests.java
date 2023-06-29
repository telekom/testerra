package io.testerra.report.test.pretest_status.dependency;

import io.testerra.report.test.AbstractTestSitesTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Created on 2023-05-11
 *
 * @author mgn
 */
public class DependencySetup1Tests extends AbstractTestSitesTest {

    @BeforeTest(groups = {Groups.EXT})
    public void beforeTest() {

    }

    @BeforeClass(groups = {Groups.EXT})
    public void beforeClass() {

    }

    @BeforeMethod(groups = {Groups.EXT})
    public void beforeTestMethod() {

    }

    @Test(groups = {Groups.EXT})
    public void test_BeforeAfterMethodTest() {

    }

    @AfterMethod(groups = {Groups.EXT})
    public void afterTestMethod() {

    }

    @AfterClass(groups = {Groups.EXT})
    public void afterClass() {

    }

    @AfterTest(groups = {Groups.EXT})
    public void afterTest() {

    }

}
