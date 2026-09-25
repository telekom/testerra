import type {
    ExecutionAggregate, History,
    LogMessageAggregate
} from "../model/report-model/report_pb.ts";
import type {ClassContext, LogMessage, MethodContext} from "../model/report-model/framework_pb.ts";
import {ExecutionStatistics} from "../model/ExecutionStatistics.ts";
import {ClassStatistics} from "../model/ClassStatistics.ts";
import {MethodDetails} from "../model/MethodDetails";
import {HistoryStatistics} from "../model/HistoryStatistics.ts";


export class ExecutionStatisticsManager {
    private readonly executionAggregate: ExecutionAggregate;
    private readonly logMessageAggregate: LogMessageAggregate;
    private readonly history: History;

    private readonly executionStatistics: ExecutionStatistics;
    private readonly historyStatistics: HistoryStatistics;
    private logMessages: { [key: string]: LogMessage } = {};

    constructor(executionAggregate: ExecutionAggregate, logMessageAggregate: LogMessageAggregate, history: History) {
        this.executionAggregate = executionAggregate;
        this.logMessageAggregate = logMessageAggregate;
        this.history = history;
        this.executionStatistics = new ExecutionStatistics(this.executionAggregate);
        this.historyStatistics = new HistoryStatistics(this.history);
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

        if (this.logMessageAggregate.logMessages) {
            this.logMessages = this.logMessageAggregate.logMessages;
            // console.info(this.logMessages);
        }
    }

    public getExecutionAggregate() : ExecutionAggregate {
        return this.executionAggregate;
    }

    public getExecutionStatistics(): ExecutionStatistics {
        return this.executionStatistics;
    }

    public getLogs() {
        return this.logMessages;
    }

    public getHistoryStatistics(): HistoryStatistics {
        return this.historyStatistics;
    }

    // note: "!" operator is used to tell typescript that this property is not undefined
    // (this problem is caused by optional proto properties being non-optional in typescript)
    // talk with mgn: classContext, testContext, suiteContext, sessionContext and all Ids are never undefined if the "parent" is not undefined
    public getMethodDetails(methodId: string) {
        const executionStatistics = this.getExecutionStatistics()
        const executionAggregate = this.getExecutionAggregate();
        const methodContext = executionAggregate.methodContexts?.[methodId];
        if (methodContext) {
            const classContext = executionAggregate.classContexts![methodContext.classContextId!];
            const testContext = executionAggregate.testContexts![classContext.testContextId!];
            const suiteContext = executionAggregate.suiteContexts![testContext.suiteContextId!];
            const sessionContexts = methodContext.sessionContextIds!.map(value => executionAggregate.sessionContexts![value])

            const methodDetails = new MethodDetails(methodContext, new ClassStatistics(classContext));
            methodDetails.executionStatistics = executionStatistics;
            methodDetails.testContext = testContext;
            methodDetails.suiteContext = suiteContext;
            methodDetails.sessionContexts = sessionContexts;
            methodDetails.promptLogs = this.getMethodPromptLogs(methodContext);
            return methodDetails;
        }
    }

    private getMethodPromptLogs(methodContext: MethodContext) {
        const logMessageIds = methodContext.testSteps
            ?.flatMap(value => value.actions)
            .flatMap(value => value?.entries)
            .filter(value => value?.logMessageId)
            .map(value => value?.logMessageId)
        const logMessages = this.getLogs()
        return Object.values(logMessages).filter(logMessage => logMessage.prompt && logMessageIds?.includes(logMessage.id))
    }

    // TODO: Add all the other methods here...


}
