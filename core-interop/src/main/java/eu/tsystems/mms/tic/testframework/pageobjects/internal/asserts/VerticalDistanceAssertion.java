package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicGuiElement;

/**
 * Allows element location tests
 * @author Mike Reiche
 */
public interface VerticalDistanceAssertion extends ActualProperty<Integer> {
    QuantifiedPropertyAssertion<Integer> toTopOf(BasicGuiElement guiElement);
    QuantifiedPropertyAssertion<Integer> toBottomOf(BasicGuiElement guiElement);
}
