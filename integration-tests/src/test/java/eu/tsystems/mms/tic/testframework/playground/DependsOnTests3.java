package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

public class DependsOnTests3 extends AbstractTest {


    @BeforeClass(groups = "group1")
    public void beforeGroupClass() {

    }


    @Test(groups = "group1")
    public void test1Group1() {

    }

    @Test(groups = "group1")
    public void test2Group1() {

    }

    @Test(groups = "group2")
    public void test1Group2() {

    }

}
