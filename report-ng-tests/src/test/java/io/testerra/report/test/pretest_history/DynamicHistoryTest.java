/*
 * Testerra
 *
 * (C) 2025, Tobias Adler, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

package io.testerra.report.test.pretest_history;

import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import org.testng.annotations.Test;

import static eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider.PROPERTY_MANAGER;

public class DynamicHistoryTest extends TesterraTest implements AssertProvider {

    @Test
    public void test_highFlakiness() {
        int historyIndex = Math.toIntExact(PROPERTY_MANAGER.getLongProperty("history.index"));
        System.out.println(historyIndex);
        boolean x = (historyIndex % 2 != 0);
        ASSERT.assertTrue(x);
    }

    @Test
    public void test_passedToFailed() {
        int historyIndex = Math.toIntExact(PROPERTY_MANAGER.getLongProperty("history.index"));
        ASSERT.assertTrue(historyIndex <= 5);
    }

    @Test
    public void test_longDuration() {
        int historyIndex = Math.toIntExact(PROPERTY_MANAGER.getLongProperty("history.index"));
        switch (historyIndex) {
            case 5:
                TimerUtils.sleep(3000);
                break;
            case 10:
                TimerUtils.sleep(5000);
                break;
            default:
                TimerUtils.sleep(2000);
                break;
        }
        ASSERT.assertTrue(true);
    }

    @Test
    public void test_multipleFailureAspects() {
        int historyIndex = Math.toIntExact(PROPERTY_MANAGER.getLongProperty("history.index"));
        switch (historyIndex) {
            case 5:
                ASSERT.assertTrue(false);
                break;
            case 7:
            case 10:
                ASSERT.assertEquals(42, 7);
                break;
            default:
                ASSERT.assertEquals(1, 2);
                break;
        }
    }

    @Test
    public void test_addedLater() {
        ASSERT.assertTrue(true);
    }

    @Test
    public void test_removedLater() {
        ASSERT.assertTrue(true);
    }
}
