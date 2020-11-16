/**
 * Optimized highlight.js import
 * @see https://github.com/highlightjs/highlight.js#es6-modules
 */
import hljs from 'highlight.js/lib/core';
import plaintext from 'highlight.js/lib/languages/plaintext';
import java from 'highlight.js/lib/languages/java';
import 'highlight.js/styles/darcula.css';
import {autoinject} from 'aurelia-framework';
import {StatisticsGenerator} from "../../services/statistics-generator";
import {data} from "../../services/report-model";
import {FailureAspectStatistics} from "../../services/statistic-models";
import {DataLoader} from "../../services/data-loader";
import {Config} from "../../services/config";
import {MdcDialogService} from '@aurelia-mdc-web/dialog';
import {ScreenshotsDialog} from "../screenshots-dialog/screenshots-dialog";
import {AbstractMethod} from "./abstract-method";
import IStackTraceCause = data.IStackTraceCause;

@autoinject()
export class Details extends AbstractMethod {
    private _hljs = hljs;
    private _stackTrace:IStackTraceCause[];
    private _failureAspect:FailureAspectStatistics;
    private _screenshots:data.File[];

    constructor(
        statistics: StatisticsGenerator,
        private _dataLoader:DataLoader,
        private _config:Config,
        private _dialogService:MdcDialogService,
    ) {
        super(statistics);
        this._hljs.registerLanguage("java", java);
        this._hljs.registerLanguage("plaintext", plaintext);
        //this._hljs.registerLanguage("xml", xml);
    }

    protected loaded() {
        if (this.methodContext.errorContext?.cause) {
            this._stackTrace = [];
            let cause = this.methodContext.errorContext.cause;
            do {
                this._stackTrace.push(cause);
                cause = cause.cause;
            } while (cause);
        }

        this._failureAspect = new FailureAspectStatistics().addMethodContext(this.methodContext);

        this._screenshots = [];
        this.methodContext.testSteps
            .flatMap(testStep => testStep.testStepActions)
            .flatMap(testStepAction => testStepAction.screenshotIds)
            .forEach(id => {
                this._dataLoader.getFile(id).then(file => {
                    file.relativePath = this._config.correctRelativePath(file.relativePath);
                    this._screenshots.push(file);
                })
            })
    }

    private _showScreenshot(file:data.File) {
        this._dialogService.open({
            viewModel: ScreenshotsDialog,
            model: {
                current: file,
                screenshots: this._screenshots
            },
            class: "screenshot-dialog"
        });
    }
}
