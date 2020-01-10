package eu.tsystems.mms.tic.testframework.clickpath;

public class ClickPathEvent {
    public enum Type {
        WINDOW,
        CLICK,
        VALUE,
        PAGE,
        URL
    }

    private final Type type;
    private final String sessionId;
    private final String subject;

    ClickPathEvent(Type type, String sessionId, String subject) {
        this.type = type;
        this.sessionId = sessionId;
        this.subject = subject;
    }

    public Type getType() {
        return type;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getSubject() {
        return subject;
    }
}
