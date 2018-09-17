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

/**
 * Created with IntelliJ IDEA.
 * User: peter
 * Date: 14.10.13
 * Time: 23:09
 * To change this template use File | Settings | File Templates.
 */
public final class FennecEvent {

    /**
     * Event type constant.
     */
    public static final String Fennec_EVENT_TYPE = "Fennec_EVENT_TYPE";

    /**
     * Event type.
     */
    private final IFennecEventType fennecEventType;

    /**
     * Stored data.
     */
    private final Map<IFennecEventDataType, Object> data = new HashMap<IFennecEventDataType, Object>(); // make thread safe

    /**
     * Constructor.
     *
     * @param FennecEventType Event type.
     */
    public FennecEvent(final IFennecEventType FennecEventType) {
        this.fennecEventType = FennecEventType;
    }

    /**
     * Add a data set.
     *
     * @param key .
     * @param value .
     * @return The Event itself.
     */
    public FennecEvent addData(final IFennecEventDataType key, final Object value) {
        data.put(key, value);
        return this;
    }

    /**
     * Return the fennec event type.
     *
     * @return .
     */
    public IFennecEventType getFennecEventType() {
        return fennecEventType;
    }

    /**
     * Return all stored data.
     *
     * @return Map.
     */
    public Map<IFennecEventDataType, Object> getData() {
        return data;
    }

    /**
     * Add user data.
     *
     * @return This event for chaining.
     */
    public FennecEvent addUserData() {
        Map<IFennecEventDataType, Object> globalData = FennecEventUserDataManager.getGlobalData();
        data.putAll(globalData);

        Map<IFennecEventDataType, Object> threadLocalData = FennecEventUserDataManager.getThreadLocalData();
        data.putAll(threadLocalData);

        return this;
    }

    @Override
    public String toString() {
        return "FennecEvent{" +
                "fennecEventType=" + fennecEventType +
                '}';
    }
}
