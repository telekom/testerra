package eu.tsystems.mms.tic.testframework.internal;

import eu.tsystems.mms.tic.testframework.report.model.AssertionInfo;

import java.util.List;

public interface AssertionsCollector {
    /**
     * @return True if stored
     */
    boolean store(Throwable throwable);
    void clear();
    boolean hasEntries();
    List<AssertionInfo> getEntries();
}
