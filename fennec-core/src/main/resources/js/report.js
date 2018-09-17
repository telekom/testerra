/**
 * JQuery Tooltip DataTable init.
 */
$(document).ready(function() {
    $(".tooltip").tooltip();
});

function initDataTable(tableId) {
    var swfPath = "../swf/copy_csv_xls.swf";
    if(tableId.indexOf("summaryDatatable")!=-1) {
        swfPath = "swf/copy_csv_xls.swf";
    }
    var table = $(tableId).DataTable({
        "paging" : false,
        "dom" : '<"tableHead"fBT>rt',   
		"select": {
            style: 'multi'
        },
        "colReorder": true,
        "buttons": [
            'selectAll',
            'selectNone',
            'copy',
            'excel',
            'csv',
            'pdf',
			'print'
        ],
		"language": {
			"sSearch": "Include: "
		}
    });
    
    $(tableId + '_filterInp').keyup( function() {
        table.draw();
    } );

    /* Custom filtering function which will filter all rows containing the given string */
    $.fn.dataTable.ext.search.push(
        function( settings, data, dataIndex ) {
            var filterText = $(tableId + '_filterInp').val();
            if(filterText == undefined || filterText.length < 3) {
                return true;
            }
            for(var i=0; i<data.length; ++i) {
                var text = data[i];
                if(text != undefined && text.indexOf(filterText) != -1) {
                    return false;
                }
            }
            return true;
        }
    );
}
/*
 * Hide/Show col of datatable
 */
function fnShowHide(iCol) {
    /*
     * Get the DataTables object again - this is not a recreation, just a get of
     * the object
     */
    var oTable = $('#summaryDatatable').dataTable();
    var bVis = oTable.fnSettings().aoColumns[iCol].bVisible;
    oTable.fnSetColumnVis(iCol, bVis ? false : true);
}

function toggleDetailsView(elementIdOn, elementClassOff, buttonIdOn, buttonClassOff) {
    if ($('#' + elementIdOn).is(":visible")) {
        // nothing
    }
    else {
        $('.' + elementClassOff).hide(0);
        $('#' + elementIdOn).show(200);

        $('.' + buttonClassOff).removeClass("inview");
        $('#' + buttonIdOn).addClass("inview");
    }
}

function toggleElements(classname) {
    $('.' + classname).toggle();
}

function toggleElement(elementId, callback) {
    if (callback == null) {
        callback = function(){};
    }
    var element = $('#' + elementId);

    if (element.is(':visible')) {
        element.fadeTo(500, 0, function() {
            element.slideUp(200, callback);
        });
    }
    else {
        element.css({
            opacity: 0
        });
        element.slideDown(200, function() {
            element.fadeTo(500, 1, callback());
        });
    }
}

function toggleClickPathElement(elementId, gNodes, gEdges) {
    var element = $('#' + elementId);
    element.toggle();
    showClickPath(elementId + "-graph", gNodes, gEdges);
}

function showDetailsLog(viewclass, logid) {
    $(".logentry").hide();
    $("." + viewclass).fadeIn();
//    oTable.fnDestroy();
//    initDataTable('#logging-' + logid + "-table");
}

/**
 * Hide test classes that are passed.
 */
function hidePassedTests() {
    var nodesSnapshot = document.evaluate("//table[@class='overviewTable']//tr[contains(@class,'test') and .//span[contains(@title,'Passed')]]",
						document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null );
    for ( var i=0 ; i < nodesSnapshot.snapshotLength; i++ )
    {
        var node = nodesSnapshot.snapshotItem(i);
		if(document.getElementById('hidePassed').checked) {
			node.style.display="none";
		} else {
			node.style.display="table-row";
        }
    }
}

/**
 * Save a comment to the test result. Index contains a number + testclass and
 * methodname Testclass and methodname is required to map to correct frame
 */
function saveComment(row, testStartTime) {
    var comment = document.getElementById('com' + row).comment.value;
    localStorage[getHash(row, testStartTime)] = comment;
}

String.prototype.hashCode = function() {
    var hash = 0, i, char;
    if (this.length == 0)
        return hash;
    for (i = 0; i < this.length; i++) {
        char = this.charCodeAt(i);
        hash = ((hash << 5) - hash) + char;
        hash = hash & hash; // Convert to 32bit integer
    }
    return hash;
};

