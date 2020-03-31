package eu.tsystems.mms.tic.testframework.report.model;

import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractResultTableFailureEntry;

import java.util.ArrayList;
import java.util.List;

public class ExitPointEntry extends AbstractResultTableFailureEntry {

    private static final String TITLE_LABEL_NOT_EXPLICIT = "No explicit exit points due to collected assertions";
    private static final String TITLE_PATTERN = "%s (%d Tests)";

    private boolean explicitness;


    // Explicit means, that the Exit Point appears as a single entry. Otherwise it is sum up in a general Exit Point
    // TODO: Could be removed, since all Exit Points are explicit
    public boolean isExplicit() {
        return explicitness;
    }

    public void setExplicitness(boolean isExplicit) {
        this.explicitness = isExplicit;
    }

    public ExitPointEntry(TestResultHelper.TestResultFailurePointEntryType failurePointEntryType, int entryNumber, int numberOfTests, String description, boolean isExplicit, List<String> methodDetailsPaths, List<String> methodDetailsAssertions) {
        super(ResultTableFailureType.EXIT_POINT, failurePointEntryType, entryNumber, numberOfTests, description, methodDetailsPaths, methodDetailsAssertions);
        this.explicitness = isExplicit;
        if (!isExplicit()) {
            changeTitle();
        }
    }

    public ExitPointEntry(TestResultHelper.TestResultFailurePointEntryType failurePointEntryType, int entryNumber, int numberOfTests, String description, boolean isExplicit) {
        this(failurePointEntryType, entryNumber, numberOfTests, description, isExplicit, new ArrayList<>(), new ArrayList<>());
    }

    protected void changeTitle() {
        setTitle(String.format(TITLE_PATTERN, TITLE_LABEL_NOT_EXPLICIT, getNumberOfTests()));
    }
}
