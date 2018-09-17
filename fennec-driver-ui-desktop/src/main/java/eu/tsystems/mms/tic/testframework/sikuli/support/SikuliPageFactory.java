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

import eu.tsystems.mms.tic.testframework.sikuli.SikuliFirefoxDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 *
 */
public final class SikuliPageFactory {

    private SikuliPageFactory() {

    }
    /**
     * initialize elements
     *
     * @param driver .
     * @param pageClassToProxy .
     * @param <T> .
     * @return page
     */
    public static <T> T initElements(SikuliFirefoxDriver driver, Class<T> pageClassToProxy) {
        T page = instantiatePage(driver, pageClassToProxy);
        initElements(driver, page);
        return page;
    }

    /**
     * initialize elements on object page
     *
     * @param driverRef .
     * @param page .
     */
    public static void initElements(SikuliFirefoxDriver driverRef, Object page) {
        initElements(new DefaultImageElementLocatorFactory(driverRef), page);
    }

    static void initElements(ImageElementLocatorFactory factoryRef, Object page) {
        PageFactory.initElements(new ImageElementFieldDecorator(factoryRef), page);
    }


    private static <T> T instantiatePage(WebDriver driver, Class<T> pageClassToProxy) {
        try {
            try {
                Constructor<T> constructor = pageClassToProxy.getConstructor(WebDriver.class);
                return constructor.newInstance(driver);
            } catch (NoSuchMethodException e) {
                return pageClassToProxy.newInstance();
            }
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
