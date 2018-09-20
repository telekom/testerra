package eu.tsystems.mms.tic.testframework.report.general;

public enum ReportDirectory {
    REPORT_DIRECTORY_1("reportDirectoryExecution1"),
    REPORT_DIRECTORY_2("reportDirectoryExecution2"),
    REPORT_DIRECTORY_3("reportDirectoryExecution3"),
    REPORT_DIRECTORY_4("reportDirectoryExecution4"),
    REPORT_DIRECTORY_5("reportDirectoryExecution5"),
    REPORT_DIRECTORY_6("reportDirectoryExecution6"),
    REPORT_DIRECTORY_7("reportDirectoryExecution7"),
    REPORT_DIRECTORY_8("reportDirectoryExecution8"),
    REPORT_DIRECTORY_9("reportDirectoryExecution9");

    private String reportDirectory;

    ReportDirectory(String reportDirectory) {
        this.reportDirectory = reportDirectory;
    }

    public String toString(){
        return reportDirectory;
    }
}
