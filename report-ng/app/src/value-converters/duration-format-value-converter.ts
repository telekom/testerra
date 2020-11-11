import 'moment-duration-format';
import moment, {Duration} from "moment";

export class DurationFormatValueConverter {

    toView(value: number | string | Date) {
        const duration: Duration = moment.duration(DurationFormatValueConverter.normalizeValue(value));
        return duration.format();
        //return moment.utc(duration.asMilliseconds()).format("HH:mm:ss");
        //return duration.asSeconds() >= 3600 ? moment.format("h[h] m[min]", {trim: true}) : moment.format("m[min] s[s]", {trim: true})
    }

    static normalizeValue(value: number | string | Date) {
        if (typeof value === "string") {
            value = Number.parseInt(value);
        } else if (value instanceof Date) {
            value = value.getDate();
        }
        return value;
    }

}
