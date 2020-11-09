import {autoinject} from "aurelia-framework";
import 'ts-replace-all'

@autoinject()
export class HtmlValueConverter {
    constructor(
    ) {
    }

    toView(value: string) {
        return value.replaceAll("\n","<br/>");
    }
}
