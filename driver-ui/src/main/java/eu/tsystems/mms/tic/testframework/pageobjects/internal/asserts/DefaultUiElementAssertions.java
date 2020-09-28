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

package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementAssertions;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;
import org.openqa.selenium.Rectangle;

/**
 * Default implementation for {@link UiElementAssertions}
 */
public class DefaultUiElementAssertions implements UiElementAssertions {
    private static final PropertyAssertionFactory propertyAssertionFactory = Testerra.injector.getInstance(PropertyAssertionFactory.class);
    private final PropertyAssertionConfig propertyAssertionConfig = new PropertyAssertionConfig();
    private final GuiElementCore core;
    private final GuiElement guiElement;

    public DefaultUiElementAssertions(UiElement uiElement, boolean throwErrors) {
        this.guiElement = (GuiElement)uiElement;
        this.core = this.guiElement.getCore();
        this.propertyAssertionConfig.throwErrors = throwErrors;
    }


    @Override
    public StringAssertion<String> tagName() {
        DefaultStringAssertion<String> assertion = propertyAssertionFactory.createWithConfig(DefaultStringAssertion.class, this.propertyAssertionConfig, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return core.getTagName();
            }

            @Override
            public String getSubject() {
                return String.format("%s.@tagName", guiElement);
            }
        });
        return assertion;
    }

    @Override
    public StringAssertion<String> text() {
        DefaultStringAssertion<String> assertion = propertyAssertionFactory.createWithConfig(DefaultStringAssertion.class, this.propertyAssertionConfig, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return core.getText();
            }

            @Override
            public String getSubject() {
                return String.format("%s.@text", guiElement.toString(true));
            }
        });
        return assertion;
    }

    @Override
    public StringAssertion<String> value(String attribute) {
        final String finalAttribute = attribute;
        DefaultStringAssertion<String> assertion = propertyAssertionFactory.createWithConfig(DefaultStringAssertion.class, this.propertyAssertionConfig, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return core.getAttribute(finalAttribute);
            }

            @Override
            public String getSubject() {
                return String.format("%s.@%s", guiElement.toString(true), finalAttribute);
            }
        });
        return assertion;
    }

    @Override
    public StringAssertion<String> css(String property) {
        DefaultStringAssertion<String> assertion = propertyAssertionFactory.createWithConfig(DefaultStringAssertion.class, this.propertyAssertionConfig, new AssertionProvider<String>() {
            @Override
            public String getActual() {
                return core.getCssValue(property);
            }

            @Override
            public String getSubject() {
                return String.format("%s.css(@%s)", guiElement.toString(true), property);
            }
        });
        return assertion;
    }

    @Override
    public BinaryAssertion<Boolean> present() {
        DefaultBinaryAssertion<Boolean> assertion = propertyAssertionFactory.createWithConfig(DefaultBinaryAssertion.class, this.propertyAssertionConfig, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isPresent();
//                try {
//                    frameAwareCore.findWebElement(webElement -> {});
//                    return true;
//                } catch (ElementNotFoundException e) {
//                    return false;
//                }
            }

            @Override
            public String getSubject() {
                return String.format("%s.@present", guiElement.toString(true));
            }
        });
        return assertion;
    }

    @Override
    public BinaryAssertion<Boolean> visible(boolean complete) {
        DefaultBinaryAssertion<Boolean> assertion = propertyAssertionFactory.createWithConfig(DefaultBinaryAssertion.class, this.propertyAssertionConfig, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isVisible(complete);
            }

            @Override
            public String getSubject() {
                return String.format("%s.visible(complete: %s)", guiElement.toString(true), complete);
            }
        });
        return assertion;
    }

    @Override
    public BinaryAssertion<Boolean> displayed() {
        DefaultBinaryAssertion<Boolean> assertion = propertyAssertionFactory.createWithConfig(DefaultBinaryAssertion.class, this.propertyAssertionConfig, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isDisplayed();
            }
            @Override
            public String getSubject() {
                return String.format("%s.@displayed", guiElement.toString(true));
            }
        });
        return assertion;
    }

    @Override
    public BinaryAssertion<Boolean> enabled() {
        DefaultBinaryAssertion<Boolean> assertion = propertyAssertionFactory.createWithConfig(DefaultBinaryAssertion.class, this.propertyAssertionConfig, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isEnabled();
            }

            @Override
            public String getSubject() {
                return String.format("%s.@enabled", guiElement.toString(true));
            }
        });
        return assertion;
    }

    @Override
    public BinaryAssertion<Boolean> selected() {
        DefaultBinaryAssertion<Boolean> assertion = propertyAssertionFactory.createWithConfig(DefaultBinaryAssertion.class, this.propertyAssertionConfig, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isSelected();
            }

            @Override
            public String getSubject() {
                return String.format("%s.@selected", guiElement.toString(true));
            }
        });
        return assertion;
    }

    @Override
    public BinaryAssertion<Boolean> selectable() {
        DefaultBinaryAssertion<Boolean> assertion = propertyAssertionFactory.createWithConfig(DefaultBinaryAssertion.class, this.propertyAssertionConfig, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isSelectable();
            }

            @Override
            public String getSubject() {
                return String.format("%s.selectable", guiElement.toString(true));
            }
        });
        return assertion;
    }

    @Override
    public RectAssertion bounds() {
        DefaultRectAssertion assertion = propertyAssertionFactory.createWithConfig(DefaultRectAssertion.class, this.propertyAssertionConfig, new AssertionProvider<Rectangle>() {
            @Override
            public Rectangle getActual() {
                return core.getRect();
            }

            @Override
            public String getSubject() {
                return String.format("%s.bounds", guiElement.toString(true));
            }
        });
        return assertion;
    }

    @Override
    public QuantityAssertion<Integer> numberOfElements() {
        DefaultQuantityAssertion assertion = propertyAssertionFactory.createWithConfig(DefaultQuantityAssertion.class, this.propertyAssertionConfig, new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                try {
                    return core.getNumberOfFoundElements();
                } catch (ElementNotFoundException e) {
                    return 0;
                }
            }

            @Override
            public String getSubject() {
                return String.format("%s.@numberOfElements", guiElement.toString(true));
            }
        });
        return assertion;
    }

    @Override
    public ImageAssertion screenshot() {
        final AtomicReference<File> screenshot = new AtomicReference<>();
        screenshot.set(core.takeScreenshot());
        DefaultImageAssertion assertion = propertyAssertionFactory.createWithConfig(DefaultImageAssertion.class, this.propertyAssertionConfig, new AssertionProvider<File>() {
            @Override
            public File getActual() {
                return screenshot.get();
            }

            @Override
            public void failed(AbstractPropertyAssertion assertion) {
                screenshot.set(core.takeScreenshot());
            }

            @Override
            public String getSubject() {
                return String.format("%s.screenshot", guiElement.toString(true));
            }
        });
        return assertion;
    }
}
