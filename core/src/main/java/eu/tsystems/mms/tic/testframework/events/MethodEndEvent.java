package eu.tsystems.mms.tic.testframework.events;

/**
 * Called at the end of every test method annotated by TestNG @Test annotation, but before the execution of registered After Method Worker and the execution fo all TestNG configuration methods like @AfterMethod.
 */
public class MethodEndEvent extends AbstractMethodEvent {
    public interface Listener {
        void onMethodEnd(MethodEndEvent event);
    }
}
