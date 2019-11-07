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
package eu.tsystems.mms.tic.testframework.pageobjects.filter;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by rnhb on 10.03.2016.
 *
 * Quick Solution - check for more elegant solution later.
 * I didn't want to touch the existing WebElementFilter classes, but not create explicit classes for every filter type.
 *
 */
public class Child {

    public static final Enabled ENABLED = new Enabled() {
        @Override
        public boolean isSatisfiedBy(WebElement webElement) {
            List<WebElement> children = webElement.findElements(By.xpath(".//*"));
            for (WebElement child : children) {
                boolean satisfiedByChild = super.isSatisfiedBy(child);
                if (!satisfiedByChild) {
                    return false;
                }
            }
            return true;
        }
    };

    public static final Selected SELECTED = new Selected() {
        @Override
        public boolean isSatisfiedBy(WebElement webElement) {
            List<WebElement> children = webElement.findElements(By.xpath(".//*"));
            for (WebElement child : children) {
                boolean satisfiedByChild = super.isSatisfiedBy(child);
                if (!satisfiedByChild) {
                    return false;
                }
            }
            return true;
        }
    };

    public static final Tag TAG = new Tag() {
        @Override
        public boolean isSatisfiedBy(WebElement webElement) {
            List<WebElement> children = webElement.findElements(By.xpath(".//*"));
            for (WebElement child : children) {
                boolean satisfiedByChild = super.isSatisfiedBy(child);
                if (!satisfiedByChild) {
                    return false;
                }
            }
            return true;
        }
    };

    public static final Size SIZE = new Size() {
        @Override
        public boolean isSatisfiedBy(WebElement webElement) {
            List<WebElement> children = webElement.findElements(By.xpath(".//*"));
            for (WebElement child : children) {
                boolean satisfiedByChild = super.isSatisfiedBy(child);
                if (!satisfiedByChild) {
                    return false;
                }
            }
            return true;
        }
    };

    public static final Text TEXT =  new Text() {
        @Override
        public boolean isSatisfiedBy(WebElement webElement) {
            List<WebElement> children = webElement.findElements(By.xpath(".//*"));
            for (WebElement child : children) {
                boolean satisfiedByChild = super.isSatisfiedBy(child);
                if (!satisfiedByChild) {
                    return false;
                }
            }
            return true;
        }
    };

    public static final Css CSS =  new Css() {
        @Override
        public boolean isSatisfiedBy(WebElement webElement) {
            List<WebElement> children = webElement.findElements(By.xpath(".//*"));
            for (WebElement child : children) {
                boolean satisfiedByChild = super.isSatisfiedBy(child);
                if (!satisfiedByChild) {
                    return false;
                }
            }
            return true;
        }
    };

    public static final Displayed DISPLAYED =  new Displayed() {
        @Override
        public boolean isSatisfiedBy(WebElement webElement) {
            List<WebElement> children = webElement.findElements(By.xpath(".//*"));
            for (WebElement child : children) {
                boolean satisfiedByChild = super.isSatisfiedBy(child);
                if (!satisfiedByChild) {
                    return false;
                }
            }
            return true;
        }
    };

    public static final Attribute ATTRIBUTE =  new Attribute() {
        @Override
        public boolean isSatisfiedBy(WebElement webElement) {
            List<WebElement> children = webElement.findElements(By.xpath(".//*"));
            for (WebElement child : children) {
                boolean satisfiedByChild = super.isSatisfiedBy(child);
                if (!satisfiedByChild) {
                    return false;
                }
            }
            return true;
        }
    };

    public static final Position POSITION =  new Position() {
        @Override
        public boolean isSatisfiedBy(WebElement webElement) {
            List<WebElement> children = webElement.findElements(By.xpath(".//*"));
            for (WebElement child : children) {
                boolean satisfiedByChild = super.isSatisfiedBy(child);
                if (!satisfiedByChild) {
                    return false;
                }
            }
            return true;
        }
    };
}
