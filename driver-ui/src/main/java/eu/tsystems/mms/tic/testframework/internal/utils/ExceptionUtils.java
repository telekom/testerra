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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.internal.utils;

import eu.tsystems.mms.tic.testframework.pageobjects.AbstractPage;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextUtils;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;

public class ExceptionUtils extends CoreExceptionUtils {

    private static final String INIT_STRING = "<init>";

    public static String getPageContextFromThrowable(final Throwable throwable) {
        if (throwable == null) {
            return null;
        }

        String methodName = ExecutionContextUtils.getMethodNameFromCurrentTestResult();
        if (methodName == null) {
            return null;
        }

        /*
        at first find the method name in the stack trace
         */
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        int position = 0;
        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement stackTraceElement = stackTrace[i];
            if (methodName.equals(stackTraceElement.getMethodName())) {
                position = i;
                break;
            }
        }
        if (position == 0) {
            // method name not found
            return null;
        }

        /*
        Now search upwards for a page class call
         */
        position = findSubclassCallBackwards(stackTrace, position, AbstractPage.class, null);
        if (position == -1) {
            return null;
        }

        // get info
        StackTraceElement stackTraceElement = stackTrace[position];
        String simpleClassName = getSimpleNameFromClassString(stackTraceElement.getClassName());
        String actionName = stackTraceElement.getMethodName();

        /*
        in case of error in <init> of a page class, find out, if it was the checkPage
         */
//        if (INIT_STRING.equals(actionName)) {
//            position = findSubclassCallBackwards(stackTrace, position - 1, AbstractPage.class, Page.CHECKPAGE_METHOD_NAME);
//            if (position != -1) {
//                stackTraceElement = stackTrace[position];
//                actionName = stackTraceElement.getMethodName();
//                // do not overwrite the class here, it would be AbstractPage
//            }
//        }

        if (
            StringUtils.isStringEmpty(actionName)
            || actionName.equals(INIT_STRING)
        ) {
            return String.format("Construct %s", simpleClassName);
        } else {
            return String.format("%s.%s", simpleClassName, actionName);
        }
    }
}
