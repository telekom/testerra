import {data} from "../../services/report-model";
import {autoinject, bindable} from "aurelia-framework";
import {StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {ClassStatistics} from "../../services/statistic-models";
import ResultStatusType = data.ResultStatusType;

@autoinject
export class TestClassesCard {
    @bindable filter;
    @bindable classStatistics: ClassStatistics[];
    private _apexBarOptions: any = undefined;
    private _currentFilter;


    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator
    ) {
    }

/*    attached() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._prepareHorizontalBarChart(executionStatistics.classStatistics);
            this._classStats = executionStatistics.classStatistics;
        });

        this._eventAggregator.subscribe('pie-piece-click', dataLabel => {
            this._piePieceClicked(dataLabel);
        });
    }*/

    classStatisticsChanged() {
        console.log("stats changed.");
        this._prepareHorizontalBarChart(this.classStatistics);
    }

    filterChanged(){
        console.log("filter changed. ", this.filter);
        if (this.filter.status == this._currentFilter){
            this.filter = "";
        }
        if (this.classStatistics?.length > 0 ) {
            this._prepareHorizontalBarChart(this.classStatistics);
        }
        this._currentFilter = this.filter.status;
    }

    private _prepareHorizontalBarChart(classStatistics: ClassStatistics[]): void {
        let data: Map<ResultStatusType, Array<number>> = new Map();
        let xlabels: Array<string> = [];

        const series = [];
        const filteredStatuses = this._statusConverter.relevantStatuses.filter(status => (!this.filter?.status || status == this.filter.status) );

        for (const status of filteredStatuses) {
            data.set(status, []);
            series.push({
                name: this._statusConverter.getLabelForStatus(status),
                data: data.get(status),
                color: this._statusConverter.getColorForStatus(status)
            })
        }

        //Iterate through classStatistics array to fill map with data for series
        classStatistics
            .filter(classStatistic => {
                for (let status of filteredStatuses) {
                    if (classStatistic.getStatusCount(status) > 0) {
                        return true;
                    }
                }
            })
            .forEach(classStats => {
                for (const status of filteredStatuses) {
                    data.get(status).push(classStats.getStatusCount(status));
                }
                //Push Class Names in array for x-axis labels
                xlabels.push(classStats.classAggregate.classContext.testContextName||classStats.classAggregate.classContext.simpleClassName);
            });

        //Display at least 10 rows in bar chart even if there are less classes
        // if (xlabels.length < 10) {
        //     for (let i = xlabels.length; i <= 10; i++) {
        //         xlabels[i] = "";
        //         for (const status of this._statusConverter.relevantStatuses) {
        //             data.get(status)[i] = 0;
        //         }
        //     }
        // }

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
                labels: {
                    show: true,
                    trim: false,    //ignored apparently, documentation: https://apexcharts.com/docs/options/xaxis/#trim
                    maxHeight: undefined,
                },
                categories: xlabels,
            },
            grid: {
                show: false,
            },
            plotOptions: {
                bar: {
                    horizontal: true,
                    barHeight: '60%',
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
