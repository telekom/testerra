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
    public int getElementTimeoutInSeconds(int fallbackTimeout) {
        Integer timeout = threadLocalTimeout.get();
        if (timeout==null) {
            timeout = fallbackTimeout;
        }
        return timeout;
    }

    @Override
    public int setElementTimeoutInSeconds(int elementTimeoutInSeconds) {
        int prevTimeout = threadLocalTimeout.get();
        threadLocalTimeout.set(elementTimeoutInSeconds);
        return prevTimeout;
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
}
