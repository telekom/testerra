/*
 * Testerra
 *
 * (C) 2026, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import type {HistoryAggregateStatistics} from "../../model/HistoryAggregateStatistics.ts";
import type {ExecutionStatistics} from "../../model/ExecutionStatistics.ts";
import {HistoricalMethod} from "../../model/HistoricalMethod.ts";
import {MethodType, ResultStatusType} from "../../model/report-model/framework_pb.ts";
import {ClassName, classNameConverter} from "../../utils/classNameConverter.ts";

export interface RunMethodComparison {
    methodPath: string;
    methodName: string;
    classIdentifier: string;
    isConfiguration: boolean;
    relatedTestMethods: string[];
    selectedStatus: ResultStatusType;
    currentStatus: ResultStatusType;
    changedFailureAspect: boolean;
    currentMethodId?: string;
}

const buildRelatedMethodsKey = (relatedMethods: string[]) => Array.from(new Set(relatedMethods)).sort().join("|");

const buildMethodKey = (classIdentifier: string, method: HistoricalMethod) => {
    const relatedMethodsKey = method.context.methodType === MethodType.TEST_METHOD
        ? ""
        : buildRelatedMethodsKey(method.relatedMethods);
    return `${classIdentifier}.${method.identifier}${relatedMethodsKey ? ` [${relatedMethodsKey}]` : ""}`;
};

const indexMethods = (classes: Iterable<{identifier: string; methods: HistoricalMethod[]}>) => {
    const methods = new Map<string, {method: HistoricalMethod; classIdentifier: string}>();
    for (const cls of classes) {
        for (const method of cls.methods) {
            if (method.context.resultStatus !== ResultStatusType.FAILED_RETRIED) {
                methods.set(buildMethodKey(cls.identifier, method), {method, classIdentifier: cls.identifier});
            }
        }
    }
    return methods;
};

const buildExecutionClasses = (executionStatistics: ExecutionStatistics) => {
    const methodsById = new Map<string, HistoricalMethod>();
    const classes = executionStatistics.classStatistics.map(cls => ({
        identifier: cls.classIdentifier,
        methods: cls.methodContexts.map(context => {
            const method = new HistoricalMethod(context);
            const methodId = context.contextValues?.id;
            if (methodId) {
                methodsById.set(methodId, method);
            }
            return method;
        }),
    }));
    methodsById.forEach(method => {
        (method.context.relatedMethodContextIds ?? []).forEach(relatedId => {
            const related = methodsById.get(relatedId);
            if (related) {
                method.addRelatedMethods(related.identifier);
            }
        });
    });
    return classes;
};

export const compareRunMethods = (
    selectedRun: HistoryAggregateStatistics,
    currentRun: HistoryAggregateStatistics,
    executionStatistics: ExecutionStatistics,
): RunMethodComparison[] => {
    const selectedMethods = indexMethods(selectedRun.classes.values());
    const currentMethods = indexMethods(currentRun.classes.values());
    const executionMethods = indexMethods(buildExecutionClasses(executionStatistics));
    const methodPaths = new Set([...selectedMethods.keys(), ...currentMethods.keys()]);
    const comparisons: RunMethodComparison[] = [];

    for (const methodPath of methodPaths) {
        const selectedEntry = selectedMethods.get(methodPath);
        const currentEntry = currentMethods.get(methodPath);
        const selectedMethod = selectedEntry?.method;
        const currentMethod = currentEntry?.method;
        const selectedStatus = selectedMethod?.context.resultStatus ?? ResultStatusType.NO_RUN;
        const currentStatus = currentMethod?.context.resultStatus ?? ResultStatusType.NO_RUN;
        const changedFailureAspect = selectedMethod !== undefined && currentMethod !== undefined
            && selectedMethod.getCombinedErrorMessage() !== currentMethod.getCombinedErrorMessage();
        const entry = currentEntry ?? selectedEntry;
        if (!entry || (selectedStatus === currentStatus && !changedFailureAspect)) {
            continue;
        }
        const isConfiguration = entry.method.context.methodType === MethodType.CONFIGURATION_METHOD;
        const relatedRun = currentEntry ? currentRun : selectedRun;
        const relatedTestMethods = isConfiguration
            ? (entry.method.context.relatedMethodContextIds ?? [])
                .flatMap(id => {
                    const context = relatedRun.historyAggregate.methodContexts?.[id];
                    return context?.methodType === MethodType.TEST_METHOD
                        && context.resultStatus !== ResultStatusType.FAILED_RETRIED
                        ? [new HistoricalMethod(context).identifier]
                        : [];
                })
            : [];
        comparisons.push({
            methodPath,
            methodName: entry.method.identifier,
            classIdentifier: entry.classIdentifier,
            isConfiguration,
            relatedTestMethods: [...new Set(relatedTestMethods)],
            selectedStatus,
            currentStatus,
            changedFailureAspect,
            currentMethodId: executionMethods.get(methodPath)?.method.context.contextValues?.id,
        });
    }

    return comparisons.sort((a, b) =>
        classNameConverter(a.classIdentifier, ClassName.simpleName)
            .localeCompare(classNameConverter(b.classIdentifier, ClassName.simpleName))
        || a.methodName.localeCompare(b.methodName)
        || a.classIdentifier.localeCompare(b.classIdentifier)
    );
};
