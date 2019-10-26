package eu.tsystems.mms.tic.testframework.execution.testng;

import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;

public class DefaultFunctionalAssertFactory implements FunctionalAssertFactory {
    @Override
    public IAssert create() {
        if (TesterraListener.isActive() || Flags.GUIELEMENT_DEFAULT_ASSERT_IS_COLLECTOR) {
            return new PlatformAssert();
        } else {
            return new ExceptionAssert();
        }
    }

    @Override
    public IAssert create(final boolean collectable) {
        if (!collectable) {
            return new ExceptionAssert();
        }
        return create();
    }
}
