package eu.tsystems.mms.tic.testframework.events;

public abstract class AbstractWebDriverShutdownEvent {
    private final MethodEndEvent methodEndEvent;

    AbstractWebDriverShutdownEvent(MethodEndEvent methodEndEvent) {
        this.methodEndEvent = methodEndEvent;
    }

    public MethodEndEvent getMethodEndEvent() {
        return methodEndEvent;
    }
}
