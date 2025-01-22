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

import {autoinject, observable} from 'aurelia-framework';
import {MethodDetails, StatisticsGenerator} from "../../services/statistics-generator";
import {NavigationInstruction, RouteConfig, Router} from "aurelia-router";
import "vis-network/styles/vis-network.css";
import {StatusConverter} from "../../services/status-converter";
import * as echarts from "echarts";
import {EChartsOption} from "echarts";
import {GraphNodeItemOption} from "echarts/types/src/chart/graph/GraphSeries";
import {MethodType} from "../../services/report-model/framework_pb";
import {AbstractViewModel} from "../abstract-view-model";

@autoinject()
export class DependencyNetwork2 extends AbstractViewModel {
    private _router: Router;

    private _option: EChartsOption;
    @observable()
    private _chart: echarts.ECharts;

    private _data: GraphNodeItemOption [] = [];
    private _demoData = [];
    private _demoLinks = [];
    // private _nodeLinks: GraphEdgeItemOption[] = [];
    private _nodeLinks = [];

    // private _container: HTMLDivElement;
    private _queryParams;
    // private _network: Network;
    private _methodDetails: MethodDetails;

    // private _loadListener: EventListener;

    constructor(
        private _statistics: StatisticsGenerator,
        private _statusConverter: StatusConverter,
    ) {
        super();
        this._option = {};
    }

    private _focus() {
    }

    //
    // private _generateRoute(methodDetails:IMethodDetails) {
    //     return this._router?.generate("method", {methodId:methodDetails.methodContext.contextValues.id})+"/dependencies";
    // }

    attached() {
    }

