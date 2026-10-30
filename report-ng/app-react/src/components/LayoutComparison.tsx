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

import {useMemo, useState} from "react";
import type {LayoutCheckContext} from "../model/report-model/framework_pb.ts";
import {Card, Stack, Typography} from "@mui/material";
import ArrowLeftIcon from "@mui/icons-material/ArrowLeft";
import ArrowRightIcon from "@mui/icons-material/ArrowRight";
import LazyImage from "../widgets/LazyImage.tsx";
import ScreenshotComparisonModal, {type ComparisonImage} from "./ScreenshotComparisonModal.tsx";

interface LayoutComparisonProps {
    layoutCheckContext: LayoutCheckContext;
}

type CompareImageKey = "actual" | "diff" | "expected";
type CompareImage = {
    id?: string;
    title: string;
};

interface ComparisonThumbnailProps {
    image: CompareImage;
    onClick: () => void;
}

const ComparisonThumbnail = ({image, onClick}: ComparisonThumbnailProps) => (
    <Stack sx={{alignItems: "start"}}>
        {image.id && (
            <Card variant="outlined" sx={{height: 120}}>
                <LazyImage
                    fileId={image.id}
                    style={{width: 120, height: 120, objectFit: "cover", cursor: "pointer"}}
                    onClick={onClick}
                />
            </Card>
        )}
        <Typography variant="caption" color="text.secondary">{image.title}</Typography>
    </Stack>
);

const LayoutComparison = ({layoutCheckContext}: LayoutComparisonProps) => {
    const [isComparisonOpen, setIsComparisonOpen] = useState(false);
    const [initialLeftId, setInitialLeftId] = useState<string | undefined>(undefined);
    const [initialRightId, setInitialRightId] = useState<string | undefined>(undefined);

    const images = useMemo<Record<CompareImageKey, CompareImage>>(() => ({
        actual: {
            id: layoutCheckContext.actualScreenshotId,
            title: "Actual",
        },
        diff: {
            id: layoutCheckContext.distanceScreenshotId,
            title: "Difference",
        },
        expected: {
            id: layoutCheckContext.expectedScreenshotId,
            title: "Expected",
        },
    }), [layoutCheckContext]);

    const imageIds = Object.values(images).map(image => image.id).filter((id): id is string => Boolean(id));
    const comparisonImages = Object.values(images)
        .filter((image): image is CompareImage & {id: string} => Boolean(image.id))
        .map((image): ComparisonImage => ({id: image.id, title: image.title}));

    if (imageIds.length === 0) {
        return null;
    }

    const imageClicked = (imageKey: CompareImageKey) => {
        const left = images[imageKey];
        const right = imageKey === "diff" ? images.expected : images.diff;
        const comparisonIds = [left.id, right.id].filter((id): id is string => Boolean(id));

        if (!left.id || comparisonIds.length === 0) {
            return;
        }

        setInitialLeftId(left.id);
        setInitialRightId(comparisonIds.find(id => id !== left.id));
        setIsComparisonOpen(true);
    };

    return (
        <>
            <Stack direction="row" spacing={0.5} sx={{alignItems: "center", mt: 1, flexWrap: "wrap"}}>
                <ComparisonThumbnail image={images.actual} onClick={() => imageClicked("actual")}/>

                <ArrowRightIcon fontSize="medium" color="inherit"/>

                <ComparisonThumbnail image={images.diff} onClick={() => imageClicked("diff")}/>

                <ArrowLeftIcon fontSize="medium" color="inherit"/>

                <ComparisonThumbnail image={images.expected} onClick={() => imageClicked("expected")}/>
            </Stack>
            <ScreenshotComparisonModal
                open={isComparisonOpen}
                images={comparisonImages}
                initialLeftId={initialLeftId}
                initialRightId={initialRightId}
                onClose={() => setIsComparisonOpen(false)}
            />
        </>
    );
};

export default LayoutComparison;
