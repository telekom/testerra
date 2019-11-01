package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.enums.CheckRule;

public class DefaultPageConfig implements PageConfig {

    private final ThreadLocal<Integer> threadLocalTimeout = new ThreadLocal<>();
    private final ThreadLocal<CheckRule> threadLocalCheckRule = new ThreadLocal<>();

    DefaultPageConfig() {
    }

    @Override
    public int getElementTimeoutInSeconds() {
        Integer timeout = threadLocalTimeout.get();
        if (timeout==null) {
            return Testerra.Properties.ELEMENT_TIMEOUT_SECONDS.asLong().intValue();
        } else {
            return timeout;
        }
    }

    @Override
    public PageConfig setElementTimeoutInSeconds(int uiElementTimeoutInSeconds) {
        threadLocalTimeout.set(uiElementTimeoutInSeconds);
        return this;
    }

    @Override
    public CheckRule getGuiElementCheckRule() {
        CheckRule checkRule = threadLocalCheckRule.get();
        if (checkRule==null) {
            return CheckRule.valueOf(GuiElement.Properties.CHECK_RULE.asString());
        }
        return checkRule;
    }

    @Override
    public PageConfig setGuiElementCheckRule(CheckRule guiElementCheckRule) {
        threadLocalCheckRule.set(guiElementCheckRule);
        return this;
    }
}
