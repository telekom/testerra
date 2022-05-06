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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link StringAssertion}
 *
 * @author Mike Reiche
 */
public class DefaultStringAssertion<T> extends DefaultQuantityAssertion<T> implements StringAssertion<T>, Loggable {

    public DefaultStringAssertion(AbstractPropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public BinaryAssertion<Boolean> contains(String expected) {
        return propertyAssertionFactory.createWithParent(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return provider.getActual().toString().contains(expected);
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
                return provider.getActual().toString().startsWith(expected);
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
                return provider.getActual().toString().endsWith(expected);
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
                return pattern.matcher(provider.getActual().toString());
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

        return propertyAssertionFactory.createWithParent(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                int found = 0;
                Matcher matcher = wordsPattern.matcher(provider.getActual().toString());
                while (matcher.find()) found++;
                return found == words.size();
            }

            @Override
            public String createSubject() {
                return "has words " + Format.param(wordsList);
            }
        });

//        AtomicInteger found = new AtomicInteger();
//        words.forEach(word -> {
//            /*
//            Non-word characters have an impact to word boundary regex '\b'
//            If word contains non-word characters, assertion falls back to string.contains()
//            */
//            final Pattern wordBoundCharPattern = Pattern.compile("\\W");
//            Matcher charMatcher = wordBoundCharPattern.matcher(word);
//            if (!charMatcher.find()) {
//                final Pattern wordBoundaryPattern = Pattern.compile("\\b(" + word + ")\\b", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
//                Matcher wordMatcher = wordBoundaryPattern.matcher(provider.getActual().toString());
//                if (wordMatcher.find()) {
//                    found.incrementAndGet();
//                }
//            } else {
//                log().warn("'{}' contains non-word characters. 'hasWords()' assertion falls back to string.contains()", word);
//                if (provider.getActual().toString().toLowerCase().contains(word.toLowerCase())) {
//                    found.incrementAndGet();
//                }
//            }
//        });
//
//        return propertyAssertionFactory.createWithParent(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
//            @Override
//            public Boolean getActual() {
//                return found.get() == words.size();
//            }
//
//            @Override
//            public String createSubject() {
//                return "has words " + Format.param(String.join("|", words));
//            }
//        });
    }

    @Override
    public QuantityAssertion<Integer> length() {
        return propertyAssertionFactory.createWithParent(DefaultQuantityAssertion.class, this, new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                return provider.getActual().toString().length();
            }

            @Override
            public String createSubject() {
                return "length";
            }
        });
    }
}
