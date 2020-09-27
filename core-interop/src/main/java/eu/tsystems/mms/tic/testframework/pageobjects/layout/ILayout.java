package eu.tsystems.mms.tic.testframework.pageobjects.layout;

import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElement;

@Deprecated
public interface ILayout {
    LayoutBorders getElementLayoutBorders(UiElement GuiElementFacade);

    ILayout leftOf(UiElement distanceGE);

    void checkOn(UiElement actualGE, Assertion configuredAssert);

    String toStringText();

    ILayout above(UiElement distanceGE);

    ILayout rightOf(UiElement distanceGE);

    ILayout below(UiElement distanceGE);

    ILayout sameTop(UiElement distanceGE, int delta);

    ILayout sameBottom(UiElement distanceGE, int delta);

    ILayout sameLeft(UiElement distanceGE, int delta);

    ILayout sameRight(UiElement distanceGE, int delta);

    @Deprecated
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
}
