package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import org.testng.annotations.Test;

public class DependsOnTests2 extends AbstractWebDriverTest {

    @Test(dependsOnGroups = "passed")
    public void testDependsOnGroupsInOtherClass() {

    }

}
