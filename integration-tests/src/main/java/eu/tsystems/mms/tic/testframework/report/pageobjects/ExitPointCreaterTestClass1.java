package eu.tsystems.mms.tic.testframework.report.pageobjects;

import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;

/**
 * Created by riwa on 04.04.2017.
 */
public class ExitPointCreaterTestClass1 {

    public static void testCreatorForDifferentExitPoints() throws Exception {
        NonFunctionalAssert.assertTrue(2>3);
        throw new Exception();
    }
}
