package eu.tsystems.mms.tic.testframework.report.testundertest;

import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * Created by jlma on 05.12.2016.
 */
public class ReportTestUnderTestSkipped extends AbstractTest {


    @Test
    public void test_TestStateSkipped1() {
        TestStep.begin("Test-Step-1");
        TestStep.begin("Test-Step-2");
        TestStep.begin("Test-Step-3");
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkipped2() { throw new SkipException("Skipped because of Skip Exception");}
    @Test
    public void test_TestStateSkipped3() {
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkipped4() {
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkipped5() {
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkipped6() {
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkipped7() {
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkipped8() {
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkipped9() {throw new SkipException("Skipped because of Skip Exception");}
    @Test
    public void test_TestStateSkippedInherited1() {
        TestStep.begin("Test-Step-1");
        TestStep.begin("Test-Step-2");
        TestStep.begin("Test-Step-3");
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkippedInherited2() {
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkippedInherited3() {
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkippedInherited4() {
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkippedInherited5() {
        throw new SkipException("Skipped because of Skip Exception");
    }



}
