package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicGuiElement;
import org.openqa.selenium.Rectangle;

/**
 * Allows element location tests
 * @author Mike Reiche
 */
public interface RectAssertion extends ActualProperty<Rectangle> {
    BinaryPropertyAssertion<Boolean> contains(BasicGuiElement guiElement);
    BinaryPropertyAssertion<Boolean> intersects(BasicGuiElement guiElement);
    HorizontalDistanceAssertion fromRight();
    HorizontalDistanceAssertion fromLeft();
    VerticalDistanceAssertion fromTop();
    VerticalDistanceAssertion fromBottom();
}
