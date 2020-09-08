/*eslint-disable block-scoped-var, id-length, no-control-regex, no-magic-numbers, no-prototype-builtins, no-redeclare, no-shadow, no-var, sort-vars*/
"use strict";

var $protobuf = require("protobufjs/minimal");

var $Reader = $protobuf.Reader, $util = $protobuf.util;

var $root = $protobuf.roots["default"] || ($protobuf.roots["default"] = {});

$root.data = (function() {

    var data = {};

    data.SuiteContext = (function() {

        function SuiteContext(properties) {
            this.testContextIds = [];
            if (properties)
                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        SuiteContext.prototype.contextValues = null;
        SuiteContext.prototype.testContextIds = $util.emptyArray;
        SuiteContext.prototype.executionContextId = "";

        SuiteContext.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.SuiteContext();
            while (reader.pos < end) {
                var tag = reader.uint32();
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

        function ClassContext(properties) {
            this.methodContextIds = [];
            if (properties)
                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        ClassContext.prototype.contextValues = null;
        ClassContext.prototype.methodContextIds = $util.emptyArray;
        ClassContext.prototype.fullClassName = "";
        ClassContext.prototype.simpleClassName = "";
        ClassContext.prototype.testContextId = "";
        ClassContext.prototype.executionContextId = "";
        ClassContext.prototype.merged = false;

        ClassContext.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.ClassContext();
            while (reader.pos < end) {
                var tag = reader.uint32();
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

        function TestContext(properties) {
            this.classContextIds = [];
            if (properties)
                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        TestContext.prototype.contextValues = null;
        TestContext.prototype.classContextIds = $util.emptyArray;
        TestContext.prototype.suiteContextId = "";
        TestContext.prototype.executionContextId = "";

        TestContext.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.TestContext();
            while (reader.pos < end) {
                var tag = reader.uint32();
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

        function ExecutionContext(properties) {
            this.mergedClassContextIds = [];
            this.exitPoints = [];
            this.failureAscpects = [];
            this.suiteContextIds = [];
            this.exclusiveSessionContextIds = [];
            if (properties)
                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        ExecutionContext.prototype.contextValues = null;
        ExecutionContext.prototype.mergedClassContextIds = $util.emptyArray;
        ExecutionContext.prototype.exitPoints = $util.emptyArray;
        ExecutionContext.prototype.failureAscpects = $util.emptyArray;
        ExecutionContext.prototype.suiteContextIds = $util.emptyArray;
        ExecutionContext.prototype.runConfig = null;
        ExecutionContext.prototype.project_Id = "";
        ExecutionContext.prototype.job_Id = "";
        ExecutionContext.prototype.run_Id = "";
        ExecutionContext.prototype.task_Id = "";
        ExecutionContext.prototype.exclusiveSessionContextIds = $util.emptyArray;
        ExecutionContext.prototype.estimatedTestMethodCount = 0;

        ExecutionContext.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.ExecutionContext();
            while (reader.pos < end) {
                var tag = reader.uint32();
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

        function ContextClip(properties) {
            this.methodContextIds = [];
            if (properties)
                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        ContextClip.prototype.key = "";
        ContextClip.prototype.methodContextIds = $util.emptyArray;

        ContextClip.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.ContextClip();
            while (reader.pos < end) {
                var tag = reader.uint32();
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
                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        MethodContext.prototype.contextValues = null;
        MethodContext.prototype.methodType = 0;
        MethodContext.prototype.parameters = $util.emptyArray;
        MethodContext.prototype.methodTags = $util.emptyArray;
        MethodContext.prototype.retryNumber = 0;
        MethodContext.prototype.methodRunIndex = 0;
        MethodContext.prototype.threadName = "";
        MethodContext.prototype.failureCorridorValue = 0;
        MethodContext.prototype.classContextId = "";
        MethodContext.prototype.executionContextId = "";
        MethodContext.prototype.nonFunctionalInfos = $util.emptyArray;
        MethodContext.prototype.collectedAssertions = $util.emptyArray;
        MethodContext.prototype.infos = $util.emptyArray;
        MethodContext.prototype.priorityMessage = "";
        MethodContext.prototype.relatedMethodContextIds = $util.emptyArray;
        MethodContext.prototype.dependsOnMethodContextIds = $util.emptyArray;
        MethodContext.prototype.errorContext = null;
        MethodContext.prototype.testSteps = $util.emptyArray;
        MethodContext.prototype.testContextId = "";
        MethodContext.prototype.suiteContextId = "";
        MethodContext.prototype.sessionContextIds = $util.emptyArray;
        MethodContext.prototype.videoIds = $util.emptyArray;
        MethodContext.prototype.screenshotIds = $util.emptyArray;
        MethodContext.prototype.customContextJson = "";

        MethodContext.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.MethodContext();
            while (reader.pos < end) {
                var tag = reader.uint32();
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

        function ContextValues(properties) {
            if (properties)
                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        ContextValues.prototype.id = "";
        ContextValues.prototype.created = $util.Long ? $util.Long.fromBits(0,0,false) : 0;
        ContextValues.prototype.name = "";
        ContextValues.prototype.startTime = $util.Long ? $util.Long.fromBits(0,0,false) : 0;
        ContextValues.prototype.endTime = $util.Long ? $util.Long.fromBits(0,0,false) : 0;
        ContextValues.prototype.swi = "";
        ContextValues.prototype.resultStatus = 0;
        ContextValues.prototype.execStatus = 0;

        ContextValues.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.ContextValues();
            while (reader.pos < end) {
                var tag = reader.uint32();
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

        function PTestStep(properties) {
            this.testStepActions = [];
            if (properties)
                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        PTestStep.prototype.name = "";
        PTestStep.prototype.id = "";
        PTestStep.prototype.testStepActions = $util.emptyArray;

        PTestStep.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.PTestStep();
            while (reader.pos < end) {
                var tag = reader.uint32();
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

        function PTestStepAction(properties) {
            this.screenshotNames = [];
            this.clickpathEvents = [];
            if (properties)
                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        PTestStepAction.prototype.name = "";
        PTestStepAction.prototype.id = "";
        PTestStepAction.prototype.timestamp = $util.Long ? $util.Long.fromBits(0,0,false) : 0;
        PTestStepAction.prototype.screenshotNames = $util.emptyArray;
        PTestStepAction.prototype.clickpathEvents = $util.emptyArray;

        PTestStepAction.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.PTestStepAction();
            while (reader.pos < end) {
                var tag = reader.uint32();
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
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        return PTestStepAction;
    })();

    data.PClickPathEventType = (function() {
        var valuesById = {}, values = Object.create(valuesById);
        values[valuesById[0] = "NOT_SET"] = 0;
        values[valuesById[1] = "WINDOW"] = 1;
        values[valuesById[2] = "CLICK"] = 2;
        values[valuesById[3] = "VALUE"] = 3;
        values[valuesById[4] = "PAGE"] = 4;
        values[valuesById[5] = "URL"] = 5;
        return values;
    })();

    data.PClickPathEvent = (function() {

        function PClickPathEvent(properties) {
            if (properties)
                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        PClickPathEvent.prototype.type = 0;
        PClickPathEvent.prototype.subject = "";
        PClickPathEvent.prototype.sessionId = "";

        PClickPathEvent.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.PClickPathEvent();
            while (reader.pos < end) {
                var tag = reader.uint32();
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

        function ErrorContext(properties) {
            if (properties)
                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        ErrorContext.prototype.readableErrorMessage = "";
        ErrorContext.prototype.additionalErrorMessage = "";
        ErrorContext.prototype.stackTrace = null;
        ErrorContext.prototype.errorFingerprint = "";
        ErrorContext.prototype.scriptSource = null;
        ErrorContext.prototype.executionObjectSource = null;

        ErrorContext.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.ErrorContext();
            while (reader.pos < end) {
                var tag = reader.uint32();
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

        function SessionContext(properties) {
            this.metadata = {};
            if (properties)
                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        SessionContext.prototype.contextValues = null;
        SessionContext.prototype.sessionKey = "";
        SessionContext.prototype.provider = "";
        SessionContext.prototype.metadata = $util.emptyObject;
        SessionContext.prototype.sessionId = "";

        SessionContext.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.SessionContext(), key, value;
            while (reader.pos < end) {
                var tag = reader.uint32();
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
                    var end2 = reader.uint32() + reader.pos;
                    key = "";
                    value = "";
                    while (reader.pos < end2) {
                        var tag2 = reader.uint32();
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

        function RunConfig(properties) {
            if (properties)
                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        RunConfig.prototype.runcfg = "";
        RunConfig.prototype.testerraBuildInformation = null;
        RunConfig.prototype.reportName = "";

        RunConfig.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.RunConfig();
            while (reader.pos < end) {
                var tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.runcfg = reader.string();
                    break;
                case 2:
                    message.testerraBuildInformation = $root.data.BuildInformation.decode(reader, reader.uint32());
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

        function BuildInformation(properties) {
            if (properties)
                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        BuildInformation.prototype.buildJavaVersion = "";
        BuildInformation.prototype.buildOsName = "";
        BuildInformation.prototype.buildOsVersion = "";
        BuildInformation.prototype.buildUserName = "";
        BuildInformation.prototype.buildVersion = "";
        BuildInformation.prototype.buildTimestamp = "";

        BuildInformation.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.BuildInformation();
            while (reader.pos < end) {
                var tag = reader.uint32();
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

    data.FailureCorridorValue = (function() {
        var valuesById = {}, values = Object.create(valuesById);
        values[valuesById[0] = "FCV_NOT_SET"] = 0;
        values[valuesById[1] = "HIGH"] = 1;
        values[valuesById[2] = "MID"] = 2;
        values[valuesById[3] = "LOW"] = 3;
        return values;
    })();

    data.MethodType = (function() {
        var valuesById = {}, values = Object.create(valuesById);
        values[valuesById[0] = "MT_NOT_SET"] = 0;
        values[valuesById[1] = "TEST_METHOD"] = 1;
        values[valuesById[2] = "CONFIGURATION_METHOD"] = 2;
        return values;
    })();

    data.StackTrace = (function() {

        function StackTrace(properties) {
            if (properties)
                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        StackTrace.prototype.cause = null;
        StackTrace.prototype.additionalErrorMessage = "";

        StackTrace.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.StackTrace();
            while (reader.pos < end) {
                var tag = reader.uint32();
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

        function StackTraceCause(properties) {
            this.stackTraceElements = [];
            if (properties)
                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        StackTraceCause.prototype.className = "";
        StackTraceCause.prototype.message = "";
        StackTraceCause.prototype.stackTraceElements = $util.emptyArray;
        StackTraceCause.prototype.cause = null;

        StackTraceCause.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.StackTraceCause();
            while (reader.pos < end) {
                var tag = reader.uint32();
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

        function ScriptSource(properties) {
            this.lines = [];
            if (properties)
                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        ScriptSource.prototype.fileName = "";
        ScriptSource.prototype.methodName = "";
        ScriptSource.prototype.lines = $util.emptyArray;

        ScriptSource.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.ScriptSource();
            while (reader.pos < end) {
                var tag = reader.uint32();
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

        function ScriptSourceLine(properties) {
            if (properties)
                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        ScriptSourceLine.prototype.line = "";
        ScriptSourceLine.prototype.lineNumber = 0;
        ScriptSourceLine.prototype.mark = false;

        ScriptSourceLine.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.ScriptSourceLine();
            while (reader.pos < end) {
                var tag = reader.uint32();
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

        function File(properties) {
            this.meta = {};
            if (properties)
                for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        File.prototype.id = "";
        File.prototype.size = $util.Long ? $util.Long.fromBits(0,0,false) : 0;
        File.prototype.mimetype = "";
        File.prototype.relativePath = "";
        File.prototype.createdTimestamp = $util.Long ? $util.Long.fromBits(0,0,false) : 0;
        File.prototype.sha1Checksum = $util.newBuffer([]);
        File.prototype.meta = $util.emptyObject;
        File.prototype.lastModified = $util.Long ? $util.Long.fromBits(0,0,false) : 0;
        File.prototype.projectId = "";
        File.prototype.jobId = "";
        File.prototype.isDirectory = false;
        File.prototype.name = "";

        File.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            var end = length === undefined ? reader.len : reader.pos + length, message = new $root.data.File(), key, value;
            while (reader.pos < end) {
                var tag = reader.uint32();
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
                    var end2 = reader.uint32() + reader.pos;
                    key = "";
                    value = "";
                    while (reader.pos < end2) {
                        var tag2 = reader.uint32();
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

    data.ExecStatusType = (function() {
        var valuesById = {}, values = Object.create(valuesById);
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

    data.ResultStatusType = (function() {
        var valuesById = {}, values = Object.create(valuesById);
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

    return data;
})();

module.exports = $root;
