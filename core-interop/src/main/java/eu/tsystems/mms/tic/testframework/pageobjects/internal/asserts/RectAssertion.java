package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.TestableGuiElement;
import org.openqa.selenium.Rectangle;

/**
 * Allows element location tests
 * @author Mike Reiche
 */
public interface RectAssertion extends ActualProperty<Rectangle> {
    BinaryAssertion<Boolean> contains(TestableGuiElement guiElement);
    BinaryAssertion<Boolean> intersects(TestableGuiElement guiElement);
    BinaryAssertion<Boolean> leftOf(TestableGuiElement guiElement);
    BinaryAssertion<Boolean> rightOf(TestableGuiElement guiElement);
    BinaryAssertion<Boolean> above(TestableGuiElement guiElement);
    BinaryAssertion<Boolean> below(TestableGuiElement guiElement);
    HorizontalDistanceAssertion fromRight();
    HorizontalDistanceAssertion fromLeft();
    VerticalDistanceAssertion fromTop();
    VerticalDistanceAssertion fromBottom();
}
