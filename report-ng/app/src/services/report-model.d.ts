import * as $protobuf from "protobufjs";
/** Namespace data. */
export namespace data {

    /** Properties of a SuiteContext. */
    interface ISuiteContext {

        /** SuiteContext contextValues */
        contextValues?: (data.IContextValues|null);

        /** SuiteContext testContextIds */
        testContextIds?: (string[]|null);

        /** SuiteContext executionContextId */
        executionContextId?: (string|null);
    }

    /** Represents a SuiteContext. */
    class SuiteContext implements ISuiteContext {

        /**
         * Constructs a new SuiteContext.
         * @param [p] Properties to set
         */
        constructor(p?: data.ISuiteContext);

        /** SuiteContext contextValues. */
        public contextValues?: (data.IContextValues|null);

        /** SuiteContext testContextIds. */
        public testContextIds: string[];

        /** SuiteContext executionContextId. */
        public executionContextId: string;

        /**
         * Decodes a SuiteContext message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns SuiteContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.SuiteContext;
    }

    /** Properties of a ClassContext. */
    interface IClassContext {

        /** ClassContext contextValues */
        contextValues?: (data.IContextValues|null);

        /** ClassContext methodContextIds */
        methodContextIds?: (string[]|null);

        /** ClassContext fullClassName */
        fullClassName?: (string|null);

        /** ClassContext testContextId */
        testContextId?: (string|null);

        /** ClassContext executionContextId */
        executionContextId?: (string|null);

        /** ClassContext testContextName */
        testContextName?: (string|null);
    }

    /** Represents a ClassContext. */
    class ClassContext implements IClassContext {

        /**
         * Constructs a new ClassContext.
         * @param [p] Properties to set
         */
        constructor(p?: data.IClassContext);

        /** ClassContext contextValues. */
        public contextValues?: (data.IContextValues|null);

        /** ClassContext methodContextIds. */
        public methodContextIds: string[];

        /** ClassContext fullClassName. */
        public fullClassName: string;

        /** ClassContext testContextId. */
        public testContextId: string;

        /** ClassContext executionContextId. */
        public executionContextId: string;

        /** ClassContext testContextName. */
        public testContextName: string;

        /**
         * Decodes a ClassContext message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns ClassContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.ClassContext;
    }

    /** Properties of a TestContext. */
    interface ITestContext {

        /** TestContext contextValues */
        contextValues?: (data.IContextValues|null);

        /** TestContext classContextIds */
        classContextIds?: (string[]|null);

        /** TestContext suiteContextId */
        suiteContextId?: (string|null);

        /** TestContext executionContextId */
        executionContextId?: (string|null);
    }

    /** Represents a TestContext. */
    class TestContext implements ITestContext {

        /**
         * Constructs a new TestContext.
         * @param [p] Properties to set
         */
        constructor(p?: data.ITestContext);

        /** TestContext contextValues. */
        public contextValues?: (data.IContextValues|null);

        /** TestContext classContextIds. */
        public classContextIds: string[];

        /** TestContext suiteContextId. */
        public suiteContextId: string;

        /** TestContext executionContextId. */
        public executionContextId: string;

        /**
         * Decodes a TestContext message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns TestContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.TestContext;
    }

    /** Properties of an ExecutionContext. */
    interface IExecutionContext {

        /** ExecutionContext contextValues */
        contextValues?: (data.IContextValues|null);

        /** ExecutionContext suiteContextIds */
        suiteContextIds?: (string[]|null);

        /** ExecutionContext runConfig */
        runConfig?: (data.IRunConfig|null);

        /** ExecutionContext project_Id */
        project_Id?: (string|null);

        /** ExecutionContext job_Id */
        job_Id?: (string|null);

        /** ExecutionContext run_Id */
        run_Id?: (string|null);

        /** ExecutionContext task_Id */
        task_Id?: (string|null);

        /** ExecutionContext exclusiveSessionContextIds */
        exclusiveSessionContextIds?: (string[]|null);

