package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.TestableGuiElement;

/**
 * Allows element location tests
 * @author Mike Reiche
 */
public interface VerticalDistanceAssertion extends ActualProperty<Integer> {
    QuantityAssertion<Integer> toTopOf(TestableGuiElement guiElement);
    QuantityAssertion<Integer> toBottomOf(TestableGuiElement guiElement);
}
