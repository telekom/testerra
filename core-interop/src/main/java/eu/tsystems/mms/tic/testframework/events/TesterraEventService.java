/*
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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: peter
 * Date: 14.10.13
 * Time: 23:04
 * To change this template use File | Settings | File Templates.
 */
public final class TesterraEventService implements TesterraEventListener {

    private static final TesterraEventService INSTANCE = new TesterraEventService();

    private static final Logger LOGGER = LoggerFactory.getLogger(TesterraEventService.class);

    private static List<TesterraEventListener> listeners = new ArrayList<TesterraEventListener>(1);

    /**
     * Hidden constructor.
     */
    private TesterraEventService() {
    }

    /**
     * adds event listener
     *
     * @param TesterraEventListener .
     */
    public static void addListener(TesterraEventListener TesterraEventListener) {

        final String TesterraEventListenerName = TesterraEventListener.getClass().getSimpleName();

        for (TesterraEventListener listener : listeners) {
            if (listener.getClass().equals(TesterraEventListener.getClass())) {
                LOGGER.warn("Listener" + TesterraEventListenerName + " already added");
                return;
            }
        }

        LOGGER.debug("Add listener: " + TesterraEventListenerName);
        listeners.add(TesterraEventListener);
    }

    /**
     * removes event listener
     *
     * @param testerraEventListener .
     */
    public static void removeListener(TesterraEventListener testerraEventListener) {
        listeners.remove(testerraEventListener);
    }

    /**
     *
     * @param TesterraEvent .
     */
    public void fireEvent(final TesterraEvent TesterraEvent) {
        ITesterraEventType eventType = TesterraEvent.getTesterraEventType();
        for (final TesterraEventListener listener : listeners) {
            LOGGER.debug("Firing event " + TesterraEvent + " for " + listener);
            if (eventType == TesterraEventType.TEST_END) {
                // not thread at test end
                listener.fireEvent(TesterraEvent);
            } else {
                listener.fireEvent(TesterraEvent);
            }
        }
    }

    public static TesterraEventService getInstance() {
        return INSTANCE;
    }

    public static boolean hasListenerOfThisType(TesterraEventListener listener) {
        for (TesterraEventListener testerraEventListener : listeners) {
            if (testerraEventListener.getClass() == listener.getClass()) {
                return true;
            }
        }
        return false;
    }
}
