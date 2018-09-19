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

/**
 * Created by piet on 20.10.15.
 */
public class ClickPathElement {

    private final CPEType type;
    private String info;

    public ClickPathElement(CPEType type, String info) {
        this.type = type;
        this.info = info;
    }

    public enum CPEType {
        CLICK,
        GET,
        NON_CLICK_ACTION,
        PAGE,
        ERROR
    }

    public CPEType getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }

    public void enhanceInfo(String msg) {
        info += msg;
    }

    @Override
    public String toString() {
        return "ClickPathElement{" +
                "type=" + type +
                ", info='" + info + '\'' +
                '}';
    }
}
