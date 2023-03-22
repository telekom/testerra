package io.testerra.report.test.pretest_status.classContext;

import eu.tsystems.mms.tic.testframework.annotations.TestClassContext;
import org.testng.annotations.Test;

/**
 * Created on 2023-03-21
 *
 * @author mgn
 */
@TestClassContext(name = "ClassContextInheritedClasses")
public class GenerateClassContextChildClassTest extends GenerateClassContextParentClassTest {

    @Test
    public void testTestMethodInChildClasses() {

    }

}
