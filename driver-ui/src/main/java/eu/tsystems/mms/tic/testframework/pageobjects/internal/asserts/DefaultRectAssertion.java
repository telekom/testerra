/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.pageobjects.TestableUiElement;
import eu.tsystems.mms.tic.testframework.utils.Formatter;
import org.openqa.selenium.Rectangle;

/**
 * Default implementation of {@link RectAssertion}
 * @author Mike Reiche
 */
public class DefaultRectAssertion extends AbstractPropertyAssertion<Rectangle> implements RectAssertion {

    private static final Formatter formatter = Testerra.injector.getInstance(Formatter.class);

    public DefaultRectAssertion(AbstractPropertyAssertion<Rectangle> parentAssertion, AssertionProvider<Rectangle> provider) {
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
    public BinaryAssertion<Boolean> contains(TestableUiElement guiElement) {
        return propertyAssertionFactory.create(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return toRectangle(provider.getActual()).contains(toRectangle(guiElement.bounds().getActual()));
            }

            @Override
            public String getSubject() {
                return String.format("%s.contains(guiElement: %s.bounds.%s)",
                    formatter.toString(provider.getActual()),
                    guiElement,
                    formatter.toString(guiElement.bounds().getActual())
                );
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> intersects(TestableUiElement guiElement) {
        return propertyAssertionFactory.create(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return toRectangle(provider.getActual()).intersects(toRectangle(guiElement.bounds().getActual()));
            }

            @Override
            public String getSubject() {
                return String.format("%s.intersects(guiElement: %s.bounds.%s)",
                    formatter.toString(provider.getActual()),
                    guiElement,
                    formatter.toString(guiElement.bounds().getActual())
                );
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> leftOf(TestableUiElement guiElement) {
        return propertyAssertionFactory.create(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return fromLeft().toLeftOf(guiElement).getActual()<0;
            }

            @Override
            public String getSubject() {
                return String.format("leftOf(guiElement: %s)", guiElement);
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> rightOf(TestableUiElement guiElement) {
        return propertyAssertionFactory.create(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return fromLeft().toRightOf(guiElement).getActual()>=0;
            }

            @Override
            public String getSubject() {
                return String.format("rightOf(guiElement: %s)", guiElement);
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> above(TestableUiElement guiElement) {
        return propertyAssertionFactory.create(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return fromTop().toTopOf(guiElement).getActual()<0;
            }

            @Override
            public String getSubject() {
                return String.format("above(guiElement: %s)", guiElement);
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> below(TestableUiElement guiElement) {
        return propertyAssertionFactory.create(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return fromTop().toBottomOf(guiElement).getActual()>=0;
            }

            @Override
            public String getSubject() {
                return String.format("below(guiElement: %s)", guiElement);
            }
        });
    }

    @Override
    public HorizontalDistanceAssertion fromRight() {
        return propertyAssertionFactory.create(DefaultHorizontalDistanceAssertion.class, this, new AssertionProvider<Integer>() {
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
    public HorizontalDistanceAssertion fromLeft() {
        return propertyAssertionFactory.create(DefaultHorizontalDistanceAssertion.class, this, new AssertionProvider<Integer>() {
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
    public VerticalDistanceAssertion fromTop() {
        return propertyAssertionFactory.create(DefaultVerticalDistanceAssertion.class, this, new AssertionProvider<Integer>() {
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
    public VerticalDistanceAssertion fromBottom() {
        return propertyAssertionFactory.create(DefaultVerticalDistanceAssertion.class, this, new AssertionProvider<Integer>() {
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
