package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

public class DependsOnTests4 extends AbstractTest {


    @Test(groups = "group1")
    public void test3Group1() {

    }

    @Test(groups = "group1")
    public void test4Group1() {

    }

    @Test(groups = "group2")
    public void test2Group2() {

    }

}
