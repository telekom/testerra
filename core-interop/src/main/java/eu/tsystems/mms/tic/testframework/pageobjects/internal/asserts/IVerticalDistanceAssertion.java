package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicGuiElement;

/**
 * Allows element location tests
 * @author Mike Reiche
 */
public interface IVerticalDistanceAssertion extends ActualProperty<Integer> {
    IQuantifiedPropertyAssertion<Integer> toTopOf(BasicGuiElement guiElement);
    IQuantifiedPropertyAssertion<Integer> toBottomOf(BasicGuiElement guiElement);
}
