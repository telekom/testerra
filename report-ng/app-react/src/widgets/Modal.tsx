import {useEffect, useMemo, useState} from "react";
import Dialog from "@mui/material/Dialog";
import DialogContent from "@mui/material/DialogContent";
import IconButton from "@mui/material/IconButton";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Link from "@mui/material/Link";
import List from "@mui/material/List";
import KeyboardArrowLeftIcon from "@mui/icons-material/KeyboardArrowLeft";
import KeyboardArrowRightIcon from "@mui/icons-material/KeyboardArrowRight";
import FullscreenIcon from "@mui/icons-material/Fullscreen";
import CloseIcon from "@mui/icons-material/Close";
import type {File as ReportFile, SessionContext} from "../model/report-model/framework_pb.ts";
import {File} from "../model/report-model/framework_pb.ts";
import Stack from "@mui/material/Stack";
import DetailKeyValueListItem from "../components/method-details-tab-components/DetailKeyValueListItem.tsx";

interface ModalProps {
    open: boolean;
    screenshotIds: string[];
    initialScreenshotId?: string;
    sessionContexts?: SessionContext[];
    onClose: () => void;
}

const normalizeRelativePath = (path?: string) => {
    if (!path) {
        return "";
    }
    return `..${path.replaceAll("\\", "/")}`;
};

