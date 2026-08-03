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

import {forwardRef} from 'react';
import {Box, Typography} from '@mui/material';
import {ExecutionStatistics} from '../../model/ExecutionStatistics';
import {DateTime} from 'luxon';
import ReportCard from '../../widgets/ReportCard';
import FailureAspectsList from '../FailureAspectsList';
import TestList from '../TestList';

interface PrintableContentProps {
    executionStatistics: ExecutionStatistics | null;
    visibleSections: {
        failureAspects: 'none' | 'all' | 'major' | 'minor';
        testCaseList: 'none' | 'all' | 'failed';
    };
}

const PrintableContent = forwardRef<HTMLDivElement, PrintableContentProps>(
    ({executionStatistics, visibleSections}, ref) => {
        if (!executionStatistics) {
            return null;
        }

        const execAggregate = executionStatistics.getExecutionAggregate;
        const contextValues = execAggregate.executionContext?.contextValues;
        const runConfig = execAggregate.executionContext?.runConfig;

        if (!contextValues || !runConfig) {
            return null;
        }

        const startTime = DateTime.fromMillis(contextValues.startTime ?? 0);
        const endTime = DateTime.fromMillis(contextValues.endTime ?? 0);
        const duration = endTime.diff(startTime, 'milliseconds').toObject();

        const sessionContexts = execAggregate.sessionContexts || {};
        const browsers = Array.from(
            new Map(
                Object.entries(sessionContexts).map(([_, context]) => [
                    `${context?.browserName} ${context?.browserVersion}`,
                    true
                ])
            ).keys()
        ).join(', ');

        return (
            <Box
                ref={ref}
                id="printable-body"
                sx={{
                    padding: '20px',
                    // The preview renders the content at exactly the printable page width,
                    // so the layout (and therefore the page count) matches the print output
                    display: 'flex',
                    flexDirection: 'column',
                    gap: 2
                }}
            >
                {/* Header Card */}
                <ReportCard
                    label="Test Report Information"
                    sxCard={{boxShadow: "none", border: "1px solid rgba(0, 0, 0, .12)"}}
                    content={
                        <Box sx={{display: 'grid', gridTemplateColumns: '150px 1fr', gap: 1}}>
                            <Typography variant="body2">
                                Test configuration
                            </Typography>
                            <Box sx={{display: 'flex', flexDirection: 'column'}}>
                                <Typography variant="body2">• {runConfig.runcfg}</Typography>
                            </Box>

                            <Typography variant="body2">
                                Duration
                            </Typography>
                            <Box sx={{display: 'flex', flexDirection: 'column'}}>
                                <Typography variant="body2">•
                                    Duration: {duration.hours}h {duration.minutes}m {Math.round(duration.seconds || 0)}s</Typography>
                                <Typography variant="body2">•
                                    Started: {startTime.toFormat('dd.MM.yyyy HH:mm:ss')}</Typography>
                                <Typography variant="body2">•
                                    Ended: {endTime.toFormat('dd.MM.yyyy HH:mm:ss')}</Typography>
                            </Box>

                            <Typography variant="body2">
                                Browser Information
                            </Typography>
                            <Box sx={{display: 'flex', flexDirection: 'column'}}>
                                <Typography variant="body2">• used Browsers: {browsers}</Typography>
                                <Typography variant="body2">• created
                                    Sessions: {Object.keys(sessionContexts).length}</Typography>
                            </Box>
                        </Box>
                    }
                />

                {visibleSections.failureAspects !== 'none' && (
                    <FailureAspectsList
                        searchText=""
                        expectedFailedChecked={true}
                        type={visibleSections.failureAspects}
                    />
                )}

                {visibleSections.testCaseList !== 'none' && (
                    <TestList
                        filters={{
                            status: visibleSections.testCaseList === 'failed'
                                ? [2, 3, 4]
                                : undefined,
                        }}
                        searchText=""
                        showConfigurationMethods={false}
                    />
                )}

            </Box>
        );
    }
);

PrintableContent.displayName = 'PrintableContent';

export default PrintableContent;
