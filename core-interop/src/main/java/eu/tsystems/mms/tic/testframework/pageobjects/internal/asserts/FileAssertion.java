package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.io.File;

/**
 * Allows local file based assertions
 * @author Mike Reiche
 */
public interface FileAssertion extends ActualProperty<File> {
    QuantityAssertion<Long> bytes();
    StringAssertion<String> name();
    StringAssertion<String> extension();
    StringAssertion<String> mimetype();
    BinaryAssertion<Boolean> exists();
}
