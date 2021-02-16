/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.test.events;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.events.InterceptMethodsEvent;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.events.MethodStartEvent;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class EventListenerTest extends TesterraTest {

    public class MyListener implements
            MethodStartEvent.Listener,
            MethodEndEvent.Listener,
            InterceptMethodsEvent.Listener
    {

        public MethodStartEvent startEvent;
        public MethodEndEvent endEvent;

        @Override
        @Subscribe
        public void onMethodEnd(MethodEndEvent event) {
            endEvent = event;
        }

        @Override
        @Subscribe
        public void onMethodStart(MethodStartEvent event) {
            startEvent = event;
        }

        @Override
        @Subscribe
        public void onInterceptMethods(InterceptMethodsEvent event) {
            event.getMethodInstances().removeIf(iMethodInstance -> iMethodInstance.getMethod().getMethodName().equals("testToBeIgnored"));
        }
    }

    private MyListener listener;

    @BeforeTest
    public void setupListener() {
        listener = new MyListener();
        Testerra.getEventBus().register(listener);
    }

    @Test
    public void testToBeIgnored() {
        Assert.assertTrue(false, "This test should be filtered and never be called");
    }

    @Test
    public void testCalled() {
        // I just do nothing, sorry
    }

    @AfterClass
    public void checkListener() {
        Assert.assertNotNull(listener);
        Assert.assertNotNull(listener.startEvent);
        Assert.assertNotNull(listener.endEvent);
        Testerra.getEventBus().unregister(listener);
    }
}
