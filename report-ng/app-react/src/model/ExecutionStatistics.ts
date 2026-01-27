import {Statistics} from "./Statistics.ts";
import type {ExecutionAggregate} from "./report-model/report_pb.ts";
import type {ClassStatistics} from "./ClassStatistics.ts";

export class ExecutionStatistics extends Statistics{

    private _classStatistics: ClassStatistics[] = [];

    private executionAggregate: ExecutionAggregate;

    constructor(executionAggregate: ExecutionAggregate) {
        super();
        this.executionAggregate = executionAggregate;
    }

    setClassStatistics(classStatistics: ClassStatistics[]) {
        this._classStatistics = classStatistics;
        this._classStatistics.forEach(classStatistics => this.addStatistics(classStatistics));
    }

    // private _addUniqueFailureAspect(failureAspect: FailureAspectStatistics, methodContext: data.MethodContext) {
    //     const foundFailureAspect = this._uniqueFailureAspects.find(existingFailureAspectStatistics => {
    //         return existingFailureAspectStatistics.identifier == failureAspect.identifier;
    //     });
    //
    //     if (foundFailureAspect) {
    //         failureAspect = foundFailureAspect;
    //     } else {
    //         this._uniqueFailureAspects.push(failureAspect);
    //     }
    //     failureAspect.addMethodContext(methodContext);
    // }

    // protected addStatistics(classStatistics: ClassStatistics) {
    //     super.addStatistics(classStatistics)
    //     classStatistics.methodContexts
    //         .forEach(methodContext => {
    //             const methodDetails = new MethodDetails(methodContext, classStatistics);
    //             methodDetails.failureAspects.forEach(failureAspect => {
    //                 this._addUniqueFailureAspect(failureAspect, methodContext);
    //             })
    //         })
    //
    //     // Sort failure aspects by fail count
    //     this._uniqueFailureAspects = this._uniqueFailureAspects.sort((a, b) => b.overallFailed - a.overallFailed);
    //
    //     for (let i = 0; i < this._uniqueFailureAspects.length; ++i) {
    //         this._uniqueFailureAspects[i].index = i;
    //     }
    // }

    get executionContextLogMessageIds() {
        return this.executionAggregate.executionContext?.logMessageIds
    }

    get methodContextLogMessageIds() {
        return Object.values(this.executionAggregate.methodContexts)
            .flatMap(methodContext => {
                return methodContext.testSteps
                    .flatMap(testStep => testStep.actions)
                    .flatMap(action => action.entries)
                    .filter(entry => entry.logMessageId)
                    .map(entry => {
                        return {logMessageId: entry.logMessageId, methodContext}
                    })
            })
    }

    get classStatistics() {
        return this._classStatistics;
    }

    // get uniqueFailureAspects() {
    //     return this._uniqueFailureAspects;
    // }

}
