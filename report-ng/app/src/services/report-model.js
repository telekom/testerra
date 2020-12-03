/*eslint-disable block-scoped-var, id-length, no-control-regex, no-magic-numbers, no-prototype-builtins, no-redeclare, no-shadow, no-var, sort-vars*/
import * as $protobuf from "protobufjs/minimal";

// Common aliases
const $Reader = $protobuf.Reader, $util = $protobuf.util;

// Exported root namespace
const $root = $protobuf.roots["default"] || ($protobuf.roots["default"] = {});

export const data = $root.data = (() => {

    /**
     * Namespace data.
     * @exports data
     * @namespace
     */
    const data = {};

    data.SuiteContext = (function() {

        /**
         * Properties of a SuiteContext.
         * @memberof data
         * @interface ISuiteContext
         * @property {data.IContextValues|null} [contextValues] SuiteContext contextValues
         * @property {string|null} [executionContextId] SuiteContext executionContextId
         */

        /**
         * Constructs a new SuiteContext.
         * @memberof data
         * @classdesc Represents a SuiteContext.
         * @implements ISuiteContext
         * @constructor
         * @param {data.ISuiteContext=} [p] Properties to set
         */
        function SuiteContext(p) {
            if (p)
                for (var ks = Object.keys(p), i = 0; i < ks.length; ++i)
                    if (p[ks[i]] != null)
                        this[ks[i]] = p[ks[i]];
        }

        /**
         * SuiteContext contextValues.
         * @member {data.IContextValues|null|undefined} contextValues
         * @memberof data.SuiteContext
         * @instance
         */
        SuiteContext.prototype.contextValues = null;

        /**
         * SuiteContext executionContextId.
         * @member {string} executionContextId
         * @memberof data.SuiteContext
         * @instance
         */
        SuiteContext.prototype.executionContextId = "";

        /**
         * Decodes a SuiteContext message from the specified reader or buffer.
         * @function decode
         * @memberof data.SuiteContext
         * @static
         * @param {$protobuf.Reader|Uint8Array} r Reader or buffer to decode from
         * @param {number} [l] Message length if known beforehand
         * @returns {data.SuiteContext} SuiteContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        SuiteContext.decode = function decode(r, l) {
            if (!(r instanceof $Reader))
                r = $Reader.create(r);
            var c = l === undefined ? r.len : r.pos + l, m = new $root.data.SuiteContext();
            while (r.pos < c) {
                var t = r.uint32();
                switch (t >>> 3) {
                case 1:
                    m.contextValues = $root.data.ContextValues.decode(r, r.uint32());
                    break;
                case 7:
                    m.executionContextId = r.string();
                    break;
                default:
                    r.skipType(t & 7);
                    break;
                }
            }
            return m;
        };

        return SuiteContext;
    })();

    data.ClassContext = (function() {

        /**
         * Properties of a ClassContext.
         * @memberof data
         * @interface IClassContext
         * @property {data.IContextValues|null} [contextValues] ClassContext contextValues
         * @property {string|null} [fullClassName] ClassContext fullClassName
         * @property {string|null} [testContextId] ClassContext testContextId
         * @property {string|null} [testContextName] ClassContext testContextName
         */

        /**
         * Constructs a new ClassContext.
         * @memberof data
         * @classdesc Represents a ClassContext.
         * @implements IClassContext
         * @constructor
         * @param {data.IClassContext=} [p] Properties to set
         */
        function ClassContext(p) {
            if (p)
                for (var ks = Object.keys(p), i = 0; i < ks.length; ++i)
                    if (p[ks[i]] != null)
                        this[ks[i]] = p[ks[i]];
        }

        /**
         * ClassContext contextValues.
         * @member {data.IContextValues|null|undefined} contextValues
         * @memberof data.ClassContext
         * @instance
         */
        ClassContext.prototype.contextValues = null;

        /**
         * ClassContext fullClassName.
         * @member {string} fullClassName
         * @memberof data.ClassContext
         * @instance
         */
        ClassContext.prototype.fullClassName = "";

        /**
         * ClassContext testContextId.
         * @member {string} testContextId
         * @memberof data.ClassContext
         * @instance
         */
        ClassContext.prototype.testContextId = "";

        /**
         * ClassContext testContextName.
         * @member {string} testContextName
         * @memberof data.ClassContext
         * @instance
         */
        ClassContext.prototype.testContextName = "";

        /**
         * Decodes a ClassContext message from the specified reader or buffer.
         * @function decode
         * @memberof data.ClassContext
         * @static
         * @param {$protobuf.Reader|Uint8Array} r Reader or buffer to decode from
         * @param {number} [l] Message length if known beforehand
         * @returns {data.ClassContext} ClassContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        ClassContext.decode = function decode(r, l) {
            if (!(r instanceof $Reader))
                r = $Reader.create(r);
            var c = l === undefined ? r.len : r.pos + l, m = new $root.data.ClassContext();
            while (r.pos < c) {
                var t = r.uint32();
                switch (t >>> 3) {
                case 1:
                    m.contextValues = $root.data.ContextValues.decode(r, r.uint32());
                    break;
                case 7:
                    m.fullClassName = r.string();
                    break;
                case 9:
                    m.testContextId = r.string();
                    break;
                case 11:
                    m.testContextName = r.string();
                    break;
                default:
                    r.skipType(t & 7);
                    break;
                }
            }
            return m;
        };

        return ClassContext;
    })();

    data.TestContext = (function() {

        /**
         * Properties of a TestContext.
         * @memberof data
         * @interface ITestContext
         * @property {data.IContextValues|null} [contextValues] TestContext contextValues
         * @property {string|null} [suiteContextId] TestContext suiteContextId
         */

        /**
         * Constructs a new TestContext.
         * @memberof data
         * @classdesc Represents a TestContext.
         * @implements ITestContext
         * @constructor
         * @param {data.ITestContext=} [p] Properties to set
         */
        function TestContext(p) {
            if (p)
                for (var ks = Object.keys(p), i = 0; i < ks.length; ++i)
                    if (p[ks[i]] != null)
                        this[ks[i]] = p[ks[i]];
        }

        /**
         * TestContext contextValues.
         * @member {data.IContextValues|null|undefined} contextValues
         * @memberof data.TestContext
         * @instance
         */
        TestContext.prototype.contextValues = null;

        /**
         * TestContext suiteContextId.
         * @member {string} suiteContextId
         * @memberof data.TestContext
         * @instance
         */
        TestContext.prototype.suiteContextId = "";

        /**
         * Decodes a TestContext message from the specified reader or buffer.
         * @function decode
         * @memberof data.TestContext
         * @static
         * @param {$protobuf.Reader|Uint8Array} r Reader or buffer to decode from
         * @param {number} [l] Message length if known beforehand
         * @returns {data.TestContext} TestContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        TestContext.decode = function decode(r, l) {
            if (!(r instanceof $Reader))
                r = $Reader.create(r);
            var c = l === undefined ? r.len : r.pos + l, m = new $root.data.TestContext();
            while (r.pos < c) {
                var t = r.uint32();
                switch (t >>> 3) {
                case 1:
                    m.contextValues = $root.data.ContextValues.decode(r, r.uint32());
                    break;
                case 7:
                    m.suiteContextId = r.string();
                    break;
                default:
                    r.skipType(t & 7);
                    break;
                }
            }
            return m;
        };

        return TestContext;
    })();

    data.ExecutionContext = (function() {

        /**
         * Properties of an ExecutionContext.
         * @memberof data
         * @interface IExecutionContext
         * @property {data.IContextValues|null} [contextValues] ExecutionContext contextValues
         * @property {data.IRunConfig|null} [runConfig] ExecutionContext runConfig
         * @property {string|null} [project_Id] ExecutionContext project_Id
         * @property {string|null} [job_Id] ExecutionContext job_Id
         * @property {string|null} [run_Id] ExecutionContext run_Id
         * @property {string|null} [task_Id] ExecutionContext task_Id
         * @property {Array.<string>|null} [exclusiveSessionContextIds] ExecutionContext exclusiveSessionContextIds
         * @property {Array.<data.ILogMessage>|null} [logMessages] ExecutionContext logMessages
         * @property {number|null} [estimatedTestsCount] ExecutionContext estimatedTestsCount
         * @property {Object.<string,data.IStackTraceCause>|null} [causes] ExecutionContext causes
         */

        /**
         * Constructs a new ExecutionContext.
         * @memberof data
         * @classdesc Represents an ExecutionContext.
         * @implements IExecutionContext
         * @constructor
         * @param {data.IExecutionContext=} [p] Properties to set
         */
        function ExecutionContext(p) {
            this.exclusiveSessionContextIds = [];
            this.logMessages = [];
            this.causes = {};
            if (p)
                for (var ks = Object.keys(p), i = 0; i < ks.length; ++i)
                    if (p[ks[i]] != null)
                        this[ks[i]] = p[ks[i]];
        }

        /**
         * ExecutionContext contextValues.
         * @member {data.IContextValues|null|undefined} contextValues
         * @memberof data.ExecutionContext
         * @instance
         */
        ExecutionContext.prototype.contextValues = null;

        /**
         * ExecutionContext runConfig.
         * @member {data.IRunConfig|null|undefined} runConfig
         * @memberof data.ExecutionContext
         * @instance
         */
        ExecutionContext.prototype.runConfig = null;

        /**
         * ExecutionContext project_Id.
         * @member {string} project_Id
         * @memberof data.ExecutionContext
         * @instance
         */
        ExecutionContext.prototype.project_Id = "";

        /**
         * ExecutionContext job_Id.
         * @member {string} job_Id
         * @memberof data.ExecutionContext
         * @instance
         */
        ExecutionContext.prototype.job_Id = "";

        /**
         * ExecutionContext run_Id.
         * @member {string} run_Id
         * @memberof data.ExecutionContext
         * @instance
         */
        ExecutionContext.prototype.run_Id = "";

        /**
         * ExecutionContext task_Id.
         * @member {string} task_Id
         * @memberof data.ExecutionContext
         * @instance
         */
        ExecutionContext.prototype.task_Id = "";

        /**
         * ExecutionContext exclusiveSessionContextIds.
         * @member {Array.<string>} exclusiveSessionContextIds
         * @memberof data.ExecutionContext
         * @instance
         */
        ExecutionContext.prototype.exclusiveSessionContextIds = $util.emptyArray;

        /**
         * ExecutionContext logMessages.
         * @member {Array.<data.ILogMessage>} logMessages
         * @memberof data.ExecutionContext
         * @instance
         */
        ExecutionContext.prototype.logMessages = $util.emptyArray;

        /**
         * ExecutionContext estimatedTestsCount.
         * @member {number} estimatedTestsCount
         * @memberof data.ExecutionContext
         * @instance
         */
        ExecutionContext.prototype.estimatedTestsCount = 0;

        /**
         * ExecutionContext causes.
         * @member {Object.<string,data.IStackTraceCause>} causes
         * @memberof data.ExecutionContext
         * @instance
         */
        ExecutionContext.prototype.causes = $util.emptyObject;

        /**
         * Decodes an ExecutionContext message from the specified reader or buffer.
         * @function decode
         * @memberof data.ExecutionContext
         * @static
         * @param {$protobuf.Reader|Uint8Array} r Reader or buffer to decode from
         * @param {number} [l] Message length if known beforehand
         * @returns {data.ExecutionContext} ExecutionContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        ExecutionContext.decode = function decode(r, l) {
            if (!(r instanceof $Reader))
                r = $Reader.create(r);
            var c = l === undefined ? r.len : r.pos + l, m = new $root.data.ExecutionContext(), k, value;
            while (r.pos < c) {
                var t = r.uint32();
                switch (t >>> 3) {
                case 1:
                    m.contextValues = $root.data.ContextValues.decode(r, r.uint32());
                    break;
                case 7:
                    m.runConfig = $root.data.RunConfig.decode(r, r.uint32());
                    break;
                case 8:
                    m.project_Id = r.string();
                    break;
                case 9:
                    m.job_Id = r.string();
                    break;
                case 10:
                    m.run_Id = r.string();
                    break;
                case 11:
                    m.task_Id = r.string();
                    break;
                case 12:
                    if (!(m.exclusiveSessionContextIds && m.exclusiveSessionContextIds.length))
                        m.exclusiveSessionContextIds = [];
                    m.exclusiveSessionContextIds.push(r.string());
                    break;
                case 14:
                    if (!(m.logMessages && m.logMessages.length))
                        m.logMessages = [];
                    m.logMessages.push($root.data.LogMessage.decode(r, r.uint32()));
                    break;
                case 15:
                    m.estimatedTestsCount = r.int32();
                    break;
                case 16:
                    if (m.causes === $util.emptyObject)
                        m.causes = {};
                    var c2 = r.uint32() + r.pos;
                    k = "";
                    value = null;
                    while (r.pos < c2) {
                        var tag2 = r.uint32();
                        switch (tag2 >>> 3) {
                        case 1:
                            k = r.string();
                            break;
                        case 2:
                            value = $root.data.StackTraceCause.decode(r, r.uint32());
                            break;
                        default:
                            r.skipType(tag2 & 7);
                            break;
                        }
                    }
                    m.causes[k] = value;
                    break;
                default:
                    r.skipType(t & 7);
                    break;
                }
            }
            return m;
        };

        return ExecutionContext;
    })();

    data.MethodContext = (function() {

        /**
         * Properties of a MethodContext.
         * @memberof data
         * @interface IMethodContext
         * @property {data.IContextValues|null} [contextValues] MethodContext contextValues
         * @property {data.MethodType|null} [methodType] MethodContext methodType
         * @property {Array.<string>|null} [parameters] MethodContext parameters
         * @property {Array.<string>|null} [methodTags] MethodContext methodTags
         * @property {number|null} [retryNumber] MethodContext retryNumber
         * @property {number|null} [methodRunIndex] MethodContext methodRunIndex
         * @property {string|null} [threadName] MethodContext threadName
         * @property {data.FailureCorridorValue|null} [failureCorridorValue] MethodContext failureCorridorValue
         * @property {string|null} [classContextId] MethodContext classContextId
         * @property {Array.<string>|null} [infos] MethodContext infos
         * @property {string|null} [priorityMessage] MethodContext priorityMessage
         * @property {Array.<string>|null} [relatedMethodContextIds] MethodContext relatedMethodContextIds
         * @property {Array.<string>|null} [dependsOnMethodContextIds] MethodContext dependsOnMethodContextIds
         * @property {data.IErrorContext|null} [errorContext] MethodContext errorContext
         * @property {Array.<data.ITestStep>|null} [testSteps] MethodContext testSteps
         * @property {Array.<string>|null} [sessionContextIds] MethodContext sessionContextIds
         * @property {Array.<string>|null} [videoIds] MethodContext videoIds
         * @property {string|null} [customContextJson] MethodContext customContextJson
         */

        /**
         * Constructs a new MethodContext.
         * @memberof data
         * @classdesc Represents a MethodContext.
         * @implements IMethodContext
         * @constructor
         * @param {data.IMethodContext=} [p] Properties to set
         */
        function MethodContext(p) {
            this.parameters = [];
            this.methodTags = [];
            this.infos = [];
            this.relatedMethodContextIds = [];
            this.dependsOnMethodContextIds = [];
            this.testSteps = [];
            this.sessionContextIds = [];
            this.videoIds = [];
            if (p)
                for (var ks = Object.keys(p), i = 0; i < ks.length; ++i)
                    if (p[ks[i]] != null)
                        this[ks[i]] = p[ks[i]];
        }

        /**
         * MethodContext contextValues.
         * @member {data.IContextValues|null|undefined} contextValues
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.contextValues = null;

        /**
         * MethodContext methodType.
         * @member {data.MethodType} methodType
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.methodType = 0;

        /**
         * MethodContext parameters.
         * @member {Array.<string>} parameters
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.parameters = $util.emptyArray;

        /**
         * MethodContext methodTags.
         * @member {Array.<string>} methodTags
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.methodTags = $util.emptyArray;

        /**
         * MethodContext retryNumber.
         * @member {number} retryNumber
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.retryNumber = 0;

        /**
         * MethodContext methodRunIndex.
         * @member {number} methodRunIndex
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.methodRunIndex = 0;

        /**
         * MethodContext threadName.
         * @member {string} threadName
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.threadName = "";

        /**
         * MethodContext failureCorridorValue.
         * @member {data.FailureCorridorValue} failureCorridorValue
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.failureCorridorValue = 0;

        /**
         * MethodContext classContextId.
         * @member {string} classContextId
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.classContextId = "";

        /**
         * MethodContext infos.
         * @member {Array.<string>} infos
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.infos = $util.emptyArray;

        /**
         * MethodContext priorityMessage.
         * @member {string} priorityMessage
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.priorityMessage = "";

        /**
         * MethodContext relatedMethodContextIds.
         * @member {Array.<string>} relatedMethodContextIds
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.relatedMethodContextIds = $util.emptyArray;

        /**
         * MethodContext dependsOnMethodContextIds.
         * @member {Array.<string>} dependsOnMethodContextIds
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.dependsOnMethodContextIds = $util.emptyArray;

        /**
         * MethodContext errorContext.
         * @member {data.IErrorContext|null|undefined} errorContext
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.errorContext = null;

        /**
         * MethodContext testSteps.
         * @member {Array.<data.ITestStep>} testSteps
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.testSteps = $util.emptyArray;

        /**
         * MethodContext sessionContextIds.
         * @member {Array.<string>} sessionContextIds
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.sessionContextIds = $util.emptyArray;

        /**
         * MethodContext videoIds.
         * @member {Array.<string>} videoIds
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.videoIds = $util.emptyArray;

        /**
         * MethodContext customContextJson.
         * @member {string} customContextJson
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.customContextJson = "";

        /**
         * Decodes a MethodContext message from the specified reader or buffer.
         * @function decode
         * @memberof data.MethodContext
         * @static
         * @param {$protobuf.Reader|Uint8Array} r Reader or buffer to decode from
         * @param {number} [l] Message length if known beforehand
         * @returns {data.MethodContext} MethodContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        MethodContext.decode = function decode(r, l) {
            if (!(r instanceof $Reader))
                r = $Reader.create(r);
            var c = l === undefined ? r.len : r.pos + l, m = new $root.data.MethodContext();
            while (r.pos < c) {
                var t = r.uint32();
                switch (t >>> 3) {
                case 1:
                    m.contextValues = $root.data.ContextValues.decode(r, r.uint32());
                    break;
                case 7:
                    m.methodType = r.int32();
                    break;
                case 8:
                    if (!(m.parameters && m.parameters.length))
                        m.parameters = [];
                    m.parameters.push(r.string());
                    break;
                case 9:
                    if (!(m.methodTags && m.methodTags.length))
                        m.methodTags = [];
                    m.methodTags.push(r.string());
                    break;
                case 10:
                    m.retryNumber = r.int32();
                    break;
                case 11:
                    m.methodRunIndex = r.int32();
                    break;
                case 12:
                    m.threadName = r.string();
                    break;
                case 14:
                    m.failureCorridorValue = r.int32();
                    break;
                case 15:
                    m.classContextId = r.string();
                    break;
                case 19:
                    if (!(m.infos && m.infos.length))
                        m.infos = [];
                    m.infos.push(r.string());
                    break;
                case 21:
                    m.priorityMessage = r.string();
                    break;
                case 23:
                    if (!(m.relatedMethodContextIds && m.relatedMethodContextIds.length))
                        m.relatedMethodContextIds = [];
                    m.relatedMethodContextIds.push(r.string());
                    break;
                case 24:
                    if (!(m.dependsOnMethodContextIds && m.dependsOnMethodContextIds.length))
                        m.dependsOnMethodContextIds = [];
                    m.dependsOnMethodContextIds.push(r.string());
                    break;
                case 25:
                    m.errorContext = $root.data.ErrorContext.decode(r, r.uint32());
                    break;
                case 26:
                    if (!(m.testSteps && m.testSteps.length))
                        m.testSteps = [];
                    m.testSteps.push($root.data.TestStep.decode(r, r.uint32()));
                    break;
                case 29:
                    if (!(m.sessionContextIds && m.sessionContextIds.length))
                        m.sessionContextIds = [];
                    m.sessionContextIds.push(r.string());
                    break;
                case 30:
                    if (!(m.videoIds && m.videoIds.length))
                        m.videoIds = [];
                    m.videoIds.push(r.string());
                    break;
                case 32:
                    m.customContextJson = r.string();
                    break;
                default:
                    r.skipType(t & 7);
                    break;
                }
            }
            return m;
        };

        return MethodContext;
    })();

    data.ContextValues = (function() {

        /**
         * Properties of a ContextValues.
         * @memberof data
         * @interface IContextValues
         * @property {string|null} [id] ContextValues id
         * @property {number|null} [created] ContextValues created
         * @property {string|null} [name] ContextValues name
         * @property {number|null} [startTime] ContextValues startTime
         * @property {number|null} [endTime] ContextValues endTime
         * @property {string|null} [swi] ContextValues swi
         * @property {data.ResultStatusType|null} [resultStatus] ContextValues resultStatus
         * @property {data.ExecStatusType|null} [execStatus] ContextValues execStatus
         */

        /**
         * Constructs a new ContextValues.
         * @memberof data
         * @classdesc Represents a ContextValues.
         * @implements IContextValues
         * @constructor
         * @param {data.IContextValues=} [p] Properties to set
         */
        function ContextValues(p) {
            if (p)
                for (var ks = Object.keys(p), i = 0; i < ks.length; ++i)
                    if (p[ks[i]] != null)
                        this[ks[i]] = p[ks[i]];
        }

        /**
         * ContextValues id.
         * @member {string} id
         * @memberof data.ContextValues
         * @instance
         */
        ContextValues.prototype.id = "";

        /**
         * ContextValues created.
         * @member {number} created
         * @memberof data.ContextValues
         * @instance
         */
        ContextValues.prototype.created = $util.Long ? $util.Long.fromBits(0,0,false) : 0;

        /**
         * ContextValues name.
         * @member {string} name
         * @memberof data.ContextValues
         * @instance
         */
        ContextValues.prototype.name = "";

        /**
         * ContextValues startTime.
         * @member {number} startTime
         * @memberof data.ContextValues
         * @instance
         */
        ContextValues.prototype.startTime = $util.Long ? $util.Long.fromBits(0,0,false) : 0;

        /**
         * ContextValues endTime.
         * @member {number} endTime
         * @memberof data.ContextValues
         * @instance
         */
        ContextValues.prototype.endTime = $util.Long ? $util.Long.fromBits(0,0,false) : 0;

        /**
         * ContextValues swi.
         * @member {string} swi
         * @memberof data.ContextValues
         * @instance
         */
        ContextValues.prototype.swi = "";

        /**
         * ContextValues resultStatus.
         * @member {data.ResultStatusType} resultStatus
         * @memberof data.ContextValues
         * @instance
         */
        ContextValues.prototype.resultStatus = 0;

        /**
         * ContextValues execStatus.
         * @member {data.ExecStatusType} execStatus
         * @memberof data.ContextValues
         * @instance
         */
        ContextValues.prototype.execStatus = 0;

        /**
         * Decodes a ContextValues message from the specified reader or buffer.
         * @function decode
         * @memberof data.ContextValues
         * @static
         * @param {$protobuf.Reader|Uint8Array} r Reader or buffer to decode from
         * @param {number} [l] Message length if known beforehand
         * @returns {data.ContextValues} ContextValues
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        ContextValues.decode = function decode(r, l) {
            if (!(r instanceof $Reader))
                r = $Reader.create(r);
            var c = l === undefined ? r.len : r.pos + l, m = new $root.data.ContextValues();
            while (r.pos < c) {
                var t = r.uint32();
                switch (t >>> 3) {
                case 1:
                    m.id = r.string();
                    break;
                case 2:
                    m.created = r.int64();
                    break;
                case 3:
                    m.name = r.string();
                    break;
                case 4:
                    m.startTime = r.int64();
                    break;
                case 5:
                    m.endTime = r.int64();
                    break;
                case 6:
                    m.swi = r.string();
                    break;
                case 7:
                    m.resultStatus = r.int32();
                    break;
                case 8:
                    m.execStatus = r.int32();
                    break;
                default:
                    r.skipType(t & 7);
                    break;
                }
            }
            return m;
        };

        return ContextValues;
    })();

    data.TestStep = (function() {

        /**
         * Properties of a TestStep.
         * @memberof data
         * @interface ITestStep
         * @property {string|null} [name] TestStep name
         * @property {Array.<data.ITestStepAction>|null} [testStepActions] TestStep testStepActions
         */

        /**
         * Constructs a new TestStep.
         * @memberof data
         * @classdesc Represents a TestStep.
         * @implements ITestStep
         * @constructor
         * @param {data.ITestStep=} [p] Properties to set
         */
        function TestStep(p) {
            this.testStepActions = [];
            if (p)
                for (var ks = Object.keys(p), i = 0; i < ks.length; ++i)
                    if (p[ks[i]] != null)
                        this[ks[i]] = p[ks[i]];
        }

        /**
         * TestStep name.
         * @member {string} name
         * @memberof data.TestStep
         * @instance
         */
        TestStep.prototype.name = "";

        /**
         * TestStep testStepActions.
         * @member {Array.<data.ITestStepAction>} testStepActions
         * @memberof data.TestStep
         * @instance
         */
        TestStep.prototype.testStepActions = $util.emptyArray;

        /**
         * Decodes a TestStep message from the specified reader or buffer.
         * @function decode
         * @memberof data.TestStep
         * @static
         * @param {$protobuf.Reader|Uint8Array} r Reader or buffer to decode from
         * @param {number} [l] Message length if known beforehand
         * @returns {data.TestStep} TestStep
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        TestStep.decode = function decode(r, l) {
            if (!(r instanceof $Reader))
                r = $Reader.create(r);
            var c = l === undefined ? r.len : r.pos + l, m = new $root.data.TestStep();
            while (r.pos < c) {
                var t = r.uint32();
                switch (t >>> 3) {
                case 1:
                    m.name = r.string();
                    break;
                case 3:
                    if (!(m.testStepActions && m.testStepActions.length))
                        m.testStepActions = [];
                    m.testStepActions.push($root.data.TestStepAction.decode(r, r.uint32()));
                    break;
                default:
                    r.skipType(t & 7);
                    break;
                }
            }
            return m;
        };

        return TestStep;
    })();

    data.TestStepAction = (function() {

        /**
         * Properties of a TestStepAction.
         * @memberof data
         * @interface ITestStepAction
         * @property {string|null} [name] TestStepAction name
         * @property {number|null} [timestamp] TestStepAction timestamp
         * @property {Array.<data.IClickPathEvent>|null} [clickpathEvents] TestStepAction clickpathEvents
         * @property {Array.<string>|null} [screenshotIds] TestStepAction screenshotIds
         * @property {Array.<data.ILogMessage>|null} [logMessages] TestStepAction logMessages
         * @property {Array.<data.IErrorContext>|null} [optionalAssertions] TestStepAction optionalAssertions
         * @property {Array.<data.IErrorContext>|null} [collectedAssertions] TestStepAction collectedAssertions
         */

        /**
         * Constructs a new TestStepAction.
         * @memberof data
         * @classdesc Represents a TestStepAction.
         * @implements ITestStepAction
         * @constructor
         * @param {data.ITestStepAction=} [p] Properties to set
         */
        function TestStepAction(p) {
            this.clickpathEvents = [];
            this.screenshotIds = [];
            this.logMessages = [];
            this.optionalAssertions = [];
            this.collectedAssertions = [];
            if (p)
                for (var ks = Object.keys(p), i = 0; i < ks.length; ++i)
                    if (p[ks[i]] != null)
                        this[ks[i]] = p[ks[i]];
        }

        /**
         * TestStepAction name.
         * @member {string} name
         * @memberof data.TestStepAction
         * @instance
         */
        TestStepAction.prototype.name = "";

        /**
         * TestStepAction timestamp.
         * @member {number} timestamp
         * @memberof data.TestStepAction
         * @instance
         */
        TestStepAction.prototype.timestamp = $util.Long ? $util.Long.fromBits(0,0,false) : 0;

        /**
         * TestStepAction clickpathEvents.
         * @member {Array.<data.IClickPathEvent>} clickpathEvents
         * @memberof data.TestStepAction
         * @instance
         */
        TestStepAction.prototype.clickpathEvents = $util.emptyArray;

        /**
         * TestStepAction screenshotIds.
         * @member {Array.<string>} screenshotIds
         * @memberof data.TestStepAction
         * @instance
         */
        TestStepAction.prototype.screenshotIds = $util.emptyArray;

        /**
         * TestStepAction logMessages.
         * @member {Array.<data.ILogMessage>} logMessages
         * @memberof data.TestStepAction
         * @instance
         */
        TestStepAction.prototype.logMessages = $util.emptyArray;

        /**
         * TestStepAction optionalAssertions.
         * @member {Array.<data.IErrorContext>} optionalAssertions
         * @memberof data.TestStepAction
         * @instance
         */
        TestStepAction.prototype.optionalAssertions = $util.emptyArray;

        /**
         * TestStepAction collectedAssertions.
         * @member {Array.<data.IErrorContext>} collectedAssertions
         * @memberof data.TestStepAction
         * @instance
         */
        TestStepAction.prototype.collectedAssertions = $util.emptyArray;

        /**
         * Decodes a TestStepAction message from the specified reader or buffer.
         * @function decode
         * @memberof data.TestStepAction
         * @static
         * @param {$protobuf.Reader|Uint8Array} r Reader or buffer to decode from
         * @param {number} [l] Message length if known beforehand
         * @returns {data.TestStepAction} TestStepAction
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        TestStepAction.decode = function decode(r, l) {
            if (!(r instanceof $Reader))
                r = $Reader.create(r);
            var c = l === undefined ? r.len : r.pos + l, m = new $root.data.TestStepAction();
            while (r.pos < c) {
                var t = r.uint32();
                switch (t >>> 3) {
                case 1:
                    m.name = r.string();
                    break;
                case 3:
                    m.timestamp = r.int64();
                    break;
                case 5:
                    if (!(m.clickpathEvents && m.clickpathEvents.length))
                        m.clickpathEvents = [];
                    m.clickpathEvents.push($root.data.ClickPathEvent.decode(r, r.uint32()));
                    break;
                case 6:
                    if (!(m.screenshotIds && m.screenshotIds.length))
                        m.screenshotIds = [];
                    m.screenshotIds.push(r.string());
                    break;
                case 7:
                    if (!(m.logMessages && m.logMessages.length))
                        m.logMessages = [];
                    m.logMessages.push($root.data.LogMessage.decode(r, r.uint32()));
                    break;
                case 8:
                    if (!(m.optionalAssertions && m.optionalAssertions.length))
                        m.optionalAssertions = [];
                    m.optionalAssertions.push($root.data.ErrorContext.decode(r, r.uint32()));
                    break;
                case 9:
                    if (!(m.collectedAssertions && m.collectedAssertions.length))
                        m.collectedAssertions = [];
                    m.collectedAssertions.push($root.data.ErrorContext.decode(r, r.uint32()));
                    break;
                default:
                    r.skipType(t & 7);
                    break;
                }
            }
            return m;
        };

        return TestStepAction;
    })();

    /**
     * ClickPathEventType enum.
     * @name data.ClickPathEventType
     * @enum {number}
     * @property {number} CPET_NOT_SET=0 CPET_NOT_SET value
     * @property {number} CPET_WINDOW=1 CPET_WINDOW value
     * @property {number} CPET_CLICK=2 CPET_CLICK value
     * @property {number} CPET_VALUE=3 CPET_VALUE value
     * @property {number} CPET_PAGE=4 CPET_PAGE value
     * @property {number} CPET_URL=5 CPET_URL value
     */
    data.ClickPathEventType = (function() {
        const valuesById = {}, values = Object.create(valuesById);
        values[valuesById[0] = "CPET_NOT_SET"] = 0;
        values[valuesById[1] = "CPET_WINDOW"] = 1;
        values[valuesById[2] = "CPET_CLICK"] = 2;
        values[valuesById[3] = "CPET_VALUE"] = 3;
        values[valuesById[4] = "CPET_PAGE"] = 4;
        values[valuesById[5] = "CPET_URL"] = 5;
        return values;
    })();

    data.ClickPathEvent = (function() {

        /**
         * Properties of a ClickPathEvent.
         * @memberof data
         * @interface IClickPathEvent
         * @property {data.ClickPathEventType|null} [type] ClickPathEvent type
         * @property {string|null} [subject] ClickPathEvent subject
         * @property {string|null} [sessionId] ClickPathEvent sessionId
         */

        /**
         * Constructs a new ClickPathEvent.
         * @memberof data
         * @classdesc Represents a ClickPathEvent.
         * @implements IClickPathEvent
         * @constructor
         * @param {data.IClickPathEvent=} [p] Properties to set
         */
        function ClickPathEvent(p) {
            if (p)
                for (var ks = Object.keys(p), i = 0; i < ks.length; ++i)
                    if (p[ks[i]] != null)
                        this[ks[i]] = p[ks[i]];
        }

        /**
         * ClickPathEvent type.
         * @member {data.ClickPathEventType} type
         * @memberof data.ClickPathEvent
         * @instance
         */
        ClickPathEvent.prototype.type = 0;

        /**
         * ClickPathEvent subject.
         * @member {string} subject
         * @memberof data.ClickPathEvent
         * @instance
         */
        ClickPathEvent.prototype.subject = "";

        /**
         * ClickPathEvent sessionId.
         * @member {string} sessionId
         * @memberof data.ClickPathEvent
         * @instance
         */
        ClickPathEvent.prototype.sessionId = "";

        /**
         * Decodes a ClickPathEvent message from the specified reader or buffer.
         * @function decode
         * @memberof data.ClickPathEvent
         * @static
         * @param {$protobuf.Reader|Uint8Array} r Reader or buffer to decode from
         * @param {number} [l] Message length if known beforehand
         * @returns {data.ClickPathEvent} ClickPathEvent
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        ClickPathEvent.decode = function decode(r, l) {
            if (!(r instanceof $Reader))
                r = $Reader.create(r);
            var c = l === undefined ? r.len : r.pos + l, m = new $root.data.ClickPathEvent();
            while (r.pos < c) {
                var t = r.uint32();
                switch (t >>> 3) {
                case 1:
                    m.type = r.int32();
                    break;
                case 2:
                    m.subject = r.string();
                    break;
                case 3:
                    m.sessionId = r.string();
                    break;
                default:
                    r.skipType(t & 7);
                    break;
                }
            }
            return m;
        };

        return ClickPathEvent;
    })();

    /**
     * LogMessageType enum.
     * @name data.LogMessageType
     * @enum {number}
     * @property {number} LMT_OFF=0 LMT_OFF value
     * @property {number} LMT_ERROR=1 LMT_ERROR value
     * @property {number} LMT_WARN=2 LMT_WARN value
     * @property {number} LMT_INFO=3 LMT_INFO value
     * @property {number} LMT_DEBUG=4 LMT_DEBUG value
     */
    data.LogMessageType = (function() {
        const valuesById = {}, values = Object.create(valuesById);
        values[valuesById[0] = "LMT_OFF"] = 0;
        values[valuesById[1] = "LMT_ERROR"] = 1;
        values[valuesById[2] = "LMT_WARN"] = 2;
        values[valuesById[3] = "LMT_INFO"] = 3;
        values[valuesById[4] = "LMT_DEBUG"] = 4;
        return values;
    })();

    data.LogMessage = (function() {

        /**
         * Properties of a LogMessage.
         * @memberof data
         * @interface ILogMessage
         * @property {data.LogMessageType|null} [type] LogMessage type
         * @property {string|null} [loggerName] LogMessage loggerName
         * @property {string|null} [message] LogMessage message
         * @property {number|null} [timestamp] LogMessage timestamp
         * @property {string|null} [threadName] LogMessage threadName
         * @property {string|null} [causeId] LogMessage causeId
         */

        /**
         * Constructs a new LogMessage.
         * @memberof data
         * @classdesc Represents a LogMessage.
         * @implements ILogMessage
         * @constructor
         * @param {data.ILogMessage=} [p] Properties to set
         */
        function LogMessage(p) {
            if (p)
                for (var ks = Object.keys(p), i = 0; i < ks.length; ++i)
                    if (p[ks[i]] != null)
                        this[ks[i]] = p[ks[i]];
        }

        /**
         * LogMessage type.
         * @member {data.LogMessageType} type
         * @memberof data.LogMessage
         * @instance
         */
        LogMessage.prototype.type = 0;

        /**
         * LogMessage loggerName.
         * @member {string} loggerName
         * @memberof data.LogMessage
         * @instance
         */
        LogMessage.prototype.loggerName = "";

        /**
         * LogMessage message.
         * @member {string} message
         * @memberof data.LogMessage
         * @instance
         */
        LogMessage.prototype.message = "";

        /**
         * LogMessage timestamp.
         * @member {number} timestamp
         * @memberof data.LogMessage
         * @instance
         */
        LogMessage.prototype.timestamp = $util.Long ? $util.Long.fromBits(0,0,false) : 0;

        /**
         * LogMessage threadName.
         * @member {string} threadName
         * @memberof data.LogMessage
         * @instance
         */
        LogMessage.prototype.threadName = "";

        /**
         * LogMessage causeId.
         * @member {string} causeId
         * @memberof data.LogMessage
         * @instance
         */
        LogMessage.prototype.causeId = "";

        /**
         * Decodes a LogMessage message from the specified reader or buffer.
         * @function decode
         * @memberof data.LogMessage
         * @static
         * @param {$protobuf.Reader|Uint8Array} r Reader or buffer to decode from
         * @param {number} [l] Message length if known beforehand
         * @returns {data.LogMessage} LogMessage
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        LogMessage.decode = function decode(r, l) {
            if (!(r instanceof $Reader))
                r = $Reader.create(r);
            var c = l === undefined ? r.len : r.pos + l, m = new $root.data.LogMessage();
            while (r.pos < c) {
                var t = r.uint32();
                switch (t >>> 3) {
                case 1:
                    m.type = r.int32();
                    break;
                case 2:
                    m.loggerName = r.string();
                    break;
                case 3:
                    m.message = r.string();
                    break;
                case 4:
                    m.timestamp = r.int64();
                    break;
                case 5:
                    m.threadName = r.string();
                    break;
                case 6:
                    m.causeId = r.string();
                    break;
                default:
                    r.skipType(t & 7);
                    break;
                }
            }
            return m;
        };

        return LogMessage;
    })();

    data.ErrorContext = (function() {

        /**
         * Properties of an ErrorContext.
         * @memberof data
         * @interface IErrorContext
         * @property {data.IScriptSource|null} [scriptSource] ErrorContext scriptSource
         * @property {data.IScriptSource|null} [executionObjectSource] ErrorContext executionObjectSource
         * @property {string|null} [ticketId] ErrorContext ticketId
         * @property {string|null} [description] ErrorContext description
         * @property {string|null} [causeId] ErrorContext causeId
         */

        /**
         * Constructs a new ErrorContext.
         * @memberof data
         * @classdesc Represents an ErrorContext.
         * @implements IErrorContext
         * @constructor
         * @param {data.IErrorContext=} [p] Properties to set
         */
        function ErrorContext(p) {
            if (p)
                for (var ks = Object.keys(p), i = 0; i < ks.length; ++i)
                    if (p[ks[i]] != null)
                        this[ks[i]] = p[ks[i]];
        }

        /**
         * ErrorContext scriptSource.
         * @member {data.IScriptSource|null|undefined} scriptSource
         * @memberof data.ErrorContext
         * @instance
         */
        ErrorContext.prototype.scriptSource = null;

        /**
         * ErrorContext executionObjectSource.
         * @member {data.IScriptSource|null|undefined} executionObjectSource
         * @memberof data.ErrorContext
         * @instance
         */
        ErrorContext.prototype.executionObjectSource = null;

        /**
         * ErrorContext ticketId.
         * @member {string} ticketId
         * @memberof data.ErrorContext
         * @instance
         */
        ErrorContext.prototype.ticketId = "";

        /**
         * ErrorContext description.
         * @member {string} description
         * @memberof data.ErrorContext
         * @instance
         */
        ErrorContext.prototype.description = "";

        /**
         * ErrorContext causeId.
         * @member {string} causeId
         * @memberof data.ErrorContext
         * @instance
         */
        ErrorContext.prototype.causeId = "";

        /**
         * Decodes an ErrorContext message from the specified reader or buffer.
         * @function decode
         * @memberof data.ErrorContext
         * @static
         * @param {$protobuf.Reader|Uint8Array} r Reader or buffer to decode from
         * @param {number} [l] Message length if known beforehand
         * @returns {data.ErrorContext} ErrorContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        ErrorContext.decode = function decode(r, l) {
            if (!(r instanceof $Reader))
                r = $Reader.create(r);
            var c = l === undefined ? r.len : r.pos + l, m = new $root.data.ErrorContext();
            while (r.pos < c) {
                var t = r.uint32();
                switch (t >>> 3) {
                case 7:
                    m.scriptSource = $root.data.ScriptSource.decode(r, r.uint32());
                    break;
                case 8:
                    m.executionObjectSource = $root.data.ScriptSource.decode(r, r.uint32());
                    break;
                case 9:
                    m.ticketId = r.string();
                    break;
                case 10:
                    m.description = r.string();
                    break;
                case 11:
                    m.causeId = r.string();
                    break;
                default:
                    r.skipType(t & 7);
                    break;
                }
            }
            return m;
        };

        return ErrorContext;
    })();

    data.SessionContext = (function() {

        /**
         * Properties of a SessionContext.
         * @memberof data
         * @interface ISessionContext
         * @property {data.IContextValues|null} [contextValues] SessionContext contextValues
         * @property {string|null} [sessionKey] SessionContext sessionKey
         * @property {string|null} [provider] SessionContext provider
         * @property {Object.<string,string>|null} [metadata] SessionContext metadata
         * @property {string|null} [sessionId] SessionContext sessionId
         */

        /**
         * Constructs a new SessionContext.
         * @memberof data
         * @classdesc Represents a SessionContext.
         * @implements ISessionContext
         * @constructor
         * @param {data.ISessionContext=} [p] Properties to set
         */
        function SessionContext(p) {
            this.metadata = {};
            if (p)
                for (var ks = Object.keys(p), i = 0; i < ks.length; ++i)
                    if (p[ks[i]] != null)
                        this[ks[i]] = p[ks[i]];
        }

        /**
         * SessionContext contextValues.
         * @member {data.IContextValues|null|undefined} contextValues
         * @memberof data.SessionContext
         * @instance
         */
        SessionContext.prototype.contextValues = null;

        /**
         * SessionContext sessionKey.
         * @member {string} sessionKey
         * @memberof data.SessionContext
         * @instance
         */
        SessionContext.prototype.sessionKey = "";

        /**
         * SessionContext provider.
         * @member {string} provider
         * @memberof data.SessionContext
         * @instance
         */
        SessionContext.prototype.provider = "";

        /**
         * SessionContext metadata.
         * @member {Object.<string,string>} metadata
         * @memberof data.SessionContext
         * @instance
         */
        SessionContext.prototype.metadata = $util.emptyObject;

        /**
         * SessionContext sessionId.
         * @member {string} sessionId
         * @memberof data.SessionContext
         * @instance
         */
        SessionContext.prototype.sessionId = "";

        /**
         * Decodes a SessionContext message from the specified reader or buffer.
         * @function decode
         * @memberof data.SessionContext
         * @static
         * @param {$protobuf.Reader|Uint8Array} r Reader or buffer to decode from
         * @param {number} [l] Message length if known beforehand
         * @returns {data.SessionContext} SessionContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        SessionContext.decode = function decode(r, l) {
            if (!(r instanceof $Reader))
                r = $Reader.create(r);
            var c = l === undefined ? r.len : r.pos + l, m = new $root.data.SessionContext(), k, value;
            while (r.pos < c) {
                var t = r.uint32();
                switch (t >>> 3) {
                case 1:
                    m.contextValues = $root.data.ContextValues.decode(r, r.uint32());
                    break;
                case 2:
                    m.sessionKey = r.string();
                    break;
                case 3:
                    m.provider = r.string();
                    break;
                case 4:
                    if (m.metadata === $util.emptyObject)
                        m.metadata = {};
                    var c2 = r.uint32() + r.pos;
                    k = "";
                    value = "";
                    while (r.pos < c2) {
                        var tag2 = r.uint32();
                        switch (tag2 >>> 3) {
                        case 1:
                            k = r.string();
                            break;
                        case 2:
                            value = r.string();
                            break;
                        default:
                            r.skipType(tag2 & 7);
                            break;
                        }
                    }
                    m.metadata[k] = value;
                    break;
                case 6:
                    m.sessionId = r.string();
                    break;
                default:
                    r.skipType(t & 7);
                    break;
                }
            }
            return m;
        };

        return SessionContext;
    })();

    data.RunConfig = (function() {

        /**
         * Properties of a RunConfig.
         * @memberof data
         * @interface IRunConfig
         * @property {string|null} [runcfg] RunConfig runcfg
         * @property {data.IBuildInformation|null} [buildInformation] RunConfig buildInformation
         * @property {string|null} [reportName] RunConfig reportName
         */

        /**
         * Constructs a new RunConfig.
         * @memberof data
         * @classdesc Represents a RunConfig.
         * @implements IRunConfig
         * @constructor
         * @param {data.IRunConfig=} [p] Properties to set
         */
        function RunConfig(p) {
            if (p)
                for (var ks = Object.keys(p), i = 0; i < ks.length; ++i)
                    if (p[ks[i]] != null)
                        this[ks[i]] = p[ks[i]];
        }

        /**
         * RunConfig runcfg.
         * @member {string} runcfg
         * @memberof data.RunConfig
         * @instance
         */
        RunConfig.prototype.runcfg = "";

        /**
         * RunConfig buildInformation.
         * @member {data.IBuildInformation|null|undefined} buildInformation
         * @memberof data.RunConfig
         * @instance
         */
        RunConfig.prototype.buildInformation = null;

        /**
         * RunConfig reportName.
         * @member {string} reportName
         * @memberof data.RunConfig
         * @instance
         */
        RunConfig.prototype.reportName = "";

        /**
         * Decodes a RunConfig message from the specified reader or buffer.
         * @function decode
         * @memberof data.RunConfig
         * @static
         * @param {$protobuf.Reader|Uint8Array} r Reader or buffer to decode from
         * @param {number} [l] Message length if known beforehand
         * @returns {data.RunConfig} RunConfig
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        RunConfig.decode = function decode(r, l) {
            if (!(r instanceof $Reader))
                r = $Reader.create(r);
            var c = l === undefined ? r.len : r.pos + l, m = new $root.data.RunConfig();
            while (r.pos < c) {
                var t = r.uint32();
                switch (t >>> 3) {
                case 1:
                    m.runcfg = r.string();
                    break;
                case 2:
                    m.buildInformation = $root.data.BuildInformation.decode(r, r.uint32());
                    break;
                case 3:
                    m.reportName = r.string();
                    break;
                default:
                    r.skipType(t & 7);
                    break;
                }
            }
            return m;
        };

        return RunConfig;
    })();

    data.BuildInformation = (function() {

        /**
         * Properties of a BuildInformation.
         * @memberof data
         * @interface IBuildInformation
         * @property {string|null} [buildJavaVersion] BuildInformation buildJavaVersion
         * @property {string|null} [buildOsName] BuildInformation buildOsName
         * @property {string|null} [buildOsVersion] BuildInformation buildOsVersion
         * @property {string|null} [buildUserName] BuildInformation buildUserName
         * @property {string|null} [buildVersion] BuildInformation buildVersion
         * @property {string|null} [buildTimestamp] BuildInformation buildTimestamp
         */

        /**
         * Constructs a new BuildInformation.
         * @memberof data
         * @classdesc Represents a BuildInformation.
         * @implements IBuildInformation
         * @constructor
         * @param {data.IBuildInformation=} [p] Properties to set
         */
        function BuildInformation(p) {
            if (p)
                for (var ks = Object.keys(p), i = 0; i < ks.length; ++i)
                    if (p[ks[i]] != null)
                        this[ks[i]] = p[ks[i]];
        }

        /**
         * BuildInformation buildJavaVersion.
         * @member {string} buildJavaVersion
         * @memberof data.BuildInformation
         * @instance
         */
        BuildInformation.prototype.buildJavaVersion = "";

        /**
         * BuildInformation buildOsName.
         * @member {string} buildOsName
         * @memberof data.BuildInformation
         * @instance
         */
        BuildInformation.prototype.buildOsName = "";

        /**
         * BuildInformation buildOsVersion.
         * @member {string} buildOsVersion
         * @memberof data.BuildInformation
         * @instance
         */
        BuildInformation.prototype.buildOsVersion = "";

        /**
         * BuildInformation buildUserName.
         * @member {string} buildUserName
         * @memberof data.BuildInformation
         * @instance
         */
        BuildInformation.prototype.buildUserName = "";

        /**
         * BuildInformation buildVersion.
         * @member {string} buildVersion
         * @memberof data.BuildInformation
         * @instance
         */
        BuildInformation.prototype.buildVersion = "";

        /**
         * BuildInformation buildTimestamp.
         * @member {string} buildTimestamp
         * @memberof data.BuildInformation
         * @instance
         */
        BuildInformation.prototype.buildTimestamp = "";

        /**
         * Decodes a BuildInformation message from the specified reader or buffer.
         * @function decode
         * @memberof data.BuildInformation
         * @static
         * @param {$protobuf.Reader|Uint8Array} r Reader or buffer to decode from
         * @param {number} [l] Message length if known beforehand
         * @returns {data.BuildInformation} BuildInformation
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        BuildInformation.decode = function decode(r, l) {
            if (!(r instanceof $Reader))
                r = $Reader.create(r);
            var c = l === undefined ? r.len : r.pos + l, m = new $root.data.BuildInformation();
            while (r.pos < c) {
                var t = r.uint32();
                switch (t >>> 3) {
                case 1:
                    m.buildJavaVersion = r.string();
                    break;
                case 2:
                    m.buildOsName = r.string();
                    break;
                case 3:
                    m.buildOsVersion = r.string();
                    break;
                case 4:
                    m.buildUserName = r.string();
                    break;
                case 5:
                    m.buildVersion = r.string();
                    break;
                case 6:
                    m.buildTimestamp = r.string();
                    break;
                default:
                    r.skipType(t & 7);
                    break;
                }
            }
            return m;
        };

        return BuildInformation;
    })();

    /**
     * FailureCorridorValue enum.
     * @name data.FailureCorridorValue
     * @enum {number}
     * @property {number} FCV_NOT_SET=0 FCV_NOT_SET value
     * @property {number} FCV_HIGH=1 FCV_HIGH value
     * @property {number} FCV_MID=2 FCV_MID value
     * @property {number} FCV_LOW=3 FCV_LOW value
     */
    data.FailureCorridorValue = (function() {
        const valuesById = {}, values = Object.create(valuesById);
        values[valuesById[0] = "FCV_NOT_SET"] = 0;
        values[valuesById[1] = "FCV_HIGH"] = 1;
        values[valuesById[2] = "FCV_MID"] = 2;
        values[valuesById[3] = "FCV_LOW"] = 3;
        return values;
    })();

    /**
     * MethodType enum.
     * @name data.MethodType
     * @enum {number}
     * @property {number} MT_NOT_SET=0 MT_NOT_SET value
     * @property {number} TEST_METHOD=1 TEST_METHOD value
     * @property {number} CONFIGURATION_METHOD=2 CONFIGURATION_METHOD value
     */
    data.MethodType = (function() {
        const valuesById = {}, values = Object.create(valuesById);
        values[valuesById[0] = "MT_NOT_SET"] = 0;
        values[valuesById[1] = "TEST_METHOD"] = 1;
        values[valuesById[2] = "CONFIGURATION_METHOD"] = 2;
        return values;
    })();

    data.StackTraceCause = (function() {

        /**
         * Properties of a StackTraceCause.
         * @memberof data
         * @interface IStackTraceCause
         * @property {string|null} [className] StackTraceCause className
         * @property {string|null} [message] StackTraceCause message
         * @property {Array.<string>|null} [stackTraceElements] StackTraceCause stackTraceElements
         * @property {string|null} [id] StackTraceCause id
         * @property {string|null} [causeId] StackTraceCause causeId
         */

        /**
         * Constructs a new StackTraceCause.
         * @memberof data
         * @classdesc Represents a StackTraceCause.
         * @implements IStackTraceCause
         * @constructor
         * @param {data.IStackTraceCause=} [p] Properties to set
         */
        function StackTraceCause(p) {
            this.stackTraceElements = [];
            if (p)
                for (var ks = Object.keys(p), i = 0; i < ks.length; ++i)
                    if (p[ks[i]] != null)
                        this[ks[i]] = p[ks[i]];
        }

        /**
         * StackTraceCause className.
         * @member {string} className
         * @memberof data.StackTraceCause
         * @instance
         */
        StackTraceCause.prototype.className = "";

        /**
         * StackTraceCause message.
         * @member {string} message
         * @memberof data.StackTraceCause
         * @instance
         */
        StackTraceCause.prototype.message = "";

        /**
         * StackTraceCause stackTraceElements.
         * @member {Array.<string>} stackTraceElements
         * @memberof data.StackTraceCause
         * @instance
         */
        StackTraceCause.prototype.stackTraceElements = $util.emptyArray;

        /**
         * StackTraceCause id.
         * @member {string} id
         * @memberof data.StackTraceCause
         * @instance
         */
        StackTraceCause.prototype.id = "";

        /**
         * StackTraceCause causeId.
         * @member {string} causeId
         * @memberof data.StackTraceCause
         * @instance
         */
        StackTraceCause.prototype.causeId = "";

        /**
         * Decodes a StackTraceCause message from the specified reader or buffer.
         * @function decode
         * @memberof data.StackTraceCause
         * @static
         * @param {$protobuf.Reader|Uint8Array} r Reader or buffer to decode from
         * @param {number} [l] Message length if known beforehand
         * @returns {data.StackTraceCause} StackTraceCause
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        StackTraceCause.decode = function decode(r, l) {
            if (!(r instanceof $Reader))
                r = $Reader.create(r);
            var c = l === undefined ? r.len : r.pos + l, m = new $root.data.StackTraceCause();
            while (r.pos < c) {
                var t = r.uint32();
                switch (t >>> 3) {
                case 1:
                    m.className = r.string();
                    break;
                case 2:
                    m.message = r.string();
                    break;
                case 3:
                    if (!(m.stackTraceElements && m.stackTraceElements.length))
                        m.stackTraceElements = [];
                    m.stackTraceElements.push(r.string());
                    break;
                case 5:
                    m.id = r.string();
                    break;
                case 6:
                    m.causeId = r.string();
                    break;
                default:
                    r.skipType(t & 7);
                    break;
                }
            }
            return m;
        };

        return StackTraceCause;
    })();

    data.ScriptSource = (function() {

        /**
         * Properties of a ScriptSource.
         * @memberof data
         * @interface IScriptSource
         * @property {string|null} [fileName] ScriptSource fileName
         * @property {string|null} [methodName] ScriptSource methodName
         * @property {Array.<data.IScriptSourceLine>|null} [lines] ScriptSource lines
         */

        /**
         * Constructs a new ScriptSource.
         * @memberof data
         * @classdesc Represents a ScriptSource.
         * @implements IScriptSource
         * @constructor
         * @param {data.IScriptSource=} [p] Properties to set
         */
        function ScriptSource(p) {
            this.lines = [];
            if (p)
                for (var ks = Object.keys(p), i = 0; i < ks.length; ++i)
                    if (p[ks[i]] != null)
                        this[ks[i]] = p[ks[i]];
        }

        /**
         * ScriptSource fileName.
         * @member {string} fileName
         * @memberof data.ScriptSource
         * @instance
         */
        ScriptSource.prototype.fileName = "";

        /**
         * ScriptSource methodName.
         * @member {string} methodName
         * @memberof data.ScriptSource
         * @instance
         */
        ScriptSource.prototype.methodName = "";

        /**
         * ScriptSource lines.
         * @member {Array.<data.IScriptSourceLine>} lines
         * @memberof data.ScriptSource
         * @instance
         */
        ScriptSource.prototype.lines = $util.emptyArray;

        /**
         * Decodes a ScriptSource message from the specified reader or buffer.
         * @function decode
         * @memberof data.ScriptSource
         * @static
         * @param {$protobuf.Reader|Uint8Array} r Reader or buffer to decode from
         * @param {number} [l] Message length if known beforehand
         * @returns {data.ScriptSource} ScriptSource
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        ScriptSource.decode = function decode(r, l) {
            if (!(r instanceof $Reader))
                r = $Reader.create(r);
            var c = l === undefined ? r.len : r.pos + l, m = new $root.data.ScriptSource();
            while (r.pos < c) {
                var t = r.uint32();
                switch (t >>> 3) {
                case 1:
                    m.fileName = r.string();
                    break;
                case 2:
                    m.methodName = r.string();
                    break;
                case 3:
                    if (!(m.lines && m.lines.length))
                        m.lines = [];
                    m.lines.push($root.data.ScriptSourceLine.decode(r, r.uint32()));
                    break;
                default:
                    r.skipType(t & 7);
                    break;
                }
            }
            return m;
        };

        return ScriptSource;
    })();

    data.ScriptSourceLine = (function() {

        /**
         * Properties of a ScriptSourceLine.
         * @memberof data
         * @interface IScriptSourceLine
         * @property {string|null} [line] ScriptSourceLine line
         * @property {number|null} [lineNumber] ScriptSourceLine lineNumber
         * @property {boolean|null} [mark] ScriptSourceLine mark
         */

        /**
         * Constructs a new ScriptSourceLine.
         * @memberof data
         * @classdesc Represents a ScriptSourceLine.
         * @implements IScriptSourceLine
         * @constructor
         * @param {data.IScriptSourceLine=} [p] Properties to set
         */
        function ScriptSourceLine(p) {
            if (p)
                for (var ks = Object.keys(p), i = 0; i < ks.length; ++i)
                    if (p[ks[i]] != null)
                        this[ks[i]] = p[ks[i]];
        }

        /**
         * ScriptSourceLine line.
         * @member {string} line
         * @memberof data.ScriptSourceLine
         * @instance
         */
        ScriptSourceLine.prototype.line = "";

        /**
         * ScriptSourceLine lineNumber.
         * @member {number} lineNumber
         * @memberof data.ScriptSourceLine
         * @instance
         */
        ScriptSourceLine.prototype.lineNumber = 0;

        /**
         * ScriptSourceLine mark.
         * @member {boolean} mark
         * @memberof data.ScriptSourceLine
         * @instance
         */
        ScriptSourceLine.prototype.mark = false;

        /**
         * Decodes a ScriptSourceLine message from the specified reader or buffer.
         * @function decode
         * @memberof data.ScriptSourceLine
         * @static
         * @param {$protobuf.Reader|Uint8Array} r Reader or buffer to decode from
         * @param {number} [l] Message length if known beforehand
         * @returns {data.ScriptSourceLine} ScriptSourceLine
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        ScriptSourceLine.decode = function decode(r, l) {
            if (!(r instanceof $Reader))
                r = $Reader.create(r);
            var c = l === undefined ? r.len : r.pos + l, m = new $root.data.ScriptSourceLine();
            while (r.pos < c) {
                var t = r.uint32();
                switch (t >>> 3) {
                case 1:
                    m.line = r.string();
                    break;
                case 2:
                    m.lineNumber = r.int32();
                    break;
                case 3:
                    m.mark = r.bool();
                    break;
                default:
                    r.skipType(t & 7);
                    break;
                }
            }
            return m;
        };

        return ScriptSourceLine;
    })();

    data.File = (function() {

        /**
         * Properties of a File.
         * @memberof data
         * @interface IFile
         * @property {string|null} [id] File id
         * @property {number|null} [size] File size
         * @property {string|null} [mimetype] File mimetype
         * @property {string|null} [relativePath] File relativePath
         * @property {number|null} [createdTimestamp] File createdTimestamp
         * @property {Uint8Array|null} [sha1Checksum] File sha1Checksum
         * @property {Object.<string,string>|null} [meta] File meta
         * @property {number|null} [lastModified] File lastModified
         * @property {string|null} [projectId] File projectId
         * @property {string|null} [jobId] File jobId
         * @property {boolean|null} [isDirectory] File isDirectory
         * @property {string|null} [name] File name
         */

        /**
         * Constructs a new File.
         * @memberof data
         * @classdesc Represents a File.
         * @implements IFile
         * @constructor
         * @param {data.IFile=} [p] Properties to set
         */
        function File(p) {
            this.meta = {};
            if (p)
                for (var ks = Object.keys(p), i = 0; i < ks.length; ++i)
                    if (p[ks[i]] != null)
                        this[ks[i]] = p[ks[i]];
        }

        /**
         * File id.
         * @member {string} id
         * @memberof data.File
         * @instance
         */
        File.prototype.id = "";

        /**
         * File size.
         * @member {number} size
         * @memberof data.File
         * @instance
         */
        File.prototype.size = $util.Long ? $util.Long.fromBits(0,0,false) : 0;

        /**
         * File mimetype.
         * @member {string} mimetype
         * @memberof data.File
         * @instance
         */
        File.prototype.mimetype = "";

        /**
         * File relativePath.
         * @member {string} relativePath
         * @memberof data.File
         * @instance
         */
        File.prototype.relativePath = "";

        /**
         * File createdTimestamp.
         * @member {number} createdTimestamp
         * @memberof data.File
         * @instance
         */
        File.prototype.createdTimestamp = $util.Long ? $util.Long.fromBits(0,0,false) : 0;

        /**
         * File sha1Checksum.
         * @member {Uint8Array} sha1Checksum
         * @memberof data.File
         * @instance
         */
        File.prototype.sha1Checksum = $util.newBuffer([]);

        /**
         * File meta.
         * @member {Object.<string,string>} meta
         * @memberof data.File
         * @instance
         */
        File.prototype.meta = $util.emptyObject;

        /**
         * File lastModified.
         * @member {number} lastModified
         * @memberof data.File
         * @instance
         */
        File.prototype.lastModified = $util.Long ? $util.Long.fromBits(0,0,false) : 0;

        /**
         * File projectId.
         * @member {string} projectId
         * @memberof data.File
         * @instance
         */
        File.prototype.projectId = "";

        /**
         * File jobId.
         * @member {string} jobId
         * @memberof data.File
         * @instance
         */
        File.prototype.jobId = "";

        /**
         * File isDirectory.
         * @member {boolean} isDirectory
         * @memberof data.File
         * @instance
         */
        File.prototype.isDirectory = false;

        /**
         * File name.
         * @member {string} name
         * @memberof data.File
         * @instance
         */
        File.prototype.name = "";

        /**
         * Decodes a File message from the specified reader or buffer.
         * @function decode
         * @memberof data.File
         * @static
         * @param {$protobuf.Reader|Uint8Array} r Reader or buffer to decode from
         * @param {number} [l] Message length if known beforehand
         * @returns {data.File} File
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        File.decode = function decode(r, l) {
            if (!(r instanceof $Reader))
                r = $Reader.create(r);
            var c = l === undefined ? r.len : r.pos + l, m = new $root.data.File(), k, value;
            while (r.pos < c) {
                var t = r.uint32();
                switch (t >>> 3) {
                case 1:
                    m.id = r.string();
                    break;
                case 2:
                    m.size = r.int64();
                    break;
                case 3:
                    m.mimetype = r.string();
                    break;
                case 4:
                    m.relativePath = r.string();
                    break;
                case 5:
                    m.createdTimestamp = r.int64();
                    break;
                case 6:
                    m.sha1Checksum = r.bytes();
                    break;
                case 7:
                    if (m.meta === $util.emptyObject)
                        m.meta = {};
                    var c2 = r.uint32() + r.pos;
                    k = "";
                    value = "";
                    while (r.pos < c2) {
                        var tag2 = r.uint32();
                        switch (tag2 >>> 3) {
                        case 1:
                            k = r.string();
                            break;
                        case 2:
                            value = r.string();
                            break;
                        default:
                            r.skipType(tag2 & 7);
                            break;
                        }
                    }
                    m.meta[k] = value;
                    break;
                case 9:
                    m.lastModified = r.int64();
                    break;
                case 10:
                    m.projectId = r.string();
                    break;
                case 11:
                    m.jobId = r.string();
                    break;
                case 12:
                    m.isDirectory = r.bool();
                    break;
                case 13:
                    m.name = r.string();
                    break;
                default:
                    r.skipType(t & 7);
                    break;
                }
            }
            return m;
        };

        return File;
    })();

    /**
     * ExecStatusType enum.
     * @name data.ExecStatusType
     * @enum {number}
     * @property {number} EST_NOT_SET=0 EST_NOT_SET value
     * @property {number} NEW=1 NEW value
     * @property {number} PENDING=2 PENDING value
     * @property {number} PROVISIONING=3 PROVISIONING value
     * @property {number} RUNNING=4 RUNNING value
     * @property {number} FINISHED=5 FINISHED value
     * @property {number} ABORTED=6 ABORTED value
     * @property {number} CRASHED=7 CRASHED value
     * @property {number} INVALID=8 INVALID value
     * @property {number} VOID=9 VOID value
     * @property {number} ARTIFACT_UPLOAD=10 ARTIFACT_UPLOAD value
     */
    data.ExecStatusType = (function() {
        const valuesById = {}, values = Object.create(valuesById);
        values[valuesById[0] = "EST_NOT_SET"] = 0;
        values[valuesById[1] = "NEW"] = 1;
        values[valuesById[2] = "PENDING"] = 2;
        values[valuesById[3] = "PROVISIONING"] = 3;
        values[valuesById[4] = "RUNNING"] = 4;
        values[valuesById[5] = "FINISHED"] = 5;
        values[valuesById[6] = "ABORTED"] = 6;
        values[valuesById[7] = "CRASHED"] = 7;
        values[valuesById[8] = "INVALID"] = 8;
        values[valuesById[9] = "VOID"] = 9;
        values[valuesById[10] = "ARTIFACT_UPLOAD"] = 10;
        return values;
    })();

    /**
     * ResultStatusType enum.
     * @name data.ResultStatusType
     * @enum {number}
     * @property {number} RST_NOT_SET=0 RST_NOT_SET value
     * @property {number} NO_RUN=1 NO_RUN value
     * @property {number} INFO=2 INFO value
     * @property {number} SKIPPED=3 SKIPPED value
     * @property {number} PASSED=4 PASSED value
     * @property {number} MINOR=5 MINOR value
     * @property {number} FAILED=7 FAILED value
     * @property {number} FAILED_MINOR=8 FAILED_MINOR value
     * @property {number} FAILED_RETRIED=9 FAILED_RETRIED value
     * @property {number} FAILED_EXPECTED=10 FAILED_EXPECTED value
     * @property {number} PASSED_RETRY=11 PASSED_RETRY value
     * @property {number} MINOR_RETRY=12 MINOR_RETRY value
     */
    data.ResultStatusType = (function() {
        const valuesById = {}, values = Object.create(valuesById);
        values[valuesById[0] = "RST_NOT_SET"] = 0;
        values[valuesById[1] = "NO_RUN"] = 1;
        values[valuesById[2] = "INFO"] = 2;
        values[valuesById[3] = "SKIPPED"] = 3;
        values[valuesById[4] = "PASSED"] = 4;
        values[valuesById[5] = "MINOR"] = 5;
        values[valuesById[7] = "FAILED"] = 7;
        values[valuesById[8] = "FAILED_MINOR"] = 8;
        values[valuesById[9] = "FAILED_RETRIED"] = 9;
        values[valuesById[10] = "FAILED_EXPECTED"] = 10;
        values[valuesById[11] = "PASSED_RETRY"] = 11;
        values[valuesById[12] = "MINOR_RETRY"] = 12;
        return values;
    })();

    data.ExecutionAggregate = (function() {

        /**
         * Properties of an ExecutionAggregate.
         * @memberof data
         * @interface IExecutionAggregate
         * @property {data.IExecutionContext|null} [executionContext] ExecutionAggregate executionContext
         * @property {Array.<data.ISuiteContext>|null} [suiteContexts] ExecutionAggregate suiteContexts
         * @property {Array.<data.ITestContext>|null} [testContexts] ExecutionAggregate testContexts
         * @property {Array.<data.IClassContext>|null} [classContexts] ExecutionAggregate classContexts
         * @property {Array.<data.IMethodContext>|null} [methodContexts] ExecutionAggregate methodContexts
         * @property {Array.<data.ISessionContext>|null} [sessionContexts] ExecutionAggregate sessionContexts
         */

        /**
         * Constructs a new ExecutionAggregate.
         * @memberof data
         * @classdesc Represents an ExecutionAggregate.
         * @implements IExecutionAggregate
         * @constructor
         * @param {data.IExecutionAggregate=} [p] Properties to set
         */
        function ExecutionAggregate(p) {
            this.suiteContexts = [];
            this.testContexts = [];
            this.classContexts = [];
            this.methodContexts = [];
            this.sessionContexts = [];
            if (p)
                for (var ks = Object.keys(p), i = 0; i < ks.length; ++i)
                    if (p[ks[i]] != null)
                        this[ks[i]] = p[ks[i]];
        }

        /**
         * ExecutionAggregate executionContext.
         * @member {data.IExecutionContext|null|undefined} executionContext
         * @memberof data.ExecutionAggregate
         * @instance
         */
        ExecutionAggregate.prototype.executionContext = null;

        /**
         * ExecutionAggregate suiteContexts.
         * @member {Array.<data.ISuiteContext>} suiteContexts
         * @memberof data.ExecutionAggregate
         * @instance
         */
        ExecutionAggregate.prototype.suiteContexts = $util.emptyArray;

        /**
         * ExecutionAggregate testContexts.
         * @member {Array.<data.ITestContext>} testContexts
         * @memberof data.ExecutionAggregate
         * @instance
         */
        ExecutionAggregate.prototype.testContexts = $util.emptyArray;

        /**
         * ExecutionAggregate classContexts.
         * @member {Array.<data.IClassContext>} classContexts
         * @memberof data.ExecutionAggregate
         * @instance
         */
        ExecutionAggregate.prototype.classContexts = $util.emptyArray;

        /**
         * ExecutionAggregate methodContexts.
         * @member {Array.<data.IMethodContext>} methodContexts
         * @memberof data.ExecutionAggregate
         * @instance
         */
        ExecutionAggregate.prototype.methodContexts = $util.emptyArray;

        /**
         * ExecutionAggregate sessionContexts.
         * @member {Array.<data.ISessionContext>} sessionContexts
         * @memberof data.ExecutionAggregate
         * @instance
         */
        ExecutionAggregate.prototype.sessionContexts = $util.emptyArray;

        /**
         * Decodes an ExecutionAggregate message from the specified reader or buffer.
         * @function decode
         * @memberof data.ExecutionAggregate
         * @static
         * @param {$protobuf.Reader|Uint8Array} r Reader or buffer to decode from
         * @param {number} [l] Message length if known beforehand
         * @returns {data.ExecutionAggregate} ExecutionAggregate
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        ExecutionAggregate.decode = function decode(r, l) {
            if (!(r instanceof $Reader))
                r = $Reader.create(r);
            var c = l === undefined ? r.len : r.pos + l, m = new $root.data.ExecutionAggregate();
            while (r.pos < c) {
                var t = r.uint32();
                switch (t >>> 3) {
                case 1:
                    m.executionContext = $root.data.ExecutionContext.decode(r, r.uint32());
                    break;
                case 2:
                    if (!(m.suiteContexts && m.suiteContexts.length))
                        m.suiteContexts = [];
                    m.suiteContexts.push($root.data.SuiteContext.decode(r, r.uint32()));
                    break;
                case 3:
                    if (!(m.testContexts && m.testContexts.length))
                        m.testContexts = [];
                    m.testContexts.push($root.data.TestContext.decode(r, r.uint32()));
                    break;
                case 4:
                    if (!(m.classContexts && m.classContexts.length))
                        m.classContexts = [];
                    m.classContexts.push($root.data.ClassContext.decode(r, r.uint32()));
                    break;
                case 5:
                    if (!(m.methodContexts && m.methodContexts.length))
                        m.methodContexts = [];
                    m.methodContexts.push($root.data.MethodContext.decode(r, r.uint32()));
                    break;
                case 6:
                    if (!(m.sessionContexts && m.sessionContexts.length))
                        m.sessionContexts = [];
                    m.sessionContexts.push($root.data.SessionContext.decode(r, r.uint32()));
                    break;
                default:
                    r.skipType(t & 7);
                    break;
                }
            }
            return m;
        };

        return ExecutionAggregate;
    })();

    return data;
})();

export { $root as default };
