package eu.tsystems.mms.tic.testframework.layout;

/**
 * An exception thrown for layout checks
 * @author Mike Reiche
 */
public class LayoutCheckException extends RuntimeException {
    private final LayoutCheck.MatchStep matchStep;

    public LayoutCheckException(LayoutCheck.MatchStep matchStep, Throwable cause) {
        super(cause);
        this.matchStep = matchStep;
    }

    public LayoutCheck.MatchStep getMatchStep() {
        return this.matchStep;
    }
}