        /** ExecutionContext logMessages */
        logMessages?: (data.ILogMessage[]|null);

        /** ExecutionContext estimatedTestsCount */
        estimatedTestsCount?: (number|null);

        /** ExecutionContext failureCorridorLimits */
        failureCorridorLimits?: ({ [k: string]: number }|null);
    }

    /** Represents an ExecutionContext. */
    class ExecutionContext implements IExecutionContext {

        /**
         * Constructs a new ExecutionContext.
         * @param [p] Properties to set
         */
        constructor(p?: data.IExecutionContext);

        /** ExecutionContext contextValues. */
        public contextValues?: (data.IContextValues|null);

        /** ExecutionContext suiteContextIds. */
        public suiteContextIds: string[];

        /** ExecutionContext runConfig. */
        public runConfig?: (data.IRunConfig|null);

        /** ExecutionContext project_Id. */
        public project_Id: string;

        /** ExecutionContext job_Id. */
        public job_Id: string;

        /** ExecutionContext run_Id. */
        public run_Id: string;

        /** ExecutionContext task_Id. */
        public task_Id: string;

        /** ExecutionContext exclusiveSessionContextIds. */
        public exclusiveSessionContextIds: string[];

        /** ExecutionContext logMessages. */
        public logMessages: data.ILogMessage[];

        /** ExecutionContext estimatedTestsCount. */
        public estimatedTestsCount: number;

        /** ExecutionContext failureCorridorLimits. */
        public failureCorridorLimits: { [k: string]: number };

        /**
         * Decodes an ExecutionContext message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns ExecutionContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.ExecutionContext;
    }

    /** Properties of a MethodContext. */
    interface IMethodContext {

        /** MethodContext contextValues */
        contextValues?: (data.IContextValues|null);

        /** MethodContext methodType */
        methodType?: (data.MethodType|null);

        /** MethodContext parameters */
        parameters?: (string[]|null);

        /** MethodContext methodTags */
        methodTags?: (string[]|null);

        /** MethodContext retryNumber */
        retryNumber?: (number|null);

        /** MethodContext methodRunIndex */
        methodRunIndex?: (number|null);

        /** MethodContext threadName */
        threadName?: (string|null);

        /** MethodContext failureCorridorValue */
        failureCorridorValue?: (data.FailureCorridorValue|null);

        /** MethodContext classContextId */
        classContextId?: (string|null);

        /** MethodContext executionContextId */
        executionContextId?: (string|null);

        /** MethodContext infos */
        infos?: (string[]|null);

        /** MethodContext priorityMessage */
        priorityMessage?: (string|null);

        /** MethodContext relatedMethodContextIds */
        relatedMethodContextIds?: (string[]|null);

        /** MethodContext dependsOnMethodContextIds */
        dependsOnMethodContextIds?: (string[]|null);

        /** MethodContext errorContext */
        errorContext?: (data.IErrorContext|null);

        /** MethodContext testSteps */
        testSteps?: (data.ITestStep[]|null);

        /** MethodContext testContextId */
        testContextId?: (string|null);

        /** MethodContext suiteContextId */
        suiteContextId?: (string|null);

        /** MethodContext sessionContextIds */
        sessionContextIds?: (string[]|null);

        /** MethodContext videoIds */
        videoIds?: (string[]|null);

        /** MethodContext customContextJson */
        customContextJson?: (string|null);

        /** MethodContext failedStepIndex */
        failedStepIndex?: (number|null);

        /** MethodContext resultStatus */
        resultStatus?: (data.ResultStatusType|null);
    }

    /** Represents a MethodContext. */
    class MethodContext implements IMethodContext {

        /**
         * Constructs a new MethodContext.
         * @param [p] Properties to set
         */
        constructor(p?: data.IMethodContext);

        /** MethodContext contextValues. */
        public contextValues?: (data.IContextValues|null);

        /** MethodContext methodType. */
        public methodType: data.MethodType;

