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

import ReportCard from "../../widgets/ReportCard.tsx";
import type {MethodDetails} from "../../model/MethodDetails.ts";
import {Grid, useTheme} from "@mui/material";
import DurationCard from "../DurationCard.tsx";
import LazyImage from "../../widgets/LazyImage.tsx";
import {useState} from "react";
import Modal from "../../widgets/Modal.tsx";
import NoResultsCard from "../../widgets/NoResultsCard.tsx";
import CommonDetailsCard from "./CommonDetailsCard.tsx";

interface GeneralDetailsProps {
    methodDetail?: MethodDetails;
    previousDetail?: MethodDetails;
    nextDetail?: MethodDetails;
}

const GeneralDetails = ({methodDetail, previousDetail, nextDetail}: GeneralDetailsProps) => {
        const theme = useTheme();
        const [isScreenshotModalOpen, setIsScreenshotModalOpen] = useState(false);
        const [selectedScreenshotId, setSelectedScreenshotId] = useState<string | undefined>(undefined);

        if (!methodDetail) {
            return <NoResultsCard title="This method has no error context information"/>;
        }

        const allScreenshotIds = methodDetail.methodContext.testSteps
            ?.flatMap(step => step.actions ?? [])
            .flatMap(action => action.entries ?? [])
            .map(entry => entry.screenshotId)
            .filter((id): id is string => Boolean(id)) ?? [];
        const lastScreenshotId = allScreenshotIds.length > 0 ? allScreenshotIds[allScreenshotIds.length - 1] : undefined;

        return (
            <>
                <Grid container={true} spacing={3}>
                    <Grid size={lastScreenshotId ? 7 : 9}>
                        <CommonDetailsCard
                            methodDetails={methodDetail}
                            // sx={theme.mixins.cardHeight(5)}
                            previousDetail={previousDetail}
                            nextDetail={nextDetail}
                        />
                    </Grid>

                    {
                        lastScreenshotId && <Grid size={2}>
                            <ReportCard label={"Last Screenshot"}
                                        content={
                                            <LazyImage
                                                fileId={lastScreenshotId}
                                                onClick={(file) => {
                                                    if (file.id) {
                                                        setSelectedScreenshotId(file.id);
                                                        setIsScreenshotModalOpen(true);
                                                    }
                                                }}
                                                style={theme.custom.generalDetails.lastScreenshotImage}
                                            />

                                        }
                                        sxContent={theme.custom.generalDetails.lastScreenshotCardContent}/>
                        </Grid>
                    }

                    <Grid size={3}>
                        <DurationCard
                            start={methodDetail.methodContext.contextValues?.startTime}
                            end={methodDetail.methodContext.contextValues?.endTime}
                            sx={theme.mixins.cardHeight(4)}
                        />
                    </Grid>
                </Grid>

                <Modal
                    open={isScreenshotModalOpen}
                    screenshotIds={allScreenshotIds}
                    initialScreenshotId={selectedScreenshotId}
                    sessionContexts={methodDetail.sessionContexts}
                    onClose={() => setIsScreenshotModalOpen(false)}
                />
            </>
        );
};
export default GeneralDetails;
