/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems MMS GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package eu.tsystems.mms.tic.testframework.report.utils;

import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;

import java.util.Optional;

public class DefaultExecutionContextController implements IExecutionContextController {

    @Override
    public Optional<SessionContext> getCurrentSessionContext() {
        return ExecutionContextController.getSessionContextForThread();
    }

    @Override
    public Optional<MethodContext> getCurrentMethodContext() {
        return Optional.ofNullable(ExecutionContextController.getCurrentMethodContext());
    }

    @Override
    public ExecutionContext getExecutionContext() {
        return ExecutionContextController.getCurrentExecutionContext();
    }

    @Override
    public void clearCurrentSessionContext() {
        ExecutionContextController.clearCurrentSessionContext();
    }

    @Override
    public void setCurrentSessionContext(SessionContext sessionContext) {
        ExecutionContextController.setCurrentSessionContext(sessionContext);
    }
}
