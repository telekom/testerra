package eu.tsystems.mms.tic.testframework.events;

import eu.tsystems.mms.tic.testframework.report.model.context.SynchronizableContext;

public class ContextUpdateEvent {
    public interface Listener {
        void onContextUpdate(ContextUpdateEvent event);
    }

    private SynchronizableContext context;

    public SynchronizableContext getContext() {
        return context;
    }

    public ContextUpdateEvent setContext(SynchronizableContext context) {
        this.context = context;
        return this;
    }
}
