package eu.tsystems.mms.tic.testframework.listener;

import eu.tsystems.mms.tic.testframework.events.ITesterraEventDataType;
import eu.tsystems.mms.tic.testframework.events.TesterraEvent;
import eu.tsystems.mms.tic.testframework.events.TesterraEventDataType;
import eu.tsystems.mms.tic.testframework.events.TesterraEventListener;
import eu.tsystems.mms.tic.testframework.events.TesterraEventType;
import eu.tsystems.mms.tic.testframework.report.external.junit.JUnitXMLReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;
import report.utils.GenerateReport;
import report.utils.ReportUtilsA;

import java.util.List;
import java.util.Map;

public class GenerateReportListener implements TesterraEventListener {

    @Override
    public void fireEvent(final TesterraEvent testerraEvent) {

        if (testerraEvent.getTesterraEventType() == TesterraEventType.GENERATE_REPORT) {
            final Map<ITesterraEventDataType, Object> eventData = testerraEvent.getData();

            // normal event after suite completion
            if  (eventData.containsKey(TesterraEventDataType.XML_SUITES)) {

                final List<XmlSuite> o = (List<XmlSuite>)eventData.get(TesterraEventDataType.XML_SUITES);
                final List<ISuite> o1 = (List<ISuite>)eventData.get(TesterraEventDataType.SUITES);
                final String o2 = (String) eventData.get(TesterraEventDataType.OUTPUT_DIR);
                final JUnitXMLReporter o3 = (JUnitXMLReporter) eventData.get(TesterraEventDataType.XML_REPORTER);

                GenerateReport.runOnce(o, o1, o2, o3);

                // empty event after suite failure --> GenerateTesterraReportWorker will not be reached
            } else {
                ReportUtilsA.generateReportEssentials();
            }
        }
    }
}
