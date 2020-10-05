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

package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.AbstractUiElementList;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.PageFactory;

public class DefaultComponentList<COMPONENT extends AbstractComponent<COMPONENT>> extends AbstractUiElementList<COMPONENT> {
    private final PageFactory pageFactory = Testerra.injector.getInstance(PageFactory.class);
    private COMPONENT component;

    public DefaultComponentList(COMPONENT component) {
        super(component);
        this.component = component;
    }

    @Override
    public COMPONENT get(int i) {
        COMPONENT component = (COMPONENT)pageFactory.createComponent(this.component.getClass(), this.component.rootElement.list().get(i));
        //component.setParent(this.component.getParent());
        return component;
    }
}
