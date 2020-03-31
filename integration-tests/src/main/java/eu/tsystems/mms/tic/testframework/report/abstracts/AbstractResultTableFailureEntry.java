package eu.tsystems.mms.tic.testframework.report.abstracts;

import eu.tsystems.mms.tic.testframework.report.model.ResultTableFailureType;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fakr on 15.08.2017.
 * Represents the model for a single Failure Point Entry on a Failure Points Page
 *
 */
public abstract class AbstractResultTableFailureEntry {

    private final String DEFAULT_TITLE_PATTERN = "%s #%d (%d Tests)";

    private ResultTableFailureType resultTableFailureType;
    private TestResultHelper.TestResultFailurePointEntryType failurePointEntryType;
    private String title;
    private int entryNumber;
    private int numberOfTests;
    private String description;

    private List<String> methodDetailPaths;
    private List<String> methodDetailAssertions;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getEntryNumber() {
        return entryNumber;
    }

    public void setEntryNumber(int entryNumber) {
        this.entryNumber = entryNumber;
    }

    public int getNumberOfTests() {
        return numberOfTests;
    }

    public void setNumberOfTests(int numberOfTests) {
        this.numberOfTests = numberOfTests;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ResultTableFailureType getResultTableFailureType() {
        return resultTableFailureType;
    }

    public void setResultTableFailureType(ResultTableFailureType resultTableFailureType) {
        this.resultTableFailureType = resultTableFailureType;
    }

    public TestResultHelper.TestResultFailurePointEntryType getFailurePointEntryType() {
        return failurePointEntryType;
    }

    public void setFailurePointEntryType(TestResultHelper.TestResultFailurePointEntryType failurePointEntryType) {
        this.failurePointEntryType = failurePointEntryType;
    }

    public List<String> getMethodDetailPaths() {
        return methodDetailPaths;
    }

    public void setMethodDetailPaths(List<String> methodDetailPaths) {
        this.methodDetailPaths = methodDetailPaths;
    }

    public List<String> getMethodDetailAssertions() {
        return methodDetailAssertions;
    }

    public void setMethodDetailAssertions(List<String> methodDetailAssertions) {
        this.methodDetailAssertions = methodDetailAssertions;
    }

    public List<String> getMethodDetailPathSimpleMethodNames() {
        List<String> pathSimpleNames = new ArrayList<>();
        for (int index = 0; index < getMethodDetailPaths().size(); index++) {

            String shortTestMethodName;
            String currentTestMethodName = getMethodDetailPaths().get(index);

            if (currentTestMethodName.contains("in Report- TestsUnderTest")) {

                String bufferTestMethod = "t" + getMethodDetailPaths().get(index).split(" - t")[getMethodDetailPaths().get(index).split(" - t").length - 1];
                shortTestMethodName = bufferTestMethod.split(" \\(in ")[getMethodDetailPaths().get(index).split(" \\(in ").length - 2];

            } else {

                shortTestMethodName = getMethodDetailPaths().get(index).split(" - ")[getMethodDetailPaths().get(index).split(" - ").length - 1];
            }

            pathSimpleNames.add(shortTestMethodName);
        }

        return pathSimpleNames;
    }

    public AbstractResultTableFailureEntry(ResultTableFailureType failureType, TestResultHelper.TestResultFailurePointEntryType failurePointEntryType, int entryNumber, int numberOfTests, String description, List<String> methodDetailPath, List<String> methodDetailAssertion) {

        this.resultTableFailureType = failureType;
        this.failurePointEntryType = failurePointEntryType;
        this.entryNumber = entryNumber;
        this.numberOfTests = numberOfTests;
        this.description = description;
        this.methodDetailPaths = methodDetailPath;
        this.methodDetailAssertions = methodDetailAssertion;
        this.title = String.format(DEFAULT_TITLE_PATTERN, resultTableFailureType.getLabel(), entryNumber, numberOfTests);
    }

    public AbstractResultTableFailureEntry(ResultTableFailureType failureType, TestResultHelper.TestResultFailurePointEntryType failurePointEntryType, int entryNumber, int numberOfTests, String description) {
        this(failureType, failurePointEntryType, entryNumber, numberOfTests, description, new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!AbstractResultTableFailureEntry.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final AbstractResultTableFailureEntry other = (AbstractResultTableFailureEntry) obj;
        if (this.resultTableFailureType != other.resultTableFailureType) {
            return false;
        }
        if (this.failurePointEntryType != other.failurePointEntryType) {
            return false;
        }
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
        if (this.entryNumber != other.entryNumber) {
            return false;
        }
        if (this.numberOfTests != other.numberOfTests) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        return true;
    }
}
