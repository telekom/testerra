import type {MethodHistoryStatistics} from "./MethodHistoryStatistics.ts";
import {ResultStatusType} from "./report-model/framework_pb.ts";

export class ClassHistory {
    private readonly _identifier: string;
    private _methods: MethodHistoryStatistics[] = [];

    constructor(
        identifier: string
    ) {
        this._identifier = identifier;
    }

    addMethod(method: MethodHistoryStatistics) {
        this._methods.push(method);
    }

    get availableFinalStatuses() {
        const availableFinalStatuses: Set<ResultStatusType> = new Set();
        this._methods
            .filter(method => method.isTestMethod())
            .forEach(method => {
                method.availableStatuses.forEach(status => {
                    if (status === ResultStatusType.FAILED_RETRIED) {
                        return;
                    }
                    availableFinalStatuses.add(
                        status === ResultStatusType.REPAIRED || status === ResultStatusType.PASSED_RETRY
                            ? ResultStatusType.PASSED
                            : status
                    );
                });
            });
        return Array.from(availableFinalStatuses);
    }

    get identifier() {
        return this._identifier;
    }

    get methods() {
        return this._methods;
    }
}
