package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileAssertion extends AbstractAssertion<File> implements IFileAssertion {

    public FileAssertion(AssertionProvider<File> provider) {
        super(provider);
    }

    @Override
    public IQuantifiedAssertion<Long> bytes() {
        return new QuantifiedAssertion<>(new AssertionProvider<Long>(this) {
            @Override
            public Long actual() {
                return provider.actual().length();
            }

            @Override
            public Object subject() {
                return "bytes";
            }
        });
    }

    @Override
    public IValueAssertion<String> name() {
        return new ValueAssertion<>(new AssertionProvider<String>(this) {
            @Override
            public String actual() {
                return provider.actual().getName();
            }

            @Override
            public Object subject() {
                return "name";
            }
        });
    }

    @Override
    public IValueAssertion<String> extension() {
        return new ValueAssertion<>(new AssertionProvider<String>(this) {
            @Override
            public String actual() {
                return FilenameUtils.getExtension(provider.actual().getName());
            }

            @Override
            public Object subject() {
                return "extension";
            }
        });
    }

    @Override
    public IValueAssertion<String> mimetype() {
        return new ValueAssertion<>(new AssertionProvider<String>(this) {
            @Override
            public String actual() {
                try {
                    return Files.probeContentType(provider.actual().toPath());
                } catch (IOException e) {
                    return e.getMessage();
                }
            }

            @Override
            public Object subject() {
                return "mimetype";
            }
        });
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
