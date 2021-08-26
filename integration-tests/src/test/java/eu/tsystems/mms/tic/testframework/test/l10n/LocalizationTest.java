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
package eu.tsystems.mms.tic.testframework.test.l10n;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.l10n.LocalizedBundle;
import eu.tsystems.mms.tic.testframework.l10n.SimpleLocalization;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import java.util.Locale;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LocalizationTest extends TesterraTest {

    @Test(dataProvider = "locales")
    public void test_readUtf8FromResourceBundle(String locale, String expected) {
        Locale.setDefault(Locale.forLanguageTag(locale));
        Assert.assertEquals(SimpleLocalization.getText("TEST"), expected);
    }

    @Test
    public void test_inexistentLocalizedProperty() {
        Assert.assertEquals("NOT_EXISTENT", SimpleLocalization.getText("NOT_EXISTENT"));
    }

    @DataProvider
    public Object[][] locales() {
        return new Object[][]{
            {"de", "üöäß-german"},
            {"en", "üöäß-english"},
        };
    }

    @Test
    public void test_fixedLocale() {
        Locale.setDefault(Locale.ENGLISH);
        LocalizedBundle germanBundle = new LocalizedBundle(SimpleLocalization.BUNDLE_NAME, Locale.GERMAN);
        LocalizedBundle defaultBundle = new LocalizedBundle(SimpleLocalization.BUNDLE_NAME);
        Assert.assertNotEquals(germanBundle.getString("TEST"), defaultBundle.getString("TEST"));

        Locale.setDefault(Locale.GERMAN);
        Assert.assertEquals(germanBundle.getString("TEST"), defaultBundle.getString("TEST"));
    }

}
