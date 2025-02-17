/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.core.testpage;

public enum TestPage {
    INPUT_TEST_PAGE("Input/input.html",
            "Open again",
            new String[]{"Ope", "en agai", "ain"},
            "Button2",
            "value"),
    FRAME_TEST_PAGE("FrameSimple/frame.html", INPUT_TEST_PAGE),
    DRAG_AND_DROP("DragAndDrop/index.html", INPUT_TEST_PAGE),
    DRAG_AND_DROP_OVER_FRAMES("DragAndDropFrame/index.html", INPUT_TEST_PAGE),
    MULTISELECT("Multiselect/index.html"),
    LAYOUT("Layout/index.html"),
    LAYOUT_FRAME("Layout/index-frame.html"),
    LAYOUT_IMAGE("LayoutImage/index.html"),
    LAYOUT_EXT_CSS_X("LayoutExtremeCSS/index-x.html"),
    LAYOUT_EXT_CSS_Y("LayoutExtremeCSS/index-y.html"),
    LIST("GuiElementList/index.html"),
    IMG("Img/logo", INPUT_TEST_PAGE),
    SHADOW_ROOT("ShadowDom/shadow_dom1.html"),
    SHADOW_ROOT2("ShadowDom/shadow_dom2.html"),
    SHADOW_ROOT3("ShadowDom/shadow_dom3.html"),
    SWITCH_WINDOW("Multiwindow/index.html"),

    BIDI_JS_LOGS("Bidi/log-entries.html"),
    BIDI_BROKEN_IMAGE("Bidi/broken-image.html");

    private String path;
    private String elementText;
    private String[] elementTextArray;
    private String attributeValue;
    private String attributeValuePart;
    private String attributeName;

    TestPage(String path) {
        this.path = path;
    }

    TestPage(String path, TestPage parentPage) {
        this.path = path;
        if (parentPage != null) {
            this.elementText = parentPage.elementText;
            this.elementTextArray = parentPage.elementTextArray;
            this.attributeValue = parentPage.attributeValue;
            this.attributeName = parentPage.attributeName;
            this.attributeValuePart = attributeValue.substring(1, 4);
        }
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
