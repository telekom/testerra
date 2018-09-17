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
package eu.tsystems.mms.tic.testframework.sikuli.support;

import eu.tsystems.mms.tic.testframework.sikuli.ImageElement;
import eu.tsystems.mms.tic.testframework.sikuli.SikuliFirefoxDriver;
import org.openqa.selenium.support.pagefactory.DefaultElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * default image element locator with implemented image element locator, element locator and extending default element
 * locator
 */
public class DefaultImageElementLocator extends DefaultElementLocator implements ImageElementLocator, ElementLocator {

    private final SikuliFirefoxDriver driver;
    private final FindByImage findBy;

    /**
     * default image element locator
     *
     * @param driverRef .
     * @param field .
     */
    public DefaultImageElementLocator(SikuliFirefoxDriver driverRef, Field field) {
        super(driverRef, field);
        findBy = field.getAnnotation(FindByImage.class);
        driver = driverRef;
    }

    @Override
    public ImageElement findImageElement() {
        ImageElement element;
        try {
            element = driver.findImageElement(new URL(findBy.url()));
        } catch (MalformedURLException e) {
            return null;
        }
        return element;
    }

    @Override
    public List<ImageElement> findImageElements() {
        throw new UnsupportedOperationException();
    }

}
