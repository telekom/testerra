package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileAssertion extends AbstractPropertyAssertion<File> implements IFileAssertion {

    public FileAssertion(PropertyAssertion parentAssertion, AssertionProvider<File> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public IQuantifiedPropertyAssertion<Long> bytes() {
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
    public IStringPropertyAssertion<String> name() {
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
    public IStringPropertyAssertion<String> extension() {
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
    public IStringPropertyAssertion<String> mimetype() {
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
    public IBinaryPropertyAssertion<Boolean> exists() {
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
