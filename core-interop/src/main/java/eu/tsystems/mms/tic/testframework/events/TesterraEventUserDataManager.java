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
/*
 * Created on 07.01.14
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.events;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public final class TesterraEventUserDataManager {

    private TesterraEventUserDataManager() {
    }

    /**
     * Global data.
     */
    private static final Map<ITesterraEventDataType, Object> GLOBAL_DATA = new ConcurrentHashMap<ITesterraEventDataType, Object>();

    /**
     * Thread local data.
     */
    private static final ThreadLocal<Map<ITesterraEventDataType, Object>> THREAD_LOCAL_DATA = new ThreadLocal<Map<ITesterraEventDataType, Object>>();

    /**
     * Access the global data.
     *
     * @return Map.
     */
    public static Map<ITesterraEventDataType, Object> getGlobalData() {
        return GLOBAL_DATA;
    }

    /**
     * Access the thread local data.
     *
     * @return Map.
     */
    public static Map<ITesterraEventDataType, Object> getThreadLocalData() {
        if (THREAD_LOCAL_DATA.get() == null) {
            THREAD_LOCAL_DATA.set(new ConcurrentHashMap<>(1));
        }

        return THREAD_LOCAL_DATA.get();
    }

    /**
     * Cleanup.
     */
    public static void cleanupThreadLocalData() {
        THREAD_LOCAL_DATA.remove();
    }

    /**
     * Cleanup.
     */
    public static void cleanupAllData() {
        cleanupThreadLocalData();
        GLOBAL_DATA.clear();
    }

}
