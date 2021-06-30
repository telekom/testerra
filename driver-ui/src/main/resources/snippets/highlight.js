function ttHighlight(element, color, timeout) {
    var origOutline = element.style.outline;
    ttAddStyle(element, "outline: 5px solid " + color + " !important");
    window.setTimeout(function() {
        element.style.outline = origOutline;
    }, timeout);
}

function ttFill(element, color, timeout) {
    var origBackground = element.style.background;
    ttAddStyle(element, "background-color: " + color)
    window.setTimeout(function() {
        element.style.backgroundColor = origBackground;
    }, timeout)
}

/**
 * @see https://stackoverflow.com/questions/462537/overriding-important-style
 */
function ttAddStyle(element, css) {
    element.style.cssText += css;
}
