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
package eu.tsystems.mms.tic.testframework.core.playground.commons;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.utils.Synchronize;
import eu.tsystems.mms.tic.testframework.utils.TestUtils;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SynchronizeTest extends AbstractTest {

    Object LOCK = new Object();

    @Test
    public void testT01_d() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 1; i <= 10; i++) {
            final int nr = i;

            executorService.execute(() -> {
                try {
                    runSynchronized(nr);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.awaitTermination(20, TimeUnit.SECONDS);
    }

    private void runSynchronized(int nr) throws InterruptedException {
        System.out.println("thread add " + nr);
        Synchronize.d(LOCK, () -> {
            System.out.println("Running " + nr  + " -start");
            TestUtils.sleep(200);
            System.out.println("Running " + nr  + " -end");
        }, () -> {
            System.out.println("Waiting " + nr);
        }, 1000, 10000);

    }
}
