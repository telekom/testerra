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

import {DataLoader} from "../../services/data-loader";
import {StatusConverter} from "../../services/status-converter";
import {autoinject} from "aurelia-framework";
import {MethodDetails, StatisticsGenerator} from "../../services/statistics-generator";
import {ClassStatistics, ExecutionStatistics, FailureAspectStatistics} from "../../services/statistic-models";
import {AbstractViewModel} from "../abstract-view-model";
import {data} from "../../services/report-model";
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import "./classes.scss"
import {ClassName, ClassNameValueConverter} from "../../value-converters/class-name-value-converter";
import {ChipNameValueConverter} from "../../value-converters/chip-name-value-converter";
import {bindable} from "aurelia-templating";
import {bindingMode} from "aurelia-binding";
import MethodType = data.MethodType;

enum SortBy {
    Class = "CLASS",
    Method = "METHOD",
    RunIndex = "RUNINDEX",
}

@autoinject()
export class Classes extends AbstractViewModel {
    @bindable({defaultBindingMode: bindingMode.twoWay}) _searchInput: string;
    readonly filters: Filter[] = [
        {
            "type": FilterType.CLASS,
            "cssClass": "class",
            "tooltip": "Class"
        },
        {
            "type": FilterType.CUSTOM_TEXT,
            "cssClass": "text",
            "tooltip": "Custom Filter"
        },
        {
            "type": FilterType.STATUS,
            "cssClass": "status",
            "tooltip": "Status"
        },
        {
            "type": FilterType.CUSTOM_FILTER_TIMINGS,
            "cssClass": "default",
            "tooltip": "Custom Test Filter"
        },
        {
            "type": FilterType.CUSTOM_FILTER_FAILURE_ASPECTS,
            "cssClass": "default",
            "tooltip": "Custom Failure Aspects Filter"
        }
    ]
    private _executionStatistics: ExecutionStatistics;
    private _selectedStatus: data.ResultStatusType;
    private _selectedClass: string;
    private _availableStatuses: data.ResultStatusType[] | number[];
    private _filteredStatuses: data.ResultStatusType[] | number[];
    private _filteredMethodDetails: MethodDetails[];
    private _showConfigurationMethods: boolean = null;
    private _uniqueStatuses = 0;
    private _uniqueClasses = 0;
    private _loading = false;
    private _sortBy = SortBy.Class;
    private _filteredClassStatistics: ClassStatistics[] = [];
    private _chips: IFilterChip[]

    constructor(
        private _dataLoader: DataLoader,
        private _statusConverter: StatusConverter,
        private _chipNameValueConverter: ChipNameValueConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _classNameValueConverter: ClassNameValueConverter,
    ) {
        super();
    }

    activate(
        params: any,
        routeConfig: RouteConfig,
        navInstruction: NavigationInstruction
    ) {
        super.activate(params, routeConfig, navInstruction);

        this._chips = [];
        this._filteredClassStatistics = [];
        this._filteredStatuses = [];

        this._createFilterChips();

        if (params.config) {
            if (params.config.toLowerCase() == "true") {
                this._showConfigurationMethods = true;
            } else {
                this._showConfigurationMethods = false;
            }
        }
        return this._filter();
    }

    private _createFilterChips() {
        this._chips = [];

        for (const type in this.queryParams) {
            const filter = this.filters.find(filter => filter.type == type);

            if (type != 'config' && type != 'methods') {
                this.queryParams[type].split("~").forEach(param => {
                    const chip = {
                        filter: filter,
                        value: param
                    }
                    if (type == 'failureAspect') {
                        this._chips.unshift(chip);
                    } else {
                        this._chips.push(chip);
                    }

                });

            } else if (type == 'methods') {
                // only add one chip for all methods when navigating here from timings view
                const filter = this.filters.find(filter => filter.type == FilterType.CUSTOM_FILTER_TIMINGS)
                const chip: IFilterChip = {
                    filter: filter,
                    value: this.queryParams[type].split("~"),
                }
                this._chips.unshift(chip);
            }
        }
    }

    private _updateAvailableStatuses() {
        const resultStatuses: data.ResultStatusType[] | number[] = [];

        if (this._executionStatistics) {
            this._executionStatistics.classStatistics.forEach(classStat => {
                classStat.methodContexts.forEach(context => {
                    if (this._showConfigurationMethods || context.methodType == 1) {
                        if (!resultStatuses.includes(context.resultStatus)) {
                            resultStatuses.push(context.resultStatus);
                        }
                    }
                });
            });
        }
        this._availableStatuses = resultStatuses;
    }

