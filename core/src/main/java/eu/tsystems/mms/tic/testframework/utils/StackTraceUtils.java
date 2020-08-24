/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility Methods to support searching in StackStraces related to Selenium and the logging extension.
 *
 * @author mibu
 */
public final class StackTraceUtils {

    /**
     * For the string representation of calling code.
     */
    public static final String LINE_NUMBER_SEPARATOR = "#";

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StackTraceUtils.class);

    /**
     * Private constructor to hide the public one since this a static only class.
     */
    private StackTraceUtils() {
    }

    /**
     * Prints the current stack trace to <code>System.err</code>.
     */
    public static void debugStackTrace() {
        final StackTraceElement[] testElements = Thread.currentThread().getStackTrace();
        for (final StackTraceElement stackTraceElement : testElements) {
            LOGGER.debug(getClassNameAndLineNumber(stackTraceElement));
        }
    }

    /**
     * Generates a string representation of the given stack trace element containing the class name and the line number.
     *
     * @param stackTraceElement The stack trace element.
     *
     * @return string A String representation of the stack trace element containing the class name and the line number.
     */
    public static String getClassNameAndLineNumber(final StackTraceElement stackTraceElement) {
        if (null != stackTraceElement) {
            return stackTraceElement.getClassName() + LINE_NUMBER_SEPARATOR + stackTraceElement.getLineNumber();
        } else {
            return "Internal ERROR stackTraceElement should not be null";
        }
    }

    /**
     * Determines whether the class name of the stack trace element contains the <code>wantedClassName</code>.
     *
     * @param stackTraceElement The stack trace element.
     *
     * @param wantedClassName The class to be contained.
     *
     * @return True, if <code>wantedClassName</code> is contained in <code>stackTraceElement</code>s class name.
     */
    public static boolean isClassName(final StackTraceElement stackTraceElement, final String wantedClassName) {
        final String className = stackTraceElement.getClassName();
        return className.contains(wantedClassName);
    }

    /**
     * Determines the current calling class by searching the given stack trace for the first element after
     * <code>preecedingClassName</code>.
     *
     * @param testElements The stack trace array to be searched in.
     *
     * @return The element following <code>preceedingClassName</code> or <code>preceedingClassName</code> if no further
     *         element left. <code>null</code> if <code>preceedingClassName</code> wasn't there.
     */
    public static StackTraceElement getCurrentCallingClassAsStackTraceElement(final StackTraceElement[] testElements) {
        StackTraceElement currentCallingClassAsStackTraceElement = null;

        for (int i = 0; i < testElements.length; i++) {
            if (StackTraceUtils.isClassName(testElements[i], "NativeMethodAccessorImpl")) {
                currentCallingClassAsStackTraceElement = testElements[i - 1];
                break;
            }
        }
        return currentCallingClassAsStackTraceElement;
    }

    /**
     * Determines whether <code>className</code> is present in the given stack trace array.
     *
     * @param testElements The stack trace array to be searched in.
     *
     * @param className The class name to be searched for.
     *
     * @return True, if the class name was seen in the stack trace, false otherwise.
     */
    public static boolean isClassInStackTrace(final StackTraceElement[] testElements, final String className) {
        boolean result = false;
        for (final StackTraceElement stackTraceElement : testElements) {
            if (stackTraceElement.getClassName().endsWith(className)) {
                result = true;
            }
        }
        return result;
    }
}
