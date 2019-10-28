package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FilePropertyAssertion extends AbstractPropertyAssertion<File> implements IFilePropertyAssertion {

    public FilePropertyAssertion(AssertionProvider<File> provider) {
        super(provider);
    }

    @Override
    public IQuantifiedPropertyAssertion<Long> bytes() {
        return propertyAssertionFactory.quantified(new AssertionProvider<Long>(this) {
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
    public IStringPropertyAssertion<String> name() {
        return propertyAssertionFactory.string(new AssertionProvider<String>(this) {
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
    public IStringPropertyAssertion<String> extension() {
        return propertyAssertionFactory.string(new AssertionProvider<String>(this) {
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
    public IStringPropertyAssertion<String> mimetype() {
        return propertyAssertionFactory.string(new AssertionProvider<String>(this) {
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
    public IBinaryPropertyAssertion<Boolean> exists() {
        return propertyAssertionFactory.binary(new AssertionProvider<Boolean>(this) {
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