    private async _filter() {
        this._loading = true;
        const customTextFilter = this.filters.find(filter => filter.type == FilterType.CUSTOM_TEXT);
        const statusFilter = this.filters.find(filter => filter.type == FilterType.STATUS);
        const classFilter = this.filters.find(filter => filter.type == FilterType.CLASS);
        const customFilterFailureAspects = this.filters.find(filter => filter.type == FilterType.CUSTOM_FILTER_FAILURE_ASPECTS);
        const customFilterTimings = this.filters.find(filter => filter.type == FilterType.CUSTOM_FILTER_TIMINGS);

        this._createFilterChips();

        const uniqueClasses = {};
        const uniqueStatuses = {};
        this._filteredMethodDetails = [];

        const executionStatistics = await this._statisticsGenerator.getExecutionStatistics()
        this._executionStatistics = executionStatistics;
        this._updateAvailableStatuses();

        this._filteredStatuses = [...this._availableStatuses.sort((a, b) =>
            this._statusConverter.normalizeStatus(a) - this._statusConverter.normalizeStatus(b))];

        // remove selected statuses from options in select box
        this._chips.filter(chip => chip.filter == statusFilter).forEach(chipStatus => {
            const index = this._filteredStatuses
                .map(stat => this._statusConverter.normalizeStatus(stat))
                .indexOf(this._statusConverter.getStatusForClass(chipStatus.value), 0);
            if (index > -1) {
                this._filteredStatuses.splice(index, 1);
            }
        })

        // this._availableStatuses = Object.keys();
        // this._statusConverter.relevantStatuses
        //     .concat(...[data.ResultStatusType.FAILED_RETRIED, data.ResultStatusType.PASSED_RETRY])
        //     .forEach(status => {
        //         if (executionStatistics.getStatusesCount(this._statusConverter.groupStatus(status)) > 0) {
        //             this._availableStatuses.push(status)
        //         }
        //     });

        let relevantFailureAspect: FailureAspectStatistics;
        let filterByFailureAspect = false;

        if (this._chips.find(chip => chip.filter == customFilterFailureAspects)?.value > 0) {
            relevantFailureAspect = executionStatistics.uniqueFailureAspects[this._chips.find(chip => chip.filter == customFilterFailureAspects).value - 1];
            filterByFailureAspect = true;
        }
        this._executionStatistics.classStatistics.sort((a, b) => a.classIdentifier.localeCompare(b.classIdentifier));

            if (this._filteredClassStatistics.length <= 0) {      // create array for class select options that can be manipulated
                this._filteredClassStatistics = [...executionStatistics.classStatistics.sort((a, b) =>
                    this._classNameValueConverter.toView(a.classIdentifier, 1).localeCompare(this._classNameValueConverter.toView(b.classIdentifier, 1)))];

                // remove selected classes from options in select box
                this._chips.filter(chip => chip.filter == classFilter).forEach(chipClass => {
                    const index = this._filteredClassStatistics.map(stat => stat.classIdentifier).indexOf(chipClass.value, 0);
                    if (index > -1) {
                        this._filteredClassStatistics.splice(index, 1);
                    }
                })
            }

        executionStatistics.classStatistics
            .map(classStatistics => {
                // Determine if we need to enable showing config methods by default if there has any error occured
                if (this._showConfigurationMethods === null) {
                    this._showConfigurationMethods = classStatistics.configStatistics.overallFailed > 0;
                }
                return classStatistics;
            })
            .filter(classStatistic => {
                return !this._chips.filter(chip => chip.filter === classFilter).length ||
                    this._chips.some(chip => {
                        const className = this._classNameValueConverter.toView(classStatistic.classIdentifier, ClassName.full);
                        return chip.value === className || chip.value == classStatistic.classIdentifier;
                    });
            })
            .forEach(classStatistic => {
                let methodContexts = classStatistic.methodContexts;

                if (this._chips.filter(chip => chip.filter === statusFilter).length > 0) {
                    const selectedStatusGroups = this._chips.filter(chip => chip.filter === statusFilter).map(chip => {
                        return this._statusConverter.groupStatus(this._statusConverter.getStatusForClass(chip.value));
                    });

                    methodContexts = methodContexts.filter(methodContext => {
                        return selectedStatusGroups.some(selectedStatusGroup => {
                            return selectedStatusGroup.indexOf(methodContext.resultStatus) >= 0;
                        });
                    });
                }

                if (filterByFailureAspect) {
                    methodContexts = methodContexts.filter(methodContext => relevantFailureAspect.methodContexts.indexOf(methodContext) >= 0);
                }

                if (this._showConfigurationMethods === false) {
                    methodContexts = methodContexts.filter(methodContext => methodContext.methodType == MethodType.TEST_METHOD);
                }

                let methodDetails = methodContexts.map(methodContext => {
                    return new MethodDetails(methodContext, classStatistic);
                });

                if (this._chips.filter(chip => chip.filter == customTextFilter).length > 0) {
                    methodDetails = methodDetails.filter(methodDetail => {
                        return this._chips.filter(chip => chip.filter == customTextFilter).map(chip => chip.value).every(searchTerm => {
                            searchTerm = this._statusConverter.createRegexpFromSearchString(searchTerm);
                            return methodDetail.identifier.match(searchTerm)
                                || methodDetail.failureAspects.some(failureAspect => failureAspect.identifier.match(searchTerm))
                                || methodDetail.failsAnnotation?.description?.match(searchTerm)
                                || methodDetail.failsAnnotation?.ticketString?.match(searchTerm)
                                || methodDetail.promptLogs.some(logMessage => logMessage.message.match(searchTerm))
                                || methodDetail.classStatistics.classIdentifier.match(searchTerm)
                        })
                    });
                }

                methodDetails.forEach(methodDetails => {
                    uniqueClasses[classStatistic.classContext.fullClassName] = true;
                    uniqueStatuses[methodDetails.methodContext.resultStatus] = true;
                    this._filteredMethodDetails.push(methodDetails);
                });

            });

        if (this._chips.filter(chip => chip.filter == customFilterTimings).length > 0) {
                this._filteredMethodDetails = this._filteredMethodDetails.filter(method => {
                    return this._chips.filter(chip => chip.filter == customFilterTimings).flatMap(chip => chip.value)
                        .includes(method.methodContext.contextValues.id);
                });
        }

        switch (this._sortBy) {
            case SortBy.Method :
                this._filteredMethodDetails = this._filteredMethodDetails.sort((a, b) => a.identifier.localeCompare(b.identifier));
                break;
            case SortBy.RunIndex :  // Sort by run index
                this._filteredMethodDetails = this._filteredMethodDetails.sort((a, b) => a.methodContext.methodRunIndex - b.methodContext.methodRunIndex);
                break;
            case SortBy.Class :
            default:
                // Sort by class and method name
                this._filteredMethodDetails = this._filteredMethodDetails.sort(
                    (a, b) => a.classStatistics.classIdentifier === b.classStatistics.classIdentifier ?
                        a.identifier.localeCompare(b.identifier) : this._classNameValueConverter.toView(a.classStatistics.classIdentifier, 1).localeCompare(this._classNameValueConverter.toView(b.classStatistics.classIdentifier, 1)));
        }

        this._uniqueClasses = Object.keys(uniqueClasses).length;
        this._uniqueStatuses = Object.keys(uniqueStatuses).length;
        if (this._showConfigurationMethods) {
            this.queryParams.config = this._showConfigurationMethods;
        } else {
            delete this.queryParams.config;
        }

        this.updateUrl(this.queryParams);
        this._loading = false;
    }

