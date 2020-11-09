import {autoinject} from "aurelia-framework";
import 'ts-replace-all'

@autoinject()
export class HtmlValueConverter {
    constructor(
    ) {
    }

    toView(value?: string) {
        if (!value) {
            return value;
        }
        return value.replaceAll("\n","<br/>");
    }
}
