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
 */

package eu.tsystems.mms.tic.testframework.logging;

import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;

/**
 * Replaces %contextIds by [MCID:xxx][SID:xxx] in the log pattern layout
 * @todo Try to find a way to initialize this programmatically
 * @author Mike Reiche
 */
@Plugin(name = "ContextIdsPatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({"contextIds"})
public class ContextIdsPatternConverter extends LogEventPatternConverter {
    protected ContextIdsPatternConverter() {
        super( "ContextIdsPatternConverter", null );
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo ) {
        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        if (methodContext != null) {
            toAppendTo.append("[MCID:").append(methodContext.getId()).append("]");
        }

        // enhance with method context id
        SessionContext currentSessionContext = ExecutionContextController.getCurrentSessionContext();
        if (currentSessionContext != null) {
            toAppendTo.append("[SCID:").append(currentSessionContext.getId()).append("]");
        }
    }

    public static ContextIdsPatternConverter newInstance(String[] options) {
        return new ContextIdsPatternConverter();
    }
}
