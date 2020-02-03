package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.common.Testerra;

/**
 * Provides a {@link UiElementFactory}
 * @author Mike Reiche
 */
public interface UiElementFactoryProvider {
    UiElementFactory uiElementFactory = Testerra.injector.getInstance(UiElementFactory.class);
}
