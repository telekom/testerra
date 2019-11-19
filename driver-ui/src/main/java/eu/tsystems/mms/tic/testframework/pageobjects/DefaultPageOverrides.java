package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;

/**
 * @todo Implement thread local reset
 * @author Mike Reiche
 */
public class DefaultPageOverrides implements PageOverrides {

    private final ThreadLocal<Integer> threadLocalTimeout = new ThreadLocal<>();
    private final ThreadLocal<CheckRule> threadLocalCheckRule = new ThreadLocal<>();

    DefaultPageOverrides() {
    }

    @Override
    public int getElementTimeoutInSeconds() {
        Integer timeout = threadLocalTimeout.get();
        if (timeout==null) {
            timeout = IGuiElement.Properties.ELEMENT_TIMEOUT_SECONDS.asLong().intValue();
        }
        return timeout;
    }

    @Override
    public int setElementTimeoutInSeconds(int elementTimeoutInSeconds) {
        int prevTimeout = getElementTimeoutInSeconds();
        threadLocalTimeout.set(elementTimeoutInSeconds);
        return prevTimeout;
    }

    @Override
    public CheckRule getGuiElementCheckRule() {
        CheckRule checkRule = threadLocalCheckRule.get();
        if (checkRule==null) {
            checkRule = CheckRule.valueOf(GuiElement.Properties.CHECK_RULE.asString());
        }
        return checkRule;
    }

    @Override
    public CheckRule setGuiElementCheckRule(CheckRule guiElementCheckRule) {
        CheckRule prevCheckRule = getGuiElementCheckRule();
        threadLocalCheckRule.set(guiElementCheckRule);
        return prevCheckRule;
    }
}
