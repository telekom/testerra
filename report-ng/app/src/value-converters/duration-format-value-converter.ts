import 'moment-duration-format';

export class DurationFormatValueConverter{
  toView(value) {
    return value.asSeconds() >= 3600 ? value.format("h[h] m[min]", {trim: true}) : value.format("m[min] s[s]", {trim: true})
  }
}
