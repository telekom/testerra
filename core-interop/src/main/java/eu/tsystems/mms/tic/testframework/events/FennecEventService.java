/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.events;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: peter
 * Date: 14.10.13
 * Time: 23:04
 * To change this template use File | Settings | File Templates.
 */
public final class FennecEventService implements IFennecEventListener {

    private static final FennecEventService INSTANCE = new FennecEventService();

    private static final Logger LOGGER = LoggerFactory.getLogger(FennecEventService.class);

    private static List<IFennecEventListener> listeners = new ArrayList<IFennecEventListener>(1);

    /**
     * Hidden constructor.
     */
    private FennecEventService() {
    }

    /**
     * adds fennec event listener
     *
     * @param FennecEventListener .
     */
    public static void addListener(IFennecEventListener FennecEventListener) {

        final String FennecEventListenerName = FennecEventListener.getClass().getSimpleName();

        for (IFennecEventListener listener : listeners) {
            if (listener.getClass().equals(FennecEventListener.getClass())) {
                LOGGER.warn("Listener" + FennecEventListenerName + " already added to FennecEventService.");
                return;
            }
        }

        LOGGER.info("Adding listener to FennecEventService: " + FennecEventListenerName);
        listeners.add(FennecEventListener);
    }

    /**
     * removes fennec event listener
     *
     * @param FennecEventListener .
     */
    public static void removeListener(IFennecEventListener FennecEventListener) {
        listeners.remove(FennecEventListener);
    }

    /**
     *
     * @param FennecEvent .
     */
    public void fireEvent(final FennecEvent FennecEvent) {
        IFennecEventType fennecEventType = FennecEvent.getFennecEventType();
        for (final IFennecEventListener listener : listeners) {
            LOGGER.debug("Firing event " + FennecEvent + " for " + listener);
            if (fennecEventType == FennecEventType.TEST_END) {
                // not thread at test end
                listener.fireEvent(FennecEvent);
            } else {

                // TODO: does it have perf effects when not running as a thread?
//                Thread thread = new Thread(new Runnable() {
//                    public void run() {
//                        listener.fireEvent(FennecEvent);
//                    }
//                });
//                thread.start();

                listener.fireEvent(FennecEvent);
            }
        }
    }

    public static FennecEventService getInstance() {
        return INSTANCE;
    }

    public static boolean hasListenerOfThisType(IFennecEventListener listener) {
        for (IFennecEventListener iFennecEventListener : listeners) {
            if (iFennecEventListener.getClass() == listener.getClass()) {
                return true;
            }
        }
        return false;
    }
}
