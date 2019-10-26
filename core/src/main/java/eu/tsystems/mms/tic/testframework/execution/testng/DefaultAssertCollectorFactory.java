package eu.tsystems.mms.tic.testframework.execution.testng;

import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;

public class DefaultAssertCollectorFactory implements AssertCollectorFactory {
    @Override
    public IAssertCollector create() {
        if (TesterraListener.isActive() && Flags.GUIELEMENT_DEFAULT_ASSERT_IS_COLLECTOR) {
            return new PlatformAssertCollector();
        } else {
            return new ThrowAssertCollector();
        }
    }
}
