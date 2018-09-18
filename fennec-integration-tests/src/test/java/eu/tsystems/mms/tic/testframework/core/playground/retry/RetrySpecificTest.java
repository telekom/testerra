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
 * Created on 09.04.2014
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.retry;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.annotations.Retry;
import eu.tsystems.mms.tic.testframework.utils.RandomUtils;
import eu.tsystems.mms.tic.testframework.utils.TestUtils;
import org.openqa.selenium.WebDriverException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public class RetrySpecificTest extends AbstractTest {

    private static int counter = 0;

    static {
        System.setProperty("fennec.failed.tests.max.retries", "0");
    }

    /**
     * T01_OwnExceptionRetryTest
     * <p/>
     * Description: T01 OwnExceptionRetryTest
     *
     * @throws Exception .
     */
//    @Test
//    public void testT01_OwnExceptionRetryTest() throws SQLRecoverableException {
//        throw new SQLRecoverableException("Getrennte Verbindung");
//    }
//
//    @Test
//    public void testT02_OwnExceptionRetryTest() throws Exception {
//        throw new Exception("PollDEMAILMAILINDBTable: timeout (90s) while message missing");
//    }

    private void sleepRandom() {
        TestUtils.sleep(1000 + RandomUtils.generateRandomInt(1000));
    }

    @Retry
    @Test
    public void testT03_AimedExceptionInASubcause() throws Exception {
        Exception exception = new Exception("PollDEMAILMAILINDBTable: timeout (90s) while message missing");
        sleepRandom();
        throw new Exception("Nothing of interest", exception);
    }

    @Retry(maxRetries = 2)
    @Test
    public void testT04_AimedExceptionInASubcause_Repair() throws Exception {
        counter++;
        sleepRandom();
        if (counter < 2) {
            Exception exception = new Exception("PollDEMAILMAILINDBTable: timeout (90s) while message missing");
            throw new Exception("Nothing of interest", exception);
        }
    }

    @Test(dependsOnMethods = "testT04_AimedExceptionInASubcause_Repair")
    public void testT05_DependsOn_Repair() throws Exception {
    }

    @Test
    public void testRetryAnalyzer() throws Throwable {
        Throwable t3 = new WebDriverException("###3 org.openqa.selenium.WebDriverException: Session [a4824420-2fc9-4497-bd55-57b11402a23a] was terminated due to TIMEOUT\n" +
                "Command duration or timeout: 18 milliseconds");
        Throwable t2 = new RuntimeException("###2", t3);
        Throwable t1 = new Throwable("###1", t2);
        sleepRandom();
        throw t1;
    }

    @Retry(maxRetries = 3)
    @Test
    public void testT05_AimedExceptionInASubcause2() throws Exception {
        Exception exception = new RuntimeException("yes, yes, the session was not found");
        sleepRandom();
        throw new Exception("Nothing of interest", exception);
    }

    @Test
    public void testT06_RetryByHistory() throws Exception {
        sleepRandom();
        throw new RuntimeException("Should retry if not failed in last run (history missing)");
    }

//    @BeforeMethod
//    public void beforeMethod(Method method) {
//
//    }

    @DataProvider(name = "dp")
    public Object[][] dp() {
        Object[][] objects = new Object[3][1];
        objects[0][0] = "1";
        objects[1][0] = "2";
        objects[2][0] = "3";
        return objects;
    }

    @Test(dataProvider = "dp")
    public void testDataProvidedTest(String s) throws Exception {
        throw new RuntimeException("going to hell");
    }

    @Test(dataProvider = "dp")
    public void testDataProvidedTestResetMethodName(String s) throws Exception {
        throw new RuntimeException("going to hell");
    }

    @Test
    public void testPassed1() {}
    @Test
    public void testPassed2() {}
    @Test
    public void testPassed3() {}
    @Test
    public void testPassed4() {}
    @Test
    public void testPassed5() {}

    @Test(dataProvider = "dp")
    public void testPassedWithDP(String s) {

    }

}
