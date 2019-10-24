package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IAssertableValue;

public interface IPage extends IWebDriverRetainer {
    IAssertableValue title();
    IAssertableValue url();
    IPage navigateTo(final String to);
}
