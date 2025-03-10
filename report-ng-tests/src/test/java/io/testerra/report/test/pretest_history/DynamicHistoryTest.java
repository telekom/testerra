package io.testerra.report.test.pretest_history;

import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import org.testng.annotations.Test;

import static eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider.PROPERTY_MANAGER;

public class DynamicHistoryTest extends TesterraTest implements AssertProvider {

    @Test
    public void test_highFlakiness() {
        int historyIndex = Math.toIntExact(PROPERTY_MANAGER.getLongProperty("history.index"));
        System.out.println(historyIndex);
        boolean x = (historyIndex % 2 != 0);
        ASSERT.assertTrue(x);
    }

    @Test
    public void test_passedToFailed() {
        int historyIndex = Math.toIntExact(PROPERTY_MANAGER.getLongProperty("history.index"));
        ASSERT.assertTrue(historyIndex <= 5);
    }

    @Test
    public void test_longDuration() {
        int historyIndex = Math.toIntExact(PROPERTY_MANAGER.getLongProperty("history.index"));
        switch (historyIndex) {
            case 5:
                TimerUtils.sleep(3000);
                break;
            case 10:
                TimerUtils.sleep(5000);
                break;
            default:
                TimerUtils.sleep(2000);
                break;
        }
        ASSERT.assertTrue(true);
    }

    @Test
    public void test_multipleFailureAspects() {
        int historyIndex = Math.toIntExact(PROPERTY_MANAGER.getLongProperty("history.index"));
        switch (historyIndex) {
            case 5:
                ASSERT.assertTrue(false);
                break;
            case 7:
            case 10:
                ASSERT.assertEquals(42, 7);
                break;
            default:
                ASSERT.assertEquals(1, 2);
                break;
        }
    }

    @Test
    public void test_addedLater() {
        ASSERT.assertTrue(true);
    }

    @Test
    public void test_removedLater() {
        ASSERT.assertTrue(true);
    }
}
