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

import {useMemo} from "react";

interface HighlightTextProps {
    text?: string | null;
    searchWord?: string[];
}

// splits text in words and returns the words and if each one should be highlighted (highlight: true/false)
function splitByTerms(text: string, searchWord: string[]): { text: string; highlight: boolean }[] {
    // if there is no text/no search word then the text is returned unhighlighted
    if (!text || searchWord.length === 0) return [{ text, highlight: false }];

    const lowerText = text.toLowerCase();
    const lowerCaseSearchTerm = searchWord
        .map(t => t.trim().toLowerCase())
        .filter(Boolean); // filters empty strings

    if (lowerCaseSearchTerm.length === 0) return [{ text, highlight: false }];

    const result: { text: string; highlight: boolean }[] = [];
    let index = 0;

    // searches each text word for the searchTerm
    while (index < text.length) {
        let nextMatchStart = -1;
        let nextMatchEnd = -1;

        for (const term of lowerCaseSearchTerm) {
            const currentPosition = lowerText.indexOf(term, index); // searches for term; if no match it returns -1

            // if there was a match and it is the first match (nextMatchStart === -1) or it is earlier than the currently
            // found match (currentPosition < nextMatchStart); we'll save it as nextMatchStart
            if (currentPosition !== -1 && (nextMatchStart === -1 || currentPosition < nextMatchStart)) {
                nextMatchStart = currentPosition;   // start position of earliest match
                nextMatchEnd = currentPosition + term.length; // end position of match
            }
        }

        // no more matches
        if (nextMatchStart === -1) {
            result.push({ text: text.slice(index), highlight: false });
            break;
        }

        if (nextMatchStart > index) {
            // normal text before match
            result.push({ text: text.slice(index, nextMatchStart), highlight: false });
        }

        // match
        result.push({ text: text.slice(nextMatchStart, nextMatchEnd), highlight: true });
        index = nextMatchEnd;
    }

    return result;
}


const HighlightText = ({ text, searchWord }: HighlightTextProps) => {
    const value = text ?? "";
    const terms = searchWord ?? [];

    // only execute splitByTerms if value changed
    const segments = useMemo(
        () => splitByTerms(value, terms),
        [value, terms.join("|")] // .join("|") transforms array to string (Arrays change their reference often for each render => prevents unnecessary renders)
    );

    return (
        <>
            {segments.map((segment, i) =>
                segment.highlight ? (
                    <mark key={i}>{segment.text}</mark>
                ) : (
                    <span key={i}>{segment.text}</span>
                )
            )}
        </>
    );
};

export default HighlightText;
