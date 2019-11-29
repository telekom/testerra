package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.TestableGuiElement;
import org.openqa.selenium.Rectangle;

public class DefaultHorizontalDistanceAssertion extends AbstractPropertyAssertion<Integer> implements HorizontalDistanceAssertion {

    public DefaultHorizontalDistanceAssertion(PropertyAssertion<Integer> parentAssertion, AssertionProvider<Integer> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public QuantityAssertion<Integer> toRightOf(TestableGuiElement guiElement) {
        return propertyAssertionFactory.create(DefaultQuantityAssertion.class, this, new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                Rectangle referenceRect = guiElement.bounds().getActual();
                return provider.getActual()-(referenceRect.x+referenceRect.width);
            }

            @Override
            public String getSubject() {
                return String.format("toRightOf(guiElement: %s)", guiElement);
            }
        });
    }

    @Override
    public QuantityAssertion<Integer> toLeftOf(TestableGuiElement guiElement) {
        return propertyAssertionFactory.create(DefaultQuantityAssertion.class, this, new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                Rectangle referenceRect = guiElement.bounds().getActual();
                return provider.getActual()-referenceRect.x;
            }

            @Override
            public String getSubject() {
                return String.format("toLeftOf(guiElement: %s)", guiElement);
            }
        });
    }
}
