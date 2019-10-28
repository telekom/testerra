package eu.tsystems.mms.tic.testframework.internal;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.model.AssertionInfo;

import java.util.List;

public class EmptyAssertionsCollector implements AssertionsCollector, Loggable {

    @Override
    public boolean store(Throwable throwable) {
       return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean hasEntries() {
        return false;
    }

    @Override
    public List<AssertionInfo> getEntries() {
        return null;
    }
}
