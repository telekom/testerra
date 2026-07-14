import {useEffect, useMemo, useRef, useState} from "react";
import Dialog from "@mui/material/Dialog";
import DialogContent from "@mui/material/DialogContent";
import Box from "@mui/material/Box";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import FormControl from "@mui/material/FormControl";
import InputLabel from "@mui/material/InputLabel";
import Select from "@mui/material/Select";
import MenuItem from "@mui/material/MenuItem";
import CompareIcon from "@mui/icons-material/Compare";
import CompareArrowsIcon from "@mui/icons-material/CompareArrows";
import CloseIcon from "@mui/icons-material/Close";
import type {File as ReportFile} from "../model/report-model/framework_pb.ts";
import {File} from "../model/report-model/framework_pb.ts";
import Stack from "@mui/material/Stack";

export interface ComparisonImage {
    id: string;
    title: string;
}

interface ScreenshotComparisonModalProps {
    open: boolean;
    images: ComparisonImage[];
    initialLeftId?: string;
    initialRightId?: string;
    onClose: () => void;
}

const normalizeRelativePath = (path?: string) => {
    if (!path) {
        return "";
    }
    return `..${path.replaceAll("\\", "/")}`;
};

const getTouchClientX = (touches: { item(index: number): { clientX: number } | null }): number | null => {
    const touch = touches.item(0);
    return touch ? touch.clientX : null;
};

