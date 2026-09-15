import {Statistics} from "./Statistics.ts";
import type {HistoricalMethod} from "./HistoricalMethod.ts";
import {MethodType, ResultStatusType} from "./report-model/framework_pb.ts";

export class ClassHistoryStatistics extends Statistics {
    private readonly _identifier: string;
    private _methods: HistoricalMethod[] = [];

    constructor(
        identifier: string
    ) {
        super();
        this._identifier = identifier;
    }

    addMethod(method: HistoricalMethod) {
        if (method.context.contextValues?.endTime != 0) {
            this._methods.push(method);
            if (method.context.methodType === MethodType.TEST_METHOD) {
                this.addResultStatus(method.context.resultStatus ?? ResultStatusType.NO_RUN);
            }
        }
    }

    get methods() {
        return this._methods;
    }

    get identifier() {
        return this._identifier;
    }
}
