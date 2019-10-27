package eu.tsystems.mms.tic.testframework.pageobjects.layout;

import eu.tsystems.mms.tic.testframework.execution.testng.IAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.IGuiElement;

public interface ILayout {
    LayoutBorders getElementLayoutBorders(IGuiElement GuiElementFacade);

    ILayout leftOf(IGuiElement distanceGE);

    void checkOn(IGuiElement actualGE, IAssertion configuredAssert);

    String toStringText();

    ILayout above(IGuiElement distanceGE);

    ILayout rightOf(IGuiElement distanceGE);

    ILayout below(IGuiElement distanceGE);

    ILayout sameTop(IGuiElement distanceGE, int delta);

    ILayout sameBottom(IGuiElement distanceGE, int delta);

    ILayout sameLeft(IGuiElement distanceGE, int delta);

    ILayout sameRight(IGuiElement distanceGE, int delta);

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
