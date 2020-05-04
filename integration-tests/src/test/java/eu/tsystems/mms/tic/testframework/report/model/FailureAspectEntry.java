package eu.tsystems.mms.tic.testframework.report.model;


import eu.tsystems.mms.tic.testframework.report.pageobjetcs.abstracts.AbstractResultTableFailureEntry;

import java.util.List;

public class FailureAspectEntry extends AbstractResultTableFailureEntry {

    public FailureAspectEntry(TestResultHelper.TestResultFailurePointEntryType failurePointEntryType, int entryNumber, int numberOfTests, String description) {
        super(ResultTableFailureType.FAILURE_ASPECT, failurePointEntryType, entryNumber, numberOfTests, description);
    }

    public FailureAspectEntry(TestResultHelper.TestResultFailurePointEntryType failurePointEntryType, int entryNumber, int numberOfTests, String description, List<String> methodDetailsPaths, List<String> methodDetailsAssertions) {
        super(ResultTableFailureType.FAILURE_ASPECT, failurePointEntryType, entryNumber, numberOfTests, description, methodDetailsPaths, methodDetailsAssertions);
    }
}
