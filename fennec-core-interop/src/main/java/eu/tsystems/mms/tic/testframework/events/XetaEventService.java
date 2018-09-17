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
public final class fennecEventService implements IfennecEventListener {

    private static final fennecEventService INSTANCE = new fennecEventService();

    private static final Logger LOGGER = LoggerFactory.getLogger(fennecEventService.class);

    private static List<IfennecEventListener> listeners = new ArrayList<IfennecEventListener>(1);

    /**
     * Hidden constructor.
     */
    private fennecEventService() {
    }

    /**
     * adds fennec event listener
     *
     * @param fennecEventListener .
     */
    public static void addListener(IfennecEventListener fennecEventListener) {

        final String fennecEventListenerName = fennecEventListener.getClass().getSimpleName();

        for (IfennecEventListener listener : listeners) {
            if (listener.getClass().equals(fennecEventListener.getClass())) {
                LOGGER.warn("Listener" + fennecEventListenerName + " already added to fennecEventService.");
                return;
            }
        }

        LOGGER.info("Adding listener to fennecEventService: " + fennecEventListenerName);
        listeners.add(fennecEventListener);
    }

    /**
     * removes fennec event listener
     *
     * @param fennecEventListener .
     */
    public static void removeListener(IfennecEventListener fennecEventListener) {
        listeners.remove(fennecEventListener);
    }

    /**
     *
     * @param fennecEvent .
     */
    public void fireEvent(final fennecEvent fennecEvent) {
        IfennecEventType fennecEventType = fennecEvent.getfennecEventType();
        for (final IfennecEventListener listener : listeners) {
            LOGGER.debug("Firing event " + fennecEvent + " for " + listener);
            if (fennecEventType == fennecEventType.TEST_END) {
                // not thread at test end
                listener.fireEvent(fennecEvent);
            } else {

                // TODO: does it have perf effects when not running as a thread?
//                Thread thread = new Thread(new Runnable() {
//                    public void run() {
//                        listener.fireEvent(fennecEvent);
//                    }
//                });
//                thread.start();

                listener.fireEvent(fennecEvent);
            }
        }
    }

    public static fennecEventService getInstance() {
        return INSTANCE;
    }

    public static boolean hasListenerOfThisType(IfennecEventListener listener) {
        for (IfennecEventListener ifennecEventListener : listeners) {
            if (ifennecEventListener.getClass() == listener.getClass()) {
                return true;
            }
        }
        return false;
    }
}
