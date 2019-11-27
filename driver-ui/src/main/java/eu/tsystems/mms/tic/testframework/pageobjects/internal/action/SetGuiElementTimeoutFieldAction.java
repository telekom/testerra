package eu.tsystems.mms.tic.testframework.pageobjects.internal.action;

import eu.tsystems.mms.tic.testframework.pageobjects.AbstractPage;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;

import java.lang.reflect.Field;

/**
 * Created by rnhb on 02.02.2016.
 * @deprecated Use {@link Check#timeout()} instead
 */
@Deprecated
public class SetGuiElementTimeoutFieldAction extends FieldAction {

    public SetGuiElementTimeoutFieldAction(Field field, AbstractPage declaringPage) {
        super(field, declaringPage);
    }

    @Override
    public boolean before() {
        return true;
    }

    @Override
    public void execute() {
        Class<?> typeOfField = field.getType();
        if (GuiElement.class.isAssignableFrom(typeOfField)) {
            GuiElement guiElement = null;
            try {
                guiElement = (GuiElement) field.get(declaringPage);
            } catch (IllegalAccessException e) {
                logger.error("Failed to set element timeout to " + field + ". Make sure the field was made accessible in the" +
                        " AbstractPage class before calling this method.");
            }
            int timeoutFromPage = declaringPage.getElementTimeoutInSeconds();
            if (guiElement != null) {
                int alreadySetTimeout = guiElement.getTimeoutInSeconds();
                if (alreadySetTimeout != timeoutFromPage) {
                    // override timeout setting only if it is set to default
                    logger.info(String.format("Setting page specific timeout of {%s.%s} to %ds",declaringPage,guiElement,timeoutFromPage));
                    guiElement.setTimeoutInSeconds(timeoutFromPage);
                }
            }
        }
    }

    @Override
    public void after() {

    }
}
