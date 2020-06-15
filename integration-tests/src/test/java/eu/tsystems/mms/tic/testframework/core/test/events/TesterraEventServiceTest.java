/*
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
 *     Eric Kubenka
 */
package eu.tsystems.mms.tic.testframework.core.test.events;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.events.TesterraEventService;
import eu.tsystems.mms.tic.testframework.core.test.events.implementation.BasicEventListener;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TesterraEventService Licecycle tests
 *
 * @author Eric Kubenka
 */
public class TesterraEventServiceTest extends AbstractWebDriverTest implements Loggable {

    @Test
    public void testT01_TesterraEventListenerLifeCycle() {

        // add listener
        BasicEventListener basicEventListener = new BasicEventListener();
        TesterraEventService.addListener(basicEventListener);

        // assert its registered
        final boolean hasListener = TesterraEventService.hasListenerOfThisType(basicEventListener);
        Assert.assertTrue(hasListener, "Listener successfully added.");

        // remove
        TesterraEventService.removeListener(basicEventListener);
        final boolean hasListenerAfterRemove = TesterraEventService.hasListenerOfThisType(basicEventListener);
        Assert.assertFalse(hasListenerAfterRemove, "Listener successfully added.");
    }

}
