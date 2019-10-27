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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.creation;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Encapsulates a constructor of a page class.
 */
@Deprecated
public class PageInstantiator<T extends Page> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageInstantiator.class);

    private Constructor<T> constructor;

    private WebDriver driver;

    /**
     * Builds a page instantiator using the given constructor and driver.
     *
     * @param constructor The constructor to call on instantiation
     * @param driver The WebDriver to pass to the page
     */
    public PageInstantiator(final Constructor<T> constructor, final WebDriver driver) {
        this.constructor = constructor;
        this.driver = driver;
    }

    /**
     * Instantiates the page by calling the constructor and passing the given values to it. WebDriver is automatically
     * added as the first parameter.
     *
     * @param initArgs The args to pass to the constructor
     * @param <T> The class to be instantiated
     *
     * @return
     */
    public <T extends Page> T newInstance(Object... initArgs) {
        Object[] args = new Object[initArgs.length + 1];
        System.arraycopy(initArgs, 0, args, 1, initArgs.length);
        args[0] = driver;
        LOGGER.info(String.format("Instantiate class <%s> using parameters %s",
                constructor.getDeclaringClass().getSimpleName(), Arrays.toString(args)));
        try {
            return (T) constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            throw new TesterraRuntimeException(String.format("Error instantiating target class <%s>: %s. Expected parameter types are: %s. Used args are: %s",
                    constructor.getDeclaringClass().getSimpleName(), e.getMessage(), Arrays.toString(constructor.getParameterTypes()), Arrays.toString(args)), e);
        }
    }
}
