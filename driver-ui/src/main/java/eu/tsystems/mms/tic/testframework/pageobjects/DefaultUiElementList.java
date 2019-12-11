package eu.tsystems.mms.tic.testframework.pageobjects;

public class DefaultUiElementList extends AbstractUiElementList<UiElement> {
    private GuiElement guiElement;

    public DefaultUiElementList(GuiElement guiElement) {
        super(guiElement);
        this.guiElement = guiElement;
    }

    @Override
    public UiElement get(int i) {
        return new GuiElement(guiElement, i);
    }
}
