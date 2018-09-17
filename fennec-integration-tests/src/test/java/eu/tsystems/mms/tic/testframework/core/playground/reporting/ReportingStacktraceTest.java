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
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public class ReportingStacktraceTest extends AbstractTest {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testFailing() throws Exception {
        throw new RuntimeException("By: org.openqa.selenium.WebDriverException: Element is not clickable at point (854.5, 563). Other element would receive the click: <td class=\"x-toolbar-left\" align=\"center\"></td>\n" +
                        "Command duration or timeout: 125 milliseconds\n" +
                        "Build info: version: '2.53.1', revision: 'a36b8b1', time: '2016-06-30 17:37:03'\n" +
                        "System info: host: 'WUM61184', ip: '192.168.61.184', os.name: 'Windows Server 2008 R2', os.arch: 'x86', os.version: '6.1', java.version: '1.7.0_51'\n" +
                        "Session ID: be3a337b-5198-409c-9833-7e9212b7ba03\n" +
                        "Driver info: org.openqa.selenium.firefox.FirefoxDriver\n" +
                        "Capabilities [{platform=WINDOWS, acceptSslCerts=true, javascriptEnabled=true, browserName=firefox, pageLoadingStrategy=fast, rotatable=false, locationContextEnabled=true, pageLoadStrategy=fast, version=35.0, cssSelectorsEnabled=true, databaseEnabled=true, handlesAlerts=true, nativeEvents=false, webStorageEnabled=true, applicationCacheEnabled=true, takesScreenshot=true}]\n" +
                        "Build info: version: '2.53.1', revision: 'a36b8b1cd5757287168e54b817830adce9b0158d', time: '2016-06-30 19:26:09'\n" +
                        "System info: host: '2887d48473e9', ip: '172.17.0.6', os.name: 'Linux', os.arch: 'amd64', os.version: '4.2.0-35-generic', java.version: '1.8.0_91'\n" +
                        "Driver info: driver.version: RemoteWebDriver");
    }


}
