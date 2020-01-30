package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.HasParent;

/**
 * Fundamental Page Object features
 * @author Mike Reiche
 */
public interface PageObject extends
    HasParent,
    WebDriverRetainer,
    CheckablePage
{
}
