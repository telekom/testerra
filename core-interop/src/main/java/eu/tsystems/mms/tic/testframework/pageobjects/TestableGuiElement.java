package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.BinaryAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.StringAssertion;

public interface TestableGuiElement<SELF> extends BasicGuiElement<SELF> {
    StringAssertion<String> text();
    StringAssertion<String> value();
    StringAssertion<String> value(Attribute attribute);
    StringAssertion<String> value(String attribute);
    StringAssertion<String> css(String property);
    BinaryAssertion<Boolean> enabled();
    BinaryAssertion<Boolean> selected();
}
