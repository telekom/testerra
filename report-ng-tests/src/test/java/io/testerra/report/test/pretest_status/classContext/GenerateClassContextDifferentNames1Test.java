package io.testerra.report.test.pretest_status.classContext;

import eu.tsystems.mms.tic.testframework.annotations.TestClassContext;
import io.testerra.report.test.AbstractTestSitesTest;
import org.testng.annotations.Test;

/**
 * Created on 2023-03-21
 *
 * @author mgn
 */
@TestClassContext(name = "ClassContextDifferentClasses")
public class GenerateClassContextDifferentNames1Test extends AbstractTestSitesTest {

    @Test
    public void testTestMethodInDifferentClasses1() {

    }

}
