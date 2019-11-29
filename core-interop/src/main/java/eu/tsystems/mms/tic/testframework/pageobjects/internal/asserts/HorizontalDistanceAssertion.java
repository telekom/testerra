package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.TestableGuiElement;

/**
 * Allows element location tests
 * @author Mike Reiche
 */
public interface HorizontalDistanceAssertion extends ActualProperty<Integer> {
    QuantityAssertion<Integer> toRightOf(TestableGuiElement guiElement);
    QuantityAssertion<Integer> toLeftOf(TestableGuiElement guiElement);
}
