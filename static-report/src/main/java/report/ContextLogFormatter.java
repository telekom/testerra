/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 *
 */
package report;

import eu.tsystems.mms.tic.testframework.report.LogFormatter;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * Adds {@link SessionContext} and {@link MethodContext} ids to log message
 * @author Mike Reiche
 */
public class ContextLogFormatter implements LogFormatter {
    private final Layout layout;

    {
        PatternLayout.Builder builder = PatternLayout.newBuilder();
        builder.withPattern("%d{dd.MM.yyyy HH:mm:ss.SSS} [%t][%p]%X{ids}: %c{2} - %m");
        layout = builder.build();
    }

    @Override
    public String format(LogEvent event) {
        StringBuilder sb = new StringBuilder();

        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        if (methodContext != null) {
            sb.append("[MCID:").append(methodContext.id).append("]");
        }

        // enhance with method context id
        SessionContext currentSessionContext = ExecutionContextController.getCurrentSessionContext();
        if (currentSessionContext != null) {
            sb.append("[SCID:").append(currentSessionContext.id).append("]");
        }

        try (final CloseableThreadContext.Instance ctc = CloseableThreadContext.put("ids", sb.toString())) {
            return new String(layout.toByteArray(event));
        }
    }
}
