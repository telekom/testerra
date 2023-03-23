package io.testerra.report.test.pretest_status.classContext.pack1;

import eu.tsystems.mms.tic.testframework.annotations.TestClassContext;
import io.testerra.report.test.AbstractTestSitesTest;
import org.testng.annotations.Test;

/**
 * Created on 2023-03-22
 *
 * @author mgn
 */
@TestClassContext(name = "ClassContextSameClassPack1")
public class GenerateClassContextSameNameTest extends AbstractTestSitesTest {
    @Test
    public void testTestMethodInSameClassesPack1() {
    }
}
