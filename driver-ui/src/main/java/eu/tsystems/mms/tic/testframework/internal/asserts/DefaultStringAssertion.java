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

import eu.tsystems.mms.tic.testframework.logging.Loggable;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link StringAssertion}
 *
 * @author Mike Reiche
 */
public class DefaultStringAssertion extends DefaultObjectAssertion<String> implements StringAssertion, Loggable {

    public DefaultStringAssertion(AbstractPropertyAssertion<String> parentAssertion, AssertionProvider<String> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public BinaryAssertion<Boolean> contains(String expected) {
        return propertyAssertionFactory.createWithParent(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return provider.getActual().contains(expected);
            }

            @Override
            public String createSubject() {
                return "contains " + Format.param(expected);
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> startsWith(String expected) {
        return propertyAssertionFactory.createWithParent(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return provider.getActual().startsWith(expected);
            }

            @Override
            public String createSubject() {
                return "starts with " + Format.param(expected);
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> endsWith(String expected) {
        return propertyAssertionFactory.createWithParent(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return provider.getActual().endsWith(expected);
            }

            @Override
            public String createSubject() {
                return "ends with " + Format.param(expected);
            }
        });
    }

    @Override
    public PatternAssertion matches(Pattern pattern) {
        return propertyAssertionFactory.createWithParent(DefaultPatternAssertion.class, this, new AssertionProvider<Matcher>() {
            @Override
            public Matcher getActual() {
                return pattern.matcher(provider.getActual());
            }

            @Override
            public String createSubject() {
                return "matches " + Format.param(pattern);
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> hasWords(List<String> words) {
        final Pattern nonWordAtBegin = Pattern.compile("^\\W");
        final Pattern nonWordAtTheEnd = Pattern.compile("\\W$");

        final String wordsList = words.stream()
                .map(word -> (nonWordAtBegin.matcher(word).find() ? "\\B" : "\\b") + "\\Q" + word) // word boundary for begin
                .map(word -> word + "\\E" + (nonWordAtTheEnd.matcher(word).find() ? "\\B" : "\\b")) // word boundary for end
                .collect(Collectors.joining("|"));
        final Pattern wordsPattern = Pattern.compile(wordsList, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

        final String wordsListWithoutRegex = String.join("|", words);

        return propertyAssertionFactory.createWithParent(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                int found = 0;
                Matcher matcher = wordsPattern.matcher(provider.getActual());
                while (matcher.find()) found++;
                return found == words.size();
            }

            @Override
            public String createSubject() {
                return "has words " + Format.param(wordsListWithoutRegex);
            }
        });
    }

    @Override
    public QuantityAssertion<Integer> length() {
        return propertyAssertionFactory.createWithParent(DefaultQuantityAssertion.class, this, new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                return provider.getActual().length();
            }

            @Override
            public String createSubject() {
                return "length";
            }
        });
    }

    @Override
    public StringAssertion map(Function<String, String> mapFunction) {
        return propertyAssertionFactory.createWithParent(DefaultStringAssertion.class, this, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                String actual = provider.getActual();
                if (actual == null) {
                    return null;
                } else {
                    return mapFunction.apply(provider.getActual());
                }
            }

            @Override
            public java.lang.String createSubject() {
                return "mapped to " + Format.shortString(getActual());
            }
        });
    }
}
