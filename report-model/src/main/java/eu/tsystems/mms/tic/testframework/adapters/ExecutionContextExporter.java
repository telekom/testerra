/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     erku <Eric.Kubenka@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.adapters;

import eu.tsystems.mms.tic.testframework.report.model.ContextClip;
import eu.tsystems.mms.tic.testframework.report.model.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.RunConfig;
import eu.tsystems.mms.tic.testframework.report.model.TesterraBuildInformation;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

final class ExecutionContextExporter extends ContextExporter {

    public ExecutionContext.Builder prepareExecutionContext(eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext executionContext) {

        final ExecutionContext.Builder builder = ExecutionContext.newBuilder();

        value(createContextValues(executionContext), builder::setContextValues);

        valueList(executionContext.suiteContexts, s -> s.id, builder::addAllSuiteContextIds);
        valueList(executionContext.mergedClassContexts, classContext -> classContext.id, builder::addAllMergedClassContextIds);

        /*
        add exit points and failure aspects
         */
        valueMapping(executionContext.exitPoints, ExecutionContextExporter::contextClip, builder::addAllExitPoints);
        valueMapping(executionContext.failureAspects, ExecutionContextExporter::contextClip, builder::addAllFailureAscpects);

        value(executionContext.runConfig, config -> builder.setRunConfig(createRunConfig(config)));

//        value(executionContext.metaData.get(PlatformProperties.JOB_ID), builder::setJobId);
//        value(executionContext.metaData.get(PlatformProperties.RUN_ID), builder::setRunId);
//        value(executionContext.metaData.get(PlatformProperties.TASK_ID), builder::setTaskId);
//        value(executionContext.metaData.get(PlatformProperties.PROJECT_ID), builder::setProjectId);

        valueList(executionContext.exclusiveSessionContexts, sc -> sc.id, builder::addAllExclusiveSessionContextIds);

        value(executionContext.estimatedTestMethodCount, builder::setEstimatedTestMethodCount);

        return builder;
    }

    private static List<ContextClip> contextClip(Map<String, List<eu.tsystems.mms.tic.testframework.report.model.context.MethodContext>> values) {
        List<ContextClip> out = new LinkedList<>();
        values.forEach((key, list) -> {
            ContextClip.Builder builder = ContextClip.newBuilder();
            builder.setKey(key);
            list.forEach(methodContext -> builder.addMethodContextIds(methodContext.id));
            ContextClip contextClip = builder.build();
            out.add(contextClip);
        });
        return out;
    }

    public RunConfig.Builder createRunConfig(eu.tsystems.mms.tic.testframework.report.model.context.RunConfig runConfig) {

        final RunConfig.Builder builder = RunConfig.newBuilder();

        value(runConfig.getReportName(), builder::setReportName);
        value(runConfig.RUNCFG, builder::setRuncfg);

        /*
        add build information
         */
        TesterraBuildInformation.Builder bi = TesterraBuildInformation.newBuilder();
        builder.setTesterraBuildInformation(bi);
        value(runConfig.testerraBuildInformation.buildJavaVersion, bi::setBuildJavaVersion);
//        value(runConfig.testerraBuildInformation.buildOsArch, bi::setBuildOsName);
        value(runConfig.testerraBuildInformation.buildOsName, bi::setBuildOsName);
        value(runConfig.testerraBuildInformation.buildOsVersion, bi::setBuildOsVersion);
        value(runConfig.testerraBuildInformation.buildTimestamp, bi::setBuildTimestamp);
        value(runConfig.testerraBuildInformation.buildUserName, bi::setBuildUserName);
        value(runConfig.testerraBuildInformation.buildVersion, bi::setBuildVersion);

        return builder;
    }

}
