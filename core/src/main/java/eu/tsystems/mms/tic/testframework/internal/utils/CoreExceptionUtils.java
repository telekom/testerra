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
package eu.tsystems.mms.tic.testframework.internal.utils;

import eu.tsystems.mms.tic.testframework.utils.ThrowableUtils;

import java.lang.reflect.Modifier;

/**
 * Created by piet on 11.03.16.
 */
public class CoreExceptionUtils extends ThrowableUtils {

    public static String getSimpleNameFromClassString(final String fullClassName) {
        String[] split = fullClassName.split("\\.");
        if (split.length <= 1) {
            return fullClassName;
        }
        return split[split.length - 1];
    }

    public static int findSubclassCallBackwards(StackTraceElement[] stackTrace, int from, Class baseClass, final String methodNameToLookFor) {
        for (int i = from; i >= 0; i--) {
            StackTraceElement stackTraceElement = stackTrace[i];
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();

            boolean foundClass = baseClass == null;
            boolean foundMethod = methodNameToLookFor == null;

            // search class
            if (!foundClass) {
                try {
                    Class<?> classToTest = Class.forName(className);
                    int modifiers = classToTest.getModifiers();
                    // We only want non abstract public implementations
                    foundClass = (
                        !Modifier.isAbstract(modifiers)
                        && baseClass.isAssignableFrom(classToTest)
                        && Modifier.isPublic(modifiers)
                    );
                } catch (ClassNotFoundException e) {
                    // ignore
                }
            }

            // search method
            if (foundClass && !foundMethod) {
                if (methodNameToLookFor.equals(methodName)) {
                    foundMethod = true;
                }
            }

            // found everything?
            if (foundClass && foundMethod) {
                return i;
            }
        }
        return -1;
    }
}
