/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

import java.util.function.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @deprecated Use {@link LocatorFactoryProvider}
 */
public class Locate implements Locator, LocatorFactoryProvider {
    public static Locator by(By by) {
        return Locate.by(by);
    }
    @Override
    public By getBy() {
        return null;
    }

    @Override
    public Locator unique() {
        return null;
    }

    @Override
    public Locator filter(Predicate<WebElement> filter) {
        return null;
    }
}
