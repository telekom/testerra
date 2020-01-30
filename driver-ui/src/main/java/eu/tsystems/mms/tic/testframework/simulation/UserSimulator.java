package eu.tsystems.mms.tic.testframework.simulation;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementActions;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import org.openqa.selenium.WebElement;

/**
 * Simulates a user
 * @author Mike Reiche
 */
public class UserSimulator extends AbstractSimulator {

    public UserSimulator(GuiElement guiElement) {
        super(guiElement);
    }

    @Override
    public UiElementActions sendKeys(CharSequence... charSequences) {
        float cps = UiElement.Properties.USER_INPUT_CPM.asLong()/60;
        if (cps <= 0) cps = 1;
        int cpsSleepMs = Math.round(1000/cps);
        final WebElement webElement = guiElement.getCore().findWebElement();
        for (CharSequence charSequence : charSequences) {
            charSequence.codePoints().forEach(codePoint -> {
                webElement.sendKeys(new String(Character.toChars(codePoint)));
                TimerUtils.sleepSilent(cpsSleepMs);
            });
        }
        return this;
    }
}
