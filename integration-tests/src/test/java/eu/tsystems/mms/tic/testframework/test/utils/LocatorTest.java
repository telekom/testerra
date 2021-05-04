/*
 * Testerra
 *
 * (C) 2021, Mike Reiche,  T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.test.utils;

import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LocatorTest extends TesterraTest {

    @Test
    public void test_cloneLocateWithNewBy() {
        Locate locator = Locate.by(By.xpath("//body")).displayed().unique();
        Locate clonedLocator = Locate.by(By.xpath("//head"), locator);

        Assert.assertSame(clonedLocator.getFilter(), locator.getFilter());
        Assert.assertEquals("By.xpath: //head", clonedLocator.getBy().toString());
        Assert.assertTrue(locator.isUnique());
    }
}
