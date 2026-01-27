import {ResultStatusType} from "./report-model/framework_pb.ts";
import {data} from "./report-model";
import {StatusConverter} from "./status-converter.ts";

export class Statistics {
    private _resultStatuses = new Map<ResultStatusType, number>;

    addResultStatus(status: ResultStatusType) {
        const currentCount = this._resultStatuses.get(status) ?? 0;
        this._resultStatuses.set(status, currentCount + 1);
    }

    get availableStatuses(): ResultStatusType[] {
        return Array.from(this._resultStatuses.keys());
    }

    getStatusCount(status: ResultStatusType) {
        return this._resultStatuses.get(status) ?? 0;
    }

    getUpmostStatus(): ResultStatusType {
        let maxCount = 0;
        let upmostStatus: ResultStatusType = ResultStatusType.NO_RUN;
        for (const [status, count] of this._resultStatuses.entries()) {
            if (count > maxCount) {
                maxCount = count;
                upmostStatus = status;
            }
        }
        return (
            upmostStatus == data.ResultStatusType.FAILED_EXPECTED && this._resultStatuses.has(data.ResultStatusType.FAILED))
            ? data.ResultStatusType.FAILED
            : upmostStatus; // if there is the same failure aspect for one failed and one expected failed test, the failed class color should be displayed
    }

    protected addStatistics(statistics: Statistics) {
        for (const [status, count] of statistics._resultStatuses) {
            const currentCount = this._resultStatuses.get(status) ?? 0;
            this._resultStatuses.set(status, currentCount + count);
        }
    }

    get overallPassed() {
        return this.getSummarizedStatusCount(StatusConverter.passedStatuses);
    }

    get overallSkipped() {
        return this.getStatusCount(data.ResultStatusType.SKIPPED);
    }

    get overallFailed() {
        return this.getStatusCount(data.ResultStatusType.FAILED);
    }

    /**
     * Returns the number of test cases including passed retried
     */
    get overallTestCases() {
        return this.getSummarizedStatusCount([
            data.ResultStatusType.PASSED,
            data.ResultStatusType.REPAIRED,
            data.ResultStatusType.PASSED_RETRY, // Recovered
            data.ResultStatusType.SKIPPED,
            data.ResultStatusType.FAILED,
            data.ResultStatusType.FAILED_EXPECTED,
            data.ResultStatusType.FAILED_RETRIED,
        ]);
    }

    getSummarizedStatusCount(statuses: ResultStatusType[]) {
        let count = 0;
        statuses.forEach(value => {
            count += this.getStatusCount(value);
        })
        return count;
    }
}
