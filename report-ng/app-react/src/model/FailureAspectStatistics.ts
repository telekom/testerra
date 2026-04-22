import {Statistics} from "./Statistics.ts";
import {type ErrorContext, type MethodContext, ResultStatusType, type StackTraceCause,} from "./report-model/framework_pb.ts";

export class FailureAspectStatistics extends Statistics {
    private _irrelevantClassNameNeedles = ["TimeoutException"];
    private _methodContexts: MethodContext[] = [];
    private _errorContext: ErrorContext;
    readonly identifier: string = "";
    readonly relevantCause: StackTraceCause | undefined;
    readonly message: string = "";
    public index: number = 0;

    constructor(
        errorContext: ErrorContext
    ) {
        super();
        this._errorContext = errorContext;
        this.relevantCause = this._findRelevantCause(this._errorContext.stackTrace!);
        if (this._errorContext.description) {
            this.identifier = this._errorContext.description;
            this.message = this._errorContext.description;
        } else if (this.relevantCause && this.relevantCause.message) {
            // Replace all occurring line-breaks with a space-character
            this.message = this.relevantCause.message.replaceAll('\n', ' ');
            this.message = this.message.trim();
            this.identifier = this.relevantCause.className + this.message;
        }
    }

    /**
     * This method finds a relevant cause from the stack trace.
     * If it could not find any, it takes the first cause from the stack.
     */
    private _findRelevantCause(causes: StackTraceCause[]): StackTraceCause | undefined {
        let relevantCause = causes.find(cause => {
            return !this._irrelevantClassNameNeedles.find(needle => cause.className!.indexOf(needle) >= 0);
        })
        if (!relevantCause) {
            relevantCause = causes.find(_ => true);
        }
        return relevantCause;
    }

    addMethodContext(methodContext: MethodContext) {
        if (this._methodContexts.indexOf(methodContext) == -1) {
            this._methodContexts.push(methodContext);
            if (methodContext.resultStatus) {
                this.addResultStatus(methodContext.resultStatus);
            }
        }
    }

    /**
     * A failure aspect is minor when it has no statuses in failed state
     */
    get isMinor() {
        return this.getStatusCount(ResultStatusType.FAILED) == 0;
    }

    get methodContexts() {
        return this._methodContexts;
    }
}
