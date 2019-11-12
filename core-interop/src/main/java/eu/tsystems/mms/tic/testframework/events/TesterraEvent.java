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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.events;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: peter
 * Date: 14.10.13
 * Time: 23:09
 * To change this template use File | Settings | File Templates.
 */
public final class TesterraEvent {

    /**
     * Event type constant.
     */
    public static final String EVENT_TYPE = "EVENT_TYPE";

    /**
     * Event type.
     */
    private final ITesterraEventType testerraEventType;

    /**
     * Stored data.
     */
    private final Map<ITesterraEventDataType, Object> data = new HashMap<ITesterraEventDataType, Object>(); // make thread safe

    /**
     * Constructor.
     *
     * @param testerraEventType Event type.
     */
    public TesterraEvent(final ITesterraEventType testerraEventType) {
        this.testerraEventType = testerraEventType;
    }

    /**
     * Add a data set.
     *
     * @param key .
     * @param value .
     * @return The Event itself.
     */
    public TesterraEvent addData(final ITesterraEventDataType key, final Object value) {
        data.put(key, value);
        return this;
    }

    /**
     * Return the event type.
     *
     * @return .
     */
    public ITesterraEventType getTesterraEventType() {
        return testerraEventType;
    }

    /**
     * Return all stored data.
     *
     * @return Map.
     */
    public Map<ITesterraEventDataType, Object> getData() {
        return data;
    }

    /**
     * Add user data.
     *
     * @return This event for chaining.
     */
    public TesterraEvent addUserData() {
        Map<ITesterraEventDataType, Object> globalData = TesterraEventUserDataManager.getGlobalData();
        data.putAll(globalData);

        Map<ITesterraEventDataType, Object> threadLocalData = TesterraEventUserDataManager.getThreadLocalData();
        data.putAll(threadLocalData);

        return this;
    }

    @Override
    public String toString() {
        return "TesterraEvent{" +
                "EventType=" + testerraEventType +
                '}';
    }
}
