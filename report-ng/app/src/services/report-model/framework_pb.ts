/* eslint-disable */
import Long from "long";
import _m0 from "protobufjs/minimal";

export enum ClickPathEventType {
  CPET_NOT_SET = 0,
  CPET_WINDOW = 1,
  CPET_CLICK = 2,
  CPET_VALUE = 3,
  CPET_PAGE = 4,
  CPET_URL = 5,
}

export enum LogMessageType {
  LMT_OFF = 0,
  LMT_ERROR = 1,
  LMT_WARN = 2,
  LMT_INFO = 3,
  LMT_DEBUG = 4,
}

export enum FailureCorridorValue {
  FCV_NOT_SET = 0,
  FCV_HIGH = 1,
  FCV_MID = 2,
  FCV_LOW = 3,
}

export enum MethodType {
  MT_NOT_SET = 0,
  TEST_METHOD = 1,
  CONFIGURATION_METHOD = 2,
}

/** All status elements for an actual executed test method */
export enum ResultStatusType {
  RST_NOT_SET = 0,
  /** NO_RUN - default status when entity is spawned, basically an illegal end status */
  NO_RUN = 1,
  /**
   * INFO - info status, not representative
   *
   * @deprecated
   */
  INFO = 2,
  /** SKIPPED - skipped */
  SKIPPED = 3,
  /** PASSED - passed status, without any issue */
  PASSED = 4,
  /**
   * MINOR - passed, but with minor, non-blocking issues
   *
   * @deprecated
   */
  MINOR = 5,
  /** FAILED - failed status, with hard failing issues */
  FAILED = 7,
  /**
   * FAILED_MINOR - failed with additional minor issues
   *
   * @deprecated
   */
  FAILED_MINOR = 8,
  /** FAILED_RETRIED - failed and retry is triggered */
  FAILED_RETRIED = 9,
  /** FAILED_EXPECTED - entity was expected to fail (with or without an explicit issue), it may or may not be representative */
  FAILED_EXPECTED = 10,
  /** PASSED_RETRY - passed after a retry */
  PASSED_RETRY = 11,
  /**
   * MINOR_RETRY - minor after a retry
   *
   * @deprecated
   */
  MINOR_RETRY = 12,
  /** REPAIRED - passed with fail annotation */
  REPAIRED = 13,
}

export interface SuiteContext {
  contextValues?:
    | ContextValues
    | undefined;
  /**
   * list of all test
   *
   * @deprecated
   */
  testContextIds?:
    | string[]
    | undefined;
  /** reference */
  executionContextId?: string | undefined;
}

export interface ClassContext {
  contextValues?:
    | ContextValues
    | undefined;
  /** @deprecated */
  methodContextIds?: string[] | undefined;
  fullClassName?:
    | string
    | undefined;
  /** string simple_class_name = 8 [deprecated = true]; */
  testContextId?:
    | string
    | undefined;
  /** @deprecated */
  executionContextId?:
    | string
    | undefined;
  /** bool merged = 12 [deprecated = true]; */
  testContextName?: string | undefined;
}

export interface TestContext {
  contextValues?:
    | ContextValues
    | undefined;
  /** @deprecated */
  classContextIds?: string[] | undefined;
  suiteContextId?:
    | string
    | undefined;
  /** @deprecated */
  executionContextId?: string | undefined;
}

export interface ExecutionContext {
  contextValues?:
    | ContextValues
    | undefined;
  /**
   * repeated string merged_class_context_ids = 3 [deprecated = true];
   *    repeated ContextClip exit_points = 4 [deprecated = true];
   *    repeated ContextClip failure_ascpects = 5 [deprecated = true];
   *
   * @deprecated
   */
  suiteContextIds?: string[] | undefined;
  runConfig?: RunConfig | undefined;
  projectId?: string | undefined;
  jobId?: string | undefined;
  runId?: string | undefined;
  taskId?: string | undefined;
  exclusiveSessionContextIds?: string[] | undefined;
  logMessages?: LogMessage[] | undefined;
  estimatedTestsCount?: number | undefined;
  failureCorridorLimits?: { [key: number]: number } | undefined;
  failureCorridorCounts?: { [key: number]: number } | undefined;
}

export interface ExecutionContext_FailureCorridorLimitsEntry {
  key: number;
  value: number;
}

export interface ExecutionContext_FailureCorridorCountsEntry {
  key: number;
  value: number;
}

export interface MethodContext {
  contextValues?: ContextValues | undefined;
  methodType?:
    | MethodType
    | undefined;
  /**
   * repeated string parameters = 8 [deprecated = true];
   *    repeated string method_tags = 9 [deprecated = true];
   */
  retryNumber?: number | undefined;
  methodRunIndex?: number | undefined;
  threadName?:
    | string
    | undefined;
  /** TestStep failed_step = 13; */
  failureCorridorValue?: FailureCorridorValue | undefined;
  classContextId?:
    | string
    | undefined;
  /** @deprecated */
  executionContextId?:
    | string
    | undefined;
  /**
   * repeated ErrorContext non_functional_infos = 17 [deprecated = true];
   *    repeated ErrorContext collected_assertions = 18 [deprecated = true];
   *
   * @deprecated
   */
  infos?:
    | string[]
    | undefined;
  /** @deprecated */
  priorityMessage?: string | undefined;
  relatedMethodContextIds?: string[] | undefined;
  dependsOnMethodContextIds?:
    | string[]
    | undefined;
  /** @deprecated */
  errorContext?: ErrorContext | undefined;
  testSteps?:
    | TestStep[]
    | undefined;
  /** @deprecated */
  testContextId?:
    | string
    | undefined;
  /** @deprecated */
  suiteContextId?: string | undefined;
  sessionContextIds?:
    | string[]
    | undefined;
  /**
   * repeated string video_ids = 30  [deprecated = true];
   *    repeated string screenshot_ids = 31 [deprecated = true];
   *    string custom_context_json = 32 [deprecated = true];
   */
  failedStepIndex?: number | undefined;
  resultStatus?: ResultStatusType | undefined;
  parameters?: { [key: string]: string } | undefined;
  customContexts?: { [key: string]: string } | undefined;
  annotations?:
    | { [key: string]: string }
    | undefined;
  /** A custom generated test name (e.a. cucumber scenario) */
  testName?: string | undefined;
}

export interface MethodContext_ParametersEntry {
  key: string;
  value: string;
}

export interface MethodContext_CustomContextsEntry {
  key: string;
  value: string;
}

