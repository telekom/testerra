package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import eu.tsystems.mms.tic.testframework.testing.TestFeatures;

/**
 * Provides protected {@link PageObject} features
 * @author Mike Reiche
 */
public abstract class AbstractPageObject implements
    TestFeatures,
    PageFactoryProvider,
    PageObject,
    UiElementFinder
{

    protected <T extends Component> T createComponent(Class<T> componentClass, UiElement rootElement) {
        return pageFactory.createComponent(componentClass, rootElement);
    }

    protected <T extends PageObject> T createPage(final Class<T> pageClass) {
        return pageFactory.createPage(pageClass, getWebDriver());
    }
}
