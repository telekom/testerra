package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IAssertableValue;
import eu.tsystems.mms.tic.testframework.pageobjects.location.Locator;

public interface IPage extends IWebDriverRetainer, Locator {
    IAssertableValue title();
    IAssertableValue url();
    IPage navigateTo(final String to);
}