export interface MethodContext_AnnotationsEntry {
  key: string;
  value: string;
}

export interface ContextValues {
  id?: string | undefined;
  created?: number | undefined;
  name?: string | undefined;
  startTime?:
    | number
    | undefined;
  /**
   * string swi = 6 [deprecated = true];
   *    ResultStatusType result_status = 7 [deprecated = true];
   *    ExecStatusType exec_status = 8 [deprecated = true];
   */
  endTime?: number | undefined;
}

export interface TestStep {
  name?:
    | string
    | undefined;
  /** string id = 2; */
  actions?: TestStepAction[] | undefined;
}

export interface TestStepAction {
  name?:
    | string
    | undefined;
  /** string id = 2; */
  timestamp?:
    | number
    | undefined;
  /**
   * repeated string screenshot_names = 4 [deprecated = true];
   *    repeated ClickPathEvent clickpath_events = 5 [deprecated = true];
   *    repeated string screenshot_ids = 6 [deprecated = true];
   */
  entries?: TestStepActionEntry[] | undefined;
}

export interface TestStepActionEntry {
  clickPathEvent?: ClickPathEvent | undefined;
  screenshotId?: string | undefined;
  logMessage?:
    | LogMessage
    | undefined;
  /** @deprecated */
  assertion?: ErrorContext | undefined;
  errorContext?: ErrorContext | undefined;
}

export interface ClickPathEvent {
  type?: ClickPathEventType | undefined;
  subject?: string | undefined;
  sessionId?: string | undefined;
}

export interface LogMessage {
  type?: LogMessageType | undefined;
  loggerName?: string | undefined;
  message?: string | undefined;
  timestamp?: number | undefined;
  threadName?: string | undefined;
  stackTrace?: StackTraceCause[] | undefined;
  prompt?: boolean | undefined;
}

export interface ErrorContext {
  /**
   * string readable_error_message = 1 [deprecated = true];
   *    string additional_error_message = 2 [deprecated = true];
   *    StackTrace stack_trace = 3  [deprecated = true];
   *    string error_fingerprint = 6 [deprecated = true];
   */
  scriptSource?:
    | ScriptSource
    | undefined;
  /**
   * ScriptSource execution_object_source = 8 [deprecated = true];
   *
   * @deprecated
   */
  ticketId?:
    | string
    | undefined;
  /** @deprecated */
  description?: string | undefined;
  stackTrace?: StackTraceCause[] | undefined;
  optional?: boolean | undefined;
}

export interface SessionContext {
  contextValues?:
    | ContextValues
    | undefined;
  /**
   * string session_key = 2 [deprecated = true];
   *    string provider = 3 [deprecated = true];
   *    map<string, string> metadata = 4 [deprecated = true];
   */
  sessionId?: string | undefined;
  videoId?: string | undefined;
  executionContextId?: string | undefined;
  browserName?: string | undefined;
  browserVersion?: string | undefined;
  capabilities?: string | undefined;
  serverUrl?: string | undefined;
  nodeUrl?: string | undefined;
  userAgent?: string | undefined;
}

export interface RunConfig {
  runcfg?: string | undefined;
  buildInformation?: BuildInformation | undefined;
  reportName?: string | undefined;
}

export interface BuildInformation {
  buildJavaVersion?: string | undefined;
  buildOsName?: string | undefined;
  buildOsVersion?: string | undefined;
  buildUserName?: string | undefined;
  buildVersion?: string | undefined;
  buildTimestamp?: string | undefined;
}

export interface StackTraceCause {
  className?: string | undefined;
  message?:
    | string
    | undefined;
  /** StackTraceCause cause = 4 [deprecated = true]; */
  stackTraceElements?: string[] | undefined;
}

export interface ScriptSource {
  fileName?: string | undefined;
  methodName?: string | undefined;
  lines?: ScriptSourceLine[] | undefined;
  mark?: number | undefined;
}

export interface ScriptSourceLine {
  line?:
    | string
    | undefined;
  /** bool mark = 3 [deprecated = true]; */
  lineNumber?: number | undefined;
}

export interface File {
  /** XID */
  id?:
    | string
    | undefined;
  /** in bytes */
  size?: number | undefined;
  mimetype?:
    | string
    | undefined;
  /** relative path based on project storage --> /projectID/jobID/XXX... */
  relativePath?: string | undefined;
  createdTimestamp?: number | undefined;
  sha1Checksum?: Uint8Array | undefined;
  meta?: { [key: string]: string } | undefined;
  lastModified?: number | undefined;
  projectId?: string | undefined;
  jobId?: string | undefined;
  isDirectory?: boolean | undefined;
  name?: string | undefined;
}

export interface File_MetaEntry {
  key: string;
  value: string;
}

function createBaseSuiteContext(): SuiteContext {
  return { contextValues: undefined, testContextIds: [], executionContextId: "" };
}

