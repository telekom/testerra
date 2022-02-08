/*
 * Testerra
 *
 * (C) 2021, Mike Reiche,  T-Systems MMS GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.logging;

import eu.tsystems.mms.tic.testframework.report.model.context.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import java.util.Arrays;
import java.util.Optional;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.message.Message;

public class MethodContextLogAppender extends AbstractAppender {

    public MethodContextLogAppender() {
        super("MethodContextLogAppender", null, null, true, null);
    }

    @Override
    public void append(final LogEvent event) {
        final LogMessage logMessage = new LogMessage(event);
        Optional<MethodContext> optionalMethodContext;
        final Message message = event.getMessage();
        if (message.getParameters() != null) {
            optionalMethodContext = Arrays.stream(event.getMessage().getParameters())
                    .filter(MethodContext.class::isInstance)
                    .map(o -> (MethodContext) o)
                    .findFirst();
        } else {
            optionalMethodContext = Optional.empty();
        }

        // Otherwise, we take the current method context from execution context
        if (!optionalMethodContext.isPresent()) {
            optionalMethodContext = ExecutionContextController.getMethodContextForThread();
        }

        if (optionalMethodContext.isPresent()) {
            optionalMethodContext.get().addLogMessage(logMessage);
        } else {
            ExecutionContextController.getCurrentExecutionContext().addLogMessage(logMessage);
        }
    }
}
