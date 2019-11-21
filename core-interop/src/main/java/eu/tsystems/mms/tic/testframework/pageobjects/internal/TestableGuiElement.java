package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.BinaryAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.StringAssertion;

public interface TestableGuiElement extends BasicGuiElement {
    StringAssertion<String> text();
    StringAssertion<String> value();
    StringAssertion<String> value(Attribute attribute);
    StringAssertion<String> value(String attribute);
    StringAssertion<String> css(String property);
    BinaryAssertion<Boolean> enabled();
    BinaryAssertion<Boolean> selected();
    QuantityAssertion<Integer> numberOfElements();
    TestableGuiElement waitFor();
}