const Modal = ({open, screenshotIds, initialScreenshotId, sessionContexts = [], onClose}: ModalProps) => {
    const [screenshots, setScreenshots] = useState<ReportFile[]>([]);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [pageSourceFile, setPageSourceFile] = useState<ReportFile | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        let isMounted = true;

        const fetchFile = async (fileId: string) => {
            const response = await fetch(`model/files/${fileId}`);
            if (!response.ok) {
                throw new Error(`Cannot load model/files/${fileId}: ${response.status}`);
            }
            const buffer = await response.arrayBuffer();
            return File.decode(new Uint8Array(buffer));
        };

        const fetchScreenshots = async () => {
            if (!open || screenshotIds.length === 0) {
                return;
            }

            const files = await Promise.all(screenshotIds.map(fileId => fetchFile(fileId)));
            if (!isMounted) {
                return;
            }

            setScreenshots(files);
            setError(null);

            const foundIndex = initialScreenshotId
                ? files.findIndex(file => file.id === initialScreenshotId)
                : -1;
            setCurrentIndex(foundIndex >= 0 ? foundIndex : 0);
        };

        fetchScreenshots().catch((caughtError) => {
            if (isMounted) {
                setScreenshots([]);
                setError(caughtError instanceof Error ? caughtError.message : "Cannot load screenshots.");
            }
        });

        return () => {
            isMounted = false;
        };
    }, [open, screenshotIds, initialScreenshotId]);

    const currentScreenshot = screenshots[currentIndex];

    useEffect(() => {
        let isMounted = true;

        const fetchPageSource = async () => {
            const sourceRefId = currentScreenshot?.meta?.sourcesRefId;
            if (!open || !sourceRefId) {
                setPageSourceFile(null);
                return;
            }

            const response = await fetch(`model/files/${sourceRefId}`);
            if (!response.ok) {
                throw new Error(`Cannot load model/files/${sourceRefId}: ${response.status}`);
            }

            const buffer = await response.arrayBuffer();
            const file = File.decode(new Uint8Array(buffer));
            if (isMounted) {
                setPageSourceFile(file);
            }
        };

        fetchPageSource().catch((caughtError) => {
            if (isMounted) {
                setPageSourceFile(null);
                setError(caughtError instanceof Error ? caughtError.message : "Cannot load page source file.");
            }
        });

        return () => {
            isMounted = false;
        };
    }, [open, currentScreenshot]);

    const sessionContext = useMemo(() => {
        const sessionKey = currentScreenshot?.meta?.SessionKey;
        if (!sessionKey) {
            return undefined;
        }
        return sessionContexts.find(context => context.contextValues?.name === sessionKey);
    }, [currentScreenshot, sessionContexts]);

    const hasMultipleScreenshots = screenshots.length > 1;
    const imageSrc = normalizeRelativePath(currentScreenshot?.relativePath);
    const imageAlt = currentScreenshot?.name ?? currentScreenshot?.meta?.Title ?? "Screenshot";

    const goLeft = () => {
        setCurrentIndex(value => (value - 1 + screenshots.length) % screenshots.length);
    };

    const goRight = () => {
        setCurrentIndex(value => (value + 1) % screenshots.length);
    };

    const openFullscreen = () => {
        if (imageSrc) {
            window.open(imageSrc, "_blank", "noopener,noreferrer");
        }
    };

    return (
        <Dialog
            open={open}
            onClose={onClose}
            maxWidth="xl"
        >
            <Box sx={{p: 1}}>
                <Box sx={{display: "flex", justifyContent: "space-between", alignItems: "flex-start", gap: 1}}>
                    <Box sx={{width: 40}}/>
                    <Box sx={{display: "flex", alignItems: "flex-start", gap: 1, flex: 1}}>
                        {hasMultipleScreenshots && (
                            <IconButton aria-label="Previous screenshot" onClick={goLeft}>
                                <KeyboardArrowLeftIcon/>
                            </IconButton>
                        )}
                        <Stack direction={"row"} gap={6}>
                            <List disablePadding sx={{mx: 1.5, py: 1}}>
                                <DetailKeyValueListItem
                                    label="Title"
                                    value={<Typography variant="caption">{currentScreenshot?.meta?.Title ?? "-"}</Typography>}
                                    wideLabel={false}
                                    compact={true}
                                />
                                {currentScreenshot?.meta?.URL && (
                                    <DetailKeyValueListItem
                                        label="URL"
                                        value={
                                            <Link href={currentScreenshot.meta.URL} target="_blank"
                                                  rel="noopener noreferrer" sx={{overflowWrap: "anywhere"}}>
                                                <Typography variant={"caption"}>{currentScreenshot.meta.URL}</Typography>
                                            </Link>
                                        }
                                        wideLabel={false}
                                        compact={true}
                                    />
                                )}
                                {pageSourceFile?.relativePath && (
                                    <DetailKeyValueListItem
                                        label="PageSource"
                                        value={
                                            <Link href={normalizeRelativePath(pageSourceFile.relativePath)} target="_blank"
                                                  rel="noopener noreferrer" sx={{overflowWrap: "anywhere"}}>
                                                <Typography variant={"caption"}>{pageSourceFile.relativePath}</Typography>
                                            </Link>
                                        }
                                        wideLabel={false}
                                        compact={true}
                                    />
                                )}
                            </List>

                            <List disablePadding sx={{mx: 1.5, py: 1}}>
                                <DetailKeyValueListItem
                                    label="Taken"
                                    value={
                                        <Typography variant="caption">
                                            {currentScreenshot?.lastModified ? new Date(currentScreenshot.lastModified).toLocaleString() : "-"}
                                        </Typography>
                                    }
                                    wideLabel={false}
                                    compact={true}
                                />
                                {sessionContext?.contextValues && (
                                    <DetailKeyValueListItem
                                        label="Session"
                                        value={
                                            <Typography variant="caption">
                                                {sessionContext.contextValues.name} (ID: {sessionContext.contextValues.id})
                                            </Typography>
                                        }
                                        wideLabel={false}
                                        compact={true}
                                    />
                                )}
                            </List>
                        </Stack>

                        <IconButton aria-label="Open fullscreen in new tab" onClick={openFullscreen}>
                            <FullscreenIcon/>
                        </IconButton>
                        {hasMultipleScreenshots && (
                            <IconButton aria-label="Next screenshot" onClick={goRight}>
                                <KeyboardArrowRightIcon/>
                            </IconButton>
                        )}
                    </Box>
                    <IconButton aria-label="Close dialog" onClick={onClose}>
                        <CloseIcon/>
                    </IconButton>
                </Box>

                <DialogContent sx={{pt: 1, pb: 0, px: 0}}>
                    {error && <Typography color="error">{error}</Typography>}
                    {imageSrc && (
                        <Box
                            component="img"
                            src={imageSrc}
                            alt={imageAlt}
                            sx={{
                                border: "1px solid #5d6f81",
                                width: "100%",
                                maxHeight: "75vh",
                                objectFit: "contain",
                                display: "block",
                            }}
                        />
                    )}
                </DialogContent>
            </Box>
        </Dialog>
    );
};

export default Modal;