package eu.tsystems.mms.tic.testframework.report.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fakr on 16.08.2017
 */
public class TestReportTwoExitPoints implements IFailurePointEntryHelper {

    public static final int NUMBER_OF_EXIT_POINTS = 32;

    public static final ExitPointEntry EP1 = new ExitPointEntry(
            TestResultHelper.TestResultFailurePointEntryType.FAILED,
            1,
            7,
            "ExitPointCreaterTestClass1.testCreatorForDifferentExitPoints(ExitPointCreaterTestClass1.java:12)",
            true
    );

    public static final ExitPointEntry EP2 = new ExitPointEntry(
            TestResultHelper.TestResultFailurePointEntryType.FAILED,
            2,
            3,
            "ExitPointCreatorTestClass2.testCreatorForDifferentExitPoints2();",
            true,
            Arrays.asList("ReportTestUnderTestCorridorLow - test_testLowCorridorFailed3 (in Report- TestsUnderTest - Low-Corridor Creator)",
                    "ReportTestUnderTestCorridorLow - test_testLowCorridorFailed4 (in Report- TestsUnderTest - Low-Corridor Creator)",
                    "ReportTestUnderTestCorridorLow - test_testLowCorridorFailed5 (in Report- TestsUnderTest - Low-Corridor Creator)"),
            Arrays.asList("ArithmeticException: / by zero",
                    "ArithmeticException: / by zero",
                    "ArithmeticException: / by zero")
    );

    public static List<ExitPointEntry> getAllExitPointEntryTestObjects() {
        List testObjects = new ArrayList<ExitPointEntry>() {};
        testObjects.addAll(Arrays.asList(EP1, EP2));
        for (int index = 3; index <= NUMBER_OF_EXIT_POINTS; index++) {
            final ExitPointEntry furtherExitPoint = new ExitPointEntry(
                    TestResultHelper.TestResultFailurePointEntryType.FAILED,
                    index,
                    1,
                    "",
                    true);
            testObjects.add(furtherExitPoint);
        }
        return testObjects;
    }


}
