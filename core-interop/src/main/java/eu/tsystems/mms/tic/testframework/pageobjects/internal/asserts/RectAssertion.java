package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicGuiElement;
import org.openqa.selenium.Rectangle;

/**
 * Allows element location tests
 * @author Mike Reiche
 */
public interface RectAssertion extends ActualProperty<Rectangle> {
    BinaryAssertion<Boolean> contains(BasicGuiElement guiElement);
    BinaryAssertion<Boolean> intersects(BasicGuiElement guiElement);
    BinaryAssertion<Boolean> leftOf(BasicGuiElement guiElement);
    BinaryAssertion<Boolean> rightOf(BasicGuiElement guiElement);
    BinaryAssertion<Boolean> above(BasicGuiElement guiElement);
    BinaryAssertion<Boolean> below(BasicGuiElement guiElement);
    HorizontalDistanceAssertion fromRight();
    HorizontalDistanceAssertion fromLeft();
    VerticalDistanceAssertion fromTop();
    VerticalDistanceAssertion fromBottom();
}
