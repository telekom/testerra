/**
 * gaugeSVG - a very easy JavaScript plugin for greating simple dashboard gauges.
 * Copyright (c) 2013 Steffen Ploetz. Licensed under LGPL.
 * @date:    04/06/2013
 * @author:  Steffen Ploetz  (@Toorshia)
 * @version: 1.0
 */

// @description: Create and initialize a gauge instance.
// @param:       params: [array] The array of named parameters.
// @return:      instance: [GaugeSVG] The initialize  gauge instance.
// @remark:      params *** MUST *** contain these named parameters:
//               id: [string] The name of a DIV element to draw the gauge in.
//               params *** CAN *** contain these named parameters:
//               title: [string] The title text displayed above the gauge. Can be an emty string to be suppressed. Default is emty string.
//               titleColor: [#rrggbb] The title color. Default is "#888888".
//               value: [float] The value to display.  Default is (max - min) / 2.0.
//               valueColor: [#rrggbb] The value text color. Default is "#000000".
//               label: [string] The label displayed below the value text. Can be an emty string to be suppressed. Default is emty string.
//               labelColor: [#rrggbb] The label text color. Default is "#888888".
//               min: [float] The minimum of the gauge display range. Can be displayed at the gauge start point.
//               max: [float] The maximum of the gauge display range. Can be displayed at the gauge end point.
//               showMinMax: [bool] Hide or display the minimum and maximum values. Default is true.
//               minmaxColor: [#rrggbb] The minimum and maximum values color. Default is "#888888".
//               canvasBackColor: [#rrggbb] The background color of the gauge canvas. Default is "#f8f8f8".
//               gaugeWidthScale: [float] The width of the gauge arc. Default is 1.0.
//               gaugeBorderColor: [float] The gauge arc border color. Default is "#cccccc".
//               gaugeBorderWidth: [#rrggbb] The gauge arc border width. Default is 0.
//               gaugeBackColor: [#rrggbb] The gauge arc background color. Default is "#cccccc".
//               showGaugeShadow: [bool] Hide or display a gauge arc shadow. Dafault is true.
//               gaugeShadowColor: [#rrggbb] The gauge arc shadow color. Dafault is "#000000".
//               gaugeShadowScale: [float] The width of the gauge arc's shadow. Default is 1.0.
//               lowerActionLimit: [float] The lower action limit or a negative value, if not desired.  Default is (max - min) * 0.15 + min.
//               lowerWarningLimit: [float] The lower warning limit or a negative value, if not desired.  Default is (max - min) * 0.30 + min.
//               upperWarningLimit: [float] The upper warning limit or a negative value, if not desired.  Default is (max - min) * 0.70 + min.
//               upperActionLimit: [float] The upper action limit or a negative value, if not desired.  Default is (max - min) * 0.85 + min.
//               needleColor: [#rrggbb] The gauge needle color. Default is "#444444".
//               optimumRangeColor: [#rrggbb] The optimum range color. Default is "#44ff44".
//               warningRangeColor: [#rrggbb] The warning range color. Default is "#ffff00".
//               actionRangeColor: [#rrggbb] The action range color. Default is "#ff4444".
GaugeSVG = function(params)
{ 
	// Check prerequisits.
	if (!params.id)
	{
		alert ("Missing parameter 'params.id' for GaugeSVG.");
		return undefined;
	}

	// Determine drawing container.
	var container = document.getElementById(params.id);
	if (container == null || container == undefined)
	{
		alert ("Can not find DOM element with ID '" + params.id + "'.");
		return undefined;
	}
	if (container.tagName != "DIV")
	{
		alert ("DOM element with ID '" + params.id + "' must be of type 'DIV' but is of type '" + container.tagName + "'.");
		return undefined;
	}

	// Initialize complete configuration.
	var precalculatedMin = (params.min) ? params.min : 0.0;
	var precalculatedMax = (params.max) ? params.max : 100.0;
	if (precalculatedMin > precalculatedMax)
	{
		var buffer = precalculatedMin;
		precalculatedMin = precalculatedMax;
		precalculatedMax = buffer;
	}
	this.config = {
		// The DOM ID of the DIV element, that hosts the SVG gauge.
		id: params.id,
		
		// The title text displayed above the gauge. Can be an emty string to be suppressed. Default is emty string.
		title: (params.title != undefined) ? params.title : "",
		
		// The title color. Default is emty string.
		titleColor: (params.titleColor != undefined) ? params.titleColor : "#000000",
		
		// The gauge value set by the user. Might underflow/overflow the min/max and must be adopted to be displayed correct.
		originalValue: (params.value) ? params.value : precalculatedMin,
//		originalValue: (params.value) ? params.value : Math.floor((precalculatedMax - precalculatedMin) / 2.0),

		// The current gauge value. Might be adopted to prevent underflow/overflow the min/max.
		value: (params.value) ? params.value : precalculatedMin,
//		value: (params.value) ? params.value : Math.floor((precalculatedMax - precalculatedMin) / 2.0),

		// The value text color. Default is "#000000".
		valueColor: (params.valueColor != undefined) ? params.valueColor : "#000000",
		
		// The label displayed below the value text. Can be an emty string to be suppressed. Default is emty string.
		label: (params.label != undefined) ? params.label : "",
		
		// The label text color. Default is "#888888".
		labelColor: (params.labelColor != undefined) ? params.labelColor : "#888888",
		
		// The minimum of the gauge display range. Can be displayed at the gauge start point.
		min: precalculatedMin,
		
		// The maximum of the gauge display range. Can be displayed at the gauge end point.
		max: precalculatedMax,
		
		// Hide or display the minimum and maximum values. Default is true.
		showMinMax: (params.showMinMax != undefined) ? params.showMinMax : true,
		
		// The minimum and maximum values color. Default is "#888888".
		minmaxColor: (params.minmaxColor != undefined) ? params.minmaxColor : "#888888",
		
		// The background color of the gauge canvas. Default is "#f8f8f8".
		canvasBackColor: (params.canvasBackColor != undefined) ? params.canvasBackColor : "#f8f8f8",
		
		// The width of the gauge arc. Default is 1.0.
		gaugeWidthScale: (params.gaugeWidthScale != undefined) ? params.gaugeWidthScale : 1.0,

		// The gauge arc border color. Default is "#cccccc".
		gaugeBorderColor: (params.gaugeBorderColor != undefined) ? params.gaugeBorderColor : "#cccccc",
		
		// The gauge arc border width. Default is 0.
		gaugeBorderWidth: (params.gaugeBorderWidth != undefined) ? params.gaugeBorderWidth : 0,
		
		// The gauge arc background color. Dafault is "#cccccc".
		gaugeBackColor: (params.gaugeBackColor != undefined) ? params.gaugeBackColor : "#cccccc",
		
		// Hide or display a gauge arc shadow. Dafault is true.
		showGaugeShadow: (params.showGaugeShadow != undefined) ? params.showGaugeShadow : true,
		
		// The gauge arc shadow color. Dafault is "#000000".
		gaugeShadowColor: (params.gaugeShadowColor != undefined) ? params.gaugeShadowColor : "#000000",

		// The width of the gauge arc's shadow. Default is 1.0.
		gaugeShadowScale: (params.gaugeShadowScale != undefined) ? params.gaugeShadowScale : 1.0,
		
		// The width of the canvas, defined by it's parent DIV element.
		canvasW: 0.8 * (container.style.pixelWidth  || container.offsetWidth),
		
		// The Height of the canvas, defined by it's parent DIV element.
		canvasH: 0.8 * (container.style.pixelHeight || container.offsetHeight),
		
		// The offset from the canvas origin to the widget origin.
		offsetX: 0,
		
		// The offset from the canvas origin to the widget origin.
		offsetY: 10,
		
		// The lower action limit. Must be negative to be suppressed.
		lowerActionLimit: (params.lowerActionLimit != undefined) ? params.lowerActionLimit : (precalculatedMax - precalculatedMin) * 0.15 + precalculatedMin,
		
		// The lower warning limit. Must be negative to be suppressed.
		lowerWarningLimit: (params.lowerWarningLimit != undefined) ? params.lowerWarningLimit : (precalculatedMax - precalculatedMin) * 0.30 + precalculatedMin,

		// The upper warning limit. Must be negative to be suppressed.
		upperWarningLimit: (params.upperWarningLimit != undefined) ? params.upperWarningLimit : (precalculatedMax - precalculatedMin) * 0.70 + precalculatedMin,
		
		// The upper action limit. Must be negative to be suppressed.
		upperActionLimit: (params.upperActionLimit != undefined) ? params.upperActionLimit : (precalculatedMax - precalculatedMin) * 0.85 + precalculatedMin,
		
		// The gauge needle color. Default is "#444444".
		needleColor: (params.needleColor != undefined) ? params.needleColor : "#444444",
		
		// The optimum range color. Default is "#44ff44".
		optimumRangeColor: (params.optimumRangeColor != undefined) ? params.optimumRangeColor : "#88EE88",
		//optimumRangeColor: (params.optimumRangeColor != undefined) ? params.optimumRangeColor : "#44ff44",

		// The warning range color. Default is "#ffff00".
		warningRangeColor: (params.warningRangeColor != undefined) ? params.warningRangeColor : "#FFFF77",
		//warningRangeColor: (params.warningRangeColor != undefined) ? params.warningRangeColor : "#ffff00",

		// The action range color. Default is "#ff4444".
		actionRangeColor: (params.actionRangeColor != undefined) ? params.actionRangeColor : "#FF8888",
		//actionRangeColor: (params.actionRangeColor != undefined) ? params.actionRangeColor : "#ff4444",
	};

	// Initialize animation values. Perform extensive testing on if you plan any changes!
	this.animation = {
		// The devisor to devide the differential between old and new value into an increment value.
		startIncrementDivisor: 24.0,
		
		// The number increments to animate.
		maxIncrements: 48,
		
		// The decrease of the increment value to apply every animated increment (to simulate a slowdown of the needle).
		decreaseOfIncrementValue: 0.966,
		
		// The delay between two animated increments in ms.
		delay: 15,
	}
  
	// Prevent value overflow.
	if (this.config.value > this.config.max)
		this.config.value = this.config.max; 
	if (this.config.value < this.config.min)
		this.config.value = this.config.min;

	// Determine container geometry.
	var svgns   = "http://www.w3.org/2000/svg";
	var widgetW = ((this.config.canvasW / this.config.canvasH) > 1.25 ? 1.25 * this.config.canvasH : this.config.canvasW);
	var widgetH = ((this.config.canvasW / this.config.canvasH) > 1.25 ? this.config.canvasH : this.config.canvasW / 1.25);
	this.config.offsetX = (this.config.canvasW - widgetW) / 2;
	this.config.offsetY = (this.config.canvasH - widgetH) / 2;

	// Create SVG canvas.
	this.canvas  = document.createElementNS(svgns, "svg");
	this.canvas.setAttributeNS(null, 'version', "1.1");
	this.canvas.setAttributeNS(null, 'width', "100%");
	this.canvas.setAttributeNS(null, 'height', "100%");
	this.canvas.setAttributeNS(null, 'style', "overflow: hidden; position: relative; left: -0.5px; top: -0.5px;");
	if (this.canvas == null || this.canvas == undefined)
	{
		alert ("Can not create DOM element of type 'SVG' as child of DOM element with ID '" + this.config.id + "'.");
		return;
	}
	container.appendChild(this.canvas);

	// @description: Calculate any arc path.
	// @param:       valueStart [float]: The value to mark the begin of the arc.
	// @param:       valueEnd [float]: The value to mark the end of the arc.
	// @param:       valueMin [float]: The minimum value of the display range.
	// @param:       valueMax [float]: The maximum value of the display range.
	// @param:       widgetWidth [int]: The width of the display area.
	// @param:       widgetHeight [int]: The height of the display area.
	// @param:       widgetOffsetX [int]: The offset of the display area.
	// @param:       widgetOffsetY [int]: The offset of the display area.
	// @param:       gaugeWidthScale [float]: The thickmess of the scale.
	// @return:      path [string]: The svg path of the arc.
	this.calculateArcPath = function (valueStart, valueEnd, valueMin, valueMax, widgetWidth, widgetHeight, widgetOffsetX, widgetOffsetY, gaugeWidthScale)
	{
		var alpha = (1 - (valueStart - valueMin) / (valueMax - valueMin)) * Math.PI;
		var beta  = (1 - (valueEnd - valueMin) / (valueMax - valueMin)) * Math.PI;

		var radiusOutside = widgetWidth / 2 - widgetWidth / 20;
		var radiusInside  = radiusOutside - widgetWidth / 6.666666666666667 * gaugeWidthScale;
	  
		var centerX   = widgetWidth / 2 + widgetOffsetX;
		var centerY   = widgetHeight / 1.15 + widgetOffsetY;
	  
		var x1Outside = widgetWidth / 2 + widgetOffsetX + radiusOutside * Math.cos(alpha);
		var y1Outside = widgetHeight - (widgetHeight - centerY) + widgetOffsetY - radiusOutside * Math.sin(alpha);
		var x1Inside  = widgetWidth / 2 + widgetOffsetX + radiusInside * Math.cos(alpha);
		var y1Inside  = widgetHeight - (widgetHeight - centerY) + widgetOffsetY - radiusInside * Math.sin(alpha);
		var x2Outside = widgetWidth / 2 + widgetOffsetX + radiusOutside * Math.cos(beta);
		var y2Outside = widgetHeight - (widgetHeight - centerY) + widgetOffsetY - radiusOutside * Math.sin(beta);
		var x2Inside  = widgetWidth / 2 + widgetOffsetX + radiusInside * Math.cos(beta);
		var y2Inside  = widgetHeight - (widgetHeight - centerY) + widgetOffsetY - radiusInside * Math.sin(beta);
		var path      = "";

		path += "M" + x1Inside + "," + y1Inside + " ";
		path += "L" + x1Outside + "," + y1Outside + " ";
		path += "A" + radiusOutside + "," + radiusOutside + " 0 0,1 " + x2Outside + "," + y2Outside + " ";
		path += "L" + x2Inside + "," + y2Inside + " ";
		path += "A" + radiusInside + "," + radiusInside + " 0 0,0 " + x1Inside + "," + y1Inside + " ";
		path += "z ";
		return path;
	};
	
	this.calculateArcGradient = function (widgetWidth, widgetHeight, widgetOffsetX, widgetOffsetY, gaugeWidthScale)
	{
		var radiusOutside = widgetWidth / 2 - widgetWidth / 20;
		
		var centerX   = widgetWidth / 2 + widgetOffsetX;
		var centerY   = widgetHeight / 1.15 + widgetOffsetY;
	}

	// @description: Calculate any needle path.
	// @param:       value [float]: The value to display by the needle.
	// @param:       valueMin [float]: The minimum value of the display range.
	// @param:       valueMax [float]: The maximum value of the display range.
	// @param:       widgetWidth [int]: The width of the display area.
	// @param:       widgetHeight [int]: The height of the display area.
	// @param:       widgetOffsetX [int]: The offset of the display area.
	// @param:       widgetOffsetY [int]: The offset of the display area.
	// @param:       gaugeWidthScale [float]: The thickmess of the scale.
	// @return:      path [string]: The svg path of the needle.
	this.calculateNeedlePath = function (value, valueMin, valueMax, widgetWidth, widgetHeight, widgetOffsetX, widgetOffsetY, gaugeWidthScale)
	{
		var alpha   = (1.028 - (value - valueMin) / (valueMax - valueMin)) * Math.PI;
		var beta    = (1.01 - (value - valueMin) / (valueMax - valueMin)) * Math.PI;
		var gamma   = (1.00 - (value - valueMin) / (valueMax - valueMin)) * Math.PI;
		var delta   = (0.99 - (value - valueMin) / (valueMax - valueMin)) * Math.PI;
		var epsilon = (0.982 - (value - valueMin) / (valueMax - valueMin)) * Math.PI;

		var radiusPeak    = widgetWidth / 2 - widgetWidth / 50;
		var radiusOutside = widgetWidth / 2 - widgetWidth / 16;
		var radiusInside  = (widgetWidth / 2 - widgetWidth / 20) - widgetWidth / 6.666666666666667 * gaugeWidthScale;
	  
		var centerX = widgetWidth / 2 + widgetOffsetX;
		var centerY = widgetHeight / 1.15 + widgetOffsetY;
	  
		var x1Inside  = widgetWidth / 2 + widgetOffsetX + radiusInside * Math.cos(alpha);
		var y1Inside  = widgetHeight - (widgetHeight - centerY) + widgetOffsetY - radiusInside * Math.sin(alpha);
		var x1Outside = widgetWidth / 2 + widgetOffsetX + radiusOutside * Math.cos(beta);
		var y1Outside = widgetHeight - (widgetHeight - centerY) + widgetOffsetY - radiusOutside * Math.sin(beta);
		var xPeak     = widgetWidth / 2 + widgetOffsetX + radiusPeak * Math.cos(gamma);
		var yPeak     = widgetHeight - (widgetHeight - centerY) + widgetOffsetY - radiusPeak * Math.sin(gamma);
		var x2Outside = widgetWidth / 2 + widgetOffsetX + radiusOutside * Math.cos(delta);
		var y2Outside = widgetHeight - (widgetHeight - centerY) + widgetOffsetY - radiusOutside * Math.sin(delta);
		var x2Inside  = widgetWidth / 2 + widgetOffsetX + radiusInside * Math.cos(epsilon);
		var y2Inside  = widgetHeight - (widgetHeight - centerY) + widgetOffsetY - radiusInside * Math.sin(epsilon);
		var path      = "";

		path += "M" + x1Inside + "," + y1Inside + " ";
		path += "L" + x1Outside + "," + y1Outside + " ";
		path += "L" + xPeak + "," + yPeak + " ";
		path += "L" + x2Outside + "," + y2Outside + " ";
		path += "L" + x2Inside + "," + y2Inside + " ";
		path += "A" + radiusInside + "," + radiusInside + " 0 0,0 " + x1Inside + "," + y1Inside + " ";
		path += "z ";
		return path;
	};

// Create gradient.
if (this.config.showGaugeShadow == true)
{
	this.gradients = document.createElementNS(svgns, 'defs');
	this.gradients.setAttributeNS(null, 'id', "gradients");
	this.gradient = document.createElementNS(svgns, 'radialGradient');
	this.gradients.appendChild(this.gradient);
	this.gradient.setAttributeNS(null, 'id', this.config.id + "_gradient");
	this.gradient.setAttributeNS(null, 'cx', "50%");
	this.gradient.setAttributeNS(null, 'cy', "50%");
	this.gradient.setAttributeNS(null, 'r',  "100%");
	this.gradient.setAttributeNS(null, 'fx', "50%");
	this.gradient.setAttributeNS(null, 'fy', "50%");
	this.gradient.setAttributeNS(null, 'gradientTransform', "scale(1 2)");
	this.grad1sub1 = document.createElementNS(svgns, 'stop');
	this.gradient.appendChild(this.grad1sub1);
	this.grad1sub1.setAttributeNS(null, 'offset', "15%");
	this.grad1sub1.setAttributeNS(null, 'style', "stop-color:" + this.config.gaugeShadowColor + ";stop-opacity:1");
	this.grad1sub2 = document.createElementNS(svgns, 'stop');
	this.gradient.appendChild(this.grad1sub2);
	this.grad1sub2.setAttributeNS(null, 'offset', this.config.gaugeShadowScale * 33 + "%");
	this.grad1sub2.setAttributeNS(null, 'style', "stop-color:" + this.config.gaugeBackColor + ";stop-opacity:1");
	this.canvas.appendChild(this.gradients);
}

	// Draw canvas background.
	this.rectBG = document.createElementNS(svgns, 'rect');
	this.rectBG.setAttributeNS(null, 'stroke', "none");
	this.rectBG.setAttributeNS(null, 'fill',   this.config.canvasBackColor);
	this.rectBG.setAttributeNS(null, 'x',      this.config.offsetX);
	this.rectBG.setAttributeNS(null, 'y',      this.config.offsetY);
	this.rectBG.setAttributeNS(null, 'width',  this.config.canvasW);
	this.rectBG.setAttributeNS(null, 'height', this.config.canvasH);
	this.canvas.appendChild(this.rectBG);

	// Draw gauge background.
	this.gaugeBG = document.createElementNS(svgns, 'path');
	this.gaugeBG.setAttributeNS(null, 'stroke', this.config.gaugeBorderColor);
	this.gaugeBG.setAttributeNS(null, 'stroke-width', this.config.gaugeBorderWidth);
	if (this.config.showGaugeShadow == true)
	{
		this.gaugeBG.setAttributeNS(null, 'fill',   "url(#" + this.config.id + "_gradient)");
	}
	else
	{
		this.gaugeBG.setAttributeNS(null, 'fill',   this.config.gaugeBackColor);
	}
	this.gaugeBG.setAttributeNS(null, 'd',      this.calculateArcPath(this.config.min, this.config.max, this.config.min, this.config.max,
																	  this.config.canvasW, this.config.canvasH,
																	  this.config.offsetX, this.config.offsetY, this.config.gaugeWidthScale + 0.45));
	this.canvas.appendChild(this.gaugeBG);
	
	// Draw lower action range.
	if (this.config.lowerActionLimit >= 0.0)
	{
		this.gaugeLAR = document.createElementNS(svgns, 'path');
		this.gaugeLAR.setAttributeNS(null, 'stroke', "none");
		this.gaugeLAR.setAttributeNS(null, 'fill',   this.config.actionRangeColor);
		this.gaugeLAR.setAttributeNS(null, 'd',      this.calculateArcPath(this.config.min, this.config.lowerActionLimit, this.config.min, this.config.max,
																		   this.config.canvasW, this.config.canvasH,
																		   this.config.offsetX, this.config.offsetY, this.config.gaugeWidthScale));
		this.canvas.appendChild(this.gaugeLAR);
	}
	// Draw lower warning range.
	if (this.config.lowerWarningLimit >= 0.0 && this.config.lowerWarningLimit != this.config.min)
	{
		var lowerWarningStart = (this.config.lowerActionLimit >= 0.0) ? this.config.lowerActionLimit : this.config.min;
		this.gaugeLWR = document.createElementNS(svgns, 'path');
		this.gaugeLWR.setAttributeNS(null, 'stroke', "none");
		this.gaugeLWR.setAttributeNS(null, 'fill',   this.config.warningRangeColor);
		this.gaugeLWR.setAttributeNS(null, 'd',      this.calculateArcPath(lowerWarningStart, this.config.lowerWarningLimit, this.config.min, this.config.max,
																		   this.config.canvasW, this.config.canvasH,
																		   this.config.offsetX, this.config.offsetY, this.config.gaugeWidthScale));
		this.canvas.appendChild(this.gaugeLWR);
	}
	
	// Draw optimum range.
	if (this.config.lowerWarningLimit >= 0.0 && this.config.upperWarningLimit >= 0.0)
	{
		this.gaugeOPT = document.createElementNS(svgns, 'path');
		this.gaugeOPT.setAttributeNS(null, 'stroke', "none");
		this.gaugeOPT.setAttributeNS(null, 'fill',   this.config.optimumRangeColor);
		this.gaugeOPT.setAttributeNS(null, 'd',      this.calculateArcPath(this.config.lowerWarningLimit, this.config.upperWarningLimit, this.config.min, this.config.max,
																		   this.config.canvasW, this.config.canvasH,
																		   this.config.offsetX, this.config.offsetY, this.config.gaugeWidthScale));
		this.canvas.appendChild(this.gaugeOPT);
	}
	
	// Draw upper warning range.
	if (this.config.upperWarningLimit >= 0.0 && this.config.upperWarningLimit != this.config.max)
	{
		var upperWarningEnd = (this.config.upperActionLimit >= 0.0) ? this.config.upperActionLimit : this.config.max;
		this.gaugeUWR = document.createElementNS(svgns, 'path');
		this.gaugeUWR.setAttributeNS(null, 'stroke', "none");
		this.gaugeUWR.setAttributeNS(null, 'fill',   this.config.warningRangeColor);
		this.gaugeUWR.setAttributeNS(null, 'd',      this.calculateArcPath(this.config.upperWarningLimit, upperWarningEnd, this.config.min, this.config.max,
																		   this.config.canvasW, this.config.canvasH,
																		   this.config.offsetX, this.config.offsetY, this.config.gaugeWidthScale));
		this.canvas.appendChild(this.gaugeUWR);
	}
	
	// Draw upper action range.
	if (this.config.upperActionLimit > 0.0)
	{
		this.gaugeUAR = document.createElementNS(svgns, 'path');
		this.gaugeUAR.setAttributeNS(null, 'stroke', "none");
		this.gaugeUAR.setAttributeNS(null, 'fill',   this.config.actionRangeColor);
		this.gaugeUAR.setAttributeNS(null, 'd',      this.calculateArcPath(this.config.upperActionLimit, this.config.max, this.config.min, this.config.max,
																		   this.config.canvasW, this.config.canvasH,
																		   this.config.offsetX, this.config.offsetY, this.config.gaugeWidthScale));
		this.canvas.appendChild(this.gaugeUAR);
	}
	
	// Draw needle.
	this.gaugeNDL = document.createElementNS(svgns, 'path');
	this.gaugeNDL.setAttributeNS(null, 'stroke', "none");
	this.gaugeNDL.setAttributeNS(null, 'fill',   this.config.needleColor);
	this.gaugeNDL.setAttributeNS(null, 'd',      this.calculateNeedlePath(this.config.value, this.config.min, this.config.max,
																		  this.config.canvasW, this.config.canvasH,
																		  this.config.offsetX, this.config.offsetY, this.config.gaugeWidthScale + 0.49));
	this.canvas.appendChild(this.gaugeNDL);
	
	// Draw title.
	if (this.config.title && this.config.title != "")
	{
		this.gaugeTIT = document.createElementNS(svgns, 'text');
		this.gaugeTIT.setAttributeNS(null, 'x',      this.config.offsetX + this.config.canvasW / 2.0);
		this.gaugeTIT.setAttributeNS(null, 'y',      this.config.offsetY + this.config.canvasH / 5.0 - 10);
		this.gaugeTIT.setAttributeNS(null, 'style',  "font-family:Arial,Verdana; font-size:" + Math.floor(this.config.canvasW / 25) + "px; font-weight:bold; fill-opacity:1.0; fill:" + this.config.titleColor + "; text-anchor:middle;");
		this.gaugeTIT.appendChild(document.createTextNode(this.config.title));
		this.canvas.appendChild(this.gaugeTIT);
	}
	
	// Draw label.
	if (this.config.label && this.config.label != "")
	{
		this.gaugeLBL = document.createElementNS(svgns, 'text');
		this.gaugeLBL.setAttributeNS(null, 'x',      this.config.offsetX + this.config.canvasW / 2.0);
		this.gaugeLBL.setAttributeNS(null, 'y',      this.config.offsetY + this.config.canvasH / 1.04);
		this.gaugeLBL.setAttributeNS(null, 'style',  "font-family:Arial,Verdana; font-size:" + Math.floor(this.config.canvasW / 16) + "px; font-weight:normal; fill-opacity:1.0; fill:" + this.config.labelColor + "; text-anchor:middle;");
		this.gaugeLBL.appendChild(document.createTextNode(this.config.label));
		this.canvas.appendChild(this.gaugeLBL);
	}
	
	// Draw current value.
	this.gaugeVAL = document.createElementNS(svgns, 'text');
	this.gaugeVAL.setAttributeNS(null, 'x',      this.config.offsetX + this.config.canvasW / 2.0);
	this.gaugeVAL.setAttributeNS(null, 'y',      this.config.offsetY + this.config.canvasH / 1.2);
	this.gaugeVAL.setAttributeNS(null, 'style',  "font-family:Arial,Verdana; font-size:" + Math.floor(this.config.canvasW / 8) + "px; font-weight:bold; fill-opacity:1.0; fill:" + this.config.valueColor + "; text-anchor:middle;");
	this.gaugeVAL.appendChild(document.createTextNode(this.config.originalValue));
	this.canvas.appendChild(this.gaugeVAL);
	
	// Draw minimum.
	if (this.config.showMinMax == true)
	{
		this.gaugeMAX = document.createElementNS(svgns, 'text');
		this.gaugeMAX.setAttributeNS(null, 'x',      this.config.offsetX + this.config.canvasW / 20 + this.config.canvasW / 6.666666666666667 * this.config.gaugeWidthScale / 2);
		this.gaugeMAX.setAttributeNS(null, 'y',      this.config.offsetY + this.config.canvasH / 1.04);
		this.gaugeMAX.setAttributeNS(null, 'style',  "font-family:Arial,Verdana; font-size:" + Math.floor(this.config.canvasW / 16) + "px; font-weight:normal; fill-opacity:1.0; fill:" + this.config.minmaxColor + "; text-anchor:middle;");
		this.gaugeMAX.appendChild(document.createTextNode(this.config.min));
		this.canvas.appendChild(this.gaugeMAX);
	}
	
	// Draw maximum.
	if (this.config.showMinMax == true)
	{
		this.gaugeMIN = document.createElementNS(svgns, 'text');
		this.gaugeMIN.setAttributeNS(null, 'x',      this.config.offsetX + this.config.canvasW - (this.config.canvasW / 20 + this.config.canvasW / 6.666666666666667 * this.config.gaugeWidthScale / 2));
		this.gaugeMIN.setAttributeNS(null, 'y',      this.config.offsetY + this.config.canvasH / 1.04);
		this.gaugeMIN.setAttributeNS(null, 'style',  "font-family:Arial,Verdana; font-size:" + Math.floor(this.config.canvasW / 16) + "px; font-weight:normal; fill-opacity:1.0; fill:" + this.config.minmaxColor + "; text-anchor:middle;");
		this.gaugeMIN.appendChild(document.createTextNode(this.config.max));
		this.canvas.appendChild(this.gaugeMIN);
	}

	// @description: Update the value to display.
	// @param:       valueNew [float]: The value to display after update.
	// @return:      -
	this.refresh = function(valueNew, animated)
	{
		var oldValue = this.config.value;
		this.config.originalValue = valueNew;
		this.config.value = valueNew;
	  
		// Prevent value overflow.
		if (this.config.value > this.config.max)
			this.config.value = this.config.max; 
		if (this.config.value < this.config.min)
			this.config.value = this.config.min;
		
		this.gaugeVAL.childNodes[0].textContent = this.config.value;
		if (animated == true)
		{
			var incrementValue = (this.config.value - oldValue) / this.animation.startIncrementDivisor;

			// At that time, the timed out function will be called, the context, where 'this' pointer is known to, is no longer valid. 
			var gauge = this;
			setTimeout(function() {GaugeAnimationStep(gauge, oldValue, incrementValue, gauge.animation.maxIncrements);}, gauge.animation.delay);
		}
		else
		{
			this.gaugeNDL.setAttributeNS(null, 'd',
				this.calculateNeedlePath(this.config.value, this.config.min, this.config.max,
											 this.config.canvasW, this.config.canvasH,
											 this.config.offsetX, this.config.offsetY,
											 this.config.gaugeWidthScale + 0.49));
		}
	}

	// @description: Calculate a random value between min and max.
	// @param:       min [float]: The minimum of the gauge display range.
	// @param:       max [float]: The maximum of the gauge display range.
	// @return:      value [float]: A random value.
	this.randomSampleValue = function (min, max)
	{
		return Math.floor(Math.random() * (max - min + 1)) + min;
	}    
}

