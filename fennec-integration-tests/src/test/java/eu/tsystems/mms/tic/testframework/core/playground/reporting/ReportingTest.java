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
 * Created on 07.03.14
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.reporting;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.info.ReportInfo;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public class ReportingTest extends AbstractTest {

    /*
     * AnnotationTransformerListener only works with global listener. So execute the AnnotationTransformerListener.xml
     * from resources.
     */

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private int run = 0;

    /**
     * FailFirstPassSecond
     * <p/>
     * Description: FailFirstPassSecond
     */
    @Test
    public void testT01FailFirstPassSecond() {
        run++;

        if (run == 1) {
            logger.info("Failing");
            throw new WebDriverException("Timed out waiting for action to finish");
        }

        logger.info("Passing");
    }

    /**
     * T02MethodInBetween
     * <p/>
     * Description: T02MethodInBetween
     */
    @Test
    public void testT02MethodInBetweenPASS() {
        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();

        methodContext.infos.add("quite long infooooooo");
        methodContext.infos.add("even longer iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiinfooooooo");

        Assert.assertTrue(true);
    }

    @Test
    public void testMethodWithInfos() throws Exception {
        ReportInfo.getRunInfo().addInfo("runKey1", "runValue1");
        ReportInfo.getRunInfo().addInfo("runKey2", "runValue0");
        ReportInfo.getRunInfo().addInfo("runKey2", "runValue2");

        ReportInfo.getCurrentMethodInfo().addInfo("methodKey1", "methodValue1");
        ReportInfo.getCurrentMethodInfo().addInfo("methodKey2", "methodValue0");
        ReportInfo.getCurrentMethodInfo().addInfo("Some_really_big_key_you_did_not_ask_for", "and_a_value");
        ReportInfo.getCurrentMethodInfo().addInfo("methodKey2", "methodValue2");
    }

    /**
     * DependingMethod
     * <p/>
     * Description: DependingMethod
     */
    @Test(dependsOnMethods = "testT01FailFirstPassSecond")
    public void testT03DependingMethod() {

    }

    @AfterMethod
    public void tearDown1() throws Exception {
        System.out.println("tearDown 1");
    }

    @AfterMethod
    public void tearDown2(Method method) throws Exception {
        System.out.println("tearDown 2");
    }

    @BeforeMethod
    public void beforeMethod() {

    }

    @AfterMethod
    public void afterMethod() {

    }

    @Test
    public void testA0() throws Exception {
    }

    @Test(dependsOnMethods = "testA0")
    public void testA() throws Exception {
    }

    @Test
    public void testB() throws Exception {
    }
    @Test
    public void testC() throws Exception {
    }

    @Test(dependsOnMethods = {"testA", "testB", "testC"})
    public void testD() throws Exception {
    }
}