function getHash(row, testStartTime) {
    var className = $('#headline').text();
    var method = $('#row-' + row + ' .method').clone().children().remove()
            .end().text().trim();
    if (method == "" || method == undefined) {
        method = $('#row-' + row + ' .method a').clone().children().remove()
                .end().text().trim();
    }
    var suite = $('#row-' + row + ' .tooltip').attr('title');
    if (suite == undefined)
        suite = "";
    return (className + method + suite).hashCode() + "-" + testStartTime;
}
/**
 * Shows all saved Comments of frame
 */
function loadComments(testStartTime) {
    for ( var i in localStorage) {
        $('tr[id^="row-"]')
                .each(
                        function(index) {
                            var hash = getHash(this.id.substring(4, 6),
                                    testStartTime);
                            // Look if headline of frame contains the class in
                            // index
                            if (i == hash) {
                                document.getElementById('com'
                                        + this.id.substring(4, 6)).comment.value = localStorage[i];
                                document.getElementById('saved-text'
                                        + this.id.substring(4, 6)).innerHTML = '<u>Comment:</u><br />'
                                        + localStorage[i].replace(/\n/g,
                                                "<br />");
                            }
                        }
                );
        // Check if report file is a new file, using teststatistics starttime
        if (testStartTime != null) {
            if (i.indexOf(testStartTime) == -1) {
                clearComments();
            }
        }
    }
}
/**
 * Shows all saved Comments of frame
 */
function loadSummaryComments(testStartTime) {
    for ( var i in localStorage) {
        $('#datatable').find("tr").each(
                function(index) {
                    if (index != 0) {
                        var hash;
                        if (this.children.length == 7) {
                            var className = $(this).find('td').eq(3).text();
                            className = className.substring(0,
                                    className.length - 1);
                            var method = $(this).find('td').eq(4).text();
                            var suite = $(this).find('td').eq(1).text();
                            var test = $(this).find('td').eq(2).text();
                            var suiteString;
                            if (suite != undefined && suite.length > 0) {
                                suiteString = 'Suite: ' + suite + ', Test: '
                                        + test;
                            } else {
                                suiteString = "";
                            }
                            hash = (className + method + suiteString)
                                    .hashCode()
                                    + "-" + testStartTime;
                        } else {
                            var className = $(this).find('td').eq(1).text();
                            className = className.substring(0,
                                    className.length - 1);
                            var method = $(this).find('td').eq(2).text();
                            hash = (className + method + "").hashCode() + "-"
                                    + testStartTime;
                        }
                        // Look if headline of frame contains the class in index
                        if (i == hash) {
                            $(this).find('.comment').text(localStorage[i]);
                        }
                    }
                });
    }
}
/**
 * Remove comment with index.
 */
function clearComment(index) {
    for ( var i in localStorage) {
        if (i.indexOf(getHash(index, "")) != -1) {
            document.getElementById('saved-text' + index).innerHTML = "";
            localStorage.removeItem(i);
        }
    }
}
/**
 * Remove comments from localStorage and all comment text of the frame
 */
function clearComments(testStartTime) {
    for ( var i in localStorage) {
        $('tr[id^="row-"]')
                .each(
                        function(index) {
                            var hash = getHash(this.id.substring(4, 6),
                                    testStartTime);
                            if (i == hash) {
                                document.getElementById('saved-text'
                                        + this.id.substring(4, 6)).innerHTML = "";
                                document.getElementById('com'
                                        + this.id.substring(4, 6)).comment.value = "";
                                localStorage.removeItem(i);
                            }
                        });
    }
}

function getStyle(elementId, property) {
    var element = document.getElementById(elementId);
    return element.currentStyle ? element.currentStyle[property]
            : document.defaultView.getComputedStyle(element, null)
                    .getPropertyValue(property);
}
/*
 * Fügt den Listeneinträgen Eventhandler und CSS Klassen hinzu, um die
 * Menüpunkte am Anfang zu schließen.
 * 
 * menu: Referenz auf die Liste. data: String, der die Nummern aufgeklappter
 * Menüpunkte enthält.
 */
