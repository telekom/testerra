/*
 * Testerra
 *
 * (C) 2021, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.execution.testng.worker.start;

import eu.tsystems.mms.tic.testframework.events.InterceptMethodsEvent;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import java.util.Comparator;
import org.testng.annotations.Test;

/**
 * Sorts all methods by {@link Test#priority()}
 * <p>
 * Date: 28.01.2021
 * Time: 08:03
 *
 * @author Eric Kubenka
 */
public class SortMethodsByPriorityMethodInterceptor implements Loggable, InterceptMethodsEvent.Listener {

    @Override
    public void onInterceptMethods(InterceptMethodsEvent event) {
        event.getMethodInstances().sort(Comparator.comparingInt(m -> m.getMethod().getPriority()));
    }
}
