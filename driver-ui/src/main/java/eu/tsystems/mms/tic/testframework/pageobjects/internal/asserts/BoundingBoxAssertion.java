package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicGuiElement;
import eu.tsystems.mms.tic.testframework.utils.Formatter;
import org.openqa.selenium.Rectangle;

public class BoundingBoxAssertion extends AbstractPropertyAssertion<Rectangle> implements IBoundingBoxAssertion {

    private static final Formatter formatter = Testerra.ioc().getInstance(Formatter.class);

    public BoundingBoxAssertion(PropertyAssertion parentAssertion, AssertionProvider<Rectangle> provider) {
        super(parentAssertion, provider);
    }

    private java.awt.Rectangle toRectangle(Rectangle rectangle) {
        return new java.awt.Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    @Override
    public Rectangle getActual() {
        return provider.getActual();
    }

    @Override
    public IBinaryPropertyAssertion contains(BasicGuiElement guiElement) {
        return propertyAssertionFactory.binary(this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return toRectangle(provider.getActual()).contains(toRectangle(guiElement.getWebElement().getRect()));
            }

            @Override
            public String getSubject() {
                return String.format("%s.contains(guiElement: %s.rect.%s)",
                    formatter.toString(provider.getActual()),
                    guiElement,
                    formatter.toString(guiElement.getWebElement().getRect())
                );
            }
        });
    }

    @Override
    public IBinaryPropertyAssertion intersects(BasicGuiElement guiElement) {
        return propertyAssertionFactory.binary(this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return toRectangle(provider.getActual()).intersects(toRectangle(guiElement.getWebElement().getRect()));
            }

            @Override
            public String getSubject() {
                return String.format("%s.intersects(guiElement: %s.rect.%s)",
                    formatter.toString(provider.getActual()),
                    guiElement,
                    formatter.toString(guiElement.getWebElement().getRect())
                );
            }
        });
    }

    @Override
    public IHorizontalDistanceAssertion fromRight() {
        return new HorizontalDistanceAssertion(this, new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                Rectangle originRect = provider.getActual();
                return originRect.x+originRect.width;
            }

            @Override
            public String getSubject() {
                return "fromRight";
            }
        });
    }

    @Override
    public IHorizontalDistanceAssertion fromLeft() {
        return new HorizontalDistanceAssertion(this, new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                Rectangle originRect = provider.getActual();
                return originRect.x;
            }

            @Override
            public String getSubject() {
                return "fromLeft";
            }
        });
    }

    @Override
    public IVerticalDistanceAssertion fromTop() {
        return new VerticalDistanceAssertion(this, new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                Rectangle originRect = provider.getActual();
                return originRect.y;
            }

            @Override
            public String getSubject() {
                return "fromTop";
            }
        });
    }

    @Override
    public IVerticalDistanceAssertion fromBottom() {
        return new VerticalDistanceAssertion(this, new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                Rectangle originRect = provider.getActual();
                return originRect.y+originRect.height;
            }

            @Override
            public String getSubject() {
                return "fromBottom";
            }
        });
    }
}
