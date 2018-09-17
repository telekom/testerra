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
package eu.tsystems.mms.tic.testframework.dynamicproxy;

import eu.tsystems.mms.tic.testframework.exceptions.FennecSystemException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by joku on 17.11.2016.
 */
class DefaultWrappingProxy extends Proxy {

    protected IDefaultMethodWrapper wrapper;

    protected DefaultWrappingProxy(Object object, IDefaultMethodWrapper wrapper) {
        super(object);
        this.wrapper = wrapper;
    }

    public static <T> T proxify(T obj, IDefaultMethodWrapper wrapper) {
        return new DefaultWrappingProxy(obj, wrapper).internalProxify();
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        try {
            wrapper.before(proxy, method, args);
            result = method.invoke(unProxiedObject, args);
        } catch (InvocationTargetException e) {
            throw new FennecSystemException("should never happen, design flaw! ", e);
        } catch (Exception specificException) {
            wrapper.handleExceptions(proxy, method, args, specificException);
        } finally {
            wrapper.after(proxy, method, args);
        }
        return result;
    }
}
