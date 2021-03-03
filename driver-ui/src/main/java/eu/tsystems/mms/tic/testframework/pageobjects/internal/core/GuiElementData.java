/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 *
 */
package eu.tsystems.mms.tic.testframework.pageobjects.internal.core;

import eu.tsystems.mms.tic.testframework.internal.Nameable;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locator;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.DefaultLocator;
import eu.tsystems.mms.tic.testframework.webdriver.WebDriverRetainer;
import org.openqa.selenium.WebDriver;

/**
 * Contains the intersecting information of {@link GuiElement} and {@link GuiElementCore}
 * required for communication or creating new elements.
 */
public class GuiElementData implements
    Nameable<GuiElementData>,
    WebDriverRetainer
{
    private final DefaultLocator locate;
    private final WebDriver webDriver;
    private GuiElementData parent;
    private int index = -1;
    private GuiElement guiElement;
    private String name;
    private boolean shadowRoot = false;
    private boolean sensibleData = false;
    private boolean isFrame = false;

    /**
     * Creates a state as descendant of on another state
     * by setting the GuiElementData as parent.
     */
    public GuiElementData(GuiElementData parent, Locator locator) {
        this(parent.getWebDriver(), locator);
        this.parent = parent;
        this.guiElement = parent.guiElement;
    }

    /**
     * Creates a state as iteration of another state
     * by copying the GuiElementData but using another index.
     */
    public GuiElementData(GuiElementData parent, int index) {
        this(parent.webDriver, parent.locate);
        this.index = index;
        this.parent = parent.parent;
        this.name = parent.name;
        this.guiElement = parent.guiElement;
        this.shadowRoot = parent.shadowRoot;
    }

    public GuiElementData(WebDriver webDriver, Locator locator) {
        this.webDriver = webDriver;
        this.locate = (DefaultLocator) locator;
    }

    public GuiElementData setGuiElement(GuiElement guiElement) {
        this.guiElement = guiElement;
        return this;
    }

    public GuiElement getGuiElement() {
        return guiElement;
    }

    @Override
    public GuiElementData getParent() {
        return parent;
    }

    @Override
    public boolean hasOwnName() {
        return name!=null;
    }

    public DefaultLocator getLocate() {
        return locate;
    }

    @Override
    public WebDriver getWebDriver() {
        return webDriver;
    }

    public boolean isShadowRoot() {
        return shadowRoot;
    }

    public GuiElementData setHasShadowRoot(boolean shadowRoot) {
        this.shadowRoot = shadowRoot;
        return this;
    }

    public boolean hasSensibleData() {
        return sensibleData;
    }

    public GuiElementData setHasSensibleData(boolean sensibleData) {
        this.sensibleData = sensibleData;
        return this;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public String getName(boolean detailed) {
        StringBuilder sb = new StringBuilder();

        boolean hasName = hasOwnName();

        if (hasName) {
            sb.append(this.name);
        } else {
            sb.append(UiElement.class.getSimpleName());
        }
        if (!hasName || detailed) {
            sb.append("(").append(locate);
            if (index != -1) {
                sb.append("[").append(index+1).append("]");
            }
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public GuiElementData setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }
    public boolean isFrame() {
        return isFrame;
    }

    public GuiElementData setIsFrame(boolean frame) {
        isFrame = frame;
        return this;
    }
}
