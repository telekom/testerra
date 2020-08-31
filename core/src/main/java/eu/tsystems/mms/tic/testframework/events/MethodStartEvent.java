package eu.tsystems.mms.tic.testframework.events;

/**
 * Called on start of every test method annotated by TestNG @Test annotation, but before the execution of registered Before Method Worker and after all TestNG configuration methods like @BeforeMethod.
 */
public class MethodStartEvent extends MethodEvent {
    public interface Listener {
        void onMethodStart(MethodStartEvent event);
    }
}
