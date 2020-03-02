/*
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

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import org.openqa.selenium.WebElement;

/**
 * Created by rnhb on 23.07.2015.
 */
public abstract class WebElementFilter {

    public static final Enabled ENABLED = new Enabled();
    public static final Selected SELECTED = new Selected();
    public static final Tag TAG = new Tag();
    public static final Size SIZE = new Size();
    public static final Text TEXT = new Text();
    public static final Css CSS = new Css();
    public static final Displayed DISPLAYED = new Displayed();
    public static final Attribute ATTRIBUTE = new Attribute();
    public static final Position POSITION = new Position();

    public static final Child CHILD = new Child();

    final static String STD_ERROR_MSG = "Use the filter properties like is() on your WebElementFilter!";

    public abstract boolean isSatisfiedBy(WebElement webElement);

    void checkCorrectUsage(final String helpText, Object... objectsToBeNotNull) {
        for (Object o : objectsToBeNotNull) {
            if (o == null) {
                throw new TesterraSystemException("Error using WebElementFilter.\n" + helpText);
            }
        }
    }
}
