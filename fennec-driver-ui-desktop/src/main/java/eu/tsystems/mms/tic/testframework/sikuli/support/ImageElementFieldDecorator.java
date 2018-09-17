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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

import java.lang.reflect.*;
import java.util.List;

/**
 * image element field decorator
 */
class ImageElementFieldDecorator extends DefaultFieldDecorator implements FieldDecorator {

    private final ImageElementLocatorFactory factory;

    public ImageElementFieldDecorator(ImageElementLocatorFactory factoryRef) {
        super(factoryRef);
        this.factory = factoryRef;
    }

    @Override
    public Object decorate(ClassLoader loader, Field field) {
        if (WebElement.class.isAssignableFrom(field.getType())) {
            return super.decorate(loader, field);
        } else {
            return decorateAsImageElement(loader, field);
        }
    }

    private Object decorateAsImageElement(ClassLoader loader, Field field) {
        if (!(ImageElement.class.isAssignableFrom(field.getType())
                || isDecoratableList(field))) {
            return null;
        }

        ImageElementLocator locator = factory.createImageElementLocator(field);
        if (locator == null) {
            return null;
        }

        if (ImageElement.class.isAssignableFrom(field.getType())) {
            return proxyForImageElementLocator(loader, locator);
        } else if (List.class.isAssignableFrom(field.getType())) {
            return proxyForImageElementListLocator(loader, locator);
        } else {
            return null;
        }
    }

    protected boolean isDecoratableList(Field field) {
        if (!List.class.isAssignableFrom(field.getType())) {
            return false;
        }

        // Type erasure in Java isn't complete. Attempt to discover the generic
        // type of the list.
        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            return false;
        }

        Type listType = ((ParameterizedType) genericType).getActualTypeArguments()[0];

        if (!ImageElement.class.equals(listType)) {
            return false;
        }


        //	if (field.getAnnotation(FindBy.class) == null && field.getAnnotation(FindBys.class) == null)
        // {return false;}

        if (field.getAnnotation(FindBy.class) == null) {
            return false;
        }
        return true;
    }

    protected ImageElement proxyForImageElementLocator(ClassLoader loader, ImageElementLocator locator) {
        InvocationHandler handler = new LocatingImageElementHandler(locator);

        ImageElement proxy;
        //proxy = (ImageElement) Proxy.newProxyInstance(
        //loader, new Class[] {ImageElement.class, WrapsElement.class, Locatable.class}, handler);
        proxy = (ImageElement) Proxy.newProxyInstance(
                loader, new Class[]{ImageElement.class}, handler);

        return proxy;
    }

    @SuppressWarnings("unchecked")
    protected List<ImageElement> proxyForImageElementListLocator(ClassLoader loader, ImageElementLocator locator) {
        InvocationHandler handler = new LocatingImageElementListHandler(locator);

        List<ImageElement> proxy;
        proxy = (List<ImageElement>) Proxy.newProxyInstance(
                loader, new Class[]{List.class}, handler);
        return proxy;
    }
}
