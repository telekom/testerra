/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

import {autoinject} from 'aurelia-framework';
import {MethodDetails, StatisticsGenerator} from "../../services/statistics-generator";
import {NavigationInstruction, RouteConfig, Router} from "aurelia-router";
import {DataSet, Network, DataSetNodes, DataSetEdges} from "vis-network";
import "vis-network/styles/vis-network.css";
import {data} from "../../services/report-model";
import IMethodContext = data.IMethodContext;
import {StatusConverter} from "../../services/status-converter";
import {Options} from "vis-network/dist/types/network/Network";

@autoinject()
export class DependencyNetwork {
    private _router:Router;
    private _container:HTMLDivElement;
    private _resizeListener:EventListener;
    private _queryParams;
    private _network:Network;
    private _methodDetails:MethodDetails;
    private _loadListener:EventListener;

    constructor(
        private _statistics: StatisticsGenerator,
        private _statusConverter:StatusConverter,
    ) {
        this._resizeListener = (ev:Event) => {
            this._container.style.height = (window.innerHeight - this._container.getBoundingClientRect().y - 24) + "px";
            this._network?.redraw();
        }

        this._loadListener = (ev:Event) => {
            // console.log("window load");
            this._resizeListener(ev);
            this._focus();
        }
    }

    private _focus() {
        // console.log("focus node");
        this._network?.focus(this._methodDetails.methodContext.contextValues.id, {
            scale: 2
        });
    }
    //
    // private _generateRoute(methodDetails:IMethodDetails) {
    //     return this._router?.generate("method", {methodId:methodDetails.methodContext.contextValues.id})+"/dependencies";
    // }

    attached() {
        // console.log("attached");
        this._createGraph();

        window.addEventListener("resize", this._resizeListener);
        window.addEventListener("load", this._loadListener);
    }

    detached() {
        window.removeEventListener("resize", this._resizeListener);
        window.removeEventListener("load", this._loadListener);
    }

    activate(
        params: any,
        routeConfig: RouteConfig,
        navInstruction: NavigationInstruction
    ) {
        this._router = navInstruction.router;
        this._queryParams = params;
        this._statistics.getMethodDetails(this._queryParams.methodId).then(methodDetails => {
            this._methodDetails = methodDetails;
            this._createGraph();
        });
    }

    private _createMethodDetails(methodContextIds:string[]):MethodDetails[] {
        return methodContextIds.filter(methodId => {
            return (methodId != this._methodDetails.methodContext.contextValues.id);
        })
        .map(methodId => {
            return this._methodDetails.executionStatistics.executionAggregate.methodContexts[methodId]
        })
        .filter(methodContext => methodContext)
        .map(methodContext => new MethodDetails(methodContext, this._methodDetails.classStatistics))
    }

    private _createGraph() {
        if (!this._methodDetails || !this._container) {
            return;
        }

        const nodes:DataSetNodes[] = [];
        const edges:DataSetEdges[] = [];

        const addNode = (methodDetails:MethodDetails) => {
            const node:DataSetNodes = {
                id: methodDetails.methodContext.contextValues.id,
                label: methodDetails.identifier,
                physics: false,
                color: this._statusConverter.getColorForStatus(methodDetails.methodContext.resultStatus),
                font: {
                    color: "#fff",
                    face: "Roboto, sans-serif",
                },
                value: 1,
            };
            nodes.push(node);
            return node;
        }

        const main = addNode(this._methodDetails);
        //main.mass = 10;
        main.value = 3;

        this._createMethodDetails(this._methodDetails.methodContext.dependsOnMethodContextIds)
            .forEach(otherMethodDetails => {
                addNode(otherMethodDetails);

                edges.push({
                    from: otherMethodDetails.methodContext.contextValues.id,
                    to: this._methodDetails.methodContext.contextValues.id,
                    arrows: {
                        from: {
                            enabled: true,
                            type: "arrow",
                            scaleFactor: 0.7
                        }
                    },
                    label: "depends on",
                    font: {
                        align: "middle",
                        size: 9,
                    },
                })
            })

        this._createMethodDetails(this._methodDetails.methodContext.relatedMethodContextIds)
            .forEach(otherMethodDetails => {
                addNode(otherMethodDetails);

                if (otherMethodDetails.methodContext.methodRunIndex < this._methodDetails.methodContext.methodRunIndex) {
                    edges.push({
                        from: otherMethodDetails.methodContext.contextValues.id,
                        to: this._methodDetails.methodContext.contextValues.id,
                        arrows: {
                            to: {
                                enabled: true,
                                type: "vee",
                                scaleFactor: 0.7
                            }
                        },
                        label: "before",
                        font: {
                            align: "middle",
                            size: 9,
                        },
                        dashes: true,
                    })
                } else {
                    edges.push({
                        from: this._methodDetails.methodContext.contextValues.id,
                        to: otherMethodDetails.methodContext.contextValues.id,
                        arrows: {
                            from: {
                                enabled: true,
                                type: "vee",
                                scaleFactor: 0.7
                            }
                        },
                        label: "after",
                        font: {
                            align: "middle",
                            size: 9,
                        },
                        dashes: true,
                    })
                }
            });

        // create a network
        const data = {
            nodes: new DataSet(nodes),
            edges: new DataSet(edges),
        };
        const options:Options = {
            autoResize: false,
            nodes: {
                shape: 'box',
                scaling:{
                    min:5,
                    max:100,
                    label: {
                        min:8,
                        max:12
                    }
                }
            },
            // edges: {
            //     arrows: {
            //         to: {
            //             enabled: true
            //         }
            //     }
            // },
            layout: {
                hierarchical: {
                    //nodeSpacing: 140,
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
        this._resizeListener(null);
        // console.log("build graph");
        this._network?.destroy();
        this._network = new Network(this._container, data, options);
        this._network.on("click", (params) => {
            if (params.nodes.length > 0) {
                this._router.navigate(this._router.generate("method", {methodId:params.nodes[0]})+"/dependencies");
            }
        });
        this._focus();
    }
}
