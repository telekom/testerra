import {useEffect, useState, type CSSProperties} from "react";
import {File as ReportFile} from "../model/report-model/framework_pb.ts";

interface LazyImageProps {
    fileId: string;
    className?: string;
    style?: CSSProperties;
    onClick?: (file: ReportFile) => void;
}

const normalizeRelativePath = (path?: string) => {
    if (!path) {
        return "";
    }
    return `..${path.replaceAll("\\", "/")}`;
};

const LazyImage = ({fileId, className, style, onClick}: LazyImageProps) => {
    const [file, setFile] = useState<ReportFile | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        let isMounted = true;

        const fetchFile = async () => {
            try {
                const response = await fetch(`model/files/${fileId}`);
                if (!response.ok) {
                    throw new Error(`Cannot load model/files/${fileId}: ${response.status}`);
                }

                const buffer = await response.arrayBuffer();
                const decodedFile = ReportFile.decode(new Uint8Array(buffer));

                if (isMounted) {
                    setFile(decodedFile);
                    setError(null);
                }
            } catch (caughtError) {
                if (isMounted) {
                    setFile(null);
                    setError(caughtError instanceof Error ? caughtError.message : "Cannot load screenshot.");
                }
            }
        };

        void fetchFile();

        return () => {
            isMounted = false;
        };
    }, [fileId]);

    if (error) {
        return <span>{error}</span>;
    }

    if (!file) {
        return null;
    }

    const title = file.meta?.Title ?? "";
    const alt = file.name ?? title ?? "Screenshot";

    return (
        <img
            src={normalizeRelativePath(file.relativePath)}
            className={className}
            style={style}
            title={title}
            alt={alt}
            onClick={(event) => {
                event.stopPropagation();
                onClick?.(file);
            }}
        />
    );
};

export default LazyImage;
