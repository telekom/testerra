package eu.tsystems.mms.tic.testframework.events;

public class BeforeShutdownWebDriverSessionsEvent extends AbstractWebDriverShutdownEvent {

    public interface Listener {
        void onBeforeShutdownWebDriverSessionsEvent(BeforeShutdownWebDriverSessionsEvent event);
    }

    public BeforeShutdownWebDriverSessionsEvent(MethodEndEvent methodEndEvent) {
        super(methodEndEvent);
    }
}
