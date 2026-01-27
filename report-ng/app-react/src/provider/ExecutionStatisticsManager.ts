import type {ExecutionAggregate, HistoryAggregate, LogMessageAggregate} from "./report-model/report_pb.ts";
import type {ClassContext} from "./report-model/framework_pb.ts";
import {ExecutionStatistics} from "../model/ExecutionStatistics.ts";
import {ClassStatistics} from "../model/ClassStatistics.ts";


export class ExecutionStatisticsManager {
    private readonly executionAggregate: ExecutionAggregate;
    private readonly logMessageAggregate: LogMessageAggregate;
    private readonly historyAggregate: HistoryAggregate;
    private executionStatistics: ExecutionStatistics;

    constructor(executionAggregate: ExecutionAggregate, logMessageAggregate: LogMessageAggregate, historyAggregate: HistoryAggregate) {
        this.executionAggregate = executionAggregate;
        this.logMessageAggregate = logMessageAggregate;
        this.historyAggregate = historyAggregate;
        this.executionStatistics = new ExecutionStatistics(this.executionAggregate);
    }

    public async init() {
        if (this.executionAggregate.methodContexts && this.executionAggregate.classContexts) {

            const classStatistics: { [key: string]: ClassStatistics } = {};
            for (const id of Object.keys(this.executionAggregate.methodContexts)) {
                const methodContext = this.executionAggregate.methodContexts[id];
                const classContextId = methodContext.classContextId as string;
                const classContext: ClassContext = this.executionAggregate.classContexts[classContextId];

                let currentClassStatistics = new ClassStatistics(classContext);

                if (!classStatistics[currentClassStatistics.classIdentifier]) {
                    classStatistics[currentClassStatistics.classIdentifier] = currentClassStatistics;
                } else {
                    currentClassStatistics = classStatistics[currentClassStatistics.classIdentifier];
                }

                currentClassStatistics.addMethodContext(methodContext);
            }
            this.executionStatistics.setClassStatistics(Object.values(classStatistics));
        }

    }

    public getExecutionAggregate() {
        return this.executionAggregate;
    }

    public getExecutionStatistics(): ExecutionStatistics {
        return this.executionStatistics;
    }


    // TODO: Add all the other methods here...


}
