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
 package eu.tsystems.mms.tic.testframework.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

public final class ObjectUtils {

    public static boolean isNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return true;
            }
        }
        return false;
    }

    public static boolean oneOfTypes(Object value, Class... classes) {
        Class<?> valueClass = value.getClass();
        for (Class aClass : classes) {
            if (aClass.isAssignableFrom(valueClass)) {
                return true;
            }
        }
        return false;
    }

    public static <T> T simpleProxy(Class<? extends T> iface, InvocationHandler handler, Class<?>...otherIfaces) {
        Class<?>[] allInterfaces = Stream.concat(
                Stream.of(iface),
                Stream.of(otherIfaces))
                .distinct()
                .toArray(Class<?>[]::new);

        return (T) Proxy.newProxyInstance(
                iface.getClassLoader(),
                allInterfaces,
                handler);
    }

    /**
     * @deprecated Use {@link #simpleProxy(Class, InvocationHandler, Class[])} instead
     */
    public static <T> T simpleProxy(Class<? extends T> iface, T target, Class<? extends PassThroughProxy> handler, Class<?>...otherIfaces) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        InvocationHandler handlerInstance = handler.getConstructor(iface).newInstance(target);
        return simpleProxy(iface, handlerInstance, otherIfaces);
    }

    public static abstract class PassThroughProxy<T> implements InvocationHandler {
        protected final T target;

        public PassThroughProxy(T target) {
            this.target = target;
        }

        public Object invoke(Method method, Object[] args) throws Throwable {
            try {
                return method.invoke(target, args);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                throw e;
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }

    public static Class[] getAllInterfacesOf(Object o) {
        List<Class> interfaces = new ArrayList<>();
        interfaces.addAll(Arrays.asList(o.getClass().getInterfaces()));

        Class<?> superclass = o.getClass().getSuperclass();
        while (superclass != null) {
            interfaces.addAll(Arrays.asList(superclass.getInterfaces()));
            superclass = superclass.getSuperclass();
        }

        // remove duplicates
        interfaces = new ArrayList<>(new HashSet<>(interfaces));

        return interfaces.toArray(new Class[0]);
    }
}
