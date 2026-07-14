import {useMemo, useState} from "react";
import type {LayoutCheckContext, SessionContext} from "../model/report-model/framework_pb.ts";
import {Card, Stack, Typography} from "@mui/material";
import ArrowLeftIcon from "@mui/icons-material/ArrowLeft";
import ArrowRightIcon from "@mui/icons-material/ArrowRight";
import LazyImage from "../widgets/LazyImage.tsx";
import Modal from "../widgets/Modal.tsx";

interface LayoutComparisonProps {
    layoutCheckContext: LayoutCheckContext;
    sessionContexts?: SessionContext[];
}

type CompareImageKey = "actual" | "diff" | "expected";
type CompareImage = {
    id?: string;
    title: string;
};

const LayoutComparison = ({layoutCheckContext, sessionContexts}: LayoutComparisonProps) => {
    const [isScreenshotModalOpen, setIsScreenshotModalOpen] = useState(false);
    const [selectedScreenshotId, setSelectedScreenshotId] = useState<string | undefined>(undefined);
    const [modalScreenshotIds, setModalScreenshotIds] = useState<string[]>([]);

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

    const imageIds = Object.values(images)
        .map(image => image.id)
        .filter((id): id is string => Boolean(id));

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

        setModalScreenshotIds(comparisonIds);
        setSelectedScreenshotId(left.id);
        setIsScreenshotModalOpen(true);
    };

    return (
        <>
            <Stack direction="row" spacing={0.5} sx={{alignItems: "center", mt: 1, flexWrap: "wrap"}}>
                <Stack sx={{alignItems: "start"}}>
                    {images.actual.id && (
                        <Card variant="outlined" sx={{p: 0.5}}>
                            <LazyImage
                                fileId={images.actual.id}
                                style={{width: 120, height: 120, objectFit: "cover", cursor: "pointer"}}
                                onClick={() => imageClicked("actual")}
                            />
                        </Card>
                    )}
                    <Typography variant="caption" color="textSecondary">{images.actual.title}</Typography>
                </Stack>

                <ArrowRightIcon fontSize="medium" color="secondary"/>

                <Stack sx={{alignItems: "start"}}>
                    {images.diff.id && (
                        <Card variant="outlined" sx={{p: 0.5}}>
                            <LazyImage
                                fileId={images.diff.id}
                                style={{width: 120, height: 120, objectFit: "cover", cursor: "pointer"}}
                                onClick={() => imageClicked("diff")}
                            />
                        </Card>
                    )}
                    <Typography variant="caption" color="textSecondary">{images.diff.title}</Typography>
                </Stack>

                <ArrowLeftIcon fontSize="medium" color="secondary"/>

                <Stack sx={{alignItems: "start"}}>
                    {images.expected.id && (
                        <Card variant="outlined" sx={{p: 0.5}}>
                            <LazyImage
                                fileId={images.expected.id}
                                style={{width: 120, height: 120, objectFit: "cover", cursor: "pointer"}}
                                onClick={() => imageClicked("expected")}
                            />
                        </Card>
                    )}
                    <Typography variant="caption" color="textSecondary">{images.expected.title}</Typography>
                </Stack>
            </Stack>
            <Modal
                open={isScreenshotModalOpen}
                screenshotIds={modalScreenshotIds}
                initialScreenshotId={selectedScreenshotId}
                sessionContexts={sessionContexts}
                onClose={() => setIsScreenshotModalOpen(false)}
            />
        </>
    );
};

export default LayoutComparison;
