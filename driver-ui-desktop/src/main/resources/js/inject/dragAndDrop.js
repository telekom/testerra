function TesterraCreateDragEvent(eventName, options)
{
  var event = document.createEvent("DragEvent");
  var screenX = window.screenX + options.clientX;
  var screenY = window.screenY + options.clientY;
  var clientX = options.clientX;
  var clientY = options.clientY;
  var dataTransfer = {
    data: options.dragData == null ? {
    }
     : options.dragData,
    setData: function (eventName, val) {
      if (typeof val === "string") {
        this.data[eventName] = val;
      }
    },
    getData: function (eventName) {
      return this.data[eventName];
    },
    clearData: function () {
      return this.data = {
      };
    },
    setDragImage: function (dragElement, x, y) {
    }
  };
  var eventInitialized = false;
  if (event != null && event.initDragEvent) {
    try {
      event.initDragEvent(eventName, true, true, window, 0, screenX, screenY, clientX, clientY, false, false, false, false, 0, null, dataTransfer);
      event.initialized = true;
    } catch (err) {
      /*
      no-op
      */
    }
  };
  if (!eventInitialized) {
    event = document.createEvent("CustomEvent");
    event.initCustomEvent(eventName, true, true, null);
    event.view = window;
    event.detail = 0;
    event.screenX = screenX;
    event.screenY = screenY;
    event.clientX = clientX;
    event.clientY = clientY;
    event.ctrlKey = false;
    event.altKey = false;
    event.shiftKey = false;
    event.metaKey = false;
    event.button = 0;
    event.relatedTarget = null;
    event.dataTransfer = dataTransfer;
  };
  return event;
};
/* Creates a mouse event */

function TesterraCreateMouseEvent(eventName, options)
{
  var event = document.createEvent("MouseEvent");
  var screenX = window.screenX + options.clientX;
  var screenY = window.screenY + options.clientY;
  var clientX = options.clientX;
  var clientY = options.clientY;
  if (event != null && event.initMouseEvent) {
    event.initMouseEvent(eventName, true, true, window, 0, screenX, screenY, clientX, clientY, false, false, false, false, 0, null);
  } else {
    event = document.createEvent("CustomEvent");
    event.initCustomEvent(eventName, true, true, null);
    event.view = window;
    event.detail = 0;
    event.screenX = screenX;
    event.screenY = screenY;
    event.clientX = clientX;
    event.clientY = clientY;
    event.ctrlKey = false;
    event.altKey = false;
    event.shiftKey = false;
    event.metaKey = false;
    event.button = 0;
    event.relatedTarget = null;
  };
  return event;
};
/* Runs the events */

function TesterraDispatchEvent(webElement, eventName, event)
{
  if (webElement.dispatchEvent) {
    webElement.dispatchEvent(event);
  } else if (webElement.fireEvent) {
    webElement.fireEvent("on" + eventName, event);
  }
};
/* Simulates an individual event */

function TesterraSimulateEventCall(element, eventName, dragStartEvent, options) {
  var event = null;
  if (eventName.indexOf("mouse") > - 1) {
    event = TesterraCreateMouseEvent(eventName, options);
  } else {
    event = TesterraCreateDragEvent(eventName, options);
  };
  if (dragStartEvent != null) {
    event.dataTransfer = dragStartEvent.dataTransfer;
  }
  TesterraDispatchEvent(element, eventName, event);
  return event;
};

function TesterraDND(drag, drop, dragFromX, dragFromY, dragToX, dragToY) {
  /*
    Execute dnd
    */

  var mouseDownEvent = TesterraSimulateEventCall(drag, "mousedown", null, {
    clientX: dragFromX,
    clientY: dragFromY
  });
  var dragStartEvent = TesterraSimulateEventCall(drag, "dragstart", null, {
    clientX: dragFromX,
    clientY: dragFromY
  });
  var dragEnterEvent = TesterraSimulateEventCall(drop, "dragenter", dragStartEvent, {
    clientX: dragToX,
    clientY: dragToY
  });
  var dragOverEvent = TesterraSimulateEventCall(drop, "dragover", dragStartEvent, {
    clientX: dragToX,
    clientY: dragToY
  });
  var dropEvent = TesterraSimulateEventCall(drop, "drop", dragStartEvent, {
    clientX: dragToX,
    clientY: dragToY
  });
  var dragEndEvent = TesterraSimulateEventCall(drag, "dragend", dragStartEvent, {
    clientX: dragToX,
    clientY: dragToY
  });
};

function TesterraDNDFrame(dragJsLocator, dropJsLocator, fromX, fromY, toX, toY) {

    var drag = eval(dragJsLocator);
    var drop = eval(dropJsLocator);

    TesterraDND(drag, drop, fromX, fromY, toX, toY);
};

function TesterraSwipe(element, fromX, fromY, toX, toY) {

  var mouseDownEvent = TesterraSimulateEventCall(element, "mousedown", null, {
    clientX: fromX,
    clientY: fromY
  });
  var mouseMoveEvent = TesterraSimulateEventCall(element, "mousemove", null, {
    clientX: toX,
    clientY: toY
  });
  var mouseUpEvent = TesterraSimulateEventCall(element, "mouseup", null, {
    clientX: toX,
    clientY: toY
  });

}
