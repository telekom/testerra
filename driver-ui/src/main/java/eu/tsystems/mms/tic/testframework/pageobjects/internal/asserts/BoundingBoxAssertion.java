package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.utils.Formatter;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicGuiElement;
import org.openqa.selenium.Rectangle;

public class BoundingBoxAssertion extends AbstractTestedPropertyAssertion<Rectangle> implements IBoundingBoxAssertion {

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
    public IBoundingBoxAssertion contains(BasicGuiElement guiElement) {
        this.testTimer(t -> {
            Rectangle referenceRect = guiElement.getWebElement().getRect();
            Rectangle originRect = provider.getActual();
            if (toRectangle(originRect).contains(toRectangle(referenceRect))) {
                return true;
            } else {
                assertion.fail(
                    assertion.format(
                        formatter.toString(originRect),
                        String.format("contains {%s.boundingBox} [%s]", guiElement, formatter.toString(referenceRect)),
                        traceSubjectString()
                    )
                );
                return false;
            }
        });
        return this;
    }

    @Override
    public IBoundingBoxAssertion intersects(BasicGuiElement guiElement) {
        this.testTimer(t -> {
            Rectangle referenceRect = guiElement.getWebElement().getRect();
            Rectangle originRect = provider.getActual();
            if (toRectangle(originRect).intersects(toRectangle(referenceRect))) {
                return true;
            } else {
                assertion.fail(
                    assertion.format(
                        formatter.toString(originRect),
                        String.format("intersects {%s.boundingBox} [%s]", guiElement, formatter.toString(referenceRect)),
                        traceSubjectString()
                    )
                );
                return false;
            }
        });
        return this;
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