        /** MethodContext parameters. */
        public parameters: string[];

        /** MethodContext methodTags. */
        public methodTags: string[];

        /** MethodContext retryNumber. */
        public retryNumber: number;

        /** MethodContext methodRunIndex. */
        public methodRunIndex: number;

        /** MethodContext threadName. */
        public threadName: string;

        /** MethodContext failureCorridorValue. */
        public failureCorridorValue: data.FailureCorridorValue;

        /** MethodContext classContextId. */
        public classContextId: string;

        /** MethodContext executionContextId. */
        public executionContextId: string;

        /** MethodContext infos. */
        public infos: string[];

        /** MethodContext priorityMessage. */
        public priorityMessage: string;

        /** MethodContext relatedMethodContextIds. */
        public relatedMethodContextIds: string[];

        /** MethodContext dependsOnMethodContextIds. */
        public dependsOnMethodContextIds: string[];

        /** MethodContext errorContext. */
        public errorContext?: (data.IErrorContext|null);

        /** MethodContext testSteps. */
        public testSteps: data.ITestStep[];

        /** MethodContext testContextId. */
        public testContextId: string;

        /** MethodContext suiteContextId. */
        public suiteContextId: string;

        /** MethodContext sessionContextIds. */
        public sessionContextIds: string[];

        /** MethodContext videoIds. */
        public videoIds: string[];

        /** MethodContext customContextJson. */
        public customContextJson: string;

        /** MethodContext failedStepIndex. */
        public failedStepIndex: number;

        /** MethodContext resultStatus. */
        public resultStatus: data.ResultStatusType;

        /**
         * Decodes a MethodContext message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns MethodContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.MethodContext;
    }

    /** Properties of a ContextValues. */
    interface IContextValues {

        /** ContextValues id */
        id?: (string|null);

        /** ContextValues created */
        created?: (number|null);

        /** ContextValues name */
        name?: (string|null);

        /** ContextValues startTime */
        startTime?: (number|null);

        /** ContextValues endTime */
        endTime?: (number|null);
    }

    /** Represents a ContextValues. */
    class ContextValues implements IContextValues {

        /**
         * Constructs a new ContextValues.
         * @param [p] Properties to set
         */
        constructor(p?: data.IContextValues);

        /** ContextValues id. */
        public id: string;

        /** ContextValues created. */
        public created: number;

        /** ContextValues name. */
        public name: string;

        /** ContextValues startTime. */
        public startTime: number;

        /** ContextValues endTime. */
        public endTime: number;

        /**
         * Decodes a ContextValues message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns ContextValues
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.ContextValues;
    }

    /** Properties of a TestStep. */
    interface ITestStep {

        /** TestStep name */
        name?: (string|null);

        /** TestStep actions */
        actions?: (data.ITestStepAction[]|null);
    }

    /** Represents a TestStep. */
    class TestStep implements ITestStep {

        /**
         * Constructs a new TestStep.
         * @param [p] Properties to set
         */
        constructor(p?: data.ITestStep);

        /** TestStep name. */
        public name: string;

        /** TestStep actions. */
        public actions: data.ITestStepAction[];

        /**
         * Decodes a TestStep message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns TestStep
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.TestStep;
    }

    /** Properties of a TestStepAction. */
    interface ITestStepAction {

        /** TestStepAction name */
        name?: (string|null);

        /** TestStepAction timestamp */
        timestamp?: (number|null);

        /** TestStepAction entries */
        entries?: (data.ITestStepActionEntry[]|null);
    }

    /** Represents a TestStepAction. */
    class TestStepAction implements ITestStepAction {

        /**
         * Constructs a new TestStepAction.
         * @param [p] Properties to set
         */
        constructor(p?: data.ITestStepAction);

        /** TestStepAction name. */
        public name: string;

        /** TestStepAction timestamp. */
        public timestamp: number;

        /** TestStepAction entries. */
        public entries: data.ITestStepActionEntry[];

