package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.io.File;

public interface IFileAssertion extends IAssertion<File> {
    IQuantifiedAssertion<Long> bytes();
    IValueAssertion<String> name();
    IValueAssertion<String> extension();
    IValueAssertion<String> mimetype();
    IBinaryAssertion<Boolean> exists();
}
