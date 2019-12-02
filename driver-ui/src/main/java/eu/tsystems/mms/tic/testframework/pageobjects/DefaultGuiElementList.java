package eu.tsystems.mms.tic.testframework.pageobjects;

public class DefaultGuiElementList extends AbstractGuiElementList<IGuiElement> {
    private GuiElement guiElement;

    public DefaultGuiElementList(GuiElement guiElement) {
        super(guiElement);
        this.guiElement = guiElement;
    }

    @Override
    public IGuiElement get(int i) {
        return new GuiElement(guiElement, i);
    }
}
