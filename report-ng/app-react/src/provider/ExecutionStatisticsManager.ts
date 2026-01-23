import type {ExecutionAggregate} from "./report-model/report_pb.ts";
import type {RunConfig} from "./report-model/framework_pb.ts";

export class ExecutionStatisticsManager {
    private runConfig: RunConfig | undefined = undefined;

    private executionAggregate: ExecutionAggregate;

    constructor(executionAggregate: ExecutionAggregate) {
        this.executionAggregate = executionAggregate;
        this.init();
    }

    private init() {
        this.runConfig = this.executionAggregate.executionContext?.runConfig;
    }

    public getRunConfig() {
        return this.runConfig;
    }
}
