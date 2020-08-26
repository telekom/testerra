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

import eu.tsystems.mms.tic.testframework.report.model.BuildInformation;
import eu.tsystems.mms.tic.testframework.report.model.ContextClip;
import eu.tsystems.mms.tic.testframework.report.model.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.RunConfig;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ExecutionContextExporter extends ContextExporter {

    public ExecutionContext.Builder prepareExecutionContext(eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext executionContext) {
        ExecutionContext.Builder builder = ExecutionContext.newBuilder();

        value(createContextValues(executionContext), builder::setContextValues);
        valueList(executionContext.suiteContexts, s -> s.id, builder::addAllSuiteContextIds);
        valueList(executionContext.mergedClassContexts, classContext -> classContext.id, builder::addAllMergedClassContextIds);
        valueMapping(executionContext.exitPoints, ExecutionContextExporter::contextClip, builder::addAllExitPoints);
        valueMapping(executionContext.failureAspects, ExecutionContextExporter::contextClip, builder::addAllFailureAscpects);
        value(executionContext.runConfig, config -> builder.setRunConfig(prepareRunConfig(config)));
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

    public RunConfig.Builder prepareRunConfig(eu.tsystems.mms.tic.testframework.report.model.context.RunConfig runConfig) {
        RunConfig.Builder builder = RunConfig.newBuilder();

        value(runConfig.getReportName(), builder::setReportName);
        value(runConfig.RUNCFG, builder::setRuncfg);

        /*
        add build information
         */
        BuildInformation.Builder bi = BuildInformation.newBuilder();
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