        /**
         * Decodes a TestStepAction message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns TestStepAction
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.TestStepAction;
    }

    /** Properties of a TestStepActionEntry. */
    interface ITestStepActionEntry {

        /** TestStepActionEntry clickPathEvent */
        clickPathEvent?: (data.IClickPathEvent|null);

        /** TestStepActionEntry screenshotId */
        screenshotId?: (string|null);

        /** TestStepActionEntry logMessage */
        logMessage?: (data.ILogMessage|null);

        /** TestStepActionEntry assertion */
        assertion?: (data.IErrorContext|null);
    }

    /** Represents a TestStepActionEntry. */
    class TestStepActionEntry implements ITestStepActionEntry {

        /**
         * Constructs a new TestStepActionEntry.
         * @param [p] Properties to set
         */
        constructor(p?: data.ITestStepActionEntry);

        /** TestStepActionEntry clickPathEvent. */
        public clickPathEvent?: (data.IClickPathEvent|null);

        /** TestStepActionEntry screenshotId. */
        public screenshotId: string;

        /** TestStepActionEntry logMessage. */
        public logMessage?: (data.ILogMessage|null);

        /** TestStepActionEntry assertion. */
        public assertion?: (data.IErrorContext|null);

        /** TestStepActionEntry entry. */
        public entry?: ("clickPathEvent"|"screenshotId"|"logMessage"|"assertion");

        /**
         * Decodes a TestStepActionEntry message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns TestStepActionEntry
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.TestStepActionEntry;
    }

    /** ClickPathEventType enum. */
    enum ClickPathEventType {
        CPET_NOT_SET = 0,
        CPET_WINDOW = 1,
        CPET_CLICK = 2,
        CPET_VALUE = 3,
        CPET_PAGE = 4,
        CPET_URL = 5
    }

    /** Properties of a ClickPathEvent. */
    interface IClickPathEvent {

        /** ClickPathEvent type */
        type?: (data.ClickPathEventType|null);

        /** ClickPathEvent subject */
        subject?: (string|null);

        /** ClickPathEvent sessionId */
        sessionId?: (string|null);
    }

    /** Represents a ClickPathEvent. */
    class ClickPathEvent implements IClickPathEvent {

        /**
         * Constructs a new ClickPathEvent.
         * @param [p] Properties to set
         */
        constructor(p?: data.IClickPathEvent);

        /** ClickPathEvent type. */
        public type: data.ClickPathEventType;

        /** ClickPathEvent subject. */
        public subject: string;

        /** ClickPathEvent sessionId. */
        public sessionId: string;

        /**
         * Decodes a ClickPathEvent message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns ClickPathEvent
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.ClickPathEvent;
    }

    /** LogMessageType enum. */
    enum LogMessageType {
        LMT_OFF = 0,
        LMT_ERROR = 1,
        LMT_WARN = 2,
        LMT_INFO = 3,
        LMT_DEBUG = 4
    }

    /** Properties of a LogMessage. */
    interface ILogMessage {

        /** LogMessage type */
        type?: (data.LogMessageType|null);

        /** LogMessage loggerName */
        loggerName?: (string|null);

        /** LogMessage message */
        message?: (string|null);

        /** LogMessage timestamp */
        timestamp?: (number|null);

        /** LogMessage threadName */
        threadName?: (string|null);

        /** LogMessage stackTrace */
        stackTrace?: (data.IStackTraceCause[]|null);
    }

    /** Represents a LogMessage. */
    class LogMessage implements ILogMessage {

        /**
         * Constructs a new LogMessage.
         * @param [p] Properties to set
         */
        constructor(p?: data.ILogMessage);

        /** LogMessage type. */
        public type: data.LogMessageType;

        /** LogMessage loggerName. */
        public loggerName: string;

        /** LogMessage message. */
        public message: string;

        /** LogMessage timestamp. */
        public timestamp: number;

        /** LogMessage threadName. */
        public threadName: string;

        /** LogMessage stackTrace. */
        public stackTrace: data.IStackTraceCause[];

