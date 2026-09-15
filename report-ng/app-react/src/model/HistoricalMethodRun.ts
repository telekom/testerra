import {MethodContext, ResultStatusType} from "./report-model/framework_pb.ts";
import type {HistoricalMethod} from "./HistoricalMethod.ts";

export class HistoricalMethodRun {
    private readonly _historyIndex: number = 0;
    private readonly _context: MethodContext;
    private readonly _failureAspects: string[];
    private readonly _combinedErrorMessage: string;

    constructor(historicalMethod: HistoricalMethod, index: number) {
        this._historyIndex = index;
        this._context = historicalMethod.context;
        this._failureAspects = historicalMethod.getFailureAspects();
        this._combinedErrorMessage = historicalMethod.getCombinedErrorMessage();
    }

    // Returns the overall status for flakiness calculation
    getParsedResultStatus(): ResultStatusType {
        let status = this._context.resultStatus ?? ResultStatusType.NO_RUN;
        if (status === ResultStatusType.FAILED_RETRIED) {
            status = ResultStatusType.FAILED;
        } else if (status === ResultStatusType.PASSED_RETRY || status === ResultStatusType.REPAIRED) {
            status = ResultStatusType.PASSED;
        }
        return status;
    }

    get historyIndex() {
        return this._historyIndex;
    }

    get context() {
        return this._context;
    }

    get failureAspects() {
        return this._failureAspects;
    }

    get combinedErrorMessage() {
        return this._combinedErrorMessage;
    }
}
