/*
 * Testerra
 *
 * (C) 2026, Selina Natschke, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import Box from "@mui/material/Box";
import type {ScriptSource} from "../model/report-model/framework_pb.ts";
import hljs from "highlight.js/lib/core";
import java from "highlight.js/lib/languages/java";
import "highlight.js/styles/base16/darcula.css";

if (!hljs.getLanguage("java")) {
    hljs.registerLanguage("java", java);
}

interface CodeViewProps {
    source?: ScriptSource;
    markClass?: "error" | "warning";
    showNumbers?: boolean;
    markingName?: string;
}

const CodeView = ({source, markClass = "error", showNumbers = true, markingName}: CodeViewProps) => {
    return (
        <Box sx={(theme) => theme.custom.codeView.codeView}>
            {(source?.lines ?? []).map((sourceLine, lineIndex) => (
                <Box
                    key={`source-line-${lineIndex}`}
                    sx={(theme) => ({
                        ...theme.custom.codeView.line,
                        ...(sourceLine.lineNumber === source?.mark || (markingName && sourceLine.line?.includes(markingName))
                           ? (markClass === "warning" ? theme.custom.codeView.warn : theme.custom.codeView.error)
                           : {}),
                    })}
                >
                    {showNumbers && (
                        <Box component="span" sx={(theme) => theme.custom.codeView.number}>
                            {sourceLine.lineNumber ?? ""}
                        </Box>
                    )}
                    <Box component="span"
                         dangerouslySetInnerHTML={{
                            __html: hljs.highlight(sourceLine.line ?? "", {
                                language: "java",
                                 ignoreIllegals: true,
                             }).value
                         }}>
                    </Box>
                </Box>
            ))}
        </Box>
    );
};

export default CodeView;
