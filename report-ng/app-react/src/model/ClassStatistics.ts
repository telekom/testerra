import {Statistics} from "./Statistics.ts";
import type {ClassContext as IClassContext} from "./report-model/framework_pb.ts";
import {type MethodContext as IMethodContext, MethodType} from "./report-model/framework_pb.ts";

export class ClassStatistics extends Statistics {

    private _configStatistics = new Statistics();
    private _methodContexts: IMethodContext[] = [];
    readonly classIdentifier: string;

    readonly classContext: IClassContext;

    constructor(
        classContext: IClassContext
    ) {
        super();
        this.classContext = classContext;
        this.classIdentifier = (classContext.testContextName || classContext.contextValues?.name) as string;
    }

    addMethodContext(methodContext: IMethodContext) {
        if (!methodContext.resultStatus) {
            return;
        }
        if (methodContext.methodType == MethodType.CONFIGURATION_METHOD) {
            this._configStatistics.addResultStatus(methodContext.resultStatus);
        } else {
            this.addResultStatus(methodContext.resultStatus);
        }
        this._methodContexts.push(methodContext);
    }

    get methodContexts() {
        return this._methodContexts;
    }

    get configStatistics() {
        return this._configStatistics;
    }

}
