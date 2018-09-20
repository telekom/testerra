package eu.tsystems.mms.tic.testframework.report.model;

import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractTestReportNumbers;

/**
 * Created by fakr on 13.11.2017
 */
public class TestReportNineNumbers extends AbstractTestReportNumbers {

    public TestReportNineNumbers() {
        all = 10;
        allPassed = 1;
        passed = 1;
        allFailed = 1;
        failed = 1;
        allSkipped = 8;
        skipped = 8;
    }
}
