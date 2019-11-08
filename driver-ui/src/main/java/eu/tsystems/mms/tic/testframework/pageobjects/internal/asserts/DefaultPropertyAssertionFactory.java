package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;

import java.io.File;

public class DefaultPropertyAssertionFactory implements PropertyAssertionFactory {

    @Override
    public <T> BinaryPropertyAssertion<T> binary(PropertyAssertion parent, AssertionProvider<T> provider) {
        return new DefaultBinaryPropertyAssertion(parent, provider);
    }

    @Override
    public <T> QuantifiedPropertyAssertion<T> quantified(PropertyAssertion parent, AssertionProvider<T> provider) {
        return new DefaultQuantifiedPropertyAssertion(parent, provider);
    }

    @Override
    public <T> StringPropertyAssertion<T> string(PropertyAssertion parent, AssertionProvider<T> provider) {
        return new DefaultStringPropertyAssertion(parent, provider);
    }

    @Override
    public FileAssertion file(PropertyAssertion parent, AssertionProvider<File> provider) {
        return new DefaultFileAssertion(parent, provider);
    }

    @Override
    public ImageAssertion image(PropertyAssertion parent, AssertionProvider<File> provider) {
        return new DefaultImageAssertion(parent, provider);
    }

    @Override
    public ScreenshotAssertion screenshot(PropertyAssertion parent, AssertionProvider<Screenshot> provider) {
        return new DefaultScreenshotAssertion(parent, provider);
    }
}
