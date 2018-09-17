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
package eu.tsystems.mms.tic.testframework.core.test.pageobjects;

/**
 * Created by rnhb on 02.06.2015.
 */
public enum TestPage {
    INPUT_TEST_PAGE("http://192.168.60.239/WebsitesForTests/Input/input.html",
            "Open again",
            new String[]{"Ope", "en agai", "ain"},
            "Button2",
            "value"),
    FRAME_TEST_PAGE("http://192.168.60.239/WebsitesForTests/FrameSimple/frame.html", INPUT_TEST_PAGE),
    DRAG_AND_DROP("http://192.168.60.239/WebsitesForTests/DragAndDrop/index.html", INPUT_TEST_PAGE),
    DRAG_AND_DROP_OVER_FRAMES("http://192.168.60.239/WebsitesForTests/DragAndDropFrame/index.html", INPUT_TEST_PAGE),
    MULTISELECT("http://192.168.60.239/WebsitesForTests/Multiselect/index.html", INPUT_TEST_PAGE)
    ;

    private String url;
    private String elementText;
    private String[] elementTextArray;
    private String attributeValue;
    private String attributeValuePart;
    private String attributeName;

    TestPage(String url, TestPage parentPage) {
        this.url = url;
        this.elementText = parentPage.elementText;
        this.elementTextArray = parentPage.elementTextArray;
        this.attributeValue = parentPage.attributeValue;
        this.attributeName = parentPage.attributeName;
        this.attributeValuePart = attributeValue.substring(1, 4);
    }

    TestPage(String url, String elementText, String[] elementTextArray, String attributeValue, String attributeName) {
        this.url = url;
        this.elementText = elementText;
        this.elementTextArray = elementTextArray;
        this.attributeValue = attributeValue;
        this.attributeName = attributeName;
        this.attributeValuePart = attributeValue.substring(1, 4);
    }

    public String getUrl() {
        return url;
    }

    public String getElementText() {
        return elementText;
    }

    public String[] getElementTextArray() {
        return elementTextArray;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public String getAttributeValuePart() {
        return attributeValuePart;
    }

    public String getAttributeName() {
        return attributeName;
    }
}