    detached() {
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
            this._prepareChartData()
            // this._setChartOption();
            this._setDemoOptions();
        });
    }

    private _createMethodDetails(methodContextIds: string[]): MethodDetails[] {
        return methodContextIds
            .map(methodId => {
                return this._methodDetails.executionStatistics.executionAggregate.methodContexts[methodId]
            })
            .filter(methodContext => methodContext)
            .map(methodContext => new MethodDetails(methodContext, this._methodDetails.classStatistics))
    }

    async _prepareChartData() {
        const addNode = (methodDetails: MethodDetails) => {
            const symbol = methodDetails.methodContext.methodType == MethodType.TEST_METHOD ? 'roundRect' : 'circle';
            const node: GraphNodeItemOption = {
                name: methodDetails.identifier,
                symbol: symbol,
                // symbolSize: [150, 40],
                id: methodDetails.methodContext.methodRunIndex.toString(),
                value: 2
            };
            this._data.push(node);
            return node;
        }

        addNode(this._methodDetails);

        this._createMethodDetails(this._methodDetails.methodContext.relatedMethodContextIds)
            .forEach(otherMethod => {
                addNode(otherMethod);
            });

        let sourceString = '';
        this._data
            .sort((a, b) => Number(a.id) - Number(b.id))
            .forEach(item => {
                if (sourceString !== '') {
                    this._nodeLinks.push({source: sourceString, target: item.id});
                }
                sourceString = item.id;
            });

        this._demoData = [
            {
                name: 'Node 1',
                symbol: 'roundRect'
                // symbolSize: [150, 40]
            },
            {
                name: 'Node 2'
            },
            {
                name: 'Node 3'
            },
            {
                name: 'Node 4'
            }
        ];

        this._demoLinks = [
            {
                source: 0,
                target: 1,
                // symbolSize: [5, 20],
                label: {
                    show: true
                },
                lineStyle: {
                    width: 5,
                    curveness: 0.2
                }
            },
            {
                source: 'Node 2',
                target: 'Node 1',
                label: {
                    show: true
                },
                lineStyle: {
                    curveness: 0.2
                }
            },
            {
                source: 'Node 1',
                target: 'Node 3',
            },
            {
                source: 'Node 2',
                target: 'Node 3'
            },
            {
                source: 'Node 2',
                target: 'Node 4'
            },
            {
                source: 'Node 1',
                target: 'Node 4'
            }
        ];
        // console.log(this._nodeLinks);
    }


    private _setChartOption() {
        // let chartWidth = this._chart.getWidth();
        // let chartHeight = this._chart.getHeight();


        //
        // data = [
        //     {name: 'beforeTest', x: chartWidth * 0.1, y: chartHeight / 2},
        //     {name: 'beforeClass', x: chartWidth * 0.3, y: chartHeight / 2},
        //     {name: 'beforeMethod', x: chartWidth * 0.5, y: chartHeight / 2},
        //     {name: 'testMethod', x: chartWidth * 0.7, y: chartHeight / 2, fixed: true, symbol: 'roundRect', symbolSize: [80, 40]},
        //     {name: 'afterMethod', x: chartWidth * 0.9, y: chartHeight / 2}
        // ];
        //
        // data.push({name: 'beforeSuite', x: chartWidth * 0.05, y: chartHeight / 2});

        // let item : GraphNodeItemOption = {};
        // item.name = 'afterSuite';
        // item.x = chartWidth * 0.95;
        // item.y = chartHeight / 2;
        // data.push(item);

        // this._nodeData.push({name: 'testMethod'});
        // this._nodeData.push({name: 'beforeMethod'});
        // this._nodeData.push({name: 'beforeTest'});
        // console.log(nodeData);
        // this._option.dataset

        this._option = {
            tooltip: {},
            // animation: false,
            animationDurationUpdate: 0,
            animationEasingUpdate: 'exponentialOut',
            animationDuration: 300,
            series: [
                {
                    type: 'graph',
                    layout: 'force',
                    symbolSize: 50,
                    roam: true,
                    label: {
                        show: true
                    },
                    edgeSymbol: ['circle', 'arrow'],
                    edgeSymbolSize: [4, 10],
                    edgeLabel: {
                        fontSize: 20
                    },
                    force: {
                        repulsion: 500,
                        edgeLength: 200,
                        gravity: 0.01
                    },
                    // data: [
                    //     {name: 'beforeSuite', x: chartWidth * 0.1, y: chartHeight / 2},
                    //     {name: 'beforeClassPassingTest1', x: chartWidth * 0.3, y: chartHeight / 2},
                    //     {name: 'beforeMethodPassingTest1', x: chartWidth * 0.5, y: chartHeight / 2}
                    // ],
                    data: this._data,
                    links: this._nodeLinks,
                    // links: [],
                    // links: [
                    //     {
                    //         source: 'beforeSuite',
                    //         target: '"beforeClassPassingTest1"'
                    //     },
                    //     {
                    //         source: '"beforeClassPassingTest1"',
                    //         target: '"beforeMethodPassingTest1"'
                    //     },
                    //     ],
                    //     {
                    //         source: 'beforeMethod',
                    //         target: 'testMethod'
                    //     },
                    //     {
                    //         source: 'testMethod',
                    //         target: 'afterMethod'
                    //     }
                    // ],
                    emphasis: {
                        focus: 'adjacency',
                        lineStyle: {
                            width: 10
                        }
                    },
                    lineStyle: {
                        opacity: 0.9,
                        width: 2,
                        curveness: 0
                    }
                }
            ]
        }


        // if (!this._methodDetails || !this._container) {
        //     return;
        // }
        //
        // const nodes:Node[] = [];
        // const edges:Edge[] = [];
        //
        // const addNode = (methodDetails:MethodDetails) => {
        //     const node:Node = {
        //         id: methodDetails.methodContext.contextValues.id,
        //         label: methodDetails.identifier,
        //         physics: false,
        //         color: this._statusConverter.getColorForStatus(methodDetails.methodContext.resultStatus),
        //         font: {
        //             color: "#fff",
        //             face: "Roboto, sans-serif",
        //         },
        //         value: 1,
        //     };
        //     nodes.push(node);
        //     return node;
        // }
        //
        // // Node of current method details
        // const main = addNode(this._methodDetails);
        // //main.mass = 10;
        // main.value = 3;
        //
        // this._createMethodDetails(this._methodDetails.methodContext.dependsOnMethodContextIds)
        //     .forEach(otherMethodDetails => {
        //         addNode(otherMethodDetails);
        //
        //         edges.push({
        //             from: otherMethodDetails.methodContext.contextValues.id,
        //             to: this._methodDetails.methodContext.contextValues.id,
        //             arrows: {
        //                 from: {
        //                     enabled: true,
        //                     type: "arrow",
        //                     scaleFactor: 0.7
        //                 }
        //             },
        //             label: "depends on",
        //             font: {
        //                 align: "middle",
        //                 size: 9,
        //             },
        //         })
        //     })
        // console.log(this._methodDetails);
        // this._createMethodDetails(this._methodDetails.methodContext.relatedMethodContextIds)
        //     .forEach(otherMethodDetails => {
        //         addNode(otherMethodDetails);
        //
        //         if (otherMethodDetails.methodContext.methodRunIndex < this._methodDetails.methodContext.methodRunIndex) {
        //             edges.push({
        //                 from: otherMethodDetails.methodContext.contextValues.id,
        //                 to: this._methodDetails.methodContext.contextValues.id,
        //                 arrows: {
        //                     to: {
        //                         enabled: true,
        //                         type: "vee",
        //                         scaleFactor: 0.7
        //                     }
        //                 },
        //                 label: "before",
        //                 font: {
        //                     align: "middle",
        //                     size: 9,
        //                 },
        //                 dashes: true,
        //             })
        //         } else {
        //             edges.push({
        //                 from: this._methodDetails.methodContext.contextValues.id,
        //                 to: otherMethodDetails.methodContext.contextValues.id,
        //                 arrows: {
        //                     from: {
        //                         enabled: true,
        //                         type: "vee",
        //                         scaleFactor: 0.7
        //                     }
        //                 },
        //                 label: "after",
        //                 font: {
        //                     align: "middle",
        //                     size: 9,
        //                 },
        //                 dashes: true,
        //             })
        //         }
        //     });
        //
        // // console.log("Nodes", nodes);
        // // console.log("Edges", edges);
        //
        // // create a network
        // const data: Data = {
        //     nodes: new DataSet(nodes),
        //     edges: new DataSet(edges),
        // };
        // const options:Options = {
        //     autoResize: false,
        //     nodes: {
        //         shape: 'box',
        //         scaling:{
        //             min:5,
        //             max:100,
        //             label: {
        //                 min:8,
        //                 max:12
        //             }
        //         }
        //     },
        //     // edges: {
        //     //     arrows: {
        //     //         to: {
        //     //             enabled: true
        //     //         }
        //     //     }
        //     // },
        //     layout: {
        //         hierarchical: {
        //             //nodeSpacing: 140,
        //             levelSeparation: 100,
        //             direction: "UD",
        //             sortMethod: "directed",
        //         },
        //     },
        //     physics: {
        //         hierarchicalRepulsion: {
        //             avoidOverlap: 1,
        //         },
        //     },
        // };
        // this._resizeListener(null);
        // console.log("build graph");
        // this._network?.destroy();
        // this._network = new Network(this._container, data, options);
        // this._network.on("click", (params) => {
        //     if (params.nodes.length > 0) {
        //         this._router.navigate(this._router.generate("method", {methodId:params.nodes[0]})+"/dependencies");
        //     }
        // });
        // this._focus();
    }

    private _setDemoOptions() {

        const data = [
            {name: 'beforeClass', value: 1, id: '1', symbol: 'rect', symbolSize: [150, 40]},
            {name: 'beforeMethod', value: 2, id: '2', symbol: 'rect', symbolSize: [150, 40]},
            {name: 'myFancyTestMethodWithFoo', value: 1, id: '3', symbol: 'rect', symbolSize: [150, 40]},
            {name: 'anotherFancyTestMethodWithBar', value: 2, id: '4', symbol: 'rect', symbolSize: [150, 40]},
            {name: 'afterMethod', value: 1, id: '5', symbol: 'rect', symbolSize: [150, 40]},
        ];

        const links = [
            {source: '1', target: '2'},
            {source: '2', target: '4'},
            {source: '3', target: '4'},
            {source: '4', target: '5'},
        ]
        this._option = {
            tooltip: {},
            xAxis: {
                type: 'category',
                // boundaryGap: false,
                data: data.map(item => item.id)
                // data: this._data.map(item => item.id)
            },
            yAxis: {
                type: 'value'
            },
            series: [
                {
                    type: 'graph',
                    layout: 'none',
                    coordinateSystem: 'cartesian2d',
                    symbolSize: 40,
                    label: {
                        show: true
                    },
                    edgeSymbol: ['circle', 'arrow'],
                    edgeSymbolSize: [4, 10],
                    // data: this._data,
                    data: data,
                    // links: this._nodeLinks,
                    links: links,
                    lineStyle: {
                        color: '#2f4554'
                    }
                }
            ]
        };


    }

}
