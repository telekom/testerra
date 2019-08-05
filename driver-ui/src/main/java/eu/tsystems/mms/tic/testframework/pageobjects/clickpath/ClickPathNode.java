/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.pageobjects.clickpath;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piet on 20.10.15.
 */
public class ClickPathNode {

    public void setToSomething() {
        type = Type.OTHER_ACTIONS;
    }

    public String getFirstElementInfo() {
        if (elements.size() == 0) {
            return "list size zero";
        }

        ClickPathElement clickPathElement = elements.get(0);
        if (clickPathElement == null) {
            return "no element";
        }

        String info = clickPathElement.getInfo();
        if (info == null) {
            return "no info";
        }
        return info;
    }

    public enum Type {
        GET("#6FB1FC", "triangle"),
        CLICK("#86B342", "rectangle"),
        PAGE("#f569ac", "ellipse"),
        OTHER_ACTIONS("#847da1", "octagon"),
        ERROR("#FF0000", "octagon");

        private final String colorCode;
        private final String shape;

        Type(String colorCode, String shape) {
            this.colorCode = colorCode;
            this.shape = shape;
        }

        public String getColorCode() {
            return colorCode;
        }

        public String getShape() {
            return shape;
        }
    }

    private final List<ClickPathElement> elements = new ArrayList<ClickPathElement>(1);

    private Type type;
    private WebElement webElement;

    private static int nodeCounter = 0;
    private final int nodeCount;

    public ClickPathNode(ClickPathElement clickPathElement) {
        synchronized (ClickPathNode.class) {
            nodeCounter++;
        }
        this.nodeCount = nodeCounter;
        addElement(clickPathElement);
    }

    public void addElement(ClickPathElement clickPathElement) {
        elements.add(clickPathElement);

        if (elements.size() > 1) {
            type = Type.OTHER_ACTIONS;
        }
        else {
            switch (clickPathElement.getType()) {
                case CLICK:
                    type = Type.CLICK;
                    break;
                case GET:
                    type = Type.GET;
                    break;
                case NON_CLICK_ACTION:
                    type = Type.OTHER_ACTIONS;
                    break;
                case PAGE:
                    type = Type.PAGE;
                    break;
                case ERROR:
                    type = Type.ERROR;
                    break;
                default:
                    throw new TesterraSystemException("Not yet implemented: " + type);
            }
        }
    }

    public void addElements(List<ClickPathElement> clickPathElements) {
        type = Type.OTHER_ACTIONS;
        elements.addAll(clickPathElements);
    }

    public Type getType() {
        return type;
    }

    public List<ClickPathElement> getElements() {
        return elements;
    }

    public void clear() {
        elements.clear();
    }

    public WebElement getWebElement() {
        return webElement;
    }

    public void setWebElement(WebElement webElement) {
        this.webElement = webElement;
    }

    public int getNodeCount() {
        return nodeCount;
    }
}
