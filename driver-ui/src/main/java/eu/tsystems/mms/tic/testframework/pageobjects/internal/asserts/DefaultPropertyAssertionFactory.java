package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

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
    public IFilePropertyAssertion file(PropertyAssertion parent, AssertionProvider<File> provider) {
        return new FilePropertyAssertion(parent, provider);
    }

    @Override
    public IImagePropertyAssertion image(PropertyAssertion parent, AssertionProvider<File> provider) {
        return new ImagePropertyAssertion(parent, provider);
    }
}