const ScreenshotComparisonModal = ({
                                       open,
                                       images,
                                       initialLeftId,
                                       initialRightId,
                                       onClose
                                   }: ScreenshotComparisonModalProps) => {
    const [filesById, setFilesById] = useState<Record<string, ReportFile>>({});
    const [leftId, setLeftId] = useState<string | undefined>(undefined);
    const [rightId, setRightId] = useState<string | undefined>(undefined);
    const [ratio, setRatio] = useState(0.5);
    const [dragging, setDragging] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const containerRef = useRef<HTMLDivElement | null>(null);

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

        const fetchFiles = async () => {
            if (!open || images.length === 0) {
                return;
            }

            const pairs = await Promise.all(
                images.map(async image => [image.id, await fetchFile(image.id)] as const)
            );

            if (!isMounted) {
                return;
            }

            setFilesById(Object.fromEntries(pairs));
            setError(null);
        };

        fetchFiles().catch((caughtError) => {
            if (isMounted) {
                setFilesById({});
                setError(caughtError instanceof Error ? caughtError.message : "Cannot load comparison images.");
            }
        });

        return () => {
            isMounted = false;
        };
    }, [open, images]);

    const availableImages = useMemo(() => {
        return images.filter(image => Boolean(filesById[image.id]?.relativePath));
    }, [images, filesById]);

    useEffect(() => {
        if (!open || availableImages.length < 2) {
            return;
        }

        const fallbackLeft = availableImages[0].id;
        const leftCandidate = availableImages.some(image => image.id === initialLeftId) ? initialLeftId : fallbackLeft;
        const fallbackRight = availableImages.find(image => image.id !== leftCandidate)?.id;
        const rightCandidate = availableImages.some(image => image.id === initialRightId && image.id !== leftCandidate)
            ? initialRightId
            : fallbackRight;

        setLeftId(leftCandidate);
        setRightId(rightCandidate);
        setRatio(0.5);
    }, [open, availableImages, initialLeftId, initialRightId]);

    useEffect(() => {
        if (!leftId || !rightId) {
            return;
        }

        if (leftId === rightId) {
            const replacement = availableImages.find(image => image.id !== leftId)?.id;
            setRightId(replacement);
        }
    }, [leftId, rightId, availableImages]);

    const moveSlider = (clientX: number) => {
        const container = containerRef.current;
        if (!container) {
            return;
        }

        const rect = container.getBoundingClientRect();
        if (rect.width <= 0) {
            return;
        }

        const nextRatio = Math.min(1, Math.max(0, (clientX - rect.left) / rect.width));
        setRatio(nextRatio);
    };

    useEffect(() => {
        if (!dragging) {
            return;
        }

        const onMouseMove = (event: MouseEvent) => {
            moveSlider(event.clientX);
        };
        const onMouseUp = () => {
            setDragging(false);
        };
        const onTouchMove = (event: TouchEvent) => {
            const clientX = getTouchClientX(event.touches);
            if (clientX !== null) {
                moveSlider(clientX);
            }
        };
        const onTouchEnd = () => {
            setDragging(false);
        };

        window.addEventListener("mousemove", onMouseMove);
        window.addEventListener("mouseup", onMouseUp);
        window.addEventListener("touchmove", onTouchMove);
        window.addEventListener("touchend", onTouchEnd);

        return () => {
            window.removeEventListener("mousemove", onMouseMove);
            window.removeEventListener("mouseup", onMouseUp);
            window.removeEventListener("touchmove", onTouchMove);
            window.removeEventListener("touchend", onTouchEnd);
        };
    }, [dragging]);

    const leftOptions = availableImages.filter(image => image.id !== rightId);
    const rightOptions = availableImages.filter(image => image.id !== leftId);
    const leftImage = leftId ? filesById[leftId] : undefined;
    const rightImage = rightId ? filesById[rightId] : undefined;
    const leftSrc = normalizeRelativePath(leftImage?.relativePath);
    const rightSrc = normalizeRelativePath(rightImage?.relativePath);

    return (
        <Dialog
            open={open}
            onClose={onClose}
            maxWidth={"xl"}
            sx={{"& .MuiDialog-paper": {position: "relative"}}}
        >
            <IconButton
                onClick={onClose}
                aria-label="Close comparison dialog"
                sx={{position: "absolute", top: 8, right: 8, zIndex: 10}}
            >
                <CloseIcon/>
            </IconButton>
            <Box sx={{px: 1.5, pt: 1.5}}>
                <Stack direction={"row"} alignItems={"center"} gap={4} width={"100%"} justifyContent={"center"} pt={1}>
                    <CompareIcon color="inherit"/>
                    <FormControl size="medium" sx={{minWidth: 180}}>
                        <InputLabel id="comparison-left-label">Left</InputLabel>
                        <Select
                            labelId="comparison-left-label"
                            label="Left"
                            value={leftId ?? ""}
                            onChange={(event) => setLeftId(String(event.target.value))}
                        >
                            {leftOptions.map(image => (
                                <MenuItem key={image.id} value={image.id}>{image.title}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    <CompareArrowsIcon color="inherit"/>

                    <FormControl size="medium" sx={{minWidth: 180}}>
                        <InputLabel id="comparison-right-label">Right</InputLabel>
                        <Select
                            labelId="comparison-right-label"
                            label="Right"
                            value={rightId ?? ""}
                            onChange={(event) => setRightId(String(event.target.value))}
                        >
                            {rightOptions.map(image => (
                                <MenuItem key={image.id} value={image.id}>{image.title}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </Stack>
            </Box>

            <DialogContent sx={{pt: 1, px: 2, pb: 2}}>
                {error && <Typography color="error">{error}</Typography>}
                {!error && (!leftSrc || !rightSrc) && (
                    <Typography color="text.secondary">Not enough images available for comparison.</Typography>
                )}

                {leftSrc && rightSrc && (
                    <Box
                        ref={containerRef}
                        sx={{
                            position: "relative",
                            width: "fit-content",
                            maxWidth: "80vw",
                            maxHeight: "80vh",
                            overflow: "hidden",
                            touchAction: "none",
                            userSelect: "none",
                        }}
                    >
                        <Box
                            component="img"
                            src={rightSrc}
                            alt="Right comparison"
                            sx={{
                                display: "block",
                                maxWidth: "80vw",
                                maxHeight: "80vh",
                                objectFit: "contain",
                            }}
                        />
                        <Box
                            sx={{
                                position: "absolute",
                                top: 0,
                                left: 0,
                                bottom: 0,
                                width: `${ratio * 100}%`,
                                overflow: "hidden",
                                zIndex: 2,
                            }}
                        >
                            <Box
                                component="img"
                                src={leftSrc}
                                alt="Left comparison"
                                sx={{
                                    display: "block",
                                    maxWidth: "80vw",
                                    maxHeight: "80vh",
                                    objectFit: "contain",
                                }}
                            />
                        </Box>

                        <Box
                            onMouseDown={(event) => {
                                setDragging(true);
                                moveSlider(event.clientX);
                            }}
                            onTouchStart={(event) => {
                                setDragging(true);
                                const clientX = getTouchClientX(event.touches);
                                if (clientX !== null) {
                                    moveSlider(clientX);
                                }
                            }}
                            sx={{
                                position: "absolute",
                                top: 0,
                                bottom: 0,
                                left: `calc(${ratio * 100}% - 1px)`,
                                width: 2,
                                zIndex: 3,
                                cursor: "ew-resize",
                                "&::before": {
                                    content: '""',
                                    position: "absolute",
                                    top: "50%",
                                    left: "50%",
                                    transform: "translate(-50%, -50%) rotate(45deg)",
                                    width: 30,
                                    height: 30,
                                    backgroundColor: "#f6a821BF",
                                },
                            }}
                        />
                    </Box>
                )}
            </DialogContent>
        </Dialog>
    );
};

export default ScreenshotComparisonModal;
