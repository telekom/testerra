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
package eu.tsystems.mms.tic.testframework.core.test.utils;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.utils.AssertUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by toku on 13.01.2015.
 */

public class AssertUtilsTest extends AbstractTest {

    private String errorMessage = "String does not contain expected content";

    /**
     * checks if string contains expected content
     */
    @Test
    public void testT01_assertContains() {
        String string = "Dresden";
        AssertUtils.assertContains(string, "Dresden");
    }

    /**
     * checks if string contains expected content but fails. checks if assertionError is correct
     * @throws AssertionError .
     */
    @Test
    public void testT02_assertContainsFails() throws AssertionError {
        String string = "Dresden";
        try {
            AssertUtils.assertContains(string, "DresdenX");
        } catch (AssertionError e) {
           Assert.assertTrue(e.getMessage().contains("String contains " + "DresdenX" + "\nactual: " + string));
            return;
        }
        Assert.fail("");
    }

    /**
     * checks if string contains expected content
     */
    @Test
    public void testT03_assertContains() {
        String string = "Leipzig";
        AssertUtils.assertContains(string, "Leipzig", "String contains " + string + "instead of Leipzig");
    }


     /**
     * checks if string contains expected content but fails. checks if assertionError is correct
     * @throws AssertionError .
     */
    @Test
    public void testT04_assertContainsError() throws AssertionError {
        String string = "Haus";
        try {
            AssertUtils.assertContains(string, "Holz", "String contains " + string + "instead of Holz");
        } catch (AssertionError e) {
            Assert.assertTrue(e.getMessage().contains("String contains " + string + "instead of Holz"));
            return;
        }
        Assert.fail("");

    }


}
