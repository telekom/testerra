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

import java.util.function.Consumer;
import java.util.function.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static Consumer<Locate> locatorConfigurator;

    private Locate() {
        if (locatorConfigurator != null) {
            locatorConfigurator.accept(this);
        }
    }

    public static Locate by(By by) {
        final Locate locate = new Locate();
        locate.by = by;
        return locate;
    }

    /**
     * Sets a global configurator callback for all created {@link Locate} instances
     */
    public static void setConfigurator(Consumer<Locate> callback) {
        Logger logger = LoggerFactory.getLogger(Locate.class);
        if (callback != null) {
            logger.info("Using global configurator");
        } else {
            logger.info("Unset global configurator");
        }
        locatorConfigurator = callback;
    }

    /**
     * Creates a copy of the given locate with new By
     */
    public static Locate by(By by, Locate locate) {
        final Locate newLocate = new Locate();
        newLocate.by = by;
        newLocate.filter = locate.filter;
        newLocate.unique = locate.unique;
        newLocate.preparedFormat = locate.preparedFormat;
        return newLocate;
    }

    public static Locate byQa(String qa) {
        return by(new By.ByXPath(String.format("//*[@data-qa='%s']", qa)));
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
     * Prepares a xpath selector
     * @param format
     * @return
     */
    public static Locate prepare(final String format) {
        final Locate locate = new Locate();
        locate.preparedFormat = format;
        return locate;
    }

    /**
     * Formats an xpath selector
     * @param args
     * @return
     */
    public Locate with(Object... args) {
        Locate locate = by(By.xpath(String.format(preparedFormat, args)));
        locate.unique = this.unique;
        locate.filter = this.filter;
        return locate;
    }
}
