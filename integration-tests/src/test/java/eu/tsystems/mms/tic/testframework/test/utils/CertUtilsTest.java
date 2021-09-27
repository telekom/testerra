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

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.utils.CertUtils;
import java.util.Arrays;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CertUtilsTest {

    @Test
    public void test_trustAllHosts() {
        PropertyManager.getThreadLocalProperties().setProperty(CertUtils.TRUSTED_HOSTS, "*");
        CertUtils certUtils = new CertUtils();
        Assert.assertTrue(certUtils.isTrustAllHosts());
        Assert.assertEquals(0, certUtils.getTrustedHosts().length);
    }

    @Test
    public void test_trustSpecificHosts() {
        PropertyManager.getThreadLocalProperties().setProperty(CertUtils.TRUSTED_HOSTS, "t-systems.com example.com");
        CertUtils certUtils = new CertUtils();
        String[] trustedHosts = certUtils.getTrustedHosts();
        Assert.assertEquals(2, trustedHosts.length);

        List<String> hostList = Arrays.asList(trustedHosts);
        Assert.assertTrue(hostList.contains("t-systems.com"));
        Assert.assertTrue(hostList.contains("example.com"));
    }
}
