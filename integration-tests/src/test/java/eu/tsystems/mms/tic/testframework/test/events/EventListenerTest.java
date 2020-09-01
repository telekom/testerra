package eu.tsystems.mms.tic.testframework.test.events;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.events.InterceptMethodsEvent;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.events.MethodStartEvent;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class EventListenerTest extends TesterraTest {

    public class MyListener implements
            MethodStartEvent.Listener,
            MethodEndEvent.Listener,
            InterceptMethodsEvent.Listener
    {

        public MethodStartEvent startEvent;
        public MethodEndEvent endEvent;

        @Override
        @Subscribe
        public void onMethodEnd(MethodEndEvent event) {
            endEvent = event;
        }

        @Override
        @Subscribe
        public void onMethodStart(MethodStartEvent event) {
            startEvent = event;
        }

        @Override
        @Subscribe
        public void onInterceptMethods(InterceptMethodsEvent event) {
            event.getMethodInstances().removeIf(iMethodInstance -> iMethodInstance.getMethod().getMethodName().equals("testToBeIgnored"));
        }
    }

    private MyListener listener;

    @BeforeSuite
    public void setupListener() {
        listener = new MyListener();
        TesterraListener.getEventBus().register(listener);
    }

    @Test
    public void testToBeIgnored() {
        Assert.assertTrue(false, "This test should be filtered and never be called");
    }

    @Test
    public void testCalled() {
        // I just do nothing, sorry
    }

    @AfterClass
    public void checkListener() {
        Assert.assertNotNull(listener);
        Assert.assertNotNull(listener.startEvent);
        Assert.assertNotNull(listener.endEvent);
        TesterraListener.getEventBus().unregister(listener);
    }
}
