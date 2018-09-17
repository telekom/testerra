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
public final class fennecEventUserDataManager {

    private fennecEventUserDataManager() {
    }

    /**
     * Global data.
     */
    private static final Map<IfennecEventDataType, Object> GLOBAL_DATA = new ConcurrentHashMap<IfennecEventDataType, Object>();

    /**
     * Thread local data.
     */
    private static final ThreadLocal<Map<IfennecEventDataType, Object>> THREAD_LOCAL_DATA = new ThreadLocal<Map<IfennecEventDataType, Object>>();

    /**
     * Access the global data.
     *
     * @return Map.
     */
    public static Map<IfennecEventDataType, Object> getGlobalData() {
        return GLOBAL_DATA;
    }

    /**
     * Access the thread local data.
     *
     * @return Map.
     */
    public static Map<IfennecEventDataType, Object> getThreadLocalData() {
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
