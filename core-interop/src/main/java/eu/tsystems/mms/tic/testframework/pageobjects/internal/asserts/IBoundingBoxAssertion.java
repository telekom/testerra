package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicGuiElement;
import org.openqa.selenium.Rectangle;

/**
 * Allows element location tests
 * @author Mike Reiche
 */
public interface IBoundingBoxAssertion extends ActualProperty<Rectangle> {
    IBinaryPropertyAssertion<Boolean> contains(BasicGuiElement guiElement);
    IBinaryPropertyAssertion<Boolean> intersects(BasicGuiElement guiElement);
    IHorizontalDistanceAssertion fromRight();
    IHorizontalDistanceAssertion fromLeft();
    IVerticalDistanceAssertion fromTop();
    IVerticalDistanceAssertion fromBottom();
}
