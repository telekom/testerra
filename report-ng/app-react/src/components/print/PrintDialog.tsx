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

import React, { useRef, useState, useEffect } from 'react';
import {
    Dialog,
    DialogContent,
    Button,
    FormGroup,
    FormControlLabel,
    Checkbox,
    RadioGroup,
    Radio,
    Box,
    Typography,
    Paper,
    Divider,
    Tooltip,
    IconButton,
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import {useReactToPrint} from 'react-to-print';
import PrintableContent from './PrintableContent';
import {ExecutionStatistics} from '../../model/ExecutionStatistics';
import {dateFormatter} from '../../utils/dateFormatter';

interface PrintDialogProps {
    open: boolean;
    onClose: () => void;
    executionStatistics: ExecutionStatistics | null;
}

// One checkbox block in the right panel (e.g. "Failure Aspects Table"),
// including all radio filters that belong to this block.
interface CheckboxOption {
    label: string;
    id: string;
    filter: FilterOption[];
}

// One selectable radio option inside a checkbox block.
interface FilterOption {
    id: string;
    name: string;
    tooltip?: string;
}

// A4 printable area in CSS pixels (96 dpi), reduced by the @page margin used for printing.
// The content is always laid out at exactly this size, so line breaks and the resulting
// page count are identical to the browser print output - independent of the screen size.
const PX_PER_MM = 96 / 25.4;
const PAGE_MARGIN_MM_X = 16;
const PAGE_MARGIN_MM_Y = 12;
const PAGE_WIDTH = (210 - 2 * PAGE_MARGIN_MM_X) * PX_PER_MM;
const PAGE_HEIGHT = (297 - 2 * PAGE_MARGIN_MM_Y) * PX_PER_MM;
const PREVIEW_SCROLLBAR_GUTTER_PX = 16;

// Shrinks printable content in preview + browser print dialog (estimated through testing)
const CONTENT_SCALE = 0.66;

// ids für filters
const FAILURE_ASPECTS_CARD_ID = 'failure-aspects-card';
const TEST_LIST_CARD_ID = 'test-list-card';

// options for checkboxes
const checkboxOptions: CheckboxOption[] = [
    {
        label: 'Failure Aspects Table',
        id: 'failure-aspects-card',
        filter: [
            {id: 'failure-aspects-table', name: 'All', tooltip: 'Displays all Failure Aspects'},
            {id: 'failure-aspects-table-major', name: 'Major', tooltip: 'Failure aspect affects end status'},
            {id: 'failure-aspects-table-minor', name: 'Minor', tooltip: 'Failure aspect does not affect end status'},
        ],
    },
    {
        label: 'Test Case List', id: TEST_LIST_CARD_ID,
        filter: [
            {
                id: 'classes-table',
                name: 'All',
                tooltip: 'Included status: Passed, Failed, Expected Failed, Skipped'
            },
            {
                id: 'classes-table-failed',
                name: 'Failed',
                tooltip: 'Included status: Failed, Expected Failed, Skipped'
            },
        ],
    },
];

const PrintDialog: React.FC<PrintDialogProps> = ({open, onClose, executionStatistics}) => {
    const contentRef = useRef<HTMLDivElement>(null);    // points to actual printable content
    const viewportRef = useRef<HTMLDivElement>(null);   // points to preview container
    const [viewportHeight, setViewportHeight] = useState(0);                      // current viewport height for preview calculation
    const [viewportWidth, setViewportWidth] = useState(0);                        // current viewport width for preview calculation
    const [contentHeight, setContentHeight] = useState(0);                        // current content height for the preview scroll area

    // Fit preview to both available height and width so the A4 frame is never clipped
    // Use a minimum scale to ensure visibility during initial load
    const scale = viewportHeight > 0 && viewportWidth > 0
        ? Math.min(
            viewportHeight / PAGE_HEIGHT,
            (viewportWidth - PREVIEW_SCROLLBAR_GUTTER_PX) / PAGE_WIDTH,
            1   // do not scale the preview up
        )
        : 0.5;  // if calculation is not ready yet, show scale at 50%

    const [enabledSections, setEnabledSections] = useState<Record<string, boolean>>({
        [FAILURE_ASPECTS_CARD_ID]: true,
        [TEST_LIST_CARD_ID]: true,
    });

    const [selectedFilters, setSelectedFilters] = useState<Record<string, string>>({
        [FAILURE_ASPECTS_CARD_ID]: 'failure-aspects-table',
        [TEST_LIST_CARD_ID]: 'classes-table',
    });

    const handleCheckboxChange = (optionId: string) => {
        setEnabledSections(prev => ({...prev, [optionId]: !prev[optionId]}));
    };

    const handleFilterChange = (cardId: string, filterId: string) => {
        setSelectedFilters(prev => ({...prev, [cardId]: filterId}));
    };

    // definition of sections
    const visibleSections: {
        failureAspects: 'none' | 'all' | 'major' | 'minor';
        testCaseList: 'none' | 'all' | 'failed';
    } = {
        failureAspects: !enabledSections[FAILURE_ASPECTS_CARD_ID]
            ? 'none'
            : selectedFilters[FAILURE_ASPECTS_CARD_ID] === 'failure-aspects-table-major'
                ? 'major'
                : selectedFilters[FAILURE_ASPECTS_CARD_ID] === 'failure-aspects-table-minor'
                    ? 'minor'
                    : 'all',
        testCaseList: !enabledSections[TEST_LIST_CARD_ID]
            ? 'none'
            : selectedFilters[TEST_LIST_CARD_ID] === 'classes-table-failed'
                ? 'failed'
                : 'all',
    };

    // Track the available preview area (height + width) -> page is scaled to fit A4
    useEffect(() => {
        if (!open) return;

        // uses viewport to update size
        const measure = () => {
            if (viewportRef.current) {
                if (viewportRef.current.clientHeight > 0) {
                    setViewportHeight(viewportRef.current.clientHeight);
                }
                if (viewportRef.current.clientWidth > 0) {
                    setViewportWidth(viewportRef.current.clientWidth);
                }
            }
        };

        const handleViewportChange = () => {
            measure();
            requestAnimationFrame(measure);
            setTimeout(measure, 80);
        };

        // Measure immediately and with a small delay to catch layout shifts
        measure();
        const timeoutId = setTimeout(measure, 50);

        window.addEventListener('resize', handleViewportChange);

        return () => {
            clearTimeout(timeoutId);
            window.removeEventListener('resize', handleViewportChange);
        };
    }, [open]);

    // Track the unscaled height of the printable content -> defines the preview scroll height
    useEffect(() => {
        if (!open) return;
        const measure = () => {
            if (contentRef.current) {
                // Measure the unscaled printable element, not the scaled preview container.
                const height = contentRef.current.scrollHeight;
                if (height > 0) {
                    setContentHeight(height);
                }
            }
        };
        
        // Measure with a small delay to ensure all content is rendered
        const timeoutId = setTimeout(measure, 100);

        return () => {
            clearTimeout(timeoutId);
        };
    }, [open, visibleSections.failureAspects, visibleSections.testCaseList, executionStatistics]);

    const handlePrint = useReactToPrint({
        contentRef,
        documentTitle: generateFileName(),
        pageStyle: `
            @page {
                size: A4;
                margin: ${PAGE_MARGIN_MM_Y}mm ${PAGE_MARGIN_MM_X}mm;
            }
            @media print {
                body {
                    margin: 0;
                    padding: 0;
                }
                #printable-body {
                    margin: 0;
                    padding: 0;
                    zoom: ${CONTENT_SCALE};
                    transform-origin: top left;
                }
            }
        `,
    });

    function generateFileName(): string {
        if (!executionStatistics) return 'Test_Report';
        const execAggregate = executionStatistics.getExecutionAggregate;
        const execContext = execAggregate.executionContext;
        if (!execContext) return 'Test_Report';
        const date = dateFormatter(execContext.contextValues?.startTime, 'print');
        return `Test_Report_${execContext.runConfig?.reportName}_${execContext.runConfig?.runcfg}_${date}`;
    }

    const reportName = executionStatistics?.getExecutionAggregate.executionContext?.runConfig?.reportName;

    return (
        <Dialog
            open={open}
            onClose={onClose}
            maxWidth="lg"
            fullWidth
            PaperProps={{
                sx: {
                    height: '90vh',
                    display: 'flex',
                    flexDirection: 'column',
                },
            }}
        >
            <IconButton
                onClick={onClose}
                sx={{
                    position: 'absolute',
                    top: 8,
                    right: 8,
                    zIndex: 1,
                    color: 'inherit',
                }}
            >
                <CloseIcon/>
            </IconButton>
            <DialogContent
                dividers
                sx={{
                    display: 'flex',
                    gap: 2,
                    p: 1,
                }}
            >

                {/* Preview Panel */}
                <Box
                    ref={viewportRef}
                    sx={{
                        position: 'relative',
                        flex: 1,
                        overflow: 'hidden',
                        display: 'flex',
                        justifyContent: 'center',
                    }}
                >
                    <Paper
                        sx={{
                            // The page is scaled to fit the height, so the scroll container has to
                            // be sized from the same scale - otherwise it stays wider than the page.
                            // The extra width covers the border and the reserved scrollbar gutter.
                            flex: '0 0 auto',
                            width: `${PAGE_WIDTH * scale + PREVIEW_SCROLLBAR_GUTTER_PX + 2}px`,
                            height: '100%',
                            overflowX: 'hidden',
                            scrollbarGutter: 'stable',
                            border: "1px solid rgba(0, 0, 0, .12)",
                        }}
                    >
                        {/* Spacer keeps the scroll height in sync with the scaled content */}
                        <Box
                            sx={{
                                width: `${PAGE_WIDTH * scale}px`,
                                height: `${contentHeight * scale * CONTENT_SCALE}px`,
                            }}
                        >
                            {/* scales down content to match a4 format */}
                            <Box
                                sx={{
                                    width: `${PAGE_WIDTH / CONTENT_SCALE}px`,
                                    transformOrigin: 'top left',
                                    transform: `scale(${scale * CONTENT_SCALE})`,
                                }}
                            >
                                <PrintableContent
                                    ref={contentRef}
                                    executionStatistics={executionStatistics}
                                    visibleSections={visibleSections}
                                />
                            </Box>
                        </Box>
                    </Paper>
                </Box>

                <Divider orientation="vertical" flexItem/>

                {/* Control Panel */}
                <Box
                    sx={{
                        flex: '1',
                        display: 'flex',
                        flexDirection: 'column',
                    }}
                >
                    <Typography variant="h6" sx={{m: 2}}>
                        Print Preview{reportName ? `: ${reportName}` : ''}
                    </Typography>

                    <FormGroup sx={{overflowY: 'auto', mx: 2, overflow: 'visible'}}>
                        {checkboxOptions.map(option => (
                            <Box key={option.id}>
                                <FormControlLabel
                                    control={
                                        <Checkbox
                                            checked={enabledSections[option.id]}
                                            onChange={() => handleCheckboxChange(option.id)}
                                            color="secondary"
                                        />
                                    }
                                    label={<Typography color="primary">{option.label}</Typography>}
                                    sx={{whiteSpace: 'normal'}}
                                />

                                <Box sx={{marginLeft: 3}}>
                                    <RadioGroup
                                        value={selectedFilters[option.id] || ''}
                                        onChange={(e) => handleFilterChange(option.id, e.target.value)}
                                        row
                                        sx={{gap: 2}}
                                    >
                                        {option.filter.map(filter => (
                                            <FormControlLabel
                                                key={filter.id}
                                                value={filter.id}
                                                disabled={!enabledSections[option.id]}
                                                control={
                                                    <Tooltip
                                                        title={filter.tooltip || ''}
                                                        placement="top"
                                                        slotProps={{
                                                            popper: {
                                                                modifiers: [
                                                                    {
                                                                        name: 'offset',
                                                                        options: {
                                                                            offset: [0, -8],
                                                                        },
                                                                    },
                                                                ],
                                                            },
                                                        }}
                                                    >
                                                        <Radio color="secondary" size="medium"/>
                                                    </Tooltip>
                                                }
                                                label={
                                                    <Typography variant="body2" color="primary">
                                                        {filter.name}
                                                    </Typography>
                                                }
                                            />
                                        ))}
                                    </RadioGroup>
                                </Box>
                            </Box>
                        ))}
                    </FormGroup>

                    <Box sx={{mt: 'auto', pt: 2, display: 'flex', justifyContent: 'flex-end'}}>
                        <Button
                            onClick={() => {
                                handlePrint();
                            }}
                            variant="outlined">
                            Print
                        </Button>
                    </Box>
                </Box>
            </DialogContent>
        </Dialog>
    );
};

export default PrintDialog;
