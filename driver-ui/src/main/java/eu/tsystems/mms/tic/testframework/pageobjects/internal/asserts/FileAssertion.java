package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.io.File;

public class FileAssertion extends AbstractAssertion<File> implements IFileAssertion {

    public FileAssertion(AssertionProvider<File> provider) {
        super(provider);
    }

    @Override
    public IQuantifiedAssertion<Long> size() {
        return null;
    }

    @Override
    public IValueAssertion<String> name() {
        return null;
    }

    @Override
    public IValueAssertion<String> extension() {
        return null;
    }

    @Override
    public IValueAssertion<String> mimetype() {
        return null;
    }

    @Override
    public IBinaryAssertion<Boolean> exists() {
        return new BinaryAssertion(new AssertionProvider<Boolean>(this) {
            @Override
            public Boolean actual() {
                return provider.actual().exists();
            }

            @Override
            public String subject() {
                return "exists";
            }
        });
    }
}
