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
package eu.tsystems.mms.tic.testframework.execution.testng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pele on 23.07.2015.
 */
public class ListenerUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerUtils.class);

    /**
     * Workaround for the testng listener implementation issue that calls each method multiple times. Why? Ask Mr.
     * Beust! To store every called method is not the best idea, since this list is filled for each thread by each used
     * listener method for every method called. There is no relevant point to reset this list in the middle of the test
     * process. But there is a need to call any method just once!
     * <p/>
     * Don't use this on configuration methods because they share a testResult!!! WTF? Ask Mr. Beust!
     */
    private static final ThreadLocal<List<String>> BAD_TESTNG_LISTENER_IMPL_WORKAROUND_LIST = new ThreadLocal<List<String>>();

    /**
     * Resets the workaround list.
     */
    @Deprecated
    // do not clean this list!
    private static synchronized void resetBadTestngListenerImplWorkaroundList() {
        BAD_TESTNG_LISTENER_IMPL_WORKAROUND_LIST.remove();
        BAD_TESTNG_LISTENER_IMPL_WORKAROUND_LIST.set(new ArrayList<String>());
    }

    /**
     * Checks if a method was already called. When is was called it returns true, false otherwise.
     *
     * @param methodName The method name that was called. Or any other index key.
     * @param objects Some object to build a hash string with. Shall be all parameters of the called method.
     * @return True if method was already called.
     */
    public static synchronized boolean wasMethodInvokedBefore(String methodName, Object... objects) {
        String hash = "" + methodName.hashCode();
        for (Object object : objects) {
            if (object != null) {
                hash += "_" + object.hashCode();
            }
        }

        if (hash.equals("" + methodName.hashCode())) {
            // exit if no valid object is given
            return false;
        }

        if (BAD_TESTNG_LISTENER_IMPL_WORKAROUND_LIST.get() == null) {
            resetBadTestngListenerImplWorkaroundList();
        }

        if (BAD_TESTNG_LISTENER_IMPL_WORKAROUND_LIST.get().contains(hash)) {
            LOGGER.trace("Method was invoked before: " + methodName);
            return true;
        }

        BAD_TESTNG_LISTENER_IMPL_WORKAROUND_LIST.get().add(hash);
        return false;
    }

}
