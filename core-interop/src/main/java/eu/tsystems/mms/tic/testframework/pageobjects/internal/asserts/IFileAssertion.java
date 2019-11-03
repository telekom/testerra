package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.io.File;

/**
 * Allows local file based assertions
 * @author Mike Reiche
 */
public interface IFileAssertion extends ActualProperty<File> {
    IQuantifiedPropertyAssertion<Long> bytes();
    IStringPropertyAssertion<String> name();
    IStringPropertyAssertion<String> extension();
    IStringPropertyAssertion<String> mimetype();
    IBinaryPropertyAssertion<Boolean> exists();
}
