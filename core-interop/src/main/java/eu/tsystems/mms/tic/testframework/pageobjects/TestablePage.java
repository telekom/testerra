package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.ScreenshotAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.StringAssertion;

public interface TestablePage {
    TestableGuiElement anyElementContainsText(String text);
    ScreenshotAssertion screenshot();
    StringAssertion<String> title();
    StringAssertion<String> url();
    TestablePage waitFor();
}
