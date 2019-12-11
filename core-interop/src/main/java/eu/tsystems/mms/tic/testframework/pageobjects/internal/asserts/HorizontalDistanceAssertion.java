package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.TestableUiElement;

/**
 * Allows element location tests
 * @author Mike Reiche
 */
public interface HorizontalDistanceAssertion extends ActualProperty<Integer> {
    QuantityAssertion<Integer> toRightOf(TestableUiElement guiElement);
    QuantityAssertion<Integer> toLeftOf(TestableUiElement guiElement);
}
