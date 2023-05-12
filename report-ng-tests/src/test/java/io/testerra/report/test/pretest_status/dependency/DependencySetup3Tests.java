package io.testerra.report.test.pretest_status.dependency;

import io.testerra.report.test.AbstractTestSitesTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created on 2023-05-12
 *
 * @author mgn
 */
public class DependencySetup3Tests extends AbstractTestSitesTest {

    @BeforeMethod
    public void beforeMethodForGroupTests() {

    }

    @Test(groups = {Groups.EXT3})
    public void test_TestRunningBeforeDependsOnGroupTest() {

    }

    @Test(groups = {Groups.EXT3}, dependsOnGroups = {Groups.EXT3})
    public void test_TestIsDependsOnGroupTest() {

    }

}
