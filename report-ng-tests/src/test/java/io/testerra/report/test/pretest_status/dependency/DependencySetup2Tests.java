package io.testerra.report.test.pretest_status.dependency;

import io.testerra.report.test.AbstractTestSitesTest;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * Created on 2023-05-12
 *
 * @author mgn
 */
public class DependencySetup2Tests extends AbstractTestSitesTest {

    @BeforeGroups(groups = {Groups.EXT2})
    public void beforeGroup() {

    }

    @Test(groups = {Groups.EXT2}, dependsOnGroups = {Groups.EXT2})
    public void test_BeforeAfterGroupTest() {

    }

    @AfterGroups(groups = {Groups.EXT2})
    public void afterGroup() {

    }

}
