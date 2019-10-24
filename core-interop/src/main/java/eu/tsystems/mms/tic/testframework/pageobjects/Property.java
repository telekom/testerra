package eu.tsystems.mms.tic.testframework.pageobjects;

@Deprecated
public enum Property {
    DISPLAYED("displayed"),
    PRESENT("present"),
    VISIBLE("visible"),
    ATTRIBUTE("attribute"),
    VALUE("value"),
    TEXT("text"),
    SELECTED("selected"),
    LAYOUT("layout"),
    TITLE("title"),
    URL("url"),
    ;

    private final String property;

    Property(final String property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return property;
    }
}
