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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: peter
 * Date: 14.10.13
 * Time: 23:09
 * To change this template use File | Settings | File Templates.
 */
public final class fennecEvent {

    /**
     * Event type constant.
     */
    public static final String fennec_EVENT_TYPE = "fennec_EVENT_TYPE";

    /**
     * Event type.
     */
    private final IfennecEventType fennecEventType;

    /**
     * Stored data.
     */
    private final Map<IfennecEventDataType, Object> data = new HashMap<IfennecEventDataType, Object>(); // make thread safe

    /**
     * Constructor.
     *
     * @param fennecEventType Event type.
     */
    public fennecEvent(final IfennecEventType fennecEventType) {
        this.fennecEventType = fennecEventType;
    }

    /**
     * Add a data set.
     *
     * @param key .
     * @param value .
     * @return The Event itself.
     */
    public fennecEvent addData(final IfennecEventDataType key, final Object value) {
        data.put(key, value);
        return this;
    }

    /**
     * Return the fennec event type.
     *
     * @return .
     */
    public IfennecEventType getfennecEventType() {
        return fennecEventType;
    }

    /**
     * Return all stored data.
     *
     * @return Map.
     */
    public Map<IfennecEventDataType, Object> getData() {
        return data;
    }

    /**
     * Add user data.
     *
     * @return This event for chaining.
     */
    public fennecEvent addUserData() {
        Map<IfennecEventDataType, Object> globalData = fennecEventUserDataManager.getGlobalData();
        data.putAll(globalData);

        Map<IfennecEventDataType, Object> threadLocalData = fennecEventUserDataManager.getThreadLocalData();
        data.putAll(threadLocalData);

        return this;
    }

    @Override
    public String toString() {
        return "fennecEvent{" +
                "fennecEventType=" + fennecEventType +
                '}';
    }
}
