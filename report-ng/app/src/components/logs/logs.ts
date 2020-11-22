import {autoinject} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {AbstractViewModel} from "../abstract-view-model";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {StatusConverter} from "../../services/status-converter";
import {FailureAspectStatistics} from "../../services/statistic-models";
import {data} from "../../services/report-model";
import IPLogMessage = data.IPLogMessage;
import {util} from "protobufjs";
import float = util.float;

@autoinject()
export class Logs extends AbstractViewModel {
    private _searchRegexp: RegExp;
    private _loading = false;
    private _logMessages:IPLogMessage[];
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
        }

        this._loading = true;
        this._statistics.getExecutionStatistics().then(executionStatistics => {
            const logMessages:IPLogMessage[] = [];
            const logLevels = {};

            const filterPredicate = (value:IPLogMessage) => {
                return (
                    (!this._selectedLogLevel || (this._selectedLogLevel == value.type))
                    && (!this._searchRegexp || value.message.match(this._searchRegexp))
                )
            }

            executionStatistics.executionAggregate.executionContext.logMessages
                .filter(filterPredicate)
                .forEach(value => {
                    logLevels[value.type] = 1;
                    logMessages.push(value);
                });

            executionStatistics.classStatistics
                .flatMap(value => value.classAggregate)
                .flatMap(value => value.methodContexts)
                .flatMap(value => value.testSteps)
                .flatMap(value => value.testStepActions)
                .flatMap(value => value.logMessages)
                .filter(filterPredicate)
                .forEach(value => {
                    logLevels[value.type] = 1;
                    logMessages.push(value)
                });

            this._logMessages = logMessages.sort((a, b) => a.timestamp-b.timestamp);

            this._availableLogLevels = [];
            for (const level in logLevels) {
                this._availableLogLevels.push({
                    level: Number.parseInt(level),
                });
            }

            this._loading = false;
            this.updateUrl(this.queryParams);
        });
    }

    private _logLevelChanged() {
        this._filter();
    }
}
