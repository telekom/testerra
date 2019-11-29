package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.HasParent;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.IterableGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.Nameable;

/**
 * Components are PageObjects restricted to a root GuiElement
 * @author Mike Reiche
 */
public interface Component<SELF> extends
    CheckablePage,
    IterableGuiElement<SELF>,
    HasParent,
    Nameable<SELF>
{
}
