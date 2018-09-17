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
package eu.tsystems.mms.tic.testframework.core.playground.reporting;

import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Created by pele on 16.06.2014.
 */
public class ExcessiveReporting {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    static int dbNumber = 200;
    static int logMessages = 50;

    @DataProvider(name = "huhu", parallel = true)
    public Object[][] dp() {

        int i = dbNumber;
        Object[][] objects = new Object[i][1];
        for (int j = 0; j < dbNumber; j++) {
            objects[j][0] = "dummy";
        }
        return objects;
    }

    @Test(dataProvider = "huhu", threadPoolSize = 10)
    public void test1(Object object1) {
        // loop
        for (int i = 0; i < logMessages; i++) {
            logger.info("huhu " + i);
        }
        throw new FennecRuntimeException("hoho");
    }

}
