import {autoinject} from "aurelia-framework";
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
        const labelStatus = [];
        const colors = [];

        for (const status of this._statusConverter.relevantStatuses) {
            series.push(executionStatistics.getStatusCount(status));
            labels.push(this._statusConverter.getLabelForStatus(status));
            labelStatus.push(status)
            colors.push(this._statusConverter.getColorForStatus(status));
        }

        this._apexPieOptions = {
            chart: {
                type: 'pie',
                width: '400px',
                fontFamily: 'Roboto',
                events: {
                    dataPointSelection: (event, chartContext, config) => {
                        this._piePieceClicked(labelStatus[config.dataPointIndex]);
                        console.log(chartContext, config);
                        event.stopPropagation();
                    }
                },
            },
            series: series,
            colors: colors,
            labels: labels
        };
    }

    private _piePieceClicked(status: number): void {
        const event = new CustomEvent("piece-clicked", {
            detail: {
                status: status
            },
            bubbles: true
        });
        this._element.dispatchEvent(event)
    }
}