function treeMenu_init(menu, data) {
    var array = new Array(0);
    if (data != null && data != "") {
        array = data.match(/\d+/g);
    }
    var items = menu.getElementsByTagName("li");
    for (var i = 0; i < items.length; i++) {
        items[i].onclick = treeMenu_handleClick;
        if (!treeMenu_contains(treeMenu_getClasses(items[i]), "treeMenu_opened")
                && items[i].getElementsByTagName("ul").length
                        + items[i].getElementsByTagName("ol").length > 0) {
            var classes = treeMenu_getClasses(items[i]);
            if (array.length > 0 && array[0] == i) {
                classes.push("treeMenu_opened")
            } else {
                classes.push("treeMenu_closed")
            }
            items[i].className = classes.join(" ");
            if (array.length > 0 && array[0] == i) {
                array.shift();
            }
        }
    }
}
/*
 * Ändert die Klasse eines angeclickten Listenelements, sodass geöffnete
 * Menüpunkte geschlossen und geschlossene geöffnet werden.
 * 
 * event: Das Event Objekt, dass der Browser übergibt.
 */
function treeMenu_handleClick(event) {
    if (event == null) { // Workaround für die fehlenden DOM Eigenschaften im
                            // IE
        event = window.event;
        event.currentTarget = event.srcElement;
        while (event.currentTarget.nodeName.toLowerCase() != "li") {
            event.currentTarget = event.currentTarget.parentNode;
        }
        event.cancelBubble = true;
    } else {
        event.stopPropagation();
    }
    var array = treeMenu_getClasses(event.currentTarget);
    for (var i = 0; i < array.length; i++) {
        if (array[i] == "treeMenu_closed") {
            array[i] = "treeMenu_opened";
        } else if (array[i] == "treeMenu_opened") {
            array[i] = "treeMenu_closed"
        }
    }
    event.currentTarget.className = array.join(" ");
}
/*
 * Gibt alle Klassen zurück, die einem HTML-Element zugeordnet sind.
 * 
 * element: Das HTML-Element return: Die zugeordneten Klassen.
 */
function treeMenu_getClasses(element) {
    if (element.className) {
        return element.className.match(/[^ \t\n\r]+/g);
    } else {
        return new Array(0);
    }
}
/*
 * Überprüft, ob ein Array ein bestimmtes Element enthält.
 * 
 * array: Das Array element: Das Element return: true, wenn das Array das
 * Element enthält.
 */
function treeMenu_contains(array, element) {
    for (var i = 0; i < array.length; i++) {
        if (array[i] == element) {
            return true;
        }
    }
    return false;
}
/*
 * Gibt einen String zurück, indem die Nummern aller geöffneten Menüpunkte
 * stehen.
 * 
 * menu: Referenz auf die Liste return: Der String
 */
function treeMenu_store(menu) {
    var result = new Array();
    ;
    var items = menu.getElementsByTagName("li");
    for (var i = 0; i < items.length; i++) {
        if (treeMenu_contains(treeMenu_getClasses(items[i]), "treeMenu_opened")) {
            result.push(i);
        }
    }
    return result.join(" ");
}

function copyToClipBoard(elementId) {
    var text = document.getElementById(elementId).innerText;
        if(window.clipboardData)
        {
            window.clipboardData.setData('text',text);
            return;
        }
        else
        {
            try {
                netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
            } catch (e) {
                alert("Internet Security settings do not allow copying to clipboard!");
                return;
            }
            try {
                e = Components.classes['@mozilla.org/widget/clipboard;1'].createInstance(Components.interfaces.nsIClipboard);
            } catch (e) {
                return;
            }
            try {
                b = Components.classes['@mozilla.org/widget/transferable;1'].createInstance(Components.interfaces.nsITransferable);
            } catch (e) {
                return;
            }
            b.addDataFlavor("text/unicode");
            o = Components.classes['@mozilla.org/supports-string;1'].createInstance(Components.interfaces.nsISupportsString);
            o.data = text;
            b.setTransferData("text/unicode", o, text.length * 2);
            try {
                t = Components.interfaces.nsIClipboard;
            } catch (e) {
                return;
            }
            e.setData(b, null, t.kGlobalClipboard);
            return;
        }
        alert('Copy doesn\'t work!');
        return;
}

function toggleButtonText(elementId, text1, text2) {
    e = $("#" + elementId);
    if (e.text() == text1) {
        e.text(text2);
    }
    else {
        e.text(text1);
    }

}

