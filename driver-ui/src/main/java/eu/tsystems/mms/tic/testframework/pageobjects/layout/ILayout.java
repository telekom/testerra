package eu.tsystems.mms.tic.testframework.pageobjects.layout;

import eu.tsystems.mms.tic.testframework.execution.testng.IAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacade;

public interface ILayout {
    LayoutBorders getElementLayoutBorders(GuiElementFacade GuiElementFacade);

    ILayout leftOf(GuiElementFacade distanceGE);

    void checkOn(GuiElementFacade actualGE, IAssertion configuredAssert);

    String toStringText();

    ILayout above(GuiElementFacade distanceGE);

    ILayout rightOf(GuiElementFacade distanceGE);

    ILayout below(GuiElementFacade distanceGE);

    ILayout sameTop(GuiElementFacade distanceGE, int delta);

    ILayout sameBottom(GuiElementFacade distanceGE, int delta);

    ILayout sameLeft(GuiElementFacade distanceGE, int delta);

    ILayout sameRight(GuiElementFacade distanceGE, int delta);

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
