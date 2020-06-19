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
 package eu.tsystems.mms.tic.testframework.core.test.events;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.events.TesterraEvent;
import eu.tsystems.mms.tic.testframework.events.TesterraEventDataType;
import eu.tsystems.mms.tic.testframework.events.TesterraEventService;
import eu.tsystems.mms.tic.testframework.events.TesterraEventType;
import eu.tsystems.mms.tic.testframework.events.TesterraEventUserDataManager;
import eu.tsystems.mms.tic.testframework.core.test.events.implementation.TesterraEventUserDataTestListener;
import eu.tsystems.mms.tic.testframework.core.test.events.implementation.UserDataTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public class TesterraEventsUserDataTest extends AbstractWebDriverTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    TesterraEventUserDataTestListener testerraEventListener = new TesterraEventUserDataTestListener();

    @BeforeClass
    public void beforeClass() throws Exception {
        TesterraEventService.addListener(testerraEventListener);
        TesterraEventUserDataManager.getGlobalData().put(UserDataTypes.UMGEBUNG, "U1");
    }

    @AfterClass
    public void afterClass() {
        TesterraEventService.removeListener(testerraEventListener);
    }

    /**
     *
     */
    @Test
    public void testT11_TesterraUserEventData_TestUnderTest1() {
        TesterraEventUserDataManager.getThreadLocalData().put(UserDataTypes.BLUBB, "BLA");
    }

    /**
     *
     */
    @Test(dependsOnMethods = "testT11_TesterraUserEventData_TestUnderTest1")
    public void testT11_TesterraUserEventData_TestUnderTest2() {
        TesterraEventUserDataManager.getThreadLocalData().put(UserDataTypes.BLUBB, "JUHU");
    }

    /**
     * T11_TesterraUserEventData
     * <p/>
     * Description: T11 TesterraUserEventData
     */
    @Test(dependsOnMethods = "testT11_TesterraUserEventData_TestUnderTest2")
    public void testT11_TesterraUserEventData() {

        List<TesterraEvent> eventsFromListener = TesterraEventUserDataTestListener.getEvents();

        // copy
        List<TesterraEvent> events = new ArrayList<TesterraEvent>(eventsFromListener.size());
        for (TesterraEvent testerraEvent : eventsFromListener) {
            events.add(testerraEvent);
        }

        boolean method1EventFired = false;
        boolean method2EventFired = false;
        for (TesterraEvent event : events) {
            logger.info(event.getTesterraEventType() + " Method: " + event.getData().get(TesterraEventDataType.METHOD_NAME));

            logger.info(event.getData().toString());

            Assert.assertTrue(event.getData().containsKey(UserDataTypes.UMGEBUNG), "Every event contains UMGEBUNG data. Failed on Event: " + event.getTesterraEventType());
            Assert.assertEquals(event.getData().get(UserDataTypes.UMGEBUNG), "U1", "Every event contains UMGEBUNG -U1- data. Failed on Event: " + event.getTesterraEventType());

            if (event.getTesterraEventType() == TesterraEventType.TEST_METHOD_END) {
                String methodName = (String) event.getData().get(TesterraEventDataType.METHOD_NAME);
                if (methodName.contains("testT11_TesterraUserEventData_TestUnderTest1")) {
                    method1EventFired = true;
                    Assert.assertTrue(event.getData().containsKey(UserDataTypes.BLUBB),
                            "Event TEST_METHOD_END for testT11_TesterraUserEventData_TestUnderTest1 contains BLUBB data.");
                    Assert.assertTrue("BLA".equals(event.getData().get(UserDataTypes.BLUBB)),
                            "Event TEST_METHOD_END for testT11_TesterraUserEventData_TestUnderTest1 contains BLUBB -BLA- data.");
                    Assert.assertFalse("JUHU".equals(event.getData().get(UserDataTypes.BLUBB)),
                            "Event TEST_METHOD_END for testT11_TesterraUserEventData_TestUnderTest1 contains BLUBB -JUHU- data.");
                } else if (methodName.contains("testT11_TesterraUserEventData_TestUnderTest2")) {
                    method2EventFired = true;
                    Assert.assertTrue(event.getData().containsKey(UserDataTypes.BLUBB),
                            "Event TEST_METHOD_END for testT11_TesterraUserEventData_TestUnderTest2 contains BLUBB data.");
                    Assert.assertTrue("JUHU".equals(event.getData().get(UserDataTypes.BLUBB)),
                            "Event TEST_METHOD_END for testT11_TesterraUserEventData_TestUnderTest2 contains BLUBB -JUHU- data.");
                    Assert.assertFalse("BLA".equals(event.getData().get(UserDataTypes.BLUBB)),
                            "Event TEST_METHOD_END for testT11_TesterraUserEventData_TestUnderTest2 contains BLUBB -BLA- data.");
                }
            }
        }

        Assert.assertTrue(method1EventFired, "Method1 event fired.");
        Assert.assertTrue(method2EventFired, "Method2 event fired.");
    }

}
