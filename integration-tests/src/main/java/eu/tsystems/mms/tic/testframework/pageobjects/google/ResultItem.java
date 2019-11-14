package eu.tsystems.mms.tic.testframework.pageobjects.google;

import eu.tsystems.mms.tic.testframework.pageobjects.Component;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.TestableGuiElement;
import org.openqa.selenium.By;

public class ResultItem extends Component {
    public ResultItem(IGuiElement rootElement) {
        super(rootElement);
    }

    public TestableGuiElement headline() {
        return find(By.className("S3Uucc"));
    }
}
