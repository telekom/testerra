package eu.tsystems.mms.tic.testframework.pageobjects.internal.frames;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacade;

import java.util.List;

public interface IFrameLogic {
    /**
     * Returns a list of frames as GuiElement in switching order.
     *
     * @return List of frames as GuiElement in switching order
     */
    List<GuiElementFacade> getAllFramesInOrder();

    /**
     * Switches to the frame the GuiElement is inside in.
     */
    void switchToCorrectFrame();

    /**
     * Switch to the default content.
     */
    void switchToDefaultFrame();

    GuiElementFacade[] getFrames();

    boolean hasFrames();
}
