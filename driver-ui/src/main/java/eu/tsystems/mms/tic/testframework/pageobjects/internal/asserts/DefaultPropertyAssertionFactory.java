package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.io.File;

public class DefaultPropertyAssertionFactory implements PropertyAssertionFactory {

    @Override
    public <T> IBinaryPropertyAssertion<T> binary(AssertionProvider<T> provider) {
        return new BinaryPropertyAssertion<>(provider);
    }

    @Override
    public <T> IQuantifiedPropertyAssertion<T> quantified(AssertionProvider<T> provider) {
        return new QuantifiedPropertyAssertion(provider);
    }

    @Override
    public <T> IStringPropertyAssertion<T> string(AssertionProvider<T> provider) {
        return new StringPropertyAssertion<>(provider);
    }

    @Override
    public IFilePropertyAssertion file(AssertionProvider<File> provider) {
        return new FilePropertyAssertion(provider);
    }

    @Override
    public IImagePropertyAssertion image(AssertionProvider<File> provider) {
        return new ImagePropertyAssertion(provider);
    }
}