        /**
         * Decodes a LogMessage message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns LogMessage
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.LogMessage;
    }

    /** Properties of an ErrorContext. */
    interface IErrorContext {

        /** ErrorContext scriptSource */
        scriptSource?: (data.IScriptSource|null);

        /** ErrorContext executionObjectSource */
        executionObjectSource?: (data.IScriptSource|null);

        /** ErrorContext ticketId */
        ticketId?: (string|null);

        /** ErrorContext description */
        description?: (string|null);

        /** ErrorContext stackTrace */
        stackTrace?: (data.IStackTraceCause[]|null);

        /** ErrorContext optional */
        optional?: (boolean|null);
    }

    /** Represents an ErrorContext. */
    class ErrorContext implements IErrorContext {

        /**
         * Constructs a new ErrorContext.
         * @param [p] Properties to set
         */
        constructor(p?: data.IErrorContext);

        /** ErrorContext scriptSource. */
        public scriptSource?: (data.IScriptSource|null);

        /** ErrorContext executionObjectSource. */
        public executionObjectSource?: (data.IScriptSource|null);

        /** ErrorContext ticketId. */
        public ticketId: string;

        /** ErrorContext description. */
        public description: string;

        /** ErrorContext stackTrace. */
        public stackTrace: data.IStackTraceCause[];

        /** ErrorContext optional. */
        public optional: boolean;

        /**
         * Decodes an ErrorContext message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns ErrorContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.ErrorContext;
    }

    /** Properties of a SessionContext. */
    interface ISessionContext {

        /** SessionContext contextValues */
        contextValues?: (data.IContextValues|null);

        /** SessionContext sessionKey */
        sessionKey?: (string|null);

        /** SessionContext provider */
        provider?: (string|null);

        /** SessionContext metadata */
        metadata?: ({ [k: string]: string }|null);

        /** SessionContext sessionId */
        sessionId?: (string|null);
    }

    /** Represents a SessionContext. */
    class SessionContext implements ISessionContext {

        /**
         * Constructs a new SessionContext.
         * @param [p] Properties to set
         */
        constructor(p?: data.ISessionContext);

        /** SessionContext contextValues. */
        public contextValues?: (data.IContextValues|null);

        /** SessionContext sessionKey. */
        public sessionKey: string;

        /** SessionContext provider. */
        public provider: string;

        /** SessionContext metadata. */
        public metadata: { [k: string]: string };

        /** SessionContext sessionId. */
        public sessionId: string;

        /**
         * Decodes a SessionContext message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns SessionContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.SessionContext;
    }

    /** Properties of a RunConfig. */
    interface IRunConfig {

        /** RunConfig runcfg */
        runcfg?: (string|null);

        /** RunConfig buildInformation */
        buildInformation?: (data.IBuildInformation|null);

        /** RunConfig reportName */
        reportName?: (string|null);
    }

    /** Represents a RunConfig. */
    class RunConfig implements IRunConfig {

        /**
         * Constructs a new RunConfig.
         * @param [p] Properties to set
         */
        constructor(p?: data.IRunConfig);

        /** RunConfig runcfg. */
        public runcfg: string;

        /** RunConfig buildInformation. */
        public buildInformation?: (data.IBuildInformation|null);

        /** RunConfig reportName. */
        public reportName: string;

        /**
         * Decodes a RunConfig message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns RunConfig
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.RunConfig;
    }

    /** Properties of a BuildInformation. */
    interface IBuildInformation {

        /** BuildInformation buildJavaVersion */
        buildJavaVersion?: (string|null);

        /** BuildInformation buildOsName */
        buildOsName?: (string|null);

        /** BuildInformation buildOsVersion */
        buildOsVersion?: (string|null);

        /** BuildInformation buildUserName */
        buildUserName?: (string|null);

        /** BuildInformation buildVersion */
        buildVersion?: (string|null);

        /** BuildInformation buildTimestamp */
        buildTimestamp?: (string|null);
    }