    private _filterOnce() {
        if (!this._loading) {
            this._filter();
        }
    }

    private _sortByRunIndex() {
        this._sortBy = SortBy.RunIndex;
        this._filterOnce();
    }

    private _sortByClass() {
        this._sortBy = SortBy.Class;
        this._filterOnce();
    }

    private _sortByMethod() {
        this._sortBy = SortBy.Method;
        this._filterOnce();
    }

    private _addChipToQueryParams(newParam, currentParam: string): string {
        return currentParam ? currentParam + "~" + newParam : newParam;
    }

    private _statusChanged() {
        if (this._selectedStatus) {
            const index = this._filteredStatuses.indexOf(this._selectedStatus, 0);
            if (index > -1) {
                this._filteredStatuses.splice(index, 1);
            }
            this.queryParams.status = this._addChipToQueryParams(this._statusConverter.getClassForStatus(this._selectedStatus), this.queryParams.status);
            this.updateUrl(this.queryParams);
            this._selectedStatus = undefined;

            this._filterOnce();
        }
    }

    private _classChanged() {
        if (this._selectedClass) {
            // remove selected classes from options in select box
            const index = this._filteredClassStatistics.map(stat => stat.classIdentifier).indexOf(this._selectedClass, 0);
            if (index > -1) {
                this._filteredClassStatistics.splice(index, 1);
            }
            this.queryParams.class = this._addChipToQueryParams(this._selectedClass, this.queryParams.class);
            this.updateUrl(this.queryParams);
            this._selectedClass = undefined;

            this._filterOnce();
        }
    }

