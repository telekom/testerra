/*
 * Testerra
 *
 * (C) 2022, Clemens Große, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package io.testerra.report.test.pages;

public enum TestPage {
    INPUT_TEST_PAGE("Input/input.html",
            "Open again",
            new String[]{"Ope", "en agai", "ain"},
            "Button2",
            "value"),

    DUMMY_TEST_PAGE("Input/tt2_testDummyPage.html"),
    DUMMY_TEST_PAGE_2("Input/tt2_testDummyPage_2.html");


    private final String path;
    private final String elementText;
    private final String[] elementTextArray;
    private final String attributeValue;
    private final String attributeValuePart;
    private final String attributeName;

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

    TestPage(String path) {
        this.path = path;
        this.elementText = null;
        this.attributeName = null;
        this.attributeValuePart = null;
        this.elementTextArray = null;
        this.attributeValue = null;
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
