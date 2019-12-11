package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.TestableUiElement;
import org.openqa.selenium.Rectangle;

/**
 * Allows element location tests
 * @author Mike Reiche
 */
public interface RectAssertion extends ActualProperty<Rectangle> {
    BinaryAssertion<Boolean> contains(TestableUiElement guiElement);
    BinaryAssertion<Boolean> intersects(TestableUiElement guiElement);
    BinaryAssertion<Boolean> leftOf(TestableUiElement guiElement);
    BinaryAssertion<Boolean> rightOf(TestableUiElement guiElement);
    BinaryAssertion<Boolean> above(TestableUiElement guiElement);
    BinaryAssertion<Boolean> below(TestableUiElement guiElement);
    HorizontalDistanceAssertion fromRight();
    HorizontalDistanceAssertion fromLeft();
    VerticalDistanceAssertion fromTop();
    VerticalDistanceAssertion fromBottom();
}
