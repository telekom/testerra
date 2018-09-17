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
 * Created on 23.03.2017
 *
 * Copyright(c) 1995 - 2007 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.reporting;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.annotations.Fails;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * ReportingConfigMethodsFail5Test
 * <p>
 * Date: 23.03.2017
 * Time: 11:35
 *
 * @author erku
 */
public class ReportingExpectedFailedTest extends AbstractTest {

    //
    //    @Test
    //    public void testT01Passed() {
    //
    //
    //    }

    @Test
    @Fails()
    public void testT02ExpectedFailed() {

        Assert.fail();
    }

    @Test
    @Fails()
    public void testT03ExpectedFailed() {

        Assert.fail();
    }


}
