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
 * Created on 08.11.2012
 *
 * Copyright(c) 2012 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.exceptions.FennecSystemException;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.Worker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.WorkerExecutor;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: pele Date: 08.11.12 Time: 12:36 To change this template use File | Settings | File
 * Templates.
 */
public final class FennecUtils {

    private static boolean assertsPrepared = false;

    private FennecUtils() {
    }

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FennecUtils.class);

    /**
     * Modifies the Asserter classes of the test frameworks for the use with non-functional tests. Call this method
     * before the classloader catched the original classes.
     */
    public static void prepareAsserts() {
        if (assertsPrepared) {
            return;
        }

        ClassPool classPool = ClassPool.getDefault();
        classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));

        try {
            /*
            !! DO NOT change the string values into class references!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
             */

            final CtClass testNgAssert = classPool.getCtClass("org.testng.Assert");
            CtMethod[] testNGAssertMethods = testNgAssert.getDeclaredMethods();

            /*
             * Pitch original assert methods
             */
            for (CtMethod testNGAssertMethod : testNGAssertMethods) {
                /*
                 * Pitching Testng assert
                 */
                if (testNGAssertMethod.getName().startsWith("assert")) {
                    LOGGER.trace("Patching method: " + testNgAssert.getSimpleName() + "."
                            + testNGAssertMethod.getName());
                    /*
                     * Hack this method;
                     */
                    if (hasDescription(testNGAssertMethod)) {
                        testNGAssertMethod.insertBefore("{eu.tsystems.mms.tic.testframework." +
                                        "internal.Counters.increaseDescriptedAsserts();}");
                    } else {
                        testNGAssertMethod.insertBefore("{eu.tsystems.mms.tic.testframework." +
                                        "internal.Counters.increaseUndescriptedAsserts();}");
                    }
                }
            }
        } catch (NotFoundException e) {
            LOGGER.error("fennec class patching not possible", e);
        } catch (CannotCompileException e) {
            throw new FennecSystemException(e);
        }

        assertsPrepared = true;
    }

    private static boolean hasDescription(CtMethod testNGAssertMethod) {
        try {
            CtClass[] parameterTypes = testNGAssertMethod.getParameterTypes();
            if ("java.lang.String".equals(parameterTypes[parameterTypes.length - 1].getName())
                    && !"java.lang.String".equals(parameterTypes[0].getName())) {
                /*
                 * if the last parameter is a string, while the first parameter isn not
                 */
                return true;
            }
            else if ("java.lang.String".equals(parameterTypes[0].getName()) && parameterTypes.length > 2) {
                /*
                for the case (String, String, String)
                 */
                return true;
            }
        } catch (NotFoundException e) {
            return false;
        }
        return false;
    }

    /**
     * checks the loaded classes
     *
     * @param packageAndClazz .
     * @param classLoader .
     * @return .
     * @throws NoSuchMethodException .
     * @throws IllegalAccessException .
     * @throws InvocationTargetException .
     */
    public static Object getLoadedClass(String packageAndClazz, ClassLoader classLoader) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        Method m = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
        m.setAccessible(true);
        return m.invoke(classLoader, packageAndClazz);
    }

    public static void addWorkersToExecutor(List<Class<? extends Worker>> workerList, WorkerExecutor workerExecutor) {
        for (Class<? extends Worker> workerClass : workerList) {
            try {
                Constructor<? extends Worker> constructor = workerClass.getConstructor();
                Worker worker = constructor.newInstance();
                workerExecutor.add(worker);
            } catch (Exception e) {
                LOGGER.error("Error using " + workerClass.getClass().getSimpleName(), e);
            }
        }
    }

}
