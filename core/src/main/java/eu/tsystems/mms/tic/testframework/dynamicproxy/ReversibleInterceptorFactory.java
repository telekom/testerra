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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by joku on 17.11.2016.
 * see @ReversibleProxy
 * adds storage logic for wrapped objects.
 * you can now get the unwrapped object of any wrapped object that was created with this factory.
 */
public final class ReversibleInterceptorFactory<T> {
    protected final Map<T, ReversibleInterceptor<T>> proxied;

    public ReversibleInterceptorFactory() {
        proxied = new HashMap<>();
    }

    public boolean isProxied(Object candidate) {
        return java.lang.reflect.Proxy.isProxyClass(candidate.getClass());
    }

    public T getUnproxied(Object proxiedObject) {
        ReversibleInterceptor<T> proxy = proxied.get(proxiedObject);
        return (proxy == null) ? (null) : proxy.getUnproxiedObject();
    }

    public T proxify(T unproxiedObject, IDefaultMethodWrapper wrapper) {
        ReversibleInterceptor<T> proxy = new ReversibleInterceptor<>(unproxiedObject, wrapper);
        T proxiedObject = proxy.newProxiedObject();
        proxied.put(proxiedObject, proxy);
        return proxiedObject;
    }
}
