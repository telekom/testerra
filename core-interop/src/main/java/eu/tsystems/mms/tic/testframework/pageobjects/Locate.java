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
 package eu.tsystems.mms.tic.testframework.pageobjects;

import java.util.function.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Advanced selector for elements as replacement for By
 *
 * @author Mike Reiche
 */
public class Locate {

    private Predicate<WebElement> filter;
    private boolean unique = false;
    private By by;
    private String preparedFormat;

    Locate(By by) {
        this.by = by;
    }

    Locate(String preparedFormat) {
        this.preparedFormat = preparedFormat;
    }

    public By getBy() {
        return this.by;
    }

    public boolean isUnique() {
        return this.unique;
    }

    public Locate unique() {
        this.unique = true;
        return this;
    }

    public Locate displayed() {
        return displayed(true);
    }
    public Locate displayed(boolean displayed) {
        return filter(webElement -> webElement.isDisplayed()==displayed);
    }

    public Locate filter(Predicate<WebElement> filter) {
        this.filter = filter;
        return this;
    }

    public Predicate<WebElement> getFilter() {
        return this.filter;
    }

    @Override
    public String toString() {
        String toString = by.toString() + (unique ? " (unique)" : "");
        return toString;
    }

    /**
     * Formats an xpath selector
     * @param args
     * @return
     */
    public Locate with(Object... args) {
        Locate locate = new Locate(By.xpath(String.format(preparedFormat, args)));
        locate.unique = unique;
        locate.filter = filter;
        return locate;
    }
}
