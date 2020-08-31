package eu.tsystems.mms.tic.testframework.events;

import eu.tsystems.mms.tic.testframework.report.ReportingData;

public class GenerateReportEvent {
    public interface Listener {
        void onGenerateReport(GenerateReportEvent event);
    }

    public ReportingData getReportingData() {
        return reportingData;
    }

    public GenerateReportEvent setReportingData(ReportingData reportingData) {
        this.reportingData = reportingData;
        return this;
    }

    private ReportingData reportingData;

}
