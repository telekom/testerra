package eu.tsystems.mms.tic.testframework.execution.worker.finish;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;

/**
 * Created on 28.01.2022
 *
 * @author mgn
 */
public class PageFactoryBufferWorker implements MethodEndEvent.Listener {

    /**
     * Clear the PageFactory Loop detection buffer at the end of every method
     *
     * @param event
     */
    @Override
    @Subscribe
    public void onMethodEnd(MethodEndEvent event) {
//        PageFactory.clearLoopDetectionBuffer();
    }
}
