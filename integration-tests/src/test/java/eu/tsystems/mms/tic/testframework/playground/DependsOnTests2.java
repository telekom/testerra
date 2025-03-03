package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import org.testng.annotations.Test;

public class DependsOnTests2 extends AbstractWebDriverTest {

    // Can only be executed with class 'DependsOnTests'

    @Test(dependsOnGroups = "passed")
    public void testDependsOnGroupsInOtherClass() {

    }

}
