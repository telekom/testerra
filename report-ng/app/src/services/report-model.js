/*eslint-disable block-scoped-var, id-length, no-control-regex, no-magic-numbers, no-prototype-builtins, no-redeclare, no-shadow, no-var, sort-vars*/
import * as $protobuf from "protobufjs/minimal";

// Common aliases
const $Reader = $protobuf.Reader, $Writer = $protobuf.Writer, $util = $protobuf.util;

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
         * @property {Array.<string>|null} [testContextIds] SuiteContext testContextIds
         * @property {string|null} [executionContextId] SuiteContext executionContextId
         */

        /**
         * Constructs a new SuiteContext.
         * @memberof data
         * @classdesc Represents a SuiteContext.
         * @implements ISuiteContext
         * @constructor
         * @param {data.ISuiteContext=} [properties] Properties to set
         */
        function SuiteContext(properties) {
            this.testContextIds = [];
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        /**
         * SuiteContext contextValues.
         * @member {data.IContextValues|null|undefined} contextValues
         * @memberof data.SuiteContext
         * @instance
         */
        SuiteContext.prototype.contextValues = null;

        /**
         * SuiteContext testContextIds.
         * @member {Array.<string>} testContextIds
         * @memberof data.SuiteContext
         * @instance
         */
        SuiteContext.prototype.testContextIds = $util.emptyArray;

        /**
         * SuiteContext executionContextId.
         * @member {string} executionContextId
         * @memberof data.SuiteContext
         * @instance
         */
        SuiteContext.prototype.executionContextId = "";

        /**
         * Encodes the specified SuiteContext message. Does not implicitly {@link data.SuiteContext.verify|verify} messages.
         * @function encode
         * @memberof data.SuiteContext
         * @static
         * @param {data.ISuiteContext} message SuiteContext message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        SuiteContext.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.contextValues != null && Object.hasOwnProperty.call(message, "contextValues"))
                $root.data.ContextValues.encode(message.contextValues, writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
            if (message.testContextIds != null && message.testContextIds.length)
                for (let i = 0; i < message.testContextIds.length; ++i)
                    writer.uint32(/* id 6, wireType 2 =*/50).string(message.testContextIds[i]);
            if (message.executionContextId != null && Object.hasOwnProperty.call(message, "executionContextId"))
                writer.uint32(/* id 7, wireType 2 =*/58).string(message.executionContextId);
            return writer;
        };

        /**
         * Decodes a SuiteContext message from the specified reader or buffer.
         * @function decode
         * @memberof data.SuiteContext
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.SuiteContext} SuiteContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        SuiteContext.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.SuiteContext();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.contextValues = $root.data.ContextValues.decode(reader, reader.uint32());
                    break;
                case 6:
                    if (!(message.testContextIds && message.testContextIds.length))
                        message.testContextIds = [];
                    message.testContextIds.push(reader.string());
                    break;
                case 7:
                    message.executionContextId = reader.string();
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        return SuiteContext;
    })();

    data.ClassContext = (function() {

        /**
         * Properties of a ClassContext.
         * @memberof data
         * @interface IClassContext
         * @property {data.IContextValues|null} [contextValues] ClassContext contextValues
         * @property {Array.<string>|null} [methodContextIds] ClassContext methodContextIds
         * @property {string|null} [fullClassName] ClassContext fullClassName
         * @property {string|null} [simpleClassName] ClassContext simpleClassName
         * @property {string|null} [testContextId] ClassContext testContextId
         * @property {string|null} [executionContextId] ClassContext executionContextId
         * @property {boolean|null} [merged] ClassContext merged
         */

        /**
         * Constructs a new ClassContext.
         * @memberof data
         * @classdesc Represents a ClassContext.
         * @implements IClassContext
         * @constructor
         * @param {data.IClassContext=} [properties] Properties to set
         */
        function ClassContext(properties) {
            this.methodContextIds = [];
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        /**
         * ClassContext contextValues.
         * @member {data.IContextValues|null|undefined} contextValues
         * @memberof data.ClassContext
         * @instance
         */
        ClassContext.prototype.contextValues = null;

        /**
         * ClassContext methodContextIds.
         * @member {Array.<string>} methodContextIds
         * @memberof data.ClassContext
         * @instance
         */
        ClassContext.prototype.methodContextIds = $util.emptyArray;

        /**
         * ClassContext fullClassName.
         * @member {string} fullClassName
         * @memberof data.ClassContext
         * @instance
         */
        ClassContext.prototype.fullClassName = "";

        /**
         * ClassContext simpleClassName.
         * @member {string} simpleClassName
         * @memberof data.ClassContext
         * @instance
         */
        ClassContext.prototype.simpleClassName = "";

        /**
         * ClassContext testContextId.
         * @member {string} testContextId
         * @memberof data.ClassContext
         * @instance
         */
        ClassContext.prototype.testContextId = "";

        /**
         * ClassContext executionContextId.
         * @member {string} executionContextId
         * @memberof data.ClassContext
         * @instance
         */
        ClassContext.prototype.executionContextId = "";

        /**
         * ClassContext merged.
         * @member {boolean} merged
         * @memberof data.ClassContext
         * @instance
         */
        ClassContext.prototype.merged = false;

        /**
         * Encodes the specified ClassContext message. Does not implicitly {@link data.ClassContext.verify|verify} messages.
         * @function encode
         * @memberof data.ClassContext
         * @static
         * @param {data.IClassContext} message ClassContext message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        ClassContext.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.contextValues != null && Object.hasOwnProperty.call(message, "contextValues"))
                $root.data.ContextValues.encode(message.contextValues, writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
            if (message.methodContextIds != null && message.methodContextIds.length)
                for (let i = 0; i < message.methodContextIds.length; ++i)
                    writer.uint32(/* id 6, wireType 2 =*/50).string(message.methodContextIds[i]);
            if (message.fullClassName != null && Object.hasOwnProperty.call(message, "fullClassName"))
                writer.uint32(/* id 7, wireType 2 =*/58).string(message.fullClassName);
            if (message.simpleClassName != null && Object.hasOwnProperty.call(message, "simpleClassName"))
                writer.uint32(/* id 8, wireType 2 =*/66).string(message.simpleClassName);
            if (message.testContextId != null && Object.hasOwnProperty.call(message, "testContextId"))
                writer.uint32(/* id 9, wireType 2 =*/74).string(message.testContextId);
            if (message.executionContextId != null && Object.hasOwnProperty.call(message, "executionContextId"))
                writer.uint32(/* id 10, wireType 2 =*/82).string(message.executionContextId);
            if (message.merged != null && Object.hasOwnProperty.call(message, "merged"))
                writer.uint32(/* id 12, wireType 0 =*/96).bool(message.merged);
            return writer;
        };

        /**
         * Decodes a ClassContext message from the specified reader or buffer.
         * @function decode
         * @memberof data.ClassContext
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.ClassContext} ClassContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        ClassContext.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.ClassContext();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.contextValues = $root.data.ContextValues.decode(reader, reader.uint32());
                    break;
                case 6:
                    if (!(message.methodContextIds && message.methodContextIds.length))
                        message.methodContextIds = [];
                    message.methodContextIds.push(reader.string());
                    break;
                case 7:
                    message.fullClassName = reader.string();
                    break;
                case 8:
                    message.simpleClassName = reader.string();
                    break;
                case 9:
                    message.testContextId = reader.string();
                    break;
                case 10:
                    message.executionContextId = reader.string();
                    break;
                case 12:
                    message.merged = reader.bool();
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        return ClassContext;
    })();

    data.TestContext = (function() {

        /**
         * Properties of a TestContext.
         * @memberof data
         * @interface ITestContext
         * @property {data.IContextValues|null} [contextValues] TestContext contextValues
         * @property {Array.<string>|null} [classContextIds] TestContext classContextIds
         * @property {string|null} [suiteContextId] TestContext suiteContextId
         * @property {string|null} [executionContextId] TestContext executionContextId
         */

        /**
         * Constructs a new TestContext.
         * @memberof data
         * @classdesc Represents a TestContext.
         * @implements ITestContext
         * @constructor
         * @param {data.ITestContext=} [properties] Properties to set
         */
        function TestContext(properties) {
            this.classContextIds = [];
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        /**
         * TestContext contextValues.
         * @member {data.IContextValues|null|undefined} contextValues
         * @memberof data.TestContext
         * @instance
         */
        TestContext.prototype.contextValues = null;

        /**
         * TestContext classContextIds.
         * @member {Array.<string>} classContextIds
         * @memberof data.TestContext
         * @instance
         */
        TestContext.prototype.classContextIds = $util.emptyArray;

        /**
         * TestContext suiteContextId.
         * @member {string} suiteContextId
         * @memberof data.TestContext
         * @instance
         */
        TestContext.prototype.suiteContextId = "";

        /**
         * TestContext executionContextId.
         * @member {string} executionContextId
         * @memberof data.TestContext
         * @instance
         */
        TestContext.prototype.executionContextId = "";

        /**
         * Encodes the specified TestContext message. Does not implicitly {@link data.TestContext.verify|verify} messages.
         * @function encode
         * @memberof data.TestContext
         * @static
         * @param {data.ITestContext} message TestContext message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        TestContext.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.contextValues != null && Object.hasOwnProperty.call(message, "contextValues"))
                $root.data.ContextValues.encode(message.contextValues, writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
            if (message.classContextIds != null && message.classContextIds.length)
                for (let i = 0; i < message.classContextIds.length; ++i)
                    writer.uint32(/* id 6, wireType 2 =*/50).string(message.classContextIds[i]);
            if (message.suiteContextId != null && Object.hasOwnProperty.call(message, "suiteContextId"))
                writer.uint32(/* id 7, wireType 2 =*/58).string(message.suiteContextId);
            if (message.executionContextId != null && Object.hasOwnProperty.call(message, "executionContextId"))
                writer.uint32(/* id 8, wireType 2 =*/66).string(message.executionContextId);
            return writer;
        };

        /**
         * Decodes a TestContext message from the specified reader or buffer.
         * @function decode
         * @memberof data.TestContext
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.TestContext} TestContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        TestContext.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.TestContext();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.contextValues = $root.data.ContextValues.decode(reader, reader.uint32());
                    break;
                case 6:
                    if (!(message.classContextIds && message.classContextIds.length))
                        message.classContextIds = [];
                    message.classContextIds.push(reader.string());
                    break;
                case 7:
                    message.suiteContextId = reader.string();
                    break;
                case 8:
                    message.executionContextId = reader.string();
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        return TestContext;
    })();

    data.ExecutionContext = (function() {

        /**
         * Properties of an ExecutionContext.
         * @memberof data
         * @interface IExecutionContext
         * @property {data.IContextValues|null} [contextValues] ExecutionContext contextValues
         * @property {Array.<string>|null} [mergedClassContextIds] ExecutionContext mergedClassContextIds
         * @property {Array.<data.IContextClip>|null} [exitPoints] ExecutionContext exitPoints
         * @property {Array.<data.IContextClip>|null} [failureAscpects] ExecutionContext failureAscpects
         * @property {Array.<string>|null} [suiteContextIds] ExecutionContext suiteContextIds
         * @property {data.IRunConfig|null} [runConfig] ExecutionContext runConfig
         * @property {string|null} [project_Id] ExecutionContext project_Id
         * @property {string|null} [job_Id] ExecutionContext job_Id
         * @property {string|null} [run_Id] ExecutionContext run_Id
         * @property {string|null} [task_Id] ExecutionContext task_Id
         * @property {Array.<string>|null} [exclusiveSessionContextIds] ExecutionContext exclusiveSessionContextIds
         * @property {number|null} [estimatedTestMethodCount] ExecutionContext estimatedTestMethodCount
         */

        /**
         * Constructs a new ExecutionContext.
         * @memberof data
         * @classdesc Represents an ExecutionContext.
         * @implements IExecutionContext
         * @constructor
         * @param {data.IExecutionContext=} [properties] Properties to set
         */
        function ExecutionContext(properties) {
            this.mergedClassContextIds = [];
            this.exitPoints = [];
            this.failureAscpects = [];
            this.suiteContextIds = [];
            this.exclusiveSessionContextIds = [];
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        /**
         * ExecutionContext contextValues.
         * @member {data.IContextValues|null|undefined} contextValues
         * @memberof data.ExecutionContext
         * @instance
         */
        ExecutionContext.prototype.contextValues = null;

        /**
         * ExecutionContext mergedClassContextIds.
         * @member {Array.<string>} mergedClassContextIds
         * @memberof data.ExecutionContext
         * @instance
         */
        ExecutionContext.prototype.mergedClassContextIds = $util.emptyArray;

        /**
         * ExecutionContext exitPoints.
         * @member {Array.<data.IContextClip>} exitPoints
         * @memberof data.ExecutionContext
         * @instance
         */
        ExecutionContext.prototype.exitPoints = $util.emptyArray;

        /**
         * ExecutionContext failureAscpects.
         * @member {Array.<data.IContextClip>} failureAscpects
         * @memberof data.ExecutionContext
         * @instance
         */
        ExecutionContext.prototype.failureAscpects = $util.emptyArray;

        /**
         * ExecutionContext suiteContextIds.
         * @member {Array.<string>} suiteContextIds
         * @memberof data.ExecutionContext
         * @instance
         */
        ExecutionContext.prototype.suiteContextIds = $util.emptyArray;

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
         * ExecutionContext estimatedTestMethodCount.
         * @member {number} estimatedTestMethodCount
         * @memberof data.ExecutionContext
         * @instance
         */
        ExecutionContext.prototype.estimatedTestMethodCount = 0;

        /**
         * Encodes the specified ExecutionContext message. Does not implicitly {@link data.ExecutionContext.verify|verify} messages.
         * @function encode
         * @memberof data.ExecutionContext
         * @static
         * @param {data.IExecutionContext} message ExecutionContext message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        ExecutionContext.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.contextValues != null && Object.hasOwnProperty.call(message, "contextValues"))
                $root.data.ContextValues.encode(message.contextValues, writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
            if (message.mergedClassContextIds != null && message.mergedClassContextIds.length)
                for (let i = 0; i < message.mergedClassContextIds.length; ++i)
                    writer.uint32(/* id 3, wireType 2 =*/26).string(message.mergedClassContextIds[i]);
            if (message.exitPoints != null && message.exitPoints.length)
                for (let i = 0; i < message.exitPoints.length; ++i)
                    $root.data.ContextClip.encode(message.exitPoints[i], writer.uint32(/* id 4, wireType 2 =*/34).fork()).ldelim();
            if (message.failureAscpects != null && message.failureAscpects.length)
                for (let i = 0; i < message.failureAscpects.length; ++i)
                    $root.data.ContextClip.encode(message.failureAscpects[i], writer.uint32(/* id 5, wireType 2 =*/42).fork()).ldelim();
            if (message.suiteContextIds != null && message.suiteContextIds.length)
                for (let i = 0; i < message.suiteContextIds.length; ++i)
                    writer.uint32(/* id 6, wireType 2 =*/50).string(message.suiteContextIds[i]);
            if (message.runConfig != null && Object.hasOwnProperty.call(message, "runConfig"))
                $root.data.RunConfig.encode(message.runConfig, writer.uint32(/* id 7, wireType 2 =*/58).fork()).ldelim();
            if (message.project_Id != null && Object.hasOwnProperty.call(message, "project_Id"))
                writer.uint32(/* id 8, wireType 2 =*/66).string(message.project_Id);
            if (message.job_Id != null && Object.hasOwnProperty.call(message, "job_Id"))
                writer.uint32(/* id 9, wireType 2 =*/74).string(message.job_Id);
            if (message.run_Id != null && Object.hasOwnProperty.call(message, "run_Id"))
                writer.uint32(/* id 10, wireType 2 =*/82).string(message.run_Id);
            if (message.task_Id != null && Object.hasOwnProperty.call(message, "task_Id"))
                writer.uint32(/* id 11, wireType 2 =*/90).string(message.task_Id);
            if (message.exclusiveSessionContextIds != null && message.exclusiveSessionContextIds.length)
                for (let i = 0; i < message.exclusiveSessionContextIds.length; ++i)
                    writer.uint32(/* id 12, wireType 2 =*/98).string(message.exclusiveSessionContextIds[i]);
            if (message.estimatedTestMethodCount != null && Object.hasOwnProperty.call(message, "estimatedTestMethodCount"))
                writer.uint32(/* id 13, wireType 0 =*/104).int32(message.estimatedTestMethodCount);
            return writer;
        };

        /**
         * Decodes an ExecutionContext message from the specified reader or buffer.
         * @function decode
         * @memberof data.ExecutionContext
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.ExecutionContext} ExecutionContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        ExecutionContext.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.ExecutionContext();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.contextValues = $root.data.ContextValues.decode(reader, reader.uint32());
                    break;
                case 3:
                    if (!(message.mergedClassContextIds && message.mergedClassContextIds.length))
                        message.mergedClassContextIds = [];
                    message.mergedClassContextIds.push(reader.string());
                    break;
                case 4:
                    if (!(message.exitPoints && message.exitPoints.length))
                        message.exitPoints = [];
                    message.exitPoints.push($root.data.ContextClip.decode(reader, reader.uint32()));
                    break;
                case 5:
                    if (!(message.failureAscpects && message.failureAscpects.length))
                        message.failureAscpects = [];
                    message.failureAscpects.push($root.data.ContextClip.decode(reader, reader.uint32()));
                    break;
                case 6:
                    if (!(message.suiteContextIds && message.suiteContextIds.length))
                        message.suiteContextIds = [];
                    message.suiteContextIds.push(reader.string());
                    break;
                case 7:
                    message.runConfig = $root.data.RunConfig.decode(reader, reader.uint32());
                    break;
                case 8:
                    message.project_Id = reader.string();
                    break;
                case 9:
                    message.job_Id = reader.string();
                    break;
                case 10:
                    message.run_Id = reader.string();
                    break;
                case 11:
                    message.task_Id = reader.string();
                    break;
                case 12:
                    if (!(message.exclusiveSessionContextIds && message.exclusiveSessionContextIds.length))
                        message.exclusiveSessionContextIds = [];
                    message.exclusiveSessionContextIds.push(reader.string());
                    break;
                case 13:
                    message.estimatedTestMethodCount = reader.int32();
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        return ExecutionContext;
    })();

    data.ContextClip = (function() {

        /**
         * Properties of a ContextClip.
         * @memberof data
         * @interface IContextClip
         * @property {string|null} [key] ContextClip key
         * @property {Array.<string>|null} [methodContextIds] ContextClip methodContextIds
         */

        /**
         * Constructs a new ContextClip.
         * @memberof data
         * @classdesc Represents a ContextClip.
         * @implements IContextClip
         * @constructor
         * @param {data.IContextClip=} [properties] Properties to set
         */
        function ContextClip(properties) {
            this.methodContextIds = [];
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        /**
         * ContextClip key.
         * @member {string} key
         * @memberof data.ContextClip
         * @instance
         */
        ContextClip.prototype.key = "";

        /**
         * ContextClip methodContextIds.
         * @member {Array.<string>} methodContextIds
         * @memberof data.ContextClip
         * @instance
         */
        ContextClip.prototype.methodContextIds = $util.emptyArray;

        /**
         * Encodes the specified ContextClip message. Does not implicitly {@link data.ContextClip.verify|verify} messages.
         * @function encode
         * @memberof data.ContextClip
         * @static
         * @param {data.IContextClip} message ContextClip message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        ContextClip.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.key != null && Object.hasOwnProperty.call(message, "key"))
                writer.uint32(/* id 1, wireType 2 =*/10).string(message.key);
            if (message.methodContextIds != null && message.methodContextIds.length)
                for (let i = 0; i < message.methodContextIds.length; ++i)
                    writer.uint32(/* id 2, wireType 2 =*/18).string(message.methodContextIds[i]);
            return writer;
        };

        /**
         * Decodes a ContextClip message from the specified reader or buffer.
         * @function decode
         * @memberof data.ContextClip
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.ContextClip} ContextClip
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        ContextClip.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.ContextClip();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.key = reader.string();
                    break;
                case 2:
                    if (!(message.methodContextIds && message.methodContextIds.length))
                        message.methodContextIds = [];
                    message.methodContextIds.push(reader.string());
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        return ContextClip;
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
         * @property {string|null} [executionContextId] MethodContext executionContextId
         * @property {Array.<data.IErrorContext>|null} [nonFunctionalInfos] MethodContext nonFunctionalInfos
         * @property {Array.<data.IErrorContext>|null} [collectedAssertions] MethodContext collectedAssertions
         * @property {Array.<string>|null} [infos] MethodContext infos
         * @property {string|null} [priorityMessage] MethodContext priorityMessage
         * @property {Array.<string>|null} [relatedMethodContextIds] MethodContext relatedMethodContextIds
         * @property {Array.<string>|null} [dependsOnMethodContextIds] MethodContext dependsOnMethodContextIds
         * @property {data.IErrorContext|null} [errorContext] MethodContext errorContext
         * @property {Array.<data.IPTestStep>|null} [testSteps] MethodContext testSteps
         * @property {string|null} [testContextId] MethodContext testContextId
         * @property {string|null} [suiteContextId] MethodContext suiteContextId
         * @property {Array.<string>|null} [sessionContextIds] MethodContext sessionContextIds
         * @property {Array.<string>|null} [videoIds] MethodContext videoIds
         * @property {Array.<string>|null} [screenshotIds] MethodContext screenshotIds
         * @property {string|null} [customContextJson] MethodContext customContextJson
         */

        /**
         * Constructs a new MethodContext.
         * @memberof data
         * @classdesc Represents a MethodContext.
         * @implements IMethodContext
         * @constructor
         * @param {data.IMethodContext=} [properties] Properties to set
         */
        function MethodContext(properties) {
            this.parameters = [];
            this.methodTags = [];
            this.nonFunctionalInfos = [];
            this.collectedAssertions = [];
            this.infos = [];
            this.relatedMethodContextIds = [];
            this.dependsOnMethodContextIds = [];
            this.testSteps = [];
            this.sessionContextIds = [];
            this.videoIds = [];
            this.screenshotIds = [];
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
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
         * MethodContext executionContextId.
         * @member {string} executionContextId
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.executionContextId = "";

        /**
         * MethodContext nonFunctionalInfos.
         * @member {Array.<data.IErrorContext>} nonFunctionalInfos
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.nonFunctionalInfos = $util.emptyArray;

        /**
         * MethodContext collectedAssertions.
         * @member {Array.<data.IErrorContext>} collectedAssertions
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.collectedAssertions = $util.emptyArray;

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
         * @member {Array.<data.IPTestStep>} testSteps
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.testSteps = $util.emptyArray;

        /**
         * MethodContext testContextId.
         * @member {string} testContextId
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.testContextId = "";

        /**
         * MethodContext suiteContextId.
         * @member {string} suiteContextId
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.suiteContextId = "";

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
         * MethodContext screenshotIds.
         * @member {Array.<string>} screenshotIds
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.screenshotIds = $util.emptyArray;

        /**
         * MethodContext customContextJson.
         * @member {string} customContextJson
         * @memberof data.MethodContext
         * @instance
         */
        MethodContext.prototype.customContextJson = "";

        /**
         * Encodes the specified MethodContext message. Does not implicitly {@link data.MethodContext.verify|verify} messages.
         * @function encode
         * @memberof data.MethodContext
         * @static
         * @param {data.IMethodContext} message MethodContext message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        MethodContext.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.contextValues != null && Object.hasOwnProperty.call(message, "contextValues"))
                $root.data.ContextValues.encode(message.contextValues, writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
            if (message.methodType != null && Object.hasOwnProperty.call(message, "methodType"))
                writer.uint32(/* id 7, wireType 0 =*/56).int32(message.methodType);
            if (message.parameters != null && message.parameters.length)
                for (let i = 0; i < message.parameters.length; ++i)
                    writer.uint32(/* id 8, wireType 2 =*/66).string(message.parameters[i]);
            if (message.methodTags != null && message.methodTags.length)
                for (let i = 0; i < message.methodTags.length; ++i)
                    writer.uint32(/* id 9, wireType 2 =*/74).string(message.methodTags[i]);
            if (message.retryNumber != null && Object.hasOwnProperty.call(message, "retryNumber"))
                writer.uint32(/* id 10, wireType 0 =*/80).int32(message.retryNumber);
            if (message.methodRunIndex != null && Object.hasOwnProperty.call(message, "methodRunIndex"))
                writer.uint32(/* id 11, wireType 0 =*/88).int32(message.methodRunIndex);
            if (message.threadName != null && Object.hasOwnProperty.call(message, "threadName"))
                writer.uint32(/* id 12, wireType 2 =*/98).string(message.threadName);
            if (message.failureCorridorValue != null && Object.hasOwnProperty.call(message, "failureCorridorValue"))
                writer.uint32(/* id 14, wireType 0 =*/112).int32(message.failureCorridorValue);
            if (message.classContextId != null && Object.hasOwnProperty.call(message, "classContextId"))
                writer.uint32(/* id 15, wireType 2 =*/122).string(message.classContextId);
            if (message.executionContextId != null && Object.hasOwnProperty.call(message, "executionContextId"))
                writer.uint32(/* id 16, wireType 2 =*/130).string(message.executionContextId);
            if (message.nonFunctionalInfos != null && message.nonFunctionalInfos.length)
                for (let i = 0; i < message.nonFunctionalInfos.length; ++i)
                    $root.data.ErrorContext.encode(message.nonFunctionalInfos[i], writer.uint32(/* id 17, wireType 2 =*/138).fork()).ldelim();
            if (message.collectedAssertions != null && message.collectedAssertions.length)
                for (let i = 0; i < message.collectedAssertions.length; ++i)
                    $root.data.ErrorContext.encode(message.collectedAssertions[i], writer.uint32(/* id 18, wireType 2 =*/146).fork()).ldelim();
            if (message.infos != null && message.infos.length)
                for (let i = 0; i < message.infos.length; ++i)
                    writer.uint32(/* id 19, wireType 2 =*/154).string(message.infos[i]);
            if (message.priorityMessage != null && Object.hasOwnProperty.call(message, "priorityMessage"))
                writer.uint32(/* id 21, wireType 2 =*/170).string(message.priorityMessage);
            if (message.relatedMethodContextIds != null && message.relatedMethodContextIds.length)
                for (let i = 0; i < message.relatedMethodContextIds.length; ++i)
                    writer.uint32(/* id 23, wireType 2 =*/186).string(message.relatedMethodContextIds[i]);
            if (message.dependsOnMethodContextIds != null && message.dependsOnMethodContextIds.length)
                for (let i = 0; i < message.dependsOnMethodContextIds.length; ++i)
                    writer.uint32(/* id 24, wireType 2 =*/194).string(message.dependsOnMethodContextIds[i]);
            if (message.errorContext != null && Object.hasOwnProperty.call(message, "errorContext"))
                $root.data.ErrorContext.encode(message.errorContext, writer.uint32(/* id 25, wireType 2 =*/202).fork()).ldelim();
            if (message.testSteps != null && message.testSteps.length)
                for (let i = 0; i < message.testSteps.length; ++i)
                    $root.data.PTestStep.encode(message.testSteps[i], writer.uint32(/* id 26, wireType 2 =*/210).fork()).ldelim();
            if (message.testContextId != null && Object.hasOwnProperty.call(message, "testContextId"))
                writer.uint32(/* id 27, wireType 2 =*/218).string(message.testContextId);
            if (message.suiteContextId != null && Object.hasOwnProperty.call(message, "suiteContextId"))
                writer.uint32(/* id 28, wireType 2 =*/226).string(message.suiteContextId);
            if (message.sessionContextIds != null && message.sessionContextIds.length)
                for (let i = 0; i < message.sessionContextIds.length; ++i)
                    writer.uint32(/* id 29, wireType 2 =*/234).string(message.sessionContextIds[i]);
            if (message.videoIds != null && message.videoIds.length)
                for (let i = 0; i < message.videoIds.length; ++i)
                    writer.uint32(/* id 30, wireType 2 =*/242).string(message.videoIds[i]);
            if (message.screenshotIds != null && message.screenshotIds.length)
                for (let i = 0; i < message.screenshotIds.length; ++i)
                    writer.uint32(/* id 31, wireType 2 =*/250).string(message.screenshotIds[i]);
            if (message.customContextJson != null && Object.hasOwnProperty.call(message, "customContextJson"))
                writer.uint32(/* id 32, wireType 2 =*/258).string(message.customContextJson);
            return writer;
        };

        /**
         * Decodes a MethodContext message from the specified reader or buffer.
         * @function decode
         * @memberof data.MethodContext
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.MethodContext} MethodContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        MethodContext.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.MethodContext();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.contextValues = $root.data.ContextValues.decode(reader, reader.uint32());
                    break;
                case 7:
                    message.methodType = reader.int32();
                    break;
                case 8:
                    if (!(message.parameters && message.parameters.length))
                        message.parameters = [];
                    message.parameters.push(reader.string());
                    break;
                case 9:
                    if (!(message.methodTags && message.methodTags.length))
                        message.methodTags = [];
                    message.methodTags.push(reader.string());
                    break;
                case 10:
                    message.retryNumber = reader.int32();
                    break;
                case 11:
                    message.methodRunIndex = reader.int32();
                    break;
                case 12:
                    message.threadName = reader.string();
                    break;
                case 14:
                    message.failureCorridorValue = reader.int32();
                    break;
                case 15:
                    message.classContextId = reader.string();
                    break;
                case 16:
                    message.executionContextId = reader.string();
                    break;
                case 17:
                    if (!(message.nonFunctionalInfos && message.nonFunctionalInfos.length))
                        message.nonFunctionalInfos = [];
                    message.nonFunctionalInfos.push($root.data.ErrorContext.decode(reader, reader.uint32()));
                    break;
                case 18:
                    if (!(message.collectedAssertions && message.collectedAssertions.length))
                        message.collectedAssertions = [];
                    message.collectedAssertions.push($root.data.ErrorContext.decode(reader, reader.uint32()));
                    break;
                case 19:
                    if (!(message.infos && message.infos.length))
                        message.infos = [];
                    message.infos.push(reader.string());
                    break;
                case 21:
                    message.priorityMessage = reader.string();
                    break;
                case 23:
                    if (!(message.relatedMethodContextIds && message.relatedMethodContextIds.length))
                        message.relatedMethodContextIds = [];
                    message.relatedMethodContextIds.push(reader.string());
                    break;
                case 24:
                    if (!(message.dependsOnMethodContextIds && message.dependsOnMethodContextIds.length))
                        message.dependsOnMethodContextIds = [];
                    message.dependsOnMethodContextIds.push(reader.string());
                    break;
                case 25:
                    message.errorContext = $root.data.ErrorContext.decode(reader, reader.uint32());
                    break;
                case 26:
                    if (!(message.testSteps && message.testSteps.length))
                        message.testSteps = [];
                    message.testSteps.push($root.data.PTestStep.decode(reader, reader.uint32()));
                    break;
                case 27:
                    message.testContextId = reader.string();
                    break;
                case 28:
                    message.suiteContextId = reader.string();
                    break;
                case 29:
                    if (!(message.sessionContextIds && message.sessionContextIds.length))
                        message.sessionContextIds = [];
                    message.sessionContextIds.push(reader.string());
                    break;
                case 30:
                    if (!(message.videoIds && message.videoIds.length))
                        message.videoIds = [];
                    message.videoIds.push(reader.string());
                    break;
                case 31:
                    if (!(message.screenshotIds && message.screenshotIds.length))
                        message.screenshotIds = [];
                    message.screenshotIds.push(reader.string());
                    break;
                case 32:
                    message.customContextJson = reader.string();
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        return MethodContext;
    })();

    data.ContextValues = (function() {

        /**
         * Properties of a ContextValues.
         * @memberof data
         * @interface IContextValues
         * @property {string|null} [id] ContextValues id
         * @property {number|Long|null} [created] ContextValues created
         * @property {string|null} [name] ContextValues name
         * @property {number|Long|null} [startTime] ContextValues startTime
         * @property {number|Long|null} [endTime] ContextValues endTime
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
         * @param {data.IContextValues=} [properties] Properties to set
         */
        function ContextValues(properties) {
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
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
         * @member {number|Long} created
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
         * @member {number|Long} startTime
         * @memberof data.ContextValues
         * @instance
         */
        ContextValues.prototype.startTime = $util.Long ? $util.Long.fromBits(0,0,false) : 0;

        /**
         * ContextValues endTime.
         * @member {number|Long} endTime
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
         * Encodes the specified ContextValues message. Does not implicitly {@link data.ContextValues.verify|verify} messages.
         * @function encode
         * @memberof data.ContextValues
         * @static
         * @param {data.IContextValues} message ContextValues message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        ContextValues.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.id != null && Object.hasOwnProperty.call(message, "id"))
                writer.uint32(/* id 1, wireType 2 =*/10).string(message.id);
            if (message.created != null && Object.hasOwnProperty.call(message, "created"))
                writer.uint32(/* id 2, wireType 0 =*/16).int64(message.created);
            if (message.name != null && Object.hasOwnProperty.call(message, "name"))
                writer.uint32(/* id 3, wireType 2 =*/26).string(message.name);
            if (message.startTime != null && Object.hasOwnProperty.call(message, "startTime"))
                writer.uint32(/* id 4, wireType 0 =*/32).int64(message.startTime);
            if (message.endTime != null && Object.hasOwnProperty.call(message, "endTime"))
                writer.uint32(/* id 5, wireType 0 =*/40).int64(message.endTime);
            if (message.swi != null && Object.hasOwnProperty.call(message, "swi"))
                writer.uint32(/* id 6, wireType 2 =*/50).string(message.swi);
            if (message.resultStatus != null && Object.hasOwnProperty.call(message, "resultStatus"))
                writer.uint32(/* id 7, wireType 0 =*/56).int32(message.resultStatus);
            if (message.execStatus != null && Object.hasOwnProperty.call(message, "execStatus"))
                writer.uint32(/* id 8, wireType 0 =*/64).int32(message.execStatus);
            return writer;
        };

        /**
         * Decodes a ContextValues message from the specified reader or buffer.
         * @function decode
         * @memberof data.ContextValues
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.ContextValues} ContextValues
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        ContextValues.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.ContextValues();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.id = reader.string();
                    break;
                case 2:
                    message.created = reader.int64();
                    break;
                case 3:
                    message.name = reader.string();
                    break;
                case 4:
                    message.startTime = reader.int64();
                    break;
                case 5:
                    message.endTime = reader.int64();
                    break;
                case 6:
                    message.swi = reader.string();
                    break;
                case 7:
                    message.resultStatus = reader.int32();
                    break;
                case 8:
                    message.execStatus = reader.int32();
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        return ContextValues;
    })();

    data.PTestStep = (function() {

        /**
         * Properties of a PTestStep.
         * @memberof data
         * @interface IPTestStep
         * @property {string|null} [name] PTestStep name
         * @property {string|null} [id] PTestStep id
         * @property {Array.<data.IPTestStepAction>|null} [testStepActions] PTestStep testStepActions
         */

        /**
         * Constructs a new PTestStep.
         * @memberof data
         * @classdesc Represents a PTestStep.
         * @implements IPTestStep
         * @constructor
         * @param {data.IPTestStep=} [properties] Properties to set
         */
        function PTestStep(properties) {
            this.testStepActions = [];
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        /**
         * PTestStep name.
         * @member {string} name
         * @memberof data.PTestStep
         * @instance
         */
        PTestStep.prototype.name = "";

        /**
         * PTestStep id.
         * @member {string} id
         * @memberof data.PTestStep
         * @instance
         */
        PTestStep.prototype.id = "";

        /**
         * PTestStep testStepActions.
         * @member {Array.<data.IPTestStepAction>} testStepActions
         * @memberof data.PTestStep
         * @instance
         */
        PTestStep.prototype.testStepActions = $util.emptyArray;

        /**
         * Encodes the specified PTestStep message. Does not implicitly {@link data.PTestStep.verify|verify} messages.
         * @function encode
         * @memberof data.PTestStep
         * @static
         * @param {data.IPTestStep} message PTestStep message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        PTestStep.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.name != null && Object.hasOwnProperty.call(message, "name"))
                writer.uint32(/* id 1, wireType 2 =*/10).string(message.name);
            if (message.id != null && Object.hasOwnProperty.call(message, "id"))
                writer.uint32(/* id 2, wireType 2 =*/18).string(message.id);
            if (message.testStepActions != null && message.testStepActions.length)
                for (let i = 0; i < message.testStepActions.length; ++i)
                    $root.data.PTestStepAction.encode(message.testStepActions[i], writer.uint32(/* id 3, wireType 2 =*/26).fork()).ldelim();
            return writer;
        };

        /**
         * Decodes a PTestStep message from the specified reader or buffer.
         * @function decode
         * @memberof data.PTestStep
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.PTestStep} PTestStep
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        PTestStep.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.PTestStep();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.name = reader.string();
                    break;
                case 2:
                    message.id = reader.string();
                    break;
                case 3:
                    if (!(message.testStepActions && message.testStepActions.length))
                        message.testStepActions = [];
                    message.testStepActions.push($root.data.PTestStepAction.decode(reader, reader.uint32()));
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        return PTestStep;
    })();

    data.PTestStepAction = (function() {

        /**
         * Properties of a PTestStepAction.
         * @memberof data
         * @interface IPTestStepAction
         * @property {string|null} [name] PTestStepAction name
         * @property {string|null} [id] PTestStepAction id
         * @property {number|Long|null} [timestamp] PTestStepAction timestamp
         * @property {Array.<string>|null} [screenshotNames] PTestStepAction screenshotNames
         * @property {Array.<data.IPClickPathEvent>|null} [clickpathEvents] PTestStepAction clickpathEvents
         * @property {Array.<string>|null} [screenshotIds] PTestStepAction screenshotIds
         */

        /**
         * Constructs a new PTestStepAction.
         * @memberof data
         * @classdesc Represents a PTestStepAction.
         * @implements IPTestStepAction
         * @constructor
         * @param {data.IPTestStepAction=} [properties] Properties to set
         */
        function PTestStepAction(properties) {
            this.screenshotNames = [];
            this.clickpathEvents = [];
            this.screenshotIds = [];
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        /**
         * PTestStepAction name.
         * @member {string} name
         * @memberof data.PTestStepAction
         * @instance
         */
        PTestStepAction.prototype.name = "";

        /**
         * PTestStepAction id.
         * @member {string} id
         * @memberof data.PTestStepAction
         * @instance
         */
        PTestStepAction.prototype.id = "";

        /**
         * PTestStepAction timestamp.
         * @member {number|Long} timestamp
         * @memberof data.PTestStepAction
         * @instance
         */
        PTestStepAction.prototype.timestamp = $util.Long ? $util.Long.fromBits(0,0,false) : 0;

        /**
         * PTestStepAction screenshotNames.
         * @member {Array.<string>} screenshotNames
         * @memberof data.PTestStepAction
         * @instance
         */
        PTestStepAction.prototype.screenshotNames = $util.emptyArray;

        /**
         * PTestStepAction clickpathEvents.
         * @member {Array.<data.IPClickPathEvent>} clickpathEvents
         * @memberof data.PTestStepAction
         * @instance
         */
        PTestStepAction.prototype.clickpathEvents = $util.emptyArray;

        /**
         * PTestStepAction screenshotIds.
         * @member {Array.<string>} screenshotIds
         * @memberof data.PTestStepAction
         * @instance
         */
        PTestStepAction.prototype.screenshotIds = $util.emptyArray;

        /**
         * Encodes the specified PTestStepAction message. Does not implicitly {@link data.PTestStepAction.verify|verify} messages.
         * @function encode
         * @memberof data.PTestStepAction
         * @static
         * @param {data.IPTestStepAction} message PTestStepAction message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        PTestStepAction.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.name != null && Object.hasOwnProperty.call(message, "name"))
                writer.uint32(/* id 1, wireType 2 =*/10).string(message.name);
            if (message.id != null && Object.hasOwnProperty.call(message, "id"))
                writer.uint32(/* id 2, wireType 2 =*/18).string(message.id);
            if (message.timestamp != null && Object.hasOwnProperty.call(message, "timestamp"))
                writer.uint32(/* id 3, wireType 0 =*/24).int64(message.timestamp);
            if (message.screenshotNames != null && message.screenshotNames.length)
                for (let i = 0; i < message.screenshotNames.length; ++i)
                    writer.uint32(/* id 4, wireType 2 =*/34).string(message.screenshotNames[i]);
            if (message.clickpathEvents != null && message.clickpathEvents.length)
                for (let i = 0; i < message.clickpathEvents.length; ++i)
                    $root.data.PClickPathEvent.encode(message.clickpathEvents[i], writer.uint32(/* id 5, wireType 2 =*/42).fork()).ldelim();
            if (message.screenshotIds != null && message.screenshotIds.length)
                for (let i = 0; i < message.screenshotIds.length; ++i)
                    writer.uint32(/* id 6, wireType 2 =*/50).string(message.screenshotIds[i]);
            return writer;
        };

        /**
         * Decodes a PTestStepAction message from the specified reader or buffer.
         * @function decode
         * @memberof data.PTestStepAction
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.PTestStepAction} PTestStepAction
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        PTestStepAction.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.PTestStepAction();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.name = reader.string();
                    break;
                case 2:
                    message.id = reader.string();
                    break;
                case 3:
                    message.timestamp = reader.int64();
                    break;
                case 4:
                    if (!(message.screenshotNames && message.screenshotNames.length))
                        message.screenshotNames = [];
                    message.screenshotNames.push(reader.string());
                    break;
                case 5:
                    if (!(message.clickpathEvents && message.clickpathEvents.length))
                        message.clickpathEvents = [];
                    message.clickpathEvents.push($root.data.PClickPathEvent.decode(reader, reader.uint32()));
                    break;
                case 6:
                    if (!(message.screenshotIds && message.screenshotIds.length))
                        message.screenshotIds = [];
                    message.screenshotIds.push(reader.string());
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        return PTestStepAction;
    })();

    /**
     * PClickPathEventType enum.
     * @name data.PClickPathEventType
     * @enum {number}
     * @property {number} NOT_SET=0 NOT_SET value
     * @property {number} WINDOW=1 WINDOW value
     * @property {number} CLICK=2 CLICK value
     * @property {number} VALUE=3 VALUE value
     * @property {number} PAGE=4 PAGE value
     * @property {number} URL=5 URL value
     */
    data.PClickPathEventType = (function() {
        const valuesById = {}, values = Object.create(valuesById);
        values[valuesById[0] = "NOT_SET"] = 0;
        values[valuesById[1] = "WINDOW"] = 1;
        values[valuesById[2] = "CLICK"] = 2;
        values[valuesById[3] = "VALUE"] = 3;
        values[valuesById[4] = "PAGE"] = 4;
        values[valuesById[5] = "URL"] = 5;
        return values;
    })();

    data.PClickPathEvent = (function() {

        /**
         * Properties of a PClickPathEvent.
         * @memberof data
         * @interface IPClickPathEvent
         * @property {data.PClickPathEventType|null} [type] PClickPathEvent type
         * @property {string|null} [subject] PClickPathEvent subject
         * @property {string|null} [sessionId] PClickPathEvent sessionId
         */

        /**
         * Constructs a new PClickPathEvent.
         * @memberof data
         * @classdesc Represents a PClickPathEvent.
         * @implements IPClickPathEvent
         * @constructor
         * @param {data.IPClickPathEvent=} [properties] Properties to set
         */
        function PClickPathEvent(properties) {
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        /**
         * PClickPathEvent type.
         * @member {data.PClickPathEventType} type
         * @memberof data.PClickPathEvent
         * @instance
         */
        PClickPathEvent.prototype.type = 0;

        /**
         * PClickPathEvent subject.
         * @member {string} subject
         * @memberof data.PClickPathEvent
         * @instance
         */
        PClickPathEvent.prototype.subject = "";

        /**
         * PClickPathEvent sessionId.
         * @member {string} sessionId
         * @memberof data.PClickPathEvent
         * @instance
         */
        PClickPathEvent.prototype.sessionId = "";

        /**
         * Encodes the specified PClickPathEvent message. Does not implicitly {@link data.PClickPathEvent.verify|verify} messages.
         * @function encode
         * @memberof data.PClickPathEvent
         * @static
         * @param {data.IPClickPathEvent} message PClickPathEvent message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        PClickPathEvent.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.type != null && Object.hasOwnProperty.call(message, "type"))
                writer.uint32(/* id 1, wireType 0 =*/8).int32(message.type);
            if (message.subject != null && Object.hasOwnProperty.call(message, "subject"))
                writer.uint32(/* id 2, wireType 2 =*/18).string(message.subject);
            if (message.sessionId != null && Object.hasOwnProperty.call(message, "sessionId"))
                writer.uint32(/* id 3, wireType 2 =*/26).string(message.sessionId);
            return writer;
        };

        /**
         * Decodes a PClickPathEvent message from the specified reader or buffer.
         * @function decode
         * @memberof data.PClickPathEvent
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.PClickPathEvent} PClickPathEvent
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        PClickPathEvent.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.PClickPathEvent();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.type = reader.int32();
                    break;
                case 2:
                    message.subject = reader.string();
                    break;
                case 3:
                    message.sessionId = reader.string();
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        return PClickPathEvent;
    })();

    data.ErrorContext = (function() {

        /**
         * Properties of an ErrorContext.
         * @memberof data
         * @interface IErrorContext
         * @property {string|null} [readableErrorMessage] ErrorContext readableErrorMessage
         * @property {string|null} [additionalErrorMessage] ErrorContext additionalErrorMessage
         * @property {data.IStackTrace|null} [stackTrace] ErrorContext stackTrace
         * @property {string|null} [errorFingerprint] ErrorContext errorFingerprint
         * @property {data.IScriptSource|null} [scriptSource] ErrorContext scriptSource
         * @property {data.IScriptSource|null} [executionObjectSource] ErrorContext executionObjectSource
         */

        /**
         * Constructs a new ErrorContext.
         * @memberof data
         * @classdesc Represents an ErrorContext.
         * @implements IErrorContext
         * @constructor
         * @param {data.IErrorContext=} [properties] Properties to set
         */
        function ErrorContext(properties) {
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        /**
         * ErrorContext readableErrorMessage.
         * @member {string} readableErrorMessage
         * @memberof data.ErrorContext
         * @instance
         */
        ErrorContext.prototype.readableErrorMessage = "";

        /**
         * ErrorContext additionalErrorMessage.
         * @member {string} additionalErrorMessage
         * @memberof data.ErrorContext
         * @instance
         */
        ErrorContext.prototype.additionalErrorMessage = "";

        /**
         * ErrorContext stackTrace.
         * @member {data.IStackTrace|null|undefined} stackTrace
         * @memberof data.ErrorContext
         * @instance
         */
        ErrorContext.prototype.stackTrace = null;

        /**
         * ErrorContext errorFingerprint.
         * @member {string} errorFingerprint
         * @memberof data.ErrorContext
         * @instance
         */
        ErrorContext.prototype.errorFingerprint = "";

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
         * Encodes the specified ErrorContext message. Does not implicitly {@link data.ErrorContext.verify|verify} messages.
         * @function encode
         * @memberof data.ErrorContext
         * @static
         * @param {data.IErrorContext} message ErrorContext message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        ErrorContext.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.readableErrorMessage != null && Object.hasOwnProperty.call(message, "readableErrorMessage"))
                writer.uint32(/* id 1, wireType 2 =*/10).string(message.readableErrorMessage);
            if (message.additionalErrorMessage != null && Object.hasOwnProperty.call(message, "additionalErrorMessage"))
                writer.uint32(/* id 2, wireType 2 =*/18).string(message.additionalErrorMessage);
            if (message.stackTrace != null && Object.hasOwnProperty.call(message, "stackTrace"))
                $root.data.StackTrace.encode(message.stackTrace, writer.uint32(/* id 3, wireType 2 =*/26).fork()).ldelim();
            if (message.errorFingerprint != null && Object.hasOwnProperty.call(message, "errorFingerprint"))
                writer.uint32(/* id 6, wireType 2 =*/50).string(message.errorFingerprint);
            if (message.scriptSource != null && Object.hasOwnProperty.call(message, "scriptSource"))
                $root.data.ScriptSource.encode(message.scriptSource, writer.uint32(/* id 7, wireType 2 =*/58).fork()).ldelim();
            if (message.executionObjectSource != null && Object.hasOwnProperty.call(message, "executionObjectSource"))
                $root.data.ScriptSource.encode(message.executionObjectSource, writer.uint32(/* id 8, wireType 2 =*/66).fork()).ldelim();
            return writer;
        };

        /**
         * Decodes an ErrorContext message from the specified reader or buffer.
         * @function decode
         * @memberof data.ErrorContext
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.ErrorContext} ErrorContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        ErrorContext.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.ErrorContext();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.readableErrorMessage = reader.string();
                    break;
                case 2:
                    message.additionalErrorMessage = reader.string();
                    break;
                case 3:
                    message.stackTrace = $root.data.StackTrace.decode(reader, reader.uint32());
                    break;
                case 6:
                    message.errorFingerprint = reader.string();
                    break;
                case 7:
                    message.scriptSource = $root.data.ScriptSource.decode(reader, reader.uint32());
                    break;
                case 8:
                    message.executionObjectSource = $root.data.ScriptSource.decode(reader, reader.uint32());
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
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
         * @param {data.ISessionContext=} [properties] Properties to set
         */
        function SessionContext(properties) {
            this.metadata = {};
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
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
         * Encodes the specified SessionContext message. Does not implicitly {@link data.SessionContext.verify|verify} messages.
         * @function encode
         * @memberof data.SessionContext
         * @static
         * @param {data.ISessionContext} message SessionContext message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        SessionContext.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.contextValues != null && Object.hasOwnProperty.call(message, "contextValues"))
                $root.data.ContextValues.encode(message.contextValues, writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
            if (message.sessionKey != null && Object.hasOwnProperty.call(message, "sessionKey"))
                writer.uint32(/* id 2, wireType 2 =*/18).string(message.sessionKey);
            if (message.provider != null && Object.hasOwnProperty.call(message, "provider"))
                writer.uint32(/* id 3, wireType 2 =*/26).string(message.provider);
            if (message.metadata != null && Object.hasOwnProperty.call(message, "metadata"))
                for (let keys = Object.keys(message.metadata), i = 0; i < keys.length; ++i)
                    writer.uint32(/* id 4, wireType 2 =*/34).fork().uint32(/* id 1, wireType 2 =*/10).string(keys[i]).uint32(/* id 2, wireType 2 =*/18).string(message.metadata[keys[i]]).ldelim();
            if (message.sessionId != null && Object.hasOwnProperty.call(message, "sessionId"))
                writer.uint32(/* id 6, wireType 2 =*/50).string(message.sessionId);
            return writer;
        };

        /**
         * Decodes a SessionContext message from the specified reader or buffer.
         * @function decode
         * @memberof data.SessionContext
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.SessionContext} SessionContext
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        SessionContext.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.SessionContext(), key, value;
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.contextValues = $root.data.ContextValues.decode(reader, reader.uint32());
                    break;
                case 2:
                    message.sessionKey = reader.string();
                    break;
                case 3:
                    message.provider = reader.string();
                    break;
                case 4:
                    if (message.metadata === $util.emptyObject)
                        message.metadata = {};
                    let end2 = reader.uint32() + reader.pos;
                    key = "";
                    value = "";
                    while (reader.pos < end2) {
                        let tag2 = reader.uint32();
                        switch (tag2 >>> 3) {
                        case 1:
                            key = reader.string();
                            break;
                        case 2:
                            value = reader.string();
                            break;
                        default:
                            reader.skipType(tag2 & 7);
                            break;
                        }
                    }
                    message.metadata[key] = value;
                    break;
                case 6:
                    message.sessionId = reader.string();
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
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
         * @param {data.IRunConfig=} [properties] Properties to set
         */
        function RunConfig(properties) {
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
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
         * Encodes the specified RunConfig message. Does not implicitly {@link data.RunConfig.verify|verify} messages.
         * @function encode
         * @memberof data.RunConfig
         * @static
         * @param {data.IRunConfig} message RunConfig message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        RunConfig.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.runcfg != null && Object.hasOwnProperty.call(message, "runcfg"))
                writer.uint32(/* id 1, wireType 2 =*/10).string(message.runcfg);
            if (message.buildInformation != null && Object.hasOwnProperty.call(message, "buildInformation"))
                $root.data.BuildInformation.encode(message.buildInformation, writer.uint32(/* id 2, wireType 2 =*/18).fork()).ldelim();
            if (message.reportName != null && Object.hasOwnProperty.call(message, "reportName"))
                writer.uint32(/* id 3, wireType 2 =*/26).string(message.reportName);
            return writer;
        };

        /**
         * Decodes a RunConfig message from the specified reader or buffer.
         * @function decode
         * @memberof data.RunConfig
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.RunConfig} RunConfig
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        RunConfig.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.RunConfig();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.runcfg = reader.string();
                    break;
                case 2:
                    message.buildInformation = $root.data.BuildInformation.decode(reader, reader.uint32());
                    break;
                case 3:
                    message.reportName = reader.string();
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
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
         * @param {data.IBuildInformation=} [properties] Properties to set
         */
        function BuildInformation(properties) {
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
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
         * Encodes the specified BuildInformation message. Does not implicitly {@link data.BuildInformation.verify|verify} messages.
         * @function encode
         * @memberof data.BuildInformation
         * @static
         * @param {data.IBuildInformation} message BuildInformation message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        BuildInformation.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.buildJavaVersion != null && Object.hasOwnProperty.call(message, "buildJavaVersion"))
                writer.uint32(/* id 1, wireType 2 =*/10).string(message.buildJavaVersion);
            if (message.buildOsName != null && Object.hasOwnProperty.call(message, "buildOsName"))
                writer.uint32(/* id 2, wireType 2 =*/18).string(message.buildOsName);
            if (message.buildOsVersion != null && Object.hasOwnProperty.call(message, "buildOsVersion"))
                writer.uint32(/* id 3, wireType 2 =*/26).string(message.buildOsVersion);
            if (message.buildUserName != null && Object.hasOwnProperty.call(message, "buildUserName"))
                writer.uint32(/* id 4, wireType 2 =*/34).string(message.buildUserName);
            if (message.buildVersion != null && Object.hasOwnProperty.call(message, "buildVersion"))
                writer.uint32(/* id 5, wireType 2 =*/42).string(message.buildVersion);
            if (message.buildTimestamp != null && Object.hasOwnProperty.call(message, "buildTimestamp"))
                writer.uint32(/* id 6, wireType 2 =*/50).string(message.buildTimestamp);
            return writer;
        };

        /**
         * Decodes a BuildInformation message from the specified reader or buffer.
         * @function decode
         * @memberof data.BuildInformation
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.BuildInformation} BuildInformation
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        BuildInformation.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.BuildInformation();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.buildJavaVersion = reader.string();
                    break;
                case 2:
                    message.buildOsName = reader.string();
                    break;
                case 3:
                    message.buildOsVersion = reader.string();
                    break;
                case 4:
                    message.buildUserName = reader.string();
                    break;
                case 5:
                    message.buildVersion = reader.string();
                    break;
                case 6:
                    message.buildTimestamp = reader.string();
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        return BuildInformation;
    })();

    /**
     * FailureCorridorValue enum.
     * @name data.FailureCorridorValue
     * @enum {number}
     * @property {number} FCV_NOT_SET=0 FCV_NOT_SET value
     * @property {number} HIGH=1 HIGH value
     * @property {number} MID=2 MID value
     * @property {number} LOW=3 LOW value
     */
    data.FailureCorridorValue = (function() {
        const valuesById = {}, values = Object.create(valuesById);
        values[valuesById[0] = "FCV_NOT_SET"] = 0;
        values[valuesById[1] = "HIGH"] = 1;
        values[valuesById[2] = "MID"] = 2;
        values[valuesById[3] = "LOW"] = 3;
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

    data.StackTrace = (function() {

        /**
         * Properties of a StackTrace.
         * @memberof data
         * @interface IStackTrace
         * @property {data.IStackTraceCause|null} [cause] StackTrace cause
         * @property {string|null} [additionalErrorMessage] StackTrace additionalErrorMessage
         */

        /**
         * Constructs a new StackTrace.
         * @memberof data
         * @classdesc Represents a StackTrace.
         * @implements IStackTrace
         * @constructor
         * @param {data.IStackTrace=} [properties] Properties to set
         */
        function StackTrace(properties) {
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        /**
         * StackTrace cause.
         * @member {data.IStackTraceCause|null|undefined} cause
         * @memberof data.StackTrace
         * @instance
         */
        StackTrace.prototype.cause = null;

        /**
         * StackTrace additionalErrorMessage.
         * @member {string} additionalErrorMessage
         * @memberof data.StackTrace
         * @instance
         */
        StackTrace.prototype.additionalErrorMessage = "";

        /**
         * Encodes the specified StackTrace message. Does not implicitly {@link data.StackTrace.verify|verify} messages.
         * @function encode
         * @memberof data.StackTrace
         * @static
         * @param {data.IStackTrace} message StackTrace message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        StackTrace.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.cause != null && Object.hasOwnProperty.call(message, "cause"))
                $root.data.StackTraceCause.encode(message.cause, writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
            if (message.additionalErrorMessage != null && Object.hasOwnProperty.call(message, "additionalErrorMessage"))
                writer.uint32(/* id 2, wireType 2 =*/18).string(message.additionalErrorMessage);
            return writer;
        };

        /**
         * Decodes a StackTrace message from the specified reader or buffer.
         * @function decode
         * @memberof data.StackTrace
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.StackTrace} StackTrace
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        StackTrace.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.StackTrace();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.cause = $root.data.StackTraceCause.decode(reader, reader.uint32());
                    break;
                case 2:
                    message.additionalErrorMessage = reader.string();
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        return StackTrace;
    })();

    data.StackTraceCause = (function() {

        /**
         * Properties of a StackTraceCause.
         * @memberof data
         * @interface IStackTraceCause
         * @property {string|null} [className] StackTraceCause className
         * @property {string|null} [message] StackTraceCause message
         * @property {Array.<string>|null} [stackTraceElements] StackTraceCause stackTraceElements
         * @property {data.IStackTraceCause|null} [cause] StackTraceCause cause
         */

        /**
         * Constructs a new StackTraceCause.
         * @memberof data
         * @classdesc Represents a StackTraceCause.
         * @implements IStackTraceCause
         * @constructor
         * @param {data.IStackTraceCause=} [properties] Properties to set
         */
        function StackTraceCause(properties) {
            this.stackTraceElements = [];
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
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
         * StackTraceCause cause.
         * @member {data.IStackTraceCause|null|undefined} cause
         * @memberof data.StackTraceCause
         * @instance
         */
        StackTraceCause.prototype.cause = null;

        /**
         * Encodes the specified StackTraceCause message. Does not implicitly {@link data.StackTraceCause.verify|verify} messages.
         * @function encode
         * @memberof data.StackTraceCause
         * @static
         * @param {data.IStackTraceCause} message StackTraceCause message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        StackTraceCause.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.className != null && Object.hasOwnProperty.call(message, "className"))
                writer.uint32(/* id 1, wireType 2 =*/10).string(message.className);
            if (message.message != null && Object.hasOwnProperty.call(message, "message"))
                writer.uint32(/* id 2, wireType 2 =*/18).string(message.message);
            if (message.stackTraceElements != null && message.stackTraceElements.length)
                for (let i = 0; i < message.stackTraceElements.length; ++i)
                    writer.uint32(/* id 3, wireType 2 =*/26).string(message.stackTraceElements[i]);
            if (message.cause != null && Object.hasOwnProperty.call(message, "cause"))
                $root.data.StackTraceCause.encode(message.cause, writer.uint32(/* id 4, wireType 2 =*/34).fork()).ldelim();
            return writer;
        };

        /**
         * Decodes a StackTraceCause message from the specified reader or buffer.
         * @function decode
         * @memberof data.StackTraceCause
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.StackTraceCause} StackTraceCause
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        StackTraceCause.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.StackTraceCause();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.className = reader.string();
                    break;
                case 2:
                    message.message = reader.string();
                    break;
                case 3:
                    if (!(message.stackTraceElements && message.stackTraceElements.length))
                        message.stackTraceElements = [];
                    message.stackTraceElements.push(reader.string());
                    break;
                case 4:
                    message.cause = $root.data.StackTraceCause.decode(reader, reader.uint32());
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
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
         * @param {data.IScriptSource=} [properties] Properties to set
         */
        function ScriptSource(properties) {
            this.lines = [];
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
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
         * Encodes the specified ScriptSource message. Does not implicitly {@link data.ScriptSource.verify|verify} messages.
         * @function encode
         * @memberof data.ScriptSource
         * @static
         * @param {data.IScriptSource} message ScriptSource message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        ScriptSource.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.fileName != null && Object.hasOwnProperty.call(message, "fileName"))
                writer.uint32(/* id 1, wireType 2 =*/10).string(message.fileName);
            if (message.methodName != null && Object.hasOwnProperty.call(message, "methodName"))
                writer.uint32(/* id 2, wireType 2 =*/18).string(message.methodName);
            if (message.lines != null && message.lines.length)
                for (let i = 0; i < message.lines.length; ++i)
                    $root.data.ScriptSourceLine.encode(message.lines[i], writer.uint32(/* id 3, wireType 2 =*/26).fork()).ldelim();
            return writer;
        };

        /**
         * Decodes a ScriptSource message from the specified reader or buffer.
         * @function decode
         * @memberof data.ScriptSource
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.ScriptSource} ScriptSource
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        ScriptSource.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.ScriptSource();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.fileName = reader.string();
                    break;
                case 2:
                    message.methodName = reader.string();
                    break;
                case 3:
                    if (!(message.lines && message.lines.length))
                        message.lines = [];
                    message.lines.push($root.data.ScriptSourceLine.decode(reader, reader.uint32()));
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
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
         * @param {data.IScriptSourceLine=} [properties] Properties to set
         */
        function ScriptSourceLine(properties) {
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
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
         * Encodes the specified ScriptSourceLine message. Does not implicitly {@link data.ScriptSourceLine.verify|verify} messages.
         * @function encode
         * @memberof data.ScriptSourceLine
         * @static
         * @param {data.IScriptSourceLine} message ScriptSourceLine message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        ScriptSourceLine.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.line != null && Object.hasOwnProperty.call(message, "line"))
                writer.uint32(/* id 1, wireType 2 =*/10).string(message.line);
            if (message.lineNumber != null && Object.hasOwnProperty.call(message, "lineNumber"))
                writer.uint32(/* id 2, wireType 0 =*/16).int32(message.lineNumber);
            if (message.mark != null && Object.hasOwnProperty.call(message, "mark"))
                writer.uint32(/* id 3, wireType 0 =*/24).bool(message.mark);
            return writer;
        };

        /**
         * Decodes a ScriptSourceLine message from the specified reader or buffer.
         * @function decode
         * @memberof data.ScriptSourceLine
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.ScriptSourceLine} ScriptSourceLine
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        ScriptSourceLine.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.ScriptSourceLine();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.line = reader.string();
                    break;
                case 2:
                    message.lineNumber = reader.int32();
                    break;
                case 3:
                    message.mark = reader.bool();
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        return ScriptSourceLine;
    })();

    data.File = (function() {

        /**
         * Properties of a File.
         * @memberof data
         * @interface IFile
         * @property {string|null} [id] File id
         * @property {number|Long|null} [size] File size
         * @property {string|null} [mimetype] File mimetype
         * @property {string|null} [relativePath] File relativePath
         * @property {number|Long|null} [createdTimestamp] File createdTimestamp
         * @property {Uint8Array|null} [sha1Checksum] File sha1Checksum
         * @property {Object.<string,string>|null} [meta] File meta
         * @property {number|Long|null} [lastModified] File lastModified
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
         * @param {data.IFile=} [properties] Properties to set
         */
        function File(properties) {
            this.meta = {};
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
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
         * @member {number|Long} size
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
         * @member {number|Long} createdTimestamp
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
         * @member {number|Long} lastModified
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
         * Encodes the specified File message. Does not implicitly {@link data.File.verify|verify} messages.
         * @function encode
         * @memberof data.File
         * @static
         * @param {data.IFile} message File message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        File.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.id != null && Object.hasOwnProperty.call(message, "id"))
                writer.uint32(/* id 1, wireType 2 =*/10).string(message.id);
            if (message.size != null && Object.hasOwnProperty.call(message, "size"))
                writer.uint32(/* id 2, wireType 0 =*/16).int64(message.size);
            if (message.mimetype != null && Object.hasOwnProperty.call(message, "mimetype"))
                writer.uint32(/* id 3, wireType 2 =*/26).string(message.mimetype);
            if (message.relativePath != null && Object.hasOwnProperty.call(message, "relativePath"))
                writer.uint32(/* id 4, wireType 2 =*/34).string(message.relativePath);
            if (message.createdTimestamp != null && Object.hasOwnProperty.call(message, "createdTimestamp"))
                writer.uint32(/* id 5, wireType 0 =*/40).int64(message.createdTimestamp);
            if (message.sha1Checksum != null && Object.hasOwnProperty.call(message, "sha1Checksum"))
                writer.uint32(/* id 6, wireType 2 =*/50).bytes(message.sha1Checksum);
            if (message.meta != null && Object.hasOwnProperty.call(message, "meta"))
                for (let keys = Object.keys(message.meta), i = 0; i < keys.length; ++i)
                    writer.uint32(/* id 7, wireType 2 =*/58).fork().uint32(/* id 1, wireType 2 =*/10).string(keys[i]).uint32(/* id 2, wireType 2 =*/18).string(message.meta[keys[i]]).ldelim();
            if (message.lastModified != null && Object.hasOwnProperty.call(message, "lastModified"))
                writer.uint32(/* id 9, wireType 0 =*/72).int64(message.lastModified);
            if (message.projectId != null && Object.hasOwnProperty.call(message, "projectId"))
                writer.uint32(/* id 10, wireType 2 =*/82).string(message.projectId);
            if (message.jobId != null && Object.hasOwnProperty.call(message, "jobId"))
                writer.uint32(/* id 11, wireType 2 =*/90).string(message.jobId);
            if (message.isDirectory != null && Object.hasOwnProperty.call(message, "isDirectory"))
                writer.uint32(/* id 12, wireType 0 =*/96).bool(message.isDirectory);
            if (message.name != null && Object.hasOwnProperty.call(message, "name"))
                writer.uint32(/* id 13, wireType 2 =*/106).string(message.name);
            return writer;
        };

        /**
         * Decodes a File message from the specified reader or buffer.
         * @function decode
         * @memberof data.File
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.File} File
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        File.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.File(), key, value;
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.id = reader.string();
                    break;
                case 2:
                    message.size = reader.int64();
                    break;
                case 3:
                    message.mimetype = reader.string();
                    break;
                case 4:
                    message.relativePath = reader.string();
                    break;
                case 5:
                    message.createdTimestamp = reader.int64();
                    break;
                case 6:
                    message.sha1Checksum = reader.bytes();
                    break;
                case 7:
                    if (message.meta === $util.emptyObject)
                        message.meta = {};
                    let end2 = reader.uint32() + reader.pos;
                    key = "";
                    value = "";
                    while (reader.pos < end2) {
                        let tag2 = reader.uint32();
                        switch (tag2 >>> 3) {
                        case 1:
                            key = reader.string();
                            break;
                        case 2:
                            value = reader.string();
                            break;
                        default:
                            reader.skipType(tag2 & 7);
                            break;
                        }
                    }
                    message.meta[key] = value;
                    break;
                case 9:
                    message.lastModified = reader.int64();
                    break;
                case 10:
                    message.projectId = reader.string();
                    break;
                case 11:
                    message.jobId = reader.string();
                    break;
                case 12:
                    message.isDirectory = reader.bool();
                    break;
                case 13:
                    message.name = reader.string();
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
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

    data.ClassContextAggregate = (function() {

        /**
         * Properties of a ClassContextAggregate.
         * @memberof data
         * @interface IClassContextAggregate
         * @property {data.IClassContext|null} [classContext] ClassContextAggregate classContext
         * @property {Array.<data.IMethodContext>|null} [methodContexts] ClassContextAggregate methodContexts
         */

        /**
         * Constructs a new ClassContextAggregate.
         * @memberof data
         * @classdesc Represents a ClassContextAggregate.
         * @implements IClassContextAggregate
         * @constructor
         * @param {data.IClassContextAggregate=} [properties] Properties to set
         */
        function ClassContextAggregate(properties) {
            this.methodContexts = [];
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        /**
         * ClassContextAggregate classContext.
         * @member {data.IClassContext|null|undefined} classContext
         * @memberof data.ClassContextAggregate
         * @instance
         */
        ClassContextAggregate.prototype.classContext = null;

        /**
         * ClassContextAggregate methodContexts.
         * @member {Array.<data.IMethodContext>} methodContexts
         * @memberof data.ClassContextAggregate
         * @instance
         */
        ClassContextAggregate.prototype.methodContexts = $util.emptyArray;

        /**
         * Encodes the specified ClassContextAggregate message. Does not implicitly {@link data.ClassContextAggregate.verify|verify} messages.
         * @function encode
         * @memberof data.ClassContextAggregate
         * @static
         * @param {data.IClassContextAggregate} message ClassContextAggregate message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        ClassContextAggregate.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.classContext != null && Object.hasOwnProperty.call(message, "classContext"))
                $root.data.ClassContext.encode(message.classContext, writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
            if (message.methodContexts != null && message.methodContexts.length)
                for (let i = 0; i < message.methodContexts.length; ++i)
                    $root.data.MethodContext.encode(message.methodContexts[i], writer.uint32(/* id 2, wireType 2 =*/18).fork()).ldelim();
            return writer;
        };

        /**
         * Decodes a ClassContextAggregate message from the specified reader or buffer.
         * @function decode
         * @memberof data.ClassContextAggregate
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {data.ClassContextAggregate} ClassContextAggregate
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        ClassContextAggregate.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.ClassContextAggregate();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.classContext = $root.data.ClassContext.decode(reader, reader.uint32());
                    break;
                case 2:
                    if (!(message.methodContexts && message.methodContexts.length))
                        message.methodContexts = [];
                    message.methodContexts.push($root.data.MethodContext.decode(reader, reader.uint32()));
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        return ClassContextAggregate;
    })();

    return data;
})();

export { $root as default };
