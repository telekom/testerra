package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicUiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.Nameable;

/**
 * Components are PageObjects restricted to a root GuiElement
 * @author Mike Reiche
 */
public interface Component<SELF> extends
    CheckablePage,
    BasicUiElement,
    Nameable<SELF>,
    WebElementRetainer
{
    UiElementList<SELF> list();
}
