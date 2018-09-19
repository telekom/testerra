$(document).ready(function () {
    jQuery("#buttondeps").click(function () {
        var cy = window.cy = cytoscape({
            container: document.getElementById("cy"),

            boxSelectionEnabled: false,
            autounselectify: true,
            autoungrabify: true,
            userPanningEnabled: false,
            maxZoom: 3,

            style: cytoscape.stylesheet()
              .selector("node")
                .css({
                  "text-valign": "center",
                  "color": "white",
                  "content": "data(name)",
                  "text-outline-width": 2,
                  "border-width": "data(borderWidth)",
                  "border-color": "data(borderColor)",
                  "border-style": "data(borderStyle)",
                  "text-outline-color": "data(backgroundColor)",
                  "background-color": "data(backgroundColor)"
                })
              .selector("edge")
                .css({
                  "curve-style": "bezier",
                  "target-arrow-shape": "triangle",
                  "width": 2,
                  "line-color": "#CDCDCD",
                  "target-arrow-color": "#CDCDCD"
                }),

            elements: getDependencyElements(),

            layout: {
                name: "dagre"
            }
        });

        cy.on("tap", "node", function () {
            window.location.href = this.data("link");
        });
    });
});

function getDependencyElements() {
    var dependencyElements = {
        nodes: [],
        edges: []
    };

    if (methods === undefined) {
        return dependencyElements;
    }

    for (var methodIndex in methods) {
        var method = methods[methodIndex],
            nextMethod = methods[(parseInt(methodIndex) + 1)],
            previousMethod = methods[(parseInt(methodIndex) - 1)];

        if (method === null) {
            continue;
        }

        addDependencyNode(dependencyElements, method);
        addDependencyPath(dependencyElements, method, nextMethod, previousMethod);
    }

    return dependencyElements;
}

function addDependencyNode(dependencyElements, method) {
    var dependencyNode = {
       data: {
           id: method.id,
           name: method.name,
           backgroundColor: method.backgroundColor,
           link: method.link,
           borderWidth: 0,
           borderStyle: "solid",
           borderColor: method.backgroundColor
       }
    };

    if (method.isSetUpMethod) {
        dependencyNode.data.borderWidth = 6;
        dependencyNode.data.borderStyle = "dashed";
    }
    if (method.isInitialMethod) {
        dependencyNode.data.borderWidth = 8;
        dependencyNode.data.borderStyle = "double";
    }

    dependencyElements.nodes.push(dependencyNode);
}

function addDependencyPath(dependencyElements, method, nextMethod, previousMethod) {
    var dependencyEdge = { data: { source: "", target: "" } };

    if (method.isBeforeMethod) {
         dependencyEdge.data.source = method.id;
         dependencyEdge.data.target = method.linkedMethod;

         if (nextMethod !== undefined &&
             (nextMethod.isBeforeMethod || method.isSetUpMethod)
         ) {
             dependencyEdge.data.target = nextMethod.id;
         }
    }
    if (method.isAfterMethod) {
         dependencyEdge.data.source = method.linkedMethod;
         dependencyEdge.data.target = method.id;

         if (previousMethod !== undefined &&
             (previousMethod.isAfterMethod || method.isSetUpMethod)
         ) {
             dependencyEdge.data.source = previousMethod.id;
         }
    }
    if (!method.isBeforeMethod && !method.isAfterMethod) {
         dependencyEdge.data.source = method.id;
         dependencyEdge.data.target = method.parentToMethod;
    }

    if (dependencyEdge.data.source !== "" && dependencyEdge.data.target !== "") {
         dependencyElements.edges.push(dependencyEdge);
    }
}