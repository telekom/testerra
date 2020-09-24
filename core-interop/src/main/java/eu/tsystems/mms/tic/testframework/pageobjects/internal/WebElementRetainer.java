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

package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.NonUniqueElementException;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import java.util.function.Consumer;
import org.openqa.selenium.WebElement;

/**
 * Interface for classes that retain a {@link WebElement}
 */
public interface WebElementRetainer {
    /**
     * Supplies the first found filtered {@link WebElement} to a consumer.
     * This makes sure that the element is present it it's current scope and not stale
     * @throws ElementNotFoundException If none found
     * @throws NonUniqueElementException If more than one WebElement has been found according to given {@link Locate}
     */
    void findWebElement(Consumer<WebElement> consumer);
}
