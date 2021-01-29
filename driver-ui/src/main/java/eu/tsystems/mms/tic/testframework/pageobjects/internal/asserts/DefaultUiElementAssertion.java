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
import eu.tsystems.mms.tic.testframework.exceptions.UiElementAssertionError;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.AbstractPropertyAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.AssertionProvider;
import eu.tsystems.mms.tic.testframework.internal.asserts.BinaryAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.DefaultBinaryAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.DefaultQuantityAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.DefaultStringAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.ImageAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.PropertyAssertionConfig;
import eu.tsystems.mms.tic.testframework.internal.asserts.PropertyAssertionFactory;
import eu.tsystems.mms.tic.testframework.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.StringAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import java.awt.Color;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;
import org.openqa.selenium.Rectangle;

/**
 * Default implementation for {@link UiElementAssertion}
 */
public class DefaultUiElementAssertion implements UiElementAssertion {
    private static final PropertyAssertionFactory propertyAssertionFactory = Testerra.getInjector().getInstance(PropertyAssertionFactory.class);
    private static final Report report = Testerra.getInjector().getInstance(Report.class);
    private final PropertyAssertionConfig propertyAssertionConfig = new PropertyAssertionConfig();
    private final GuiElementCore core;
    private final GuiElement guiElement;

    abstract class UiElementAssertionProvider<T> extends AssertionProvider<T> {

        @Override
        public AssertionError wrapAssertionError(AssertionError assertionError) {
            return new UiElementAssertionError(guiElement.getData(), assertionError);
        }

        @Override
        public void passed(AbstractPropertyAssertion<T> assertion) {
            if (Testerra.Properties.DEMO_MODE.asBool()) {
                guiElement.highlight(new Color(0, 255, 0));
            }
        }

        @Override
        public void failed(AbstractPropertyAssertion<T> assertion) {
            if (Testerra.Properties.DEMO_MODE.asBool()) {
                guiElement.highlight(new Color(255, 0, 0));
            }
        }
    }

    public DefaultUiElementAssertion(UiElement uiElement, boolean throwErrors) {
        this.guiElement = (GuiElement)uiElement;
        this.core = this.guiElement.getCore();
        this.propertyAssertionConfig.throwErrors = throwErrors;
    }

    /**
     * @deprecated This is only required for {@link LegacyGuiElementAssertWrapper}
     * @param uiElement
     * @param useAssertion
     */
    public DefaultUiElementAssertion(UiElement uiElement, Assertion useAssertion) {
        this(uiElement, true);
        this.propertyAssertionConfig.useAssertion = useAssertion;
    }

    @Override
    public StringAssertion<String> tagName() {
        return propertyAssertionFactory.createWithConfig(DefaultStringAssertion.class, this.propertyAssertionConfig, new UiElementAssertionProvider<String>() {
            @Override
            public String getActual() {
                return core.getTagName();
            }

            @Override
            public String getSubject() {
                return String.format("%s.@tagName", guiElement);
            }
        });
    }

    @Override
    public StringAssertion<String> text() {
        return propertyAssertionFactory.createWithConfig(DefaultStringAssertion.class, this.propertyAssertionConfig, new UiElementAssertionProvider<String>() {
            @Override
            public String getActual() {
                return core.getText();
            }

            @Override
            public String getSubject() {
                return String.format("%s.@text", guiElement.toString(true));
            }
        });
    }

    @Override
    public StringAssertion<String> value(String attribute) {
        final String finalAttribute = attribute;
        return propertyAssertionFactory.createWithConfig(DefaultStringAssertion.class, this.propertyAssertionConfig, new UiElementAssertionProvider<String>() {
            @Override
            public String getActual() {
                return core.getAttribute(finalAttribute);
            }

            @Override
            public String getSubject() {
                return String.format("%s.@%s", guiElement.toString(true), finalAttribute);
            }
        });
    }

