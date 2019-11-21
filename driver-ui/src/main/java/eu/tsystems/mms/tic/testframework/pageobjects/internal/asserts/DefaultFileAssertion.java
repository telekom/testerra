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
    public QuantityAssertion<Long> bytes() {
        return new DefaultQuantityAssertion<>(this, new AssertionProvider<Long>() {
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
    public StringAssertion<String> name() {
        return new DefaultStringAssertion<>(this, new AssertionProvider<String>() {
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
    public StringAssertion<String> extension() {
        return new DefaultStringAssertion<>(this, new AssertionProvider<String>() {
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
    public StringAssertion<String> mimetype() {
        return new DefaultStringAssertion<>(this, new AssertionProvider<String>() {
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
    public BinaryAssertion<Boolean> exists() {
        return new DefaultBinaryAssertion<>(this, new AssertionProvider<Boolean>() {
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
