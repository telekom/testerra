package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicGuiElement;

/**
 * Allows element location tests
 * @author Mike Reiche
 */
public interface IVerticalDistanceAssertion extends ActualProperty<Integer> {
    IQuantifiedPropertyAssertion<Integer> toTop(BasicGuiElement guiElement);
    IQuantifiedPropertyAssertion<Integer> toBottom(BasicGuiElement guiElement);
}
