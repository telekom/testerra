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
 */
package eu.tsystems.mms.tic.testframework.adapters;

import eu.tsystems.mms.tic.testframework.report.model.ClassContext;

public class ClassContextExporter extends ContextExporter {

    public ClassContext.Builder prepareClassContext(eu.tsystems.mms.tic.testframework.report.model.context.ClassContext classContext) {
        ClassContext.Builder builder = ClassContext.newBuilder();

        value(createContextValues(classContext), builder::setContextValues);
        valueList(classContext.methodContexts, methodContext -> methodContext.id, builder::addAllMethodContextIds);

        value(classContext.fullClassName, builder::setFullClassName);
        value(classContext.simpleClassName, builder::setFullClassName);

        value(classContext.testContextModel.id, builder::setTestContextId);
        value(classContext.executionContext.id, builder::setExecutionContextId);

        //valueMapping(classContext.testContext, TestContext::name, builder::setTesterraClassContext);
        value(classContext.merged, builder::setMerged);

        return builder;
    }
}
