package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicGuiElement;

/**
 * Allows element location tests
 * @author Mike Reiche
 */
public interface IHorizontalDistanceAssertion extends ActualProperty<Integer> {
    IQuantifiedPropertyAssertion<Integer> toRightOf(BasicGuiElement guiElement);
    IQuantifiedPropertyAssertion<Integer> toLeftOf(BasicGuiElement guiElement);
}
