package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicGuiElement;
import org.openqa.selenium.Rectangle;

public class HorizontalDistanceAssertion extends AbstractPropertyAssertion<Integer> implements IHorizontalDistanceAssertion {

    public HorizontalDistanceAssertion(PropertyAssertion parentAssertion, AssertionProvider<Integer> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public IQuantifiedPropertyAssertion<Integer> toRight(BasicGuiElement guiElement) {
        return propertyAssertionFactory.quantified(this, new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                Rectangle referenceRect = guiElement.getWebElement().getRect();
                return provider.getActual()-(referenceRect.x+referenceRect.width);
            }

            @Override
            public String getSubject() {
                return String.format("toRight(guiElement: %s)", guiElement);
            }
        });
    }

    @Override
    public IQuantifiedPropertyAssertion<Integer> toLeft(BasicGuiElement guiElement) {
        return propertyAssertionFactory.quantified(this, new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                Rectangle referenceRect = guiElement.getWebElement().getRect();
                return provider.getActual()-referenceRect.x;
            }

            @Override
            public String getSubject() {
                return String.format("toLeft(guiElement: %s)", guiElement);
            }
        });
    }
}
