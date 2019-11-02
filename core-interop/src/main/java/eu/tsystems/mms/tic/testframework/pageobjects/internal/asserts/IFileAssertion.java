package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.io.File;

public interface IFileAssertion extends ActualProperty<File> {
    IQuantifiedPropertyAssertion<Long> bytes();
    IStringPropertyAssertion<String> name();
    IStringPropertyAssertion<String> extension();
    IStringPropertyAssertion<String> mimetype();
    IBinaryPropertyAssertion<Boolean> exists();
}
