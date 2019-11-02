package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;

public class DefaultPageOverrides implements PageOverrides {

    private final ThreadLocal<Integer> threadLocalTimeout = new ThreadLocal<>();
    private final ThreadLocal<CheckRule> threadLocalCheckRule = new ThreadLocal<>();

    DefaultPageOverrides() {
    }

    @Override
    public int getElementTimeoutInSeconds(int fallbackTimeout) {
        Integer timeout = threadLocalTimeout.get();
        if (timeout==null) {
            timeout = fallbackTimeout;
        }
        return timeout;
    }

    @Override
    public PageOverrides setElementTimeoutInSeconds(int elementTimeoutInSeconds) {
        threadLocalTimeout.set(elementTimeoutInSeconds);
        return this;
    }

    @Override
    public PageOverrides removeElementTimeoutInSeconds() {
        threadLocalTimeout.remove();
        return this;
    }

    @Override
    public CheckRule getGuiElementCheckRule(CheckRule fallbackCheckRule) {
        CheckRule checkRule = threadLocalCheckRule.get();
        if (checkRule==null) {
            checkRule = fallbackCheckRule;
        }
        return checkRule;
    }

    @Override
    public PageOverrides setGuiElementCheckRule(CheckRule guiElementCheckRule) {
        threadLocalCheckRule.set(guiElementCheckRule);
        return this;
    }

    @Override
    public PageOverrides removeGuiElementCheckRule() {
        threadLocalCheckRule.remove();
        return this;
    }
}
