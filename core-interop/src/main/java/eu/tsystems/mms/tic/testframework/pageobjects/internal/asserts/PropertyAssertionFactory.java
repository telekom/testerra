package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.io.File;

public interface PropertyAssertionFactory {
    <T> IBinaryPropertyAssertion<T> binary(AssertionProvider<T> provider);
    <T> IQuantifiedPropertyAssertion<T> quantified(AssertionProvider<T> provider);
    <T> IStringPropertyAssertion<T> string(AssertionProvider<T> provider);
    IFilePropertyAssertion file(AssertionProvider<File> provider);
    IImagePropertyAssertion image(AssertionProvider<File> provider);
}
