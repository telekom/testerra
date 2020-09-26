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

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.WebDriverRetainer;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.Nameable;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.WebDriver;

/**
 * Holds the state of the {@link GuiElement} to interchange with any other
 * implementations like {@link GuiElementCore}
 */
public class GuiElementData implements
    Nameable<GuiElementData>,
    WebDriverRetainer
{
    private final Locate locate;
    private final WebDriver webDriver;
    private GuiElementData parent;
    private int index = -1;
    private GuiElement guiElement;
    private String name;
    private boolean shadowRoot = false;
    private boolean sensibleData = false;

    /**
     * Creates a state as descendant of on another state
     * by setting the GuiElementData as parent.
     */
    public GuiElementData(GuiElementData parent, Locate locate) {
        this(parent.getWebDriver(), locate);
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

    public GuiElementData(
        WebDriver webDriver,
        Locate locate
    ) {
        this.webDriver = webDriver;
        this.locate = locate;
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
    public boolean hasName() {
        return name!=null;
    }

    public Locate getLocate() {
        return locate;
    }

    public String getBrowser() {
        return WebDriverManager.getRelatedWebDriverRequest(webDriver).browser;
    }

    @Override
    public WebDriver getWebDriver() {
        return webDriver;
    }

    public boolean isShadowRoot() {
        return shadowRoot;
    }

    public GuiElementData setShadowRoot(boolean shadowRoot) {
        this.shadowRoot = shadowRoot;
        return this;
    }

    public boolean hasSensibleData() {
        return sensibleData;
    }

    public GuiElementData setSensibleData(boolean sensibleData) {
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

        boolean hasName = hasName();

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
}
