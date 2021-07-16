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
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import java.awt.Color;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.openqa.selenium.Rectangle;

/**
 * Default implementation for {@link UiElementAssertion}
 */
public class DefaultUiElementAssertion implements UiElementAssertion {
    private static final PropertyAssertionFactory propertyAssertionFactory = Testerra.getInjector().getInstance(PropertyAssertionFactory.class);
    private static final Report report = Testerra.getInjector().getInstance(Report.class);
    private static final boolean demoModeEnabled = Testerra.Properties.DEMO_MODE.asBool();
    private final PropertyAssertionConfig propertyAssertionConfig;
    private final GuiElementCore core;
    private final GuiElement guiElement;

    abstract class UiElementAssertionProvider<T> extends AssertionProvider<T> implements Loggable {

        @Override
        public AssertionError wrapAssertionError(AssertionError assertionError) {
            return new UiElementAssertionError(guiElement.getData(), assertionError);
        }

        @Override
        public void passed(AbstractPropertyAssertion<T> assertion) {
            if (demoModeEnabled) {
                try {
                    guiElement.getCore().highlight(new Color(0, 255, 0));
                } catch (Throwable e) {
                    log().warn("Unable to highlight a PASSED assertion: " + e.getMessage());
                }
            }
        }

        @Override
        public void failed(AbstractPropertyAssertion<T> assertion) {
            if (demoModeEnabled) {
                try {
                    guiElement.getCore().highlight(new Color(255, 0, 0));
                } catch (Throwable e) {
                    // ignore
                }
            }
        }
    }

    public DefaultUiElementAssertion(UiElement uiElement, PropertyAssertionConfig config) {
        this.guiElement = (GuiElement)uiElement;
        this.core = this.guiElement.getCore();
        this.propertyAssertionConfig = config;
    }

    /**
     * @deprecated This is only required for {@link LegacyGuiElementAssertWrapper}
     * @param uiElement
     * @param useAssertion
     */
    public DefaultUiElementAssertion(UiElement uiElement, Assertion useAssertion) {
        this.guiElement = (GuiElement)uiElement;
        this.core = this.guiElement.getCore();
        this.propertyAssertionConfig = new PropertyAssertionConfig();
        this.propertyAssertionConfig.throwErrors = true;
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
            public String createSubject(String actual) {
                return Format.separate(guiElement.toString(), "tagName="+Format.param(actual));
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
            public String createSubject(String actual) {
                return Format.separate(guiElement.toString(), "text="+Format.shortString(actual));
            }
        });
    }

    @Override
    public StringAssertion<String> attribute(String attribute) {
        return propertyAssertionFactory.createWithConfig(DefaultStringAssertion.class, this.propertyAssertionConfig, new UiElementAssertionProvider<String>() {
            @Override
            public String getActual() {
                return core.getAttribute(attribute);
            }

            @Override
            public String createSubject(String actual) {
                return Format.separate(guiElement.toString(), attribute+"="+Format.param(actual));
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
            public String createSubject(String actual) {
                return Format.separate(guiElement.toString(),"css { "+Format.label(property, actual) + " }");
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
            public String createSubject(Boolean actual) {
                return Format.separate(guiElement.toString(), "present");
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> visible(boolean fullyVisible) {
        return propertyAssertionFactory.createWithConfig(DefaultBinaryAssertion.class, this.propertyAssertionConfig, new UiElementAssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return core.isVisible(fullyVisible);
            }

            @Override
            public String createSubject(Boolean actual) {
                return Format.separate(guiElement.toString(), (fullyVisible ?"fully ":"")+"visible");
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> displayed() {
        return propertyAssertionFactory.createWithConfig(DefaultBinaryAssertion.class, this.propertyAssertionConfig, new UiElementAssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                try {
                    return core.isDisplayed();
                } catch (ElementNotFoundException e) {
                    return false;
                }
            }
            @Override
            public String createSubject(Boolean actual) {
                return Format.separate(guiElement.toString(), "displayed");
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
            public String createSubject(Boolean actual) {
                return Format.separate(guiElement.toString(), "enabled");
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
            public String createSubject(Boolean actual) {
                return Format.separate(guiElement.toString(), "selected");
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
            public String createSubject(Boolean actual) {
                return Format.separate(guiElement.toString(), "selectable");
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
            public String createSubject(Rectangle actual) {
                return Format.separate(guiElement.toString(), "bounds");
            }
        });
    }

    @Override
    public QuantityAssertion<Integer> foundElements() {
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
            public String createSubject(Integer actual) {
                return Format.separate(guiElement.toString(), "numberOfElements");
            }
        });
    }

    private static void addScreenshotToReport(Screenshot screenshot) {
        report.addScreenshot(screenshot, Report.FileMode.COPY);
        ExecutionContextController.getCurrentMethodContext().addScreenshot(screenshot);
    }

    @Override
    public ImageAssertion screenshot(Report.Mode reportMode) {
        Screenshot screenshot = new Screenshot(guiElement.getName(true));
        screenshot.setFile(core.takeScreenshot());

        AtomicReference<Screenshot> atomicScreenshot = new AtomicReference<>();
        atomicScreenshot.set(screenshot);

        if (reportMode == Report.Mode.ALWAYS) {
            addScreenshotToReport(atomicScreenshot.get());
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
                addScreenshotToReport(atomicScreenshot.get());
            }

            @Override
            public String createSubject(File actual) {
                return Format.separate(guiElement.toString(), "screenshot");
            }
        });
    }
}