// @description: Animate a value update step.
// @param:       gaugeSVG [GaugeSVG]: The gauge to perform value step animation on.
// @param:       valueCurrent [float]: The current gauge display value.
// @param:       valueIncrement [float]: The increment value to apply this animation step.
// @param:       incrementsLeft [int]: The of increments left until animation stops.
// @return:      -
// @remark:      This function runs time shifted. It will be called when the context,
//               where 'this' pointer is known to, is not valid. Therefore implementation
//               is not made as class member.
function GaugeAnimationStep(gaugeSVG, valueCurrent, valueIncrement, incrementsLeft)
{
	if (incrementsLeft <= 0)
	{
		gaugeSVG.gaugeNDL.setAttributeNS(null, 'd',
			gaugeSVG.calculateNeedlePath(gaugeSVG.config.value, gaugeSVG.config.min, gaugeSVG.config.max,
										 gaugeSVG.config.canvasW, gaugeSVG.config.canvasH,
										 gaugeSVG.config.offsetX, gaugeSVG.config.offsetY,
										 gaugeSVG.config.gaugeWidthScale + 0.49));
	}
	else
	{
		valueCurrent = valueCurrent + valueIncrement;
		valueIncrement = valueIncrement * gaugeSVG.animation.decreaseOfIncrementValue;
		incrementsLeft = incrementsLeft - 1;
		gaugeSVG.gaugeNDL.setAttributeNS(null, 'd',
			gaugeSVG.calculateNeedlePath(valueCurrent, gaugeSVG.config.min, gaugeSVG.config.max,
										 gaugeSVG.config.canvasW, gaugeSVG.config.canvasH,
										 gaugeSVG.config.offsetX, gaugeSVG.config.offsetY,
										 gaugeSVG.config.gaugeWidthScale + 0.49));
		
		setTimeout(function() {GaugeAnimationStep(gaugeSVG, valueCurrent, valueIncrement, incrementsLeft);}, gaugeSVG.animation.delay);
	}
}