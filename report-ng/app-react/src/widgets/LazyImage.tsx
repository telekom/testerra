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
