package eu.tsystems.mms.tic.testframework.pageobjects.components;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Component;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.TestableGuiElement;
import org.openqa.selenium.By;

public class InputForm extends Component {

    @Check
    private IGuiElement button = find(By.className("component-btn"));

    public InputForm(IGuiElement rootElement) {
        super(rootElement);
    }

    public TestableGuiElement button() {
        return button;
    }
}
