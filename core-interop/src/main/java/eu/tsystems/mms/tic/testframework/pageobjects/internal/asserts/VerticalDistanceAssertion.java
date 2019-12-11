package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.TestableUiElement;

/**
 * Allows element location tests
 * @author Mike Reiche
 */
public interface VerticalDistanceAssertion extends ActualProperty<Integer> {
    QuantityAssertion<Integer> toTopOf(TestableUiElement guiElement);
    QuantityAssertion<Integer> toBottomOf(TestableUiElement guiElement);
}
