package eu.tsystems.mms.tic.testframework.testing;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.webdrivermanager.SeleniumBiDiTools;

/**
 * Created on 2024-02-28
 *
 * @author mgn
 */
public interface SeleniumBidiToolsProvider {

    SeleniumBiDiTools SELENIUM_BIDI_TOOLS = Testerra.getInjector().getInstance(SeleniumBiDiTools.class);

}
