package eu.tsystems.mms.tic.testframework.core.playground;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.annotations.*;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.RandomUtils;
import eu.tsystems.mms.tic.testframework.utils.TestUtils;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.*;

public class SimplePassingTest extends AbstractTest {

    @Test
    public void testPassing() {

    }

}
