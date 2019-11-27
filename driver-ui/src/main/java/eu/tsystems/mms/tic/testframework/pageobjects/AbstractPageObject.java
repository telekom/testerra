package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.testing.AbstractTest;
import org.openqa.selenium.By;

/**
 * Provides protected {@link PageObject} features
 */
public abstract class AbstractPageObject extends AbstractTest implements PageObject {
    private static final PageObjectFactory pageFactory = Testerra.injector.getInstance(PageObjectFactory.class);

    protected abstract IGuiElement find(Locate locator);

    protected IGuiElement findById(Object id) {
        return find(Locate.by().id(id));
    }

    protected IGuiElement findByQa(String qa) {
        return find(Locate.by().qa(qa));
    }

    protected IGuiElement find(By by) {
        return find(Locate.by(by));
    }

    protected <T extends Component> T createComponent(Class<T> componentClass, IGuiElement rootElement) {
        return pageFactory.createComponent(componentClass, rootElement);
    }

    protected <T extends PageObject> T createPage(final Class<T> pageClass) {
        return pageFactory.createPage(pageClass, getWebDriver());
    }
}
