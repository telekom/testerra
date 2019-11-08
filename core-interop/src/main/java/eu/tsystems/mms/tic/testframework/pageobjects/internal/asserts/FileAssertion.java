package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.io.File;

/**
 * Allows local file based assertions
 * @author Mike Reiche
 */
public interface FileAssertion extends ActualProperty<File> {
    QuantifiedPropertyAssertion<Long> bytes();
    StringPropertyAssertion<String> name();
    StringPropertyAssertion<String> extension();
    StringPropertyAssertion<String> mimetype();
    BinaryPropertyAssertion<Boolean> exists();
}
