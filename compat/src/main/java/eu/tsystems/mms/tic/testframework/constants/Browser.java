package eu.tsystems.mms.tic.testframework.constants;
/**
 * @deprecated Use {@link Browsers} instead
 */
@Deprecated
public enum Browser {
    /**
     * Firefox.
     */
    firefox,
    /**
     * Chrome.
     */
    chrome,
    chromeHeadless,
    /**
     * Internet Exporer.
     */
    ie,
    /**
     * HTMLUnit.
     */
    @Deprecated
    htmlunit,
    phantomjs,
    /**
     * Safari.
     */
    safari,
    edge,
    mobile_safari(true),
    mobile_chrome(true),

    native_uia;

    /**
     * Name of the browser, when launching in SeeTest. Having that information here seems wrong, we might need a better mechanism
     * for deciding, which of the underlying automation drivers to use.
     */
    private final boolean isMobile;

    Browser() {
        isMobile = false;
    }

    Browser(boolean isMobile) {
        this.isMobile = isMobile;
    }

    public boolean isMobile() {
        return isMobile;
    }

    /**
     * Check if a String represents the browser object.
     *
     * @param stringToCompare Any String.
     * @return true if it is a representation.
     */
    public boolean is(final String stringToCompare) {
        if (this.name().equalsIgnoreCase(stringToCompare)) {
            return true;
        } else {
            return false;
        }
    }
}
