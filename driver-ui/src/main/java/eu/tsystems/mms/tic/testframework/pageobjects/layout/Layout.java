/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.pageobjects.layout;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.ConfiguredAssert;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

import java.util.Map;

/**
 * Created by pele on 31.08.2015.
 */
public abstract class Layout {

    private boolean innerBorders = false;

    private Layout(boolean innerBorders) {
        this.innerBorders = innerBorders;
    }

    public static class LayoutBorders {
        long left = -1;
        long right = -1;
        long top = -1;
        long bottom = -1;

        LayoutBorders(long left, long right, long top, long bottom) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
        }

        public long getLeft() {
            return left;
        }

        public long getRight() {
            return right;
        }

        public long getTop() {
            return top;
        }

        public long getBottom() {
            return bottom;
        }
    }

    public static Layout inner() {
        /*
        Dummy impl.
         */
        Layout layout = new Layout(true) {
            @Override
            public void checkOn(GuiElement actualGE, ConfiguredAssert configuredAssert) {
                //dummy
            }

            @Override
            public String toStringText() {
                return null;
            }
        };

        return layout;
    }

    public static Layout outer() {
        /*
        Dummy impl.
         */
        Layout layout = new Layout(false) {
            @Override
            public void checkOn(GuiElement actualGE, ConfiguredAssert configuredAssert) {
                //dummy
            }

            @Override
            public String toStringText() {
                return null;
            }
        };

        return layout;
    }

    public LayoutBorders getElementLayoutBorders(GuiElement guiElement) {
        if (innerBorders) {
            Map<String, Long> borders = JSUtils.getElementInnerBorders(guiElement);
            if (borders != null) {
                return new LayoutBorders(borders.get("left"), borders.get("right"), borders.get("top"), borders.get("bottom"));
            }
            return new LayoutBorders(0, 0, 0, 0);
        } else {
            Point location = guiElement.getLocation();
            Dimension size = guiElement.getSize();
            int left = location.getX();
            int right = left + size.getWidth();
            int top = location.getY();
            int bottom = top + size.getHeight();
            return new LayoutBorders(left, right, top, bottom);
        }
    }

    public Layout leftOf(final GuiElement distanceGE) {
        return new Layout(this.innerBorders) {
            @Override
            public void checkOn(GuiElement actualGE, ConfiguredAssert configuredAssert) {
                LayoutBorders actLB = getElementLayoutBorders(actualGE);
                LayoutBorders distLB = getElementLayoutBorders(distanceGE);
                long actual = actLB.left;
                long reference = distLB.left;
                configuredAssert.assertTrue(actual < reference, ">" + actualGE + "< is left of >" + distanceGE + "< (" + actual + "<" + reference + ")");
            }

            @Override
            public String toStringText() {
                return "leftOf " + distanceGE;
            }
        };
    }

    public abstract void checkOn(GuiElement actualGE, ConfiguredAssert configuredAssert);

    public abstract String toStringText();

    public String toString() {
        return toStringText();
    }

    public Layout above(final GuiElement distanceGE) {
        return new Layout(this.innerBorders) {
            @Override
            public void checkOn(GuiElement actualGE, ConfiguredAssert configuredAssert) {
                LayoutBorders actLB = getElementLayoutBorders(actualGE);
                LayoutBorders distLB = getElementLayoutBorders(distanceGE);
                long actual = actLB.top;
                long reference = distLB.top;
                configuredAssert.assertTrue(actual < reference, ">" + actualGE + "< is above >" + distanceGE + "< (" + actual + "<" + reference + ")");
            }

            @Override
            public String toStringText() {
                return "above " + distanceGE;
            }
        };
    }

    public Layout rightOf(final GuiElement distanceGE) {
        return new Layout(this.innerBorders) {
            @Override
            public void checkOn(GuiElement actualGE, ConfiguredAssert configuredAssert) {
                LayoutBorders actLB = getElementLayoutBorders(actualGE);
                LayoutBorders distLB = getElementLayoutBorders(distanceGE);
                long actual = actLB.right;
                long reference = distLB.right;
                configuredAssert.assertTrue(actual > reference, ">" + actualGE + "< is right of >" + distanceGE + "< (" + actual + ">" + reference + ")");
            }

            @Override
            public String toStringText() {
                return "rightOf " + distanceGE;
            }
        };
    }

    public Layout below(final GuiElement distanceGE) {
        return new Layout(this.innerBorders) {
            @Override
            public void checkOn(GuiElement actualGE, ConfiguredAssert configuredAssert) {
                LayoutBorders actLB = getElementLayoutBorders(actualGE);
                LayoutBorders distLB = getElementLayoutBorders(distanceGE);
                long actual = actLB.bottom;
                long reference = distLB.bottom;
                configuredAssert.assertTrue(actual > reference, ">" + actualGE + "< is below >" + distanceGE + "< (" + actual + ">" + reference + ")");
            }

            @Override
            public String toStringText() {
                return "below " + distanceGE;
            }
        };
    }

    public Layout sameTop(final GuiElement distanceGE, final int delta) {
        return new Layout(this.innerBorders) {
            @Override
            public void checkOn(GuiElement actualGE, ConfiguredAssert configuredAssert) {
                LayoutBorders actLB = getElementLayoutBorders(actualGE);
                LayoutBorders distLB = getElementLayoutBorders(distanceGE);
                long actual = actLB.top;
                long reference = distLB.top;

                long max = reference + delta;
                long min = reference - delta;
                configuredAssert.assertTrue(actual <= max && actual >= min, ">" + actualGE + "< has same top coords as >" + distanceGE + "< (" + actual + "==" + reference + " +-" + delta + ")");
            }

            @Override
            public String toStringText() {
                return "same top coords as " + distanceGE;
            }
        };
    }

    public Layout sameBottom(final GuiElement distanceGE, final int delta) {
        return new Layout(this.innerBorders) {
            @Override
            public void checkOn(GuiElement actualGE, ConfiguredAssert configuredAssert) {
                LayoutBorders actLB = getElementLayoutBorders(actualGE);
                LayoutBorders distLB = getElementLayoutBorders(distanceGE);
                long actual = actLB.bottom;
                long reference = distLB.bottom;

                long max = reference + delta;
                long min = reference - delta;
                configuredAssert.assertTrue(actual <= max && actual >= min, ">" + actualGE + "< has same bottom coords as >" + distanceGE + "< (" + actual + "==" + reference + " +-" + delta + ")");
            }

            @Override
            public String toStringText() {
                return "same bottom coords as " + distanceGE;
            }
        };
    }

    public Layout sameLeft(final GuiElement distanceGE, final int delta) {
        return new Layout(this.innerBorders) {
            @Override
            public void checkOn(GuiElement actualGE, ConfiguredAssert configuredAssert) {
                LayoutBorders actLB = getElementLayoutBorders(actualGE);
                LayoutBorders distLB = getElementLayoutBorders(distanceGE);
                long actual = actLB.left;
                long reference = distLB.left;

                long max = reference + delta;
                long min = reference - delta;
                configuredAssert.assertTrue(actual <= max && actual >= min, ">" + actualGE + "< has same left coords as >" + distanceGE + "< (" + actual + "==" + reference + " +-" + delta + ")");
            }

            @Override
            public String toStringText() {
                return "same left coords as " + distanceGE;
            }
        };
    }

    public Layout sameRight(final GuiElement distanceGE, final int delta) {
        return new Layout(this.innerBorders) {
            @Override
            public void checkOn(GuiElement actualGE, ConfiguredAssert configuredAssert) {
                LayoutBorders actLB = getElementLayoutBorders(actualGE);
                LayoutBorders distLB = getElementLayoutBorders(distanceGE);
                long actual = actLB.right;
                long reference = distLB.right;

                long max = reference + delta;
                long min = reference - delta;
                configuredAssert.assertTrue(actual <= max && actual >= min, ">" + actualGE + "< has same right coords as >" + distanceGE + "< (" + actual + "==" + reference + " +-" + delta + ")");
            }

            @Override
            public String toStringText() {
                return "same right coords as " + distanceGE;
            }
        };
    }

}
