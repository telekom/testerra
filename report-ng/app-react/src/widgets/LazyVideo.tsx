import {useEffect, useRef, useState} from "react";
import {File as ReportFile} from "../model/report-model/framework_pb.ts";

interface LazyVideoProps {
    fileId: string;
}

interface IVideoInfo {
    time: number;
}

const normalizeRelativePath = (path?: string) => {
    if (!path) return "";
    if (path.startsWith("/") || path.startsWith("http://") || path.startsWith("https://")) {
        return path;
    }
    return `..${path.replaceAll("\\", "/")}`;
};

const LazyVideo = ({fileId}: LazyVideoProps) => {
    const [file, setFile] = useState<ReportFile | null>(null);
    const [error, setError] = useState<string | null>(null);
    const videoRef = useRef<HTMLVideoElement | null>(null);
    const storageKey = `video-${fileId}`;

    const storeCurrentVideoTime = () => {
        const currentTime = videoRef.current?.currentTime;
        if (currentTime === undefined) {
            return;
        }
        const videoInfo: IVideoInfo = {time: currentTime};
        window.localStorage.setItem(storageKey, JSON.stringify(videoInfo));
    };

    useEffect(() => {
        const abortController = new AbortController();

        const fetchFile = async () => {
            try {
                const response = await fetch(`model/files/${fileId}`, {signal: abortController.signal});
                if (!response.ok) {
                    throw new Error(`Cannot load model/files/${fileId}: ${response.status}`);
                }
                const buffer = await response.arrayBuffer();
                const decodedFile = ReportFile.decode(new Uint8Array(buffer));
                setFile(decodedFile);
                setError(null);
            } catch (caughtError) {
                if (caughtError instanceof DOMException && caughtError.name === "AbortError") {
                    return;
                }
                setFile(null);
                setError(caughtError instanceof Error ? caughtError.message : "Cannot load video.");
            }
        };

        void fetchFile();
        return () => {
            storeCurrentVideoTime();
            abortController.abort();
        };
    }, [fileId]);

    if (error) return <span>{error}</span>;
    if (!file) return null;

    const videoSrc = normalizeRelativePath(file.relativePath);

    return (
        <video
            ref={videoRef}
            controls
            preload="metadata"
            playsInline
            style={{height: "30em", maxWidth: "100%"}}
            src={videoSrc}
            onLoadedMetadata={() => {
                const raw = window.localStorage.getItem(storageKey);
                if (!raw || !videoRef.current) {
                    return;
                }
                try {
                    const parsed = JSON.parse(raw) as IVideoInfo;
                    if (typeof parsed.time === "number" && Number.isFinite(parsed.time)) {
                        videoRef.current.currentTime = parsed.time;
                    }
                } catch {
                    // Ignore invalid stored values.
                }
            }}
            onPause={storeCurrentVideoTime}
            onEnded={storeCurrentVideoTime}
        >
            Your browser cannot play this video.
        </video>
    );
};

export default LazyVideo;