    private _showConfigurationChanged() {
        this._filterOnce();
    }

    private _searchQueryChanged($event) {
        if (this._searchInput) {
            if ($event.key == "Enter") {
                this.queryParams.q = this._addChipToQueryParams(this._searchInput, this.queryParams.q);
                this.updateUrl(this.queryParams);
                this._searchInput = undefined;

                this._filterOnce();
            }
        }
    }

    private _removeAllFilters() {
        this._chips = [];

        this.queryParams = {}
        this.updateUrl(this.queryParams);

        // reset class filter select options
        this._filteredClassStatistics = [...this._executionStatistics.classStatistics.sort((a, b) =>
            this._classNameValueConverter.toView(a.classIdentifier, 1).localeCompare(this._classNameValueConverter.toView(b.classIdentifier, 1)))];

        //reset status filter select options
        this._filteredStatuses = [...this._availableStatuses.sort((a, b) => this._statusConverter.normalizeStatus(a) - this._statusConverter.normalizeStatus(b))];

        this._filter();
    }

    private _removeFilter(filterType: FilterType, filterObject?) {

        const filterTypeArray = this.queryParams[filterType].split("~");
        const index = filterTypeArray.indexOf(filterObject);
        filterTypeArray.splice(index, 1);
        if (filterTypeArray.length > 0) {
            this.queryParams[filterType] = filterTypeArray.join("~");
        } else {
            delete this.queryParams[filterType];
        }
        this.updateUrl(this.queryParams);

        if (filterType == FilterType.CUSTOM_FILTER_TIMINGS || filterType == FilterType.CUSTOM_FILTER_FAILURE_ASPECTS) {
            // since the Custom Filter Chip only appears when navigating from another view to the tests view,
            // we can use it for failureAspects and Methods,because they never appear together
            delete this.queryParams.methods;
            delete this.queryParams.failureAspect;
        }

        // insert classes that are not selected as filter anymore back into filter class select options
        if (filterType == FilterType.CLASS && !this._filteredClassStatistics.some(stat => stat.classIdentifier == filterObject)) {
            this._filteredClassStatistics.push(this._executionStatistics.classStatistics.find(stat => stat.classIdentifier == filterObject));
            this._filteredClassStatistics.sort((a, b) =>
                this._classNameValueConverter.toView(a.classIdentifier, 1).localeCompare(this._classNameValueConverter.toView(b.classIdentifier, 1)));
        }

        // insert statuses that are not selected as filter anymore back into filter status select options
        if (filterType == FilterType.STATUS && !this._filteredStatuses.some(stat => stat == this._statusConverter.getStatusForClass(filterObject))) {
            this._filteredStatuses.push(this._availableStatuses.find(stat => stat == this._statusConverter.getStatusForClass(filterObject)));
            this._filteredStatuses.sort((a, b) => this._statusConverter.normalizeStatus(a) - this._statusConverter.normalizeStatus(b));
        }

        this.updateUrl(this.queryParams);

        this._filter();
    }
}

export enum FilterType {
    CLASS = "class",
    CUSTOM_TEXT = "q",
    STATUS = "status",
    CUSTOM_FILTER_TIMINGS = "methods",
    CUSTOM_FILTER_FAILURE_ASPECTS = "failureAspect"
}

type Filter = {
    type: FilterType,
    cssClass: string,
    tooltip: string
}

export interface IFilterChip {
    element?: HTMLElement,
    filter: Filter,
    value: any
}

