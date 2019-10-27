package eu.tsystems.mms.tic.testframework.execution.testng;

import eu.tsystems.mms.tic.testframework.report.TesterraListener;

/**
 * This functional assertion is always a PlatformAssertion.
 * because the {@link TesterraListener} is always active.
 */
public class DefaultFunctionalAssertion extends PlatformAssertion implements FunctionalAssertion {
}
