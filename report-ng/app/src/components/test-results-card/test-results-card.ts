import {autoinject} from "aurelia-framework";
import {EventAggregator} from 'aurelia-event-aggregator';
import "./test-results-card.scss";
import {StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {ExecutionStatistics} from "../../services/statistic-models";
import ApexOptions = ApexCharts.ApexOptions;

@autoinject
export class TestResultsCard {
    private _apexPieOptions: ApexOptions = undefined;
    private _executionStatistics;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _eventAggregator: EventAggregator,
        private _element: Element
    ) {
    }

    attached() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;
            this._preparePieChart(executionStatistics);
        });
    }

    private _preparePieChart(executionStatistics: ExecutionStatistics): void {
        const series = [];
        const labels = [];
        const colors = [];

        for (const status of this._statusConverter.relevantStatuses) {
            series.push(executionStatistics.getStatusCount(status));
            labels.push(this._statusConverter.getLabelForStatus(status));
            colors.push(this._statusConverter.getColorForStatus(status));
        }

        this._apexPieOptions = {
            chart: {
                type: 'pie',
                width: '400px',
                fontFamily: 'Roboto',
                events: {
                    dataPointSelection: (event, chartContext, config) => {
                        this._piePieceClicked(labels[config.dataPointIndex]);
                        event.stopPropagation();
                    }
                },
            },
            series: series,
            colors: colors,
            labels: labels
        };
    }

    private _piePieceClicked(dataLabel: string): void {
        /*let pieEvent = new CustomEvent('pie-piece-click', {
            detail: {dataLabel: dataLabel},
            bubbles: true
        });
        this._element.dispatchEvent(pieEvent);*/
        console.log("Event fired. Label:" + dataLabel);
        this._eventAggregator.publish('pie-piece-click', dataLabel);
    }
}
