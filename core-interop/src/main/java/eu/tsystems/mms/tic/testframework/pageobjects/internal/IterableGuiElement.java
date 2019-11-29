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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import java.util.Iterator;

/**
 * An GuiElement which is iterable
 */
public interface IterableGuiElement<SELF> extends
    Iterable<SELF>,
    Iterator<SELF>,
    BasicGuiElement
{
    SELF element(int position);

    default SELF firstElement() {
        return element(1);
    }

    default SELF lastElement() {
        return element(numberOfElements().getActual());
    }
}
