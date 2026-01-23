import type {ExecutionAggregate} from "./report-model/report_pb.ts";
import type {ClassContext, RunConfig} from "./report-model/framework_pb.ts";
import {ExecutionStatistics} from "../model/ExecutionStatistics.ts";
import {ClassStatistics} from "../model/ClassStatistics.ts";


export class ExecutionStatisticsManager {
    private runConfig: RunConfig | undefined = undefined;

    private readonly executionAggregate: ExecutionAggregate;
    private executionStatistics: ExecutionStatistics;

    constructor(executionAggregate: ExecutionAggregate) {
        this.executionAggregate = executionAggregate;
        this.executionStatistics = new ExecutionStatistics(this.executionAggregate);
    }

    public async init() {
        this.runConfig = this.executionAggregate.executionContext?.runConfig;

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

    // TODO: Just for testing
    public getRunConfig() {
        return this.runConfig;
    }

    // TODO: Add all the other methods here...


}
