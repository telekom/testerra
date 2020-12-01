import {DataLoader} from "../../services/data-loader";
import {StatusConverter} from "../../services/status-converter";
import {autoinject} from "aurelia-framework";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {ClassStatistics, ExecutionStatistics, FailureAspectStatistics} from "../../services/statistic-models";
import {AbstractViewModel} from "../abstract-view-model";
import {data} from "../../services/report-model";
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import IMethodContext = data.IMethodContext;
import IClassContext = data.IClassContext;
import MethodType = data.MethodType;
import ResultStatusType = data.ResultStatusType;

interface MethodAggregate {
    methodContext:IMethodContext,
    classStatistics:ClassStatistics
    failureAspect?:FailureAspectStatistics,
}

@autoinject()
export class Classes extends AbstractViewModel {

    private _executionStatistics: ExecutionStatistics;
    private _selectedClass:string;
    private _selectedStatus:number;
    private _availableStatuses = this._statusConverter.relevantStatuses;
    private _filteredMethodAggregates:MethodAggregate[];
    private _showConfigurationMethods:boolean = null;
    private _searchQuery:string;
    private _searchRegexp:RegExp;
    //private _uniqueFailureAspects = 0;
    private _uniqueStatuses = 0;
    private _uniqueClasses = 0;
    private _loading = false;

    constructor(
        private _dataLoader: DataLoader,
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
        super();
    }

    activate(
        params: any,
        routeConfig: RouteConfig,
        navInstruction: NavigationInstruction
    ) {
        super.activate(params, routeConfig, navInstruction);
        console.log("activate", params);
        if (params.q) {
            this._searchQuery = params.q;
        }
        if (params.class) {
            this._selectedClass = params.class;
        } else {
            this._selectedClass = null;
        }
        if (params.status) {
            this._selectedStatus = this._statusConverter.getStatusForClass(params.status);
        } else {
            this._selectedStatus = null;
        }

        if (params.config) {
            if (params.config.toLowerCase() == "true") {
                this._showConfigurationMethods = true;
            } else {
                this._showConfigurationMethods = false;
            }
        }
        this._filter();
    }

    private _filter() {
        this._loading = true;

        const queryParams:any = {};
        if (this._searchQuery?.length > 0) {
            this._searchRegexp = this._statusConverter.createRegexpFromSearchString(this._searchQuery);
            queryParams.q = this._searchQuery;
        } else {
            this._searchRegexp = null;
        }

        if (this._selectedClass) {
            queryParams.class = this._selectedClass;
        }

        if (this._selectedStatus && this._selectedStatus >= 0) {
            queryParams.status = this._statusConverter.getClassForStatus(this._selectedStatus);
        }

        const uniqueClasses = {};
        const uniqueStatuses = {};
        //const uniqueFailureAspects = {};

        let relevantStatuses:ResultStatusType[] = null;

        if (this._selectedStatus == ResultStatusType.PASSED) {
            relevantStatuses = this._statusConverter.passedStatuses;
        } else if (this._selectedStatus == ResultStatusType.FAILED) {
            relevantStatuses = [ResultStatusType.FAILED, ResultStatusType.FAILED_RETRIED];
        } else if (this._selectedStatus) {
            relevantStatuses = [ this._selectedStatus ];
        }

        this._filteredMethodAggregates = [];

        console.log("filter", queryParams);
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;

            executionStatistics.classStatistics
                .map(classStatistics => {
                    // Determine if we need to enable showing config methods by default if there has any error occured
                    if (this._showConfigurationMethods === null) {
                        this._showConfigurationMethods = classStatistics.configStatistics.overallFailed > 0;
                    }
                    return classStatistics;
                })
                .filter(classStatistic => {
                    return !this._selectedClass || this._selectedClass == classStatistic.classIdentifier
                })
                .forEach(classStatistic => {
                    classStatistic.methodContexts
                        .filter(methodContext => {
                            return (!relevantStatuses || relevantStatuses.indexOf(methodContext.contextValues.resultStatus) >= 0)
                        })
                        .filter(methodContext => {
                            return (this._showConfigurationMethods == true
                                || (
                                    this._showConfigurationMethods == false
                                    && methodContext.methodType == MethodType.TEST_METHOD
                                )
                            )
                        })
                        .map(methodContext => {
                            return {
                                classStatistics: classStatistic,
                                failureAspect: methodContext.errorContext ? new FailureAspectStatistics().setErrorContext(methodContext.errorContext) : null,
                                methodContext: methodContext
                            }
                        })
                        .filter(methodAggregate => {
                            return (
                                !this._searchRegexp
                                || (
                                    methodAggregate.failureAspect?.name.match(this._searchRegexp)
                                    || methodAggregate.methodContext.contextValues.name.match(this._searchRegexp)
                                )
                            );
                        })
                        .forEach(methodAggregate => {
                            uniqueClasses[classStatistic.classContext.fullClassName] = true;
                            uniqueStatuses[methodAggregate.methodContext.contextValues.resultStatus] = true;
                            // if (methodAggregate.failureAspect) {
                            //     uniqueFailureAspects[methodAggregate.failureAspect.name] = true;
                            // }
                            this._filteredMethodAggregates.push(methodAggregate);
                        })
                });

            this._uniqueClasses = Object.keys(uniqueClasses).length;
            this._uniqueStatuses = Object.keys(uniqueStatuses).length;
            //this._uniqueFailureAspects = Object.keys(uniqueFailureAspects).length;
            if (this._showConfigurationMethods) {
                queryParams.config = this._showConfigurationMethods;
            }

            this.updateUrl(queryParams);
            console.log("done loading");
            this._loading = false;
        });
    }

    private _filterOnce() {
        if (!this._loading) {
            this._filter();
        }
    }

    private _statusChanged() {
        console.log("status changed");
        this._filterOnce();
    }

    private _classChanged() {
        console.log("class changed");
        this._filterOnce();
    }


    private _showConfigurationChanged() {
        console.log("show configuration changed");
        this._filterOnce();
    }

    private _searchQueryChanged() {
        console.log("search query changed");
        this._filterOnce();
    }
}