function showClickPath(elementid, gNodes){
        $("#" + elementid).empty();
        $("#" + elementid).cytoscape({
            layout: {
                name: 'dagre',
                padding: 0
            },

        style: cytoscape.stylesheet()
        .selector('node')
        .css({
            'shape': 'data(faveShape)',
            'width': '150', //mapData(weight, 40, 80, 20, 60)
            'height': '60',
            'content': 'data(name)',
            'text-valign': 'center',
            'text-outline-width': 3,
            'text-outline-color': 'data(faveColor)',
            'background-color': 'data(faveColor)',
            'font-size': 'data(fontSize)',
            'text-wrap': 'wrap',
            'color': '#FFFFFF'
            })
        .selector(':selected')
            .css({
                'border-width': 3,
                'border-color': '#333'
                })
        .selector('edge')
            .css({
                'opacity': 0.666,
                'width': 'mapData(strength, 70, 100, 2, 6)',
                'target-arrow-shape': 'triangle',
                'source-arrow-shape': 'none',
                'line-color': 'data(faveColor)',
                'source-arrow-color': 'data(faveColor)',
                'target-arrow-color': 'data(faveColor)',
                'content': 'data(edgeLabel)',
                'text-wrap': 'wrap',
                'font-size': '11px'
                })
        .selector('edge.questionable')
            .css({
                'line-style': 'dotted',
                'target-arrow-shape': 'diamond'
            })
        .selector('.faded')
                .css({
                'opacity': 0.25,
                'text-opacity': 0
            }),

        elements: {
        nodes: gNodes,
        edges: gEdges
        },

        zoomingEnabled: true,
        userZoomingEnabled: true,

        ready: function(){
            this.center();
            window.cy = this;
            // giddy up
            }
        });
}

var selectList = (function(){
  var $moreItems  = $('.more-items'),
      $moreToggle = $('.more-toggle'),
      $lessToggle = $('.less-toggle');

  function init() {
    toggle();
    select();
  }

  function toggle() {
    $moreToggle.click(function(e) {
      e.preventDefault();

      $(this).addClass('hidden');
      $moreItems.removeClass('hidden');
      $lessToggle.removeClass('hidden');
    });

    $lessToggle.click(function(e) {
      e.preventDefault();

      $(this).addClass('hidden');
      $moreItems.addClass('hidden');
      $moreToggle.removeClass('hidden');
    });
  }

  function select() {
    $('.list-group-item').click(function(e) {
      e.preventDefault();

      var $this = $(this),
          $siblings = $('.list-group-item');

      if($this.hasClass('itemactive')) {
        return;
      } else {
        $siblings.removeClass('itemactive');
        $this.addClass('itemactive');
      }
    });
  }

  $(init);
})();

function showMethodsViewOnDashboard(classname, status) {
    var e = "#detailsView";

    if (status == null) {
        status= "Methods";
    }

    var filter = ".filter" + status;
    var file = "classes/" + classname + "_dashboard.html";

    $(e).empty();
    $(e).load(file, function() {
        $('.filterMethods').hide();
        $(filter).show();
    });

}

function showLoadingIn(elementId) {
    var e;
    if (elementId == null) {
        e = $('body');
    }
    else {
        e = $('#' + elementId);
    }
    e.empty();
    e.html('<div class="loadingcontainer"><div class="loader"></div></div>');
}

function timestampToString(timestamp) {
    var date =  new Date(timestamp);
    var day = date.getDate();
    var month = date.getMonth() + 1;
    var year = date.getFullYear();
    var minute = date.getMinutes();
    var hour = date.getHours();
    var second = date.getSeconds();
    var second = date.getSeconds();
    var milli = date.getMilliseconds();

    var formattedDate = ((day < 10) ? "0" + day : day) +
    '.' + ((month < 10) ? "0" + month : month) +
    '.' + year +
    '_' + ((hour < 10) ? "0" + hour : hour) +
    ':' + ((minute < 10) ? "0" + minute : minute) +
    ':' + ((second < 10) ? "0" + second : second) +
    ':' + milli;

    return formattedDate;
}

function msToFormattedString(msInput, addZeros) {
    var milliseconds = parseInt(msInput%1000)
                , seconds = parseInt((msInput/1000)%60)
                , minutes = parseInt((msInput/(1000*60))%60)
                , hours = parseInt((msInput/(1000*60*60))%24);

    var out="";
    if (hours > 0) {
        out += ((hours < 10 && addZeros) ? "0" + hours : hours) + "h ";
    }
    if (minutes > 0) {
        out += ((minutes < 10 && addZeros) ? "0" + minutes : minutes) + "m ";
    }
    if (seconds > 0) {
        out += ((seconds < 10 && addZeros) ? "0" + seconds : seconds) + "s ";
    }
    if (milliseconds > 0) {
        out += milliseconds + "ms ";
    }

    if (out == "") {
        out = "0ms";
    }
    else {
        out = out.trim();
    }

    return out;
}
