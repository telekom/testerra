/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package eu.tsystems.mms.tic.testframework.internal.asserts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.commons.io.FilenameUtils;

/**
 * Default implementation of {@link FileAssertion}
 * @author Mike Reiche
 */
public class DefaultFileAssertion extends AbstractPropertyAssertion<File> implements FileAssertion {

    public DefaultFileAssertion(AbstractPropertyAssertion parentAssertion, AssertionProvider<File> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public QuantityAssertion<Long> bytes() {
        return propertyAssertionFactory.createWithParent(DefaultQuantityAssertion.class, this, new AssertionProvider<Long>() {
            @Override
            public Long getActual() {
                return provider.getActual().length();
            }

            @Override
            public String createSubject() {
                return "bytes";
            }
        });
    }

    @Override
    public StringAssertion<String> name() {
        return propertyAssertionFactory.createWithParent(DefaultStringAssertion.class, this, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return provider.getActual().getName();
            }

            @Override
            public String createSubject() {
                return "name";
            }
        });
    }

    @Override
    public StringAssertion<String> extension() {
        return propertyAssertionFactory.createWithParent(DefaultStringAssertion.class, this, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return FilenameUtils.getExtension(provider.getActual().getName());
            }

            @Override
            public String createSubject() {
                return "extension";
            }
        });
    }

    @Override
    public StringAssertion<String> mimetype() {
        return propertyAssertionFactory.createWithParent(DefaultStringAssertion.class, this, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                try {
                    return Files.probeContentType(provider.getActual().toPath());
                } catch (IOException e) {
                    return e.getMessage();
                }
            }

            @Override
            public String createSubject() {
                return "mimetype";
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> exists() {
        return propertyAssertionFactory.createWithParent(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return provider.getActual().exists();
            }

            @Override
            public String createSubject() {
                return "exists";
            }
        });
    }
}