export const SuiteContext = {
  encode(message: SuiteContext, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.contextValues !== undefined) {
      ContextValues.encode(message.contextValues, writer.uint32(10).fork()).ldelim();
    }
    if (message.testContextIds !== undefined && message.testContextIds.length !== 0) {
      for (const v of message.testContextIds) {
        writer.uint32(50).string(v!);
      }
    }
    if (message.executionContextId !== undefined && message.executionContextId !== "") {
      writer.uint32(58).string(message.executionContextId);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): SuiteContext {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseSuiteContext();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.contextValues = ContextValues.decode(reader, reader.uint32());
          continue;
        case 6:
          if (tag !== 50) {
            break;
          }

          message.testContextIds!.push(reader.string());
          continue;
        case 7:
          if (tag !== 58) {
            break;
          }

          message.executionContextId = reader.string();
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseClassContext(): ClassContext {
  return {
    contextValues: undefined,
    methodContextIds: [],
    fullClassName: "",
    testContextId: "",
    executionContextId: "",
    testContextName: "",
  };
}

export const ClassContext = {
  encode(message: ClassContext, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.contextValues !== undefined) {
      ContextValues.encode(message.contextValues, writer.uint32(10).fork()).ldelim();
    }
    if (message.methodContextIds !== undefined && message.methodContextIds.length !== 0) {
      for (const v of message.methodContextIds) {
        writer.uint32(50).string(v!);
      }
    }
    if (message.fullClassName !== undefined && message.fullClassName !== "") {
      writer.uint32(58).string(message.fullClassName);
    }
    if (message.testContextId !== undefined && message.testContextId !== "") {
      writer.uint32(74).string(message.testContextId);
    }
    if (message.executionContextId !== undefined && message.executionContextId !== "") {
      writer.uint32(82).string(message.executionContextId);
    }
    if (message.testContextName !== undefined && message.testContextName !== "") {
      writer.uint32(90).string(message.testContextName);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): ClassContext {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseClassContext();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.contextValues = ContextValues.decode(reader, reader.uint32());
          continue;
        case 6:
          if (tag !== 50) {
            break;
          }

          message.methodContextIds!.push(reader.string());
          continue;
        case 7:
          if (tag !== 58) {
            break;
          }

          message.fullClassName = reader.string();
          continue;
        case 9:
          if (tag !== 74) {
            break;
          }

          message.testContextId = reader.string();
          continue;
        case 10:
          if (tag !== 82) {
            break;
          }

          message.executionContextId = reader.string();
          continue;
        case 11:
          if (tag !== 90) {
            break;
          }

          message.testContextName = reader.string();
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseTestContext(): TestContext {
  return { contextValues: undefined, classContextIds: [], suiteContextId: "", executionContextId: "" };
}

export const TestContext = {
  encode(message: TestContext, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.contextValues !== undefined) {
      ContextValues.encode(message.contextValues, writer.uint32(10).fork()).ldelim();
    }
    if (message.classContextIds !== undefined && message.classContextIds.length !== 0) {
      for (const v of message.classContextIds) {
        writer.uint32(50).string(v!);
      }
    }
    if (message.suiteContextId !== undefined && message.suiteContextId !== "") {
      writer.uint32(58).string(message.suiteContextId);
    }
    if (message.executionContextId !== undefined && message.executionContextId !== "") {
      writer.uint32(66).string(message.executionContextId);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): TestContext {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseTestContext();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.contextValues = ContextValues.decode(reader, reader.uint32());
          continue;
        case 6:
          if (tag !== 50) {
            break;
          }

          message.classContextIds!.push(reader.string());
          continue;
        case 7:
          if (tag !== 58) {
            break;
          }

          message.suiteContextId = reader.string();
          continue;
        case 8:
          if (tag !== 66) {
            break;
          }

          message.executionContextId = reader.string();
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseExecutionContext(): ExecutionContext {
  return {
    contextValues: undefined,
    suiteContextIds: [],
    runConfig: undefined,
    projectId: "",
    jobId: "",
    runId: "",
    taskId: "",
    exclusiveSessionContextIds: [],
    logMessages: [],
    estimatedTestsCount: 0,
    failureCorridorLimits: {},
    failureCorridorCounts: {},
  };
}

export const ExecutionContext = {
  encode(message: ExecutionContext, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.contextValues !== undefined) {
      ContextValues.encode(message.contextValues, writer.uint32(10).fork()).ldelim();
    }
    if (message.suiteContextIds !== undefined && message.suiteContextIds.length !== 0) {
      for (const v of message.suiteContextIds) {
        writer.uint32(50).string(v!);
      }
    }
    if (message.runConfig !== undefined) {
      RunConfig.encode(message.runConfig, writer.uint32(58).fork()).ldelim();
    }
    if (message.projectId !== undefined && message.projectId !== "") {
      writer.uint32(66).string(message.projectId);
    }
    if (message.jobId !== undefined && message.jobId !== "") {
      writer.uint32(74).string(message.jobId);
    }
    if (message.runId !== undefined && message.runId !== "") {
      writer.uint32(82).string(message.runId);
    }
    if (message.taskId !== undefined && message.taskId !== "") {
      writer.uint32(90).string(message.taskId);
    }
    if (message.exclusiveSessionContextIds !== undefined && message.exclusiveSessionContextIds.length !== 0) {
      for (const v of message.exclusiveSessionContextIds) {
        writer.uint32(98).string(v!);
      }
    }
    if (message.logMessages !== undefined && message.logMessages.length !== 0) {
      for (const v of message.logMessages) {
        LogMessage.encode(v!, writer.uint32(114).fork()).ldelim();
      }
    }
    if (message.estimatedTestsCount !== undefined && message.estimatedTestsCount !== 0) {
      writer.uint32(120).int32(message.estimatedTestsCount);
    }
    Object.entries(message.failureCorridorLimits || {}).forEach(([key, value]) => {
      ExecutionContext_FailureCorridorLimitsEntry.encode({ key: key as any, value }, writer.uint32(130).fork())
        .ldelim();
    });
    Object.entries(message.failureCorridorCounts || {}).forEach(([key, value]) => {
      ExecutionContext_FailureCorridorCountsEntry.encode({ key: key as any, value }, writer.uint32(138).fork())
        .ldelim();
    });
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): ExecutionContext {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseExecutionContext();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.contextValues = ContextValues.decode(reader, reader.uint32());
          continue;
        case 6:
          if (tag !== 50) {
            break;
          }

          message.suiteContextIds!.push(reader.string());
          continue;
        case 7:
          if (tag !== 58) {
            break;
          }

          message.runConfig = RunConfig.decode(reader, reader.uint32());
          continue;
        case 8:
          if (tag !== 66) {
            break;
          }

          message.projectId = reader.string();
          continue;
        case 9:
          if (tag !== 74) {
            break;
          }

          message.jobId = reader.string();
          continue;
        case 10:
          if (tag !== 82) {
            break;
          }

          message.runId = reader.string();
          continue;
        case 11:
          if (tag !== 90) {
            break;
          }

          message.taskId = reader.string();
          continue;
        case 12:
          if (tag !== 98) {
            break;
          }

          message.exclusiveSessionContextIds!.push(reader.string());
          continue;
        case 14:
          if (tag !== 114) {
            break;
          }

          message.logMessages!.push(LogMessage.decode(reader, reader.uint32()));
          continue;
        case 15:
          if (tag !== 120) {
            break;
          }

          message.estimatedTestsCount = reader.int32();
          continue;
        case 16:
          if (tag !== 130) {
            break;
          }

          const entry16 = ExecutionContext_FailureCorridorLimitsEntry.decode(reader, reader.uint32());
          if (entry16.value !== undefined) {
            message.failureCorridorLimits![entry16.key] = entry16.value;
          }
          continue;
        case 17:
          if (tag !== 138) {
            break;
          }

          const entry17 = ExecutionContext_FailureCorridorCountsEntry.decode(reader, reader.uint32());
          if (entry17.value !== undefined) {
            message.failureCorridorCounts![entry17.key] = entry17.value;
          }
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseExecutionContext_FailureCorridorLimitsEntry(): ExecutionContext_FailureCorridorLimitsEntry {
  return { key: 0, value: 0 };
}

export const ExecutionContext_FailureCorridorLimitsEntry = {
  encode(message: ExecutionContext_FailureCorridorLimitsEntry, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.key !== 0) {
      writer.uint32(8).int32(message.key);
    }
    if (message.value !== 0) {
      writer.uint32(16).int32(message.value);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): ExecutionContext_FailureCorridorLimitsEntry {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseExecutionContext_FailureCorridorLimitsEntry();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 8) {
            break;
          }

          message.key = reader.int32();
          continue;
        case 2:
          if (tag !== 16) {
            break;
          }

          message.value = reader.int32();
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseExecutionContext_FailureCorridorCountsEntry(): ExecutionContext_FailureCorridorCountsEntry {
  return { key: 0, value: 0 };
}

export const ExecutionContext_FailureCorridorCountsEntry = {
  encode(message: ExecutionContext_FailureCorridorCountsEntry, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.key !== 0) {
      writer.uint32(8).int32(message.key);
    }
    if (message.value !== 0) {
      writer.uint32(16).int32(message.value);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): ExecutionContext_FailureCorridorCountsEntry {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseExecutionContext_FailureCorridorCountsEntry();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 8) {
            break;
          }

          message.key = reader.int32();
          continue;
        case 2:
          if (tag !== 16) {
            break;
          }

          message.value = reader.int32();
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseMethodContext(): MethodContext {
  return {
    contextValues: undefined,
    methodType: 0,
    retryNumber: 0,
    methodRunIndex: 0,
    threadName: "",
    failureCorridorValue: 0,
    classContextId: "",
    executionContextId: "",
    infos: [],
    priorityMessage: "",
    relatedMethodContextIds: [],
    dependsOnMethodContextIds: [],
    errorContext: undefined,
    testSteps: [],
    testContextId: "",
    suiteContextId: "",
    sessionContextIds: [],
    failedStepIndex: 0,
    resultStatus: 0,
    parameters: {},
    customContexts: {},
    annotations: {},
    testName: "",
  };
}

export const MethodContext = {
  encode(message: MethodContext, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.contextValues !== undefined) {
      ContextValues.encode(message.contextValues, writer.uint32(10).fork()).ldelim();
    }
    if (message.methodType !== undefined && message.methodType !== 0) {
      writer.uint32(56).int32(message.methodType);
    }
    if (message.retryNumber !== undefined && message.retryNumber !== 0) {
      writer.uint32(80).int32(message.retryNumber);
    }
    if (message.methodRunIndex !== undefined && message.methodRunIndex !== 0) {
      writer.uint32(88).int32(message.methodRunIndex);
    }
    if (message.threadName !== undefined && message.threadName !== "") {
      writer.uint32(98).string(message.threadName);
    }
    if (message.failureCorridorValue !== undefined && message.failureCorridorValue !== 0) {
      writer.uint32(112).int32(message.failureCorridorValue);
    }
    if (message.classContextId !== undefined && message.classContextId !== "") {
      writer.uint32(122).string(message.classContextId);
    }
    if (message.executionContextId !== undefined && message.executionContextId !== "") {
      writer.uint32(130).string(message.executionContextId);
    }
    if (message.infos !== undefined && message.infos.length !== 0) {
      for (const v of message.infos) {
        writer.uint32(154).string(v!);
      }
    }
    if (message.priorityMessage !== undefined && message.priorityMessage !== "") {
      writer.uint32(170).string(message.priorityMessage);
    }
    if (message.relatedMethodContextIds !== undefined && message.relatedMethodContextIds.length !== 0) {
      for (const v of message.relatedMethodContextIds) {
        writer.uint32(186).string(v!);
      }
    }
    if (message.dependsOnMethodContextIds !== undefined && message.dependsOnMethodContextIds.length !== 0) {
      for (const v of message.dependsOnMethodContextIds) {
        writer.uint32(194).string(v!);
      }
    }
    if (message.errorContext !== undefined) {
      ErrorContext.encode(message.errorContext, writer.uint32(202).fork()).ldelim();
    }
    if (message.testSteps !== undefined && message.testSteps.length !== 0) {
      for (const v of message.testSteps) {
        TestStep.encode(v!, writer.uint32(210).fork()).ldelim();
      }
    }
    if (message.testContextId !== undefined && message.testContextId !== "") {
      writer.uint32(218).string(message.testContextId);
    }
    if (message.suiteContextId !== undefined && message.suiteContextId !== "") {
      writer.uint32(226).string(message.suiteContextId);
    }
    if (message.sessionContextIds !== undefined && message.sessionContextIds.length !== 0) {
      for (const v of message.sessionContextIds) {
        writer.uint32(234).string(v!);
      }
    }
    if (message.failedStepIndex !== undefined && message.failedStepIndex !== 0) {
      writer.uint32(264).int32(message.failedStepIndex);
    }
    if (message.resultStatus !== undefined && message.resultStatus !== 0) {
      writer.uint32(272).int32(message.resultStatus);
    }
    Object.entries(message.parameters || {}).forEach(([key, value]) => {
      MethodContext_ParametersEntry.encode({ key: key as any, value }, writer.uint32(282).fork()).ldelim();
    });
    Object.entries(message.customContexts || {}).forEach(([key, value]) => {
      MethodContext_CustomContextsEntry.encode({ key: key as any, value }, writer.uint32(290).fork()).ldelim();
    });
    Object.entries(message.annotations || {}).forEach(([key, value]) => {
      MethodContext_AnnotationsEntry.encode({ key: key as any, value }, writer.uint32(298).fork()).ldelim();
    });
    if (message.testName !== undefined && message.testName !== "") {
      writer.uint32(306).string(message.testName);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): MethodContext {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseMethodContext();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.contextValues = ContextValues.decode(reader, reader.uint32());
          continue;
        case 7:
          if (tag !== 56) {
            break;
          }

          message.methodType = reader.int32() as any;
          continue;
        case 10:
          if (tag !== 80) {
            break;
          }

          message.retryNumber = reader.int32();
          continue;
        case 11:
          if (tag !== 88) {
            break;
          }

          message.methodRunIndex = reader.int32();
          continue;
        case 12:
          if (tag !== 98) {
            break;
          }

          message.threadName = reader.string();
          continue;
        case 14:
          if (tag !== 112) {
            break;
          }

          message.failureCorridorValue = reader.int32() as any;
          continue;
        case 15:
          if (tag !== 122) {
            break;
          }

          message.classContextId = reader.string();
          continue;
        case 16:
          if (tag !== 130) {
            break;
          }

          message.executionContextId = reader.string();
          continue;
        case 19:
          if (tag !== 154) {
            break;
          }

          message.infos!.push(reader.string());
          continue;
        case 21:
          if (tag !== 170) {
            break;
          }

          message.priorityMessage = reader.string();
          continue;
        case 23:
          if (tag !== 186) {
            break;
          }

          message.relatedMethodContextIds!.push(reader.string());
          continue;
        case 24:
          if (tag !== 194) {
            break;
          }

          message.dependsOnMethodContextIds!.push(reader.string());
          continue;
        case 25:
          if (tag !== 202) {
            break;
          }

          message.errorContext = ErrorContext.decode(reader, reader.uint32());
          continue;
        case 26:
          if (tag !== 210) {
            break;
          }

          message.testSteps!.push(TestStep.decode(reader, reader.uint32()));
          continue;
        case 27:
          if (tag !== 218) {
            break;
          }

          message.testContextId = reader.string();
          continue;
        case 28:
          if (tag !== 226) {
            break;
          }

          message.suiteContextId = reader.string();
          continue;
        case 29:
          if (tag !== 234) {
            break;
          }

          message.sessionContextIds!.push(reader.string());
          continue;
        case 33:
          if (tag !== 264) {
            break;
          }

          message.failedStepIndex = reader.int32();
          continue;
        case 34:
          if (tag !== 272) {
            break;
          }

          message.resultStatus = reader.int32() as any;
          continue;
        case 35:
          if (tag !== 282) {
            break;
          }

          const entry35 = MethodContext_ParametersEntry.decode(reader, reader.uint32());
          if (entry35.value !== undefined) {
            message.parameters![entry35.key] = entry35.value;
          }
          continue;
        case 36:
          if (tag !== 290) {
            break;
          }

          const entry36 = MethodContext_CustomContextsEntry.decode(reader, reader.uint32());
          if (entry36.value !== undefined) {
            message.customContexts![entry36.key] = entry36.value;
          }
          continue;
        case 37:
          if (tag !== 298) {
            break;
          }

          const entry37 = MethodContext_AnnotationsEntry.decode(reader, reader.uint32());
          if (entry37.value !== undefined) {
            message.annotations![entry37.key] = entry37.value;
          }
          continue;
        case 38:
          if (tag !== 306) {
            break;
          }

          message.testName = reader.string();
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseMethodContext_ParametersEntry(): MethodContext_ParametersEntry {
  return { key: "", value: "" };
}

export const MethodContext_ParametersEntry = {
  encode(message: MethodContext_ParametersEntry, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.key !== "") {
      writer.uint32(10).string(message.key);
    }
    if (message.value !== "") {
      writer.uint32(18).string(message.value);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): MethodContext_ParametersEntry {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseMethodContext_ParametersEntry();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.key = reader.string();
          continue;
        case 2:
          if (tag !== 18) {
            break;
          }

          message.value = reader.string();
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseMethodContext_CustomContextsEntry(): MethodContext_CustomContextsEntry {
  return { key: "", value: "" };
}

export const MethodContext_CustomContextsEntry = {
  encode(message: MethodContext_CustomContextsEntry, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.key !== "") {
      writer.uint32(10).string(message.key);
    }
    if (message.value !== "") {
      writer.uint32(18).string(message.value);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): MethodContext_CustomContextsEntry {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseMethodContext_CustomContextsEntry();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.key = reader.string();
          continue;
        case 2:
          if (tag !== 18) {
            break;
          }

          message.value = reader.string();
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseMethodContext_AnnotationsEntry(): MethodContext_AnnotationsEntry {
  return { key: "", value: "" };
}

export const MethodContext_AnnotationsEntry = {
  encode(message: MethodContext_AnnotationsEntry, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.key !== "") {
      writer.uint32(10).string(message.key);
    }
    if (message.value !== "") {
      writer.uint32(18).string(message.value);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): MethodContext_AnnotationsEntry {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseMethodContext_AnnotationsEntry();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.key = reader.string();
          continue;
        case 2:
          if (tag !== 18) {
            break;
          }

          message.value = reader.string();
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseContextValues(): ContextValues {
  return { id: "", created: 0, name: "", startTime: 0, endTime: 0 };
}

export const ContextValues = {
  encode(message: ContextValues, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.id !== undefined && message.id !== "") {
      writer.uint32(10).string(message.id);
    }
    if (message.created !== undefined && message.created !== 0) {
      writer.uint32(16).int64(message.created);
    }
    if (message.name !== undefined && message.name !== "") {
      writer.uint32(26).string(message.name);
    }
    if (message.startTime !== undefined && message.startTime !== 0) {
      writer.uint32(32).int64(message.startTime);
    }
    if (message.endTime !== undefined && message.endTime !== 0) {
      writer.uint32(40).int64(message.endTime);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): ContextValues {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseContextValues();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.id = reader.string();
          continue;
        case 2:
          if (tag !== 16) {
            break;
          }

          message.created = longToNumber(reader.int64() as Long);
          continue;
        case 3:
          if (tag !== 26) {
            break;
          }

          message.name = reader.string();
          continue;
        case 4:
          if (tag !== 32) {
            break;
          }

          message.startTime = longToNumber(reader.int64() as Long);
          continue;
        case 5:
          if (tag !== 40) {
            break;
          }

          message.endTime = longToNumber(reader.int64() as Long);
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseTestStep(): TestStep {
  return { name: "", actions: [] };
}

export const TestStep = {
  encode(message: TestStep, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.name !== undefined && message.name !== "") {
      writer.uint32(10).string(message.name);
    }
    if (message.actions !== undefined && message.actions.length !== 0) {
      for (const v of message.actions) {
        TestStepAction.encode(v!, writer.uint32(26).fork()).ldelim();
      }
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): TestStep {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseTestStep();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.name = reader.string();
          continue;
        case 3:
          if (tag !== 26) {
            break;
          }

          message.actions!.push(TestStepAction.decode(reader, reader.uint32()));
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseTestStepAction(): TestStepAction {
  return { name: "", timestamp: 0, entries: [] };
}

export const TestStepAction = {
  encode(message: TestStepAction, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.name !== undefined && message.name !== "") {
      writer.uint32(10).string(message.name);
    }
    if (message.timestamp !== undefined && message.timestamp !== 0) {
      writer.uint32(24).int64(message.timestamp);
    }
    if (message.entries !== undefined && message.entries.length !== 0) {
      for (const v of message.entries) {
        TestStepActionEntry.encode(v!, writer.uint32(58).fork()).ldelim();
      }
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): TestStepAction {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseTestStepAction();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.name = reader.string();
          continue;
        case 3:
          if (tag !== 24) {
            break;
          }

          message.timestamp = longToNumber(reader.int64() as Long);
          continue;
        case 7:
          if (tag !== 58) {
            break;
          }

          message.entries!.push(TestStepActionEntry.decode(reader, reader.uint32()));
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseTestStepActionEntry(): TestStepActionEntry {
  return {
    clickPathEvent: undefined,
    screenshotId: undefined,
    logMessage: undefined,
    assertion: undefined,
    errorContext: undefined,
  };
}

export const TestStepActionEntry = {
  encode(message: TestStepActionEntry, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.clickPathEvent !== undefined) {
      ClickPathEvent.encode(message.clickPathEvent, writer.uint32(10).fork()).ldelim();
    }
    if (message.screenshotId !== undefined) {
      writer.uint32(18).string(message.screenshotId);
    }
    if (message.logMessage !== undefined) {
      LogMessage.encode(message.logMessage, writer.uint32(26).fork()).ldelim();
    }
    if (message.assertion !== undefined) {
      ErrorContext.encode(message.assertion, writer.uint32(34).fork()).ldelim();
    }
    if (message.errorContext !== undefined) {
      ErrorContext.encode(message.errorContext, writer.uint32(42).fork()).ldelim();
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): TestStepActionEntry {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseTestStepActionEntry();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.clickPathEvent = ClickPathEvent.decode(reader, reader.uint32());
          continue;
        case 2:
          if (tag !== 18) {
            break;
          }

          message.screenshotId = reader.string();
          continue;
        case 3:
          if (tag !== 26) {
            break;
          }

          message.logMessage = LogMessage.decode(reader, reader.uint32());
          continue;
        case 4:
          if (tag !== 34) {
            break;
          }

          message.assertion = ErrorContext.decode(reader, reader.uint32());
          continue;
        case 5:
          if (tag !== 42) {
            break;
          }

          message.errorContext = ErrorContext.decode(reader, reader.uint32());
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseClickPathEvent(): ClickPathEvent {
  return { type: 0, subject: "", sessionId: "" };
}

export const ClickPathEvent = {
  encode(message: ClickPathEvent, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.type !== undefined && message.type !== 0) {
      writer.uint32(8).int32(message.type);
    }
    if (message.subject !== undefined && message.subject !== "") {
      writer.uint32(18).string(message.subject);
    }
    if (message.sessionId !== undefined && message.sessionId !== "") {
      writer.uint32(26).string(message.sessionId);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): ClickPathEvent {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseClickPathEvent();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 8) {
            break;
          }

          message.type = reader.int32() as any;
          continue;
        case 2:
          if (tag !== 18) {
            break;
          }

          message.subject = reader.string();
          continue;
        case 3:
          if (tag !== 26) {
            break;
          }

          message.sessionId = reader.string();
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseLogMessage(): LogMessage {
  return { type: 0, loggerName: "", message: "", timestamp: 0, threadName: "", stackTrace: [], prompt: false };
}

export const LogMessage = {
  encode(message: LogMessage, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.type !== undefined && message.type !== 0) {
      writer.uint32(8).int32(message.type);
    }
    if (message.loggerName !== undefined && message.loggerName !== "") {
      writer.uint32(18).string(message.loggerName);
    }
    if (message.message !== undefined && message.message !== "") {
      writer.uint32(26).string(message.message);
    }
    if (message.timestamp !== undefined && message.timestamp !== 0) {
      writer.uint32(32).int64(message.timestamp);
    }
    if (message.threadName !== undefined && message.threadName !== "") {
      writer.uint32(42).string(message.threadName);
    }
    if (message.stackTrace !== undefined && message.stackTrace.length !== 0) {
      for (const v of message.stackTrace) {
        StackTraceCause.encode(v!, writer.uint32(50).fork()).ldelim();
      }
    }
    if (message.prompt === true) {
      writer.uint32(56).bool(message.prompt);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): LogMessage {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseLogMessage();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 8) {
            break;
          }

          message.type = reader.int32() as any;
          continue;
        case 2:
          if (tag !== 18) {
            break;
          }

          message.loggerName = reader.string();
          continue;
        case 3:
          if (tag !== 26) {
            break;
          }

          message.message = reader.string();
          continue;
        case 4:
          if (tag !== 32) {
            break;
          }

          message.timestamp = longToNumber(reader.int64() as Long);
          continue;
        case 5:
          if (tag !== 42) {
            break;
          }

          message.threadName = reader.string();
          continue;
        case 6:
          if (tag !== 50) {
            break;
          }

          message.stackTrace!.push(StackTraceCause.decode(reader, reader.uint32()));
          continue;
        case 7:
          if (tag !== 56) {
            break;
          }

          message.prompt = reader.bool();
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseErrorContext(): ErrorContext {
  return { scriptSource: undefined, ticketId: "", description: "", stackTrace: [], optional: false };
}

export const ErrorContext = {
  encode(message: ErrorContext, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.scriptSource !== undefined) {
      ScriptSource.encode(message.scriptSource, writer.uint32(58).fork()).ldelim();
    }
    if (message.ticketId !== undefined && message.ticketId !== "") {
      writer.uint32(74).string(message.ticketId);
    }
    if (message.description !== undefined && message.description !== "") {
      writer.uint32(82).string(message.description);
    }
    if (message.stackTrace !== undefined && message.stackTrace.length !== 0) {
      for (const v of message.stackTrace) {
        StackTraceCause.encode(v!, writer.uint32(90).fork()).ldelim();
      }
    }
    if (message.optional === true) {
      writer.uint32(96).bool(message.optional);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): ErrorContext {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseErrorContext();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 7:
          if (tag !== 58) {
            break;
          }

          message.scriptSource = ScriptSource.decode(reader, reader.uint32());
          continue;
        case 9:
          if (tag !== 74) {
            break;
          }

          message.ticketId = reader.string();
          continue;
        case 10:
          if (tag !== 82) {
            break;
          }

          message.description = reader.string();
          continue;
        case 11:
          if (tag !== 90) {
            break;
          }

          message.stackTrace!.push(StackTraceCause.decode(reader, reader.uint32()));
          continue;
        case 12:
          if (tag !== 96) {
            break;
          }

          message.optional = reader.bool();
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseSessionContext(): SessionContext {
  return {
    contextValues: undefined,
    sessionId: "",
    videoId: "",
    executionContextId: "",
    browserName: "",
    browserVersion: "",
    capabilities: "",
    serverUrl: "",
    nodeUrl: "",
    userAgent: "",
  };
}

export const SessionContext = {
  encode(message: SessionContext, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.contextValues !== undefined) {
      ContextValues.encode(message.contextValues, writer.uint32(10).fork()).ldelim();
    }
    if (message.sessionId !== undefined && message.sessionId !== "") {
      writer.uint32(50).string(message.sessionId);
    }
    if (message.videoId !== undefined && message.videoId !== "") {
      writer.uint32(58).string(message.videoId);
    }
    if (message.executionContextId !== undefined && message.executionContextId !== "") {
      writer.uint32(66).string(message.executionContextId);
    }
    if (message.browserName !== undefined && message.browserName !== "") {
      writer.uint32(74).string(message.browserName);
    }
    if (message.browserVersion !== undefined && message.browserVersion !== "") {
      writer.uint32(82).string(message.browserVersion);
    }
    if (message.capabilities !== undefined && message.capabilities !== "") {
      writer.uint32(90).string(message.capabilities);
    }
    if (message.serverUrl !== undefined && message.serverUrl !== "") {
      writer.uint32(98).string(message.serverUrl);
    }
    if (message.nodeUrl !== undefined && message.nodeUrl !== "") {
      writer.uint32(106).string(message.nodeUrl);
    }
    if (message.userAgent !== undefined && message.userAgent !== "") {
      writer.uint32(114).string(message.userAgent);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): SessionContext {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseSessionContext();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.contextValues = ContextValues.decode(reader, reader.uint32());
          continue;
        case 6:
          if (tag !== 50) {
            break;
          }

          message.sessionId = reader.string();
          continue;
        case 7:
          if (tag !== 58) {
            break;
          }

          message.videoId = reader.string();
          continue;
        case 8:
          if (tag !== 66) {
            break;
          }

          message.executionContextId = reader.string();
          continue;
        case 9:
          if (tag !== 74) {
            break;
          }

          message.browserName = reader.string();
          continue;
        case 10:
          if (tag !== 82) {
            break;
          }

          message.browserVersion = reader.string();
          continue;
        case 11:
          if (tag !== 90) {
            break;
          }

          message.capabilities = reader.string();
          continue;
        case 12:
          if (tag !== 98) {
            break;
          }

          message.serverUrl = reader.string();
          continue;
        case 13:
          if (tag !== 106) {
            break;
          }

          message.nodeUrl = reader.string();
          continue;
        case 14:
          if (tag !== 114) {
            break;
          }

          message.userAgent = reader.string();
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseRunConfig(): RunConfig {
  return { runcfg: "", buildInformation: undefined, reportName: "" };
}

export const RunConfig = {
  encode(message: RunConfig, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.runcfg !== undefined && message.runcfg !== "") {
      writer.uint32(10).string(message.runcfg);
    }
    if (message.buildInformation !== undefined) {
      BuildInformation.encode(message.buildInformation, writer.uint32(18).fork()).ldelim();
    }
    if (message.reportName !== undefined && message.reportName !== "") {
      writer.uint32(26).string(message.reportName);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): RunConfig {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseRunConfig();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.runcfg = reader.string();
          continue;
        case 2:
          if (tag !== 18) {
            break;
          }

          message.buildInformation = BuildInformation.decode(reader, reader.uint32());
          continue;
        case 3:
          if (tag !== 26) {
            break;
          }

          message.reportName = reader.string();
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseBuildInformation(): BuildInformation {
  return {
    buildJavaVersion: "",
    buildOsName: "",
    buildOsVersion: "",
    buildUserName: "",
    buildVersion: "",
    buildTimestamp: "",
  };
}

export const BuildInformation = {
  encode(message: BuildInformation, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.buildJavaVersion !== undefined && message.buildJavaVersion !== "") {
      writer.uint32(10).string(message.buildJavaVersion);
    }
    if (message.buildOsName !== undefined && message.buildOsName !== "") {
      writer.uint32(18).string(message.buildOsName);
    }
    if (message.buildOsVersion !== undefined && message.buildOsVersion !== "") {
      writer.uint32(26).string(message.buildOsVersion);
    }
    if (message.buildUserName !== undefined && message.buildUserName !== "") {
      writer.uint32(34).string(message.buildUserName);
    }
    if (message.buildVersion !== undefined && message.buildVersion !== "") {
      writer.uint32(42).string(message.buildVersion);
    }
    if (message.buildTimestamp !== undefined && message.buildTimestamp !== "") {
      writer.uint32(50).string(message.buildTimestamp);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): BuildInformation {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseBuildInformation();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.buildJavaVersion = reader.string();
          continue;
        case 2:
          if (tag !== 18) {
            break;
          }

          message.buildOsName = reader.string();
          continue;
        case 3:
          if (tag !== 26) {
            break;
          }

          message.buildOsVersion = reader.string();
          continue;
        case 4:
          if (tag !== 34) {
            break;
          }

          message.buildUserName = reader.string();
          continue;
        case 5:
          if (tag !== 42) {
            break;
          }

          message.buildVersion = reader.string();
          continue;
        case 6:
          if (tag !== 50) {
            break;
          }

          message.buildTimestamp = reader.string();
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseStackTraceCause(): StackTraceCause {
  return { className: "", message: "", stackTraceElements: [] };
}

export const StackTraceCause = {
  encode(message: StackTraceCause, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.className !== undefined && message.className !== "") {
      writer.uint32(10).string(message.className);
    }
    if (message.message !== undefined && message.message !== "") {
      writer.uint32(18).string(message.message);
    }
    if (message.stackTraceElements !== undefined && message.stackTraceElements.length !== 0) {
      for (const v of message.stackTraceElements) {
        writer.uint32(26).string(v!);
      }
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): StackTraceCause {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseStackTraceCause();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.className = reader.string();
          continue;
        case 2:
          if (tag !== 18) {
            break;
          }

          message.message = reader.string();
          continue;
        case 3:
          if (tag !== 26) {
            break;
          }

          message.stackTraceElements!.push(reader.string());
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseScriptSource(): ScriptSource {
  return { fileName: "", methodName: "", lines: [], mark: 0 };
}

export const ScriptSource = {
  encode(message: ScriptSource, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.fileName !== undefined && message.fileName !== "") {
      writer.uint32(10).string(message.fileName);
    }
    if (message.methodName !== undefined && message.methodName !== "") {
      writer.uint32(18).string(message.methodName);
    }
    if (message.lines !== undefined && message.lines.length !== 0) {
      for (const v of message.lines) {
        ScriptSourceLine.encode(v!, writer.uint32(26).fork()).ldelim();
      }
    }
    if (message.mark !== undefined && message.mark !== 0) {
      writer.uint32(32).int32(message.mark);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): ScriptSource {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseScriptSource();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.fileName = reader.string();
          continue;
        case 2:
          if (tag !== 18) {
            break;
          }

          message.methodName = reader.string();
          continue;
        case 3:
          if (tag !== 26) {
            break;
          }

          message.lines!.push(ScriptSourceLine.decode(reader, reader.uint32()));
          continue;
        case 4:
          if (tag !== 32) {
            break;
          }

          message.mark = reader.int32();
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseScriptSourceLine(): ScriptSourceLine {
  return { line: "", lineNumber: 0 };
}

export const ScriptSourceLine = {
  encode(message: ScriptSourceLine, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.line !== undefined && message.line !== "") {
      writer.uint32(10).string(message.line);
    }
    if (message.lineNumber !== undefined && message.lineNumber !== 0) {
      writer.uint32(16).int32(message.lineNumber);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): ScriptSourceLine {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseScriptSourceLine();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.line = reader.string();
          continue;
        case 2:
          if (tag !== 16) {
            break;
          }

          message.lineNumber = reader.int32();
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseFile(): File {
  return {
    id: "",
    size: 0,
    mimetype: "",
    relativePath: "",
    createdTimestamp: 0,
    sha1Checksum: new Uint8Array(0),
    meta: {},
    lastModified: 0,
    projectId: "",
    jobId: "",
    isDirectory: false,
    name: "",
  };
}

export const File = {
  encode(message: File, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.id !== undefined && message.id !== "") {
      writer.uint32(10).string(message.id);
    }
    if (message.size !== undefined && message.size !== 0) {
      writer.uint32(16).int64(message.size);
    }
    if (message.mimetype !== undefined && message.mimetype !== "") {
      writer.uint32(26).string(message.mimetype);
    }
    if (message.relativePath !== undefined && message.relativePath !== "") {
      writer.uint32(34).string(message.relativePath);
    }
    if (message.createdTimestamp !== undefined && message.createdTimestamp !== 0) {
      writer.uint32(40).int64(message.createdTimestamp);
    }
    if (message.sha1Checksum !== undefined && message.sha1Checksum.length !== 0) {
      writer.uint32(50).bytes(message.sha1Checksum);
    }
    Object.entries(message.meta || {}).forEach(([key, value]) => {
      File_MetaEntry.encode({ key: key as any, value }, writer.uint32(58).fork()).ldelim();
    });
    if (message.lastModified !== undefined && message.lastModified !== 0) {
      writer.uint32(72).int64(message.lastModified);
    }
    if (message.projectId !== undefined && message.projectId !== "") {
      writer.uint32(82).string(message.projectId);
    }
    if (message.jobId !== undefined && message.jobId !== "") {
      writer.uint32(90).string(message.jobId);
    }
    if (message.isDirectory === true) {
      writer.uint32(96).bool(message.isDirectory);
    }
    if (message.name !== undefined && message.name !== "") {
      writer.uint32(106).string(message.name);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): File {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseFile();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.id = reader.string();
          continue;
        case 2:
          if (tag !== 16) {
            break;
          }

          message.size = longToNumber(reader.int64() as Long);
          continue;
        case 3:
          if (tag !== 26) {
            break;
          }

          message.mimetype = reader.string();
          continue;
        case 4:
          if (tag !== 34) {
            break;
          }

          message.relativePath = reader.string();
          continue;
        case 5:
          if (tag !== 40) {
            break;
          }

          message.createdTimestamp = longToNumber(reader.int64() as Long);
          continue;
        case 6:
          if (tag !== 50) {
            break;
          }

          message.sha1Checksum = reader.bytes();
          continue;
        case 7:
          if (tag !== 58) {
            break;
          }

          const entry7 = File_MetaEntry.decode(reader, reader.uint32());
          if (entry7.value !== undefined) {
            message.meta![entry7.key] = entry7.value;
          }
          continue;
        case 9:
          if (tag !== 72) {
            break;
          }

          message.lastModified = longToNumber(reader.int64() as Long);
          continue;
        case 10:
          if (tag !== 82) {
            break;
          }

          message.projectId = reader.string();
          continue;
        case 11:
          if (tag !== 90) {
            break;
          }

          message.jobId = reader.string();
          continue;
        case 12:
          if (tag !== 96) {
            break;
          }

          message.isDirectory = reader.bool();
          continue;
        case 13:
          if (tag !== 106) {
            break;
          }

          message.name = reader.string();
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

function createBaseFile_MetaEntry(): File_MetaEntry {
  return { key: "", value: "" };
}

export const File_MetaEntry = {
  encode(message: File_MetaEntry, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.key !== "") {
      writer.uint32(10).string(message.key);
    }
    if (message.value !== "") {
      writer.uint32(18).string(message.value);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): File_MetaEntry {
    const reader = input instanceof _m0.Reader ? input : _m0.Reader.create(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseFile_MetaEntry();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          if (tag !== 10) {
            break;
          }

          message.key = reader.string();
          continue;
        case 2:
          if (tag !== 18) {
            break;
          }

          message.value = reader.string();
          continue;
      }
      if ((tag & 7) === 4 || tag === 0) {
        break;
      }
      reader.skipType(tag & 7);
    }
    return message;
  },
};

declare const self: any | undefined;
declare const window: any | undefined;
declare const global: any | undefined;
const tsProtoGlobalThis: any = (() => {
  if (typeof globalThis !== "undefined") {
    return globalThis;
  }
  if (typeof self !== "undefined") {
    return self;
  }
  if (typeof window !== "undefined") {
    return window;
  }
  if (typeof global !== "undefined") {
    return global;
  }
  throw "Unable to locate global object";
})();

function longToNumber(long: Long): number {
  if (long.gt(Number.MAX_SAFE_INTEGER)) {
    throw new tsProtoGlobalThis.Error("Value is larger than Number.MAX_SAFE_INTEGER");
  }
  return long.toNumber();
}

if (_m0.util.Long !== Long) {
  _m0.util.Long = Long as any;
  _m0.configure();
}
