package eu.tsystems.mms.tic.testframework.testing;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.pageobjects.PageObjectFactory;

/**
 * Provides a {@link PageObjectFactory}
 * @author Mike Reiche
 */
public interface PageObjectCreator {
    PageObjectFactory PAGE_FACTORY = Testerra.injector.getInstance(PageObjectFactory.class);
}
