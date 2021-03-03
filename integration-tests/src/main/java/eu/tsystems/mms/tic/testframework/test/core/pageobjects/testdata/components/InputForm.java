package eu.tsystems.mms.tic.testframework.test.core.pageobjects.testdata.components;

import eu.tsystems.mms.tic.testframework.pageobjects.AbstractComponent;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.InteractiveUiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;

public class InputForm extends AbstractComponent<InputForm> {

    @Check
    private UiElement button = find(By.className("component-btn")).setName("submitButton");

    private UiElement input = findByQa("input/text");

    private TableRow tableRow = createComponent(TableRow.class, createEmpty());

    public InputForm(UiElement rootElement) {
        super(rootElement);
    }

    public InteractiveUiElement button() {
        return button;
    }

    public InteractiveUiElement input() {
        return input;
    }

    public TableRow getTableRow() {
        return tableRow;
    }
}
