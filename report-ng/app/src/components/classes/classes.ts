import {DataLoader} from "../../services/data-loader";
import {StatusConverter} from "../../services/status-converter";
import {autoinject} from "aurelia-framework";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {ClassStatistics} from "../../services/statistic-models";
import {AbstractViewModel} from "../abstract-view-model";
import {data} from "../../services/report-model";
import IMethodContext = data.IMethodContext;
import IClassContext = data.IClassContext;

interface MethodAggregate {
    methodContext:IMethodContext,
    classContext:IClassContext
}

@autoinject()
export class Classes extends AbstractViewModel {

    private _classStatistics: ClassStatistics[] = [];
    private _selectedClass;
    private _selectedStatus;
    private _availableStatuses:number[];
    private _filteredMethodAggregates:MethodAggregate[];
    private _searchQuery:string;

    constructor(
        private _dataLoader: DataLoader,
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
        super();
    }

    attached() {

        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._availableStatuses = this._statusConverter.relevantStatuses;
            this._classStatistics = executionStatistics.classStatistics;
            this._filter();
        });
    }

    private _filter() {
        console.log("filter", this._selectedClass, this._selectedStatus, this._searchQuery);
        this._filteredMethodAggregates = [];
        this._classStatistics
            .filter(classStatistic => {
                return (!this._selectedClass || classStatistic.classAggregate.classContext.contextValues.id === this._selectedClass)
            })
            .forEach(classStatistic => {
                classStatistic.classAggregate.methodContexts
                    .filter(methodContext => {
                        return (!this._selectedStatus || methodContext.contextValues.resultStatus == this._selectedStatus)
                    })
                    .forEach(methodContext => {
                        this._filteredMethodAggregates.push({
                            methodContext: methodContext,
                            classContext: classStatistic.classAggregate.classContext
                        });
                    })
            })
    }
}

