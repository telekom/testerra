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
    public boolean hasTimeout() {
        return threadLocalTimeout.get()!=null;
    }

    @Override
    public int getTimeout() {
        Integer timeout = threadLocalTimeout.get();
        if (timeout==null) {
            timeout = UiElement.Properties.ELEMENT_TIMEOUT_SECONDS.asLong().intValue();
        }
        return timeout;
    }

    @Override
    public int setTimeout(int seconds) {
        int prevTimeout = -1;
        if (hasTimeout()) {
            prevTimeout = getTimeout();
        }
        if (seconds < 0) {
            threadLocalTimeout.remove();
        } else {
            threadLocalTimeout.set(seconds);
        }
        return prevTimeout;
    }

    @Override
    public boolean hasCheckRule() {
        return threadLocalCheckRule.get()!=null;
    }

    @Override
    public CheckRule getCheckRule() {
        CheckRule checkRule = threadLocalCheckRule.get();
        if (checkRule == null) {
            checkRule = CheckRule.valueOf(GuiElement.Properties.CHECK_RULE.asString());
        }
        return checkRule;
    }

    @Override
    public CheckRule setCheckRule(CheckRule checkRule) {
        CheckRule prevCheckRule = null;
        if (hasCheckRule()) {
            prevCheckRule = getCheckRule();
        }
        if (checkRule == null) {
            threadLocalCheckRule.remove();
        } else {
            threadLocalCheckRule.set(checkRule);
        }
        return prevCheckRule;
    }
}
