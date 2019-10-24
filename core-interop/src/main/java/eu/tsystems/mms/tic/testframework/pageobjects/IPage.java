package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IAssertableValue;

public interface IPage extends IWebDriverRetainer {
    IAssertableValue<String, IPage> title();
    IAssertableValue<String, IPage> url();
    IPage navigateTo(final String to);
}
