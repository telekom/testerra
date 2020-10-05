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

package eu.tsystems.mms.tic.testframework.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.TestableUiElement;
import org.openqa.selenium.Rectangle;

/**
 * Default implementation of {@link RectAssertion}
 * @author Mike Reiche
 */
public class DefaultRectAssertion extends AbstractPropertyAssertion<Rectangle> implements RectAssertion {

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

    protected String format(Rectangle rectangle) {
        return String.format("(left: %d, top: %d, right: %d, bottom: %d)", rectangle.x, rectangle.y, rectangle.x+rectangle.width,rectangle.y+rectangle.height);
    }

    @Override
    public BinaryAssertion<Boolean> contains(TestableUiElement uiElement) {
        return propertyAssertionFactory.createWithParent(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return toRectangle(provider.getActual()).contains(toRectangle(uiElement.waitFor().bounds().getActual()));
            }

            @Override
            public String getSubject() {
                return String.format("%s.contains(%s.bounds.%s)",
                    format(provider.getActual()),
                    uiElement,
                    format(uiElement.waitFor().bounds().getActual())
                );
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> intersects(TestableUiElement uiElement) {
        return propertyAssertionFactory.createWithParent(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return toRectangle(provider.getActual()).intersects(toRectangle(uiElement.waitFor().bounds().getActual()));
            }

            @Override
            public String getSubject() {
                return String.format("%s.intersects(%s.bounds.%s)",
                    format(provider.getActual()),
                    uiElement,
                    format(uiElement.waitFor().bounds().getActual())
                );
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> leftOf(TestableUiElement uiElement) {
        return propertyAssertionFactory.createWithParent(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return fromLeft().toLeftOf(uiElement).getActual()<0;
            }

            @Override
            public String getSubject() {
                return String.format("leftOf(%s)", uiElement);
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> rightOf(TestableUiElement uiElement) {
        return propertyAssertionFactory.createWithParent(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return fromLeft().toRightOf(uiElement).getActual()>=0;
            }

            @Override
            public String getSubject() {
                return String.format("rightOf(%s)", uiElement);
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> above(TestableUiElement uiElement) {
        return propertyAssertionFactory.createWithParent(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return fromTop().toTopOf(uiElement).getActual()<0;
            }

            @Override
            public String getSubject() {
                return String.format("above(%s)", uiElement);
            }
        });
    }

    @Override
    public BinaryAssertion<Boolean> below(TestableUiElement uiElement) {
        return propertyAssertionFactory.createWithParent(DefaultBinaryAssertion.class, this, new AssertionProvider<Boolean>() {
            @Override
            public Boolean getActual() {
                return fromTop().toBottomOf(uiElement).getActual()>=0;
            }

            @Override
            public String getSubject() {
                return String.format("below(%s)", uiElement);
            }
        });
    }

    @Override
    public HorizontalDistanceAssertion fromRight() {
        return propertyAssertionFactory.createWithParent(DefaultHorizontalDistanceAssertion.class, this, new AssertionProvider<Integer>() {
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
        return propertyAssertionFactory.createWithParent(DefaultHorizontalDistanceAssertion.class, this, new AssertionProvider<Integer>() {
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
        return propertyAssertionFactory.createWithParent(DefaultVerticalDistanceAssertion.class, this, new AssertionProvider<Integer>() {
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
        return propertyAssertionFactory.createWithParent(DefaultVerticalDistanceAssertion.class, this, new AssertionProvider<Integer>() {
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
