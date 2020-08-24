package eu.tsystems.mms.tic.testframework.listener;

import eu.tsystems.mms.tic.testframework.events.ITesterraEventDataType;
import eu.tsystems.mms.tic.testframework.events.TesterraEvent;
import eu.tsystems.mms.tic.testframework.events.TesterraEventDataType;
import eu.tsystems.mms.tic.testframework.events.TesterraEventListener;
import eu.tsystems.mms.tic.testframework.events.TesterraEventType;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.external.junit.JUnitXMLReporter;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.utils.ReportUtils;
import java.util.List;
import java.util.Map;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;
import eu.tsystems.mms.tic.testframework.utils.GenerateReport;

public class GenerateReportListener implements TesterraEventListener, Loggable {

    @Override
    public void fireEvent(final TesterraEvent testerraEvent) {

        if (testerraEvent.getTesterraEventType() == TesterraEventType.GENERATE_REPORT) {

            final Map<ITesterraEventDataType, Object> eventData = testerraEvent.getData();

            // normal event after suite completion
            if (eventData.containsKey(TesterraEventDataType.XML_SUITES)) {

                final List<XmlSuite> xmlSuites = (List<XmlSuite>) eventData.get(TesterraEventDataType.XML_SUITES);
                final List<ISuite> suites = (List<ISuite>) eventData.get(TesterraEventDataType.SUITES);
                final String outputDirectory = (String) eventData.get(TesterraEventDataType.OUTPUT_DIR);
                final JUnitXMLReporter junitXmlReporter = (JUnitXMLReporter) eventData.get(TesterraEventDataType.XML_REPORTER);

                GenerateReport.runOnce(xmlSuites, suites, outputDirectory, junitXmlReporter);

                // empty event after suite failure --> GenerateTesterraReportWorker will not be reached
            } else {
                ReportUtils.generateReportEssentials();
            }
        }

        if (testerraEvent.getTesterraEventType() == TesterraEventType.GENERATE_METHOD_REPORT) {

            // generate html
            try {
                final Map<ITesterraEventDataType, Object> eventData = testerraEvent.getData();
                final MethodContext methodContext = (MethodContext) eventData.get(TesterraEventDataType.CONTEXT);
                ReportUtils.createMethodDetailsStepsView(methodContext);
            } catch (Throwable e) {
                log().error("FATAL: Could not create html", e);
            }
        }
    }
}
