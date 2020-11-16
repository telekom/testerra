import {autoinject, bindable} from "aurelia-framework";
import "./test-results-card.scss";
import {StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {ExecutionStatistics} from "../../services/statistic-models";
import ApexOptions = ApexCharts.ApexOptions;

@autoinject
export class TestResultsCard {
    @bindable executionStatistics: ExecutionStatistics;
    private _apexPieOptions: ApexOptions = undefined;


    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _element: Element
    ) {
    }

    executionStatisticsChanged() {
        this._preparePieChart(this.executionStatistics);
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
            dataLabels: {
                style: {
                    fontSize: '12px',
                    fontFamily: 'Roboto',
                    fontWeight: 400
                },
                background: {
                    enabled: true,
                    dropShadow: {
                        enabled:false
                    },
                    opacity: 0.2
                },
                dropShadow: {
                    enabled: false
                }
            },
            stroke: {
                show: false
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
