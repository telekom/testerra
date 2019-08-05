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

/**
 * Created by joku on 17.11.2016.
 */
public final class ReversibleInterceptor<T> extends ReversibleProxy<T> {
    /**
     * For tt. Users / Devs
     * Wraps an Object with an interceptor. All methods are now wrapped with overridable "before" "after" and "handle exceptions" methods.
     * This means: those methods (before, after, handleExceptions) will be executed for each method of wrapped object.
     * As the naming suggests: before each method is called, after each method is called, and when any method of the wrapped Object throws an Exception.
     * usage:
     * <p>
     * first wrap the object:
     * <p>
     * myObject object = new myObject();
     * myObject wrappedObject = new TesterraReversibleInterceptor<myObject>(
     * object,
     * new IDefaultMethodWrapper() {
     * \@Override
     * public void before(Object proxy, Method method, Object[] args) {
     * <p>
     * }
     * \@Override
     * public void after(Object proxy, Method method, Object[] args) {
     * System.out.println("method " + method + "of " + myObject + " was called");
     * }
     * \@Override
     * public void handleExceptions(Object proxy, Method method, Object[] args, Exception e) {
     * <p>
     * }
     * }
     * )
     * .newProxiedObject();
     * <p>
     * then call your method on the wrapped object:
     * <p>
     * wrappedObject().yourMethod();
     * <p>
     * this would print something similar to 'method "yourMethod" of "myObject#3" was called.' before yourMethod() is executed.
     * after and handleExceptions work accordingly, as the names suggest.
     *
     * @param object  object to be wrapped. original object wont change.
     * @param wrapper custom before calls, after calls and exception handling for your desired Method
     */
    public ReversibleInterceptor(T object, IDefaultMethodWrapper wrapper) {
        super(object, wrapper);
    }
}