    /** Represents a BuildInformation. */
    class BuildInformation implements IBuildInformation {

        /**
         * Constructs a new BuildInformation.
         * @param [p] Properties to set
         */
        constructor(p?: data.IBuildInformation);

        /** BuildInformation buildJavaVersion. */
        public buildJavaVersion: string;

        /** BuildInformation buildOsName. */
        public buildOsName: string;

        /** BuildInformation buildOsVersion. */
        public buildOsVersion: string;

        /** BuildInformation buildUserName. */
        public buildUserName: string;

        /** BuildInformation buildVersion. */
        public buildVersion: string;

        /** BuildInformation buildTimestamp. */
        public buildTimestamp: string;

        /**
         * Decodes a BuildInformation message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns BuildInformation
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.BuildInformation;
    }

    /** FailureCorridorValue enum. */
    enum FailureCorridorValue {
        FCV_NOT_SET = 0,
        FCV_HIGH = 1,
        FCV_MID = 2,
        FCV_LOW = 3
    }

    /** MethodType enum. */
    enum MethodType {
        MT_NOT_SET = 0,
        TEST_METHOD = 1,
        CONFIGURATION_METHOD = 2
    }

    /** Properties of a StackTraceCause. */
    interface IStackTraceCause {

        /** StackTraceCause className */
        className?: (string|null);

        /** StackTraceCause message */
        message?: (string|null);

        /** StackTraceCause stackTraceElements */
        stackTraceElements?: (string[]|null);
    }

    /** Represents a StackTraceCause. */
    class StackTraceCause implements IStackTraceCause {

        /**
         * Constructs a new StackTraceCause.
         * @param [p] Properties to set
         */
        constructor(p?: data.IStackTraceCause);

        /** StackTraceCause className. */
        public className: string;

        /** StackTraceCause message. */
        public message: string;

        /** StackTraceCause stackTraceElements. */
        public stackTraceElements: string[];

        /**
         * Decodes a StackTraceCause message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns StackTraceCause
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.StackTraceCause;
    }

    /** Properties of a ScriptSource. */
    interface IScriptSource {

        /** ScriptSource fileName */
        fileName?: (string|null);

        /** ScriptSource methodName */
        methodName?: (string|null);

        /** ScriptSource lines */
        lines?: (data.IScriptSourceLine[]|null);

        /** ScriptSource mark */
        mark?: (number|null);
    }

    /** Represents a ScriptSource. */
    class ScriptSource implements IScriptSource {

        /**
         * Constructs a new ScriptSource.
         * @param [p] Properties to set
         */
        constructor(p?: data.IScriptSource);

        /** ScriptSource fileName. */
        public fileName: string;

        /** ScriptSource methodName. */
        public methodName: string;

        /** ScriptSource lines. */
        public lines: data.IScriptSourceLine[];

        /** ScriptSource mark. */
        public mark: number;

        /**
         * Decodes a ScriptSource message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns ScriptSource
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.ScriptSource;
    }

    /** Properties of a ScriptSourceLine. */
    interface IScriptSourceLine {

        /** ScriptSourceLine line */
        line?: (string|null);

        /** ScriptSourceLine lineNumber */
        lineNumber?: (number|null);
    }

    /** Represents a ScriptSourceLine. */
    class ScriptSourceLine implements IScriptSourceLine {

        /**
         * Constructs a new ScriptSourceLine.
         * @param [p] Properties to set
         */
        constructor(p?: data.IScriptSourceLine);

        /** ScriptSourceLine line. */
        public line: string;

        /** ScriptSourceLine lineNumber. */
        public lineNumber: number;

        /**
         * Decodes a ScriptSourceLine message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns ScriptSourceLine
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.ScriptSourceLine;
    }

    /** Properties of a File. */
    interface IFile {

        /** File id */
        id?: (string|null);

        /** File size */
        size?: (number|null);

        /** File mimetype */
        mimetype?: (string|null);

