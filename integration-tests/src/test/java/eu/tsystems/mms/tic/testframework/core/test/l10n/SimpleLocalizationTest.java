package eu.tsystems.mms.tic.testframework.core.test.l10n;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.l10n.SimpleLocalization;
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