    @Override
    public StringAssertion<String> css(String property) {
        return propertyAssertionFactory.createWithConfig(DefaultStringAssertion.class, this.propertyAssertionConfig, new UiElementAssertionProvider<String>() {
            @Override
            public String getActual() {
                return core.getCssValue(property);
            }

            @Override
            public String getSubject() {
                return String.format("%s.css(@%s)", guiElement.toString(true), property);
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> present() {
        return propertyAssertionFactory.createWithConfig(DefaultBinaryAssertion.class, this.propertyAssertionConfig, new UiElementAssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isPresent();
            }

            @Override
            public String getSubject() {
                return String.format("%s.@present", guiElement.toString(true));
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> visible(boolean complete) {
        return propertyAssertionFactory.createWithConfig(DefaultBinaryAssertion.class, this.propertyAssertionConfig, new UiElementAssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isVisible(complete);
            }

            @Override
            public String getSubject() {
                return String.format("%s.visible(complete: %s)", guiElement.toString(true), complete);
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> displayed() {
        return propertyAssertionFactory.createWithConfig(DefaultBinaryAssertion.class, this.propertyAssertionConfig, new UiElementAssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isDisplayed();
            }
            @Override
            public String getSubject() {
                return String.format("%s.@displayed", guiElement.toString(true));
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> enabled() {
        return propertyAssertionFactory.createWithConfig(DefaultBinaryAssertion.class, this.propertyAssertionConfig, new UiElementAssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isEnabled();
            }

            @Override
            public String getSubject() {
                return String.format("%s.@enabled", guiElement.toString(true));
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> selected() {
        return propertyAssertionFactory.createWithConfig(DefaultBinaryAssertion.class, this.propertyAssertionConfig, new UiElementAssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isSelected();
            }

            @Override
            public String getSubject() {
                return String.format("%s.@selected", guiElement.toString(true));
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> selectable() {
        return propertyAssertionFactory.createWithConfig(DefaultBinaryAssertion.class, this.propertyAssertionConfig, new UiElementAssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isSelectable();
            }

            @Override
            public String getSubject() {
                return String.format("%s.selectable", guiElement.toString(true));
            }
        });
    }

    @Override
    public RectAssertion bounds() {
        return propertyAssertionFactory.createWithConfig(DefaultRectAssertion.class, this.propertyAssertionConfig, new UiElementAssertionProvider<Rectangle>() {
            @Override
            public Rectangle getActual() {
                return core.getRect();
            }

            @Override
            public String getSubject() {
                return String.format("%s.bounds", guiElement.toString(true));
            }
        });
    }

    @Override
    public QuantityAssertion<Integer> numberOfElements() {
        return propertyAssertionFactory.createWithConfig(DefaultQuantityAssertion.class, this.propertyAssertionConfig, new UiElementAssertionProvider<Integer>() {
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
                return String.format("%s.numberOfElements", guiElement.toString(true));
            }
        });
    }

    @Override
    public ImageAssertion screenshot(Report.Mode reportMode) {
        Screenshot screenshot = new Screenshot(guiElement.getName(true));
        screenshot.setFile(core.takeScreenshot());

        AtomicReference<Screenshot> atomicScreenshot = new AtomicReference<>();
        atomicScreenshot.set(screenshot);

        if (reportMode == Report.Mode.ALWAYS) {
            report.addScreenshot(atomicScreenshot.get(), Report.FileMode.COPY);
        }

        return propertyAssertionFactory.createWithConfig(DefaultImageAssertion.class, this.propertyAssertionConfig, new UiElementAssertionProvider<File>() {
            @Override
            public File getActual() {
                return atomicScreenshot.get().getScreenshotFile();
            }

            @Override
            public void failed(AbstractPropertyAssertion assertion) {
                atomicScreenshot.get().setFile(core.takeScreenshot());
            }

            @Override
            public void failedFinally(AbstractPropertyAssertion assertion) {
                report.addScreenshot(atomicScreenshot.get(), Report.FileMode.MOVE);
            }

            @Override
            public String getSubject() {
                return String.format("%s.screenshot", guiElement.toString(true));
            }
        });
    }
}
