/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 *
 */
package eu.tsystems.mms.tic.testframework.core.test.l10n;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.l10n.SimpleLocalization;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Locale;

public class SimpleLocalizationTest extends AbstractWebDriverTest {

    @Test(dataProvider = "locales")
    public void test_readUtf8FromResourceBundle(String locale, String expected) {
        Locale.setDefault(Locale.forLanguageTag(locale));
        Assert.assertEquals(SimpleLocalization.getText("TEST"), expected);
    }

    @DataProvider
    public Object[][] locales() {
        return new Object[][]{
            {"de", "üöäß-german"},
            {"en", "üöäß-english"},
        };
    }
}
