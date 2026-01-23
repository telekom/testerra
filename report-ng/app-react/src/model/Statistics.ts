import type {ResultStatusType} from "../provider/report-model/framework_pb.ts";
import { data } from "../provider/report-model";
import {StatusConverter} from "./status-converter.ts";

export class Statistics {
    private _resultStatuses: { [key: number]: number } = {};

    addResultStatus(status: ResultStatusType) {
        if (!this._resultStatuses[status]) {
            this._resultStatuses[status] = 0;
        }
        this._resultStatuses[status]++;
    }

    get availableStatuses(): ResultStatusType[] {
        const statuses = [];
        for (const status in this._resultStatuses) {
            statuses.push(status);
        }
        return statuses;
    }

    getStatusCount(status: ResultStatusType) {
        return this._resultStatuses[status] | 0;
    }

    getUpmostStatus(): number {
        let count = 0;
        let upmostStatus: any;
        for (let status in this._resultStatuses) {
            if (this._resultStatuses[status] > count) {
                upmostStatus = status;
            }
        }
        return (upmostStatus == data.ResultStatusType.FAILED_EXPECTED && this._resultStatuses.hasOwnProperty(data.ResultStatusType.FAILED))
            ? data.ResultStatusType.FAILED : upmostStatus; // if there is the same failure aspect for one failed and one expected failed test, the failed class color should be displayed
    }

    protected addStatistics(statistics: Statistics) {
        for (const status in statistics._resultStatuses) {
            if (!this._resultStatuses[status]) {
                this._resultStatuses[status] = 0;
            }
            this._resultStatuses[status] += statistics._resultStatuses[status];
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

    getSummarizedStatusCount(statuses: number[]) {
        let count = 0;
        statuses.forEach(value => {
            count += this.getStatusCount(value);
        })
        return count;
    }
}
