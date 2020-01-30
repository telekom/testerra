package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.testing.TestFeatures;
import eu.tsystems.mms.tic.testframework.testing.PageObjectCreator;

/**
 * Provides protected {@link PageObject} features
 * @author Mike Reiche
 */
public abstract class AbstractPageObject implements
    TestFeatures,
    PageObjectCreator,
    PageObject,
    UiElementFinder
{

    protected <T extends Component> T createComponent(Class<T> componentClass, UiElement rootElement) {
        return PAGE_FACTORY.createComponent(componentClass, rootElement);
    }

    protected <T extends PageObject> T createPage(final Class<T> pageClass) {
        return PAGE_FACTORY.createPage(pageClass, getWebDriver());
    }
}
