package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicGuiElement;
import org.openqa.selenium.Rectangle;

public class VerticalDistanceAssertion extends AbstractPropertyAssertion<Integer> implements IVerticalDistanceAssertion {

    public VerticalDistanceAssertion(PropertyAssertion parentAssertion, AssertionProvider<Integer> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public IQuantifiedPropertyAssertion<Integer> toTop(BasicGuiElement guiElement) {
        return propertyAssertionFactory.quantified(this, new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                Rectangle referenceRect = guiElement.getWebElement().getRect();
                return provider.getActual()-referenceRect.y;
            }

            @Override
            public String getSubject() {
                return String.format("toTop(guiElement: %s)", guiElement);
            }
        });
    }

    @Override
    public IQuantifiedPropertyAssertion<Integer> toBottom(BasicGuiElement guiElement) {
        return propertyAssertionFactory.quantified(this, new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                Rectangle referenceRect = guiElement.getWebElement().getRect();
                return provider.getActual()-(referenceRect.y+referenceRect.height);
            }

            @Override
            public String getSubject() {
                return String.format("toBottom(guiElement: %s)", guiElement);
            }
        });
    }
}
