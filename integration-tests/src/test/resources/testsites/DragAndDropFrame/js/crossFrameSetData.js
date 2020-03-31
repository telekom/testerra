var dragObject = new function () {
	var me = this;
	
	var targetNode; 
	var eventNoticeNode, dragEventNoticeNode;
	
	var dataTransferCommentString;
	
	me.init = function () {
	
		if (EventHelpers.hasPageLoadHappened(arguments)) {
			return;
		}	
		
		
		targetNode=document.getElementById('dropTarget');
		eventNoticeNode = document.getElementById('eventNotice');
		dragEventNoticeNode = document.getElementById('dragEventNotice');
		
		/* These are events for the draggable objects */
		var dragNodes = cssQuery('[draggable=true]');
		for (var i = 0; i < dragNodes.length; i++) {
			var  dragNode=dragNodes[i]
			EventHelpers.addEvent(dragNode, 'dragstart', dragStartEvent);
		}
		
		/* These are events for the object to be dropped */
		if (targetNode) {
			EventHelpers.addEvent(targetNode, 'dragover', dragOverEvent);	
			EventHelpers.addEvent(targetNode, 'drop', dropEvent);
		}
	}
	
	

	
	function dragStartEvent(e) {
		e.dataTransfer.setData('Text',
			sprintf('<img src="%s" alt="%s" /><br /><p class="caption">%s</p>',
				this.src, this.alt, this.alt
			)
		);
	}
	
	
	function dragOverEvent(e) {
		EventHelpers.preventDefault(e);
	}
	
	
	function dropEvent(e) {
		this.innerHTML = e.dataTransfer.getData('Text');
		EventHelpers.preventDefault(e);
	}
	
	
	
	
	
}


EventHelpers.addPageLoadEvent('dragObject.init');
