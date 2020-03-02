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
 *     Peter Lehmann
 *     pele
*/
package eu.tsystems.mms.tic.testframework.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public final class Counters {

    private static int methodExecutionCounter = 0;
    private static ThreadLocal<Integer> descriptedAsserts = new ThreadLocal<Integer>();
    private static ThreadLocal<Integer> undescriptedAsserts = new ThreadLocal<Integer>();
    private static ThreadLocal<List<String>> undescriptedAssertCallers = new ThreadLocal<List<String>>();
    private static int nrOfTestMethods = 0;

    private Counters() {
    }

    /**
     * increase number of test methods
     */
    public static void increaseNumberOfTestMethods() {
        nrOfTestMethods++;
    }

    /**
     * shows counted method executions
     *
     * @return .
     */
    public static synchronized int increaseMethodExecutionCounter() {
        methodExecutionCounter++;
        return methodExecutionCounter;
    }

    /**
     * This method increases the counter for TestNG Assert WITH description.
     * It is called from injected code in Testng Asserts class.
     * Injection happens via TesterraUtils triggered from TesterraListener.
     */
    public static void increaseDescriptedAsserts() {
        // check if the assert method was forwarded
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String find = "org.testng.Assert.assert";
        boolean find1 = false;
        boolean find2 = false;
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (stackTraceElement.toString().contains(find)) {
                if (!find1) {
                    find1 = true;
                }
                else {
                    find2 = true;
                    break;
                }
            }
        }

        if (find1 && !find2) {
            if (descriptedAsserts.get() == null) {
                descriptedAsserts.set(0);
            }
            descriptedAsserts.set(descriptedAsserts.get() + 1);
        }
        else {
            increaseUndescriptedAsserts();
        }
    }

    /**
     * This method increases the counter for TestNG Assert WITHOUT description.
     * It is called from injected code in Testng Asserts class.
     * Injection happens via TesterraUtils triggered from TesterraListener.
     */
    public static void increaseUndescriptedAsserts() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String find = "org.testng.Assert.assert";
        boolean find1 = false;
        int elementCounter = 0;
        for (StackTraceElement stackTraceElement : stackTrace) {
            elementCounter++;
            if (stackTraceElement.toString().contains(find)) {
                if (!find1) {
                    find1 = true;
                    break;
                }
            }
        }

        if (!find1) {
            return;
        }

        /*
        Find out source
         */
        StackTraceElement stackTraceElement = stackTrace[elementCounter];
        String assertCaller = stackTraceElement.toString();
        if (assertCaller.contains(find)) {
            // this is an internal call, so do nothing
            return;
        }

        /*
        Raise counter
         */
        if (undescriptedAsserts.get() == null) {
            undescriptedAsserts.set(0);
        }
        undescriptedAsserts.set(undescriptedAsserts.get() + 1);

        /*
        Store assert callers
         */
        if (undescriptedAssertCallers.get() == null) {
            undescriptedAssertCallers.set(new ArrayList<String>());
        }

        undescriptedAssertCallers.get().add(assertCaller);
    }

    /**
     * checks descripted asserts
     *
     * @return .
     */
    public static int getNumberOfDescriptedAsserts() {
        if (descriptedAsserts.get() == null) {
            return 0;
        }
        return descriptedAsserts.get();
    }

    /**
     * checks undescripted asserts
     *
     * @return .
     */
    public static int getNumberOfUndescriptedAsserts() {
        if (undescriptedAsserts.get() == null) {
            return 0;
        }
        return undescriptedAsserts.get();
    }

    /**
     * gets undescripted assert callers
     *
     * @return .
     */
    public static List<String> getUndescriptedAssertCallers() {
        if (undescriptedAssertCallers.get() == null) {
            return null;
        }
        return undescriptedAssertCallers.get();
    }

    /**
     * cleans all local threads
     */
    public static void cleanupThreadLocals() {
        descriptedAsserts.remove();
        undescriptedAsserts.remove();
        undescriptedAssertCallers.remove();
    }

}
