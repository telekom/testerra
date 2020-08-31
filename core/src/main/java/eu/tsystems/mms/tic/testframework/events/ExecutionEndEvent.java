package eu.tsystems.mms.tic.testframework.events;

import java.util.List;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

public class ExecutionEndEvent {
    public interface Listener {
        void onExecutionEnd(ExecutionEndEvent event);
    }
    private List<XmlSuite> xmlSuites;
    private List<ISuite> suites;

    public List<XmlSuite> getXmlSuites() {
        return xmlSuites;
    }

    public ExecutionEndEvent setXmlSuites(List<XmlSuite> xmlSuites) {
        this.xmlSuites = xmlSuites;
        return this;
    }

    public List<ISuite> getSuites() {
        return suites;
    }

    public ExecutionEndEvent setSuites(List<ISuite> suites) {
        this.suites = suites;
        return this;
    }
}
