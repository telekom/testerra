import {useEffect, useMemo, useRef, useState} from "react";
import {useNavigate, useOutletContext} from "react-router-dom";
import {Box} from "@mui/material";
import {DataSet} from "vis-data/peer";
import {Network, type Data, type Edge, type Node, type Options} from "vis-network/peer";
import "vis-network/styles/vis-network.css";
import type {MethodDetails} from "../../model/MethodDetails.ts";
import {StatusService} from "../../model/status-service.tsx";
import {useReportData} from "../../provider/DataProvider.tsx";
import NoResultsCard from "../../widgets/NoResultsCard.tsx";

const Dependencies = () => {
    const methodDetail = useOutletContext<MethodDetails | undefined>();
    const {executionMngr} = useReportData();
    const navigate = useNavigate();
    const containerRef = useRef<HTMLDivElement | null>(null);
    const networkRef = useRef<Network | null>(null);
    const [graphHeight, setGraphHeight] = useState(500);

    const graphData = useMemo(() => {
        if (!methodDetail || !executionMngr) {
            return undefined;
        }

        const currentMethod = methodDetail.methodContext;
        const currentId = currentMethod.contextValues?.id;
        if (!currentId) {
            return undefined;
        }

        const createMethodDetails = (methodContextIds: string[] = []) => methodContextIds
            .map((methodId) => executionMngr.getMethodDetails(methodId))
            .filter((value): value is MethodDetails => Boolean(value));

        const dependsOnDetails = createMethodDetails(currentMethod.dependsOnMethodContextIds ?? []);
        const relatedDetails = createMethodDetails(currentMethod.relatedMethodContextIds ?? []);

        const nodes: Node[] = [];
        const edges: Edge[] = [];
        const knownNodeIds = new Set<string>();

        const addNode = (details: MethodDetails, isMainNode: boolean) => {
            const id = details.methodContext.contextValues?.id;
            if (!id || knownNodeIds.has(id)) {
                return;
            }
            knownNodeIds.add(id);

            nodes.push({
                id,
                label: details.identifier,
                physics: false,
                shape: "box",
                color: StatusService.getColor(details.methodContext.resultStatus ?? ""),
                font: {
                    color: "#fff",
                    face: "Roboto, sans-serif",
                    size: 12,
                },
                value: isMainNode ? 3 : 1,
            });
        };

        addNode(methodDetail, true);

        dependsOnDetails.forEach((otherDetails) => {
            addNode(otherDetails, false);
            const otherId = otherDetails.methodContext.contextValues?.id;
            if (!otherId) {
                return;
            }
            edges.push({
                from: otherId,
                to: currentId,
                arrows: {
                    from: {
                        enabled: true,
                        type: "arrow",
                        scaleFactor: 0.7,
                    },
                },
                label: "depends on",
                font: {
                    align: "middle",
                    size: 9,
                },
            });
        });

        const currentRunIndex = currentMethod.methodRunIndex ?? Number.NEGATIVE_INFINITY;
        relatedDetails.forEach((otherDetails) => {
            addNode(otherDetails, false);
            const otherId = otherDetails.methodContext.contextValues?.id;
            if (!otherId) {
                return;
            }

            const otherRunIndex = otherDetails.methodContext.methodRunIndex ?? Number.NEGATIVE_INFINITY;
            if (otherRunIndex < currentRunIndex) {
                edges.push({
                    from: otherId,
                    to: currentId,
                    arrows: {
                        to: {
                            enabled: true,
                            type: "vee",
                            scaleFactor: 0.7,
                        },
                    },
                    label: "before",
                    dashes: true,
                    font: {
                        align: "middle",
                        size: 9,
                    },
                });
                return;
            }

            edges.push({
                from: currentId,
                to: otherId,
                arrows: {
                    from: {
                        enabled: true,
                        type: "vee",
                        scaleFactor: 0.7,
                    },
                },
                label: "after",
                dashes: true,
                font: {
                    align: "middle",
                    size: 9,
                },
            });
        });

        return {currentId, nodes, edges};
    }, [executionMngr, methodDetail]);

    useEffect(() => {
        const resize = () => {
            if (!containerRef.current) {
                return;
            }
            const y = containerRef.current.getBoundingClientRect().y;
            const nextHeight = Math.max(320, window.innerHeight - y - 24);
            setGraphHeight(nextHeight);
            networkRef.current?.redraw();
        };
        resize();
        window.addEventListener("resize", resize);
        window.addEventListener("load", resize);
        return () => {
            window.removeEventListener("resize", resize);
            window.removeEventListener("load", resize);
        };
    }, []);

    useEffect(() => {
        if (!containerRef.current || !graphData) {
            networkRef.current?.destroy();
            networkRef.current = null;
            return;
        }

        const data: Data = {
            nodes: new DataSet(graphData.nodes),
            edges: new DataSet(graphData.edges),
        };
        const options: Options = {
            autoResize: false,
            nodes: {
                shape: "box",
                scaling: {
                    min: 5,
                    max: 100,
                    label: {
                        min: 8,
                        max: 12,
                    },
                },
            },
            layout: {
                hierarchical: {
                    levelSeparation: 100,
                    direction: "UD",
                    sortMethod: "directed",
                },
            },
            physics: {
                hierarchicalRepulsion: {
                    avoidOverlap: 1,
                },
            },
        };

        networkRef.current?.destroy();
        const network = new Network(containerRef.current, data, options);
        networkRef.current = network;

        network.on("click", (params) => {
            const clickedNodeId = params.nodes[0];
            if (!clickedNodeId) {
                return;
            }
            navigate(`/method/${String(clickedNodeId)}/dependencies`);
        });

        network.focus(graphData.currentId, {scale: 2});
        network.redraw();

        return () => {
            network.destroy();
            if (networkRef.current === network) {
                networkRef.current = null;
            }
        };
    }, [graphData, navigate]);

    if (!methodDetail) {
        return <NoResultsCard title="No method selected"/>;
    }

    if (!graphData || graphData.nodes.length <= 1 || graphData.edges.length === 0) {
        return <NoResultsCard title="No dependencies found for this method"/>;
    }

    return (
        <Box
            ref={containerRef}
            sx={{
                width: "100%",
                height: `${graphHeight}px`,
            }}
        >
        </Box>
    );
};
export default Dependencies;
