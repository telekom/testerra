package eu.tsystems.mms.tic.testframework.report.model.context;

public interface CustomContext {
    
    default String getName() {
        return this.getClass().getCanonicalName();
    }
}
