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
         * @param [properties] Properties to set
         */
        constructor(properties?: data.ISuiteContext);

        /** SuiteContext contextValues. */
        public contextValues?: (data.IContextValues|null);

        /** SuiteContext testContextIds. */
        public testContextIds: string[];

        /** SuiteContext executionContextId. */
        public executionContextId: string;

        /**
         * Encodes the specified SuiteContext message. Does not implicitly {@link data.SuiteContext.verify|verify} messages.
         * @param message SuiteContext message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.ISuiteContext, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a SuiteContext message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns SuiteContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.SuiteContext;
    }

    /** Properties of a ClassContext. */
    interface IClassContext {

        /** ClassContext contextValues */
        contextValues?: (data.IContextValues|null);

        /** ClassContext methodContextIds */
        methodContextIds?: (string[]|null);

        /** ClassContext fullClassName */
        fullClassName?: (string|null);

        /** ClassContext simpleClassName */
        simpleClassName?: (string|null);

        /** ClassContext testContextId */
        testContextId?: (string|null);

        /** ClassContext executionContextId */
        executionContextId?: (string|null);

        /** ClassContext merged */
        merged?: (boolean|null);
    }

    /** Represents a ClassContext. */
    class ClassContext implements IClassContext {

        /**
         * Constructs a new ClassContext.
         * @param [properties] Properties to set
         */
        constructor(properties?: data.IClassContext);

        /** ClassContext contextValues. */
        public contextValues?: (data.IContextValues|null);

        /** ClassContext methodContextIds. */
        public methodContextIds: string[];

        /** ClassContext fullClassName. */
        public fullClassName: string;

        /** ClassContext simpleClassName. */
        public simpleClassName: string;

        /** ClassContext testContextId. */
        public testContextId: string;

        /** ClassContext executionContextId. */
        public executionContextId: string;

        /** ClassContext merged. */
        public merged: boolean;

        /**
         * Encodes the specified ClassContext message. Does not implicitly {@link data.ClassContext.verify|verify} messages.
         * @param message ClassContext message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.IClassContext, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a ClassContext message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns ClassContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.ClassContext;
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
         * @param [properties] Properties to set
         */
        constructor(properties?: data.ITestContext);

        /** TestContext contextValues. */
        public contextValues?: (data.IContextValues|null);

        /** TestContext classContextIds. */
        public classContextIds: string[];

        /** TestContext suiteContextId. */
        public suiteContextId: string;

        /** TestContext executionContextId. */
        public executionContextId: string;

        /**
         * Encodes the specified TestContext message. Does not implicitly {@link data.TestContext.verify|verify} messages.
         * @param message TestContext message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.ITestContext, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a TestContext message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns TestContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.TestContext;
    }

    /** Properties of an ExecutionContext. */
    interface IExecutionContext {

        /** ExecutionContext contextValues */
        contextValues?: (data.IContextValues|null);

        /** ExecutionContext mergedClassContextIds */
        mergedClassContextIds?: (string[]|null);

        /** ExecutionContext exitPoints */
        exitPoints?: (data.IContextClip[]|null);

        /** ExecutionContext failureAscpects */
        failureAscpects?: (data.IContextClip[]|null);

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

        /** ExecutionContext estimatedTestMethodCount */
        estimatedTestMethodCount?: (number|null);
    }

    /** Represents an ExecutionContext. */
    class ExecutionContext implements IExecutionContext {

        /**
         * Constructs a new ExecutionContext.
         * @param [properties] Properties to set
         */
        constructor(properties?: data.IExecutionContext);

        /** ExecutionContext contextValues. */
        public contextValues?: (data.IContextValues|null);

        /** ExecutionContext mergedClassContextIds. */
        public mergedClassContextIds: string[];

        /** ExecutionContext exitPoints. */
        public exitPoints: data.IContextClip[];

        /** ExecutionContext failureAscpects. */
        public failureAscpects: data.IContextClip[];

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

        /** ExecutionContext estimatedTestMethodCount. */
        public estimatedTestMethodCount: number;

        /**
         * Encodes the specified ExecutionContext message. Does not implicitly {@link data.ExecutionContext.verify|verify} messages.
         * @param message ExecutionContext message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.IExecutionContext, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes an ExecutionContext message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns ExecutionContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.ExecutionContext;
    }

    /** Properties of a ContextClip. */
    interface IContextClip {

        /** ContextClip key */
        key?: (string|null);

        /** ContextClip methodContextIds */
        methodContextIds?: (string[]|null);
    }

    /** Represents a ContextClip. */
    class ContextClip implements IContextClip {

        /**
         * Constructs a new ContextClip.
         * @param [properties] Properties to set
         */
        constructor(properties?: data.IContextClip);

        /** ContextClip key. */
        public key: string;

        /** ContextClip methodContextIds. */
        public methodContextIds: string[];

        /**
         * Encodes the specified ContextClip message. Does not implicitly {@link data.ContextClip.verify|verify} messages.
         * @param message ContextClip message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.IContextClip, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a ContextClip message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns ContextClip
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.ContextClip;
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

        /** MethodContext nonFunctionalInfos */
        nonFunctionalInfos?: (data.IErrorContext[]|null);

        /** MethodContext collectedAssertions */
        collectedAssertions?: (data.IErrorContext[]|null);

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
        testSteps?: (data.IPTestStep[]|null);

        /** MethodContext testContextId */
        testContextId?: (string|null);

        /** MethodContext suiteContextId */
        suiteContextId?: (string|null);

        /** MethodContext sessionContextIds */
        sessionContextIds?: (string[]|null);

        /** MethodContext videoIds */
        videoIds?: (string[]|null);

        /** MethodContext screenshotIds */
        screenshotIds?: (string[]|null);

        /** MethodContext customContextJson */
        customContextJson?: (string|null);
    }

    /** Represents a MethodContext. */
    class MethodContext implements IMethodContext {

        /**
         * Constructs a new MethodContext.
         * @param [properties] Properties to set
         */
        constructor(properties?: data.IMethodContext);

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

        /** MethodContext nonFunctionalInfos. */
        public nonFunctionalInfos: data.IErrorContext[];

        /** MethodContext collectedAssertions. */
        public collectedAssertions: data.IErrorContext[];

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
        public testSteps: data.IPTestStep[];

        /** MethodContext testContextId. */
        public testContextId: string;

        /** MethodContext suiteContextId. */
        public suiteContextId: string;

        /** MethodContext sessionContextIds. */
        public sessionContextIds: string[];

        /** MethodContext videoIds. */
        public videoIds: string[];

        /** MethodContext screenshotIds. */
        public screenshotIds: string[];

        /** MethodContext customContextJson. */
        public customContextJson: string;

        /**
         * Encodes the specified MethodContext message. Does not implicitly {@link data.MethodContext.verify|verify} messages.
         * @param message MethodContext message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.IMethodContext, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a MethodContext message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns MethodContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.MethodContext;
    }

    /** Properties of a ContextValues. */
    interface IContextValues {

        /** ContextValues id */
        id?: (string|null);

        /** ContextValues created */
        created?: (number|Long|null);

        /** ContextValues name */
        name?: (string|null);

        /** ContextValues startTime */
        startTime?: (number|Long|null);

        /** ContextValues endTime */
        endTime?: (number|Long|null);

        /** ContextValues swi */
        swi?: (string|null);

        /** ContextValues resultStatus */
        resultStatus?: (data.ResultStatusType|null);

        /** ContextValues execStatus */
        execStatus?: (data.ExecStatusType|null);
    }

    /** Represents a ContextValues. */
    class ContextValues implements IContextValues {

        /**
         * Constructs a new ContextValues.
         * @param [properties] Properties to set
         */
        constructor(properties?: data.IContextValues);

        /** ContextValues id. */
        public id: string;

        /** ContextValues created. */
        public created: (number|Long);

        /** ContextValues name. */
        public name: string;

        /** ContextValues startTime. */
        public startTime: (number|Long);

        /** ContextValues endTime. */
        public endTime: (number|Long);

        /** ContextValues swi. */
        public swi: string;

        /** ContextValues resultStatus. */
        public resultStatus: data.ResultStatusType;

        /** ContextValues execStatus. */
        public execStatus: data.ExecStatusType;

        /**
         * Encodes the specified ContextValues message. Does not implicitly {@link data.ContextValues.verify|verify} messages.
         * @param message ContextValues message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.IContextValues, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a ContextValues message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns ContextValues
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.ContextValues;
    }

    /** Properties of a PTestStep. */
    interface IPTestStep {

        /** PTestStep name */
        name?: (string|null);

        /** PTestStep id */
        id?: (string|null);

        /** PTestStep testStepActions */
        testStepActions?: (data.IPTestStepAction[]|null);
    }

    /** Represents a PTestStep. */
    class PTestStep implements IPTestStep {

        /**
         * Constructs a new PTestStep.
         * @param [properties] Properties to set
         */
        constructor(properties?: data.IPTestStep);

        /** PTestStep name. */
        public name: string;

        /** PTestStep id. */
        public id: string;

        /** PTestStep testStepActions. */
        public testStepActions: data.IPTestStepAction[];

        /**
         * Encodes the specified PTestStep message. Does not implicitly {@link data.PTestStep.verify|verify} messages.
         * @param message PTestStep message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.IPTestStep, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a PTestStep message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns PTestStep
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.PTestStep;
    }

    /** Properties of a PTestStepAction. */
    interface IPTestStepAction {

        /** PTestStepAction name */
        name?: (string|null);

        /** PTestStepAction id */
        id?: (string|null);

        /** PTestStepAction timestamp */
        timestamp?: (number|Long|null);

        /** PTestStepAction screenshotNames */
        screenshotNames?: (string[]|null);

        /** PTestStepAction clickpathEvents */
        clickpathEvents?: (data.IPClickPathEvent[]|null);

        /** PTestStepAction screenshotIds */
        screenshotIds?: (string[]|null);
    }

    /** Represents a PTestStepAction. */
    class PTestStepAction implements IPTestStepAction {

        /**
         * Constructs a new PTestStepAction.
         * @param [properties] Properties to set
         */
        constructor(properties?: data.IPTestStepAction);

        /** PTestStepAction name. */
        public name: string;

        /** PTestStepAction id. */
        public id: string;

        /** PTestStepAction timestamp. */
        public timestamp: (number|Long);

        /** PTestStepAction screenshotNames. */
        public screenshotNames: string[];

        /** PTestStepAction clickpathEvents. */
        public clickpathEvents: data.IPClickPathEvent[];

        /** PTestStepAction screenshotIds. */
        public screenshotIds: string[];

        /**
         * Encodes the specified PTestStepAction message. Does not implicitly {@link data.PTestStepAction.verify|verify} messages.
         * @param message PTestStepAction message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.IPTestStepAction, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a PTestStepAction message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns PTestStepAction
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.PTestStepAction;
    }

    /** PClickPathEventType enum. */
    enum PClickPathEventType {
        NOT_SET = 0,
        WINDOW = 1,
        CLICK = 2,
        VALUE = 3,
        PAGE = 4,
        URL = 5
    }

    /** Properties of a PClickPathEvent. */
    interface IPClickPathEvent {

        /** PClickPathEvent type */
        type?: (data.PClickPathEventType|null);

        /** PClickPathEvent subject */
        subject?: (string|null);

        /** PClickPathEvent sessionId */
        sessionId?: (string|null);
    }

    /** Represents a PClickPathEvent. */
    class PClickPathEvent implements IPClickPathEvent {

        /**
         * Constructs a new PClickPathEvent.
         * @param [properties] Properties to set
         */
        constructor(properties?: data.IPClickPathEvent);

        /** PClickPathEvent type. */
        public type: data.PClickPathEventType;

        /** PClickPathEvent subject. */
        public subject: string;

        /** PClickPathEvent sessionId. */
        public sessionId: string;

        /**
         * Encodes the specified PClickPathEvent message. Does not implicitly {@link data.PClickPathEvent.verify|verify} messages.
         * @param message PClickPathEvent message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.IPClickPathEvent, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a PClickPathEvent message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns PClickPathEvent
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.PClickPathEvent;
    }

    /** Properties of an ErrorContext. */
    interface IErrorContext {

        /** ErrorContext readableErrorMessage */
        readableErrorMessage?: (string|null);

        /** ErrorContext additionalErrorMessage */
        additionalErrorMessage?: (string|null);

        /** ErrorContext stackTrace */
        stackTrace?: (data.IStackTrace|null);

        /** ErrorContext errorFingerprint */
        errorFingerprint?: (string|null);

        /** ErrorContext scriptSource */
        scriptSource?: (data.IScriptSource|null);

        /** ErrorContext executionObjectSource */
        executionObjectSource?: (data.IScriptSource|null);
    }

    /** Represents an ErrorContext. */
    class ErrorContext implements IErrorContext {

        /**
         * Constructs a new ErrorContext.
         * @param [properties] Properties to set
         */
        constructor(properties?: data.IErrorContext);

        /** ErrorContext readableErrorMessage. */
        public readableErrorMessage: string;

        /** ErrorContext additionalErrorMessage. */
        public additionalErrorMessage: string;

        /** ErrorContext stackTrace. */
        public stackTrace?: (data.IStackTrace|null);

        /** ErrorContext errorFingerprint. */
        public errorFingerprint: string;

        /** ErrorContext scriptSource. */
        public scriptSource?: (data.IScriptSource|null);

        /** ErrorContext executionObjectSource. */
        public executionObjectSource?: (data.IScriptSource|null);

        /**
         * Encodes the specified ErrorContext message. Does not implicitly {@link data.ErrorContext.verify|verify} messages.
         * @param message ErrorContext message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.IErrorContext, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes an ErrorContext message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns ErrorContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.ErrorContext;
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
         * @param [properties] Properties to set
         */
        constructor(properties?: data.ISessionContext);

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
         * Encodes the specified SessionContext message. Does not implicitly {@link data.SessionContext.verify|verify} messages.
         * @param message SessionContext message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.ISessionContext, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a SessionContext message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns SessionContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.SessionContext;
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
         * @param [properties] Properties to set
         */
        constructor(properties?: data.IRunConfig);

        /** RunConfig runcfg. */
        public runcfg: string;

        /** RunConfig buildInformation. */
        public buildInformation?: (data.IBuildInformation|null);

        /** RunConfig reportName. */
        public reportName: string;

        /**
         * Encodes the specified RunConfig message. Does not implicitly {@link data.RunConfig.verify|verify} messages.
         * @param message RunConfig message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.IRunConfig, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a RunConfig message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns RunConfig
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.RunConfig;
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
         * @param [properties] Properties to set
         */
        constructor(properties?: data.IBuildInformation);

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
         * Encodes the specified BuildInformation message. Does not implicitly {@link data.BuildInformation.verify|verify} messages.
         * @param message BuildInformation message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.IBuildInformation, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a BuildInformation message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns BuildInformation
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.BuildInformation;
    }

    /** FailureCorridorValue enum. */
    enum FailureCorridorValue {
        FCV_NOT_SET = 0,
        HIGH = 1,
        MID = 2,
        LOW = 3
    }

    /** MethodType enum. */
    enum MethodType {
        MT_NOT_SET = 0,
        TEST_METHOD = 1,
        CONFIGURATION_METHOD = 2
    }

    /** Properties of a StackTrace. */
    interface IStackTrace {

        /** StackTrace cause */
        cause?: (data.IStackTraceCause|null);

        /** StackTrace additionalErrorMessage */
        additionalErrorMessage?: (string|null);
    }

    /** Represents a StackTrace. */
    class StackTrace implements IStackTrace {

        /**
         * Constructs a new StackTrace.
         * @param [properties] Properties to set
         */
        constructor(properties?: data.IStackTrace);

        /** StackTrace cause. */
        public cause?: (data.IStackTraceCause|null);

        /** StackTrace additionalErrorMessage. */
        public additionalErrorMessage: string;

        /**
         * Encodes the specified StackTrace message. Does not implicitly {@link data.StackTrace.verify|verify} messages.
         * @param message StackTrace message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.IStackTrace, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a StackTrace message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns StackTrace
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.StackTrace;
    }

    /** Properties of a StackTraceCause. */
    interface IStackTraceCause {

        /** StackTraceCause className */
        className?: (string|null);

        /** StackTraceCause message */
        message?: (string|null);

        /** StackTraceCause stackTraceElements */
        stackTraceElements?: (string[]|null);

        /** StackTraceCause cause */
        cause?: (data.IStackTraceCause|null);
    }

    /** Represents a StackTraceCause. */
    class StackTraceCause implements IStackTraceCause {

        /**
         * Constructs a new StackTraceCause.
         * @param [properties] Properties to set
         */
        constructor(properties?: data.IStackTraceCause);

        /** StackTraceCause className. */
        public className: string;

        /** StackTraceCause message. */
        public message: string;

        /** StackTraceCause stackTraceElements. */
        public stackTraceElements: string[];

        /** StackTraceCause cause. */
        public cause?: (data.IStackTraceCause|null);

        /**
         * Encodes the specified StackTraceCause message. Does not implicitly {@link data.StackTraceCause.verify|verify} messages.
         * @param message StackTraceCause message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.IStackTraceCause, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a StackTraceCause message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns StackTraceCause
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.StackTraceCause;
    }

    /** Properties of a ScriptSource. */
    interface IScriptSource {

        /** ScriptSource fileName */
        fileName?: (string|null);

        /** ScriptSource methodName */
        methodName?: (string|null);

        /** ScriptSource lines */
        lines?: (data.IScriptSourceLine[]|null);
    }

    /** Represents a ScriptSource. */
    class ScriptSource implements IScriptSource {

        /**
         * Constructs a new ScriptSource.
         * @param [properties] Properties to set
         */
        constructor(properties?: data.IScriptSource);

        /** ScriptSource fileName. */
        public fileName: string;

        /** ScriptSource methodName. */
        public methodName: string;

        /** ScriptSource lines. */
        public lines: data.IScriptSourceLine[];

        /**
         * Encodes the specified ScriptSource message. Does not implicitly {@link data.ScriptSource.verify|verify} messages.
         * @param message ScriptSource message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.IScriptSource, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a ScriptSource message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns ScriptSource
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.ScriptSource;
    }

    /** Properties of a ScriptSourceLine. */
    interface IScriptSourceLine {

        /** ScriptSourceLine line */
        line?: (string|null);

        /** ScriptSourceLine lineNumber */
        lineNumber?: (number|null);

        /** ScriptSourceLine mark */
        mark?: (boolean|null);
    }

    /** Represents a ScriptSourceLine. */
    class ScriptSourceLine implements IScriptSourceLine {

        /**
         * Constructs a new ScriptSourceLine.
         * @param [properties] Properties to set
         */
        constructor(properties?: data.IScriptSourceLine);

        /** ScriptSourceLine line. */
        public line: string;

        /** ScriptSourceLine lineNumber. */
        public lineNumber: number;

        /** ScriptSourceLine mark. */
        public mark: boolean;

        /**
         * Encodes the specified ScriptSourceLine message. Does not implicitly {@link data.ScriptSourceLine.verify|verify} messages.
         * @param message ScriptSourceLine message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.IScriptSourceLine, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a ScriptSourceLine message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns ScriptSourceLine
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.ScriptSourceLine;
    }

    /** Properties of a File. */
    interface IFile {

        /** File id */
        id?: (string|null);

        /** File size */
        size?: (number|Long|null);

        /** File mimetype */
        mimetype?: (string|null);

        /** File relativePath */
        relativePath?: (string|null);

        /** File createdTimestamp */
        createdTimestamp?: (number|Long|null);

        /** File sha1Checksum */
        sha1Checksum?: (Uint8Array|null);

        /** File meta */
        meta?: ({ [k: string]: string }|null);

        /** File lastModified */
        lastModified?: (number|Long|null);

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
         * @param [properties] Properties to set
         */
        constructor(properties?: data.IFile);

        /** File id. */
        public id: string;

        /** File size. */
        public size: (number|Long);

        /** File mimetype. */
        public mimetype: string;

        /** File relativePath. */
        public relativePath: string;

        /** File createdTimestamp. */
        public createdTimestamp: (number|Long);

        /** File sha1Checksum. */
        public sha1Checksum: Uint8Array;

        /** File meta. */
        public meta: { [k: string]: string };

        /** File lastModified. */
        public lastModified: (number|Long);

        /** File projectId. */
        public projectId: string;

        /** File jobId. */
        public jobId: string;

        /** File isDirectory. */
        public isDirectory: boolean;

        /** File name. */
        public name: string;

        /**
         * Encodes the specified File message. Does not implicitly {@link data.File.verify|verify} messages.
         * @param message File message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.IFile, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a File message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns File
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.File;
    }

    /** ExecStatusType enum. */
    enum ExecStatusType {
        EST_NOT_SET = 0,
        NEW = 1,
        PENDING = 2,
        PROVISIONING = 3,
        RUNNING = 4,
        FINISHED = 5,
        ABORTED = 6,
        CRASHED = 7,
        INVALID = 8,
        VOID = 9,
        ARTIFACT_UPLOAD = 10
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

    /** Properties of a ClassContextAggregate. */
    interface IClassContextAggregate {

        /** ClassContextAggregate classContext */
        classContext?: (data.IClassContext|null);

        /** ClassContextAggregate methodContexts */
        methodContexts?: (data.IMethodContext[]|null);
    }

    /** Represents a ClassContextAggregate. */
    class ClassContextAggregate implements IClassContextAggregate {

        /**
         * Constructs a new ClassContextAggregate.
         * @param [properties] Properties to set
         */
        constructor(properties?: data.IClassContextAggregate);

        /** ClassContextAggregate classContext. */
        public classContext?: (data.IClassContext|null);

        /** ClassContextAggregate methodContexts. */
        public methodContexts: data.IMethodContext[];

        /**
         * Encodes the specified ClassContextAggregate message. Does not implicitly {@link data.ClassContextAggregate.verify|verify} messages.
         * @param message ClassContextAggregate message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: data.IClassContextAggregate, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a ClassContextAggregate message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns ClassContextAggregate
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): data.ClassContextAggregate;
    }
}
