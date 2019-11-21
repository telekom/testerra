package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicGuiElement;

/**
 * Allows element location tests
 * @author Mike Reiche
 */
public interface HorizontalDistanceAssertion extends ActualProperty<Integer> {
    QuantityAssertion<Integer> toRightOf(BasicGuiElement guiElement);
    QuantityAssertion<Integer> toLeftOf(BasicGuiElement guiElement);
}
