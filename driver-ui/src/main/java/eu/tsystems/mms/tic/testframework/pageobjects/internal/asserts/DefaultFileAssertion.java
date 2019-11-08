package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DefaultFileAssertion extends AbstractPropertyAssertion<File> implements FileAssertion {

    public DefaultFileAssertion(PropertyAssertion parentAssertion, AssertionProvider<File> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public QuantifiedPropertyAssertion<Long> bytes() {
        return propertyAssertionFactory.quantified(this, new AssertionProvider<Long>() {
            @Override
            public Long getActual() {
                return provider.getActual().length();
            }

            @Override
            public String getSubject() {
                return "bytes";
            }
        });
    }

    @Override
    public StringPropertyAssertion<String> name() {
        return propertyAssertionFactory.string(this, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return provider.getActual().getName();
            }

            @Override
            public String getSubject() {
                return "name";
            }
        });
    }

    @Override
    public StringPropertyAssertion<String> extension() {
        return propertyAssertionFactory.string(this, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return FilenameUtils.getExtension(provider.getActual().getName());
            }

            @Override
            public String getSubject() {
                return "extension";
            }
        });
    }

    @Override
    public StringPropertyAssertion<String> mimetype() {
        return propertyAssertionFactory.string(this, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                try {
                    return Files.probeContentType(provider.getActual().toPath());
                } catch (IOException e) {
                    return e.getMessage();
                }
            }

            @Override
            public String getSubject() {
                return "mimetype";
            }
        });
    }

    @Override
    public BinaryPropertyAssertion<Boolean> exists() {
        return propertyAssertionFactory.binary(this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return provider.getActual().exists();
            }

            @Override
            public String getSubject() {
                return "exists";
            }
        });
    }
}