        /** File relativePath */
        relativePath?: (string|null);

        /** File createdTimestamp */
        createdTimestamp?: (number|null);

        /** File sha1Checksum */
        sha1Checksum?: (Uint8Array|null);

        /** File meta */
        meta?: ({ [k: string]: string }|null);

        /** File lastModified */
        lastModified?: (number|null);

        /** File projectId */
        projectId?: (string|null);

        /** File jobId */
        jobId?: (string|null);

        /** File isDirectory */
        isDirectory?: (boolean|null);

        /** File name */
        name?: (string|null);
    }

    /** Represents a File. */
    class File implements IFile {

        /**
         * Constructs a new File.
         * @param [p] Properties to set
         */
        constructor(p?: data.IFile);

        /** File id. */
        public id: string;

        /** File size. */
        public size: number;

        /** File mimetype. */
        public mimetype: string;

        /** File relativePath. */
        public relativePath: string;

        /** File createdTimestamp. */
        public createdTimestamp: number;

        /** File sha1Checksum. */
        public sha1Checksum: Uint8Array;

        /** File meta. */
        public meta: { [k: string]: string };

        /** File lastModified. */
        public lastModified: number;

        /** File projectId. */
        public projectId: string;

        /** File jobId. */
        public jobId: string;

        /** File isDirectory. */
        public isDirectory: boolean;

        /** File name. */
        public name: string;

        /**
         * Decodes a File message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns File
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.File;
    }

    /** ResultStatusType enum. */
    enum ResultStatusType {
        RST_NOT_SET = 0,
        NO_RUN = 1,
        INFO = 2,
        SKIPPED = 3,
        PASSED = 4,
        MINOR = 5,
        FAILED = 7,
        FAILED_MINOR = 8,
        FAILED_RETRIED = 9,
        FAILED_EXPECTED = 10,
        PASSED_RETRY = 11,
        MINOR_RETRY = 12
    }

    /** Properties of an ExecutionAggregate. */
    interface IExecutionAggregate {

        /** ExecutionAggregate executionContext */
        executionContext?: (data.IExecutionContext|null);

        /** ExecutionAggregate suiteContexts */
        suiteContexts?: ({ [k: string]: data.ISuiteContext }|null);

        /** ExecutionAggregate testContexts */
        testContexts?: ({ [k: string]: data.ITestContext }|null);

        /** ExecutionAggregate classContexts */
        classContexts?: ({ [k: string]: data.IClassContext }|null);

        /** ExecutionAggregate methodContexts */
        methodContexts?: ({ [k: string]: data.IMethodContext }|null);

        /** ExecutionAggregate sessionContexts */
        sessionContexts?: ({ [k: string]: data.ISessionContext }|null);
    }

    /** Represents an ExecutionAggregate. */
    class ExecutionAggregate implements IExecutionAggregate {

        /**
         * Constructs a new ExecutionAggregate.
         * @param [p] Properties to set
         */
        constructor(p?: data.IExecutionAggregate);

        /** ExecutionAggregate executionContext. */
        public executionContext?: (data.IExecutionContext|null);

        /** ExecutionAggregate suiteContexts. */
        public suiteContexts: { [k: string]: data.ISuiteContext };

        /** ExecutionAggregate testContexts. */
        public testContexts: { [k: string]: data.ITestContext };

        /** ExecutionAggregate classContexts. */
        public classContexts: { [k: string]: data.IClassContext };

        /** ExecutionAggregate methodContexts. */
        public methodContexts: { [k: string]: data.IMethodContext };

        /** ExecutionAggregate sessionContexts. */
        public sessionContexts: { [k: string]: data.ISessionContext };

        /**
         * Decodes an ExecutionAggregate message from the specified reader or buffer.
         * @param r Reader or buffer to decode from
         * @param [l] Message length if known beforehand
         * @returns ExecutionAggregate
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(r: ($protobuf.Reader|Uint8Array), l?: number): data.ExecutionAggregate;
    }
}
