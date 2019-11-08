package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;

import java.io.File;

public interface PropertyAssertionFactory {
    <T> BinaryPropertyAssertion<T> binary(PropertyAssertion parent, AssertionProvider<T> provider);
    <T> QuantifiedPropertyAssertion<T> quantified(PropertyAssertion parent, AssertionProvider<T> provider);
    <T> StringPropertyAssertion<T> string(PropertyAssertion parent, AssertionProvider<T> provider);
    FileAssertion file(PropertyAssertion parent, AssertionProvider<File> provider);
    ImageAssertion image(PropertyAssertion parent, AssertionProvider<File> provider);
    ScreenshotAssertion screenshot(PropertyAssertion parent, AssertionProvider<Screenshot> provider);
    default <T> BinaryPropertyAssertion<T> binary(AssertionProvider<T> provider) {
        return binary(null, provider);
    }
    default <T> QuantifiedPropertyAssertion<T> quantified(AssertionProvider<T> provider) {
        return quantified(null, provider);
    }
    default <T> StringPropertyAssertion<T> string(AssertionProvider<T> provider) {
        return string(null, provider);
    }
    default FileAssertion file(AssertionProvider<File> provider) {
        return file(null, provider);
    }
    default ImageAssertion image(AssertionProvider<File> provider) {
        return image(null, provider);
    }
    default ScreenshotAssertion screenshot(AssertionProvider<Screenshot> provider) {
        return screenshot(null, provider);
    }
}
