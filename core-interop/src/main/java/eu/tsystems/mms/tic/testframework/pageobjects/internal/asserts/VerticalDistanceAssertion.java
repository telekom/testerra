package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicGuiElement;

/**
 * Allows element location tests
 * @author Mike Reiche
 */
public interface VerticalDistanceAssertion extends ActualProperty<Integer> {
    QuantityAssertion<Integer> toTopOf(BasicGuiElement guiElement);
    QuantityAssertion<Integer> toBottomOf(BasicGuiElement guiElement);
}
