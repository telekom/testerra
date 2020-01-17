package eu.tsystems.mms.tic.testframework.core.playground;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import org.testng.annotations.Test;

public class SimpleFailingTest extends AbstractWebDriverTest {

    @Test
    public void testFailing() {
        Assert.assertEquals(1,2);
    }

}
