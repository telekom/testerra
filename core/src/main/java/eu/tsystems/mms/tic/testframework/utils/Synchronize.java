/*
 * Testerra
 *
 * (C) 2020,  Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class Synchronize {

    private static final Logger LOGGER = LoggerFactory.getLogger(Synchronize.class);

    private static final Object locksObjectLocker = new Object();
    private static final Object runsObjectLocker = new Object();
    private static final Map<Object, Queue<Runnable>> locks = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Object, Runnable> runs = Collections.synchronizedMap(new HashMap<>());

    public static void d(final Object lockObject, Runnable runLocked, Runnable runIntermediate, int sleepTimeMS, int timeoutMS) throws InterruptedException {
        synchronized (locksObjectLocker) {
            // init
            if (!locks.containsKey(lockObject)) {
//                LOGGER.info("Init Queue for " + lockObject);
                locks.put(lockObject, new LinkedList<>());

//                LOGGER.info("NEXT: " + lockObject + " - " + runLocked);
                runs.put(lockObject, runLocked);
            }
            else {
                // add request
                locks.get(lockObject).add(runLocked);
//                LOGGER.info("Queue entry added: " + lockObject + " - " + runLocked);
            }
        }

        /*
        Wait for lock
         */
        int timeoutCounter = 0;
        while (!isMyTurn(lockObject, runLocked)) {
            if (timeoutCounter + sleepTimeMS > timeoutMS) {
                throw new TimeoutException("Synchronize.d WAITING for lock timed out " + timeoutMS + " ms");
            }
            Thread.sleep(sleepTimeMS);
            timeoutCounter += sleepTimeMS;
            runIntermediate.run();
        }

        /*
        execute
         */
        try {
            runs.get(lockObject).run();
        } finally {
            /*
            release
             */
            next(lockObject);
        }
    }

    private static boolean isMyTurn(Object lockObject, Runnable runLocked) {
        synchronized (runsObjectLocker) {
            if (runs.containsKey(lockObject) && runs.get(lockObject) == runLocked) {
//                LOGGER.info(lockObject + " - " + runLocked + " - MY TURN");
                return true;
            }
        }
//        LOGGER.info(lockObject + " - " + runLocked + " - not my turn");
        return false;
    }

    private static void next(Object lockObject) {
        synchronized (locksObjectLocker) {
            Queue<Runnable> queue = locks.get(lockObject);
//            LOGGER.info("Queue " + lockObject + " has " + queue.size() + " entries");
            if (queue.isEmpty()) {
                locks.remove(lockObject);
                runs.remove(lockObject);
//                LOGGER.info("Removing Queue for " + lockObject);
            }
            else {
                Runnable runnable = queue.poll();
                runs.put(lockObject, runnable);
//                LOGGER.info("NEXT: " + lockObject + " - " + runnable);
            }
        }
    }

}
