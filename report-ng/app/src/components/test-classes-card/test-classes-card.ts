import './test-classes-card.scss';
import {data} from "../../services/report-model";
import {autoinject} from "aurelia-framework";
import {EventAggregator} from 'aurelia-event-aggregator';
import {StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";
import ResultStatusType = data.ResultStatusType;

@autoinject
export class TestClassesCard {
    private _apexBarOptions: any = undefined;


    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _eventAggregator: EventAggregator
    ) {
    }

    attached() {
        this._eventAggregator.subscribe('executionStatistics', payload => {
            this._prepareHorizontalBarChart(payload.classStatistics);
        });
    }

    private _prepareHorizontalBarChart(classStatistics): void {
        let data: Map<ResultStatusType, Array<number>> = new Map();
        let xlabels: Array<string> = [];

        for (const status of this._statusConverter.relevantStatuses) {
            data.set(status, [0]);
        }

        //Iterate through classStatistics array to fill map with data for series
        classStatistics.forEach(classStats => {
            for (const status of this._statusConverter.relevantStatuses) {
                data.get(status).push(classStats.getStatusCount(status));
            }

            //Push Class Names in array for x-axis labels
            xlabels.push(classStats.classAggregate.classContext.fullClassName);
        });

        //Display at least 10 rows in bar chart even if there are less classes
        if (xlabels.length < 10) {
            for (let i = xlabels.length; i <= 10; i++) {
                xlabels[i] = "";
                for (const status of this._statusConverter.relevantStatuses) {
                    data.get(status)[i] = 0;
                }
            }
        }

        const series = [];

        for (const status of this._statusConverter.relevantStatuses) {
            series.push({
                name: this._statusConverter.getLabelForStatus(status),
                data: data.get(status),
                color: this._statusConverter.getColorForStatus(status)
            })
        }

        this._apexBarOptions = {
            chart: {
                type: 'bar',
                fontFamily: 'Roboto',
                stacked: true,
                toolbar: {
                    show: false,
                }
            },
            series: series,
            xaxis: {
                categories: xlabels
            },
            grid: {
                show: false,
            },
            plotOptions: {
                bar: {
                    horizontal: true,
                    barHeight: '75%',
                }
            },
            noData: {
                text: "There is no data available at the moment. Please be patient!"
            },
            dataLabels: {
                enable: false,
            },
            legend: {
                position: 'top',
                horizontalAlign: 'center'
            }
        }
    }
}
