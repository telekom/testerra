function highlightElementStatic(e,r,g,b){
    a=1;
    e.style.outline="5px dotted rgba("+r+","+g+","+b+","+a+")";
}
function highlightElement(e,r,g,b){
    fadeBorder(e,r,g,b,1);
}
function fadeBorder(e,r,g,b,a) {
    e.style.outline="3px solid rgba("+r+","+g+","+b+","+a+")";
    a=a-0.1;
    if (a <= 0) {
        e.style.outline="";
        return;
    }
    setTimeout(function(){fadeBorder(e,r,g,b,a);},200);
}

TesterraCircleSize=100;

function TesterraClick(x,y) {
  var e = document.createElement("canvas");
  e.width=TesterraCircleSize;
  e.height=TesterraCircleSize;
  e.style="position:absolute; z-index:1000; left:" + (x - TesterraCircleSize/2) + "px; top:" + (y - TesterraCircleSize/2) + "px;";
  document.body.appendChild(e);

  drawTesterraClickCircle(e,TesterraCircleSize/2);
}

function drawTesterraClickCircle(e, size) {
  if (size <= 0) {
      e.parentNode.removeChild(e);
    return;
  }
  var ctx=e.getContext("2d");
  ctx.beginPath();
  ctx.clearRect(0, 0, TesterraCircleSize, TesterraCircleSize);
  ctx.arc(TesterraCircleSize/2,TesterraCircleSize/2,size,0,2*Math.PI);
  ctx.fillStyle = "rgba(255, 0, 0, .1)";
  ctx.fill();
  ctx.stroke();
  setTimeout(function(){
    size = size - 2;
    drawTesterraClickCircle(e, size);
    },20);
}

function getElementCenter(element) {
  var r = { x: element.getBoundingClientRect().left, y: element.getBoundingClientRect().top };
  r.x = r.x + element.getBoundingClientRect().width / 2;
  r.y = r.y + element.getBoundingClientRect().height / 2;
  return r;
}

function TesterraClickElement(element) {
  r=getElementCenter(element);
  TesterraClick(r.x,r.y);
}
