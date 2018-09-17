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

import java.lang.reflect.Method;

/**
 * Created by joku on 01.06.2016.
 * override and use as custom method wrapper
 */
public interface IDefaultMethodWrapper {
    void before(Object proxy, Method method, Object[] args);

    void after(Object proxy, Method method, Object[] args);

    void handleExceptions(Object proxy, Method method, Object[] args, Exception e);
}
