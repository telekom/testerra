package eu.tsystems.mms.tic.testframework.pageobjects.components;

import eu.tsystems.mms.tic.testframework.pageobjects.Component;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.TestableGuiElement;
import org.openqa.selenium.By;

public class TableRow extends Component<TableRow> {
    public TableRow(IGuiElement rootElement) {
        super(rootElement);
    }

    @Override
    protected TableRow self() {
        return this;
    }

    public TestableGuiElement linkByName() {
        return find(By.tagName("a"));
    }

    public TestableGuiElement linkByXPath() {
        return find(By.xpath(".//a"));
    }
}
