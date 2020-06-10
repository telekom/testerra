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
 package eu.tsystems.mms.tic.testframework.core.test;

public enum TestPage {
    INPUT_TEST_PAGE("Input/input.html",
            "Open again",
            new String[]{"Ope", "en agai", "ain"},
            "Button2",
            "value"),
    FRAME_TEST_PAGE("FrameSimple/frame.html", INPUT_TEST_PAGE),
    DRAG_AND_DROP("DragAndDrop/index.html", INPUT_TEST_PAGE),
    DRAG_AND_DROP_OVER_FRAMES("DragAndDropFrame/index.html", INPUT_TEST_PAGE),
    MULTISELECT("Multiselect/index.html", INPUT_TEST_PAGE),
    LAYOUT("Layout/index.html", INPUT_TEST_PAGE),
    LIST("GuiElementList/index.html", INPUT_TEST_PAGE),
    SHADOW_ROOT("GuiElementShadowRoot/shadow_root.html", INPUT_TEST_PAGE)
    ;

    private String path;
    private String elementText;
    private String[] elementTextArray;
    private String attributeValue;
    private String attributeValuePart;
    private String attributeName;

    TestPage(String path, TestPage parentPage) {
        this.path = path;
        this.elementText = parentPage.elementText;
        this.elementTextArray = parentPage.elementTextArray;
        this.attributeValue = parentPage.attributeValue;
        this.attributeName = parentPage.attributeName;
        this.attributeValuePart = attributeValue.substring(1, 4);
    }

    TestPage(String path, String elementText, String[] elementTextArray, String attributeValue, String attributeName) {
        this.path = path;
        this.elementText = elementText;
        this.elementTextArray = elementTextArray;
        this.attributeValue = attributeValue;
        this.attributeName = attributeName;
        this.attributeValuePart = attributeValue.substring(1, 4);
    }

    public String getPath() {
        return path;
    }

    @Deprecated
    public String getElementText() {
        return elementText;
    }

    @Deprecated
    public String[] getElementTextArray() {
        return elementTextArray;
    }

    @Deprecated
    public String getAttributeValue() {
        return attributeValue;
    }

    @Deprecated
    public String getAttributeValuePart() {
        return attributeValuePart;
    }

    @Deprecated
    public String getAttributeName() {
        return attributeName;
    }
}
