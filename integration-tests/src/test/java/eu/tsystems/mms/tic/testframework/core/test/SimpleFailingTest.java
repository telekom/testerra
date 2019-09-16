package eu.tsystems.mms.tic.testframework.core.test;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SimpleFailingTest extends AbstractTest {

    @Test
    public void testFailing() {
        Assert.assertEquals(1,2);
    }

}
