package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicGuiElement;

/**
 * Allows element location tests
 * @author Mike Reiche
 */
public interface HorizontalDistanceAssertion extends ActualProperty<Integer> {
    QuantifiedPropertyAssertion<Integer> toRightOf(BasicGuiElement guiElement);
    QuantifiedPropertyAssertion<Integer> toLeftOf(BasicGuiElement guiElement);
}
