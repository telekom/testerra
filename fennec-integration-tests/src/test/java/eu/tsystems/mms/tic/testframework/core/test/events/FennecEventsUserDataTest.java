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
/*
 * Created on 07.01.14
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.test.events;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.common.FennecCommons;
import eu.tsystems.mms.tic.testframework.events.FennecEvent;
import eu.tsystems.mms.tic.testframework.events.FennecEventDataType;
import eu.tsystems.mms.tic.testframework.events.FennecEventService;
import eu.tsystems.mms.tic.testframework.events.FennecEventType;
import eu.tsystems.mms.tic.testframework.events.FennecEventUserDataManager;
import eu.tsystems.mms.tic.testframework.events.test.UserDataTypes;
import eu.tsystems.mms.tic.testframework.events.test.FennecEventUserDataTestListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * <Beschreibung der Klasse>
 * 
 * @author pele
 */
public class FennecEventsUserDataTest extends AbstractTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    FennecEventUserDataTestListener fennecEventListener = new FennecEventUserDataTestListener();

    @BeforeClass
    public void beforeClass() throws Exception {
        FennecEventService.addListener(fennecEventListener);
        FennecEventUserDataManager.getGlobalData().put(UserDataTypes.UMGEBUNG, "U1");
    }

    public void afterClass() {
        FennecEventService.removeListener(fennecEventListener);
    }

    /**
     *
     */
    @Test(groups = "T11_TestUnderTest")
    public void testT11_FennecUserEventData_TestUnderTest1() {
        FennecEventUserDataManager.getThreadLocalData().put(UserDataTypes.BLUBB, "BLA");
    }

    /**
     *
     */
    @Test(groups = "T11_TestUnderTest")
    public void testT11_FennecUserEventData_TestUnderTest2() {
        FennecEventUserDataManager.getThreadLocalData().put(UserDataTypes.BLUBB, "JUHU");
    }

    /**
     * T11_FennecUserEventData
     * <p/>
     * Description: T11 FennecUserEventData
     */
    @Test(dependsOnGroups = "T11_TestUnderTest")
    public void testT11_FennecUserEventData() {
        List<FennecEvent> eventsFromListener = FennecEventUserDataTestListener.getEvents();

        // copy
        List<FennecEvent> events = new ArrayList<FennecEvent>(eventsFromListener.size());
        for (FennecEvent fennecEvent : eventsFromListener) {
            events.add(fennecEvent);
        }

        boolean method1EventFired = false;
        boolean method2EventFired = false;
        for (FennecEvent event : events) {
            logger.info(event.getFennecEventType() + " Method: " + event.getData().get(FennecEventDataType.METHOD_NAME));

            Assert.assertTrue(event.getData().containsKey(UserDataTypes.UMGEBUNG),
                    "Every event contains UMGEBUNG data.");
            Assert.assertTrue("U1".equals(event.getData().get(UserDataTypes.UMGEBUNG)),
                    "Every event contains UMGEBUNG -U1- data.");

            if (event.getFennecEventType() == FennecEventType.TEST_METHOD_END) {
                String methodName = (String) event.getData().get(FennecEventDataType.METHOD_NAME);
                if (methodName.contains("testT11_FennecUserEventData_TestUnderTest1")) {
                    method1EventFired = true;
                    Assert.assertTrue(event.getData().containsKey(UserDataTypes.BLUBB),
                            "Event TEST_METHOD_END for testT11_FennecUserEventData_TestUnderTest1 contains BLUBB data.");
                    Assert.assertTrue("BLA".equals(event.getData().get(UserDataTypes.BLUBB)),
                            "Event TEST_METHOD_END for testT11_FennecUserEventData_TestUnderTest1 contains BLUBB -BLA- data.");
                    Assert.assertFalse("JUHU".equals(event.getData().get(UserDataTypes.BLUBB)),
                            "Event TEST_METHOD_END for testT11_FennecUserEventData_TestUnderTest1 contains BLUBB -JUHU- data.");
                }
                else if (methodName.contains("testT11_FennecUserEventData_TestUnderTest2")) {
                    method2EventFired = true;
                    Assert.assertTrue(event.getData().containsKey(UserDataTypes.BLUBB),
                            "Event TEST_METHOD_END for testT11_FennecUserEventData_TestUnderTest2 contains BLUBB data.");
                    Assert.assertTrue("JUHU".equals(event.getData().get(UserDataTypes.BLUBB)),
                            "Event TEST_METHOD_END for testT11_FennecUserEventData_TestUnderTest2 contains BLUBB -JUHU- data.");
                    Assert.assertFalse("BLA".equals(event.getData().get(UserDataTypes.BLUBB)),
                            "Event TEST_METHOD_END for testT11_FennecUserEventData_TestUnderTest2 contains BLUBB -BLA- data.");
                }
            }
        }

        Assert.assertTrue(method1EventFired, "Method1 event fired.");
        Assert.assertTrue(method2EventFired, "Method2 event fired.");
    }

}
