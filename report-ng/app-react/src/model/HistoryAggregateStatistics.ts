import {Statistics} from "./Statistics.ts";
import {HistoricalMethod} from "./HistoricalMethod.ts";
import type {HistoryAggregate} from "./report-model/report_pb.ts";
import {MethodType, ResultStatusType} from "./report-model/framework_pb.ts";
import {ClassHistoryStatistics} from "./ClassHistoryStatistics.ts";

export class HistoryAggregateStatistics extends Statistics {
    private readonly _classMap: Map<string, ClassHistoryStatistics>;
    private readonly _classIdMap: Map<string, string>;
    private readonly _methodMap: Map<string, HistoricalMethod>;
    private readonly _aggregate: HistoryAggregate;
    private readonly _historyIndex: number;

    constructor(historyEntry: HistoryAggregate) {
        super();
        this._aggregate = historyEntry;
        this._classMap = new Map();
        this._classIdMap = new Map();
        this._methodMap = new Map();
        this._historyIndex = historyEntry.historyIndex ?? 0;

        Object.values(historyEntry.classContexts ?? {}).forEach(classContext => {
            const classIdentifier = classContext.testContextName || (classContext.contextValues?.name ?? "");
            const classId = classContext.contextValues?.id;
            if (classId) {
                this._classIdMap.set(classId, classIdentifier);
            }
        });

        new Set(this._classIdMap.values()).forEach(cls => {
            this._classMap.set(cls, new ClassHistoryStatistics(cls));
        });

        Object.values(historyEntry.methodContexts ?? {}).forEach(method => {
            const methodId = method.contextValues?.id;
            if (methodId) {
                this._methodMap.set(methodId, new HistoricalMethod(method));
            }
            if (method.methodType === MethodType.TEST_METHOD) {
                this.addResultStatus(method.resultStatus ?? ResultStatusType.NO_RUN);
            }
        });

        this._methodMap.forEach(method => {
            (method.context.relatedMethodContextIds ?? []).forEach(relatedMethod => {
                const related = this._methodMap.get(relatedMethod);
                if (related) {
                    method.addRelatedMethods(related.identifier);
                }
            });

            const classId = method.context.classContextId;
            const classIdentifier = classId ? this._classIdMap.get(classId) : undefined;
            const classStats = classIdentifier ? this._classMap.get(classIdentifier) : undefined;
            if (classStats) {
                classStats.addMethod(method);
            }
        });
    }

    get classes() {
        return this._classMap;
    }

    get historyIndex() {
        return this._historyIndex;
    }

    get historyAggregate() {
        return this._aggregate;
    }
}
