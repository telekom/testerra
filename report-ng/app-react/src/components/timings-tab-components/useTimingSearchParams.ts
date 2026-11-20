/*
 * Testerra
 *
 * (C) 2026, Selina Natschke, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import {useCallback, useEffect} from "react";
import {useSearchParams} from "react-router-dom";

const DEFAULT_RANGE_NUM = 10;

export function useTimingSearchParams() {
    const [searchParams, setSearchParams] = useSearchParams();

    // Read filter state from URL so view state is shareable/bookmarkable.
    const rangeNumParam = searchParams.get("rangeNum");
    const parsedRangeNum = Number(rangeNumParam);
    const rangeNum = parsedRangeNum > 0 ? parsedRangeNum : DEFAULT_RANGE_NUM;
    const showConfigMethods = searchParams.get("config") === "true";
    const methodIdParam = searchParams.get("methodId");

    useEffect(() => {
        if (rangeNumParam !== null) {
            return;
        }

        // Keep legacy behavior: ensure rangeNum is explicitly present in URL.
        const params = new URLSearchParams(searchParams);
        params.set("rangeNum", String(DEFAULT_RANGE_NUM));
        setSearchParams(params, {replace: true});
    }, [rangeNumParam, searchParams, setSearchParams]);

    const handleRangeNumChange = useCallback((value: number) => {
        const params = new URLSearchParams(searchParams);
        params.set("rangeNum", String(value));
        setSearchParams(params);
    }, [searchParams, setSearchParams]);

    const handleShowConfigMethodsChange = useCallback((checked: boolean) => {
        const params = new URLSearchParams(searchParams);
        if (checked) {
            params.set("config", "true");
        } else {
            params.delete("config");
        }
        setSearchParams(params);
    }, [searchParams, setSearchParams]);

    const handleSelectedMethodIdChange = useCallback((methodId: string | null) => {
        const params = new URLSearchParams(searchParams);
        if (methodId) {
            params.set("methodId", methodId);
        } else {
            params.delete("methodId");
        }
        setSearchParams(params);
    }, [searchParams, setSearchParams]);

    return {
        rangeNum,
        showConfigMethods,
        methodIdParam,
        handleRangeNumChange,
        handleShowConfigMethodsChange,
        handleSelectedMethodIdChange,
    };
}
