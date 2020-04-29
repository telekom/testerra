package eu.tsystems.mms.tic.testframework.report.model;

import eu.tsystems.mms.tic.testframework.report.pageobjetcs.abstracts.AbstractTestReportNumbers;

public class TestReportEightNumbers extends AbstractTestReportNumbers {

    public TestReportEightNumbers() {
        all = 2;
        allSkipped = 2;
        skipped = 2;

        highCorridorLimit = 0;
        midCorridorLimit = 0;
        lowCorridorLimit = 0;
    }

}
