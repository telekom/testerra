import {autoinject} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {AbstractViewModel} from "../abstract-view-model";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {StatusConverter} from "../../services/status-converter";
import {data} from "../../services/report-model";
import ILogMessage = data.ILogMessage;

@autoinject()
export class Logs extends AbstractViewModel {
    private _searchRegexp: RegExp;
    private _loading = false;
    private _logMessages:ILogMessage[];
    private _availableLogLevels;
    private _selectedLogLevel;

    constructor(
        private _statistics: StatisticsGenerator,
        private _statusConverter:StatusConverter
    ) {
        super();
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._filter();
    }

    private _filter() {
        if (this.queryParams?.q?.trim().length > 0) {
            this._searchRegexp = this._statusConverter.createRegexpFromSearchString(this.queryParams.q);
        } else {
            this._searchRegexp = null;
            delete this.queryParams.q;
        }

        this._loading = true;
        this._statistics.getExecutionStatistics().then(executionStatistics => {
            const logMessages:ILogMessage[] = [];
            const logLevels = {};

            const filterPredicate = (this._selectedLogLevel?(logMessage:ILogMessage) => this._selectedLogLevel == logMessage.type:(logMessage:ILogMessage) => logMessage);

            const addLevel = (value:ILogMessage) => {
                logLevels[value.type] = 1;
                return value;
            }

            executionStatistics.executionAggregate.executionContext.logMessages
                .map(addLevel)
                .filter(filterPredicate)
                .forEach(value => {
                    logMessages.push(value);
                });

            executionStatistics.classStatistics
                .flatMap(value => value.methodContexts)
                .flatMap(value => value.testSteps)
                .flatMap(value => value.actions)
                .flatMap(value => value.entries)
                .map(value => value.logMessage)
                .map(addLevel)
                .filter(filterPredicate)
                .forEach(value => {
                    logMessages.push(value)
                });

            this._logMessages = logMessages.sort((a, b) => a.timestamp-b.timestamp);

            if (!this._availableLogLevels) {
                this._availableLogLevels = [];
                for (const level in logLevels) {
                    this._availableLogLevels.push({
                        level: Number.parseInt(level),
                    });
                }
            }

            this._loading = false;
            this.updateUrl(this.queryParams);
        });
    }

    private _logLevelChanged() {
        this._filter();
    }
}
