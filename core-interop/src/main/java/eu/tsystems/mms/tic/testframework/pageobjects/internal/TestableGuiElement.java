package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IBinaryPropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IStringPropertyAssertion;

public interface TestableGuiElement extends BasicGuiElement {
    IStringPropertyAssertion<String> text();
    IStringPropertyAssertion<String> value();
    IStringPropertyAssertion<String> value(Attribute attribute);
    IStringPropertyAssertion<String> value(String attribute);
    IBinaryPropertyAssertion<Boolean> enabled();
    IBinaryPropertyAssertion<Boolean> selected();
}
