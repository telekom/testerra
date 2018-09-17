/* 
*
* Easy front-end framework
*
* Copyright (c) 2011 Alen Grakalic
* http://easyframework.com/license.php
*
* supported by Templatica (http://templatica.com)
* and Css Globe (http://cssglobe.com)
* 
* built to be used with jQuery library
* http://jquery.com
* 
* update: Mar 22nd 2011
* 
*/
jQuery.browser = {};
jQuery.browser.opera = /opera/.test(navigator.userAgent.toLowerCase());

(function($){
    $.easy={navigation:function(_11){
        var _12={
            selector:"#nav li, nav li",
            className:"over"
        };
        if(typeof _11=="string"){
            _12.selector=_11;
        }
        var _11=$.extend(_12,_11);
        return $(_11.selector).each(function(){
            $(this).hover(function(){
                $("ul:first",this).fadeIn(100);
                $(this).addClass(_11.className);
            },function(){
            $("ul",this).hide();
            $(this).removeClass(_11.className);
            });
        });
    },
    tooltip:function(_13){
        var _14={
            selector:".tooltip",
            xOffset:10,
            yOffset:25,
            clickRemove:false,
            id:"easy_tooltip",
            content:"",
            useElement:""
        };
        if(typeof _13=="string"){
           _14.selector=_13;
        }
        var _13=$.extend(_14,_13);
        var _15;
        return $(_13.selector).each(function(){
           var _16=$(this).attr("title");
           $(this).hover(function(e){
               _15=(_13.content!="")?_13.content:_16;
               _15=(_13.useElement!="")?$("#"+_13.useElement).html():_15;
               $(this).attr("title","");
               if(_15!=""&&_15!=undefined){
                   $("body").append("<div id=\""+_13.id+"\">"+_15+"</div>");
                   $("#"+_13.id).css({
                       "position":"absolute","display":"none"
                   }).css("top",(e.pageY-_13.yOffset)+"px").css("left",(e.pageX+_13.xOffset)+"px").delay(400).fadeIn("fast");
               }
           },function(){
               $("#"+_13.id).remove();
               $(this).attr("title",_16);
           });
           $(this).mousemove(function(e){
               var x=((e.pageX+_13.xOffset+$(this).width())<$(window).width())?(e.pageX+_13.xOffset):(e.pageX-_13.xOffset-$(this).width()-16);
               $("#"+_13.id).css("top",(e.pageY-_13.yOffset)+"px").css("left",(x+"px"));
           });
           if(_13.clickRemove){
               $(this).mousedown(function(e){
                   $("#"+_13.id).remove();
                   $(this).attr("title",_16);
               });
            }
        });
    },
    popup:function(_17){
        var _18={
            selector:".popup",
            popupId:"easy_popup",
            preloadText:"Loading...",
            errorText:"There has been a problem with your request, please click outside this window to close it.",
            closeText:"Close",
            prevText:"&laquo; Previous",
            nextText:"Next &raquo;",
            opacity:0.7,
            hiddenClass:"hidden",
            callback:function(){}
        };
        if(typeof _17=="string"){
            _18.selector=_17;
        }
        var _17=$.extend(_18,_17);
        return $(_17.selector).each(function(i){var obj=this;var _19,_1a;
        var _1b=true;
        var _1c=this.tagName.toLowerCase();
        if($(this).hasClass("gallery")){
            var _1d=$(this).attr("class");
            _1d=_1d.split(" ").join("");
            $.data(this,"gallery",_1d);
            eval("if((typeof "+_1d+"_arr == \"undefined\")) "+_1d+"_arr= new Array()");
            eval(_1d+"_arr").push($(this));
            $.data(this,"index",eval(_1d+"_arr").length-1);
        }
        if(jQuery.browser.opera){
            $.support.opacity=true;
        }
        var ie6=(typeof document.body.style.maxHeight === "undefined");
        var _1e=false;//$.browser.opera&&$.browser.version<=9.5;
        var w,h,w2,h2;
        var cw,ch;cw=ch=0;
        var _1f=false;
        var _20=function(){w=$(window).width();
        h=$(document).height();
        w2=$(window).width()/2;
        h2=$(window).height()/2;
        if($("#"+_17.popupId).length==0){
            $("<div id=\""+_17.popupId+"\"></div>")
            .appendTo("body")
            .css({
                "width":w,
                "height":h,
                "position":"absolute",
                "top":"0",
                "left":"0",
                "z-index":"10000",
                "opacity":_17.opacity
                }).click(function(){
                    _26();
                });
        }$("<div id=\""+_17.popupId+"_preloader\">"+_17.preloadText+"</div>").appendTo("body");
        set($("#"+_17.popupId+"_preloader"));
        $("<div id=\""+_17.popupId+"_content\"></div>")
            .appendTo("body")
            .css({
                "visibility":"hidden",
                "position":"absolute",
                "top":"-10000px",
                "left":"-10000px"
            });
        $("<div id=\""+_17.popupId+"_inner\"></div>")
            .appendTo("#"+_17.popupId+"_content")
            .css({"overflow":"auto","height":"100%"});
        $("<small id=\""+_17.popupId+"_close\">"+_17.closeText+"</small>")
            .appendTo("#"+_17.popupId+"_inner")
            .click(function(){
                _26();
            });
        if ($(obj).attr("rel") != undefined){
            var rel=$(obj).attr("rel").split(";");
        }
        else {
            var rel="";
        }
        $.each(rel,function(i){
            if(rel[i].indexOf("width")!=-1){
                cw=rel[i].split(":")[1];
            }if(rel[i].indexOf("height")!=-1){
                ch=rel[i].split(":")[1];
            }
        });
        if($(obj).attr("title")!=undefined){
                $("<span class=\"caption\">"+$(obj).attr("title")+"</span>").appendTo("#"+_17.popupId+"_content").hide();
            }};
            var _21=function(){
                $("#"+_17.popupId+"_preloader").remove();
                var _22=$("#"+_17.popupId+"_content");
                var fh=false;
                if(cw!=0){
                    $(_22).css("width",parseInt(cw));
                }
                if(ch!=0){
                    $(_22).css("height",parseInt(ch));
                    fh=true;
                }if($(_22).width()>($(window).width()-50)){
                    $(_22).css("width",$(window).width()-50);
                }
                if($(_22).height()>($(window).height()-50)){
                    $(_22).css("height",$(window).height()-50);
                    fh=true;
                }
                $(".caption",_22).css({"width":$(_22).width(),"display":"block"});
                if($(".caption",_22).height()>0){
                    if(fh){
                        $("#"+_17.popupId+"_inner").height($("#"+_17.popupId+"_inner").height()-$(".caption",_22).outerHeight());
                    }
                }
                set($("#"+_17.popupId+"_content"));
                $("#"+_17.popupId+"_content").css("visibility","visible");
            };
            var set=function(_23){
                $(_23).css({
                    "text-align":"left",
                    "float":"left",
                    "position":"fixed",
                    "z-index":"10001",
                    "visible":"hidden"
                });
                var _24=w2-$(_23).width()/2;
                var top=h2-$(_23).height()/2;
                $(_23).css({
                    "left":_24,
                    "top":top,
                    "display":"none"
                }).fadeIn("1000");
                if(ie6){
                    $(_23).css({
                        "position":"absolute",
                        "top":(top+$(window).scrollTop())+"px"}
                    );
                }
                if(_1e){
                    $(_23).css({
                    "position":"absolute",
                    "top":(document.body["clientHeight"]/2-$(obj).height()/2+$(window).scrollTop())+"px"
                    });
                }
                if(ie6){
                    $("embed, object, select").css("visibility","hidden");
                }
            };
            var _25=function(){
                $("#"+_17.popupId+"_content").text(_17.errorText);
                _21();
            };
            var _26=function(){
                if(!_1b){
                    $(_19).addClass(_17.hiddenClass).appendTo(_1a);
                }
                $("#"+_17.popupId).remove();
                $("#"+_17.popupId+"_content").remove();
                $("#"+_17.popupId+"_preloader").remove();
                if(ie6){
                    $("embed, object, select").css("visibility","visible");
                }_17.callback();
            };
            if(_1c!="a"){
                _26();
                _20();
                _19=this;_1b=$(_19).is(":visible");
                _1a=$(_19).parent();
                if(_1b){
                    _19=$(_19).clone();
                }
                $(_19).removeClass(_17.hiddenClass).appendTo("#"+_17.popupId+"_inner").show();
                _21();
            }
            else{
                $(this).bind("click",function(e){
                    e.preventDefault();
                    _26();
                    _20();
                    var _27=$(this).attr("href");
                    var _28=_27.substr(_27.lastIndexOf(".")).toLowerCase();
                    var _29;
                    if($(this).hasClass("flash")){
                        var _2a="<object width=\"100%\" height=\"100%\"><param name=\"allowfullscreen\" value=\"true\" /><param name=\"allowscriptaccess\" value=\"always\" /><param name=\"movie\" value=\""+_27+"\" /><embed src=\""+_27+"\" type=\"application/x-shockwave-flash\" allowfullscreen=\"true\" allowscriptaccess=\"always\" width=\"100%\" height=\"100%\"></embed></object>";
                        $(_2a).appendTo("#"+_17.popupId+"_inner");
                        if(cw==0){
                            cw=600;
                        }
                        if(ch==0){
                            ch=400;
                        }
                        _1f=true;
                    }
                    else{
                    if(_28==".jpg"||_28==".jpeg"||_28==".gif"||_28==".png"||_28==".bmp"){
                        var img=new Image();
                        $(img).error(function(){
                            _25();
                        }).appendTo("#"+_17.popupId+"_inner");
                        img.onload=function(){
                            _21();
                            _1f=false;
                            img.onload=function(){};
                        };
                        img.src=_27+"?"+(new Date()).getTime()+" ="+(new Date()).getTime();
                    }
                    else{
                    if(_27.charAt(0)=="#"){
                        _19=$(_27).get(0);
                        _1b=$(_19).is(":visible");
                        _1a=$(_19).parent();
                        if(_1b){
                            _19=$(_19).clone();
                        }$(_19).removeClass(_17.hiddenClass).appendTo("#"+_17.popupId+"_inner").show();
                        _1f=true;
                    }else{
                    $("<iframe frameborder=\"0\" scrolling=\"auto\" style=\"width:100%;height:100%\" src=\""+_27+"\" />").appendTo("#"+_17.popupId+"_inner");
                    if(cw==0){
                        cw=900;
                    }if(ch==0){
                        ch=500;
                    }_1f=true;
                    }
                }
            }
            if(_1f){
                _21();
            }
            if($(this).hasClass("gallery")){
                var arr=$.data(this,"gallery");
                arr=eval(arr+"_arr");
                var _2b=$.data(this,"index");
                if(arr.length>1){
                    $("<small id=\""+_17.popupId+"_counter\">"+(_2b+1)+"/"+arr.length+"</small>").appendTo("#"+_17.popupId+"_inner");
                    $("<small id=\""+_17.popupId+"_gallery\"></small>").appendTo("#"+_17.popupId+"_inner");
                    if(_2b!=0){
                        $("<span id=\""+_17.popupId+"_prev\">"+_17.prevText+"</span>")
                            .appendTo("#"+_17.popupId+"_gallery")
                            .click(function(){
                                _26();
                                var obj=arr[_2b-1];
                                $(obj).trigger("click");
                            });
                    }
                    if(_2b<arr.length-1){
                        $("<span id=\""+_17.popupId+"_next\">"+_17.nextText+"</span>")
                            .appendTo("#"+_17.popupId+"_gallery")
                            .click(function(){
                                _26();
                                var obj=arr[_2b+1];
                                $(obj).trigger("click");
                             });
                    }
                }
            }
        });
    }
});
},
external:function(_2c){var _2d={selector:"a"};if(typeof _2c=="string"){_2d.selector=_2c;}var _2c=$.extend(_2d,_2c);var _2e=window.location.hostname;_2e=_2e.replace("www.","").toLowerCase();return $(_2c.selector).each(function(){var _2f=$(this).attr("href");if(_2f==undefined)_2f="";else _2f=_2f.toLowerCase();if(_2f.indexOf("http://")!=-1&&_2f.indexOf(_2e)==-1){$(this).attr("target","_blank");$(this).addClass("external");}});},rotate:function(_30){var _31={selector:".rotate",initPause:0,pause:5000,randomize:false,callback:function(){}};if(typeof _30=="string"){_31.selector=_30;}var _30=$.extend(_31,_30);return $(_30.selector).each(function(){var obj=$(this);var _32=$(obj).children().length;var _33=0;function _34(){var ran=Math.floor(Math.random()*_32)+1;return ran;};function _35(){if(_30.randomize){var ran=_34();while(ran==_33){ran=_34();}_33=ran;}else{_33=(_33==_32)?1:_33+1;}$(obj).children().hide();$(obj).children(":nth-child("+_33+")").fadeIn("slow",function(){_30.callback();});};function _36(){_35();setInterval(_35,_30.pause);};if(_32>1){setTimeout(_36,_30.initPause);}});},cycle:function(_37){var _38={selector:".cycle",effect:"fade",initPause:0,pause:5000,callback:function(){}};if(typeof _37=="string"){_38.selector=_37;}var _37=$.extend(_38,_37);return $(_37.selector).each(function(){var obj=$(this);var _39=$(obj).children().length;var _3a=0;var _3b=-1;var z=1;var h=$(obj).children(":nth-child(1)").height();var w=$(obj).children(":nth-child(1)").width();var _3c=($(obj).css("position")=="absolute")?"absolute":"relative";$(obj).css({"position":_3c,"overflow":"hidden"}).height(h).width(w);$(obj).children().hide().css({"position":"absolute","top":"0","left":"0"});function _3d(){_3a=(_3a==_39)?1:_3a+1;_3b=(_3a==1)?_39:_3a-1;tempObj=$(obj).children(":nth-child("+_3a+")");prevObj=$(obj).children(":nth-child("+_3b+")");if(_37.effect=="slideUp"){$(prevObj).animate({top:h*(-1)},function(){$(prevObj).hide();$(tempObj).css({"z-index":z,"top":h}).show().animate({top:0});});}else{$(tempObj).css("z-index",z).fadeIn("slow",function(){$(prevObj).fadeOut("slow",function(){_37.callback();});});}z++;};function _3e(){_3d();setInterval(_3d,_37.pause);};setTimeout(_3e,_37.initPause);});},jump:function(_3f){var _40={selector:"a.jump",speed:500};if(typeof _3f=="string"){_40.selector=_3f;}var _3f=$.extend(_40,_3f);return $(_3f.selector).click(function(){var _41=$($(this).attr("href"));var _42=$(_41).offset().top;$("html,body").animate({scrollTop:_42},_3f.speed,"linear");});},showhide:function(_43){var _44={selector:".toggle"};if(typeof _43=="string"){_44.selector=_43;}var _43=$.extend(_44,_43);return $(_43.selector).each(function(){var _45;if($(this).hasClass("prev")){_45=$(this).prev().hide();}else{if($(this).hasClass("id")){_45=$(this).attr("href");_45=$(_45).hide();}else{_45=$(this).next().hide();}}$(this).css("cursor","pointer");$(this).toggle(function(){$(this).addClass("expanded");$(_45).slideDown();},function(){$(_45).slideUp();$(this).removeClass("expanded");});});},forms:function(_46){var _47={selector:"form",err:"This is required",errEmail:"Valid email address is required",errUrl:"URL is required",errPhone:"Phone number is required",notValidClass:"notvalid",validCallback:function(obj){},notValidCallback:function(obj){},ajax:false,ajaxParams:{}};function _48(obj){if($(obj).val()==""||_49(obj)){var _4a=($(obj).attr("title")!=undefined)?$(obj).attr("title"):_46.err;_4b(obj,_4a);}};function _4c(_4d,_4e){if($(_4d).val()!=$(_4e).val()){var _4f=($(_4d).attr("title")!=undefined)?$(_4d).attr("title"):_46.err;_4b(_4d,_4f);}};function _50(obj,_51){var _52,err;switch(_51){case "url":_52=/(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;err=_46.errUrl;break;case "phone":var _52=/[\d\s_-]/;err=_46.errPhone;break;default:_52=/^[^@]+@[^@]+.[a-z]{2,}$/;err=_46.errEmail;}var val=$(obj).val();if(val.search(_52)==-1||_49(obj)){var _53=($(obj).attr("title")!=undefined)?$(obj).attr("title"):err;_4b(obj,_53);}};function _54(obj){var _55=$(obj).attr("class");var _56=$(":password[class=\""+_55+"\"], :password[class=\""+_55+" "+_46.notValidClass+"\"]");var _57=$(_56).index(obj);if(_57!=0){return _4c(obj,$(_56).get(0));}else{return _48(obj);}};function _49(obj){var _58=$("label[for="+$(obj).attr("id")+"]").text();return (_58==$(obj).val());};function _4b(obj,_59){var _5a=$(obj).parent();_5a.append("<span class=\"error\">"+_59+"</span>");$("span.error",_5a).hide().fadeIn("fast");$(obj).addClass(_46.notValidClass);valid=false;};$("input.label,textarea.label").each(function(){var _5b=$("label[for="+$(this).attr("id")+"]").text();$("label[for="+$(this).attr("id")+"]").css("display","none");$(this).val(_5b);$(this).focus(function(){if($(this).val()==_5b){$(this).val("");}});$(this).blur(function(){if($(this).val()==""){$(this).val(_5b);}});});if(typeof _46=="string"){_47.selector=_46;}var _46=$.extend(_47,_46);return $(_46.selector).each(function(){var _5c=this;$(_5c).submit(function(){$(".error",_5c).remove();$("."+_46.notValidClass,_5c).removeClass(_46.notValidClass);valid=true;$(":text.required",_5c).each(function(){if($(this).hasClass("email")){_50(this,"email");}else{if($(this).hasClass("url")){_50(this,"url");}else{if($(this).hasClass("phone")){_50(this,"phone");}else{_48(this);}}}});$(":password.required",_5c).each(function(){_54(this);});$("textarea.required",_5c).each(function(){_48(this);});$(":checkbox.required",_5c).each(function(){if(!$(this).attr("checked")){var _5d=($(this).attr("title")!=undefined)?$(this).attr("title"):_46.err;_4b(this,_5d);}});if(valid){$(".label",_5c).each(function(){if(_49(this)){$(this).val("");}});}if(valid){_46.validCallback();}else{_46.notValidCallback();}if(_46.ajax){if(_46.ajaxParams.data==undefined){_46.ajaxParams.data=values(_5c);}if(valid){$.ajax(_46.ajaxParams);}return false;}else{return valid;}});});},accordion:function(_5e){var _5f={selector:".accordion",parent:"li",source:"h3",target:"p"};if(typeof _5e=="string"){_5f.selector=_5e;}var _5e=$.extend(_5f,_5e);return $(_5e.selector).each(function(){var obj=this;$(_5e.parent,this).each(function(){var _60=$(_5e.target,this);$(_5e.target,this).hide();$(_5e.source,this).css({"cursor":"pointer"}).click(function(){$(_5e.target,_5e.selector).slideUp();if(!$(_60).is(":visible")){$(_60).slideDown();}});});});},tabs:function(_61){var _62={selector:".tabs",selectedClass:"selected"};if(typeof _61=="string"){_62.selector=_61;}var _61=$.extend(_62,_61);return $(_61.selector).each(function(){var obj=this;var _63=Array();function _64(i){$.each(_63,function(_65,_66){$(_66).hide();});$(_63[i]).fadeIn();$(obj).children().removeClass(_61.selectedClass);selected=$(obj).children().get(i);$(selected).addClass(_61.selectedClass);};$("a",this).each(function(i){_63.push($(this).attr("href"));$(this).click(function(e){e.preventDefault();_64(i);});});_64(0);});}};})(jQuery);