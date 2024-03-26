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
import {ChipType} from "../../services/common-models";
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
    private _executionStatistics: ExecutionStatistics;
    private _selectedStatus: data.ResultStatusType;
    private _selectedClass: string;
    @bindable({defaultBindingMode: bindingMode.twoWay}) _searchInput: string;
    private _availableStatuses: data.ResultStatusType[] | number[];
    private _filteredMethodDetails: MethodDetails[];
    private _showConfigurationMethods: boolean = null;
    private _uniqueStatuses = 0;
    private _uniqueClasses = 0;
    private _loading = false;
    private _sortBy = SortBy.Class;
    private _chipList: IChip[] = [];
    private _filteredClassStatistics: ClassStatistics[] = [];

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

        this._chipList = []
        this._filteredClassStatistics = [];

        if (params.status) {
            this._fillChipList(ChipType.STATUS, this._convertQueryParams(params.status, true));
        }

        if(params.class){
            this._fillChipList(ChipType.CLASS, this._convertQueryParams(params.class, true));
        }

        if(params.q){
            this._fillChipList(ChipType.CUSTOM_TEXT, this._convertQueryParams(params.q, true));
        }

        // only add one chip for all methods when navigating here from timings view
        if(params.methods){
            const chip: IChip = {
                chipType: ChipType.CUSTOM_FILTER_TIMINGS,
                chipElement: this._convertQueryParams(params.methods, true),
            }
            this._chipList.unshift(chip);
        }

        if(params.failureAspect){
            this._fillChipList(ChipType.CUSTOM_FILTER_FAILURE_ASPECTS, params.failureAspect);
        }

        if(this._chipList.length > 0){
            this._fillChipList(ChipType.CLEAR_ALL);
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

    private _fillChipList(chipType: ChipType, chipParam?: any){
        if(Array.isArray(chipParam)){
            chipParam.forEach(param => {
                const chip: IChip = {
                    chipType: chipType,
                    chipElement: param
                }
                this._chipList.push(chip);
            })
        } else {
            const chip: IChip = {
                chipType: chipType,
                chipElement: chipParam,
            }
            if(chipType == ChipType.CLEAR_ALL || chipType == ChipType.CUSTOM_FILTER_FAILURE_ASPECTS || chipType == ChipType.CUSTOM_FILTER_TIMINGS){
                this._chipList.unshift(chip);   // Clear all and Custom chips should be displayed at the beginning of the chip list
            } else {
                this._chipList.push(chip);
            }
        }
    }

    private _filter() {
        this._loading = true;

        if (this._chipList.find(chip => chip.chipType === ChipType.CUSTOM_TEXT)){
            this.queryParams.q = this._convertQueryParams(this._chipList.filter(chip => chip.chipType === ChipType.CUSTOM_TEXT).map(chip => chip.chipElement), false);
        } else {
            delete this._searchInput;
            delete this.queryParams.q;
        }

        if (this._chipList.find(chip => chip.chipType === ChipType.STATUS)){
            this.queryParams.status = this._convertQueryParams(this._chipList.filter(chip => chip.chipType === ChipType.STATUS).map(chip => chip.chipElement), false);
        } else {
            delete this.queryParams.status;
        }

        if (this._chipList.find(chip => chip.chipType === ChipType.CLASS)){
            this.queryParams.class = this._convertQueryParams(this._chipList.filter(chip => chip.chipType === ChipType.CLASS).map(chip => chip.chipElement), false);
        } else {
            delete this.queryParams.class;
        }

        const uniqueClasses = {};
        const uniqueStatuses = {};
        this._filteredMethodDetails = [];
        this._availableStatuses = [];

        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._availableStatuses = executionStatistics.availableStatuses;
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
            if (this._chipList.find(chip => chip.chipType == ChipType.CUSTOM_FILTER_FAILURE_ASPECTS)?.chipElement > 0) {
                relevantFailureAspect = executionStatistics.uniqueFailureAspects[this._chipList.find(chip => chip.chipType == ChipType.CUSTOM_FILTER_FAILURE_ASPECTS).chipElement - 1];
                filterByFailureAspect = true;
            }
            this._executionStatistics = executionStatistics;
            this._executionStatistics.classStatistics.sort((a,b) => a.classIdentifier.localeCompare(b.classIdentifier));

            if(this._filteredClassStatistics.length <= 0){      // create array for class select options that can be manipulated
                this._filteredClassStatistics = [...executionStatistics.classStatistics.sort((a,b) => a.classIdentifier.localeCompare(b.classIdentifier))];

                // remove selected classes from options in select box
                this._chipList.filter(chip => chip.chipType == ChipType.CLASS).forEach(chipClass => {
                    const index = this._filteredClassStatistics.map(stat => stat.classIdentifier).indexOf(chipClass.chipElement, 0);
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
                    return !this._chipList.filter(chip => chip.chipType === ChipType.CLASS).length ||
                    this._chipList.some(chip => {
                        const className = this._classNameValueConverter.toView(classStatistic.classIdentifier, ClassName.full);
                        return chip.chipElement === className || chip.chipElement == classStatistic.classIdentifier;
                    });
                })
                .forEach(classStatistic => {
                    let methodContexts = classStatistic.methodContexts;

                    if (this._chipList.filter(chip => chip.chipType === ChipType.STATUS).length > 0) {
                        const selectedStatusGroups = this._chipList.filter(chip => chip.chipType === ChipType.STATUS).map(chip => {
                            return this._statusConverter.groupStatus(this._statusConverter.normalizeStatus(chip.chipElement));
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

                    if (this._chipList.filter(chip => chip.chipType == ChipType.CUSTOM_TEXT).length > 0) {
                        methodDetails = methodDetails.filter(methodDetail => {
                            return this._chipList.filter(chip => chip.chipType == ChipType.CUSTOM_TEXT).map(chip => chip.chipElement).some(searchTerm => {
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

            if (this._chipList.filter(chip => chip.chipType == ChipType.CUSTOM_FILTER_TIMINGS).length > 0) {
                this._filteredMethodDetails = this._filteredMethodDetails.filter(method => {
                    return this._chipList.filter(chip => chip.chipType == ChipType.CUSTOM_FILTER_TIMINGS).flatMap(chip => chip.chipElement)
                        .includes(method.methodContext.contextValues.id);
                });
            }

            switch (this._sortBy) {
                case SortBy.Method :
                    this._filteredMethodDetails = this._filteredMethodDetails.sort((a,b) => a.identifier.localeCompare(b.identifier));
                    break;
                case SortBy.RunIndex :  // Sort by run index
                    this._filteredMethodDetails = this._filteredMethodDetails.sort((a, b) => a.methodContext.methodRunIndex-b.methodContext.methodRunIndex);
                    break;
                case SortBy.Class :
                default:
                    // Sort by class and method name
                    this._filteredMethodDetails = this._filteredMethodDetails.sort(
                        (a, b) => a.classStatistics.classIdentifier === b.classStatistics.classIdentifier ?
                            a.identifier.localeCompare(b.identifier) : a.classStatistics.classIdentifier.localeCompare(b.classStatistics.classIdentifier));
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
        });
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

    private _statusChanged() {
        this._addFilter(this._selectedStatus, ChipType.STATUS);
        this._selectedStatus = undefined;
    }

    private _classChanged() {
        // remove selected classes from options in select box
        const index = this._filteredClassStatistics.map(stat => stat.classIdentifier).indexOf(this._selectedClass, 0);
        if (index > -1) {
            this._filteredClassStatistics.splice(index, 1);
        }

        this._addFilter(this._selectedClass, ChipType.CLASS);
        this._selectedClass = undefined;
    }

    private _showConfigurationChanged() {
        this._filterOnce();
    }

    private _searchQueryChanged($event) {
        if($event.key == "Enter"){
            this._addFilter(this._searchInput, ChipType.CUSTOM_TEXT);
            this._searchInput = undefined;
        }
    }

    private _addFilter(element, chipType: ChipType){
        if(element){
            if(this._chipList.length <= 0){     // if it is the first chip to appear, the "Clear All" chip has to appear as well
                const chip = {
                    chipType: ChipType.CLEAR_ALL,
                }
                this._chipList.unshift(chip);
            }
            if(!this._chipList.filter(chip => chip.chipType === chipType).map(chip => chip.chipElement).includes(element)){
                const chip: IChip = {
                    chipType: chipType,
                    chipElement: element
                }
                this._chipList.push(chip);
            }
        }

        this._filterOnce();
    }

    private _removeFilter(chipType: ChipType, filterObject){
        if(chipType == ChipType.CLEAR_ALL){
            this._chipList = [];

            delete this.queryParams.methods;
            delete this.queryParams.q;
            delete this.queryParams.status;
            delete this.queryParams.class;
            delete this.queryParams.config;
            delete this.queryParams.failureAspect;
            this.updateUrl(this.queryParams);

            // reset class filter select options
            this._filteredClassStatistics = [...this._executionStatistics.classStatistics.sort((a,b) => a.classIdentifier.localeCompare(b.classIdentifier))];

        } else {

            const index = this._chipList.map(chip => chip.chipElement).indexOf(filterObject)
            this._chipList.splice(index, 1);

            if(chipType == ChipType.CUSTOM_FILTER_TIMINGS || chipType == ChipType.CUSTOM_FILTER_FAILURE_ASPECTS){
                // since the Custom Filter Chip only appears when navigating from another view to the tests view,
                // we can use it for failureAspects and Methods,because they never appear together
                delete this.queryParams.methods;
                delete this.queryParams.failureAspect;
            }

            // insert classes that are not selected as filter anymore back into filter class select options
            if(chipType == ChipType.CLASS && !this._filteredClassStatistics.some(stat => stat.classIdentifier == filterObject)){
                this._filteredClassStatistics.push(this._executionStatistics.classStatistics.find(stat => stat.classIdentifier == filterObject));
                this._filteredClassStatistics.sort((a,b) => a.classIdentifier.localeCompare(b.classIdentifier))
            }

            this.queryParams.q = this._convertQueryParams(this._chipList.filter(chip => chip.chipType === ChipType.CUSTOM_TEXT).map(chip => chip.chipElement), false);
            this.updateUrl(this.queryParams);

            if(this._chipList.length == 1){     // if there is only one chip left, it has to be removed because the last one will be the Clear All chip
                this._chipList = [];
            }
        }
        this._filter()
    }

    private _convertQueryParams(params, toArray: boolean){
        // use "~" as separator because it is not used in id or class names and is not reserved in urls
        if(toArray){
            return params.split("~");
        } else {
            if(params == ""){
                return;
            }
            return params.join("~");
        }
    }
}

interface IChip {
    chipElement?: any;
    chipType: ChipType;
}

