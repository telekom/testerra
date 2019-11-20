package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.BinaryPropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.QuantifiedPropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.StringPropertyAssertion;

public interface TestableGuiElement extends BasicGuiElement {
    StringPropertyAssertion<String> text();
    StringPropertyAssertion<String> value();
    StringPropertyAssertion<String> value(Attribute attribute);
    StringPropertyAssertion<String> value(String attribute);
    StringPropertyAssertion<String> css(String property);
    BinaryPropertyAssertion<Boolean> enabled();
    BinaryPropertyAssertion<Boolean> selected();
    QuantifiedPropertyAssertion<Integer> numberOfElements();
    TestableGuiElement waitFor();
}
