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
import eu.tsystems.mms.tic.testframework.report.model.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.RunConfig;

public class ExecutionContextExporter extends AbstractContextExporter {

    public ExecutionContext.Builder prepareExecutionContext(eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext executionContext) {
        ExecutionContext.Builder builder = ExecutionContext.newBuilder();

        apply(createContextValues(executionContext), builder::setContextValues);
//        forEach(executionContext.suiteContexts, suiteContext -> builder.addSuiteContextIds(suiteContext.getId()));
//        forEach(executionContext.mergedClassContexts, classContext -> builder.addMergedClassContextIds(classContext.id));
//        map(executionContext.exitPoints, this::createContextClip, builder::addAllExitPoints);
//        map(executionContext.failureAspects, this::createContextClip, builder::addAllFailureAscpects);
        map(executionContext.runConfig, this::prepareRunConfig, builder::setRunConfig);
        executionContext.readExclusiveSessionContexts().forEach(sessionContext -> builder.addExclusiveSessionContextIds(sessionContext.getId()));
        apply(executionContext.estimatedTestMethodCount, builder::setEstimatedTestsCount);
        executionContext.readMethodContextLessLogs().forEach(logMessage -> builder.addLogMessages(prepareLogMessage(logMessage)));

        return builder;
    }
//
//    private List<ContextClip> createContextClip(Map<String, List<eu.tsystems.mms.tic.testframework.report.model.context.MethodContext>> values) {
//        List<ContextClip> out = new LinkedList<>();
//        values.forEach((key, list) -> {
//            ContextClip.Builder builder = ContextClip.newBuilder();
//            builder.setKey(key);
//            list.forEach(methodContext -> builder.addMethodContextIds(methodContext.id));
//            ContextClip contextClip = builder.build();
//            out.add(contextClip);
//        });
//        return out;
//    }

    public RunConfig.Builder prepareRunConfig(eu.tsystems.mms.tic.testframework.report.model.context.RunConfig runConfig) {
        RunConfig.Builder builder = RunConfig.newBuilder();

        apply(runConfig.getReportName(), builder::setReportName);
        apply(runConfig.RUNCFG, builder::setRuncfg);

        /*
        add build information
         */
        BuildInformation.Builder bi = BuildInformation.newBuilder();
        apply(runConfig.buildInformation.buildJavaVersion, bi::setBuildJavaVersion);
        //apply(runConfig.buildInformation.buildOsArch, bi::setBuildOsName);
        apply(runConfig.buildInformation.buildOsName, bi::setBuildOsName);
        apply(runConfig.buildInformation.buildOsVersion, bi::setBuildOsVersion);
        apply(runConfig.buildInformation.buildTimestamp, bi::setBuildTimestamp);
        apply(runConfig.buildInformation.buildUserName, bi::setBuildUserName);
        apply(runConfig.buildInformation.buildVersion, bi::setBuildVersion);
        builder.setBuildInformation(bi);

        return builder;
    }

}
