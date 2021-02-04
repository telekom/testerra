package eu.tsystems.mms.tic.testframework.events;

public class AfterShutdownWebDriverSessionsEvent extends AbstractWebDriverShutdownEvent {

    public interface Listener {
        void onAfterShutdownWebDriverSessionsEvent(AfterShutdownWebDriverSessionsEvent event);
    }

    public AfterShutdownWebDriverSessionsEvent(MethodEndEvent methodEndEvent) {
        super(methodEndEvent);
    }
}
