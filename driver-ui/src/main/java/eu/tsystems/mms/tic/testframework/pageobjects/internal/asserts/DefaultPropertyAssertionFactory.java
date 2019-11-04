package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;

import java.io.File;

public class DefaultPropertyAssertionFactory implements PropertyAssertionFactory {

    @Override
    public <T> IBinaryPropertyAssertion<T> binary(PropertyAssertion parent, AssertionProvider<T> provider) {
        return new BinaryPropertyAssertion(parent, provider);
    }

    @Override
    public <T> IQuantifiedPropertyAssertion<T> quantified(PropertyAssertion parent, AssertionProvider<T> provider) {
        return new QuantifiedPropertyAssertion(parent, provider);
    }

    @Override
    public <T> IStringPropertyAssertion<T> string(PropertyAssertion parent, AssertionProvider<T> provider) {
        return new StringPropertyAssertion(parent, provider);
    }

    @Override
    public IFileAssertion file(PropertyAssertion parent, AssertionProvider<File> provider) {
        return new FileAssertion(parent, provider);
    }

    @Override
    public IImageAssertion image(PropertyAssertion parent, AssertionProvider<File> provider) {
        return new ImageAssertion(parent, provider);
    }

    @Override
    public IScreenshotAssertion screenshot(PropertyAssertion parent, AssertionProvider<Screenshot> provider) {
        return new ScreenshotAssertion(parent, provider);
    }
}
