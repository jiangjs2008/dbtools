/*
 * $Id: om-core.js,v 1.27 2012/06/19 08:40:21 licongping Exp $
 * operamasks-ui om-core @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 */
(function( $, undefined ) {
// prevent duplicate loading
// this is only a problem because we proxy existing functions
// and we don't want to double proxy them
$.om = $.om || {};
if ( $.om.version ) {
	return;
}

$.extend( $.om, {
	version: "2.0",
	keyCode: {
	    TAB: 9,
	    ENTER: 13,
	    ESCAPE: 27,
	    SPACE: 32,
		LEFT: 37,
		UP: 38,
		RIGHT: 39,
		DOWN: 40
	},
	lang : {
		// 获取属性的国际化字符串，如果组件的options中已经设置这个值就直接使用，否则从$.om.lang[comp]中获取
		_get : function(options, comp, attr){
			return options[attr] ? options[attr] : $.om.lang[comp][attr]; 
		}
	}
});
// plugins
$.fn.extend({
	propAttr: $.fn.prop || $.fn.attr,
	_oldFocus: $.fn.focus,//为避免与jQuery ui冲突导致死循环，这里不要取名为'_focus'
	//设置元素焦点（delay：延迟时间）
	focus: function( delay, fn ) {
		return typeof delay === "number" ?
			this.each(function() {
				var elem = this;
				setTimeout(function() {
					$( elem ).focus();
					if ( fn ) {
						fn.call( elem );
					}
				}, delay );
			}) :
			this._oldFocus.apply( this, arguments );
	},
	//获取设置滚动属性的 父元素
	scrollParent: function() {
		var scrollParent;
		if (($.browser.msie && (/(static|relative)/).test(this.css('position'))) || (/absolute/).test(this.css('position'))) {
			scrollParent = this.parents().filter(function() {
				return (/(relative|absolute|fixed)/).test($.curCSS(this,'position',1)) && (/(auto|scroll)/).test($.curCSS(this,'overflow',1)+$.curCSS(this,'overflow-y',1)+$.curCSS(this,'overflow-x',1));
			}).eq(0);
		} else {
			scrollParent = this.parents().filter(function() {
				return (/(auto|scroll)/).test($.curCSS(this,'overflow',1)+$.curCSS(this,'overflow-y',1)+$.curCSS(this,'overflow-x',1));
			}).eq(0);
		}
		return (/fixed/).test(this.css('position')) || !scrollParent.length ? $(document) : scrollParent;
	},
	//设置或获取元素的垂直坐标
	zIndex: function( zIndex ) {
		if ( zIndex !== undefined ) {
			return this.css( "zIndex", zIndex );
		}
		if ( this.length ) {
			var elem = $( this[ 0 ] ), position, value;
			while ( elem.length && elem[ 0 ] !== document ) {
				// Ignore z-index if position is set to a value where z-index is ignored by the browser
				// This makes behavior of this function consistent across browsers
				// WebKit always returns auto if the element is positioned
				position = elem.css( "position" );
				if ( position === "absolute" || position === "relative" || position === "fixed" ) {
					// IE returns 0 when zIndex is not specified
					// other browsers return a string
					// we ignore the case of nested elements with an explicit value of 0
					// <div style="z-index: -10;"><div style="z-index: 0;"></div></div>
					value = parseInt( elem.css( "zIndex" ), 10 );
					if ( !isNaN( value ) && value !== 0 ) {
						return value;
					}
				}
				elem = elem.parent();
			}
		}
		return 0;
	},
	//设置元素不支持被选择
	disableSelection: function() {
		return this.bind( ( $.support.selectstart ? "selectstart" : "mousedown" ) +
			".om-disableSelection", function( event ) {
				event.preventDefault();
			});
	},
	//设置元素支持被选择
	enableSelection: function() {
		return this.unbind( ".om-disableSelection" );
	}
});
// 扩展innerWidth、innerHeight、outerWidth和outerHeight方法，如果不传参则获取值，如果传参则设置计算后的宽高。
$.each( [ "Width", "Height" ], function( i, name ) {
	var side = name === "Width" ? [ "Left", "Right" ] : [ "Top", "Bottom" ],
		type = name.toLowerCase(),
		orig = {
			innerWidth: $.fn.innerWidth,
			innerHeight: $.fn.innerHeight,
			outerWidth: $.fn.outerWidth,
			outerHeight: $.fn.outerHeight
		};

	function reduce( elem, size, border, margin ) {
		$.each( side, function() {
			size -= parseFloat( $.curCSS( elem, "padding" + this, true) ) || 0;
			if ( border ) {
				size -= parseFloat( $.curCSS( elem, "border" + this + "Width", true) ) || 0;
			}
			if ( margin ) {
				size -= parseFloat( $.curCSS( elem, "margin" + this, true) ) || 0;
			}
		});
		return size;
	}

	$.fn[ "inner" + name ] = function( size ) {
		if ( size === undefined ) {
			// 返回innerWidth/innerHeight
			return orig[ "inner" + name ].call( this );
		}
		return this.each(function() {
			// 设置宽度/高度 = (size - padding)
			$( this ).css( type, reduce( this, size ) + "px" );
		});
	};

	$.fn[ "outer" + name] = function( size, margin ) {
		if ( typeof size !== "number" ) {
			// 返回outerWidth/outerHeight
			return orig[ "outer" + name ].call( this, size );
		}
		return this.each(function() {
			// 设置宽度/高度 = (size - padding - border - margin)
			$( this).css( type, reduce( this, size, true, margin ) + "px" );
		});
	};
});
// selectors
function focusable( element, isTabIndexNotNaN ) {
	var nodeName = element.nodeName.toLowerCase();
	if ( "area" === nodeName ) {
		var map = element.parentNode,
			mapName = map.name,
			img;
		if ( !element.href || !mapName || map.nodeName.toLowerCase() !== "map" ) {
			return false;
		}
		img = $( "img[usemap=#" + mapName + "]" )[0];
		return !!img && visible( img );
	}
	return ( /input|select|textarea|button|object/.test( nodeName )
		? !element.disabled
		: "a" == nodeName
			? element.href || isTabIndexNotNaN
			: isTabIndexNotNaN)
		// the element and all of its ancestors must be visible
		&& visible( element );
}
function visible( element ) {
	return !$( element ).parents().andSelf().filter(function() {
		return $.curCSS( this, "visibility" ) === "hidden" ||
			$.expr.filters.hidden( this );
	}).length;
}
$.extend( $.expr[ ":" ], {
	data: function( elem, i, match ) {
		return !!$.data( elem, match[ 3 ] );
	},
	focusable: function( element ) {
		return focusable( element, !isNaN( $.attr( element, "tabindex" ) ) );
	},
	tabbable: function( element ) {
		var tabIndex = $.attr( element, "tabindex" ),
			isTabIndexNaN = isNaN( tabIndex );
		return ( isTabIndexNaN || tabIndex >= 0 ) && focusable( element, !isTabIndexNaN );
	}
});
// support
$(function() {
	var body = document.body,
		div = body.appendChild( div = document.createElement( "div" ) );
	$.extend( div.style, {
		minHeight: "100px",
		height: "auto",
		padding: 0,
		borderWidth: 0
	});
	// 判断当前浏览器环境是否支持minHeight属性
	$.support.minHeight = div.offsetHeight === 100;
	$.support.selectstart = "onselectstart" in div;
	// set display to none to avoid a layout bug in IE
	// http://dev.jquery.com/ticket/4014
	body.removeChild( div ).style.display = "none";
});

// deprecated
$.extend( $.om, {
	// $.om.plugin is deprecated.  Use the proxy pattern instead.
	plugin: {
		add: function( module, option, set ) {
			var proto = $.om[module].prototype;
			for ( var i in set ) {
				proto.plugins[ i ] = proto.plugins[ i ] || [];
				proto.plugins[ i ].push( [ option, set[ i ] ] );
			}
		},
		call: function( instance, name, args ) {
			var set = instance.plugins[ name ];
			if ( !set || !instance.element[ 0 ].parentNode ) {
				return;
			}
			for ( var i = 0; i < set.length; i++ ) {
				if ( instance.options[ set[ i ][ 0 ] ] ) {
					set[ i ][ 1 ].apply( instance.element, args );
				}
			}
		}
	}
});

})( jQuery );


(function( $, undefined ) {
// jQuery 1.4+
if ( $.cleanData ) {
	var _cleanData = $.cleanData;
	$.cleanData = function( elems ) {
		for ( var i = 0, elem; (elem = elems[i]) != null; i++ ) { 
			$( elem ).triggerHandler( "om-remove" );
		}
		_cleanData( elems );
	};
}

$.omWidget = function( name, base, prototype ) {
	var namespace = name.split( "." )[ 0 ],
		fullName;
	name = name.split( "." )[ 1 ];
	fullName = namespace + "-" + name;
	// 例如参数name='om.tabs'，变成namespace='om',name='tabs',fullName='om-tabs' 
	// base默认为Widget类，组件默认会继承base类的所有方法  
	if ( !prototype ) {
		prototype = base;
		base = $.OMWidget;
	}
	// create selector for plugin
	$.expr[ ":" ][ fullName ] = function( elem ) {
		return !!$.data( elem, name );
	};
	// 创建命名空间$.om.tabs  
	$[ namespace ] = $[ namespace ] || {};
	// 组件的构造函数
	$[ namespace ][ name ] = function( options, element ) {
		// allow instantiation without initializing for simple inheritance
		if ( arguments.length ) {
			this._createWidget( options, element );
		}
	};
	// 初始化父类，一般调用了$.Widget  
	var basePrototype = new base();
	// we need to make the options hash a property directly on the new instance
	// otherwise we'll modify the options hash on the prototype that we're
	// inheriting from
//		$.each( basePrototype, function( key, val ) {
//			if ( $.isPlainObject(val) ) {
//				basePrototype[ key ] = $.extend( {}, val );
//			}
//		});
	basePrototype.options = $.extend( true, {}, basePrototype.options );
	// 给om.tabs继承父类的所有原型方法和参数  
	$[ namespace ][ name ].prototype = $.extend( true, basePrototype, {
		namespace: namespace,
		widgetName: name,
		// 组件的事件名前缀，调用_trigger的时候会默认给trigger的事件加上前缀  
        // 例如_trigger('create')实际会触发'tabscreate'事件  
		widgetEventPrefix: $[ namespace ][ name ].prototype.widgetEventPrefix || name,
		widgetBaseClass: fullName
	}, prototype );
	// 把tabs方法挂到jquery对象上，也就是$('#tab1').tabs();  
	$.omWidget.bridge( name, $[ namespace ][ name ] );
};

$.omWidget.bridge = function( name, object ) {
	$.fn[ name ] = function( options ) {
		// 如果tabs方法第一个参数是string类型，则认为是调用组件的方法，否则调用options方法  
		var isMethodCall = typeof options === "string",
			args = Array.prototype.slice.call( arguments, 1 ),
			returnValue = this;
		// allow multiple hashes to be passed on init
		options = !isMethodCall && args.length ?
			$.extend.apply( null, [ true, options ].concat(args) ) :
			options;
		// '_'开头的方法被认为是内部方法，不会被执行，如$('#tab1').tabs('_init')  
		if ( isMethodCall && options.charAt( 0 ) === "_" ) {
			return returnValue;
		}
		if ( isMethodCall ) {
			this.each(function() {
				// 执行组件方法  
				var instance = $.data( this, name );
				if (options == 'options') {
				    returnValue = instance && instance.options;
				    return false;
                } else {
    				var	methodValue = instance && $.isFunction( instance[options] ) ?
    						instance[ options ].apply( instance, args ) : instance;
    				if ( methodValue !== instance && methodValue !== undefined ) {
    					returnValue = methodValue;
    					return false;
    				}
                }
			});
		} else {
			// 调用组件的options方法  
			this.each(function() {
				var instance = $.data( this, name );
				if ( instance ) {
					// 设置options后再次调用_init方法，第一次调用是在_createWidget方法里面。这个方法需要开发者去实现。  
                    // 主要是当改变组件中某些参数后可能需要对组件进行重画  
                    instance._setOptions( options || {} );
				    $.extend(instance.options, options);
				    $(instance.beforeInitListeners).each(function(){
				        this.call(instance);
				    });
					instance._init();
					$(instance.initListeners).each(function(){
				        this.call(instance);
				    });
				} else {
					// 没有实例的话，在这里调用组件类的构造函数，并把构造后的示例保存在dom的data里面。注意这里的this是dom，object是模块类 
					$.data( this, name, new object( options, this ) );
				}
			});
		}

		return returnValue;
	};
};
$.omWidget.addCreateListener = function(name,fn){
    var temp=name.split( "." );
    $[ temp[0] ][ temp[1] ].prototype.createListeners.push(fn);
};
$.omWidget.addInitListener = function(name,fn){
    var temp=name.split( "." );
    $[ temp[0] ][ temp[1] ].prototype.initListeners.push(fn);
};
$.omWidget.addBeforeInitListener = function(name,fn){
    var temp=name.split( "." );
    $[ temp[0] ][ temp[1] ].prototype.beforeInitListeners.push(fn);
};
$.OMWidget = function( options, element ) {
    this.createListeners=[];
    this.initListeners=[];
    this.beforeInitListeners=[];
	// allow instantiation without initializing for simple inheritance
	if ( arguments.length ) {
		this._createWidget( options, element );
	}
};
$.OMWidget.prototype = {
	widgetName: "widget",
	widgetEventPrefix: "",
	options: {
		disabled: false
	},
	_createWidget: function( options, element ) {
		// $.widget.bridge stores the plugin instance, but we do it anyway
		// so that it's stored even before the _create function runs
		$.data( element, this.widgetName, this );
		this.element = $( element );
		this.options = $.extend( true, {},
			this.options,
			this._getCreateOptions(),
			options );
		var self = this;
		//注意，不要少了前边的 "om-"，不然会与jquery-ui冲突
		this.element.bind( "om-remove._" + this.widgetName, function() {
			self.destroy();
		});
		// 开发者实现  
		this._create();
		$(this.createListeners).each(function(){
	        this.call(self);
	    });
		// 如果绑定了初始化的回调函数，会在这里触发。注意绑定的事件名是需要加上前缀的，如$('#tab1').bind('tabscreate',function(){});  
		this._trigger( "create" );
		// 开发者实现 
		$(this.beforeInitListeners).each(function(){
	        this.call(self);
	    });
		this._init();
		$(this.initListeners).each(function(){
	        this.call(self);
	    });
	},
	_getCreateOptions: function() {
		return $.metadata && $.metadata.get( this.element[0] )[ this.widgetName ];
	},
	_create: function() {},
	_init: function() {},
	destroy: function() {
		this.element
			.unbind( "." + this.widgetName )
			.removeData( this.widgetName );
		this.widget()
			.unbind( "." + this.widgetName );
	},
	widget: function() {
		return this.element;
	},
	option: function( key, value ) {
        var options = key;
        if ( arguments.length === 0 ) {
            // don't return a reference to the internal hash
            return $.extend( {}, this.options );
        }
        if  (typeof key === "string" ) {
            if ( value === undefined ) {
                return this.options[ key ]; // 获取值
            }
            options = {};
            options[ key ] = value;
        }
        this._setOptions( options ); // 设置值
        return this;
    },
	_setOptions: function( options ) {
		var self = this;
		$.each( options, function( key, value ) {
			self._setOption( key, value );
		});
		return this;
	},
	_setOption: function( key, value ) {
		this.options[ key ] = value;
		return this;
	},
	
	// $.widget中优化过的trigger方法。type是回调事件的名称，如"onRowClick"，event是触发回调的事件（通常没有这个事件的时候传null）
	// 这个方法只声明了两个参数，如有其他参数可以直接写在event参数后面
	_trigger: function( type, event ) {
		// 获取初始化配置config中的回调方法
		var callback = this.options[ type ];
		// 封装js标准event对象为jquery的Event对象
		event = $.Event( event );
		event.type = type;
		// copy original event properties over to the new event
		// this would happen if we could call $.event.fix instead of $.Event
		// but we don't have a way to force an event to be fixed multiple times
		if ( event.originalEvent ) {
			for ( var i = $.event.props.length, prop; i; ) {
				prop = $.event.props[ --i ];
				event[ prop ] = event.originalEvent[ prop ];
			}
		}
		// 构造传给回调函数的参数，event放置在最后
		var newArgs = [],
			argLength = arguments.length;
		for(var i = 2; i < argLength; i++){
			newArgs[i-2] = arguments[i];
		}
		if( argLength > 1){
			newArgs[argLength-2] = arguments[1];
		}
		return !( $.isFunction(callback) &&
			callback.apply( this.element, newArgs ) === false ||
			event.isDefaultPrevented() );
	}
};
})( jQuery );/*
 * $Id: om-mouse.js,v 1.3 2012/03/29 06:01:25 chentianzhen Exp $
 * operamasks-ui omMouse @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 * 
 * Depends:
 *  om-core.js
 */
(function( $, undefined ) {

$.omWidget("om.omMouse", {
	options: {
		cancel: ':input,option',
		distance: 1,
		delay: 0
	},
	_mouseInit: function() {
		var self = this;

		this.element
			.bind('mousedown.'+this.widgetName, function(event) {
				return self._mouseDown(event);
			})
			.bind('click.'+this.widgetName, function(event) {
				if (true === $.data(event.target, self.widgetName + '.preventClickEvent')) {
				    $.removeData(event.target, self.widgetName + '.preventClickEvent');
					event.stopImmediatePropagation();
					return false;
				}
			});

		this.started = false;
	},

	// TODO: make sure destroying one instance of mouse doesn't mess with
	// other instances of mouse
	_mouseDestroy: function() {
		this.element.unbind('.'+this.widgetName);
	},

	_mouseDown: function(event) {
		// don't let more than one widget handle mouseStart
		// TODO: figure out why we have to use originalEvent
		event.originalEvent = event.originalEvent || {};
		if (event.originalEvent.mouseHandled) { return; }

		// we may have missed mouseup (out of window)
		(this._mouseStarted && this._mouseUp(event));

		this._mouseDownEvent = event;

		var self = this,
			btnIsLeft = (event.which == 1),
			elIsCancel = (typeof this.options.cancel == "string" ? $(event.target).closest(this.options.cancel).length : false);
		if (!btnIsLeft || elIsCancel || !this._mouseCapture(event)) {
			return true;
		}

		this.mouseDelayMet = !this.options.delay;
		if (!this.mouseDelayMet) {
			this._mouseDelayTimer = setTimeout(function() {
				self.mouseDelayMet = true;
			}, this.options.delay);
		}

		if (this._mouseDistanceMet(event) && this._mouseDelayMet(event)) {
			this._mouseStarted = (this._mouseStart(event) !== false);
			if (!this._mouseStarted) {
				event.preventDefault();
				return true;
			}
		}

		// Click event may never have fired (Gecko & Opera)
		if (true === $.data(event.target, this.widgetName + '.preventClickEvent')) {
			$.removeData(event.target, this.widgetName + '.preventClickEvent');
		}

		// these delegates are required to keep context
		this._mouseMoveDelegate = function(event) {
			return self._mouseMove(event);
		};
		this._mouseUpDelegate = function(event) {
			return self._mouseUp(event);
		};
		$(document)
			.bind('mousemove.'+this.widgetName, this._mouseMoveDelegate)
			.bind('mouseup.'+this.widgetName, this._mouseUpDelegate);

		event.preventDefault();
		event.originalEvent.mouseHandled = true;
		return true;
	},

	_mouseMove: function(event) {
		// IE mouseup check - mouseup happened when mouse was out of window
		if ($.browser.msie && !(document.documentMode >= 9) && !event.button) {
			return this._mouseUp(event);
		}

		if (this._mouseStarted) {
			this._mouseDrag(event);
			return event.preventDefault();
		}

		if (this._mouseDistanceMet(event) && this._mouseDelayMet(event)) {
			this._mouseStarted =
				(this._mouseStart(this._mouseDownEvent, event) !== false);
			(this._mouseStarted ? this._mouseDrag(event) : this._mouseUp(event));
		}

		return !this._mouseStarted;
	},

	_mouseUp: function(event) {
		$(document)
			.unbind('mousemove.'+this.widgetName, this._mouseMoveDelegate)
			.unbind('mouseup.'+this.widgetName, this._mouseUpDelegate);

		if (this._mouseStarted) {
			this._mouseStarted = false;

			if (event.target == this._mouseDownEvent.target) {
			    $.data(event.target, this.widgetName + '.preventClickEvent', true);
			}

			this._mouseStop(event);
		}

		return false;
	},

	_mouseDistanceMet: function(event) {
		return (Math.max(
				Math.abs(this._mouseDownEvent.pageX - event.pageX),
				Math.abs(this._mouseDownEvent.pageY - event.pageY)
			) >= this.options.distance
		);
	},

	_mouseDelayMet: function(event) {
		return this.mouseDelayMet;
	},

	// These are placeholder methods, to be overriden by extending plugin
	_mouseStart: function(event) {},
	_mouseDrag: function(event) {},
	_mouseStop: function(event) {},
	_mouseCapture: function(event) { return true; }
});

})(jQuery);
/*
 * $Id: om-position.js,v 1.2 2012/03/29 06:02:46 chentianzhen Exp $
 * operamasks-ui omPosition @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 */
(function( $, undefined ) {

$.om = $.om || {};

var horizontalPositions = /left|center|right/,
	verticalPositions = /top|center|bottom/,
	center = "center",
	_position = $.fn.position,
	_offset = $.fn.offset;

$.fn.position = function( options ) {
	if ( !options || !options.of ) {
		return _position.apply( this, arguments );
	}

	// make a copy, we don't want to modify arguments
	options = $.extend( {}, options );

	var target = $( options.of ),
		targetElem = target[0],
		collision = ( options.collision || "flip" ).split( " " ),
		offset = options.offset ? options.offset.split( " " ) : [ 0, 0 ],
		targetWidth,
		targetHeight,
		basePosition;

	if ( targetElem.nodeType === 9 ) {
		targetWidth = target.width();
		targetHeight = target.height();
		basePosition = { top: 0, left: 0 };
	// TODO: use $.isWindow() in 1.9
	} else if ( targetElem.setTimeout ) {
		targetWidth = target.width();
		targetHeight = target.height();
		basePosition = { top: target.scrollTop(), left: target.scrollLeft() };
	} else if ( targetElem.preventDefault ) {
		// force left top to allow flipping
		options.at = "left top";
		targetWidth = targetHeight = 0;
		basePosition = { top: options.of.pageY, left: options.of.pageX };
	} else {
		targetWidth = target.outerWidth();
		targetHeight = target.outerHeight();
		basePosition = target.offset();
	}

	// force my and at to have valid horizontal and veritcal positions
	// if a value is missing or invalid, it will be converted to center 
	$.each( [ "my", "at" ], function() {
		var pos = ( options[this] || "" ).split( " " );
		if ( pos.length === 1) {
			pos = horizontalPositions.test( pos[0] ) ?
				pos.concat( [center] ) :
				verticalPositions.test( pos[0] ) ?
					[ center ].concat( pos ) :
					[ center, center ];
		}
		pos[ 0 ] = horizontalPositions.test( pos[0] ) ? pos[ 0 ] : center;
		pos[ 1 ] = verticalPositions.test( pos[1] ) ? pos[ 1 ] : center;
		options[ this ] = pos;
	});

	// normalize collision option
	if ( collision.length === 1 ) {
		collision[ 1 ] = collision[ 0 ];
	}

	// normalize offset option
	offset[ 0 ] = parseInt( offset[0], 10 ) || 0;
	if ( offset.length === 1 ) {
		offset[ 1 ] = offset[ 0 ];
	}
	offset[ 1 ] = parseInt( offset[1], 10 ) || 0;

	if ( options.at[0] === "right" ) {
		basePosition.left += targetWidth;
	} else if ( options.at[0] === center ) {
		basePosition.left += targetWidth / 2;
	}

	if ( options.at[1] === "bottom" ) {
		basePosition.top += targetHeight;
	} else if ( options.at[1] === center ) {
		basePosition.top += targetHeight / 2;
	}

	basePosition.left += offset[ 0 ];
	basePosition.top += offset[ 1 ];

	return this.each(function() {
		var elem = $( this ),
			elemWidth = elem.outerWidth(),
			elemHeight = elem.outerHeight(),
			marginLeft = parseInt( $.curCSS( this, "marginLeft", true ) ) || 0,
			marginTop = parseInt( $.curCSS( this, "marginTop", true ) ) || 0,
			collisionWidth = elemWidth + marginLeft +
				( parseInt( $.curCSS( this, "marginRight", true ) ) || 0 ),
			collisionHeight = elemHeight + marginTop +
				( parseInt( $.curCSS( this, "marginBottom", true ) ) || 0 ),
			position = $.extend( {}, basePosition ),
			collisionPosition;

		if ( options.my[0] === "right" ) {
			position.left -= elemWidth;
		} else if ( options.my[0] === center ) {
			position.left -= elemWidth / 2;
		}

		if ( options.my[1] === "bottom" ) {
			position.top -= elemHeight;
		} else if ( options.my[1] === center ) {
			position.top -= elemHeight / 2;
		}

		// prevent fractions (see #5280)
		position.left = Math.round( position.left );
		position.top = Math.round( position.top );

		collisionPosition = {
			left: position.left - marginLeft,
			top: position.top - marginTop
		};

		$.each( [ "left", "top" ], function( i, dir ) {
			if ( $.om.omPosition[ collision[i] ] ) {
				$.om.omPosition[ collision[i] ][ dir ]( position, {
					targetWidth: targetWidth,
					targetHeight: targetHeight,
					elemWidth: elemWidth,
					elemHeight: elemHeight,
					collisionPosition: collisionPosition,
					collisionWidth: collisionWidth,
					collisionHeight: collisionHeight,
					offset: offset,
					my: options.my,
					at: options.at
				});
			}
		});

		if ( $.fn.bgiframe ) {
			elem.bgiframe();
		}
		elem.offset( $.extend( position, { using: options.using } ) );
	});
};

$.om.omPosition = {
	fit: {
		left: function( position, data ) {
			var win = $( window ),
				over = data.collisionPosition.left + data.collisionWidth - win.width() - win.scrollLeft();
			position.left = over > 0 ? position.left - over : Math.max( position.left - data.collisionPosition.left, position.left );
		},
		top: function( position, data ) {
			var win = $( window ),
				over = data.collisionPosition.top + data.collisionHeight - win.height() - win.scrollTop();
			position.top = over > 0 ? position.top - over : Math.max( position.top - data.collisionPosition.top, position.top );
		}
	},

	flip: {
		left: function( position, data ) {
			if ( data.at[0] === center ) {
				return;
			}
			var win = $( window ),
				over = data.collisionPosition.left + data.collisionWidth - win.width() - win.scrollLeft(),
				myOffset = data.my[ 0 ] === "left" ?
					-data.elemWidth :
					data.my[ 0 ] === "right" ?
						data.elemWidth :
						0,
				atOffset = data.at[ 0 ] === "left" ?
					data.targetWidth :
					-data.targetWidth,
				offset = -2 * data.offset[ 0 ];
			position.left += data.collisionPosition.left < 0 ?
				myOffset + atOffset + offset :
				over > 0 ?
					myOffset + atOffset + offset :
					0;
		},
		top: function( position, data ) {
			if ( data.at[1] === center ) {
				return;
			}
			var win = $( window ),
				over = data.collisionPosition.top + data.collisionHeight - win.height() - win.scrollTop(),
				myOffset = data.my[ 1 ] === "top" ?
					-data.elemHeight :
					data.my[ 1 ] === "bottom" ?
						data.elemHeight :
						0,
				atOffset = data.at[ 1 ] === "top" ?
					data.targetHeight :
					-data.targetHeight,
				offset = -2 * data.offset[ 1 ];
			position.top += data.collisionPosition.top < 0 ?
				myOffset + atOffset + offset :
				over > 0 ?
					myOffset + atOffset + offset :
					0;
		}
	}
};

// offset setter from jQuery 1.4
if ( !$.offset.setOffset ) {
	$.offset.setOffset = function( elem, options ) {
		// set position first, in-case top/left are set even on static elem
		if ( /static/.test( $.curCSS( elem, "position" ) ) ) {
			elem.style.position = "relative";
		}
		var curElem   = $( elem ),
			curOffset = curElem.offset(),
			curTop    = parseInt( $.curCSS( elem, "top",  true ), 10 ) || 0,
			curLeft   = parseInt( $.curCSS( elem, "left", true ), 10)  || 0,
			props     = {
				top:  (options.top  - curOffset.top)  + curTop,
				left: (options.left - curOffset.left) + curLeft
			};
		
		if ( 'using' in options ) {
			options.using.call( elem, props );
		} else {
			curElem.css( props );
		}
	};

	$.fn.offset = function( options ) {
		var elem = this[ 0 ];
		if ( !elem || !elem.ownerDocument ) { return null; }
		if ( options ) { 
			return this.each(function() {
				$.offset.setOffset( this, options );
			});
		}
		return _offset.call( this );
	};
}

}( jQuery ));
/*
 * $Id: om-draggable.js,v 1.17 2012/03/29 06:03:09 chentianzhen Exp $
 * operamasks-ui omTree @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 *
 * Depends:
 *	om-core.js
 *	om-mouse.js
 */
/** 
     * @name omDraggable
     * @class 用来提供拖动功能.<br/>
     * <b>特点：</b><br/>
     * <ol>
     * 		<li>轻量级，简单易用。</li>
     * 		<li>可限制拖动的范围及方向。</li>
     * 		<li>可自定义鼠标在拖动时的样式。</li>
     * </ol>
     * <b>示例：</b><br/>
     * <pre>
     * &lt;script type="text/javascript" &gt;
     * $(document).ready(function() {
     *     $('#selector').omDraggable();
     * });
     * &lt;/script&gt;
     * 
     * &lt;div id="selector"&gt;
	 * &lt;/div&gt;
	 * </pre>
     * @constructor
     * @description 构造函数. 
     * @param p 标准config对象：{}
     */
;(function( $, undefined ) {

$.omWidget("om.omDraggable", $.om.omMouse, {
	widgetEventPrefix: "drag",
	options: {
		/**
         * 指定拖动的方向,提供的取值有“x”，“y”，默认不指定方向，可以任意拖动。
         * @name omDraggable#axis
         * @type String
         * @default 无
         * @example
         * //只能沿着x轴的方向拖动
         * $("#selector").omDraggable({axis:"x"});
         */
		axis: false,
		/**
         * 设置拖动的范围,不能拖动到该范围以外的地方，默认不指定拖动的范围，可以任意拖动。
         * 其值可以是：“parent”、“document”、“window”、[x1,y1,x2,y2]等。
         * @name omDraggable#containment
         * @type Selector,Element,String,Array
         * @default 无
         * @example
         * //只能在上一级父元素范围内拖动
         * $("#selector").omDraggable({containment:"parent"});
         */
		containment: false,
		/**
         * 设置鼠标拖动时的样式。其值为CSS中cursor属性的取值。
         * @name omDraggable#cursor
         * @type String
         * @default “auto”
         * @example
         * //拖动元素时，鼠标呈现十字状
         * $("#selector").omDraggable({cursor:"crosshair"});
         */
		cursor: "auto",
		/**
         * 不能启动拖动操作的区域。
         * @name omDraggable#cancel
         * @type Selector
         * @default :input,option
         * @example
         * //$("#selector")的子元素的&lt;p /&gt不能能够启动拖动操作
         * $("#selector").omDraggable({cancel:"p"});
         */
		_scope:"default",
		/**
         * 能够启动拖动操作的区域，默认不指定区域，拖动元素内的所有区域都可以启动拖动操作。
         * @name omDraggable#handle
         * @type Selector
         * @default 无
         * @example
         * //只有$("#selector")的子元素的&lt;p /&gt内才能够启动拖动操作
         * $("#selector").omDraggable({handle:"p"});
         */
		handle: false,
		/**
         * 提供一个辅助的元素作为元素被拖动时的展现，默认为被拖动元素本身作为拖动时的展现元素。
         * @name omDraggable#helper
         * @type String,function
         * @default "original"
         * @example
         * //$("#selector")的clone元素将作为辅助元素
         * $("#selector").omDraggable({helper:"clone"});
         */
		helper: "original",
		/**
         * 设置当拖动结束后，元素是否会返回到初始位置。默认为false，不会返回；反之为true时，返回到初始位置。
         * String类型的取值有：“valid”，“invalid”。如果该值为“invalid”，则当元素没有拖动到目的位置时返回；反之为“valid”
         * 时，当元素拖动到目的位置返回。
         * @name omDraggable#revert
         * @type Boolean,String
         * @default false
         * @example
         * //元素没有拖动到目的位置时，返回到原位。
         * $("#selector").omDraggable({revert:"invalid"});
         */
		revert: false,
		/**
		 * 是否可拖动。
		 * @name omDraggable#disabled
         * @type Boolean
         * @default false
         * @example
         * //元素不可拖动
         * $("#selector").omDraggable({disabled:true});
         */

		/**
		 * 拖动元素时，是否自动滚屏。
		 * @name omDraggable#scroll
         * @type Boolean
         * @default true
         * @example
         * //拖动元素时，不自动滚屏
         * $("#selector").omDraggable({scroll:false});
         */
		scroll: true
		
		/**
         * 开始拖动时触发事件。
         * @event
         * @param ui Objec对象。包括四个属性：helper，position(当前位置)，originalPosition(原始位置)，offset(偏移量)
         * @param event jQuery.Event对象。
         * @name omDraggable#onStart
         * @type Function
         * @example
         *   $("#selector").omDraggable({onStart : function(ui, event) {doSomething...}});
         */
		
		/**
         * 拖动时触发事件，当返回为false时，将取消拖动操作。
         * @event
         * @param ui Objec对象。包括四个属性：helper，position(当前位置)，originalPosition(原始位置)，offset(偏移量)
         * @param event jQuery.Event对象。
         * @name omDraggable#onDrag
         * @type Function
         * @example
         *   $("#selector").omDraggable({onDrag : function(ui, event) {doSomething...}});
         */
		
		/**
         * 停止拖动时触发事件。
         * @event
         * @param ui Objec对象。包括四个属性：helper，position(当前位置)，originalPosition(原始位置)，offset(偏移量)
         * @param event jQuery.Event对象。
         * @name omDraggable#onStop
         * @type Function
         * @example
         *   $("#selector").omDraggable({onStop : function(ui, event) {doSomething...}});
         */
	},
	_create: function() {

		if (this.options.helper == 'original' && !(/^(?:r|a|f)/).test(this.element.css("position")))
			this.element[0].style.position = 'relative';

		this.element.addClass("om-draggable");
		(this.options.disabled && this.element.addClass("om-draggable-disabled"));

		this._mouseInit();

	},

	/**
     * 删除元素的拖动功能.
     * @name omDraggable#destroy
     * @function
     * @returns JQuery对象
     * @example
     * var $selector = $("#selector").omDraggable('destroy');
     * 
     */
	destroy: function() {
		if(!this.element.data('omDraggable')) return;
		this.element
			.removeData("omDraggable")
			.unbind(".draggable")
			.removeClass("om-draggable"
				+ " om-draggable-dragging"
				+ " om-draggable-disabled");
		this._mouseDestroy();

		return this;
	},

	_mouseCapture: function(event) {

		var o = this.options;

		// among others, prevent a drag on a resizable-handle
		if (this.helper || o.disabled || $(event.target).is('.om-resizable-handle'))
			return false;

		//Quit if we're not on a valid handle
		this.handle = this._getHandle(event);
		if (!this.handle)
			return false;
		return true;

	},

	_mouseStart: function(event) {

		var o = this.options;

		//Create and append the visible helper
		this.helper = this._createHelper(event);

		//Cache the helper size
		this._cacheHelperProportions();

		//If ddmanager is used for droppables, set the global draggable
		if($.om.ddmanager)
			$.om.ddmanager.current = this;

		/*
		 * - Position generation -
		 * This block generates everything position related - it's the core of draggables.
		 */

		//Cache the margins of the original element
		this._cacheMargins();

		//Store the helper's css position
		this.cssPosition = this.helper.css("position");
		this.scrollParent = this.helper.scrollParent();

		//The element's absolute position on the page minus margins
		this.offset = this.positionAbs = this.element.offset();
		this.offset = {
			top: this.offset.top - this.margins.top,
			left: this.offset.left - this.margins.left
		};

		$.extend(this.offset, {
			click: { //Where the click happened, relative to the element
				left: event.pageX - this.offset.left,
				top: event.pageY - this.offset.top
			},
			parent: this._getParentOffset(),
			relative: this._getRelativeOffset() //This is a relative to absolute position minus the actual position calculation - only used for relative positioned helper
		});

		//Generate the original position
		this.originalPosition = this.position = this._generatePosition(event);
		this.originalPageX = event.pageX;
		this.originalPageY = event.pageY;

		//Set a containment if given in the options
		if(o.containment)
			this._setContainment();

		//Trigger event + callbacks
		if(this._trigger("onStart", event) === false) {
			this._clear();
			return false;
		}

		//Recache the helper size
		this._cacheHelperProportions();

		//Prepare the droppable offsets
		if ($.om.ddmanager && !o.dropBehaviour)
			$.om.ddmanager.prepareOffsets(this, event);

		this.helper.addClass("om-draggable-dragging");
		this._mouseDrag(event, true); //Execute the drag once - this causes the helper not to be visible before getting its correct position
		
		//If the ddmanager is used for droppables, inform the manager that dragging has started (see #5003)
		if ( $.om.ddmanager ) $.om.ddmanager.dragStart(this, event);
		
		return true;
	},

	_mouseDrag: function(event, noPropagation) {

		//Compute the helpers position
		this.position = this._generatePosition(event);
		this.positionAbs = this._convertPositionTo("absolute");

		//Call plugins and callbacks and use the resulting position if something is returned
		if (!noPropagation) {
			var ui = this._uiHash();
			if(this._trigger('onDrag', event, ui) === false) {
				this._mouseUp({});
				return false;
			}
			this.position = ui.position;
		}

		if(!this.options.axis || this.options.axis != "y") this.helper[0].style.left = this.position.left+'px';
		if(!this.options.axis || this.options.axis != "x") this.helper[0].style.top = this.position.top+'px';
		if($.om.ddmanager) $.om.ddmanager.drag(this, event);

		return false;
	},

	_mouseStop: function(event) {

		//If we are using droppables, inform the manager about the drop
		var dropped = false;
		if ($.om.ddmanager && !this.options.dropBehaviour)
			dropped = $.om.ddmanager.drop(this, event);

		//if a drop comes from outside (a sortable)
		if(this.dropped) {
			dropped = this.dropped;
			this.dropped = false;
		}
		
		//if the original element is removed, don't bother to continue if helper is set to "original"
		if((!this.element[0] || !this.element[0].parentNode) && this.options.helper == "original")
			return false;

		if((this.options.revert == "invalid" && !dropped) || (this.options.revert == "valid" && dropped) || this.options.revert === true || ($.isFunction(this.options.revert) && this.options.revert.call(this.element, dropped))) {
			var self = this;
			$(this.helper).animate(this.originalPosition, 500, function() {
				if(self._trigger("onStop", event) !== false) {
					self._clear();
				}
			});
		} else {
			if(this._trigger("onStop", event) !== false) {
				this._clear();
			}
		}

		return false;
	},
	
	_mouseUp: function(event) {
		//If the ddmanager is used for droppables, inform the manager that dragging has stopped (see #5003)
		if( $.om.ddmanager ) $.om.ddmanager.dragStop(this, event);
		
		return $.om.omMouse.prototype._mouseUp.call(this, event);
	},
	
	cancel: function() {
		
		if(this.helper.is(".om-draggable-dragging")) {
			this._mouseUp({});
		} else {
			this._clear();
		}
		
		return this;
		
	},

	_getHandle: function(event) {

		var handle = !this.options.handle || !$(this.options.handle, this.element).length ? true : false;
		$(this.options.handle, this.element)
			.find("*")
			.andSelf()
			.each(function() {
				if(this == event.target) handle = true;
			});

		return handle;

	},

	_createHelper: function(event) {

		var o = this.options;
		var helper = $.isFunction(o.helper) ? $(o.helper.apply(this.element[0], [event])) : (o.helper == 'clone' ? this.element.clone().removeAttr('id') : this.element);

		if(!helper.parents('body').length)
			helper.appendTo( this.element[0].parentNode);

		if(helper[0] != this.element[0] && !(/(fixed|absolute)/).test(helper.css("position")))
			helper.css("position", "absolute");

		return helper;

	},

	

	_getParentOffset: function() {

		//Get the offsetParent and cache its position
		this.offsetParent = this.helper.offsetParent();
		var po = this.offsetParent.offset();

		// This is a special case where we need to modify a offset calculated on start, since the following happened:
		// 1. The position of the helper is absolute, so it's position is calculated based on the next positioned parent
		// 2. The actual offset parent is a child of the scroll parent, and the scroll parent isn't the document, which means that
		//    the scroll is included in the initial calculation of the offset of the parent, and never recalculated upon drag
		if(this.cssPosition == 'absolute' && this.scrollParent[0] != document && $.contains(this.scrollParent[0], this.offsetParent[0])) {
			po.left += this.scrollParent.scrollLeft();
			po.top += this.scrollParent.scrollTop();
		}

		if((this.offsetParent[0] == document.body) //This needs to be actually done for all browsers, since pageX/pageY includes this information
		|| (this.offsetParent[0].tagName && this.offsetParent[0].tagName.toLowerCase() == 'html' && $.browser.msie)) //Ugly IE fix
			po = { top: 0, left: 0 };

		return {
			top: po.top + (parseInt(this.offsetParent.css("borderTopWidth"),10) || 0),
			left: po.left + (parseInt(this.offsetParent.css("borderLeftWidth"),10) || 0)
		};

	},

	_getRelativeOffset: function() {

		if(this.cssPosition == "relative") {
			var p = this.element.position();
			return {
				top: p.top - (parseInt(this.helper.css("top"),10) || 0) + this.scrollParent.scrollTop(),
				left: p.left - (parseInt(this.helper.css("left"),10) || 0) + this.scrollParent.scrollLeft()
			};
		} else {
			return { top: 0, left: 0 };
		}

	},

	_cacheMargins: function() {
		this.margins = {
			left: (parseInt(this.element.css("marginLeft"),10) || 0),
			top: (parseInt(this.element.css("marginTop"),10) || 0),
			right: (parseInt(this.element.css("marginRight"),10) || 0),
			bottom: (parseInt(this.element.css("marginBottom"),10) || 0)
		};
	},

	_cacheHelperProportions: function() {
		this.helperProportions = {
			width: this.helper.outerWidth(),
			height: this.helper.outerHeight()
		};
	},

	_setContainment: function() {

		var o = this.options;
		if(o.containment == 'parent') o.containment = this.helper[0].parentNode;
		if(o.containment == 'document' || o.containment == 'window') this.containment = [
			o.containment == 'document' ? 0 : $(window).scrollLeft() - this.offset.relative.left - this.offset.parent.left,
			o.containment == 'document' ? 0 : $(window).scrollTop() - this.offset.relative.top - this.offset.parent.top,
			(o.containment == 'document' ? 0 : $(window).scrollLeft()) + $(o.containment == 'document' ? document : window).width() - this.helperProportions.width - this.margins.left,
			(o.containment == 'document' ? 0 : $(window).scrollTop()) + ($(o.containment == 'document' ? document : window).height() || document.body.parentNode.scrollHeight) - this.helperProportions.height - this.margins.top
		];

		if(!(/^(document|window|parent)$/).test(o.containment) && o.containment.constructor != Array) {
		        var c = $(o.containment);
			var ce = c[0]; if(!ce) return;
			var co = c.offset();
			var over = ($(ce).css("overflow") != 'hidden');

			this.containment = [
				(parseInt($(ce).css("borderLeftWidth"),10) || 0) + (parseInt($(ce).css("paddingLeft"),10) || 0),
				(parseInt($(ce).css("borderTopWidth"),10) || 0) + (parseInt($(ce).css("paddingTop"),10) || 0),
				(over ? Math.max(ce.scrollWidth,ce.offsetWidth) : ce.offsetWidth) - (parseInt($(ce).css("borderLeftWidth"),10) || 0) - (parseInt($(ce).css("paddingRight"),10) || 0) - this.helperProportions.width - this.margins.left - this.margins.right,
				(over ? Math.max(ce.scrollHeight,ce.offsetHeight) : ce.offsetHeight) - (parseInt($(ce).css("borderTopWidth"),10) || 0) - (parseInt($(ce).css("paddingBottom"),10) || 0) - this.helperProportions.height - this.margins.top  - this.margins.bottom
			];
			this.relative_container = c;

		} else if(o.containment.constructor == Array) {
			this.containment = o.containment;
		}

	},

	_convertPositionTo: function(d, pos) {

		if(!pos) pos = this.position;
		var mod = d == "absolute" ? 1 : -1;
		var o = this.options, scroll = this.cssPosition == 'absolute' && !(this.scrollParent[0] != document && $.contains(this.scrollParent[0], this.offsetParent[0])) ? this.offsetParent : this.scrollParent, scrollIsRootNode = (/(html|body)/i).test(scroll[0].tagName);

		return {
			top: (
				pos.top																	// The absolute mouse position
				+ this.offset.relative.top * mod										// Only for relative positioned nodes: Relative offset from element to offset parent
				+ this.offset.parent.top * mod											// The offsetParent's offset without borders (offset + border)
				- ($.browser.safari && $.browser.version < 526 && this.cssPosition == 'fixed' ? 0 : ( this.cssPosition == 'fixed' ? -this.scrollParent.scrollTop() : ( scrollIsRootNode ? 0 : scroll.scrollTop() ) ) * mod)
			),
			left: (
				pos.left																// The absolute mouse position
				+ this.offset.relative.left * mod										// Only for relative positioned nodes: Relative offset from element to offset parent
				+ this.offset.parent.left * mod											// The offsetParent's offset without borders (offset + border)
				- ($.browser.safari && $.browser.version < 526 && this.cssPosition == 'fixed' ? 0 : ( this.cssPosition == 'fixed' ? -this.scrollParent.scrollLeft() : scrollIsRootNode ? 0 : scroll.scrollLeft() ) * mod)
			)
		};

	},

	_generatePosition: function(event) {

		var o = this.options, scroll = this.cssPosition == 'absolute' && !(this.scrollParent[0] != document && $.contains(this.scrollParent[0], this.offsetParent[0])) ? this.offsetParent : this.scrollParent, scrollIsRootNode = (/(html|body)/i).test(scroll[0].tagName);
		var pageX = event.pageX;
		var pageY = event.pageY;

		/*
		 * - Position constraining -
		 * Constrain the position to a mix of grid, containment.
		 */

		if(this.originalPosition) { //If we are not dragging yet, we won't check for options
		         var containment;
		         if(this.containment) {
				 if (this.relative_container){
				     var co = this.relative_container.offset();
				     containment = [ this.containment[0] + co.left,
						     this.containment[1] + co.top,
						     this.containment[2] + co.left,
						     this.containment[3] + co.top ];
				 }
				 else {
				     containment = this.containment;
				 }

				if(event.pageX - this.offset.click.left < containment[0]) pageX = containment[0] + this.offset.click.left;
				if(event.pageY - this.offset.click.top < containment[1]) pageY = containment[1] + this.offset.click.top;
				if(event.pageX - this.offset.click.left > containment[2]) pageX = containment[2] + this.offset.click.left;
				if(event.pageY - this.offset.click.top > containment[3]) pageY = containment[3] + this.offset.click.top;
			}

		}

		return {
			top: (
				pageY																// The absolute mouse position
				- this.offset.click.top													// Click offset (relative to the element)
				- this.offset.relative.top												// Only for relative positioned nodes: Relative offset from element to offset parent
				- this.offset.parent.top												// The offsetParent's offset without borders (offset + border)
				+ ($.browser.safari && $.browser.version < 526 && this.cssPosition == 'fixed' ? 0 : ( this.cssPosition == 'fixed' ? -this.scrollParent.scrollTop() : ( scrollIsRootNode ? 0 : scroll.scrollTop() ) ))
			),
			left: (
				pageX																// The absolute mouse position
				- this.offset.click.left												// Click offset (relative to the element)
				- this.offset.relative.left												// Only for relative positioned nodes: Relative offset from element to offset parent
				- this.offset.parent.left												// The offsetParent's offset without borders (offset + border)
				+ ($.browser.safari && $.browser.version < 526 && this.cssPosition == 'fixed' ? 0 : ( this.cssPosition == 'fixed' ? -this.scrollParent.scrollLeft() : scrollIsRootNode ? 0 : scroll.scrollLeft() ))
			)
		};

	},

	_clear: function() {
		this.helper.removeClass("om-draggable-dragging");
		if(this.helper[0] != this.element[0] && !this.cancelHelperRemoval) this.helper.remove();
		//if($.om.ddmanager) $.om.ddmanager.current = null;
		this.helper = null;
		this.cancelHelperRemoval = false;
	},

	// From now on bulk stuff - mainly helpers

	_trigger: function(type, event, ui) {
		ui = ui || this._uiHash();
		$.om.plugin.call(this, type, [event, ui]);
		if(type == "onDrag") this.positionAbs = this._convertPositionTo("absolute"); //The absolute position has to be recalculated after plugins
		return $.OMWidget.prototype._trigger.call(this, type, event, ui);
	},

	plugins: {},

	_uiHash: function(event) {
		return {
			helper: this.helper,
			position: this.position,
			originalPosition: this.originalPosition,
			offset: this.positionAbs
		};
	}

});

$.om.plugin.add("omDraggable", "cursor", {
	onStart: function(ui, event) {
		var t = $('body'), o = $(this).data('omDraggable').options;
		if (t.css("cursor")) o._cursor = t.css("cursor");
		t.css("cursor", o.cursor);
	},
	onStop: function(ui, event) {
	    var drag = $(this).data('omDraggable');
	    if(drag){
	        var o = drag.options;
	        if (o._cursor) $('body').css("cursor", o._cursor);
	    }
	}
});

$.om.plugin.add("omDraggable", "scroll", {
	onStart: function(ui, event) {
		var i = $(this).data("omDraggable");
		if(i.scrollParent[0] != document && i.scrollParent[0].tagName != 'HTML') i.overflowOffset = i.scrollParent.offset();
	},
	onDrag: function(ui, event) {
		
		var i = $(this).data("omDraggable"), o = i.options, scrolled = false, scrollSensitivity = 20, scrollSpeed = 20;

		if(i.scrollParent[0] != document && i.scrollParent[0].tagName != 'HTML') {

			if(!o.axis || o.axis != 'x') {
				if((i.overflowOffset.top + i.scrollParent[0].offsetHeight) - event.pageY < scrollSensitivity)
					i.scrollParent[0].scrollTop = scrolled = i.scrollParent[0].scrollTop + scrollSpeed;
				else if(event.pageY - i.overflowOffset.top < scrollSensitivity)
					i.scrollParent[0].scrollTop = scrolled = i.scrollParent[0].scrollTop - scrollSpeed;
			}

			if(!o.axis || o.axis != 'y') {
				if((i.overflowOffset.left + i.scrollParent[0].offsetWidth) - event.pageX < scrollSensitivity)
					i.scrollParent[0].scrollLeft = scrolled = i.scrollParent[0].scrollLeft + scrollSpeed;
				else if(event.pageX - i.overflowOffset.left < scrollSensitivity)
					i.scrollParent[0].scrollLeft = scrolled = i.scrollParent[0].scrollLeft - scrollSpeed;
			}

		} else {

			if(!o.axis || o.axis != 'x') {
				if(event.pageY - $(document).scrollTop() < scrollSensitivity)
					scrolled = $(document).scrollTop($(document).scrollTop() - scrollSpeed);
				else if($(window).height() - (event.pageY - $(document).scrollTop()) < scrollSensitivity)
					scrolled = $(document).scrollTop($(document).scrollTop() + scrollSpeed);
			}

			if(!o.axis || o.axis != 'y') {
				if(event.pageX - $(document).scrollLeft() < scrollSensitivity)
					scrolled = $(document).scrollLeft($(document).scrollLeft() - scrollSpeed);
				else if($(window).width() - (event.pageX - $(document).scrollLeft()) < scrollSensitivity)
					scrolled = $(document).scrollLeft($(document).scrollLeft() + scrollSpeed);
			}

		}

		if(scrolled !== false && $.om.ddmanager && !o.dropBehaviour)
			$.om.ddmanager.prepareOffsets(i, event);

	}
});

})(jQuery);
/*
 * $Id: om-resizable.js,v 1.7 2012/06/11 01:17:03 linxiaomin Exp $
 * operamasks-ui omResizable @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 * 
 * Depends:
 *  om-core.js
 *  om-mouse.js
 */
(function( $, undefined ) {

$.omWidget("om.omResizable", $.om.omMouse, {
	widgetEventPrefix: "resize",    
	options: {
		alsoResize: false,
		aspectRatio: false,
		autoHide: false,
		containment: false,
		handles: "e,s,se",
		helper: false,
		maxHeight: null,
		maxWidth: null,
		minHeight: 10,
		minWidth: 10,
		zIndex: 1000
	},
	_create: function() {

		var self = this, o = this.options;
		this.element.addClass("om-resizable");

		$.extend(this, {
			_aspectRatio: !!(o.aspectRatio),
			aspectRatio: o.aspectRatio,
			originalElement: this.element,
			_proportionallyResizeElements: [],
			_helper: o.helper || o.ghost || o.animate ? o.helper || 'om-resizable-helper' : null
		});

		//Wrap the element if it cannot hold child nodes
		if(this.element[0].nodeName.match(/canvas|textarea|input|select|button|img/i)) {

			//Opera fix for relative positioning
			if (/relative/.test(this.element.css('position')) && $.browser.opera)
				this.element.css({ position: 'relative', top: 'auto', left: 'auto' });

			//Create a wrapper element and set the wrapper to the new current internal element
			this.element.wrap(
				$('<div class="om-wrapper" style="overflow: hidden;"></div>').css({
					position: this.element.css('position'),
					width: this.element.outerWidth(),
					height: this.element.outerHeight(),
					top: this.element.css('top'),
					left: this.element.css('left')
				})
			);

			//Overwrite the original this.element
			this.element = this.element.parent().data(
				"resizable", this.element.data('resizable')
			);

			this.elementIsWrapper = true;

			//Move margins to the wrapper
			this.element.css({ marginLeft: this.originalElement.css("marginLeft"), marginTop: this.originalElement.css("marginTop"), marginRight: this.originalElement.css("marginRight"), marginBottom: this.originalElement.css("marginBottom") });
			this.originalElement.css({ marginLeft: 0, marginTop: 0, marginRight: 0, marginBottom: 0});

			//Prevent Safari textarea resize
			this.originalResizeStyle = this.originalElement.css('resize');
			this.originalElement.css('resize', 'none');

			//Push the actual element to our proportionallyResize internal array
			this._proportionallyResizeElements.push(this.originalElement.css({ position: 'static', zoom: 1, display: 'block' }));

			// avoid IE jump (hard set the margin)
			this.originalElement.css({ margin: this.originalElement.css('margin') });

			// fix handlers offset
			this._proportionallyResize();

		}

		this.handles = o.handles || (!$('.om-resizable-handle', this.element).length ? "e,s,se" : { n: '.om-resizable-n', e: '.om-resizable-e', s: '.om-resizable-s', w: '.om-resizable-w', se: '.om-resizable-se', sw: '.om-resizable-sw', ne: '.om-resizable-ne', nw: '.om-resizable-nw' });
		if(this.handles.constructor == String) {

			if(this.handles == 'all') this.handles = 'n,e,s,w,se,sw,ne,nw';
			var n = this.handles.split(","); this.handles = {};

			for(var i = 0; i < n.length; i++) {

				var handle = $.trim(n[i]), hname = 'om-resizable-'+handle;
				var axis = $('<div class="om-resizable-handle ' + hname + '"></div>');

				// increase zIndex of sw, se, ne, nw axis
				//TODO : this modifies original option
				if(/sw|se|ne|nw/.test(handle)) axis.css({ zIndex: ++o.zIndex });

				//TODO : What's going on here?
				if ('se' == handle) {
					axis.addClass('om-icon om-icon-gripsmall-diagonal-se');
				};

				//Insert into internal handles object and append to element
				this.handles[handle] = '.om-resizable-'+handle;
				this.element.append(axis);
			}

		}

		this._renderAxis = function(target) {

			target = target || this.element;

			for(var i in this.handles) {

				if(this.handles[i].constructor == String)
					this.handles[i] = $(this.handles[i], this.element).show();

				//Apply pad to wrapper element, needed to fix axis position (textarea, inputs, scrolls)
				if (this.elementIsWrapper && this.originalElement[0].nodeName.match(/textarea|input|select|button/i)) {

					var axis = $(this.handles[i], this.element), padWrapper = 0;

					//Checking the correct pad and border
					padWrapper = /sw|ne|nw|se|n|s/.test(i) ? axis.outerHeight() : axis.outerWidth();

					//The padding type i have to apply...
					var padPos = [ 'padding',
						/ne|nw|n/.test(i) ? 'Top' :
						/se|sw|s/.test(i) ? 'Bottom' :
						/^e$/.test(i) ? 'Right' : 'Left' ].join("");

					target.css(padPos, padWrapper);

					this._proportionallyResize();

				}

				//TODO: What's that good for? There's not anything to be executed left
				if(!$(this.handles[i]).length)
					continue;

			}
		};

		//TODO: make renderAxis a prototype function
		this._renderAxis(this.element);

		this._handles = $('.om-resizable-handle', this.element)
			.disableSelection();

		//Matching axis name
		this._handles.mouseover(function() {
			if (!self.resizing) {
				if (this.className)
					var axis = this.className.match(/om-resizable-(se|sw|ne|nw|n|e|s|w)/i);
				//Axis, default = se
				self.axis = axis && axis[1] ? axis[1] : 'se';
			}
		});

		//If we want to auto hide the elements
		if (o.autoHide) {
			this._handles.hide();
			$(this.element)
				.addClass("om-resizable-autohide")
				.hover(function() {
					if (o.disabled) return;
					$(this).removeClass("om-resizable-autohide");
					self._handles.show();
				},
				function(){
					if (o.disabled) return;
					if (!self.resizing) {
						$(this).addClass("om-resizable-autohide");
						self._handles.hide();
					}
				});
		}

		//Initialize the mouse interaction
		this._mouseInit();

	},

	destroy: function() {

		this._mouseDestroy();

		var _destroy = function(exp) {
			$(exp).removeClass("om-resizable om-resizable-disabled om-resizable-resizing")
				.removeData("resizable").unbind(".resizable").find('.om-resizable-handle').remove();
		};

		//TODO: Unwrap at same DOM position
		if (this.elementIsWrapper) {
			_destroy(this.element);
			var wrapper = this.element;
			wrapper.after(
				this.originalElement.css({
					position: wrapper.css('position'),
					width: wrapper.outerWidth(),
					height: wrapper.outerHeight(),
					top: wrapper.css('top'),
					left: wrapper.css('left')
				})
			).remove();
		}

		this.originalElement.css('resize', this.originalResizeStyle);
		_destroy(this.originalElement);

		return this;
	},

	_mouseCapture: function(event) {
		var handle = false;
		for (var i in this.handles) {
			if ($(this.handles[i])[0] == event.target) {
				handle = true;
			}
		}

		return !this.options.disabled && handle;
	},

	_mouseStart: function(event) {

		var o = this.options, iniPos = this.element.position(), el = this.element;

		this.resizing = true;
		this.documentScroll = { top: $(document).scrollTop(), left: $(document).scrollLeft() };

		// bugfix for http://dev.jquery.com/ticket/1749
		if (el.is('.ui-draggable') || (/absolute/).test(el.css('position'))) {
			el.css({ position: 'absolute', top: iniPos.top, left: iniPos.left });
		}

		//Opera fixing relative position
		if ($.browser.opera && (/relative/).test(el.css('position')))
			el.css({ position: 'relative', top: 'auto', left: 'auto' });

		this._renderProxy();

		var curleft = num(this.helper.css('left')), curtop = num(this.helper.css('top'));

		if (o.containment) {
			curleft += $(o.containment).scrollLeft() || 0;
			curtop += $(o.containment).scrollTop() || 0;
		}

		//Store needed variables
		this.offset = this.helper.offset();
		this.position = { left: curleft, top: curtop };
		this.size = this._helper ? { width: el.outerWidth(), height: el.outerHeight() } : { width: el.width(), height: el.height() };
		this.originalSize = this._helper ? { width: el.outerWidth(), height: el.outerHeight() } : { width: el.width(), height: el.height() };
		this.originalPosition = { left: curleft, top: curtop };
		this.sizeDiff = { width: el.outerWidth() - el.width(), height: el.outerHeight() - el.height() };
		this.originalMousePosition = { left: event.pageX, top: event.pageY };

		//Aspect Ratio
		this.aspectRatio = (typeof o.aspectRatio == 'number') ? o.aspectRatio : ((this.originalSize.width / this.originalSize.height) || 1);

	    var cursor = $('.om-resizable-' + this.axis).css('cursor');
	    $('body').css('cursor', cursor == 'auto' ? this.axis + '-resize' : cursor);

		el.addClass("om-resizable-resizing");
		this._propagate("start", event);
		return true;
	},

	_mouseDrag: function(event) {

		//Increase performance, avoid regex
		var el = this.helper, o = this.options, props = {},
			self = this, smp = this.originalMousePosition, a = this.axis;

		var dx = (event.pageX-smp.left)||0, dy = (event.pageY-smp.top)||0;
		var trigger = this._change[a];
		if (!trigger) return false;

		// Calculate the attrs that will be change
		var data = trigger.apply(this, [event, dx, dy]), ie6 = $.browser.msie && $.browser.version < 7, csdif = this.sizeDiff;

		// Put this in the mouseDrag handler since the user can start pressing shift while resizing
		this._updateVirtualBoundaries(event.shiftKey);
		if (this._aspectRatio || event.shiftKey)
			data = this._updateRatio(data, event);

		data = this._respectSize(data, event);

		// plugins callbacks need to be called first
		this._propagate("resize", event);

		el.css({
			top: this.position.top + "px", left: this.position.left + "px",
			width: this.size.width + "px", height: this.size.height + "px"
		});

		if (!this._helper && this._proportionallyResizeElements.length)
			this._proportionallyResize();

		this._updateCache(data);

		// calling the user callback at the end
		this._trigger('resize', event, this.ui());

		return false;
	},

	_mouseStop: function(event) {

		this.resizing = false;
		var o = this.options, self = this;

		if(this._helper) {
			var pr = this._proportionallyResizeElements, ista = pr.length && (/textarea/i).test(pr[0].nodeName),
				soffseth = ista && self._hasScroll(pr[0], 'left') /* TODO - jump height */ ? 0 : self.sizeDiff.height,
				soffsetw = ista ? 0 : self.sizeDiff.width;

			var s = { width: (self.helper.width()  - soffsetw), height: (self.helper.height() - soffseth) },
				left = (parseInt(self.element.css('left'), 10) + (self.position.left - self.originalPosition.left)) || null,
				top = (parseInt(self.element.css('top'), 10) + (self.position.top - self.originalPosition.top)) || null;

			if (!o.animate)
				this.element.css($.extend(s, { top: top, left: left }));

			self.helper.height(self.size.height);
			self.helper.width(self.size.width);

			if (this._helper && !o.animate) this._proportionallyResize();
		}

		$('body').css('cursor', 'auto');

		this.element.removeClass("om-resizable-resizing");

		this._propagate("stop", event);

		if (this._helper) this.helper.remove();
		return false;

	},

    _updateVirtualBoundaries: function(forceAspectRatio) {
        var o = this.options, pMinWidth, pMaxWidth, pMinHeight, pMaxHeight, b;

        b = {
            minWidth: isNumber(o.minWidth) ? o.minWidth : 0,
            maxWidth: isNumber(o.maxWidth) ? o.maxWidth : Infinity,
            minHeight: isNumber(o.minHeight) ? o.minHeight : 0,
            maxHeight: isNumber(o.maxHeight) ? o.maxHeight : Infinity
        };

        if(this._aspectRatio || forceAspectRatio) {
            // We want to create an enclosing box whose aspect ration is the requested one
            // First, compute the "projected" size for each dimension based on the aspect ratio and other dimension
            pMinWidth = b.minHeight * this.aspectRatio;
            pMinHeight = b.minWidth / this.aspectRatio;
            pMaxWidth = b.maxHeight * this.aspectRatio;
            pMaxHeight = b.maxWidth / this.aspectRatio;

            if(pMinWidth > b.minWidth) b.minWidth = pMinWidth;
            if(pMinHeight > b.minHeight) b.minHeight = pMinHeight;
            if(pMaxWidth < b.maxWidth) b.maxWidth = pMaxWidth;
            if(pMaxHeight < b.maxHeight) b.maxHeight = pMaxHeight;
        }
        this._vBoundaries = b;
    },

	_updateCache: function(data) {
		var o = this.options;
		this.offset = this.helper.offset();
		if (isNumber(data.left)) this.position.left = data.left;
		if (isNumber(data.top)) this.position.top = data.top;
		if (isNumber(data.height)) this.size.height = data.height;
		if (isNumber(data.width)) this.size.width = data.width;
	},

	_updateRatio: function(data, event) {

		var o = this.options, cpos = this.position, csize = this.size, a = this.axis;

		if (isNumber(data.height)) data.width = (data.height * this.aspectRatio);
		else if (isNumber(data.width)) data.height = (data.width / this.aspectRatio);

		if (a == 'sw') {
			data.left = cpos.left + (csize.width - data.width);
			data.top = null;
		}
		if (a == 'nw') {
			data.top = cpos.top + (csize.height - data.height);
			data.left = cpos.left + (csize.width - data.width);
		}

		return data;
	},

	_respectSize: function(data, event) {

		var el = this.helper, o = this._vBoundaries, pRatio = this._aspectRatio || event.shiftKey, a = this.axis,
				ismaxw = isNumber(data.width) && o.maxWidth && (o.maxWidth < data.width), ismaxh = isNumber(data.height) && o.maxHeight && (o.maxHeight < data.height),
					isminw = isNumber(data.width) && o.minWidth && (o.minWidth > data.width), isminh = isNumber(data.height) && o.minHeight && (o.minHeight > data.height);

		if (isminw) data.width = o.minWidth;
		if (isminh) data.height = o.minHeight;
		if (ismaxw) data.width = o.maxWidth;
		if (ismaxh) data.height = o.maxHeight;

		var dw = this.originalPosition.left + this.originalSize.width, dh = this.position.top + this.size.height;
		var cw = /sw|nw|w/.test(a), ch = /nw|ne|n/.test(a);

		if (isminw && cw) data.left = dw - o.minWidth;
		if (ismaxw && cw) data.left = dw - o.maxWidth;
		if (isminh && ch)	data.top = dh - o.minHeight;
		if (ismaxh && ch)	data.top = dh - o.maxHeight;

		// fixing jump error on top/left - bug #2330
		var isNotwh = !data.width && !data.height;
		if (isNotwh && !data.left && data.top) data.top = null;
		else if (isNotwh && !data.top && data.left) data.left = null;

		return data;
	},

	_proportionallyResize: function() {

		var o = this.options;
		if (!this._proportionallyResizeElements.length) return;
		var element = this.helper || this.element;

		for (var i=0; i < this._proportionallyResizeElements.length; i++) {

			var prel = this._proportionallyResizeElements[i];

			if (!this.borderDif) {
				var b = [prel.css('borderTopWidth'), prel.css('borderRightWidth'), prel.css('borderBottomWidth'), prel.css('borderLeftWidth')],
					p = [prel.css('paddingTop'), prel.css('paddingRight'), prel.css('paddingBottom'), prel.css('paddingLeft')];

				this.borderDif = $.map(b, function(v, i) {
					var border = parseInt(v,10)||0, padding = parseInt(p[i],10)||0;
					return border + padding;
				});
			}

			if ($.browser.msie && !(!($(element).is(':hidden') || $(element).parents(':hidden').length)))
				continue;

			prel.css({
				height: (element.height() - this.borderDif[0] - this.borderDif[2]) || 0,
				width: (element.width() - this.borderDif[1] - this.borderDif[3]) || 0
			});

		};

	},

	_renderProxy: function() {

		var el = this.element, o = this.options;
		this.elementOffset = el.offset();

		if(this._helper) {

			this.helper = this.helper || $('<div style="overflow:hidden;"></div>');

			// fix ie6 offset TODO: This seems broken
			var ie6 = $.browser.msie && $.browser.version < 7, ie6offset = (ie6 ? 1 : 0),
			pxyoffset = ( ie6 ? 2 : -1 );

			this.helper.addClass(this._helper).css({
				width: this.element.outerWidth() + pxyoffset,
				height: this.element.outerHeight() + pxyoffset,
				position: 'absolute',
				left: this.elementOffset.left - ie6offset +'px',
				top: this.elementOffset.top - ie6offset +'px',
				zIndex: ++o.zIndex //TODO: Don't modify option
			});

			this.helper
				.appendTo("body")
				.disableSelection();

		} else {
			this.helper = this.element;
		}

	},

	_change: {
		e: function(event, dx, dy) {
			return { width: this.originalSize.width + dx };
		},
		w: function(event, dx, dy) {
			var o = this.options, cs = this.originalSize, sp = this.originalPosition;
			return { left: sp.left + dx, width: cs.width - dx };
		},
		n: function(event, dx, dy) {
			var o = this.options, cs = this.originalSize, sp = this.originalPosition;
			return { top: sp.top + dy, height: cs.height - dy };
		},
		s: function(event, dx, dy) {
			return { height: this.originalSize.height + dy };
		},
		se: function(event, dx, dy) {
			return $.extend(this._change.s.apply(this, arguments), this._change.e.apply(this, [event, dx, dy]));
		},
		sw: function(event, dx, dy) {
			return $.extend(this._change.s.apply(this, arguments), this._change.w.apply(this, [event, dx, dy]));
		},
		ne: function(event, dx, dy) {
			return $.extend(this._change.n.apply(this, arguments), this._change.e.apply(this, [event, dx, dy]));
		},
		nw: function(event, dx, dy) {
			return $.extend(this._change.n.apply(this, arguments), this._change.w.apply(this, [event, dx, dy]));
		}
	},

	_propagate: function(n, event) {
		$.om.plugin.call(this, n, [event, this.ui()]);
		(n != "resize" && this._trigger(n, event, this.ui()));
	},

	// only used by resizable, remove from om-core.js
    _hasScroll: function( el, a ) {
        //If overflow is hidden, the element might have extra content, but the user wants to hide it
        if ( $( el ).css( "overflow" ) === "hidden") {
            return false;
        }
        var scroll = ( a && a === "left" ) ? "scrollLeft" : "scrollTop",
            has = false;
        if ( el[ scroll ] > 0 ) {
            return true;
        }
        // TODO: determine which cases actually cause this to happen
        // if the element doesn't have the scroll set, see if it's possible to
        // set the scroll
        el[ scroll ] = 1;
        has = ( el[ scroll ] > 0 );
        el[ scroll ] = 0;
        return has;
    },
	
	plugins: {},

	ui: function() {
		return {
			originalElement: this.originalElement,
			element: this.element,
			helper: this.helper,
			position: this.position,
			size: this.size,
			originalSize: this.originalSize,
			originalPosition: this.originalPosition
		};
	}

});

$.extend($.om.resizable, {
	version: "1.1"
});

/*
 * Resizable Extensions
 */

$.om.plugin.add("omResizable", "alsoResize", {

	start: function (event, ui) {
		var self = $(this).data("omResizable"), o = self.options;

		var _store = function (exp) {
			$(exp).each(function() {
				var el = $(this);
				el.data("resizable-alsoresize", {
					width: parseInt(el.width(), 10), height: parseInt(el.height(), 10),
					left: parseInt(el.css('left'), 10), top: parseInt(el.css('top'), 10),
					position: el.css('position') // to reset Opera on stop()
				});
			});
		};

		if (typeof(o.alsoResize) == 'object' && !o.alsoResize.parentNode) {
			if (o.alsoResize.length) { o.alsoResize = o.alsoResize[0]; _store(o.alsoResize); }
			else { $.each(o.alsoResize, function (exp) { _store(exp); }); }
		}else{
			_store(o.alsoResize);
		}
	},

	resize: function (event, ui) {
		var self = $(this).data("omResizable"), o = self.options, os = self.originalSize, op = self.originalPosition;

		var delta = {
			height: (self.size.height - os.height) || 0, width: (self.size.width - os.width) || 0,
			top: (self.position.top - op.top) || 0, left: (self.position.left - op.left) || 0
		},

		_alsoResize = function (exp, c) {
			$(exp).each(function() {
				var el = $(this), start = $(this).data("resizable-alsoresize"), style = {}, 
					css = c && c.length ? c : el.parents(ui.originalElement[0]).length ? ['width', 'height'] : ['width', 'height', 'top', 'left'];

				$.each(css, function (i, prop) {
					var sum = (start[prop]||0) + (delta[prop]||0);
					if (sum && sum >= 0)
						style[prop] = sum || null;
				});

				// Opera fixing relative position
				if ($.browser.opera && /relative/.test(el.css('position'))) {
					self._revertToRelativePosition = true;
					el.css({ position: 'absolute', top: 'auto', left: 'auto' });
				}

				el.css(style);
			});
		};

		if (typeof(o.alsoResize) == 'object' && !o.alsoResize.nodeType) {
			$.each(o.alsoResize, function (exp, c) { _alsoResize(exp, c); });
		}else{
			_alsoResize(o.alsoResize);
		}
	},

	stop: function (event, ui) {
		var self = $(this).data("omResizable"), o = self.options;

		var _reset = function (exp) {
			$(exp).each(function() {
				var el = $(this);
				// reset position for Opera - no need to verify it was changed
				el.css({ position: el.data("resizable-alsoresize").position });
			});
		};

		if (self._revertToRelativePosition) {
			self._revertToRelativePosition = false;
			if (typeof(o.alsoResize) == 'object' && !o.alsoResize.nodeType) {
				$.each(o.alsoResize, function (exp) { _reset(exp); });
			}else{
				_reset(o.alsoResize);
			}
		}

		$(this).removeData("resizable-alsoresize");
	}
});

$.om.plugin.add("omResizable", "containment", {

	start: function(event, ui) {
		var self = $(this).data("omResizable"), o = self.options, el = self.element;
		var oc = o.containment,	ce = (oc instanceof $) ? oc.get(0) : (/parent/.test(oc)) ? el.parent().get(0) : oc;
		if (!ce) return;

		self.containerElement = $(ce);

		if (/document/.test(oc) || oc == document) {
			self.containerOffset = { left: 0, top: 0 };
			self.containerPosition = { left: 0, top: 0 };

			self.parentData = {
				element: $(document), left: 0, top: 0,
				width: $(document).width(), height: $(document).height() || document.body.parentNode.scrollHeight
			};
		}

		// i'm a node, so compute top, left, right, bottom
		else {
			var element = $(ce), p = [];
			$([ "Top", "Right", "Left", "Bottom" ]).each(function(i, name) { p[i] = num(element.css("padding" + name)); });

			self.containerOffset = element.offset();
			self.containerPosition = element.position();
			self.containerSize = { height: (element.innerHeight() - p[3]), width: (element.innerWidth() - p[1]) };

			var co = self.containerOffset, ch = self.containerSize.height,	cw = self.containerSize.width,
						width = (self._hasScroll(ce, "left") ? ce.scrollWidth : cw ), height = (self._hasScroll(ce) ? ce.scrollHeight : ch);

			self.parentData = {
				element: ce, left: co.left, top: co.top, width: width, height: height
			};
		}
	},

	resize: function(event, ui) {
		var self = $(this).data("omResizable"), o = self.options,
				ps = self.containerSize, co = self.containerOffset, cs = self.size, cp = self.position,
				pRatio = self._aspectRatio || event.shiftKey, cop = { top:0, left:0 }, ce = self.containerElement;

		if (ce[0] != document && (/static/).test(ce.css('position'))) cop = co;

		if (cp.left < (self._helper ? co.left : 0)) {
			self.size.width = self.size.width + (self._helper ? (self.position.left - co.left) : (self.position.left - cop.left));
			if (pRatio) self.size.height = self.size.width / o.aspectRatio;
			self.position.left = o.helper ? co.left : 0;
		}

		if (cp.top < (self._helper ? co.top : 0)) {
			self.size.height = self.size.height + (self._helper ? (self.position.top - co.top) : self.position.top);
			if (pRatio) self.size.width = self.size.height * o.aspectRatio;
			self.position.top = self._helper ? co.top : 0;
		}

		self.offset.left = self.parentData.left+self.position.left;
		self.offset.top = self.parentData.top+self.position.top;

		var woset = Math.abs( (self._helper ? self.offset.left - cop.left : (self.offset.left - cop.left)) + self.sizeDiff.width ),
					hoset = Math.abs( (self._helper ? self.offset.top - cop.top : (self.offset.top - co.top)) + self.sizeDiff.height );

		var isParent = self.containerElement.get(0) == self.element.parent().get(0),
		    isOffsetRelative = /relative|absolute/.test(self.containerElement.css('position'));

		if(isParent && isOffsetRelative) woset -= self.parentData.left;

		if (woset + self.size.width >= self.parentData.width) {
			self.size.width = self.parentData.width - woset;
			if (pRatio) self.size.height = self.size.width / self.aspectRatio;
		}

		if (hoset + self.size.height >= self.parentData.height) {
			self.size.height = self.parentData.height - hoset;
			if (pRatio) self.size.width = self.size.height * self.aspectRatio;
		}
	},

	stop: function(event, ui){
		var self = $(this).data("omResizable"), o = self.options, cp = self.position,
				co = self.containerOffset, cop = self.containerPosition, ce = self.containerElement;

		var helper = $(self.helper), ho = helper.offset(), w = helper.outerWidth() - self.sizeDiff.width, h = helper.outerHeight() - self.sizeDiff.height;

		if (self._helper && !o.animate && (/relative/).test(ce.css('position')))
			$(this).css({ left: ho.left - cop.left - co.left, width: w, height: h });

		if (self._helper && !o.animate && (/static/).test(ce.css('position')))
			$(this).css({ left: ho.left - cop.left - co.left, width: w, height: h });

	}
});

var num = function(v) {
	return parseInt(v, 10) || 0;
};

var isNumber = function(value) {
	return !isNaN(parseInt(value, 10));
};

})(jQuery);
/*
 * $Id: om-sortable.js,v 1.9 2012/05/18 09:26:42 wangfan Exp $
 * operamasks-ui omSortable @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 * 
 * Depends:
 *  om-core.js
 *  om-mouse.js
 */
(function( $, undefined ) {

$.omWidget("om.sortable", $.om.omMouse, {
	widgetEventPrefix: "sort",
	options: {
		helper: "original",
		items: '> *',
		placeholder: false
	},
	_create: function() {

		this.containerCache = {};
		this.element.addClass("ui-sortable");

		//Get the items
		this.refresh();

		//Let's determine if the items are being displayed horizontally
		this.floating = this.items.length ? (/left|right/).test(this.items[0].item.css('float')) || (/inline|table-cell/).test(this.items[0].item.css('display')) : false;

		//Let's determine the parent's offset
		this.offset = this.element.offset();

		//Initialize mouse events for interaction
		this._mouseInit();

	},

	_mouseCapture: function(event, overrideHandle) {

		if (this.reverting) {
			return false;
		}

		if(this.options.disabled || this.options.type == 'static') return false;

		//We have to refresh the items data once first
		this._refreshItems(event);

		//Find out if the clicked node (or one of its parents) is a actual item in this.items
		var currentItem = null, self = this, nodes = $(event.target).parents().each(function() {
			if($.data(this, 'sortable-item') == self) {
				currentItem = $(this);
				return false;
			}
		});
		if($.data(event.target, 'sortable-item') == self) currentItem = $(event.target);

		if(!currentItem) return false;


		this.currentItem = currentItem;
		return true;

	},

	_mouseStart: function(event, overrideHandle, noActivation) {

		this.currentContainer = this;

		//We only need to call refreshPositions, because the refreshItems call has been moved to mouseCapture
		this.refreshPositions();

		//Create and append the visible helper
		this.helper = this._createHelper(event);

		//Cache the helper size
		this._cacheHelperProportions();

		/*
		 * - Position generation -
		 * This block generates everything position related - it's the core of draggables.
		 */

		//Cache the margins of the original element
		this._cacheMargins();

		//Get the next scrolling parent
		this.scrollParent = this.helper.scrollParent();

		//The element's absolute position on the page minus margins
		this.offset = this.currentItem.offset();
		this.offset = {
			top: this.offset.top - this.margins.top,
			left: this.offset.left - this.margins.left
		};

		// Only after we got the offset, we can change the helper's position to absolute
		// TODO: Still need to figure out a way to make relative sorting possible
		this.helper.css("position", "absolute");
		this.cssPosition = this.helper.css("position");

		$.extend(this.offset, {
			click: { //Where the click happened, relative to the element
				left: event.pageX - this.offset.left,
				top: event.pageY - this.offset.top
			},
			parent: this._getParentOffset(),
			relative: this._getRelativeOffset() //This is a relative to absolute position minus the actual position calculation - only used for relative positioned helper
		});

		//Generate the original position
		this.originalPosition = this._generatePosition(event);
		this.originalPageX = event.pageX;
		this.originalPageY = event.pageY;


		//Cache the former DOM position
		this.domPosition = { prev: this.currentItem.prev()[0], parent: this.currentItem.parent()[0] };

		//If the helper is not the original, hide the original so it's not playing any role during the drag, won't cause anything bad this way
		if(this.helper[0] != this.currentItem[0]) {
			this.currentItem.hide();
		}

		//Create the placeholder
		this._createPlaceholder();

			this.helper.css("zIndex", 1000);


		//Prepare scrolling
		if(this.scrollParent[0] != document && this.scrollParent[0].tagName != 'HTML')
			this.overflowOffset = this.scrollParent.offset();

		//Call callbacks
		this._trigger("start", event, this._uiHash());

		//Recache the helper size
		if(!this._preserveHelperProportions)
			this._cacheHelperProportions();

		this.helper.addClass("ui-sortable-helper");
		this._mouseDrag(event); //Execute the drag once - this causes the helper not to be visible before getting its correct position
		return true;

	},

	_mouseDrag: function(event) {

		//Compute the helpers position
		this.position = this._generatePosition(event);
		this.positionAbs = this._convertPositionTo("absolute");

		if (!this.lastPositionAbs) {
			this.lastPositionAbs = this.positionAbs;
		}

		//Do scrolling
			var o = this.options, scrolled = false;

				if((this.overflowOffset.top + this.scrollParent[0].offsetHeight) - event.pageY < 20)
					this.scrollParent[0].scrollTop = scrolled = this.scrollParent[0].scrollTop + 20;
				else if(event.pageY - this.overflowOffset.top < 20)
					this.scrollParent[0].scrollTop = scrolled = this.scrollParent[0].scrollTop - 20;

				if((this.overflowOffset.left + this.scrollParent[0].offsetWidth) - event.pageX < 20)
					this.scrollParent[0].scrollLeft = scrolled = this.scrollParent[0].scrollLeft + 20;
				else if(event.pageX - this.overflowOffset.left < 20)
					this.scrollParent[0].scrollLeft = scrolled = this.scrollParent[0].scrollLeft - 20;

		//Regenerate the absolute position used for position checks
		this.positionAbs = this._convertPositionTo("absolute");

		//Set the helper position
		this.helper[0].style.left = this.position.left+'px';
		this.helper[0].style.top = this.position.top+'px';

		//Rearrange
		for (var i = this.items.length - 1; i >= 0; i--) {

			//Cache variables and intersection, continue if no intersection
			var item = this.items[i], itemElement = item.item[0], intersection = this._intersectsWithPointer(item);
			if (!intersection) continue;

			if(itemElement != this.currentItem[0] //cannot intersect with itself
				&&	this.placeholder[intersection == 1 ? "next" : "prev"]()[0] != itemElement //no useless actions that have been done before
				&&	!$.contains(this.placeholder[0], itemElement) //no action if the item moved is the parent of the item checked
				&& (this.options.type == 'semi-dynamic' ? !$.contains(this.element[0], itemElement) : true)
				//&& itemElement.parentNode == this.placeholder[0].parentNode // only rearrange items within the same container
			) {

				this.direction = intersection == 1 ? "down" : "up";

				if (this._intersectsWithSides(item)) {
					this._rearrange(event, item);
				} else {
					break;
				}

				break;
			}
		}

		this.lastPositionAbs = this.positionAbs;
		return false;

	},

	_mouseStop: function(event, noPropagation) {

		if(!event) return;
			this._clear(event, noPropagation);

		return false;

	},


	/* Be careful with the following core functions */
	_intersectsWith: function(item) {

		var x1 = this.positionAbs.left,
			x2 = x1 + this.helperProportions.width,
			y1 = this.positionAbs.top,
			y2 = y1 + this.helperProportions.height;

		var l = item.left,
			r = l + item.width,
			t = item.top,
			b = t + item.height;

		var dyClick = this.offset.click.top,
			dxClick = this.offset.click.left;

		var isOverElement = (y1 + dyClick) > t && (y1 + dyClick) < b && (x1 + dxClick) > l && (x1 + dxClick) < r;

			return (l < x1 + (this.helperProportions.width / 2) // Right Half
				&& x2 - (this.helperProportions.width / 2) < r // Left Half
				&& t < y1 + (this.helperProportions.height / 2) // Bottom Half
				&& y2 - (this.helperProportions.height / 2) < b ); // Top Half

	},

	_isOverAxis: function( x, reference, size ) {
        //Determines when x coordinate is over "b" element axis
        return ( x > reference ) && ( x < ( reference + size ) );
    },
	
	_intersectsWithPointer: function(item) {

		var isOverElementHeight = this._isOverAxis(this.positionAbs.top + this.offset.click.top, item.top, item.height),
			isOverElementWidth = this._isOverAxis(this.positionAbs.left + this.offset.click.left, item.left, item.width),
			isOverElement = isOverElementHeight && isOverElementWidth,
			verticalDirection = this._getDragVerticalDirection(),
			horizontalDirection = this._getDragHorizontalDirection();

		if (!isOverElement)
			return false;

		return this.floating ?
			( ((horizontalDirection && horizontalDirection == "right") || verticalDirection == "down") ? 2 : 1 )
			: ( verticalDirection && (verticalDirection == "down" ? 2 : 1) );

	},

	_intersectsWithSides: function(item) {

		var isOverBottomHalf = this._isOverAxis(this.positionAbs.top + this.offset.click.top, item.top + (item.height/2), item.height),
			isOverRightHalf = this._isOverAxis(this.positionAbs.left + this.offset.click.left, item.left + (item.width/2), item.width),
			verticalDirection = this._getDragVerticalDirection(),
			horizontalDirection = this._getDragHorizontalDirection();

		if (this.floating && horizontalDirection) {
			return ((horizontalDirection == "right" && isOverRightHalf) || (horizontalDirection == "left" && !isOverRightHalf));
		} else {
			return verticalDirection && ((verticalDirection == "down" && isOverBottomHalf) || (verticalDirection == "up" && !isOverBottomHalf));
		}

	},

	_getDragVerticalDirection: function() {
		var delta = this.positionAbs.top - this.lastPositionAbs.top;
		return delta != 0 && (delta > 0 ? "down" : "up");
	},

	_getDragHorizontalDirection: function() {
		var delta = this.positionAbs.left - this.lastPositionAbs.left;
		return delta != 0 && (delta > 0 ? "right" : "left");
	},

	refresh: function(event) {
		this._refreshItems(event);
		this.refreshPositions();
		return this;
	},

	_refreshItems: function(event) {

		this.items = [];
		this.containers = [this];
		var items = this.items;
		var queries = [$(this.options.items, this.element), this];

			var targetData = queries[1];
			var _queries = queries[0];

			for (var j=0, queriesLength = _queries.length; j < queriesLength; j++) {
				var item = $(_queries[j]);

				item.data('sortable-item', targetData); // Data for target checking (mouse manager)

				items.push({
					item: item,
					instance: targetData,
					width: 0, height: 0,
					left: 0, top: 0
				});
			};

	},

	refreshPositions: function(fast) {

		//This has to be redone because due to the item being moved out/into the offsetParent, the offsetParent's position will change
		if(this.offsetParent && this.helper) {
			this.offset.parent = this._getParentOffset();
		}

		for (var i = this.items.length - 1; i >= 0; i--){
			var item = this.items[i];

			//We ignore calculating positions of all connected containers when we're not over them
			if(item.instance != this.currentContainer && this.currentContainer && item.item[0] != this.currentItem[0])
				continue;

			var t = item.item;

			if (!fast) {
				item.width = t.outerWidth();
				item.height = t.outerHeight();
			}

			var p = t.offset();
			item.left = p.left;
			item.top = p.top;
		};
		return this;
	},

	_createPlaceholder: function(that) {

		var self = that || this, o = self.options;

		if(!o.placeholder || o.placeholder.constructor == String) {
			var className = o.placeholder;
			o.placeholder = {
				element: function() {

					var el = $(document.createElement(self.currentItem[0].nodeName))
						.addClass(className || self.currentItem[0].className+" ui-sortable-placeholder")
						.removeClass("ui-sortable-helper")[0];

					if(!className)
						el.style.visibility = "hidden";

					return el;
				}
			};
		}

		//Create the placeholder
		self.placeholder = $(o.placeholder.element.call(self.element, self.currentItem));

		//Append it after the actual current item
		self.currentItem.after(self.placeholder);


	},

	_createHelper: function(event) {
		var helper = this.currentItem;

		if(!helper.parents('body').length) //Add the helper to the DOM if that didn't happen already
			$(this.currentItem[0].parentNode)[0].appendChild(helper[0]);

		this._storedCSS = { width: this.currentItem[0].style.width, height: this.currentItem[0].style.height, position: this.currentItem.css("position"), top: this.currentItem.css("top"), left: this.currentItem.css("left") };

		return helper;

	},


	_getParentOffset: function() {


		//Get the offsetParent and cache its position
		this.offsetParent = this.helper.offsetParent();
		var offsetp = this.offsetParent, po = offsetp.offset();

		// This is a special case where we need to modify a offset calculated on start, since the following happened:
		// 1. The position of the helper is absolute, so it's position is calculated based on the next positioned parent
		// 2. The actual offset parent is a child of the scroll parent, and the scroll parent isn't the document, which means that
		//    the scroll is included in the initial calculation of the offset of the parent, and never recalculated upon drag
		if(this.cssPosition == 'absolute' && this.scrollParent[0] != document && $.contains(this.scrollParent[0], offsetp[0])) {
			po.left += this.scrollParent.scrollLeft();
			po.top += this.scrollParent.scrollTop();
		}

		if((offsetp[0] == document.body) //This needs to be actually done for all browsers, since pageX/pageY includes this information
		|| (offsetp[0].tagName && offsetp[0].tagName.toLowerCase() == 'html' && $.browser.msie)) //Ugly IE fix
			po = { top: 0, left: 0 };

		return {
			top: po.top + (parseInt(offsetp.css("borderTopWidth"),10) || 0),
			left: po.left + (parseInt(offsetp.css("borderLeftWidth"),10) || 0)
		};

	},

	_getRelativeOffset: function() {

		if(this.cssPosition == "relative") {
			var p = this.currentItem.position();
			return {
				top: p.top - (parseInt(this.helper.css("top"),10) || 0) + this.scrollParent.scrollTop(),
				left: p.left - (parseInt(this.helper.css("left"),10) || 0) + this.scrollParent.scrollLeft()
			};
		} else {
			return { top: 0, left: 0 };
		}

	},

	_cacheMargins: function() {
		this.margins = {
			left: (parseInt(this.currentItem.css("marginLeft"),10) || 0),
			top: (parseInt(this.currentItem.css("marginTop"),10) || 0)
		};
	},

	_cacheHelperProportions: function() {
		this.helperProportions = {
			width: this.helper.outerWidth(),
			height: this.helper.outerHeight()
		};
	},

	_convertPositionTo: function(d, pos) {

		if(!pos) pos = this.position;
		var mod = d == "absolute" ? 1 : -1;
		var o = this.options, scroll = this.cssPosition == 'absolute' && !(this.scrollParent[0] != document && $.contains(this.scrollParent[0], this.offsetParent[0])) ? this.offsetParent : this.scrollParent, scrollIsRootNode = (/(html|body)/i).test(scroll[0].tagName);

		return {
			top: (
				pos.top																	// The absolute mouse position
				+ this.offset.relative.top * mod										// Only for relative positioned nodes: Relative offset from element to offset parent
				+ this.offset.parent.top * mod											// The offsetParent's offset without borders (offset + border)
				- ($.browser.safari && this.cssPosition == 'fixed' ? 0 : ( this.cssPosition == 'fixed' ? -this.scrollParent.scrollTop() : ( scrollIsRootNode ? 0 : scroll.scrollTop() ) ) * mod)
			),
			left: (
				pos.left																// The absolute mouse position
				+ this.offset.relative.left * mod										// Only for relative positioned nodes: Relative offset from element to offset parent
				+ this.offset.parent.left * mod											// The offsetParent's offset without borders (offset + border)
				- ($.browser.safari && this.cssPosition == 'fixed' ? 0 : ( this.cssPosition == 'fixed' ? -this.scrollParent.scrollLeft() : scrollIsRootNode ? 0 : scroll.scrollLeft() ) * mod)
			)
		};

	},

	_generatePosition: function(event) {

		var o = this.options, scroll = this.cssPosition == 'absolute' && !(this.scrollParent[0] != document && $.contains(this.scrollParent[0], this.offsetParent[0])) ? this.offsetParent : this.scrollParent, scrollIsRootNode = (/(html|body)/i).test(scroll[0].tagName);

		// This is another very weird special case that only happens for relative elements:
		// 1. If the css position is relative
		// 2. and the scroll parent is the document or similar to the offset parent
		// we have to refresh the relative offset during the scroll so there are no jumps
		if(this.cssPosition == 'relative' && !(this.scrollParent[0] != document && this.scrollParent[0] != this.offsetParent[0])) {
			this.offset.relative = this._getRelativeOffset();
		}

		var pageX = event.pageX;
		var pageY = event.pageY;

		return {
			top: (
				pageY																// The absolute mouse position
				- this.offset.click.top													// Click offset (relative to the element)
				- this.offset.relative.top												// Only for relative positioned nodes: Relative offset from element to offset parent
				- this.offset.parent.top												// The offsetParent's offset without borders (offset + border)
				+ ($.browser.safari && this.cssPosition == 'fixed' ? 0 : ( this.cssPosition == 'fixed' ? -this.scrollParent.scrollTop() : ( scrollIsRootNode ? 0 : scroll.scrollTop() ) ))
			),
			left: (
				pageX																// The absolute mouse position
				- this.offset.click.left												// Click offset (relative to the element)
				- this.offset.relative.left												// Only for relative positioned nodes: Relative offset from element to offset parent
				- this.offset.parent.left												// The offsetParent's offset without borders (offset + border)
				+ ($.browser.safari && this.cssPosition == 'fixed' ? 0 : ( this.cssPosition == 'fixed' ? -this.scrollParent.scrollLeft() : scrollIsRootNode ? 0 : scroll.scrollLeft() ))
			)
		};

	},

	_rearrange: function(event, i, a, hardRefresh) {

		a ? a[0].appendChild(this.placeholder[0]) : i.item[0].parentNode.insertBefore(this.placeholder[0], (this.direction == 'down' ? i.item[0] : i.item[0].nextSibling));

		//Various things done here to improve the performance:
		// 1. we create a setTimeout, that calls refreshPositions
		// 2. on the instance, we have a counter variable, that get's higher after every append
		// 3. on the local scope, we copy the counter variable, and check in the timeout, if it's still the same
		// 4. this lets only the last addition to the timeout stack through
		this.counter = this.counter ? ++this.counter : 1;
		var self = this, counter = this.counter;

		window.setTimeout(function() {
			if(counter == self.counter) self.refreshPositions(!hardRefresh); //Precompute after each DOM insertion, NOT on mousemove
		},0);

	},

	_clear: function(event, noPropagation) {

		this.reverting = false;
		// We delay all events that have to be triggered to after the point where the placeholder has been removed and
		// everything else normalized again
		var delayedTriggers = [], self = this;

		// We first have to update the dom position of the actual currentItem
		// Note: don't do it if the current item is already removed (by a user), or it gets reappended (see #4088)
		if(!this._noFinalSort && this.currentItem.parent().length) this.placeholder.before(this.currentItem);
		this._noFinalSort = null;

		if(this.helper[0] == this.currentItem[0]) {
			for(var i in this._storedCSS) {
				if(this._storedCSS[i] == 'auto' || this._storedCSS[i] == 'static') this._storedCSS[i] = '';
			}
			this.currentItem.css(this._storedCSS).removeClass("ui-sortable-helper");
		} else {
			this.currentItem.show();
		}

		this.helper.css("zIndex", 1000); //Reset z-index

		if(this.cancelHelperRemoval) {
			if(!noPropagation) {
				//this._trigger("beforeStop", event, this._uiHash());
				for (var i=0; i < delayedTriggers.length; i++) { delayedTriggers[i].call(this, event); }; //Trigger all delayed events
				this._trigger("stop", event, this._uiHash());
			}
			return false;
		}

		this.placeholder[0].parentNode.removeChild(this.placeholder[0]);

		if(this.helper[0] != this.currentItem[0]) this.helper.remove(); this.helper = null;

		if(!noPropagation) {
			for (var i=0; i < delayedTriggers.length; i++) { delayedTriggers[i].call(this, event); }; //Trigger all delayed events
			this._trigger("stop", event, this._uiHash());
		}

		this.fromOutside = false;
		return true;

	},

	_trigger: function() {
		$.OMWidget.prototype._trigger.apply(this, arguments); 
	},

	_uiHash: function(inst) {
		var self = inst || this;
		return {
			helper: self.helper,
			placeholder: self.placeholder || $([]),
			position: self.position,
			originalPosition: self.originalPosition,
			offset: self.positionAbs,
			item: self.currentItem,
			sender: inst ? inst.element : null
		};
	}

});

$.extend($.om.sortable, {
	version: "1.1"
});

})(jQuery);
/*
 * $Id: om-panel.js,v 1.47 2012/06/20 08:29:10 chentianzhen Exp $
 * operamasks-ui omPanel @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 *
 * Depends:
 *   om-core.js
 */
(function($) {
	var innerToolId = ['collapse','min','max','close'],
		innerToolCls = ['om-panel-tool-collapse','om-panel-tool-expand','om-panel-tool-min','om-panel-tool-max','om-panel-tool-close'],
		effects = {anim:true , speed: 'fast'};
	/**
     * @name omPanel
     * @class 面版是一个布局组件，同时也是一个展示内容的容器。<br/>
     * <b>特点：</b><br/>
     * <ol>
     *      <li>可以使用本地数据源，也可以使用远程数据源，同时提供友好的错误处理机制。</li>
     *      <li>支持动态修改标题内容和图标。</li>
     *      <li>工具条按钮内置与可扩展。</li>
     *      <li>提供丰富的事件。</li>
     * </ol>
     * 
     * <b>示例：</b><br/>
     * <pre>
     * &lt;script type="text/javascript" >
     * $(document).ready(function() {
     *     $('#panel').omPanel({
     *         width: '400px',
     *         height: '200px',
     *         title: 'panel标题',
     *         collapsed: false,//组件创建后为收起状态
     *         collapsible: true,//渲染收起与展开按钮
     *         closable: true, //渲染关闭按钮
     *         onBeforeOpen: function(event){if(window.count!==0)return false;}, 
     *         onOpen: function(event){alert('panel被打开了。');}
     *     });
     * });
     * &lt;/script>
     * 
     * &lt;input id="panel"/>
     * </pre>
     * @constructor
     * @description 构造函数. 
     * @param p 标准config对象：{}
     */
	$.omWidget("om.omPanel" , {
		options:/** @lends omPanel#*/{
			/**
			 * panel的标题，位于头部左边的位置。
			 * @type String 
			 * @default 无
			 * @example
             * $("#panel").omPanel({title:"&lt;span style='color:red'&gt;标题&lt;/span&gt;"});<br/>
             * 因为所给的标题会当成html文本，所以当出现特殊字符时必须进行转义，如"<"必须转义为"&amp;lt;"。
			 */
			title: '',
			/**
			 * panel的图标样式，位于头部左边的位置。
			 * @name omPanel#iconCls
			 * @type String
			 * @default 无
			 * @example
			 * $("#panel").omPanel({iconCls:'myCls'});(myCls为自定义的css样式类别)
			 */
			/**
			 * panel组件的宽度，可取值为'auto'（默认情况,由浏览器决定宽度），可以取值为'fit'，表示适应父容器的大小（width:100%）。任何其他的值（比如百分比、数字、em单位、px单位的值等等）将被直接赋给width属性。 
			 * @type Number,String
			 * @default 'auto'
			 * @example
			 * $("#panel").omPanel({width:'300px'});
			 */ 
			width: 'auto',
			/**
			 * panel组件的高度，可取值为'auto'（由内容决定高度），可以取值为'fit'，表示适应父容器的大小（height:100%）。任何其他的值（比如百分比、数字、em单位、px单位的值等等）将被直接赋给height属性。
			 * @type Number,String
			 * @default 'auto'
			 * @example
			 * $("#panel").omPanel({height:'200px'});
			 */
			height: 'auto',
			/**
			 * 在组件创建时是否要渲染其头部。
			 * @type Boolean
			 * @default true
			 * @example
			 * $("#panel").omPanel({header:false}); //不要渲染panel的头部
			 */
			header: true,
			/**
			 * 组件内容的数据来源。当设置了此值后，组件会从远程获取数据来填充主体部分。可以调用reload方法动态更新组件主体内容。
			 * @name omPanel#url
			 * @type String
			 * @default 无
			 * @example
			 * $("#panel").omPanel({url:'http://www.ui.operamasks.org/test'});
			 */
			/**
			 * 组件创建时是否显示收起工具按钮(位于头部右边)。
			 * @type Boolean
			 * @default false
			 * @example
			 * $("#panel").omPanel({collapsible:true});
			 */
			collapsible: false,
			/**
			 * 组件创建时是否显示关闭工具按钮(位于头部右边)。
			 * @type Boolean
			 * @default false
			 * @example
			 * $("#panel").omPanel({closable:true});
			 */
			closable: false,
			/**
			 * 组件创建后是否处于关闭状态，可调用open方法动态打开该组件。
			 * @type Boolean
			 * @default false
			 * @example
			 * $("#panel").omPanel({closed:false});
			 */
			closed: false,
			/**
			 * 组件创建后是否处于收起状态，可调用expand方法动态展开组件主体内容。
			 * @type Boolean
			 * @default false
			 * @example
			 * $("#panel").omPanel({collapsed:true});
			 */
			collapsed: false,
			/**
			 * 组件头部右上角的工具条。<br/>
			 * 当为Array时，数组中每个对象代表了一个工具按钮,每个对象格式如下:<br/>
			 * <pre>
			 * {
			 *     id:内置工具按钮，可选值为'min'，'max'，'close'，collapse'。
			 *     iconCls:工具按钮的样式，如果id属性存在，则忽略此属性，此属性可为String或者Array，
			 *             当为String时，表示按钮在所有状态下的样式，当为Array时，索引0表示按钮
			 * 	           常态下的样式，索引1表示按钮被鼠标hover时的样式。
			 *     handler:按钮图标被单击时触发的事件(如果没有提供此属性，则按钮按下后会没有反应)。
			 * }
			 * </pre>
			 * 补充:考虑到用户习惯，默认情况下，如果collapsible=true，则会显示收起按钮，它将永远排在第一个位置。<br/>
			 * 如果closable=true,则会显示关闭按钮，它将永远排在最后一个位置。 <br/>
			 * 所以可以认为tools产生的工具条会放在中间，如果用户不想受限于这样的排序，则不要设置collapsible和closable这两个属性，直接利用tools属性重新定义想要的工具条。 <br/><br/>
			 * 
			 * 当为Selector时，此Selector对应的dom结构将作为tool的一部分进行渲染，这时事件的注册，样式的变换将完全交由用户处理。
			 * @type Array,Selector
			 * @default []
			 * @example
			 * <pre>
			 * $("#panel").omPanel({tools:[
			 *         {id:'min',handler:function(panel , event){ alert("最小化操作还未实现."); }},
			 *         {id:'max',handler:function(panel , event){ alert("最大化操作还未实现."); }}
			 *     ]}
			 * );
			 * </pre>
			 */
			tools: [],
			/**
			 * 远程加载数据时的提示信息，只有设置了url或者调用reload方法时传入一个url才生效。
			 * 内置了一种默认的样式(显示一个正在加载的图标)，当传入字符串"default"时启用此默认样式。
			 * @type String
			 * @default 'default'
			 * @example
			 * $("#panel").omPanel({loadingMessage:"&lt;img src='load.gif'&gt;&lt;/img&gt;loading......"});
			 */
			loadingMessage: "default",
			/**
			 * 在远程取数时，拿到数据后，显示数据前的一个预处理函数，类似于一个过滤器的作用，该函数的返回值即为最终的数据。
			 * @name omPanel#preProcess
			 * @type Function
			 * @param data 服务端返回的数据 
			 * @param textStatus 服务端响应的状态
			 * @default null
			 * @example
			 * $("#panel").omPanel({url:'test.do',preProcess:function(data , textStatus){return 'test';}});
			 * //不管服务器返回什么数据，主体内容永远为'test'
			 */
			/**
			 * 远程取数发生错误时触发的函数。
			 * @event
			 * @param xmlHttpRequest XMLHttpRequest对象
			 * @param textStatus  错误类型
			 * @param errorThrown  捕获的异常对象
			 * @param event jQuery.Event对象
			 * @name omPanel#onError
			 * @type Function
			 * @default null
			 * @example
			 * <pre>
			 * $("#panel").omPanel({url:'test.do',
			 *     onError:function(xmlHttpRequest, textStatus, errorThrown, event){
			 *         alert('网络发生了错误，请稍后再试。');
			 *     }
			 * });
			 * </pre>
			 */
			/**
			 * 远程取数成功后触发的函数。
			 * @event
			 * @param data 从服务器返回的数据
			 * @param textStatus 服务端响应的状态
			 * @param xmlHttpRequest XMLHttpRequest对象
			 * @param event jQuery.Event对象
			 * @name omPanel#onSuccess
			 * @type Function
			 * @default null
			 * @example
			 * <pre>
			 * $("#panel").omPanel({url:'test.do',
			 *     onSuccess:function(data, textStatus, xmlHttpRequest, event){
			 *         alert("服务器返回的数据为:" + data);
			 *     }
			 * });
			 * </pre>
			 */
			/**
			 * 打开panel组件前触发的函数，返回false可以阻止打开。
			 * @event
			 * @param event jQuery.Event对象
			 * @name omPanel#onBeforeOpen
			 * @type Function
			 * @default null
			 * @example
			 * $("#panel").omPanel({onBeforeOpen:function(event){alert("永远打不开该组件.");return false;}});
			 */
			/**
			 * 打开panel组件后触发的函数。
			 * @event
			 * @param event jQuery.Event对象
			 * @name omPanel#onOpen
			 * @type Function
			 * @default null
			 * @example
			 * $("#panel").omPanel({onOpen:function(event){alert("panel已经被打开了。");}});
			 */
			/**
			 * 关闭panel组件前触发的函数，返回false可以阻止关闭。
			 * @event
			 * @param event jQuery.Event对象
			 * @name omPanel#onBeforeClose
			 * @type Function
			 * @default null
			 * @example
			 * $("#panel").omPanel({onBeforeClose:function(event){alert("该组件即将被关闭。");}});
			 */
			/**
			 * 关闭panel组件后触发的函数。
			 * @event
			 * @param event jQuery.Event对象
			 * @name omPanel#onClose
			 * @type Function
			 * @default null
			 * @example
			 * $("#panel").omPanel({onClose:function(event){alert("panel已经被关闭了。");}});
			 */
			/**
			 * 收起panel组件前触发的函数，返回false可以阻止收起。
			 * @event
			 * @param event jQuery.Event对象
			 * @name omPanel#onBeforeCollapse
			 * @type Function
			 * @default null
			 * @example
			 * $("#panel").omPanel({onBeforeCollapse:function(event){alert("该组件即将被收起。");}});
			 */
			/**
			 * 收起panel组件后触发的函数。
			 * @event
			 * @param event jQuery.Event对象
			 * @name omPanel#onCollapse
			 * @type Function
			 * @default null
			 * @example
			 * $("#panel").omPanel({onCollapse:function(event){alert("panel已经被收起了。");}});
			 */
			/**
			 * 展开panel组件前触发的函数，返回false可以阻止展开。
			 * @event
			 * @param event jQuery.Event对象
			 * @name omPanel#onBeforeExpand
			 * @type Function
			 * @default null
			 * @example
			 * $("#panel").omPanel({onBeforeExpand:function(event){alert("该组件即将被展开。");}});
			 */
			/**
			 * 展开panel组件后触发的函数。
			 * @event
			 * @param event jQuery.Event对象
			 * @name omPanel#onExpand
			 * @type Function
			 * @default null
			 * @example
			 * $("#panel").omPanel({onExpand:function(event){alert("panel已经被展开了。");}});
			 */
			 /**
			  *组件的关闭模式，当调用close方法时怎么处理组件的关闭，"hidden"表示直接display:none ,"visibility"表示缩小为1px的点
			  * 此属性暂时不暴露
			  */
			 _closeMode : "hidden",
			 _helpMsg : false
		},
		_create: function(){
		    this.element.addClass("om-panel-body om-widget-content")
		    	.wrap("<div class='om-widget om-panel'></div>");
		},
		_init: function(){
			var options = this.options,
				$body = this.element,
				$parent = $body.parent(),
				$header;
			this._renderHeader();
			$header = $body.prev();
			if(options.header === false){
		 		$body.addClass("om-panel-noheader");
		 	}
			this._bindEvent();
		 	this._resize($parent);
		 	var headerHeight = options.header !== false? $header.outerHeight() : 0;
		 	if(options.collapsed !== false){
		 		"auto"!==options.height && $parent.height(headerHeight);		 		
		 		$body.hide();
		 		if(options.header !== false){
		 			$header.find(">.om-panel-tool >.om-panel-tool-collapse").removeClass("om-panel-tool-collapse")
		 				.addClass("om-panel-tool-expand");
		 		}
		 	}else{
		 		$body.show();
		 		"auto"!==options.height && $parent.height(headerHeight + $body.outerHeight());
		 		if(options.header !== false){
		 			$header.find(">.om-panel-tool >.om-panel-tool-expand").removeClass("om-panel-tool-expand")
		 				.addClass("om-panel-tool-collapse");
		 		}	
		 	}
		 	options.closed !== false? this._hide($parent) : this._show($parent);
		 	this.reload();
		},
		_hide: function($target){
			if("hidden" === this.options._closeMode){
				$target.hide();
			}else if("visibility" === this.options._closeMode){
				$target.addClass("om-helper-hidden-accessible");
			}
		},
		_show: function($target){
			if("hidden" === this.options._closeMode){
				$target.show();
			}else if("visibility" === this.options._closeMode){
				$target.removeClass("om-helper-hidden-accessible");
			}
		},
		_bindEvent: function(){
			var self = this,
				$body = this.element,
				options = this.options,
				header = $body.prev();
			if(options.collapsible !== false){
				header.click(function(event){
					if($(event.target).is(".om-panel-icon,.om-panel-title,.om-panel-header")){
						options.collapsed !== false? self.expand() : self.collapse();
					}
				}).find(".om-panel-tool-collapse , .om-panel-tool-expand")
				.click(function(){
					options.collapsed !== false? self.expand() : self.collapse();
				});
			}
			if(options.closable !== false){
				header.find(".om-panel-tool-close")
					.click(function(e){
						self.close();
					});				
			}
		},
		_renderHeader: function(){
			this.header && this.header.remove();
			if(this.options.header === false){
				return ;
			}
			var that = this,
				options = this.options,
				tools = options.tools,
				$header = this.header = $("<div class='om-panel-header'></div>").insertBefore(this.element);
			if(options._helpMsg){
				$header.parent().addClass('helpMsg');
			}
			if(options.iconCls){
				$("<div class='om-icon om-panel-icon'></div>").addClass(options.iconCls).appendTo($header);
			}
			$("<div class='om-panel-title'></div>").html(options.title).appendTo($header);
			$tool = $("<div class='om-panel-tool'></div>");
			if(options.collapsible !== false){
				$("<div class='om-icon om-panel-tool-collapse'></div>").appendTo($tool);	
			}
			//处理自定义头部右边的工具条
			if($.isArray(tools)){
				for(var i=0,len=tools.length; i<len; i++){
					var tool = tools[i],
						iconCls;
					if(iconCls = this._getInnerToolCls(tool.id)){
						$("<div class='om-icon'></div>").addClass(iconCls)
							.click(	function(event){
								tool.handler.call(this,that,event);
							}).appendTo($tool);
					}else if(typeof tool.iconCls === 'string'){
						$("<div class='om-icon'></div>").addClass(tool.iconCls)
							.click(	function(event){
								tool.handler.call(this,that,event);
							}).appendTo($tool);
					}else if($.isArray(tool.iconCls)){
						//这里必须要用内部匿名函数，因为hover中用到了tool，否则tool的值很可能已经被改掉了
						(function(tool){
							$("<div class='om-icon'></div>").addClass(tool.iconCls[0])
								.click(function(event){
									tool.handler.call(this,that,event);
								})
								.hover(function(){
									if(tool.iconCls[1]){
										$(this).toggleClass(tool.iconCls[1]);
									}
								}).appendTo($tool);
						})(tool);
					}
				}
			}else{
				try{
					$(tools).appendTo($tool);
				}catch(error){
					throw "bad format of jquery selector.";
				}
			}
			
			if(options.closable !== false){
				$("<div class='om-icon om-panel-tool-close'></div>").appendTo($tool);	
			}
			//处理内置工具按钮hover时的样式变换
			$tool.find(">div.om-icon").hover(
				function(){
					var self = this;
					$.each(innerToolCls , function(){
						if($(self).hasClass(this)){
							$(self).toggleClass(this+"-hover");
						}
					});
				}
			);
			$tool.appendTo($header);
		},
		/**
		 * 初始化panel,header,body的宽和高
		 */
	 	_resize: function($panel){
	 		var $body = this.element,
	 			$header = $body.prev(),
	 			$panel = $body.parent(),
	 			options = this.options;
	 		if(options.width == 'fit'){
	 			options.width = '100%';
	 			$panel.width('100%');
	 			$header.css("width" , "");
	 			$body.css("width" , "");
	 		}else if(options.width !== 'auto'){
				$panel.width(options.width);
				$header.outerWidth($panel.width());
				$body.outerWidth($panel.width());
	 		}else{
	 			var style = $body.attr("style");
	 			if(style && style.indexOf("width") !== -1){
	 				$panel.width($body.outerWidth());
	 				$header.outerWidth($body.outerWidth());
	 			}else{
	 				$panel.css("width" , "");
		 			$header.css("width" , "");
		 			$body.css("width" , "");
	 			}
	 		}
	 		if(options.height == 'fit'){
	 			options.height = '100%';
	 			$panel.height('100%');
	 			$body.outerHeight($panel.height()- (this.options.header!==false?$header.outerHeight():0) );	 
	 		}else if(options.height !== 'auto'){
				$panel.height(options.height);
				$body.outerHeight($panel.height()- (this.options.header!==false?$header.outerHeight():0) );	 
	 		}else{
	 			var style = $body.attr("style");
	 			if(style && style.indexOf("height") !== -1){
	 				$panel.height($header.outerHeight() + $body.outerHeight());
	 			}else{
	 				$panel.css("height" , "");
		 			$body.css("height" , "");
	 			}
	 		}
	 	},
	 	_getInnerToolCls: function(id){
	 		return $.inArray(id , innerToolId)!=-1? 'om-panel-tool-'+id : null;
	 	},
		_showLoadingMessage: function(){
			var options = this.options,
				$body = this.element,
				$loadMsg = $body.next(".om-panel-loadingMessage"),
				position = {
					width:$body.innerWidth(), 
					height:$body.innerHeight(),
					left:$body.position().left + parseInt($body.css("border-left-width")),
					top:$body.position().top
				};
			if($loadMsg.length === 0){
				if("default" === options.loadingMessage){
					$("<div class='om-panel-loadingMessage'><div class='valignMiddle'><div class='loadingImg'>数据加载中</div></div></div>")
					.css(position).appendTo($body.parent());
				}else{
					$("<div class='om-panel-loadingMessage'></div>").appendTo($body.parent())
					.html(options.loadingMessage)
					.css(position);
				}
			}else{
				$loadMsg.css(position).show();
			}
		},
		_hideLoadingMessage: function(){
			this.element.parent().find(".om-panel-loadingMessage").hide();
		},
		/**
		 * 设置panel的标题
		 * @name omPanel#setTitle
		 * @function
		 * @param title 新的标题
		 */
		setTitle: function(title){
		 	this.element.prev().find(">.om-panel-title").html(title);
		},
		/**
		 * 设置panel的图标样式
		 * @name omPanel#setIconClass
		 * @function
		 * @param iconCls 新的图标样式
		 * @returns 当前jquery对象
		 */
		setIconClass: function(iconCls){
			var $header = this.element.prev();
			var $icon = $header.find(">.om-panel-icon");
		 	if(iconCls == null && $icon.length!==0){
		 		$icon.remove();
		 	}else{
		 		if($icon.length==0){
		 			$icon = $("<div class='om-icon om-panel-icon'></div>").insertBefore($header.find(">.om-panel-title"));
		 		}
		 		if(this.options.iconCls){
		 			$icon.removeClass(this.options.iconCls);
		 		}
		 		$icon.addClass(iconCls);
		 		this.options.iconCls = iconCls;
		 	}
		},
		/**
		 * 打开组件，使组件可见。
		 * @name omPanel#open
		 * @function
		 */
		open: function(){
			var $body = this.element,
				options = this.options;
			if(options.closed){
				if(options.onBeforeOpen && this._trigger("onBeforeOpen") === false){
					return ;
				}
				this._show($body.parent());
				options.closed = false;
				options.onOpen && this._trigger("onOpen");
			}
		},
		/**
		 * 关闭组件，使组件不可见。
		 * @name omPanel#close
		 * @function
		 */
		close: function(){
			var $body = this.element,
				options = this.options;
			if(!options.closed){
				if(options.onBeforeClose && this._trigger("onBeforeClose") === false){
					return ;
				}
				this._hide($body.parent());
				options.closed = true;
				options.onClose && this._trigger("onClose");
			}
		},
		/**
		 * 重新加载数据,为使该方法有效，创建组件时必须指定url属性或者调用此方法时传入一个合法的url。
		 * @name omPanel#reload
		 * @function
		 * @param url 一个有效的取数地址
		 */
		reload: function(url){
			var options = this.options,
				$body = this.element,
				self = this;
			if($body.data("loading")){
				return ;
			}else{
				$body.data("loading" , true);
			}
		 	url = url || options.url;
		 	if(!url){
		 		$body.data("loading" , false);
		 		return ;
		 	}
		 	options.url = url;
		 	this._showLoadingMessage();
		 	$.ajax(url , {
		 		cache: false,
		 		success: function(data, textStatus, jqXHR){
		 			$body.html(options.preProcess? options.preProcess.call($body[0] , data , textStatus) : data);
		 			$body.data("loading" , false);
		 			self._hideLoadingMessage();
		 			options.onSuccess && self._trigger("onSuccess", null, data, textStatus, jqXHR);
		 		},
		 		error: function(jqXHR, textStatus, errorThrown){
		 			$body.data("loading" , false);
		 			self._hideLoadingMessage();
		 			options.onError && self._trigger("onError", null, jqXHR, textStatus, errorThrown);
		 		}
		 	});
		},
		/**
		 * 改变组件的大小。
		 * @name omPanel#resize
		 * @function
		 * @param position (1)可以为Object,格式如{width:'100px',height:'100px'} <br/>
		 *                 (2)只有一个参数表示width,有两个参数时依次表示width,height
		 */
		resize: function(position){
		 	var options = this.options,
		 		width,
		 		height;
		 	if($.isPlainObject(position)){
		 		width = position.width || null;
		 		height = position.height || null;
		 	}else{
		 		width = arguments[0];
		 		height = arguments[1];
		 	}
		 	options.width = width || options.width;
		 	options.height = height || options.height;
		 	this._resize(this.element.parent());
		},
		/**
		 * 收起组件。
		 * @name omPanel#collapse
		 * @function
		 */
		collapse: function(/**anim , speed**/){
		 	var self = this,
		 		$body = this.element,
				$header = $body.prev(),
				$parent = $body.parent(),
				$loadMessage = $body.next(".om-panel-loadingMessage"),
				options = this.options,
				anim = effects.anim,
				speed = effects.speed;
				if(arguments[0] != undefined){//由于anim为boolean，所以不可以写成 anim = arguments[0] || effects.anim
					anim = arguments[0];//内部使用
				}
				speed = arguments[1] || speed;//内部使用
			if (options.onBeforeCollapse && self._trigger("onBeforeCollapse") === false) {
            	return ;
        	}
        	$parent.stop(true,true);
			if($header.length !== 0){
				var $expandTool = $header.find("> .om-panel-tool > div.om-panel-tool-collapse");
				if($expandTool.length !== 0){
					$expandTool.removeClass("om-panel-tool-collapse").addClass("om-panel-tool-expand");
					if($expandTool.hasClass("om-panel-tool-collapse-hover")){
						$expandTool.toggleClass("om-panel-tool-collapse-hover om-panel-tool-expand-hover");
					}
				}
			}

			$parent.animate({
					height: '-='+$body.outerHeight()
				} , 
				anim? (speed || 'normal') : 0 , 
				function(){
					$body.hide();
					$loadMessage.hide();
					"auto"===options.height && $parent.css("height" , "");//动画执行后parent会自动添加高度值，所以设置为"auto"时要手动去掉此高度
                	options.onCollapse && self._trigger("onCollapse");
				}
			);    
			options.collapsed = true;
		},
		/**
		 * 展开组件。
		 * @name omPanel#expand
		 * @function
		 */
		expand: function(/**anim , speed**/){
			var self = this,
				$body = this.element,
				$header = $body.prev(),
				$parent = $body.parent(),
				$loadMessage = $body.next(".om-panel-loadingMessage"),
				options = this.options,
				anim = effects.anim,
				speed = effects.speed;
				if(arguments[0] != undefined){//由于anim为boolean，所以不可以写成 anim = arguments[0] || effects.anim
					anim = arguments[0];//内部使用
				}
				speed = arguments[1] || speed;//内部使用
			if (options.onBeforeExpand && self._trigger("onBeforeExpand") === false) {
            	return ;
        	}
        	$parent.stop(true,true);
			if($header.length !== 0){
				var $expandTool = $header.find("> .om-panel-tool > div.om-panel-tool-expand");
				if($expandTool.length !== 0){
					$expandTool.removeClass("om-panel-tool-expand").addClass("om-panel-tool-collapse");
					if($expandTool.hasClass("om-panel-tool-expand-hover")){
						$expandTool.toggleClass("om-panel-tool-expand-hover om-panel-tool-collapse-hover");
					}
				}
			}
			//如果parent没有设置高度值，要设置一个，不然动画效果是出不来的
			"auto"===options.height && $parent.height($header.outerHeight());
			$body.show();
			if($body.data("loading")){
				$loadMessage.show();
			}
			$parent.animate({
					height: '+='+$body.outerHeight()
				} , 
				anim? (speed || 'normal') : 0 , 
				function(){
					"auto"===options.height && $parent.css("height" , "");//动画执行后parent会自动添加高度值，所以设置为"auto"时要手动去掉此高度
	                options.onExpand && self._trigger("onExpand");
				}
			);     
			options.collapsed = false;
		},
		/**
		 * 销毁组件
		 * @name omPanel#destroy
		 * @function
		 */
		destroy: function(){
			var $body = this.element;
			$body.parent().after($body).remove();
		}
	});
})(jQuery);/*
 * $Id: om-ajaxsubmit.js,v 1.14 2012/03/15 07:16:12 wangfan Exp $
 * operamasks-ui omAjaxSubmit @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 */
;(function($) {

/*
	Usage Note:
	-----------
	Do not use both omAjaxSubmit and ajaxForm on the same form.  These
	functions are intended to be exclusive.  Use omAjaxSubmit if you want
	to bind your own submit handler to the form.  For example,

	$(document).ready(function() {
		$('#myForm').bind('submit', function(e) {
			e.preventDefault(); // <-- important
			$(this).omAjaxSubmit({
				target: '#output'
			});
		});
	});

	Use ajaxForm when you want the plugin to manage all the event binding
	for you.  For example,

	$(document).ready(function() {
		$('#myForm').ajaxForm({
			target: '#output'
		});
	});

	When using ajaxForm, the omAjaxSubmit function will be invoked for you
	at the appropriate time.
*/

/**
 * @name omAjaxSubmit
 * @class
 * <div>
 * omAjaxSubmit() 提供使用ajax方式提交HTML form的一种机制。本插件会监听表单的submit事件，<br/>
 * 覆盖传统的submit事件监听器，而使用ajax方式来处理submit事件。在表单提交之前，本插件会收集<br/>
 * 所有的表单字段，并将之序列化后附加在ajax请求的数据域(data)中。支持所有标准的html可提交的<br/>
 * 表。元素。
 * </div><br/>
 * <b>事件回调</b><br/>
 * <div>
 * 通过丰富的配置参数，omAjaxSubmit可以高度自定制。同时提供多个事件回调函数，在每一次完整<br/>
 * 的表单提交的过程中，用户有时机能够对提交的请求进行修改。
 * </div>
 * <pre>beforeSerialize:</pre>
 * <div style="text-indent:2em;">在form序列化之前执行的回调函数。在获取所有form表单字段的值之前，该函数提供了一个操作form的时机</div>
 * <pre>beforeSubmit:</pre>
 * <div style="text-indent:2em;">在form被提交之前执行的回调函数。该函数提供了一个时机来执行预提交的逻辑，比如表单校验</div><br/>
 * <b>工具方法</b><br/>
 * <div>omAjaxSubmit还提供了一系列静态工具方法，用于方便地操作表单及其字段。</div>
 * <pre>$.fn.formToArray()</pre>
 * <div style="text-indent:2em;">将表单所有元素转换成key/value的数组，例如[{name:'username', value:'jack'},{name:'password', value:'secret'}]，<br/>
 * 注意:该数组将作为参数传递给beforeSubmit函数</div>
 * <pre>$.fn.formSerialize()</pre>
 * <div style="text-indent:2em;">将表单数据序列化成可提交的字符串，例如name1=value1&amp;name2=value2</div>
 * <pre>$.fn.fieldSerialize()</pre>
 * <div style="text-indent:2em;">将表单所有元素序列化成可提交的字符串，例如name1=value1&amp;name2=value2</div>
 * <pre>$.fn.fieldValue()</pre>
 * <div style="text-indent:2em;">获取当前元素(或元素数组)的值</div>
 * <pre>$.fieldValue(successful)</pre>
 * <div style="text-indent:2em;">静态工具方法，用于获取元素的值，参数successful的意义同上</div>
 * <pre>$.fn.clearForm()</pre>
 * <div style="text-indent:2em;">清空当前表单各个元素的值</div>
 * <pre>$.fn.clearFields()</pre>
 * <div style="text-indent:2em;">清空当前元素(或元素数组)的值</div>
 * <pre>$.fn.resetForm()</pre>
 * <div style="text-indent:2em;">重置当前表单各个元素的值</div>
 * <pre>$.fn.enable(b)</pre>
 * <div style="text-indent:2em;">设置当前元素(或元素数组)的使能状态</div>
 * <pre>$.fn.selected(selected)</pre>
 * <div style="text-indent:2em;">设置当前元素(或元素数组)的选中状态</div><br/>
 * <b>示例</b><br/>
 * <pre>
 *  $(document).ready(function() {
 *      $('#myForm').bind('submit', function(e) {
 *          e.preventDefault(); //阻止form默认的提交行为
 *              $(this).omAjaxSubmit(//使用ajax提交
 *                  {
 *                      target: '#output'
 *                  }
 *              );
 *      });
 *  });
 * 
 * </pre>
 * @constructor
 * @param options 标准config对象
 * @example
 * 	$('#formId').omAjaxSubmit({target: '#output'});
 */
$.fn.omAjaxSubmit = function(options) {
	// fast fail if nothing selected (http://dev.jquery.com/ticket/2752)
	if (!this.length) {
		log('omAjaxSubmit: skipping submit process - no element selected');
		return this;
	}
	
	var method, action, url, $form = this;

	if (typeof options == 'function') {
		options = { success: options };
	}

	method = this.attr('method');
	action = this.attr('action');

	url = (typeof action === 'string') ? $.trim(action) : '';
	url = url || window.location.href || '';
	if (url) {
		// clean url (don't include hash vaue)
		url = (url.match(/^([^#]+)/)||[])[1];
	}

	options = $.extend(true, {
        /**
         * 表单提交的url。
         * @name omAjaxSubmit#url
         * @type String
         * @default form的action属性值
         * @example
         * $('#formId').omAjaxSubmit({url : 'result.jsp'});
         */
		url:  url,
        /**
         * 当表单提交成功并取到响应时，执行的回调函数。
         * @name omAjaxSubmit#success
         * @param responseText 响应的文本。具体的取值根据options中的dataType有关，请参考dataType属性的说明文档。
         * @param statusText 响应的状态，在该回调中，常见的取值为success
         * @param xhr XMLHttpRequest对象
         * @param $form 经过jQuery包装的form对象
         * @event
         * @default 无
         * @example
         * //定义一个函数
         * function showResponse(responseText, statusText, xhr, $form) {
         *  alert('submit success!');
         * }
         * //提交成功取到响应时的回调函数
         * $('#formId').omAjaxSubmit({success: showResponse});
         */
		success: $.ajaxSettings.success,
        /**
         * 表单的提交方法，取值为：'GET' 或者 'POST'。
         * @name omAjaxSubmit#method
         * @type String
         * @default 'GET'
         * @example
         * $('#formId').omAjaxSubmit({method:'POST'});
         */
		method: method || 'GET',
        /**
         * iframe的src属性值，该属性在页面中有iframe的时候才用得到，通常此时form中有文件需要上传。<br/>
         * 默认值是about:blank ，如果当前页面地址使用 https 协议，则该值为javascript:false
         * @blocked
         */
		iframeSrc: /^https/i.test(window.location.href || '') ? 'javascript:false' : 'about:blank'
	}, options);

	// hook for manipulating the form data before it is extracted;
	// convenient for use with rich editors like tinyMCE or FCKEditor
	var veto = {};
	this.trigger('form-pre-serialize', [this, options, veto]);
	if (veto.veto) {
		log('omAjaxSubmit: submit vetoed via form-pre-serialize trigger');
		return this;
	}

    /**
     * 在form序列化之前执行的回调函数。在获取form表单元素的值之前，该函数提供了一个操作form的时机。<br/>
     * 该函数接受2个参数<br/>
     * @name omAjaxSubmit#beforeSerialize
     * @event
     * @param $form form对应的jQuery对象
     * @param options 传给ajaxSubmit的options对象
     * @return false 取消form的提交
     * @example
     * beforeSerialize: function($form, options) { 
     *     // return false to cancel submit                  
     * }
     */
	// provide opportunity to alter form data before it is serialized
	if (options.beforeSerialize && options.beforeSerialize(this, options) === false) {
		log('omAjaxSubmit: submit aborted via beforeSerialize callback');
		return this;
	}

    /**
     * 
     * 是否以严格的语义化顺序提交form表单元素。同时，设置了该属性会忽略表单中的image标签。<br/>
     * 该属性一般不用设置(默认为false)。只有当你的服务器对semantic order有严格要求，<br/>
     * 并且你的表单中含有image时，你才需要设置它为true<br/>
     * @blocked
     */
	var n,v,a = this.formToArray(options.semantic);
    /**
     * ajax提交中的附加数据，以JSON的形式组成(key/value)。如果value是数组，将会被展开;如果value是函数，将会被求值。
     * @type JSON
     * @name omAjaxSubmit#data
     * @default 无
     * @example
     * data: { key1: 'value1', key2: 'value2' }
     */
	if (options.data) {
		options.extraData = options.data;
		for (n in options.data) {
			if(options.data[n] instanceof Array) {
				for (var k in options.data[n]) {
					a.push( { name: n, value: options.data[n][k] } );
				}
			}
			else {
				v = options.data[n];
				v = $.isFunction(v) ? v() : v; // if value is fn, invoke it
				a.push( { name: n, value: v } );
			}
		}
	}

    /**
     * 在form被提交之前执行的回调函数。该函数提供了一个时机来执行预提交的逻辑，或者可以用来进行校验表单元素。<br/>
     * 该函数接受3个参数:arr, $form, options。<br/>
     * 若函数返回false，则会取消form的提交。<br/>
     * @name omAjaxSubmit#beforeSubmit
     * @type Function
     * @event
     * @param arr 一个数组，包含form所有字段的key/value值，例如: [{key:value},{key1:value1},{key2:value2}]
     * @param $form form对应的jQuery对象
     * @param options 传递给ajaxSubmit的options参数
     * @return false 取消提交表单
     * @example
     * beforeSubmit: function(arr, $form, options) { 
     *     // The array of form data takes the following form: 
     *     // [ { name: 'username', value: 'jresig' }, { name: 'password', value: 'secret' } ] 
     *     // return false to cancel submit                  
     * }
     */
	// give pre-submit callback an opportunity to abort the submit
	if (options.beforeSubmit && options.beforeSubmit(a, this, options) === false) {
		log('omAjaxSubmit: submit aborted via beforeSubmit callback');
		return this;
	}

	// fire vetoable 'validate' event
	this.trigger('form-submit-validate', [a, this, options, veto]);
	if (veto.veto) {
		log('omAjaxSubmit: submit vetoed via form-submit-validate trigger');
		return this;
	}

	var q = $.param(a);

	if (options.method.toUpperCase() == 'GET') {
		options.url += (options.url.indexOf('?') >= 0 ? '&' : '?') + q;
		options.data = null;  // data is null for 'get'
	}
	else {
		options.data = q; // data is the query string for 'post'
	}

    var callbacks = [];
    /**
     * 在form成功提交后，是否将form字段重置
     * @name omAjaxSubmit#resetForm
     * @type Boolean
     * @default false
     * @example
     * //提交后重置表单字段
     * $('#formId').omAjaxSubmit({resetForm: true});
     */
	if (options.resetForm) {
		callbacks.push(function() { $form.resetForm(); });
	}
    /**
     * 在form成功提交后，是否将form字段清空。<br/>
     * @name omAjaxSubmit#clearForm
     * @type Boolean
     * @default false
     * @example
     * $('#formId').omAjaxSubmit({clearForm: true});
     */
	if (options.clearForm) {
		callbacks.push(function() { $form.clearForm(); });
	}

    /**
     * 响应的数据格式，可选的取值为'xml'， 'script'， 'json'或者null。该选项表明了响应将要被如何处理。<br/>
     * 与jQuery.httpData一一对应，其各种取值情况处理如下：<br/>
     * <pre>
     *      'xml':	响应将会被认为是xml格式的，并作为第一个参数传递给success回调函数
     *      'json':	响应将会被认为是json格式的，其将会被求值，结果将会作为第一个参数传递给success回调函数
     *      'script':响应将会被认为是js脚本，其将在全局上下文中被执行
     * </pre>
     * @name omAjaxSubmit#dataType
     * @type String
     * @default 无
     * @example
     * $('#formId').omAjaxSubmit({dataType : 'json'}); 
     */
    /**
     * 指定了一个更新区域，该区域将会被ajax响应更新。<br/>
     * 该值可以是DOM元素，jQuery对象，或者一个可以被jQuery选择到的选择器。
     * @name omAjaxSubmit#target
     * @type DOM, jQuery, or String
     * @default 无
     * @example
     * $('#formId').omAjaxSubmit({target : '#targetDivId'});
     */
	// perform a load on the target only if dataType is not provided
	if (!options.dataType && options.target) {
		var oldSuccess = options.success || function(){};
		callbacks.push(function(data) {
            /**
             * 可选配置，是否替换target指定的区域。<br/>
             * 设为true将会整体替换target对应的DOM节点，设为false将只会替换节点的内容。<br/>
             * @name omAjaxSubmit#replaceTarget
             * @type Boolean 
             * @default false
             * @example
             * $('#formId').omAjaxSubmit({replaceTarget : true});
             */
			var fn = options.replaceTarget ? 'replaceWith' : 'html';
			$(options.target)[fn](data).each(oldSuccess, arguments);
		});
	}
	else if (options.success) {
		callbacks.push(options.success);
	}

	options.success = function(data, status, xhr) { // jQuery 1.4+ passes xhr as 3rd arg
		var context = options.context || options;   // jQuery 1.4+ supports scope context 
		for (var i=0, max=callbacks.length; i < max; i++) {
			callbacks[i].apply(context, [data, status, xhr || $form, $form]);
		}
	};

	// are there files to upload?
	var fileInputs = $('input:file', this).length > 0;
	var mp = 'multipart/form-data';
	var multipart = ($form.attr('enctype') == mp || $form.attr('encoding') == mp);

    /**
     * 是否总是将form的响应指向一个iframe，该属性在有文件上传的情况下有用。
     * @blocked
     */
	// options.iframe allows user to force iframe mode
	// 06-NOV-09: now defaulting to iframe mode if file input is detected
   if (options.iframe !== false && (fileInputs || options.iframe || multipart)) {
	   // hack to fix Safari hang (thanks to Tim Molendijk for this)
	   // see:  http://groups.google.com/group/jquery-dev/browse_thread/thread/36395b7ab510dd5d
	   if (options.closeKeepAlive) {
		   $.get(options.closeKeepAlive, function() { fileUpload(a); });
		}
	   else {
		   fileUpload(a);
		}
   }
   else {
		// IE7 massage (see issue 57)
		if ($.browser.msie && method == 'get') { 
			var ieMeth = $form[0].getAttribute('method');
			if (typeof ieMeth === 'string')
				options.method = ieMeth;
		}
		options.type = options.method;
		$.ajax(options);
   }

	// fire 'notify' event
	this.trigger('form-submit-notify', [this, options]);
	return this;


	// private function for handling file uploads (hat tip to YAHOO!)
	function fileUpload(a) {
		var form = $form[0], el, i, s, g, id, $io, io, xhr, sub, n, timedOut, timeoutHandle;
        var useProp = !!$.fn.prop;

        if (a) {
        	// ensure that every serialized input is still enabled
          	for (i=0; i < a.length; i++) {
                el = $(form[a[i].name]);
                el[ useProp ? 'prop' : 'attr' ]('disabled', false);
          	}
        }

		if ($(':input[name=submit],:input[id=submit]', form).length) {
			// if there is an input with a name or id of 'submit' then we won't be
			// able to invoke the submit fn on the form (at least not x-browser)
			alert('Error: Form elements must not have name or id of "submit".');
			return;
		}
		
		s = $.extend(true, {}, $.ajaxSettings, options);
		s.context = s.context || s;
		id = 'jqFormIO' + (new Date().getTime());
		/**
		 * 指定一个iframe元素。当本插件处理有文件上传的form时，一般会临时创建一个隐藏的iframe来接收响应。<br/>
		 * 配置该属性，用户可以使用一个已存在的iframe，而不是使用临时iframe。<br/>
		 * 注意使用该属性后，本插件不会再去尝试处理服务器的响应。<br/>
		 * @blocked
         */
		if (s.iframeTarget) {
			$io = $(s.iframeTarget);
			n = $io.attr('name');
			if (n == null)
			 	$io.attr('name', id);
			else
				id = n;
		}
		else {
			$io = $('<iframe name="' + id + '" src="'+ s.iframeSrc +'" />');
			$io.css({ position: 'absolute', top: '-1000px', left: '-1000px' });
		}
		io = $io[0];


		xhr = { // mock object
			aborted: 0,
			responseText: null,
			responseXML: null,
			status: 0,
			statusText: 'n/a',
			getAllResponseHeaders: function() {},
			getResponseHeader: function() {},
			setRequestHeader: function() {},
			abort: function(status) {
				var e = (status === 'timeout' ? 'timeout' : 'aborted');
				log('aborting upload... ' + e);
				this.aborted = 1;
				$io.attr('src', s.iframeSrc); // abort op in progress
				xhr.error = e;
				s.error && s.error.call(s.context, xhr, e, status);
				g && $.event.trigger("ajaxError", [xhr, s, e]);
				s.complete && s.complete.call(s.context, xhr, e);
			}
		};

		g = s.global;
		// trigger ajax global events so that activity/block indicators work like normal
		if (g && ! $.active++) {
			$.event.trigger("ajaxStart");
		}
		if (g) {
			$.event.trigger("ajaxSend", [xhr, s]);
		}

		if (s.beforeSend && s.beforeSend.call(s.context, xhr, s) === false) {
			if (s.global) {
				$.active--;
			}
			return;
		}
		if (xhr.aborted) {
			return;
		}

		// add submitting element to data if we know it
		sub = form.clk;
		if (sub) {
			n = sub.name;
			if (n && !sub.disabled) {
				s.extraData = s.extraData || {};
				s.extraData[n] = sub.value;
				if (sub.type == "image") {
					s.extraData[n+'.x'] = form.clk_x;
					s.extraData[n+'.y'] = form.clk_y;
				}
			}
		}
		
		var CLIENT_TIMEOUT_ABORT = 1;
		var SERVER_ABORT = 2;

		function getDoc(frame) {
			var doc = frame.contentWindow ? frame.contentWindow.document : frame.contentDocument ? frame.contentDocument : frame.document;
			return doc;
		}
		
		// take a breath so that pending repaints get some cpu time before the upload starts
		function doSubmit() {
			// make sure form attrs are set
			var t = $form.attr('target'), a = $form.attr('action');

			// update form attrs in IE friendly way
			form.setAttribute('target',id);
			if (!method) {
				form.setAttribute('method', 'POST');
			}
			if (a != s.url) {
				form.setAttribute('action', s.url);
			}

			// ie borks in some cases when setting encoding
			if (! s.skipEncodingOverride && (!method || /post/i.test(method))) {
				$form.attr({
					encoding: 'multipart/form-data',
					enctype:  'multipart/form-data'
				});
			}

			// support timout
			if (s.timeout) {
				timeoutHandle = setTimeout(function() { timedOut = true; cb(CLIENT_TIMEOUT_ABORT); }, s.timeout);
			}
			
			// look for server aborts
			function checkState() {
				try {
					var state = getDoc(io).readyState;
					log('state = ' + state);
					if (state.toLowerCase() == 'uninitialized')
						setTimeout(checkState,50);
				}
				catch(e) {
					log('Server abort: ' , e, ' (', e.name, ')');
					cb(SERVER_ABORT);
					timeoutHandle && clearTimeout(timeoutHandle);
					timeoutHandle = undefined;
				}
			}

			// add "extra" data to form if provided in options
			var extraInputs = [];
			try {
				if (s.extraData) {
					for (var n in s.extraData) {
						extraInputs.push(
							$('<input type="hidden" name="'+n+'" />').attr('value',s.extraData[n])
								.appendTo(form)[0]);
					}
				}

				if (!s.iframeTarget) {
					// add iframe to doc and submit the form
					$io.appendTo('body');
	                io.attachEvent ? io.attachEvent('onload', cb) : io.addEventListener('load', cb, false);
				}
				setTimeout(checkState,15);
				form.submit();
			}
			finally {
				// reset attrs and remove "extra" input elements
				form.setAttribute('action',a);
				if(t) {
					form.setAttribute('target', t);
				} else {
					$form.removeAttr('target');
				}
				$(extraInputs).remove();
			}
		}
        /**
         * 强制同步，如果设置了该属性，在form提交时将立即进行文件上传，否则在提交表单后会延迟10毫秒再进行文件上传。<br/>
         * 在延迟的这一短暂时间里，浏览器有机会更新DOM结构，比如想要向用户展示"请稍后..."的提示。<br/>
         * 显示这些的提示是需要时间来更新DOM结构的。暂停这一会儿时间，再真正提交表单，可以增加易用性。<br/>
         * @blocked
         */
		if (s.forceSync) {
			doSubmit();
		}
		else {
			setTimeout(doSubmit, 10); // this lets dom updates render
		}

		var data, doc, domCheckCount = 50, callbackProcessed;

		function cb(e) {
			if (xhr.aborted || callbackProcessed) {
				return;
			}
			try {
				doc = getDoc(io);
			}
			catch(ex) {
				log('cannot access response document: ', ex);
				e = SERVER_ABORT;
			}
			if (e === CLIENT_TIMEOUT_ABORT && xhr) {
				xhr.abort('timeout');
				return;
			}
			else if (e == SERVER_ABORT && xhr) {
				xhr.abort('server abort');
				return;
			}

			if (!doc || doc.location.href == s.iframeSrc) {
				// response not received yet
				if (!timedOut)
					return;
			}
            io.detachEvent ? io.detachEvent('onload', cb) : io.removeEventListener('load', cb, false);

			var status = 'success', errMsg;
			try {
				if (timedOut) {
					throw 'timeout';
				}

				var isXml = s.dataType == 'xml' || doc.XMLDocument || $.isXMLDoc(doc);
				log('isXml='+isXml);
				if (!isXml && window.opera && (doc.body == null || doc.body.innerHTML == '')) {
					if (--domCheckCount) {
						// in some browsers (Opera) the iframe DOM is not always traversable when
						// the onload callback fires, so we loop a bit to accommodate
						log('requeing onLoad callback, DOM not available');
						setTimeout(cb, 250);
						return;
					}
					// let this fall through because server response could be an empty document
					//log('Could not access iframe DOM after mutiple tries.');
					//throw 'DOMException: not available';
				}

				//log('response detected');
                var docRoot = doc.body ? doc.body : doc.documentElement;
                xhr.responseText = docRoot ? docRoot.innerHTML : null;
				xhr.responseXML = doc.XMLDocument ? doc.XMLDocument : doc;
				if (isXml)
					s.dataType = 'xml';
				xhr.getResponseHeader = function(header){
					var headers = {'content-type': s.dataType};
					return headers[header];
				};
                // support for XHR 'status' & 'statusText' emulation :
                if (docRoot) {
                    xhr.status = Number( docRoot.getAttribute('status') ) || xhr.status;
                    xhr.statusText = docRoot.getAttribute('statusText') || xhr.statusText;
                }

				var dt = s.dataType || '';
				var scr = /(json|script|text)/.test(dt.toLowerCase());
				if (scr || s.textarea) {
					// see if user embedded response in textarea
					var ta = doc.getElementsByTagName('textarea')[0];
					if (ta) {
						xhr.responseText = ta.value;
                        // support for XHR 'status' & 'statusText' emulation :
                        xhr.status = Number( ta.getAttribute('status') ) || xhr.status;
                        xhr.statusText = ta.getAttribute('statusText') || xhr.statusText;
					}
					else if (scr) {
						// account for browsers injecting pre around json response
						var pre = doc.getElementsByTagName('pre')[0];
						var b = doc.getElementsByTagName('body')[0];
						if (pre) {
							xhr.responseText = pre.textContent ? pre.textContent : pre.innerHTML;
						}
						else if (b) {
							xhr.responseText = b.innerHTML;
						}
					}
				}
				else if (s.dataType == 'xml' && !xhr.responseXML && xhr.responseText != null) {
					xhr.responseXML = toXml(xhr.responseText);
				}

                try {
                    data = httpData(xhr, s.dataType, s);
                }
                catch (e) {
                    status = 'parsererror';
                    xhr.error = errMsg = (e || status);
                }
			}
			catch (e) {
				log('error caught: ',e);
				status = 'error';
                xhr.error = errMsg = (e || status);
			}

			if (xhr.aborted) {
				log('upload aborted');
				status = null;
			}

            if (xhr.status) { // we've set xhr.status
                status = (xhr.status >= 200 && xhr.status < 300 || xhr.status === 304) ? 'success' : 'error';
            }

			// ordering of these callbacks/triggers is odd, but that's how $.ajax does it
			if (status === 'success') {
				s.success && s.success.call(s.context, data, 'success', xhr);
				g && $.event.trigger("ajaxSuccess", [xhr, s]);
			}
            else if (status) {
				if (errMsg == undefined)
					errMsg = xhr.statusText;
				s.error && s.error.call(s.context, xhr, status, errMsg);
				g && $.event.trigger("ajaxError", [xhr, s, errMsg]);
            }

			g && $.event.trigger("ajaxComplete", [xhr, s]);

			if (g && ! --$.active) {
				$.event.trigger("ajaxStop");
			}

			s.complete && s.complete.call(s.context, xhr, status);

			callbackProcessed = true;
			if (s.timeout)
				clearTimeout(timeoutHandle);

			// clean up
			setTimeout(function() {
				if (!s.iframeTarget)
					$io.remove();
				xhr.responseXML = null;
			}, 100);
		}

		var toXml = $.parseXML || function(s, doc) { // use parseXML if available (jQuery 1.5+)
			if (window.ActiveXObject) {
				doc = new ActiveXObject('Microsoft.XMLDOM');
				doc.async = 'false';
				doc.loadXML(s);
			}
			else {
				doc = (new DOMParser()).parseFromString(s, 'text/xml');
			}
			return (doc && doc.documentElement && doc.documentElement.nodeName != 'parsererror') ? doc : null;
		};
		var parseJSON = $.parseJSON || function(s) {
			return window['eval']('(' + s + ')');
		};

		var httpData = function( xhr, type, s ) { // mostly lifted from jq1.4.4

			var ct = xhr.getResponseHeader('content-type') || '',
				xml = type === 'xml' || !type && ct.indexOf('xml') >= 0,
				data = xml ? xhr.responseXML : xhr.responseText;

			if (xml && data.documentElement.nodeName === 'parsererror') {
				$.error && $.error('parsererror');
			}
			if (s && s.dataFilter) {
				data = s.dataFilter(data, type);
			}
			if (typeof data === 'string') {
				if (type === 'json' || !type && ct.indexOf('json') >= 0) {
					data = parseJSON(data);
				} else if (type === "script" || !type && ct.indexOf("javascript") >= 0) {
					$.globalEval(data);
				}
			}
			return data;
		};
	}
};

/**
 * ajaxForm() provides a mechanism for fully automating form submission.
 *
 * The advantages of using this method instead of omAjaxSubmit() are:
 *
 * 1: This method will include coordinates for <input type="image" /> elements (if the element
 *	is used to submit the form).
 * 2. This method will include the submit element's name/value data (for the element that was
 *	used to submit the form).
 * 3. This method binds the submit() method to the form for you.
 *
 * The options argument for ajaxForm works exactly as it does for omAjaxSubmit.  ajaxForm merely
 * passes the options argument along after properly binding events for submit elements and
 * the form itself.
 */
$.fn.ajaxForm = function(options) {
	// in jQuery 1.3+ we can fix mistakes with the ready state
	if (this.length === 0) {
		var o = { s: this.selector, c: this.context };
		if (!$.isReady && o.s) {
			log('DOM not ready, queuing ajaxForm');
			$(function() {
				$(o.s,o.c).ajaxForm(options);
			});
			return this;
		}
		// is your DOM ready?  http://docs.jquery.com/Tutorials:Introducing_$(document).ready()
		log('terminating; zero elements found by selector' + ($.isReady ? '' : ' (DOM not ready)'));
		return this;
	}

	return this.ajaxFormUnbind().bind('submit.form-plugin', function(e) {
		if (!e.isDefaultPrevented()) { // if event has been canceled, don't proceed
			e.preventDefault();
			$(this).omAjaxSubmit(options);
		}
	}).bind('click.form-plugin', function(e) {
		var target = e.target;
		var $el = $(target);
		if (!($el.is(":submit,input:image"))) {
			// is this a child element of the submit el?  (ex: a span within a button)
			var t = $el.closest(':submit');
			if (t.length == 0) {
				return;
			}
			target = t[0];
		}
		var form = this;
		form.clk = target;
		if (target.type == 'image') {
			if (e.offsetX != undefined) {
				form.clk_x = e.offsetX;
				form.clk_y = e.offsetY;
			} else if (typeof $.fn.offset == 'function') { // try to use dimensions plugin
				var offset = $el.offset();
				form.clk_x = e.pageX - offset.left;
				form.clk_y = e.pageY - offset.top;
			} else {
				form.clk_x = e.pageX - target.offsetLeft;
				form.clk_y = e.pageY - target.offsetTop;
			}
		}
		// clear form vars
		setTimeout(function() { form.clk = form.clk_x = form.clk_y = null; }, 100);
	});
};

// ajaxFormUnbind unbinds the event handlers that were bound by ajaxForm
$.fn.ajaxFormUnbind = function() {
	return this.unbind('submit.form-plugin click.form-plugin');
};

/**
 * formToArray() gathers form element data into an array of objects that can
 * be passed to any of the following ajax functions: $.get, $.post, or load.
 * Each object in the array has both a 'name' and 'value' property.  An example of
 * an array for a simple login form might be:
 *
 * [ { name: 'username', value: 'jresig' }, { name: 'password', value: 'secret' } ]
 *
 * It is this array that is passed to pre-submit callback functions provided to the
 * omAjaxSubmit() and ajaxForm() methods.
 */
$.fn.formToArray = function(semantic) {
	var a = [];
	if (this.length === 0) {
		return a;
	}

	var form = this[0];
	var els = semantic ? form.getElementsByTagName('*') : form.elements;
	if (!els) {
		return a;
	}

	var i,j,n,v,el,max,jmax;
	for(i=0, max=els.length; i < max; i++) {
		el = els[i];
		n = el.name;
		if (!n) {
			continue;
		}

		if (semantic && form.clk && el.type == "image") {
			// handle image inputs on the fly when semantic == true
			if(!el.disabled && form.clk == el) {
				a.push({name: n, value: $(el).val()});
				a.push({name: n+'.x', value: form.clk_x}, {name: n+'.y', value: form.clk_y});
			}
			continue;
		}

		v = $.fieldValue(el, true);
		if (v && v.constructor == Array) {
			for(j=0, jmax=v.length; j < jmax; j++) {
				a.push({name: n, value: v[j]});
			}
		}
		else if (v !== null && typeof v != 'undefined') {
			a.push({name: n, value: v});
		}
	}

	if (!semantic && form.clk) {
		// input type=='image' are not found in elements array! handle it here
		var $input = $(form.clk), input = $input[0];
		n = input.name;
		if (n && !input.disabled && input.type == 'image') {
			a.push({name: n, value: $input.val()});
			a.push({name: n+'.x', value: form.clk_x}, {name: n+'.y', value: form.clk_y});
		}
	}
	return a;
};

/**
 * Serializes form data into a 'submittable' string. This method will return a string
 * in the format: name1=value1&amp;name2=value2
 */
$.fn.formSerialize = function(semantic) {
	//hand off to jQuery.param for proper encoding
	return $.param(this.formToArray(semantic));
};

/**
 * Serializes all field elements in the jQuery object into a query string.
 * This method will return a string in the format: name1=value1&amp;name2=value2
 */
$.fn.fieldSerialize = function(successful) {
	var a = [];
	this.each(function() {
		var n = this.name;
		if (!n) {
			return;
		}
		var v = $.fieldValue(this, successful);
		if (v && v.constructor == Array) {
			for (var i=0,max=v.length; i < max; i++) {
				a.push({name: n, value: v[i]});
			}
		}
		else if (v !== null && typeof v != 'undefined') {
			a.push({name: this.name, value: v});
		}
	});
	//hand off to jQuery.param for proper encoding
	return $.param(a);
};

/**
 * Returns the value(s) of the element in the matched set.  For example, consider the following form:
 *
 *  <form><fieldset>
 *	  <input name="A" type="text" />
 *	  <input name="A" type="text" />
 *	  <input name="B" type="checkbox" value="B1" />
 *	  <input name="B" type="checkbox" value="B2"/>
 *	  <input name="C" type="radio" value="C1" />
 *	  <input name="C" type="radio" value="C2" />
 *  </fieldset></form>
 *
 *  var v = $(':text').fieldValue();
 *  // if no values are entered into the text inputs
 *  v == ['','']
 *  // if values entered into the text inputs are 'foo' and 'bar'
 *  v == ['foo','bar']
 *
 *  var v = $(':checkbox').fieldValue();
 *  // if neither checkbox is checked
 *  v === undefined
 *  // if both checkboxes are checked
 *  v == ['B1', 'B2']
 *
 *  var v = $(':radio').fieldValue();
 *  // if neither radio is checked
 *  v === undefined
 *  // if first radio is checked
 *  v == ['C1']
 *
 * The successful argument controls whether or not the field element must be 'successful'
 * (per http://www.w3.org/TR/html4/interact/forms.html#successful-controls).
 * The default value of the successful argument is true.  If this value is false the value(s)
 * for each element is returned.
 *
 * Note: This method *always* returns an array.  If no valid value can be determined the
 *	   array will be empty, otherwise it will contain one or more values.
 */
$.fn.fieldValue = function(successful) {
	for (var val=[], i=0, max=this.length; i < max; i++) {
		var el = this[i];
		var v = $.fieldValue(el, successful);
		if (v === null || typeof v == 'undefined' || (v.constructor == Array && !v.length)) {
			continue;
		}
		v.constructor == Array ? $.merge(val, v) : val.push(v);
	}
	return val;
};

/**
 * Returns the value of the field element.
 */
$.fieldValue = function(el, successful) {
	var n = el.name, t = el.type, tag = el.tagName.toLowerCase();
	if (successful === undefined) {
		successful = true;
	}

	if (successful && (!n || el.disabled || t == 'reset' || t == 'button' ||
		(t == 'checkbox' || t == 'radio') && !el.checked ||
		(t == 'submit' || t == 'image') && el.form && el.form.clk != el ||
		tag == 'select' && el.selectedIndex == -1)) {
			return null;
	}

	if (tag == 'select') {
		var index = el.selectedIndex;
		if (index < 0) {
			return null;
		}
		var a = [], ops = el.options;
		var one = (t == 'select-one');
		var max = (one ? index+1 : ops.length);
		for(var i=(one ? index : 0); i < max; i++) {
			var op = ops[i];
			if (op.selected) {
				var v = op.value;
				if (!v) { // extra pain for IE...
					v = (op.attributes && op.attributes['value'] && !(op.attributes['value'].specified)) ? op.text : op.value;
				}
				if (one) {
					return v;
				}
				a.push(v);
			}
		}
		return a;
	}
	return $(el).val();
};

/**
 * Clears the form data.  Takes the following actions on the form's input fields:
 *  - input text fields will have their 'value' property set to the empty string
 *  - select elements will have their 'selectedIndex' property set to -1
 *  - checkbox and radio inputs will have their 'checked' property set to false
 *  - inputs of type submit, button, reset, and hidden will *not* be effected
 *  - button elements will *not* be effected
 */
$.fn.clearForm = function() {
	return this.each(function() {
		$('input,select,textarea', this).clearFields();
	});
};

/**
 * Clears the selected form elements.
 */
$.fn.clearFields = $.fn.clearInputs = function() {
	var re = /^(?:color|date|datetime|email|month|number|password|range|search|tel|text|time|url|week)$/i; // 'hidden' is not in this list
	return this.each(function() {
		var t = this.type, tag = this.tagName.toLowerCase();
		if (re.test(t) || tag == 'textarea') {
			this.value = '';
		}
		else if (t == 'checkbox' || t == 'radio') {
			this.checked = false;
		}
		else if (tag == 'select') {
			this.selectedIndex = -1;
		}
	});
};

/**
 * Resets the form data.  Causes all form elements to be reset to their original value.
 */
$.fn.resetForm = function() {
	return this.each(function() {
		// guard against an input with the name of 'reset'
		// note that IE reports the reset function as an 'object'
		if (typeof this.reset == 'function' || (typeof this.reset == 'object' && !this.reset.nodeType)) {
			this.reset();
		}
	});
};

/**
 * Enables or disables any matching elements.
 */
$.fn.enable = function(b) {
	if (b === undefined) {
		b = true;
	}
	return this.each(function() {
		this.disabled = !b;
	});
};

/**
 * Checks/unchecks any matching checkboxes or radio buttons and
 * selects/deselects and matching option elements.
 */
$.fn.selected = function(select) {
	if (select === undefined) {
		select = true;
	}
	return this.each(function() {
		var t = this.type;
		if (t == 'checkbox' || t == 'radio') {
			this.checked = select;
		}
		else if (this.tagName.toLowerCase() == 'option') {
			var $sel = $(this).parent('select');
			if (select && $sel[0] && $sel[0].type == 'select-one') {
				// deselect all other options
				$sel.find('option').selected(false);
			}
			this.selected = select;
		}
	});
};

// helper fn for console logging
function log() {
	var msg = '[jquery.form] ' + Array.prototype.join.call(arguments,'');
	if (window.console && window.console.log) {
		window.console.log(msg);
	}
	else if (window.opera && window.opera.postError) {
		window.opera.postError(msg);
	}
};

})(jQuery);/*
 * $Id: om-button.js,v 1.60 2012/06/20 09:03:12 luoyegang Exp $
 * operamasks-ui omButton @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 *
 * Depends:
 *  om-core.js
 */
;(function($) {
	/**
     * @name omButton
     * @class 按钮组件。类似于html中的button、input[type=submit]、input[type=button]，使用背景图片实现圆角，支持icon，可以只显示icon。<br/><br/>
     * <b>特点：</b><br/>
     * <ol>
     *      <li>实现圆角</li>
     *      <li>支持左icon和右icon，可同时出现左右icon，也可以只显示icon不显示label</li>
     *      <li>按钮文字数目不限，也可以任意设置按钮宽度</li>
     *      <li>支持input[type=button]、input[type=submit]、input[type=reset]、button、a五种标签形式</li>
     * </ol>
     * 
     * <b>示例：</b><br/>
     * <pre>
     * &lt;script type="text/javascript" &gt;
     * $(document).ready(function() {
     *     $('#bnt').omButton({
     *         icons : {left:'images/help.png',right:'images/edit_add.png'},
     *         width : 150,
     *         disabled : 'disabled',
     *         onClick : function(event){
     *             // do something
     *         }
     *     });
     * });
     * &lt;/script&gt;
     * 
     * &lt;input id="btn" type="submit" /&gt;
     * </pre>
     * 
     * @constructor
     * @description 构造函数. 
     * @param p 标准config对象：{}
     */
    $.omWidget('om.omButton', {
    	
        options: /**@lends omButton# */{
        	/**
        	 * 是否禁用组件。可通过('#id').attr('disabled')判断按钮是否禁用。
             * @type String 
             * @default null 
             * @example
             * $("#button").omButton({disabled: true}); 
             */
            disabled : null ,
            /**
             * 显示文本。label值可以写到dom元素里面，也可以设置在属性里面，设置到属性里面的优先级最高。
             * @type String 
             * @default ""
             * @example
             * $("#button").omButton({label: "apusic"});
             */
            label : null ,
            /**
        	 * 显示按钮图标，left表示左图标，right表示右图标，取值均为图片路径。
             * @type Object 
             * @default null 
             * @example
             * $("#button").omButton({
             *     icons: {
             *         left: 'images/help.png',
             *         right: 'images/edit_add.png'
             *     }
             * });
             */
            icons: {
    			left: null,
    			right: null
    		},
    		/**
        	 * 按钮宽度，设置固定宽度之后按钮将不会随文字的多少而改变。
             * @type Number 
             * @default null 
             * @example
             * width : 150
             */
            width : null ,
            /**
        	 * 点击按钮时触发的事件。
             * @event
             * @param event jQuery.Event对象。
             * @name omButton#onClick 
             * @example
             * onClick : function(event){
             *    //do something
             * }
             */
            onClick : null
        },
        
        _create : function() {
            this._determineButtonType();
            var wrapperSpan = $( '<span>' ).addClass( 'om-btn om-state-default').css('border','none'),
            leftSpan = $( '<span>' ).addClass( 'om-btn-bg om-btn-left' ),
            centerSpan = $( '<span>' ).addClass( 'om-btn-bg om-btn-center' ),
            rightSpan = $( '<span>' ).addClass( 'om-btn-bg om-btn-right');
            if(this.element.hasClass('apusic-btn-deepblue')){
            	wrapperSpan.addClass('apusic-btn-deepblue');
            }
            
            this.element.addClass( 'om-btn-txt' )
                .css( {'background-position':'left center','background-repeat':'no-repeat'} )
                .wrap( wrapperSpan )
                .before( leftSpan )
                .after( rightSpan )
                .wrap( centerSpan );
        },
        
        _init : function(){
            var self = this,
                options = this.options,
                element = this.element;
            if ( typeof options.disabled != "boolean" ) {
                options.disabled = element.propAttr( "disabled" );
    		}
            if ( element.attr('disabled') == 'disabled' || options.disabled == 'disabled') {
    			options.disabled = true;
    		}
            this._initButton();
            if(options.disabled){
            	self._addClass('disabled');
            	element.css('cursor','default');
            }
            var $newelement = element.parent().parent();
            $newelement.bind( 'mouseenter',function( event ){
            					if ( options.disabled ) {
            						return false;
            					}
            					self._addClass('hover');
            				}).bind( "mouseleave", function( event ) {
            					if ( options.disabled ) {
            						return false;
            					}
            					self._removeClass('hover');
            					self._removeClass('active');
            				}).bind( "click", function( event ){
            					if ( options.disabled ) {
            						event.preventDefault();
            						event.stopImmediatePropagation();
            						return false;
            					}else if(self.options.onClick){
            						self._trigger("onClick",event);
            					}
            				}).bind( "mousedown", function( event ) {
            					if ( options.disabled ) {
            						return false;
            					}
            					self._addClass('active');
            				    self._removeClass('focus');
            					var onClick = options.onClick;
            				})
            				.bind( "mouseup", function( event ) {
            					if ( options.disabled ) {
            						return false;
            					}
            					self._addClass('focus');
            					self._removeClass('active');
            				})
            				.bind( "keydown", function(event) {
            					if ( options.disabled ) {
            						return false;
            					}
            					if ( event.keyCode == $.om.keyCode.SPACE || event.keyCode == $.om.keyCode.ENTER ) {
            						self._addClass('active');
            					}
            					if( event.keyCode == $.om.keyCode.SPACE){
            						var onClick = options.onClick;
            		                if ( onClick && self._trigger("onClick",event) === false ) {
            		                    return;
            		                }
            					}
            				})
            				.bind( "keyup", function() {
            					self._removeClass('active');
            				});
	            element.bind( "focus.button", function( event ) {
								if ( options.disabled ) {
									return false;
								}
								self._addClass('focus');
							}).bind( "blur.button", function( event ) {
	        					if ( options.disabled ) {
	        						return false;
	        					}
	        					self._removeClass('focus');
	        				});
        },
        /**
         * 启用组件。
         * @name omButton#enable
         * @function
         * @example
         * $('#btn').omButton('enable');
         */
        enable : function(){
            this._removeClass('disabled');
            this.options.disabled = false;
            this.element.css('cursor','pointer')
                        .removeAttr('disabled');
        },
        /**
         * 禁用组件。
         * @name omButton#disable
         * @function
         * @example
         * $('#btn').omButton('disable');
         */
        disable : function(){
        	this._addClass('disabled');
            this.options.disabled = true;
            this.element.css('cursor','default');
            if(this.type == 'input' || this.type == 'button'){
            	this.element.attr('disabled', 'disabled');
            }
        },
        /**
         * 触发点击事件。
         * @name omButton#click
         * @function
         * @example
         * $('#btn').omButton('click');
         */
        click : function(){
        	if(!this.options.disabled && this.options.onClick){
        		this._trigger("onClick");
            }
        },
        /**
         * 改变按钮的label属性。
         * @name omButton#changeLabel
         * @function
         * @param label 按钮文本
         * @example
         * $('#btn').omButton('changeLabel','按钮label');
         */
        changeLabel : function(label){
            if(this.type == 'a'){
            	this.element.text(label) ;
            }else if( this.type == 'input' ){
            	this.element.val(label) ;
            }else if ( this.type == 'button' ){
            	this.element.html(label) ;
            }
        },
        /**
         * 改变按钮的icon属性。
         * @name omButton#changeIcons
         * @function
         * @param icons 图标路径
         * @example
         * $('#btn').omButton('changeIcons',{
         *     left: 'images/help.png',
         *     right: 'images/edit_add.png'
         * });
         */
        changeIcons : function( icons ){
        	if( !this.options.disabled ){
	            if( icons ){
	            	icons.left?this.element.css( 'backgroundImage','url( '+icons.left+' )' ):null;
	            	icons.right?this.element.next().attr( 'src',icons.right ):null;
	            }
            }
        },
        destroy : function(){
        	$el = this.element;
        	$el.closest(".om-btn").after($el).remove();
        },
        _addClass : function( state ){
        	this.element.parent().parent().addClass( 'om-state-'+state );
        },
        _removeClass : function( state ){
        	this.element.parent().parent().removeClass( 'om-state-'+state );
        },
        _initButton : function(){
        	var options = this.options,
        	    label = this._getLabel(),
        	    element = this.element;
        	
            element.removeClass('om-btn-icon om-btn-only-icon')
                .next("img").remove();
        	
        	if( options.width > 10 ){
        		element.parent().css( 'width',parseInt( options.width )-10 );
        	}
        	if( this.type == 'a' || this.type == 'button' ){
        	    element.html( label );
        	}else{
        	    element.val( label );
        	}
        	
        	if( options.icons.left ){
        	    if( label ){
        	        element.addClass( 'om-btn-icon' ).css( 'background-image','url('+options.icons.left+')' );
        	    }else{
        	        element.addClass( 'om-btn-only-icon' ).css('background-image','url('+options.icons.left+')' );
        	    }
        	}
        	if( options.icons.right ){
        	    if( label != '' ){
        	        $( '<img>' ).attr( 'src',options.icons.right ).css( {'vertical-align':'baseline','padding-left':'3px'} ).insertAfter( element );
        	    }else{
        	        $( '<img>' ).attr( 'src',options.icons.right ).css( 'vertical-align','baseline' ).insertAfter( element );
        	    }
            }
        },
        _getLabel : function(){
        	return this.options.label || this.element.html() || this.element.text() || this.element.val();
        },
        _determineButtonType: function() {
    		if ( this.element.is("input") ) {
    			this.type = "input";
    		}  else if ( this.element.is("a") ) {
    			this.type = "a";
    		} else if ( this.element.is('button') ){
    			this.type = "button";
    		}
    	}
    });
})(jQuery);/*
 * $Id: om-combo.js,v 1.175 2012/06/26 08:39:27 linxiaomin Exp $
 * operamasks-ui omCombo @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 *
 * Depends:
 *  om-core.js
 */
;(function($) {
    
    // Array.prototype.indexOf is added in JavaScript v1.6.
    // IE8 only support JavaScript v1.3. So added it to make this component support IE.
    if(!Array.prototype.indexOf){
        Array.prototype.indexOf=function(item){
            var len=this.length;
            for(var i=0;i<len;i++){
                if(this[i]===item){
                    return i;
                }
            }
            return -1;
        };
    }
	/**
     * @name omCombo
     * @class 下拉输入框组件。类似于html中的select，但是可以输入，可以过滤，可以使用远程数据。<br/><br/>
     * <b>特点：</b><br/>
     * <ol>
     *      <li>可以使用本地数据源，也可以使用远程数据源</li>
     *      <li>支持下拉框的缓加载（第一次显示时才初始化下拉框中的内容）</li>
     *      <li>用户可定制下拉框中数据的显示效果</li>
     *      <li>用户可定制选择后回填到输入框的文字</li>
     *      <li>用户可定制选择后组件的value值</li>
     *      <li>用户可定制下拉框的宽度和最大高度</li>
     *      <li>具有边输入边过滤的功能，也可定制过滤的算法</li>
     *      <li>提供丰富的事件</li>
     * </ol>
     * 
     * <b>示例：</b><br/>
     * <pre>
     * &lt;script type="text/javascript" >
     * $(document).ready(function() {
     *     $('#combo').omCombo({
     *         dataSource:[
     *                 {text:'Java',value:'1'},
     *                 {text:'JavaScript',value:'2'},
     *                 {text:'C',value:'3'},
     *                 {text:'PHP',value:'4'},
     *                 {text:'ASP',value:'5'}
     *         ],
     *         optionField:function(data,index){
     *             return '&lt;font color="red">'+index+'：&lt;/font>'+data.text+'('+data.value+')';
     *         },
     *         emptyText:'select one option!',
     *         value:'1',
     *         editable:false,
     *         lazyLoad:true,
     *         listMaxHeight:40
     *     });
     * });
     * &lt;/script>
     * 
     * &lt;input id="combo"/>
     * </pre>
     * 
     * @constructor
     * @description 构造函数. 
     * @param p 标准config对象：{}
     */
	$.omWidget('om.omCombo', {
	    options: 
	       /** @lends omCombo#*/
	       {
                /**
                 * 将JSON对象中哪个字段作为option的text，可以指定为JSON的一个属性，也可以指定一个function来自己决定如何显示option的text。<br/><br/> 
                 * <ul>
                 * <li>对于[{"text":"Java语言","value":"java"},{"text":"C语言","value":"c"},{"text":"C#语言","value":"c#"}]这样的对象,此属性可以不设置，将采用默认值'text'。</li>
                 * <li>对于[{"label":"Java语言","id":"java"},{"label":"C语言","id":"c"},{"label":"C#语言","id":"c#"}]这样的对象,此属性可以设置为'label'。</li>
                 * <li>对于[{"name":"深圳","abbreviation":"sz","code":"0755"},{"name":"武汉","abbreviation":"wh","code":"027"},{"name":"北京","abbreviation":"bj","code":"010"}]这样的对象,此属性可以设置为</li>
                 * </ul>
                 * <br/>
                 * @type String or Function 
                 * @default 'text' 
                 * @example
                 * optionField:function(data,index){ 
                 *   return data.name+'('+ data.abbreviation+')'; 
                 * }  
                 * // 最后options分显示成"深圳(sz)、武汉(wh)、北京(bj)"这样的。
                 * // 当然也可以写成其它东西，如下面的代码可以实现options为左图片右文字的情况
                 * // return '&lt;img src="options.jpg" style="float:left"/>&lt;span style="float:right">' + data.value+'&lt;/span>'; 
                 */
                optionField : 'text',
                /**
                 * JSON对象中哪个字段作为option的value属性，可以指定为JSON的一个属性，也可以指定一个function来自己决定如何显示option的value。<br/><br/>
                 * <ul>
                 *   <li>[{"text":"Java语言","value":"java"},{"text":"C语言","value":"c"},{"text":"C#语言","value":"c#"}] 将采用默认值'value'。</li>
                 *   <li>[{"label":"Java语言","id":"java"},{"label":"C语言","id":"c"},{"label":"C#语言","id":"c#"}] 此属性可以设置为'id'。</li>
                 *   <li>[{"name":"深圳","abbreviation":"sz","code":"0755"},{"name":"武汉","abbreviation":"wh","code":"027"},{"name":"北京","abbreviation":"bj","code":"010"}]此属性可以设置为'code'</li>
                 * </ul>
                 * <br/>
                 * @type String , Function
                 * @default 'value'
                 * @example
                 * 如果JSON数据为
                 * [
                 *   {"name":"深圳","abbreviation":"sz","code":"0755"},
                 *   {"name":"武汉","abbreviation":"wh","code":"027"}
                 * ]
                 * function(data,index){
                 *    return data.code+'(' + data.abbreviation+')';
                 * } 
                 *  最后各options的值分别是"0755(sz)、027(wh)、010(bj)"这样的。
                 *  当然也可以写成其它复杂的东西，如return data.code.substring(1); 
                 *  以实现将区号前面的0去掉作为option的value这样的功能。
                 */
                valueField : 'value',
                /**
                 * 组件宽度。可以使用px、pt、em、auto，如'100px'、'10pt'、'15em'、'auto'
                 * @type String
                 * @default 'auto'
                 * @example
                 * width : '100px'
                 */
                width : 'auto',
                /**
                 * 是否禁用组件。如果禁用，则不可以输入，form提交时也将忽略这个输入框。
                 * @type Boolean
                 * @default false
                 * @example
                 * disabled : true
                 */
                disabled : false,
                /**
                 * 组件是否只读。如果是只读，则不可以输入，不可以通过下拉框选择一个option，form提交时将包含这个输入框。
                 * @type Boolean
                 * @default false
                 * @example
                 * readOnly : true
                 */
                readOnly : false,
                /**
                 * 组件是否可以输入。设成false时不可以输入，但可以从下拉框里选择一个option。
                 * @type Boolean
                 * @default true
                 * @example
                 * editable : true
                 */
                editable : true,
                /**
                 * 是否延迟加载下拉框里的选项，设成true时页面显示时不加载下拉框选项，第一次展开下拉框才加载。
                 * @type Boolean
                 * @default false
                 * @example
                 * lazyLoad : true
                 */
                lazyLoad : false,
                /**
                 * 组件的下拉框的最大高度，设成'auto'时高度不固定，有多少选项就显示多高；设成50时表示下拉框最大高度50px，如果超过这个则显示垂直滚动条。<b>注意：由于浏览器的限制，这个属性的最小值是31，如果小于这个值时将看不到垂直滚动条</b>
                 * @type Number
                 * @default 300
                 * @example
                 * listMaxHeight : 500
                 */
                listMaxHeight : 300,
                /**
                 * 组件的下拉框的宽度是否自动扩展。设成false时下拉框的宽度将与输入框宽度保持一致；设成true时下拉框宽度将等于最宽的那个选项的宽度。
                 * @type Boolean
                 * @default false
                 * @example
                 * listAutoWidth : true
                 */
                listAutoWidth : false,
                /**
                 * 是否自动过滤下拉框选项。设成true时下拉框中将仅显示与输入框当前值匹配（匹配算法由filterStrategy决定）的选项。
                 * @type Boolean
                 * @default true
                 * @example
                 * autoFilter : true
                 */
                autoFilter : true,
                /**
                 * 自动过滤下拉框选项采用的过滤算法。<b>注意：仅当autoFilter不为false时该属性才有效果</b><br/>
                 * 默认值为'first'表示从左边匹配（相当于startWith），即下拉框的选项的label以输入框的值开头的才会显示。<br/>
                 * 设为'last'表示从右边匹配（相当于endWith），即下拉框的选项的label以输入框的值结尾的才会显示。<br/>
                 * 设为'anywhere'表示从任意位置匹配（相当于contains），即下拉框的选项的label只要出现过与输入框的值一样的都会显示。<br/>
                 * 也可以设为一个自定义function，该function返回true表示匹配成功，将会显示在下拉列表中，返回true则不显示。
                 * @type String,Function
                 * @default 'first'
                 * @example
                 * //此属性可以设置为'first' 或 'last' 或 'anywhere' 或 
                 * function(text,record){ 
                 *      var reg=new RegExp(text); 
                 *      //只要当前记录的postCode属性或idCardNo属性中包含输入框的值就算匹配成功
                 *      return reg.test(record.postCode) || reg.test(record.idCradNo); 
                 * } 
                 */
                filterStrategy : 'first',
                /**
                 * 自动过滤下拉框选项延迟时间（单位：毫秒）。如果设成300则表示在300毫秒内输入连续按键多次，则只进行最后一次按键的过滤。<b>注意：仅当autoFilter不为false时该属性才有效果</b>
                 * @type Number
                 * @default 500
                 * @example
                 * filterDelay : 1000
                 */
                filterDelay : 500, 
                /**
                 * 是否强制选择。当属性值为true时，强制用户选择下拉列表中的选项，如果用户输入的字符非下拉项中的某项，当输入框失去焦点时，输入框将被清空。
                 * 属性值为false时，允许用户输入任意字符，当输入框失去焦点时，该字符串将作为value值。
                 * @type Boolean
                 * @default false
                 * @example
                 * forceSelction : false
                 */
                forceSelection: false,
                
                /**
                 * 是否支持多选，默认为 false。如果支持多选默认将不可编辑只可选择。
                 * @type Boolean
                 * @default false
                 * @example
                 *  multi : true
                 */
                multi : false, 
                
                /**
                 * 支持多选时的多个选项之间的分隔符，默认为 ','.
                 * @type String
                 * @default ','
                 * @example
                 *  multiSeparator : ';'
                 */
                multiSeparator : ','
                
                /**
                 * 数据源属性，可以设置为“后台获取数据的URL”或者“JSON数据”
                 * @name omCombo#dataSource
                 * @type Array[JSON],URL
                 * @default 无
                 * @example
                 * dataSource : '/operamasks-ui/getCountryNameServlet.json' 
                 * 或者
                 * dataSource : [{"value":"001","text":"张三"},{"value":"002","text":"李四"}]
                 */
                /**
                 * 当input框的值为空时，input框里出现提示消息。当input框得到焦点或者input框的值不为空时这个提示消息会自动消失。
                 * @name omCombo#emptyText
                 * @default 无
                 * @type String
                 * @example
                 * emptyText : '请输入值'
                 */
                /**
                 * combo组件的初始值。<b>注意：如果设置了value属性的值则lazyLoad属性将会被强制转换为false</b>
                 * @name omCombo#value
                 * @default 无
                 * @type String
                 * @example
                 * value : '北京'
                 */
                /**
                 * 填充下拉框内容的function。设置此属性时表示用户要自己接管从records到下拉框的显示过程，用户拿到所有的records然后自己填充下拉框里的内容，最后返回一个JQuery元素集合，集合里的每个元素表示一个option，按上下键选择时将会在这个集合的元素间循环高亮。
                 * @name omCombo#listProvider
                 * @type Function
                 * @default 无
                 * @returns {jQuery Array} 应该返回一个jQuery数组，里面的每个元素表示下拉框里的一个option（如下示例中下拉框里是一个table，tabody中的每个tr表示一个option，所以返回container.find('tbody tr')）。
                 * @example
                 * listProvider:function(container,records){ 
                 *      $('&lt;table&gt;').appendTo(container);
                 *      records.each(function(){ 
                 *          $('&lt;tr&gt;&lt;td&gt;'+this.text+'&lt;td&gt;&lt;/tr&gt;').appendTo(container); 
                 *      }); 
                 *      $('&lt;/table>').appendTo(container);
                 *      return container.find('tbody tr'); //tbody中每个tr表示一个option，而thead中的tr表示表头，不是option
                 *  } 
                 */
                 /**
                 * 将JSON对象的哪个字段作为显示到input框的文字。可以指定为JSON的一个属性，也可以指定一个function来自己决定显示什么文字到input框。<b>注意：这里的内容在选择一个option后会直接显示在input框里，所以只能显示普通字符串，不能使用html</b>
                 * @name omCombo#inputField
                 * @type String or Function
                 * @default 'text'
                 * @example
                 * //以JSON对象的userName属性值作为显示到输入框的文字
                 * inputField:'userName'
                 * 
                 * //自定义一个Function来决定以什么作为显示到输入框的文字
                 * inputField:function(data,index){ 
                 *      return data.text+'('+data.value+')';
                 * } 
                 */
                 /**
                 * omCombo的输入框的内容发生变化时的回调函数。
                 * @event
                 * @param target 当前输入框对象
                 * @param newValue 选择的新值
                 * @param oldValue 原来的值
                 * @param event jQuery.Event对象。
                 * @name omCombo#onValueChange
                 * @type Function
                 * @example
                 * onValueChange:function(target,newValue,oldValue,event){ 
                 *      //do something
                 *  } 
                 */
                 /**
                 * 以Ajax方式加载下拉列表中的内容出错时的回调函数。可以在这里进行一些处理，比如以人性化的方式提示用户。
                 * @event
                 * @param xmlHttpRequest XMLHttpRequest对象
                 * @param textStatus  错误类型
                 * @param errorThrown  捕获的异常对象
                 * @param event jQuery.Event对象。
                 * @name omCombo#onError
                 * @type Function
                 * @example
                 * onError:function(xmlHttpRequest, textStatus, errorThrown, event){ 
                 *      alert('An error occurred while load records from URL "'+url+'",the error message is:'+errorThrown.message);
                 *  } 
                 */
                 /**
                 * Ajax响应回来时执行的方法。
                 * @event
                 * @param data Ajax请求返回的数据
                 * @param textStatus 响应的状态
                 * @param event jQuery.Event对象。
                 * @name omCombo#onSuccess
                 * @type Function
                 * @example
                 * onSuccess:function(data, textStatus, event){
                 *     if(data.length==0){
                 *          $('#txt').omSuggestion('showMessage','没有数据！');
                 *     } 
                 * }
                 */
        },
        _init:function(){
            var options = this.options,
                inputEl = this.textInput,
                source = options.dataSource;
            
            if (!options.inputField) {
                options.inputField = options.optionField;
            }
            //由于在lazyLoad=false的情况下设置value时无法显示正确的fieldText
            if (typeof options.value !== 'undefined') {
                options.lazyLoad = false;
            }
            
            if (options.width != 'auto') {
                var span = inputEl.parent().width(options.width);
                inputEl.width(span.innerWidth() - inputEl.next().outerWidth() - inputEl.outerWidth() + inputEl.width());
            }
            /*if (!options.listAutoWidth) {
                this.dropList.width(inputEl.parent().width());
            }*/
            
            if (options.multi) {
                options.editable = this.options.editable = false;
            }
            
            this._refeshEmptyText(options.emptyText);
            
            options.disabled ? inputEl.attr('disabled', true) : inputEl.removeAttr('disabled');
            (options.readOnly || !options.editable) ? inputEl.attr('readonly', 'readOnly') : inputEl.removeAttr('readonly');
            
            if (!options.lazyLoad) {
                //load data immediately
                this._toggleLoading('add');
                if(source && typeof source == 'string'){
                    this._ajaxLoad(source);
                }else if(source && typeof source == 'object'){
                    this._loadData(source);
                    this._toggleLoading('remove');
                }else{
                    //neither records nor remote url was found
                    this.dataHasLoaded = true;
                    this._toggleLoading('remove');
                }
                
            } else {
                this.dataHasLoaded = false;
            }
            var unusable = options.disabled || options.readOnly;
            
            if (unusable) {
                this.expandTrigger.addClass('om-state-disabled');
            } else {
                this._bindEvent();
            }
        },
        _create:function(){
            var valueEl = this.element;
            var span = $('<span class="om-combo om-widget om-state-default"></span>').insertAfter(valueEl).wrapInner(valueEl);
            this.textInput = valueEl.clone().removeAttr("id").removeAttr("name").appendTo(span);
            this.expandTrigger = $('<span class="om-combo-trigger"></span>').appendTo(span);
            valueEl.hide();
            this.dropList = $($('<div class="om-widget"><div class="om-widget-content om-droplist"></div></div>').css({position:'absolute', zIndex:2000}).appendTo(document.body).children()[0]).hide();
        },
        /**
         * 重新加载下拉框里的数据，一般用于级联combo功能。
         * @name omCombo#setData
         * @function
         * @param arg records（JSON数组）或url
         * @example
         * //用一个固定的JSON数组来重新加载combo的下拉列表
         * $('#productCombo').omCombo('setData',[
         *      {"text":'Apusic Server',"value":"aas"},
         *      {"text":'Apusic OperaMasks SDK',"value":"aom"},
         *      {"text":'Apusic OperaMasks Studio',"value":"studio"}
         * ]);
         * 
         * //通过一个url来发送Ajax请求重新加载combo的下拉列表
         * $('#cityCombo').omCombo('setData',"../data/cityData.do?province="+$('#cityCombo').omCombo('value'));
         */
        setData:function(param){
            var self = this, inputEl = self.textInput, valueEl = self.element;
            self.options.value = '';
            valueEl.val('');
            inputEl.val('');
            self._toggleLoading('add');
            if (typeof param === 'string') {
                self._ajaxLoad(param);
            } else {
                self._loadData(param);
                self._toggleLoading('remove');
            }
        },
        /**
         * 获取combo的数据源，返回一个JSON数组。<b>注意：该数组和下拉项数组不是等同的，但存在一一对应的关系:前者经过格式化后能转变成后者</b>
         * @name omCombo#getData
         * @function
         * @returns 如果combo中有数据，则返回combo的数据源(一个由所有记录组成的JSON数组)；否则返回null
         * @example
         * //获取combo的数据源
         * var store = $('#productCombo').omCombo('getData');
         * 
         */
        getData:function(){
            //如果已经存在dataSource则直接取出
            var returnValue = this.options.dataSource;
            return (typeof returnValue == 'object') ? returnValue : null;
        },
        /**
         * 得到或设置combo的value值。
         * @function
         * @name omCombo#value
         * @param v 设置的值，不设置表示获取值
         * @returns 如果没有参数时表示getValue()返回combo的value值。如果有参数时表示setValue(newValue)返回jQuery对象。
         * 
         */
         value:function(v){
             if (typeof v === 'undefined') {
                 //var value = $(this.element).attr(_valueKey);
                 var value =this.element.val();
            	 return value ? value : '';
             } else {
                 this._setValue(v+'');
                 return this;
             }
         },
        /**
         * 禁用组件。
         * @name omCombo#disable
         * @function
         * @example
         * $('#mycombo').omCombo('disable');
         */
        disable:function(){
            var input=this.element;
            //distroy event listening
            input.attr('disabled', true).unbind();
            this.options.disabled = true;
            this.expandTrigger.addClass('om-state-disabled').unbind();
        },
        /**
         * 启用组件。
         * @name omCombo#enable
         * @function
         * @example
         * $('#mycombo').omCombo('enable');
         */
        enable:function(){
            var input=this.element;
            input.removeAttr('disabled').unbind();
            this.options.disabled = false;
            this.expandTrigger.removeClass('om-state-disabled').unbind();
            //rebuild event listening
            this._bindEvent();
        },
        destroy:function(){
        	var $input = this.element;
        	$(document).unbind('mousedown.omCombo',this.globalEvent);
        	$input.parent().after($input).remove();
        	this.dropList.parent().remove();
        },
        //private
        _bindEvent:function(){
            var self = this, options = self.options,input = self.textInput, 
            valueEl = self.element, dropList = self.dropList,
            expandTrigger = self.expandTrigger, emptyText = options.emptyText;
            var isFocus = false, span = input.parent('span');
            span.mouseenter(function(){   
               if(!options.disabled){
                   span.addClass("om-state-hover");
               }
            }).mouseleave(function(){      
                span.removeClass("om-state-hover");
            }).mousedown(function(e){
                e.stopPropagation(); //document的mousedown会隐藏下拉框，这里要阻止冒泡
            });
            input.focus(function(){
                if(isFocus) 
                    return;
                isFocus = true;
                $('.om-droplist').hide(); //hide all other dropLists
                span.addClass('om-state-focus');
                //input.addClass('om-span-field-focus');
                //input.parent('span').
                //expandTrigger.addClass('om-state-hover');
                self._refeshEmptyText(emptyText);
                if (!self.dataHasLoaded) {
                    if(!expandTrigger.hasClass('om-loading')){
                        self._toggleLoading('add');
                        if (typeof(options.dataSource) == 'object') {
                            self._loadData(options.dataSource);
                            self._toggleLoading('remove');
                        } else if (typeof(options.dataSource) == 'string') {
                            self._ajaxLoad(options.dataSource);
                        } else {
                            //neither records nor remote url was found
                            self.dataHasLoaded = true;
                            self._toggleLoading('remove');
                        }
                    }
                }
                if (!options.disabled && !options.readOnly) {
                    self._showDropList();
                }
            }).blur(function(e){
                isFocus = false;
                span.removeClass('om-state-focus');
                input.removeClass('om-combo-focus');
                //expandTrigger.removeClass('om-trigger-hover');
                if (!options.disabled && !options.readOnly && !options.multi) {
                    if (self.hasManualInput) {
                        //如果有手工输入过值，在blur时检查是否是合法的值，如果不是要清除不合法的输入并还原成输入前的值
                        self.hasManualInput = false;
                        var text = input.val();
                        if (text !== '') {
                            var allInputText = $.data(valueEl, 'allInputText');
                            var allValues = $.data(valueEl, 'allValues');
                            var index = allInputText.indexOf(text);
                            if (index > -1) {
                                self._setValue(allValues[index]);
                            } else if(!options.forceSelection){ //如果输入的值在data里面不存在，则设置key和vlue为同一输入的值
                                valueEl.val(input.val());
                            }else{
                            	var value = valueEl.val();
                                index = allValues.indexOf(value);
                                if (index > -1) {
                                    input.val(allInputText[index]);
                                } else {
                                    input.val('');
                                }
                            }
                        }else{
                        	valueEl.val('');
                    	}
                    }
                    self._refeshEmptyText(emptyText);
                }
            }).keyup(function(e){
                var key = e.keyCode,
                    value = $.om.keyCode;
                switch (key) {
                    case value.DOWN:
                        self._selectNext();
                        break;
                    case value.UP: 
                        self._selectPrev();
                        break;
                    case value.ENTER: 
                        self._backfill(self.dropList.find('.om-state-hover'));
                        break;
                    case value.ESCAPE: 
                        dropList.hide();
                        break;
                    case value.TAB:
                        //only trigger the blur event
                        break;
                    default:
                        //fiter功能
                        self.hasManualInput = true;
                        if (!options.disabled && !options.readOnly && options.editable && options.autoFilter) {
                            if (window._omcomboFilterTimer) {
                                clearTimeout(window._omcomboFilterTimer);
                            }
                            window._omcomboFilterTimer = setTimeout(function(){
                                if($(document).attr('activeElement').id == input.attr('id')){//当焦点在当前输入框的时候才显示下拉框，否则隐藏
                                    dropList.show();
                                }
                                self._doFilter(input);
                            }, options.filterDelay);
                        }
                }
            });
            dropList.mousedown(function(e){
                e.stopPropagation(); //document的mousedown会隐藏下拉框，这里要阻止冒泡
            });
            expandTrigger.click(function(){
                !expandTrigger.hasClass('om-loading') && input.focus();
            }).mousedown(function(){
                !expandTrigger.hasClass('om-loading') && span.addClass('om-state-active');
            }).mouseup(function(){
                !expandTrigger.hasClass('om-loading') && span.removeClass('om-state-active');
            });
            $(document).bind('mousedown.omCombo',this.globalEvent=function(){
                dropList.hide();
            });
        },
        _showDropList:function(){
        	var self = this, options = self.options, 
        	    inputEl = self.textInput, valueInput = self.element,
          	    dropList = self.dropList.scrollTop(0).css('height','auto'),
         	    valuedItem,
         	    nowValue = valueInput.val(),
         	    $listRows = dropList.find('.om-combo-list-row'),
         	    allItems = self._getAllOptionsBeforeFiltered().removeClass('om-helper-hidden om-state-hover');
            
        	if(allItems.size()<=0){ //如果下拉框没有数据
                return;
            }
            $listRows.removeClass('om-combo-selected');
            if (nowValue !== undefined && nowValue !== '') {
                var allValues = $.data(valueInput, 'allValues');
                if (options.multi) {
                    var selectedValues = nowValue.split(options.multiSeparator);
                    for (var i=0; i<selectedValues.length; i++) {
                        var index = allValues.indexOf(selectedValues[i]);
                        if (index > -1) {
                            $(dropList.find('.om-combo-list-row').get(index)).addClass('om-combo-selected');
                        }
                    }
                    valueItem = selectedValues[0];
                } else {
                    var index = allValues?allValues.indexOf(nowValue):-1;
                    if (index > -1) {
                        valuedItem = $(dropList.find('.om-combo-list-row').get(index)).addClass('om-combo-selected');
                    }
                }
            }
            var dropListContainer = dropList.parent(), span = inputEl.parent();
            if (!options.listAutoWidth) {
                dropListContainer.width(span.outerWidth());
            }else{
            	if($.browser.msie&&($.browser.version == "7.0")&&!$.support.style){
            		dropListContainer.width(dropList.show().outerWidth());
            	}else{
            		dropListContainer.width(dropList.outerWidth());
            	}
            }
            if (options.listMaxHeight != 'auto' && dropList.show().height() > options.listMaxHeight) {
                dropList.height(options.listMaxHeight).css('overflow-y','auto');
            }
            var inputPos = span.offset();
            dropListContainer.css({
                'left': inputPos.left,
                'top': inputPos.top + span.outerHeight()
            });
            dropList.show();
            if (valuedItem) { //自动滚动滚动条到高亮的行
                dropList.scrollTop($(valuedItem).offset().top - dropList.offset().top);
            }
        },
        _toggleLoading:function(type){
            if(!this.options.disabled){
                if (type == 'add') {
                    this.expandTrigger.removeClass('om-icon-carat-1-s').addClass('om-loading');
                } else if (type == 'remove') {
                    this.expandTrigger.removeClass('om-loading').addClass('om-icon-carat-1-s');
                }
            }
        },
        _ajaxLoad:function(url){
            var self=this;
            var options = this.options;
            $.ajax({
                url: url,
                method: 'POST',
                dataType: 'json',
                success: function(data, textStatus){
                    self.dataHasLoaded = true;
                    var onSuccess = options.onSuccess;
                    if (onSuccess && self._trigger("onSuccess", null, data, textStatus) === false) {
                        options.dataSource = data;
                        return;
                    }
                    self._loadData(data);
                    self._toggleLoading('remove');
                },
                error: function(XMLHttpRequest, textStatus, errorThrown){
                    self.dataHasLoaded = true; // 必须设置为true，否则在lazyLoad为true的时候会陷入死循环
                    if (options.onError) {
                        self._toggleLoading('remove');
                        self._trigger("onError", null, XMLHttpRequest, textStatus, errorThrown);
                    } else {
                        self._toggleLoading('remove');
                        throw new Error('An error occurred while load records from URL "' + url + '",the error message is:' + errorThrown.message);
                    }
                }
            });
        },
        _loadData:function(records){
            var options = this.options,
                valueEl = this.element;
            options.dataSource = records;
            this.dataHasLoaded = true;
            //build all inputText
            var inputField = options.inputField;
            var allInputText = [];
            if (typeof inputField === 'string') {
                $(records).each(function(){
                    allInputText.push(this[inputField]);
                });
            } else {
                $(records).each(function(index){
                    allInputText.push(inputField(this, index));
                });
            }
            $.data(valueEl, 'allInputText', allInputText);
            //build all value
            var valueField = options.valueField;
            var allValues = [];
            if (typeof valueField === 'string') {
                $(records).each(function(){
                    allValues.push('' + this[valueField]);
                });
            } else {
                $(records).each(function(index){
                    allValues.push('' + valueField(this, index));
                });
            }
            $.data(valueEl, 'allValues', allValues);
            //build dropList
            var dropList = this.dropList.empty();
            if (options.listProvider) {
                var selectableOptions = options.listProvider(dropList, records);
                if (selectableOptions) {
                    selectableOptions.each(function(){
                        $(this).addClass('om-combo-list-row');
                    });
                }
            } else {
                var optionField = options.optionField;
                var innerHtml = '';
                var self = this;
                if (typeof optionField === 'string') {
                    $(records).each(function(index){
                        innerHtml += self._wrapText(this[options.optionField]);
                    });
                } else {
                    $(records).each(function(index){
                        innerHtml += self._wrapText(options.optionField(this, index));
                    });
                }
                if (innerHtml) {
                    $(innerHtml).appendTo(dropList);
                    dropList.show().css('height','auto');
                    if (options.listMaxHeight != 'auto' && dropList.height() > options.listMaxHeight) {
                        dropList.height(options.listMaxHeight).css('overflow-y','auto');
                    }
                    dropList.hide();
                    if(valueEl.parent().hasClass('om-state-hover')){
                        self._showDropList();
                    }
                }
            }
           
            if (options.value) {
                this._setValue('' + options.value);
            }
            this._bindEventsToList();
        },
        _bindEventsToList:function(){
        	var self = this,
        	items = self._getAllOptionsBeforeFiltered();
            items.hover(function(){
                items.removeClass('om-state-hover');
                $(this).addClass('om-state-hover');
            }, function(){
                $(this).removeClass('om-state-hover');
            }).mousedown(function(){
                self._backfill(this);
            });
        },
        _wrapText:function(text) {
            return '<div class="om-combo-list-row">' + text + '</div>';
        },
        _setValue:function(value){
            var input = this.textInput, valueEl = this.element;
            var valueChange = true ;
            var oldValue = valueEl.val();
            var options = this.options;
            if(value == oldValue){
                valueChange = false ;
            }
            var allValues = $.data(valueEl, 'allValues');
            
            var inputText = [], values=[];
            if (options.multi) {
                values = value.split(options.multiSeparator);
            } else {
                values.push(value);
            }
            for (var i=0; i<values.length; i++) {
                var index = allValues?allValues.indexOf(values[i]):-1;
                if (index > -1) {
                    inputText.push($.data(valueEl, 'allInputText')[index]);
                } else if(!options.forceSelection){
                	//与getValue保持一致，当setValue的值不在allVlues中，则将value值作为text显示在输入框中。bug616。
                	inputText.push(value);
                }else{
                	valueEl.val('');
                    value = '';
                }
            }
            valueEl.val(value);
            if (options.multi) {
                input.val(inputText.join(options.multiSeparator));
            } else {
                input.val(inputText.join(''));
            }
            options.value = value;
            // trigger onValueChange event
            if (options.onValueChange && valueChange) {
            	this._trigger("onValueChange",null,input,value,oldValue);
            }
            //refresh the emptyText
            this._refeshEmptyText(options.emptyText);
        },
        
        _findHighlightItem : function() {
            var dropList = this.dropList;
            var hoverItem = dropList.find('.om-state-hover');
            
            // only one item hover
            if (hoverItem.length > 0) {
                return hoverItem;
            }
            var selectedItems = dropList.find('.om-combo-selected');
            return selectedItems.length > 0 ? selectedItems[0] : selectedItems;
        },
        
        _selectPrev:function(){
            var highLightItem = this._findHighlightItem();
            var all = this._getAllOptionsAfterFiltered();
            var nowIndex = all.index(highLightItem);
            var currentItem = $(all[nowIndex]);
            if (nowIndex === 0) {
                nowIndex = all.length;
            } else if (nowIndex == -1) {
                nowIndex = all.length;
            }
            var preNeighborItem = $(all[nowIndex - 1]);
            this._highLisghtAndScrollTo(currentItem,preNeighborItem);
        },
        _selectNext:function(){
            var dropList = this.dropList;
            if (dropList.css('display') == 'none') {
                this._showDropList();
                return;
            }
            var all = this._getAllOptionsAfterFiltered();
            var nowIndex = all.index(this._findHighlightItem());
            var currentItem = $(all[nowIndex]);
            if (nowIndex == all.length - 1) {
                nowIndex = -1;
            }
            var nextNeighbor = $(all[nowIndex + 1]);
            this._highLisghtAndScrollTo(currentItem,nextNeighbor);
        },
        _highLisghtAndScrollTo: function(currentItem, targetItem){
            var dropList = this.dropList;
            currentItem.removeClass('om-state-hover');
            targetItem.addClass('om-state-hover');
            if (targetItem.position().top <= 0) {
                dropList.scrollTop(dropList.scrollTop() + targetItem.position().top);
            } else if (targetItem.position().top + targetItem.outerHeight() > dropList.height()) {
                dropList.scrollTop(dropList.scrollTop() + targetItem.position().top + targetItem.outerHeight() - dropList.height());
            }
        },
        _backfill:function(source){
            if (source.length === 0) {
                return;
            }
                
            var self = this, valueEl = self.element,
            dropList = self.dropList,
            options = self.options,
            enableMulti = options.multi;
            
            if (enableMulti) {
                $(source).toggleClass('om-combo-selected').removeClass('om-state-hover');
            } else {
                this._getAllOptionsBeforeFiltered().removeClass('om-combo-selected');
                $(source).addClass('om-combo-selected');
            }
            
            if (dropList.css('display') == 'none') {
                return;
            }
            var value = [], selectedIndexs = dropList.find('.om-combo-selected');
            for (var i=0; i<selectedIndexs.length; i++) {
                var nowIndex = $(selectedIndexs[i]).index();
                if (nowIndex > -1) {
                    value.push($.data(valueEl, 'allValues')[nowIndex]);
                }
            }
            
            this._setValue(value.join(enableMulti ? options.multiSeparator : ''));
            if (!enableMulti) {
                dropList.hide();
            }
        },
        _getAllOptionsBeforeFiltered:function(){
            return this.dropList.find('.om-combo-list-row');
        },
        _getAllOptionsAfterFiltered:function(){
            var dropList=this.dropList;
            return dropList.find('.om-combo-list-row').not(dropList.find('.om-helper-hidden'));
        },
        _doFilter:function(){
        	var self = this, inputEl = self.textInput, valueEl = self.element, options = self.options;
            records = options.dataSource,
            filterStrategy = options.filterStrategy,
            text = inputEl.val(),
            needShow=false,
            items = self._getAllOptionsBeforeFiltered(),
            allInputText = $.data(valueEl, 'allInputText');
            
            $(records).each(function(index){
                if (self._filetrPass(filterStrategy, text, records[index], allInputText[index])) {
                    $(items.get(index)).removeClass('om-helper-hidden');
                    needShow=true;
                } else {
                    $(items.get(index)).addClass('om-helper-hidden');
                }
            });
            var dropList = this.dropList.css('height','auto');
            //过滤后重新计算下拉框的高度，看是否需要出现滚动条
            if (options.listMaxHeight != 'auto' && dropList.height() > options.listMaxHeight) {
                dropList.height(options.listMaxHeight).css('overflow-y','auto');
            }
            if(!needShow){
                dropList.hide();
            }
        },
        _filetrPass:function(filterStrategy,text,record,inputText){
            if (text === '') {
                return true;
            }
            if (typeof filterStrategy === 'function') {
                return filterStrategy(text, record);
            } else {
                if (filterStrategy === 'first') {
                    return inputText.indexOf(text) === 0;
                } else if (filterStrategy === 'anywhere') {
                    return inputText.indexOf(text) > -1;
                } else if (filterStrategy === 'last') {
                    var i = inputText.lastIndexOf(text);
                    return i > -1 && i + text.length == inputText.length;
                } else {
                    return false;
                }
            }
        },
        _refeshEmptyText: function(emptyText){
            var inputEl = this.textInput;
            if(!emptyText)
                return;
            if (inputEl.val() === '') {
                inputEl.val(emptyText).addClass('om-empty-text');
            } else {
                if(inputEl.val() === emptyText){
                    inputEl.val('');
                }
                inputEl.removeClass('om-empty-text');
            }
        }
	});
})(jQuery);/*
 * $Id: om-dialog.js,v 1.34 2012/06/18 08:49:06 linxiaomin Exp $
 * operamasks-ui omDialog @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 *
 * Depends:
 *  om-core.js
 *  om-button.js
 *  om-draggable.js
 *  om-mouse.js
 *  om-position.js
 *  om-resizable.js
 */
(function( $, undefined ) {

var uiDialogClasses =
		'om-dialog ' +
		'om-widget ' +
		'om-widget-content ' +
		'om-corner-all ',
	sizeRelatedOptions = {
		buttons: true,
		height: true,
		maxHeight: true,
		maxWidth: true,
		minHeight: true,
		minWidth: true,
		width: true
	},
	resizableRelatedOptions = {
		maxHeight: true,
		maxWidth: true,
		minHeight: true,
		minWidth: true
	},
	// support for jQuery 1.3.2 - handle common attrFn methods for dialog
	attrFn = $.attrFn || {
		val: true,
		css: true,
		html: true,
		text: true,
		data: true,
		width: true,
		height: true,
		offset: true,
		click: true
	};

	/**
     * @name omDialog
     * @class 对话框组件。可以放置html。<br/><br/>
     * <b>特点：</b><br/>
     * <ol>
     *      <li>方便地自定义按钮</li>
     * </ol>
     * 
     * <b>示例：</b><br/>
     * <pre>
     * &lt;script type="text/javascript" >
     * $(document).ready(function() {
     *     $('#dialog').omDialog({
     *     });
     * });
     * &lt;/script>
     * 
     * &lt;div id="dialog"/>
     * </pre>
     * 
     * @constructor
     * @description 构造函数. 
     * @param p 标准config对象：{}
     */
$.omWidget("om.omDialog", {
	options: /** @lends omDialog#*/ {
	    /**
         * 对话框创建完成后是否自动打开。
         * @type Boolean
         * @default true
         * @example
         *   $("#select").omDialog({autoOpen : true});
         */
		autoOpen: true,
		
		/**
         * 对话框中的按钮。此配置项为JSON数组。每个JSON对象具有 <code>text</code> 属性(配置按钮文字)
         * 和 <code>click</code> 属性配置按钮触发时的回调方法。
         * @type Array
         * @default []
         *  @example
         *   $("#select").omDialog({buttons : [{
         *      text : "确定", 
         *      click : function () {...}
         *  }, {
         *      text : "取消", 
         *      click : function () {...}
         *  }]);
         */
		buttons: {},
		
		/**
		 * 按下 Esc 键时是否关闭对话框。
		 * @type Boolean
		 * @default true
		 * @example
         *   $("#select").omDialog({closeOnEscape : true});
		 */
		closeOnEscape: true,
		closeText: 'close',
		
		/**
		 * 对话框的样式。
		 * @type String
		 * @default 无
		 * @example
         *   $("#select").omDialog({dialogClass : 'class1'});
		 */
		dialogClass: '',
		
		/**
		 * 组件是否可拖动。
		 * @type Boolean
		 * @default true
		 * @example
         *   $("#select").omDialog({draggable : true});
		 */
		draggable: true,
		
		hide: null,
		
		/**
		 * 组件的高度。
		 * @type Number
		 * @default 'auto'
		 * @example
         *   $("#select").omDialog({height : 200});
		 */
		height: 'auto',
		
		/**
		 * 可改变大小时组件最大高度。
		 * @type Number
		 * @default 无
		 * @example
         *   $("#select").omDialog({maxHeight : 500});
		 */
		maxHeight: false,
		
		/**
		 * 可改变大小时组件最大宽度。
		 * @type Number
		 * @default 无
		 * @example
         *   $("#select").omDialog({maxWidth : 500});
		 */
		maxWidth: false,
		
		/**
		 * 可改变大小时组件最小高度。
		 * @type Number
		 * @default 150
		 * @example
         *   $("#select").omDialog({minHeight : 150});
		 */
		minHeight: 150,
		
		/**
		 * 可改变大小时组件最小宽度。
		 * @type Number
		 * @default 150
		 * @example
         *   $("#select").omDialog({minWidth : 150});
		 */
		minWidth: 150,
		
		/**
		 * 是否模态窗口。
		 * @type Boolean
		 * @default false
		 * @example
         *   $("#select").omDialog({modal : true});
		 */
		modal: false,
		position: {
			my: 'center',
			at: 'center',
			collision: 'fit',
			// ensure that the titlebar is never outside the document
			using: function(pos) {
				var topOffset = $(this).css(pos).offset().top;
				if (topOffset < 0) {
					$(this).css('top', pos.top - topOffset);
				}
			}
		},
		
		/**
		 * 是否可改变大小。
		 * @type Boolean
		 * @default true
		 * @example
         *   $("#select").omDialog({resizable : true});
		 */
		resizable: true,
		show: null,
		
		stack: true,
		
		/**
		 * 对话框的标题。
		 * @type String
		 * @default 无
		 */
		title: '',
		
		/**
		 * 组件的宽度。
		 * @type Number
		 * @default 300
		 * @example
         *   $("#select").omDialog({width : 300});
		 */
		width: 300,
		
        /**
         * 组件的堆叠顺序。默认为1000。
         * @type Number
         * @default 1000
         * @example
         *   $("#select").omDialog({zIndex : 300});
         */
		zIndex: 1000
		
		/**
         * 对话框打开时触发事件。
         * @event
         * @param event jQuery.Event对象。
         * @name omDialog#onOpen
         * @type Function
         * @example
         *   $("#select").omDialog({onOpen : function(event) {doSomething...}});
         */
		
		/**
         * 对话框关闭时触发事件。
         * @event
         * @param event jQuery.Event对象。
         * @name omDialog#onClose
         * @type Function
         * @example
         *   $("#select").omDialog({onClose : function(event) {doSomething...}});
         */
		
		/**
         * 对话框关闭前触发事件。
         * @event
         * @param event jQuery.Event对象。
         * @name omDialog#onBeforeClose
         * @type Function
         * @example
         *   $("#select").omDialog({onBeforeClose : function(event) {doSomething...}});
         */
	},

	_create: function() {
		this.originalTitle = this.element.attr('title');
		// #5742 - .attr() might return a DOMElement
		if ( typeof this.originalTitle !== "string" ) {
			this.originalTitle = "";
		}


		this.options.title = this.options.title || this.originalTitle;
		var self = this;
	    self.element.parent().bind("om-remove.omDialog", (self.__removeBind = function() {
	        self.element.remove();
	    }));
	    var options = self.options,

			title = options.title || '&#160;',
			titleId = $.om.omDialog.getTitleId(self.element),

			uiDialog = (self.uiDialog = $('<div></div>'))
				.appendTo(document.body)
				.hide()
				.addClass(uiDialogClasses + options.dialogClass)
				.css({
					zIndex: options.zIndex
				})
				// setting tabIndex makes the div focusable
				// setting outline to 0 prevents a border on focus in Mozilla
				.attr('tabIndex', -1).css('outline', 0).keydown(function(event) {
					if (options.closeOnEscape && event.keyCode &&
						event.keyCode === $.om.keyCode.ESCAPE) {
						
						self.close(event);
						event.preventDefault();
					}
				})
				.attr({
					role: 'dialog',
					'aria-labelledby': titleId
				})
				.mousedown(function(event) {
					self.moveToTop(false, event);
				}),

			uiDialogContent = self.element
				.show()
				.removeAttr('title')
				.addClass(
					'om-dialog-content ' +
					'om-widget-content')
				.appendTo(uiDialog),

			uiDialogTitlebar = (self.uiDialogTitlebar = $('<div></div>'))
				.addClass(
					'om-dialog-titlebar ' +
					'om-widget-header ' +
					'om-corner-all ' +
					'om-helper-clearfix'
				)
				.prependTo(uiDialog),

			uiDialogTitlebarClose = $('<a href="#"></a>')
				.addClass(
					'om-dialog-titlebar-close ' +
					'om-corner-tr'
				)
				.attr('role', 'button')
				.hover(
					function() {
						uiDialogTitlebarClose.addClass('om-state-hover');
					},
					function() {
						uiDialogTitlebarClose.removeClass('om-state-hover');
					}
				)
				.focus(function() {
					uiDialogTitlebarClose.addClass('om-state-focus');
				})
				.blur(function() {
					uiDialogTitlebarClose.removeClass('om-state-focus');
				})
				.click(function(event) {
					self.close(event);
					return false;
				})
				.appendTo(uiDialogTitlebar),

			uiDialogTitlebarCloseText = (self.uiDialogTitlebarCloseText = $('<span></span>'))
				.addClass('om-icon-closethick')
				.text(options.closeText)
				.appendTo(uiDialogTitlebarClose),

			uiDialogTitle = $('<span></span>')
				.addClass('om-dialog-title')
				.attr('id', titleId)
				.html(title)
				.prependTo(uiDialogTitlebar);

		uiDialogTitlebar.find("*").add(uiDialogTitlebar).disableSelection();

		if (options.draggable && $.om.omDraggable) {
			self._makeDraggable();
		}
		if (options.resizable && $.fn.omResizable) {
			self._makeResizable();
		}

		self._createButtons(options.buttons);
		self._isOpen = false;

		if ($.fn.bgiframe) {
			uiDialog.bgiframe();
		}
	},

	_init: function() {
		if ( this.options.autoOpen ) {
			this.open();
		}
	},

	destroy: function() {
		var self = this;
		
		if (self.overlay) {
			self.overlay.destroy();
		}
		self.uiDialog.hide();
		self.element
			.unbind('.dialog')
			.removeData('dialog')
			.removeClass('om-dialog-content om-widget-content')
			.hide().appendTo('body');
		self.uiDialog.remove();

		if (self.originalTitle) {
			self.element.attr('title', self.originalTitle);
		}

		return self;
	},

	widget: function() {
		return this.uiDialog;
	},

	/**
     * 关闭对话框.
     * @name omDialog#close
     * @function
     * @returns 支持链式操作，返回JQuery对象
     * @example
     * var store = $("#select").omDialog('close');
     * 
     */
	close: function(event) {
		var self = this,
			maxZ, thisZ,
			options = this.options,
			onBeforeClose = options.onBeforeClose,
			onClose = options.onClose;
		
		if (onBeforeClose && false === self._trigger("onBeforeClose",event)) {
			return;
		}

		if (self.overlay) {
			self.overlay.destroy();
		}
		self.uiDialog.unbind('keypress.om-dialog');

		self._isOpen = false;

		if (self.options.hide) {
			self.uiDialog.hide(self.options.hide, function() {
                onClose && self._trigger("onClose",event);
			});
		} else {
			self.uiDialog.hide();
			onClose && self._trigger("onClose",event);
		}

		$.om.omDialog.overlay.resize();

		// adjust the maxZ to allow other modal dialogs to continue to work (see #4309)
		if (self.options.modal) {
			maxZ = 0;
			$('.om-dialog').each(function() {
				if (this !== self.uiDialog[0]) {
					thisZ = $(this).css('z-index');
					if(!isNaN(thisZ)) {
						maxZ = Math.max(maxZ, thisZ);
					}
				}
			});
			$.om.omDialog.maxZ = maxZ;
		}

		return self;
	},

	/**
     * 判断对话框是否已打开。
     * @name omDialog#isOpen
     * @function
     * @returns 如果对话框已打开返回true，否则返回false
     * @example
     * var isOpen = $("#select").omDialog('isOpen');
     * 
     */
	isOpen: function() {
		return this._isOpen;
	},

	// the force parameter allows us to move modal dialogs to their correct
	// position on open
	moveToTop: function(force, event) {
		var self = this,
			options = self.options,
			saveScroll;

		if ((options.modal && !force) ||
			(!options.stack && !options.modal)) {
			return self._trigger('onFocus', event);
		}

		if (options.zIndex > $.om.omDialog.maxZ) {
			$.om.omDialog.maxZ = options.zIndex;
		}
		if (self.overlay) {
			$.om.omDialog.maxZ += 1;
			self.overlay.$el.css('z-index', $.om.omDialog.overlay.maxZ = $.om.omDialog.maxZ);
		}

		//Save and then restore scroll since Opera 9.5+ resets when parent z-Index is changed.
		//  http://ui.jquery.com/bugs/ticket/3193
		saveScroll = { scrollTop: self.element.scrollTop(), scrollLeft: self.element.scrollLeft() };
		$.om.omDialog.maxZ += 1;
		self.uiDialog.css('z-index', $.om.omDialog.maxZ);
		self.element.attr(saveScroll);
		self._trigger('onFocus', event);

		return self;
	},

	/**
     * 打开对话框。
     * @name omDialog#open
     * @function
     * @returns 支持链式操作，返回JQuery对象
     * @example
     * var store = $("#select").omDialog('open');
     * 
     */
	open: function() {
		if (this._isOpen) { return; }

		var self = this,
			options = self.options,
			uiDialog = self.uiDialog;

		self.overlay = options.modal ? new $.om.omDialog.overlay(self) : null;
		self._size();
		self._position(options.position);
		uiDialog.show(options.show);
		self.moveToTop(true);

		// prevent tabbing out of modal dialogs
		if (options.modal) {
			uiDialog.bind('keypress.om-dialog', function(event) {
				if (event.keyCode !== $.om.keyCode.TAB) {
					return;
				}

				var tabbables = $(':tabbable', this),
					first = tabbables.filter(':first'),
					last  = tabbables.filter(':last');

				if (event.target === last[0] && !event.shiftKey) {
					first.focus(1);
					return false;
				} else if (event.target === first[0] && event.shiftKey) {
					last.focus(1);
					return false;
				}
			});
		}

		// set focus to the first tabbable element in the content area or the first button
		// if there are no tabbable elements, set focus on the dialog itself
		$(self.element.find(':tabbable').get().concat(
			uiDialog.find('.om-dialog-buttonpane :tabbable').get().concat(
				uiDialog.get()))).eq(0).focus();

		self._isOpen = true;
		var onOpen = options.onOpen;
		if(onOpen){
		    self._trigger("onOpen");
		}
		return self;
	},

	_createButtons: function(buttons) {
		var self = this,
			hasButtons = false,
			uiDialogButtonPane = $('<div></div>')
				.addClass(
					'om-dialog-buttonpane ' +
					'om-helper-clearfix'
				),
			uiButtonSet = $( "<div></div>" )
				.addClass( "om-dialog-buttonset" )
				.appendTo( uiDialogButtonPane );

		// if we already have a button pane, remove it
		self.uiDialog.find('.om-dialog-buttonpane').remove();

		if (typeof buttons === 'object' && buttons !== null) {
			$.each(buttons, function() {
				return !(hasButtons = true);
			});
		}
		if (hasButtons) {
			$.each(buttons, function(name, props) {
				props = $.isFunction( props ) ?
					{ click: props, text: name } :
					props;
				var button = $('<button type="button"></button>')
					.click(function() {
						props.click.apply(self.element[0], arguments);
					})
					.appendTo(uiButtonSet);
				// can't use .attr( props, true ) with jQuery 1.3.2.
				$.each( props, function( key, value ) {
					if ( key === "click" ) {
						return;
					}
					if ( key in attrFn ) {
						button[ key ]( value );
					} else {
						button.attr( key, value );
					}
				});
				if ($.fn.omButton) {
					button.omButton();
				}
			});
			uiDialogButtonPane.appendTo(self.uiDialog);
		}
	},

	_makeDraggable: function() {
		var self = this,
			options = self.options,
			doc = $(document),
			heightBeforeDrag;

		function filteredUi(ui) {
			return {
				position: ui.position,
				offset: ui.offset
			};
		}

		self.uiDialog.omDraggable({
			cancel: '.om-dialog-content, .om-dialog-titlebar-close',
			handle: '.om-dialog-titlebar',
			containment: 'document',
			cursor: 'move',
			onStart: function(ui, event) {
				heightBeforeDrag = options.height === "auto" ? "auto" : $(this).height();
				$(this).height($(this).height()).addClass("om-dialog-dragging");
				self._trigger('onDragStart', filteredUi(ui), event);
			},
			onDrag: function(ui, event) {
				self._trigger('onDrag', filteredUi(ui), event);
			},
			onStop: function(ui, event) {
				options.position = [ui.position.left - doc.scrollLeft(),
					ui.position.top - doc.scrollTop()];
				$(this).removeClass("om-dialog-dragging").height(heightBeforeDrag);
				self._trigger('onDragStop', filteredUi(ui), event);
				$.om.omDialog.overlay.resize();
			}
		});
	},

	_makeResizable: function(handles) {
		handles = (handles === undefined ? this.options.resizable : handles);
		var self = this,
			options = self.options,
			// .ui-resizable has position: relative defined in the stylesheet
			// but dialogs have to use absolute or fixed positioning
			position = self.uiDialog.css('position'),
			resizeHandles = (typeof handles === 'string' ?
				handles	:
				'n,e,s,w,se,sw,ne,nw'
			);

		function filteredUi(ui) {
			return {
				originalPosition: ui.originalPosition,
				originalSize: ui.originalSize,
				position: ui.position,
				size: ui.size
			};
		}

		self.uiDialog.omResizable({
			cancel: '.om-dialog-content',
			containment: 'document',
			alsoResize: self.element,
			maxWidth: options.maxWidth,
			maxHeight: options.maxHeight,
			minWidth: options.minWidth,
			minHeight: self._minHeight(),
			handles: resizeHandles,
			start: function(event, ui) {
				$(this).addClass("om-dialog-resizing");
				self._trigger('onResizeStart', event, filteredUi(ui));
			},
			resize: function(event, ui) {
				self._trigger('onResize', event, filteredUi(ui));
			},
			stop: function(event, ui) {
				$(this).removeClass("om-dialog-resizing");
				options.height = $(this).height();
				options.width = $(this).width();
				self._trigger('onResizeStop', event, filteredUi(ui));
				$.om.omDialog.overlay.resize();
			}
		})
		.css('position', position)
		.find('.om-resizable-se').addClass('om-icon om-icon-grip-diagonal-se');
	},

	_minHeight: function() {
		var options = this.options;

		if (options.height === 'auto') {
			return options.minHeight;
		} else {
			return Math.min(options.minHeight, options.height);
		}
	},

	_position: function(position) {
		var myAt = [],
			offset = [0, 0],
			isVisible;

		if (position) {
			// deep extending converts arrays to objects in jQuery <= 1.3.2 :-(
	//		if (typeof position == 'string' || $.isArray(position)) {
	//			myAt = $.isArray(position) ? position : position.split(' ');

			if (typeof position === 'string' || (typeof position === 'object' && '0' in position)) {
				myAt = position.split ? position.split(' ') : [position[0], position[1]];
				if (myAt.length === 1) {
					myAt[1] = myAt[0];
				}

				$.each(['left', 'top'], function(i, offsetPosition) {
					if (+myAt[i] === myAt[i]) {
						offset[i] = myAt[i];
						myAt[i] = offsetPosition;
					}
				});

				position = {
					my: myAt.join(" "),
					at: myAt.join(" "),
					offset: offset.join(" ")
				};
			} 

			position = $.extend({}, $.om.omDialog.prototype.options.position, position);
		} else {
			position = $.om.omDialog.prototype.options.position;
		}

		// need to show the dialog to get the actual offset in the position plugin
		isVisible = this.uiDialog.is(':visible');
		if (!isVisible) {
			this.uiDialog.show();
		}
		this.uiDialog
			// workaround for jQuery bug #5781 http://dev.jquery.com/ticket/5781
			.css({ top: 0, left: 0 })
			.position($.extend({ of: window }, position));
		if (!isVisible) {
			this.uiDialog.hide();
		}
	},

	_setOptions: function( options ) {
		var self = this,
			resizableOptions = {},
			resize = false;

		$.each( options, function( key, value ) {
			self._setOption( key, value );
			
			if ( key in sizeRelatedOptions ) {
				resize = true;
			}
			if ( key in resizableRelatedOptions ) {
				resizableOptions[ key ] = value;
			}
		});

		if ( resize ) {
			this._size();
		}
		if ( this.uiDialog.is( ":data(resizable)" ) ) {
			this.uiDialog.omResizable( "option", resizableOptions );
		}
	},

	_setOption: function(key, value){
		var self = this,
			uiDialog = self.uiDialog;

		switch (key) {
			case "buttons":
				self._createButtons(value);
				break;
			case "closeText":
				// ensure that we always pass a string
				self.uiDialogTitlebarCloseText.text("" + value);
				break;
			case "dialogClass":
				uiDialog
					.removeClass(self.options.dialogClass)
					.addClass(uiDialogClasses + value);
				break;
			case "disabled":
				if (value) {
					uiDialog.addClass('om-dialog-disabled');
				} else {
					uiDialog.removeClass('om-dialog-disabled');
				}
				break;
			case "draggable":
				var isDraggable = uiDialog.is( ":data(draggable)" );
				if ( isDraggable && !value ) {
					uiDialog.omDraggable( "destroy" );
				}
				
				if ( !isDraggable && value ) {
					self._makeDraggable();
				}
				break;
			case "position":
				self._position(value);
				break;
			case "resizable":
				// currently resizable, becoming non-resizable
				var isResizable = uiDialog.is( ":data(resizable)" );
				if (isResizable && !value) {
					uiDialog.omResizable('destroy');
				}

				// currently resizable, changing handles
				if (isResizable && typeof value === 'string') {
					uiDialog.omResizable('option', 'handles', value);
				}

				// currently non-resizable, becoming resizable
				if (!isResizable && value !== false) {
					self._makeResizable(value);
				}
				break;
			case "title":
				// convert whatever was passed in o a string, for html() to not throw up
				$(".om-dialog-title", self.uiDialogTitlebar).html("" + (value || '&#160;'));
				break;
		}

		$.OMWidget.prototype._setOption.apply(self, arguments);
	},

	_size: function() {
		/* If the user has resized the dialog, the .ui-dialog and .ui-dialog-content
		 * divs will both have width and height set, so we need to reset them
		 */
		var options = this.options,
			nonContentHeight,
			minContentHeight,
			isVisible = this.uiDialog.is( ":visible" );

		// reset content sizing
		this.element.show().css({
			width: 'auto',
			minHeight: 0,
			height: 0
		});

		if (options.minWidth > options.width) {
			options.width = options.minWidth;
		}

		// reset wrapper sizing
		// determine the height of all the non-content elements
		nonContentHeight = this.uiDialog.css({
				height: 'auto',
				width: options.width
			})
			.height();
		minContentHeight = Math.max( 0, options.minHeight - nonContentHeight );
		
		if ( options.height === "auto" ) {
			// only needed for IE6 support
			if ( $.support.minHeight ) {
				this.element.css({
					minHeight: minContentHeight,
					height: "auto"
				});
			} else {
				this.uiDialog.show();
				var autoHeight = this.element.css( "height", "auto" ).height();
				if ( !isVisible ) {
					this.uiDialog.hide();
				}
				this.element.height( Math.max( autoHeight, minContentHeight ) );
			}
		} else {
			this.element.height( Math.max( options.height - nonContentHeight, 0 ) );
		}

		if (this.uiDialog.is(':data(resizable)')) {
			this.uiDialog.omResizable('option', 'minHeight', this._minHeight());
		}
	}
});

$.extend($.om.omDialog, {
	version: "@VERSION",

	uuid: 0,
	maxZ: 0,

	getTitleId: function($el) {
		var id = $el.attr('id');
		if (!id) {
			this.uuid += 1;
			id = this.uuid;
		}
		return 'ui-dialog-title-' + id;
	},

	overlay: function(dialog) {
		this.$el = $.om.omDialog.overlay.create(dialog);
	}
});

$.extend($.om.omDialog.overlay, {
	instances: [],
	// reuse old instances due to IE memory leak with alpha transparency (see #5185)
	oldInstances: [],
	maxZ: 0,
	events: $.map('focus,mousedown,mouseup,keydown,keypress,click'.split(','),
		function(event) { return event + '.dialog-overlay'; }).join(' '),
	create: function(dialog) {
		if (this.instances.length === 0) {
			// prevent use of anchors and inputs
			// we use a setTimeout in case the overlay is created from an
			// event that we're going to be cancelling (see #2804)
			setTimeout(function() {
				// handle $(el).dialog().dialog('close') (see #4065)
				if ($.om.omDialog.overlay.instances.length) {
					$(document).bind($.om.omDialog.overlay.events, function(event) {
						// stop events if the z-index of the target is < the z-index of the overlay
						// we cannot return true when we don't want to cancel the event (#3523)
						if ($(event.target).zIndex() < $.om.omDialog.overlay.maxZ) {
							return false;
						}
					});
				}
			}, 1);

			// allow closing by pressing the escape key
			$(document).bind('keydown.dialog-overlay', function(event) {
				if (dialog.options.closeOnEscape && event.keyCode &&
					event.keyCode === $.om.keyCode.ESCAPE) {
					
					dialog.close(event);
					event.preventDefault();
				}
			});

			// handle window resize
			$(window).bind('resize.dialog-overlay', $.om.omDialog.overlay.resize);
		}

		var $el = (this.oldInstances.pop() || $('<div></div>').addClass('om-widget-overlay'))
			.appendTo(document.body)
			.css({
				width: this.width(),
				height: this.height()
			});

		if ($.fn.bgiframe) {
			$el.bgiframe();
		}

		this.instances.push($el);
		return $el;
	},

	destroy: function($el) {
	    $el.parent().unbind(this.__removeBind);
		var indexOf = $.inArray($el, this.instances);
		if (indexOf != -1){
			this.oldInstances.push(this.instances.splice(indexOf, 1)[0]);
		}

		if (this.instances.length === 0) {
			$([document, window]).unbind('.dialog-overlay');
		}

		$el.remove();
		
		// adjust the maxZ to allow other modal dialogs to continue to work (see #4309)
		var maxZ = 0;
		$.each(this.instances, function() {
			maxZ = Math.max(maxZ, this.css('z-index'));
		});
		this.maxZ = maxZ;
	},

	height: function() {
		var scrollHeight,
			offsetHeight;
		// handle IE 6
		if ($.browser.msie && $.browser.version < 7) {
			scrollHeight = Math.max(
				document.documentElement.scrollHeight,
				document.body.scrollHeight
			);
			offsetHeight = Math.max(
				document.documentElement.offsetHeight,
				document.body.offsetHeight
			);

			if (scrollHeight < offsetHeight) {
				return $(window).height() + 'px';
			} else {
				return scrollHeight + 'px';
			}
		// handle "good" browsers
		} else {
			return $(document).height() + 'px';
		}
	},

	width: function() {
		var scrollWidth,
			offsetWidth;
		// handle IE
		if ( $.browser.msie ) {
			scrollWidth = Math.max(
				document.documentElement.scrollWidth,
				document.body.scrollWidth
			);
			offsetWidth = Math.max(
				document.documentElement.offsetWidth,
				document.body.offsetWidth
			);

			if (scrollWidth < offsetWidth) {
				return $(window).width() + 'px';
			} else {
				return scrollWidth + 'px';
			}
		// handle "good" browsers
		} else {
			return $(document).width() + 'px';
		}
	},

	resize: function() {
		/* If the dialog is draggable and the user drags it past the
		 * right edge of the window, the document becomes wider so we
		 * need to stretch the overlay. If the user then drags the
		 * dialog back to the left, the document will become narrower,
		 * so we need to shrink the overlay to the appropriate size.
		 * This is handled by shrinking the overlay before setting it
		 * to the full document size.
		 */
		var $overlays = $([]);
		$.each($.om.omDialog.overlay.instances, function() {
			$overlays = $overlays.add(this);
		});

		$overlays.css({
			width: 0,
			height: 0
		}).css({
			width: $.om.omDialog.overlay.width(),
			height: $.om.omDialog.overlay.height()
		});
	}
});

$.extend($.om.omDialog.overlay.prototype, {
	destroy: function() {
		$.om.omDialog.overlay.destroy(this.$el);
	}
});

}(jQuery));
/*
 * $Id: om-grid.js,v 1.164 2012/06/29 09:20:32 chentianzhen Exp $
 * operamasks-ui omGrid @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 * 
 * Depends:
 *  om-core.js
 *  om-mouse.js
 *  om-resizable.js
 */
 
/**
     * @name omGrid
     * @class 表格组件。类似于html中的table，支持后台数据源、分页、自动列宽、单选/多选、行样式、自定义列渲染等功能。<br/><br/>
     * <b>特点：</b><br/>
     * <ol>
     *      <li>使用远程数据源</li>
     *      <li>支持分页展现</li>
     *      <li>自动添加行号</li>
     *      <li>允许某列的宽度自动扩充（该列宽度等于表格总宽度送去其它列宽度之和）</li>
     *      <li>允许所有列自动缩放（自动缩放各列的宽度，使得它适应表格的总宽度）</li>
     *      <li>可以定制隔行样式，也可以根据记录的不同使用不同的行样式</li>
     *      <li>可以定制各列的显示效果</li>
     *      <li>可以设置表头和表体自动换行</li>
     *      <li>可以改变列宽</li>
     *      <li>提供丰富的事件</li>
     * </ol>
     * 
     * <b>示例：</b><br/>
     * <pre>
     * &lt;script type="text/javascript" >
     *  $(document).ready(function() {
     *      $('#mytable').omGrid({
     *          height : 250,
     *          width : 600,
     *          limit : 8, //分页显示，每页显示8条
     *          singleSelect : false, //出现checkbox列，可以选择同时多行记录
     *          colModel : [    {header:'编号',name:'id', width:100, align : 'center'},
     *                          {header:'地区',name:'city', width:250, align : 'left',wrap:true},
     *                          {header:'地址',name:'address', width:'autoExpand',renderer:function(value,rowData,rowIndex){ return '&lt;b>'+value+'&lt/b>'; }}
     *          ],
     *          dataSource : 'griddata.json' //后台取数的URL
     *      });
     *  });
     * &lt;/script>
     * 
     * &lt;table id="mytable"/>
     * </pre>
     * 
     * 后台返回的数据格式如下（可以不包含空格换行等格式内容，大括号内的各属性顺序可任意交换）：<br/>
     * <pre>
     * {"total":126, "rows":
     *     [
     *         {"address":"CZ88.NET ","city":"IANA保留地址","id":"1"},
     *         {"address":"CZ88.NET ","city":"澳大利亚","id":"2"},
     *         {"address":"电信","city":"福建省","id":"3"},
     *         {"address":"CZ88.NET ","city":"澳大利亚","id":"4"},
     *         {"address":"CZ88.NET ","city":"泰国","id":"5"},
     *         {"address":"CZ88.NET ","city":"日本","id":"6"},
     *         {"address":"电信","city":"广东省","id":"7"},
     *         {"address":"CZ88.NET ","city":"日本","id":"8"}
     *     ]
     * }
     * </pre>
     * 
     * <b>其它特殊说明：</b><br/>
     * 单击一行时有可能会触发onRowSelect、onRowDeselect、onRowClick这些事件中一个一个或多个。具体结果是这样的：
     * <ol>
     *     <li>单选（一次只能选择一行）表格：①如果该行还未被选中，先触发其它已选择的行的onRowDeselect事件监听再触发该行的onRowSelect事件监听②触发该行的onRowClick事件监听。</li>
     *     <li>多选（一次可以选择多行）表格：①如果该行还未被选中，先触发该行的onRowSelect事件监听，如果该行已经选中，则先触发该行的onRowDeselect事件监听②触发该行的onRowClick事件监听。</li>
     * </ol>
     * 
     * 请求参数说明
     * <ol>
     * 		<li>组件在请求数据时会加上start和limit两个参数。比如请求第一页数据时url会自动添加上 start=0&limit=20。 </li>
     * </ol>
     * @constructor
     * @description 构造函数. 
     * @param p 标准config对象：{}
     */
;(function($) {
    $.omWidget('om.omGrid', {
        options:/** @lends omGrid#*/{
            //外观
            /**
             * 表格高度，设为数字时单位为px,也可以设为'fit'，表示自适应父容器高度。
             * @default 462
             * @type Number
             * @example
             * $('.selector').omGrid({height : 300});
             */
            height:462,
            /**
             * 表格宽度，设为数字时单位为px,也可以设为'fit'，表示自适应父容器宽度。
             * @type Number,String
             * @default '100%'
             * @example
             * $('.selector').omGrid({width : 600});
             */
            width:'100%',
            /**
             * 列数据模型。每一个元素都是一个对象字面量，定义该列的各个属性，这些属性包括:<br/>
             * header : 表头文字。<br/>
             * name : 与数据模型对应的字段。<br/>
             * align : 列文字对齐方式，可以为'left'、'center'、'right'之中的一个。<br/>
             * renderer : 列的渲染函数，接受3个参数，v表示当前值，rowData表示当前行数据，rowIndex表示当前行号(从0开始)，<br/>
             * width : 列的宽度，取值为Number或者'autoExpand'。注意只能有一个列被设置为'autoExpand'属性。<br/>
             * wrap : 是否自动换行，取值为true或者false。<br/>
             * @type Array[JSON]
             * @default false
             * @example
             * 
             * $(".selector").omGrid({
             *      colModel : [ {
             *              header : '地区',          //表头文字
             *              name : 'city',          //与数据模型对应的字段
             *              width : 120,            //列宽,可设置具体数字，也可设置为'autoExpand'，表示自动扩展
             *              align : 'left',         //列文字对齐
             *              renderer : function(v, rowData , rowIndex) {   //列渲染函数，接受3个参数，v表示当前值，rowData表示当前行数据，rowIndex表示当前行号(从0开始)
             *                  return '&lt;b>'+v+'&lt;/b>';  //地区这一列的文字加粗显示
             *              }
             *          }, {
             *              header : '地址',
             *              name : 'address',
             *              align : 'left',
             *              width : 'autoExpand'
             *          } 
             *      ]
             * });
             */
            colModel:false,
            /**
             * 是否自动拉伸各列以适应表格的宽度（比如共2列第一列宽度100第二列宽度200，则当表格总宽度是600px时第一列自动会变成200px第二列宽度会自动变成400px，而如果表格总宽度是210px时第一列自动会变成70px第二列宽度会自动变成140px）。<b>注意：只有所有列的宽度都不是'autoExpand'时该属性才会起作用。</b>
             * @default false
             * @type Boolean
             * @example
             * $('.selector').omGrid({autoFit : true});
             */
            autoFit:false,
            /**
             * 是否在最左边显示序号列。
             * @default true
             * @type Boolean
             * @example
             * $('.selector').omGrid({showIndex : false});
             */
            showIndex:true,
            //数据源
            /**
             * ajax取数方式对应的url地址。
             * @type String
             * @default 无
             * @example
             * //下面的示例设置的url，表示将从griddata.json这个地址取数，同时附带有start和limit两个请求参数。
             * //该文件必须返回一段具有特定格式（格式可参考文档的“预览”页签的说明）的JSON数据，omGrid拿到该数据即可用来填充表格。
             * $('.selector').omGrid({url:'griddata.json'});
             */
            dataSource:false,
             /**
             * ajax取数时附加到请求的额外参数。<b>注意：这里JSON的value值只能使用普通值，比如可以设置为{key1:1,key2:'2',key3:0.2,key4:true,key5:undefined}这样，但是不可以设置为{key1:[]}或{key2:{a:1,b:2}}，因为[]和{a:1,b:2}都不是普通值</b>
             * @type JSON
             * @default {}
             * @example
             * //下面的示例在Ajax取数时将从griddata.json这个地址取数，同时附带有start、limit、googType、localtion这4个请求参数。
             * //真正的URL地址可能是griddata.json?start=0&limit=10&goodType=1&location=beijing
             * $('.selector').omGrid({url:'griddata.json',extraData:{googType:1,localtion:'beijing'} });
             */
            extraData:{},
            /**
             * 使用GET请求还是POST请求来取数据，取值为：'POST'或'GET'。
             * @type String
             * @default 'GET'
             * @example
             * $('.selector').omGrid({method : 'POST'});
             */
            method:'GET',
            /**
             * 正在取数时显示在分页条上的提示。
             * @name omGrid#loadingMsg
             * @type String
             * @default '正在加载数据，请稍候...'
             * @example
             * $('.selector').omGrid({loadingMsg : '取数中...'});
             */
            //loadingMsg:$.om.lang.omGrid.loadingMsg,
            /**
             * 取数完成后但是后台没有返回任何数据时显示在分页条上的提示。
             * @name omGrid#emptyMsg
             * @type String
             * @default '没有数据'
             * @example
             * $('.selector').omGrid({emptyMsg : 'No data!'});
             */
            //emptyMsg:$.om.lang.omGrid.emptyMsg,
            /**
             * 取数发生错误时显示在分页条上的提示。
             * @name omGrid#errorMsg
             * @type String
             * @default '取数出错'
             * @example
             * $('.selector').omGrid({emptyMsg : '应用异常，请与网站管理员联系!'});
             */
            //errorMsg:$.om.lang.omGrid.errorMsg,
            /**
             * 取数成功后的预处理，可以在取数成功后开始显示数据前对后台返回的数据进行一次预处理。<b>注意：此方法一定要返回一个值</b>。
             * @type Function
             * @default 无
             * @example
             * //将后台返回的数据中所有记录的id属性改名成name属性，并将sex中的0/1分别转换为'男'或'女'。
             * //如后台返回{"total":35,"rows":[{id:1,sex:0,password:'abc'},{id:2,sex:1,password:'def'}]}
             * //转换后结果为{"total":35,"rows":[{name:1,sex:'男',password:'abc'},{name:2,sex:'女',password:'def'}]}
             * $('.selector').omGrid({preProcess : function(data){
             *          var temp;
             *          for(var i=0,len=data.rows.length;i&lt;len;i++){
             *              temp=data.rows[i];
             *              temp.name=temp.id;
             *              temp.id=undefined;
             *              temp.sex= temp.sex==0?'男':'女';
             *          }
             *          return data;
             *      }
             * });
             */
            preProcess:false,
            //分页
            /**
             * 每页数据条数，比如每页要显示10条则设成10。<b>注意：如果设成0或负数则不分页</b>。此属性仅用于取数不用于显示（即如果limit设成10，取数时告诉后台要10条数据，如果后台非要返回15条数据，则页面会显示出15条而不是10条数据）。
             * @type Number
             * @default 15
             * @example
             * $('.selector').omGrid({limit : 15});
             */
            limit:15,
            /**
             * 显示在分页条上“上一页”和“下一页”按钮之间的文字。在显示时其中的{totalPage}会被替换为总页数，{index}会被替换为一个输入框（默认显示当前的页号，用户可以输入任意数字然后回车来跳转到指定的页）。
             * @name omGrid#pageText
             * @type String
             * @default '第{index}页，共{totalPage}页'
             * @example
             * $('.selector').omGrid({pageText : '共{totalPage}页，转到{index}页'});
             */
            //pageText:$.om.lang.omGrid.pageText,
            /**
             * 显示在分页条上的统计文字。在显示时其中的{total}会被替换为总记录数，{from}和{to}会被替换为当前显示的起止行号。比如可能会显示成'共125条数据，显示21-30条'。
             * @name omGrid#pageStat
             * @type String
             * @default '共{total}条数据，显示{from}-{to}条'
             * @example
             * $('.selector').omGrid({pageStat : '总共有{total}条记录，当前正在显示第{from}行至第{to}行'});
             */
            //pageStat:$.om.lang.omGrid.pageStat,
            //行显示
            /**
             * 行样式，默认显示成斑马纹（奇偶行背景不一样）。当然用户也可以定义成3行一循环或5行一循环。也可以定义成一个Function来根据行数据不同显示成不同的样式（比如一个显示学生成绩的表格中把不及格的记录整行显示成红色背景，满分的记录整行显示成绿色背景）。
             * @type Array或Function
             * @default ['oddRow','evenRow']
             * @example
             * 
             * //示例1：结果表格中第1/4/7/10...行的tr会加上样式class1；
             * //第2/5/8/11...行的tr会加上样式class2；
             * //第3/6/9/12...行的tr会加上样式class3
             * $('.selector').omGrid({rowClasses : ['class1','class2','class2']});
             * 
             * //示例2：满分的行加上样式fullMarks，不及格的行加上样式flunk，其它行使用默认样式。
             * $('.selector').omGrid({rowClasses : function(rowIndex,rowData){
             *          if(rowData.score==100){
             *              reuturn 'fullMarks';
             *          }else if(rowData.score<60){
             *              return 'flunk';
             *          }
             *      }
             * });
             */
            rowClasses:['oddRow','evenRow'],
            //行选择
            /**
             * 是否只能单选（一次只能选择一条记录，选择第二条时第一条会自动取消选择）。若设置为false表示可以多选（选择其它行时原来已经选择的将继续保持选择状态）。<b>注意：设成true时将不会出现checkbox列，设成false则将自动出现checkbox列</b>。
             * @type Boolean
             * @default true
             * @example
             * $('.selector').omGrid({singleSelect : false});
             */
            singleSelect:true,
            
            /**
             * 设置组件的标题
             * @type String
             * @default ''
             * @example
             * $('.selector').omGrid({title : 'Data Grid'});
             */
            title: '',
            
            //event
            /**
             * 选择一行记录后执行的方法。
             * @event
             * @type Function
             * @param rowIndex 行号（从0开始）
             * @param rowData 选择的行所代表的JSON对象
             * @param event jQuery.Event对象。
             * @default 无
             * @example
             *  $(".selector").omGrid({
             *      onRowSelect:function(rowIndex,rowData,event){
             *          alert('the '+rowIndex+'th row has been selected!');
             *      }
             *  });
             */
            onRowSelect:function(rowIndex,rowData,event){},
            /**
             * 取消一行记录的选择后执行的方法。
             * @event
             * @type Function
             * @param rowIndex 行号（从0开始）
             * @param rowData 选择的行所代表的JSON对象
             * @param event jQuery.Event对象
             * @default 无
             * @example
             *  $(".selector").omGrid({
             *      onRowDeselect:function(rowIndex,rowData,event){
             *          alert('the '+rowIndex+'th row has been deselected!');
             *      }
             *  });
             */
            onRowDeselect:function(rowIndex,rowData,event){},
            /**
             * 单击一行记录后执行的方法。
             * @event
             * @type Function
             * @param rowIndex 行号（从0开始）
             * @param rowData 选择的行所代表的JSON对象
             * @param event jQuery.Event对象
             * @default 无
             * @example
             *  $(".selector").omGrid({
             *      onRowClick:function(rowIndex,rowData,event){
             *          alert('the '+rowIndex+'th row has been clicked!city='+rowData.city);
             *      }
             *  });
             */
            onRowClick:function(rowIndex,rowData,event){},
            /**
             * 双击一行记录后执行的方法。
             * @event
             * @type Function
             * @param rowIndex 行号（从0开始）
             * @param rowData 选择的行所代表的JSON对象
             * @param event jQuery.Event对象
             * @default 无
             * @example
             *  $(".selector").omGrid({
             *      onRowDblClick:function(rowIndex,rowData,event){
             *          alert('the '+rowIndex+'th row has been double clicked!city='+rowData.city);
             *      }
             *  });
             */
            onRowDblClick:function(rowIndex,rowData,event){},
            /**
             * 改变分页<b>之前</b>执行的方法。<b>注意：如果此方法返回false则不进行分页切换或跳转</b>。
             * @event
             * @type Function
             * @param type 切换类型，是'first'、'prev'、'next'、'last'、'input'之一。
             * @param newPage 要转到的页号（从1开始，第一页是1而不是0）
             * @param event jQuery.Event对象
             * @default 无
             * @example
             *  $(".selector").omGrid({
             *      onPageChange:function(type,newPage,event){
             *          alert('will goto page '+newPage);
             *      }
             *  });
             */
            onPageChange:function(type,newPage,event){},
            /**
             * 从后台取数成功时执行的方法。
             * @event
             * @type Function
             * @param data 取回来的数据（ 格式是{"total":35,"rows":[{"id":11,"city":"河南省安阳市","address":"电信"},{"id":12,"city":"北京市","address":"北龙中网科技有限公司"},{"id":13,"city":"澳大利亚","address":"CZ88.NET"}]}  ）。
             * @param testStatus 响应的状态（参考jQuery的$.ajax的success属性）
             * @param XMLHttpRequest XMLHttpRequest对象（参考jQuery的$.ajax的success属性）
             * @param event jQuery.Event对象
             * @default 无
             * @example
             *  $(".selector").omGrid({
             *      onSuccess:function(data,testStatus,XMLHttpRequest,event){
             *          alert('fetch data success,got '+data.rows+' rows');
             *      }
             *  });
             */
            onSuccess:function(data,testStatus,XMLHttpRequest,event){},
            /**
             * 从后台取数失败时执行的方法。
             * @event
             * @type Function
             * @param XMLHttpRequest XMLHttpRequest对象（参考jQuery的$.ajax的error属性）
             * @param testStatus 响应的状态（参考jQuery的$.ajax的error属性）
             * @param errorThrown 捕获的异常对象（参考jQuery的$.ajax的error属性）
             * @param event jQuery.Event对象
             * @default 无
             * @example
             *  $(".selector").omGrid({
             *      onError:function(XMLHttpRequest,textStatus,errorThrown,event){
             *          alert('fetch data error');
             *      }
             *  });
             */
            onError:function(XMLHttpRequest,textStatus,errorThrown,event){},
            /**
             * 数据已全部显示到表体中后执行的方法。
             * @event
             * @type Function
             * @param nowPage 当前页号(第一页是1第二页是2)
             * @param pageRecords 当前页的所有记录
             * @param event jQuery.Event对象
             * @default 无
             * @example
             * //数据显示完后自动选中所有地址是'电信'的行。
             *  $(".selector").omGrid({
             *      signleSelect:false,
             *      onRefresh:function(nowPage,pageRecords,event){
             *          var rows=[];
             *          $(pageRecords).each(function(i){
             *              if(this.address=='电信'){
             *                  rows.push(i);
             *              }
             *          });
             *          $('.selector').omGrid('setSelections',rows);
             *      }
             *  });
             */
            onRefresh:function(nowPage,pageRecords,event){},
            /**
             *当重新刷新时的回调方法列表(仅内部使用)
            */
            _onRefreshCallbacks : [],
            
            /**
             * 当标题列改变大小后的回调事件列表，主要用于行编辑插件。(仅内部使用)
             */
            _onResizableStopCallbacks : [],
            
            /**
             * 当标题列改变大小过程中不断触发的回调事件列表(仅内部使用)
             */
            _onResizableCallbacks : [],
            
            /**
             * 当调用resize方法动态改变宽高时的回调事件列表
             */ 
            _onResizeCallbacks : []
        },
        //private methods
        _create:function(){
            var options=this.options,el=this.element.show() // show if hidden
                .attr({
                    cellPadding : 0,
                    cellSpacing : 0,
                    border : 0
                })
                .empty()
                .append('<tbody></tbody>');
            el.wrap('<div class="om-grid om-widget om-widget-content"><div class="bDiv" style="width:auto"></div></div>').closest('.om-grid');
            if(!$.isArray(this._getColModel())){
                return; //如果colModel没设置或值不对，什么也不做
            }
            
            this.hDiv = $('<div class="hDiv om-state-default"></div>').append('<div class="hDivBox"><table cellPadding="0" cellSpacing="0"></table></div>');
            el.parent().before(this.hDiv);
            this.pDiv=$('<div class="pDiv om-state-default"></div>');
            el.parent().after(this.pDiv);
            
            var grid = el.closest('.om-grid');
            this.loadMask=$('<div class="gBlock"><div align="center" class="gBlock-valignMiddle" ><div class="loadingImg" style="display:block"/></div></div>')
                    .mousedown(function(e){
                        return false;  //禁用双击（默认双击全把div下面的内容全选）
                    })
                    .hide();
            grid.append(this.loadMask);
            
            this.titleDiv = $("<div class='titleDiv'></div>");
            grid.prepend(this.titleDiv);
            
            this.tbody=this.element.children().eq(0);
            this._guid = 0;//对于每一行都添加一个 "_row_id"以进行唯一标识
            
            options._onRefreshCallbacks.push(function(){
            	this._refreshHeaderCheckBox();
            });
            
            //事件绑定
            this._bindScrollEnvent();
        },
        _init:function(){
        	var $el=this.element,
        		ops = this.options,
                $grid = $el.closest('.om-grid');
            
            this._measure($grid , ops);

            if(!$.isArray(this._getColModel())){
                return; //如果colModel没设置或值不对，什么也不做
            }
            //远程取数时额外带的参数，注意，这仅供内部使用，外部使用的是 this.options.extraData
            this._extraData = {};
            
            this.tbody.empty();
            $('table', this.hDiv).empty();
            this.pDiv.empty();
            this.titleDiv.empty();
            this._buildTableHead();
            this._buildPagingToolBar();
            this._buildLoadMask();
            this._bindSelectAndClickEnvent();
            this._makeColsResizable();
            this._buildTitle();
            
            this._resetHeight();
            this.pageData={nowPage:1,totalPages:1};
            this._populate();
        },
        /**
		 * 改变组件的大小。
		 * @name omGrid#resize
		 * @function
		 * @param position (1)可以为Object,格式如{width:500,height:250} <br/>
		 *                 (2)只有一个参数表示width,有两个参数时依次表示width,height
		 * @example
		 * $(".selector").omGrid("resize" , 500);<br />//把宽度改为500像素。
		 * $(".selector").omGrid("resize" , 500 , 250);<br />//把宽度改为500像素，高度改为250像素。
		 * $(".selector").omGrid("resize" , {height:300});<br />//把高度改为300像素。
		 * $(".selector").omGrid("resize" , {width:'fit',height:'fit'});<br />//把宽度和高度同时改为自适应父容器大小。
		 * 
		 */
        resize : function(position){
        	var self = this,
        		ops = this.options,
        		$grid = this.element.closest('.om-grid'),
		 		width,
		 		height;

		 	position = position || {};
		 	ops.width = position.width || arguments[0] || ops.width;
		 	ops.height = position.height || arguments[1] || ops.height;
		 	
		 	this._measure($grid , ops);
		 	this._buildLoadMask();
            this._resetWidth();
            this._resetHeight();
            $.each(ops._onResizeCallbacks , function(index , fn){
            	fn.call(self);
            });
        },
        _measure : function($grid , ops){
        	$grid.outerWidth(ops.width==='fit'?$grid.parent().width():ops.width);  
            $grid.outerHeight(ops.height==='fit'?$grid.parent().height():ops.height);
        },
        _resetHeight : function(){
        	var $el = this.element,
                $grid = $el.closest('.om-grid');
        	
        	var headerHeight = this.hDiv.outerHeight(true),
                pagerHeight = this.pDiv.is(":hidden")? 0 : this.pDiv.outerHeight(true),
                titleHeight = this.titleDiv.is(":hidden")? 0 : this.titleDiv.outerHeight(true);
                
            $grid.children('.bDiv').outerHeight($grid.height() - headerHeight - pagerHeight - titleHeight);
        },
        _resetWidth : function(){
        	var ops = this.options,
        		cms = this._getColModel(),
        		autoExpandColIndex = -1,
        		$grid = this.element.closest('.om-grid'),
        		$thead = $('thead',this.hDiv),
        		allColsWidth = 0;
        		
        	$.each(cms , function(index , cm){
        		var cmWidth = cm.width || 60;
        		if(cm.width == 'autoExpand'){
                    cmWidth = 0;
                    autoExpandColIndex = index;
                }
                $thead.find("th[axis='col"+index+"'] >div").width(cmWidth);
				allColsWidth += cmWidth;
        	});
        	
        	this._fixHeaderWidth(autoExpandColIndex , allColsWidth);
        	
            var headerWidth = {};
            $(this._getHeaderCols()).each(function(){
            	headerWidth[$(this).attr("abbr")] = $('div',$(this)).width();
            });
            
            //修正body中各个td宽度
            this.tbody.find("td[abbr]").each(function(index , td){
            	var name = $(td).prop("abbr");
            	if(headerWidth[name] != null){
            		$(td).find(">div:first").width(headerWidth[name] );
            	}
            });
        }, 
        _getColModel : function(){
        	return this.options.colModel;
        },
        _buildTitle : function() {
        	var $title = this.titleDiv;
            if (this.options.title) {
                $title.html("<div class='titleContent'>" + this.options.title + "</div>").show();
            }else{
            	$title.empty().hide();
            }
        },
        _fixHeaderWidth:function(autoExpandColIndex , allColsWidth){
        	var $grid = this.element.closest('.om-grid'),
        		$thead = $('thead',this.hDiv),
        		cms = this._getColModel(),
        		ops = this.options;
        	
        	if(autoExpandColIndex != -1){ //说明有某列要自动扩充
                var tableWidth = $grid.width() - 32,
                	toBeExpandedTh = $thead.find('th[axis="col'+autoExpandColIndex+'"] div');
                	
                //虽然toBeExpandedTh.parent().width()为0,但不同浏览器在计算下边的thead.width()竟然有差异(Chrome)，所以干脆先隐藏了，保证所有浏览器计算thead.width()值一致
                toBeExpandedTh.parent().hide();
                var usableWidth = tableWidth - $thead.width();
                toBeExpandedTh.parent().show();
                if(usableWidth <= 0){
                    toBeExpandedTh.css('width',60);
                }else{
                    toBeExpandedTh.css('width',usableWidth);
                }
            }else if(ops.autoFit){
                var tableWidth = $grid.width() - 22,
                    usableWidth = tableWidth - $thead.width(),
                    percent = 1 + usableWidth/allColsWidth,
                    toFixedThs = $thead.find('th[axis^="col"] >div');
                 
                $.each(cms , function(index){
                	var $th = toFixedThs.eq(index);
                    $th.width(parseInt($th.width()*percent));
                }); 
            }
        },
        _buildTableHead:function(){
            var op=this.options,
                el=this.element,
                grid = el.closest('.om-grid'),
                cms=this._getColModel(),
                allColsWidth = 0, //colModel的宽度
                indexWidth = 0, //colModel的宽度
                checkboxWidth = 0, //colModel的宽度
                autoExpandColIndex = -1;
                thead=$('<thead></thead>');
                tr=$('<tr></tr>').appendTo(thead);
            //渲染序号列
            if(op.showIndex){
                var cell=$('<th></th>').attr({axis:'indexCol',align:'center'}).addClass('indexCol').append($('<div class="indexheader" style="text-align:center;width:25px;"></div>'));
                tr.append(cell);
                indexWidth=25;
            }
            //渲染checkbox列
            if(!op.singleSelect){
                var cell=$('<th></th>').attr({axis:'checkboxCol',align:'center'}).addClass('checkboxCol').append($('<div class="checkboxheader" style="text-align:center;width:17px;"><span class="checkbox"/></div>'));
                tr.append(cell);
                checkboxWidth=17;
            }
            //渲染colModel各列
            for (var i=0,len=cms.length;i<len;i++) {
                var cm=cms[i],cmWidth = cm.width || 60,cmAlign=cm.align || 'center';
                if(cmWidth == 'autoExpand'){
                    cmWidth = 0;
                    autoExpandColIndex = i;
                }
                var thCell=$('<div></div>').html(cm.header).css({'text-align':cmAlign,width:cmWidth});
                cm.wrap && thCell.addClass('wrap');
                var th=$('<th></th>').attr('axis', 'col' + i).addClass('col' + i).append(thCell);
                if(cm.name) {
                    th.attr('abbr', cm.name);
                }
                if(cm.align) {
                    th.attr('align',cm.align);
                }
                //var _div=$('<div></div>').html(cm.header).attr('width', cmWidth);
                allColsWidth += cmWidth;
                tr.append(th);
            }
            //tr.append($('<th></th'));
            el.prepend(thead);
            
            
            $('table',this.hDiv).append(thead);
            this._fixHeaderWidth(autoExpandColIndex , allColsWidth);
            this.thead=thead;
            thead = null;
        },
        _buildPagingToolBar:function(){
            var op=this.options;
            if(op.limit<=0){
            	this.pDiv.css("border-width" , 0).hide();
            	this.pager = this.pDiv;//即使不出现分页条，这里仍然指定其引用，这样有些地方可以不用每次都要判断是否要分页
                return;
            }
            var self=this,
                el=this.element,
                pDiv= this.pDiv;
           
            pDiv.show().html('<div class="pDiv2">'+
                    '<div class="pGroup">'+
                    '<div class="pFirst pButton om-icon"><span class="om-icon-seek-start"></span></div>'+
                    '<div class="pPrev pButton om-icon"><span class="om-icon-seek-prev"></span></div>'+
                '</div>'+
                '<div class="btnseparator"></div>'+
                '<div class="pGroup"><span class="pControl"></span></div>'+
                '<div class="btnseparator"></div>'+
                '<div class="pGroup">'+
                    '<div class="pNext pButton om-icon"><span class="om-icon-seek-next"></span></div>'+
                    '<div class="pLast pButton om-icon"><span class="om-icon-seek-end"></span></div>'+
                '</div>'+
                '<div class="btnseparator"></div>'+
                '<div class="pGroup">'+
                    '<div class="pReload pButton om-icon"><span class="om-icon-refresh"></span></div>'+
                '</div>'+
                '<div class="btnseparator"></div>'+
                '<div class="pGroup"><span class="pPageStat"></span></div>'+
            	'</div>');
            var pageText = $.om.lang._get(op,"omGrid","pageText").replace(/{totalPage}/, '<span>1</span>').replace(/{index}/, '<input type="text" size="4" value="1" />');
            $('.pControl',pDiv).html(pageText);
            el.parent().after(pDiv);
            $('.pReload', pDiv).click(function() {
                self._populate();
            });
            $('.pFirst', pDiv).click(function() {
                self._changePage('first');
            });
            $('.pPrev', pDiv).click(function() {
                self._changePage('prev');
            });
            $('.pNext', pDiv).click(function() {
                self._changePage('next');
            });
            $('.pLast', pDiv).click(function() {
                self._changePage('last');
            });
            $('.pControl input', pDiv).keydown(function(e) {
                if (e.keyCode == $.om.keyCode.ENTER) {
					self._changePage('input');
				}
            });
            $('.pButton', pDiv).hover(function() {
                $(this).addClass('om-state-hover');
            }, function() {
                $(this).removeClass('om-state-hover');
            });
            this.pager = pDiv;
        },
        _buildLoadMask:function(){
            var grid = this.element.closest('.om-grid');
            this.loadMask.css({width:"100%",height:grid.height()});
        },
        _changePage : function(ctype) { // change page
            if (this.loading) {
                return true;
            }
            var el=this.element,
                op=this.options,
                grid = el.closest('.om-grid'),
                pageData = this.pageData,
                nowPage=pageData.nowPage,
                totalPages=pageData.totalPages,
                newPage = nowPage;
            this._oldPage = nowPage;//保存好旧的页数，有些插件如sort是需要用到的
            switch (ctype) {
                case 'first':
                    newPage = 1;
                    break;
                case 'prev':
                    if (nowPage > 1) {
                        newPage = nowPage - 1;
                    }
                    break;
                case 'next':
                    if (nowPage < totalPages) {
                        newPage = nowPage + 1;
                    }
                    break;
                case 'last':
                    newPage = totalPages;
                    break;
                case 'input':
                    var nv = parseInt($('.pControl input', el.closest('.om-grid')).val());
                    if (isNaN(nv)) {
                        nv = nowPage;
                    }
                    if (nv < 1) {
                        nv = 1;
                    } else if (nv > totalPages) {
                        nv = totalPages;
                    }
                    $('.pControl input', this.pDiv).val(nv);
                    newPage = nv;
                    break;
                default:
                    if (/\d/.test(ctype)) {
                        var nv = parseInt(ctype);
                        if (isNaN(nv)) {
                            nv = 1;
                        }
                        if (nv < 1) {
                            nv = 1;
                        } else if (nv > totalPages) {
                            nv = totalPages;
                        }
                        $('.pControl input', el.closest('.om-grid')).val(nv);
                        newPage = nv;
                    }
            }
            if (newPage == nowPage) {
                return false;
            }
            //触发事件
            if(this._trigger("onPageChange",null,ctype,newPage)===false){
                return;
            }
            //修改pageData
            pageData.nowPage=newPage;
            //刷新数据
            this._populate();
        },
        //刷新数据
        _populate : function() { // get latest data
            var self=this,
                el = this.element,
                grid = el.closest('.om-grid'),
                op = this.options,
                pageStat = $('.pPageStat', grid);
            if (!op.dataSource) {
                $('.pPageStat', grid).html(op.emptygMsg);
                return false;
            }
            if (this.loading) {
                return true;
            }
            var pageData = this.pageData,
                nowPage = pageData.nowPage || 1,
                loadMask = $('.gBlock',grid);
            //具备加载数据的前提条件了，准备加载
            this.loading = true;
            pageStat.html($.om.lang._get(op,"omGrid","loadingMsg"));
            loadMask.show();
            var limit = (op.limit<=0)?100000000:op.limit;
            var param =$.extend(true,{},this._extraData,op.extraData,{
                start : limit * (nowPage - 1),
                limit : limit,
                _time_stamp_ : new Date().getTime()
            });
            $.ajax({
                type : op.method,
                url : op.dataSource,
                data : param,
                dataType : 'json',
                success : function(data,textStatus,request) {
                    var onSuccess = op.onSuccess;
                    if (typeof(onSuccess) == 'function') {
                        self._trigger("onSuccess",null,data,textStatus,request);
                    }
                    self._addData(data);
                    for(var i=0 , len=op._onRefreshCallbacks.length; i<len; i++){
                    	op._onRefreshCallbacks[i].call(self);
                    }
                    self._trigger("onRefresh",null,nowPage,data.rows);
                    loadMask.hide();
                    self.loading = false;
                },
                error : function(XMLHttpRequest, textStatus, errorThrown) {
                    pageStat.html($.om.lang._get(op,"omGrid","errorMsg")).css('color','red');
                    try {
                        var onError = op.onError;
                        if (typeof(onError) == 'function') {
                            onError(XMLHttpRequest, textStatus, errorThrown);
                        }
                    } catch (e) {
                        // do nothing 
                    } finally {
                        loadMask.hide();
                        self.loading = false;
                        self.pageData.data={rows:[],total:0};//出错时重新设置，不然self.pageData.data可能为undefined，其它地方就要做多余空处理
                        return false;
                    }
                    
                }
            });
        },
        _addData:function(data){
            var op = this.options,
                el = this.element,
                grid = el.closest('.om-grid'),
                pageStat = $('.pPageStat', grid),
                preProcess = op.preProcess,
                pageData=this.pageData;
            //预处理
            preProcess && (data=preProcess(data));
            pageData.data=data;
            pageData.totalPages = Math.ceil(data.total/op.limit);
            //刷新分页条
            this._buildPager();
            this._renderDatas();
        },
        _buildPager:function(){
            var op=this.options;
            if(op.limit<=0){
                return;
            }
            var el=this.element,
                pager=this.pager,
                pControl=$('.pControl',pager),
                pageData = this.pageData,
                nowPage=pageData.nowPage,
                totalPages=pageData.totalPages,
                data=pageData.data,
                from=op.limit* (nowPage-1)+1,
                to=from-1+data.rows.length,
                pageStat='';
            if(data.total===0){
                pageStat=$.om.lang._get(op,"omGrid","emptyMsg");
            }else{
                pageStat = $.om.lang._get(op,"omGrid","pageStat").replace(/{from}/, from).replace(/{to}/, to).replace(/{total}/, data.total);
            }
            $('input',pControl).val(nowPage);
            $('span',pControl).html(totalPages);
            $('.pPageStat', pager).html(pageStat);
        },
        _renderDatas:function(){
            var self=this,
                el=this.element,
                op=this.options,
                grid=el.closest('.om-grid'),
                gridHeaderCols = this._getHeaderCols(),
                rows=this.pageData.data.rows || [],
                colModel=this._getColModel(),
                rowClasses=op.rowClasses,
                tbody=$('tbody',el).empty(),
                isRowClassesFn= (typeof rowClasses === 'function'),
                pageData = this.pageData,start=(pageData.nowPage-1)*op.limit,
                tdTmp = "<td align='$' abbr='$' class='$'><div align='$' class='innerCol $' style='width:$px'>$</div></td>",//td模板
                headerWidth = [],
                bodyContent = [],
                cols,
                j;
            
            if(!this.pageData.data.rows){
            	this.pageData.data.rows = [];//修复
            }
            self.hDiv.scrollLeft(0);
            
            $(gridHeaderCols).each(function(index){
            	headerWidth[index] = $('div',$(this)).width();
            });
    		
            $.each(rows,function(i, rowData) {
                var rowCls = isRowClassesFn? rowClasses(i,rowData):rowClasses[i % rowClasses.length];
                var rowValues=self._buildRowCellValues(colModel,rowData,i);
                bodyContent.push("<tr _grid_row_id="+(self._guid++)+" class='om-grid-row " + rowCls + "'>");
               
               	$(gridHeaderCols).each(function(index){
                    var axis = $(this).attr('axis'),wrap=false,html;
                    if(axis == 'indexCol'){
                        html=i+start+1;
                    }else if(axis == 'checkboxCol'){
                        html = '<span class="checkbox"/>';
                    }else if(axis.substring(0,3)=='col'){
                        var colIndex=axis.substring(3);
                        html=rowValues[colIndex];
                        if(colModel[colIndex].wrap){
							wrap=true;
						} 
                    }else{
                        html='';
                    }
                    cols = [this.align , this.abbr , axis , this.align , wrap?'wrap':'', headerWidth[index] , html];
                    j=0;
                    bodyContent.push(tdTmp.replace(/\$/g , function(){
                    	return cols[j++];
                    }));
                });
                bodyContent.push("</tr>");
            });
           	tbody.html(bodyContent.join(" ")); 
        },
        _getHeaderCols:function(){
        	return this.hDiv.find("th[axis]");
        },
        _buildRowCellValues:function(colModel,rowData,rowIndex){
            var len=colModel.length,values=[];
            for(var i=0;i<len;i++){
                var c=colModel[i],
                	v,
                	r=c.renderer;
                if(c.name.indexOf(".") > 0){
                	var properties = c.name.split("."),
                		j = 1,
                		length = properties.length,
                		v = rowData[properties[0]];
                	while(j<length && v && (v=v[properties[j++]]) != undefined){}
                }
                if(v == undefined){
                	v = rowData[c.name] == undefined? "" : rowData[c.name];
                }
                if(typeof r === 'function'){
                    v=r(v,rowData,rowIndex);
                }
                values[i]=v;
                v = null;
            }
            return values;
        },
        //滚动水平滚动条时让表头和表体一起滚动（如果没有这个方法则只有表体滚动，表头不会动，表头和表体就对不齐了）
        _bindScrollEnvent:function(){
            var self = this;
            this.tbody.closest('.bDiv').scroll(function(){
                self.hDiv.scrollLeft($(this).scrollLeft());
            });
        },
        //绑定行选择/行反选/行单击/行双击等事件监听
        _bindSelectAndClickEnvent:function(){
            var self=this;
            this.tbody.unbind();
            //如果有checkbox列则绑定事件
            if(!this.options.singleSelect){ //可以多选
                // 全选/反选,不需要刷新headerChekcbox的选择状态
                $('th.checkboxCol span.checkbox',this.thead).click(function(){
                    var thCheckbox=$(this),trSize=self._getTrs().size();
                    if(thCheckbox.hasClass('selected')){ //说明是要全部取消选择
                        thCheckbox.removeClass('selected');
                        for(var i=0;i<trSize;i++){
                            self._rowDeSelect(i);
                        }
                    }else{ //说明是要全选
                        thCheckbox.addClass('selected');
                        for(var i=0;i<trSize;i++){
                            self._rowSelect(i);
                        }
                    }
                });
                //行单击,需要刷新headerChekcbox的选择状态
                this.tbody.delegate('tr.om-grid-row','click',function(event){
                    var row=$(this),index=self._getRowIndex(row);
                    if(row.hasClass('om-state-highlight')){ //已选择
                        self._rowDeSelect(index);
                    }else{
                        self._rowSelect(index);
                    }
                    self._refreshHeaderCheckBox();
                    self._trigger("onRowClick",event,index,self._getRowData(index));
                });
                //行双击
                this.tbody.delegate('tr.om-grid-row','dblclick',function(event){
                    var row=$(this),index=self._getRowIndex(row);
                    if(row.hasClass('om-state-highlight')){ //已选择
                        //do nothing
                    }else{
                        self._rowSelect(index);
                        self._refreshHeaderCheckBox();
                    }
                    self._trigger("onRowDblClick",event,index,self._getRowData(index));
                });
            }else{ //不可多选
                //行单击
                this.tbody.delegate('tr.om-grid-row','click',function(event){
                    var row=$(this),index=self._getRowIndex(row);
                    if(row.hasClass('om-state-highlight')){ //已选择
                        // no need to deselect another row and select this row
                    }else{
                        var lastSelectedIndex = self._getRowIndex(self.tbody.find('tr.om-state-highlight:not(:hidden)'));
                        (lastSelectedIndex != -1) && self._rowDeSelect(lastSelectedIndex);
                        self._rowSelect(index);
                    }
                    self._trigger("onRowClick",event,index,self._getRowData(index));
                });
                
                //行双击,因为双击一定会先触发单击，所以对于单选表格双击时这一行一定是选中的，所以不需要强制双击前选中
                this.tbody.delegate('tr.om-grid-row','dblclick',function(event){
                    var index = self._getRowIndex(this);
                    self._trigger("onRowDblClick",event,index,self._getRowData(index));
                });
            }
        },
        _getRowData:function(index){
            return this.pageData.data.rows[index];
        },
        _rowSelect:function(index){
             var el=this.element,
                op=this.options,
                tbody=$('tbody',el),
                tr=this._getTrs().eq(index),
                chk=$('td.checkboxCol span.checkbox',tr);
            tr.addClass('om-state-highlight');
            chk.addClass('selected');
            this._trigger("onRowSelect",null,index,this._getRowData(index));
        },
        _rowDeSelect:function(index){
            var el=this.element,
                op=this.options,
                tbody=$('tbody',el),
                tr=this._getTrs().eq(index),
                chk=$('td.checkboxCol span.checkbox',tr);
            tr.removeClass('om-state-highlight');
            chk.removeClass('selected');
            this._trigger("onRowDeselect",null,index,this._getRowData(index));
        },
        _refreshHeaderCheckBox:function(){
        	var selects = this.getSelections(),
        		$trs = this._getTrs(),
        		headerCheckbox = $('th.checkboxCol span.checkbox' , this.thead),
        		len = $trs.length;
        	//如果当前页一条数据都没有，那么应该不选中比较合理
        	headerCheckbox.toggleClass('selected' ,len>0 && len==selects.length );
        },
        //让列可以改变宽度（index列和checkbox列不可以改变宽度）
        _makeColsResizable:function(){
            var self=this,
                bDiv=self.tbody.closest('.bDiv'),
                grid=self.element.closest('.om-grid'),
                $titleDiv = this.titleDiv,
                differWidth;
                
            $('th[axis^="col"] div',self.thead).omResizable({
                handles: 'e',//只可水平改变大小
                containment: 'document',
                minWidth: 60,
                resize: function(ui , event) {
                	var callbacks = self.options._onResizableCallbacks;
                	for(var i=0,len=callbacks.length; i<len; i++){
                		callbacks[i].call(self);
                	}
                	
                    var _this=$(this),abbr=_this.parent().attr('abbr'),dataCells=$('td[abbr="'+abbr+'"] > div',self.tbody),hDiv=self.thead.closest('.hDiv');
                    _this.width(ui.size.width).height('');
                    dataCells.width(ui.size.width).height('');
                    bDiv.height(grid.height()-($titleDiv.is(":hidden")?0:$titleDiv.outerHeight(true))-hDiv.outerHeight(true)-(self.pDiv.is(":hidden")?0:self.pDiv.outerHeight(true)));
                    hDiv.scrollLeft(bDiv.scrollLeft());
                },
                start: function(ui , event) {
                	differWidth = $(this).parent().width();
                },
                stop: function(ui , event) {
                	var callbacks = self.options._onResizableStopCallbacks,
                		$th = $(this).parent(),
                		hDiv=self.thead.closest('.hDiv');
                	differWidth = $th.width() - differWidth;
                	for(var i=0,len=callbacks.length; i<len; i++){
                		callbacks[i].call(self , $th , differWidth );
                	}
                	hDiv.scrollLeft(bDiv.scrollLeft());
                }
            });
        },
        //单独抽出这个方法是为了更好整合其它grid插件，因为很多插件会对tr进行操作，比如行编辑插件会对tr进行隐藏，所以这里获取行索引要注意不与插件冲突。
		_getRowIndex:function(tr){
			return this._getTrs().index(tr);
		},
		//获取所有真正的行，此方法一样可以兼容其它插件。
		_getTrs:function(){
			return this.tbody.find("tr.om-grid-row:not([_delete]=true)");		
		},
        //public methods
        /**
         * 修改取数url并立即刷新数据。一般用于查询操作。比如开始时取数url是data.json则后台实际收到data.json?start=0&limit=15这样的请求。查询时使用setData方法将取数url改成data.json?queryString=admin，后台实际收到data.json?queryString=admin&start=0&limit=15这样的请求，后台根据参数queryString来做查询即可。
         * @name omGrid#setData
         * @function
         * @param url 新的数据源url
         * @returns jQuery对象
         * @example
         *  //使用新的url来取数，设置完后会立即开始刷新表格数据。
         *  $('.selector').omGrid('setData', 'newgriddata.json');
         */
        setData:function(url){
            this.options.dataSource=url;
            this.pageData={nowPage:1,totalPages:1};
            this._populate();
        },
        /**
         * 获取表格JSON数据。<br/>
         *     
         * @name omGrid#getData
         * @function
         * @returns 如果没有设置preProcess则返回由后台返回来的对象。如果有preProcess则返回处理后的对象
         * @example
         * //获取grid的当前页数据
         * var store = $('.selector').omGrid('getData');
         * 
         * 
         */
        getData:function(){
            return this.pageData.data;
        },
        /**
         * 使用getData方法的结果重新渲染数据。<b>注意：该方法并不会发送Ajax请求，而且如果表格当前正在加载数据（loadmask还未消失）的话则什么也不做直接返回</b>。
         * @name omGrid#refresh
         * @function
         * @returns jQuery对象
         * @example
         * //根据当前grid数据模型中的数据，重新刷新grid
         * $('.selector').omGrid('refresh');//注意refresh没有传入参数
         * 
         */
        refresh:function(){
            // 修改数据模型后可以用此方法来强制刷新（仅客户端行为,不向后台发送请求）
            if (this.loading) {
                return true;
            }
            this.loading = true;
            var op=this.options;
			$('.pPageStat', this.pager).html($.om.lang._get(op,"omGrid","loadingMsg"));
            this.loadMask.show();
            this._buildPager();
            this._renderDatas();
            this._trigger("onRefresh",null,this.pageData.nowPage || 1,this.pageData.data.rows);
            //用于行编辑插件
            for(var i=0 , len=op._onRefreshCallbacks.length; i<len; i++){
            	op._onRefreshCallbacks[i].call(this);
            }
            this.loadMask.hide();
            this.loading = false;
        },
        /**
         * 刷新表格。如果没有参数则刷新当前页，如果有参数则转到参数所表示的页（如果参数不合法会自动进行修正）。<b>注意：该方法会发送Ajax请求，而且如果表格当前正在加载数据（loadmask还未消失）的话则什么也不做直接返回</b>。
         * @name omGrid#reload
         * @function
         * @param page 要转到的页，参数为空表示刷新当前页。如果参数不是数字或者小于1则自动修正为1，如果参数大于总页数则自动修正为总页数。
         * @returns jQuery对象
         * @example
         * $('.selector').omGrid('reload');//刷新当前页
         * $('.selector').omGrid('reload',3);//转到第3页
         * 
         */
        reload:function(page){
            if (this.loading) {
                return true;
            }
            if(typeof page !=='undefined'){
                page=parseInt(page) || 1;
                if(page<0){
                    page = 1;
                }
                if(page>this.pageData.totalPages){
                    page=this.pageData.totalPages;
                }
                this.pageData.nowPage = page;
            }
            //相当于goto(page) and reload()，会转到那一页并重新刷新数据（向后台发送请求）
            //没有参数时刷新当前页
            this._populate();
        },
        /**
         * 选择行。<b>注意：传入的参数是序号（第一行是0第二行是1）数组（比如[0,1]表示选择第一行和第二行）；要想清除所有选择，请使用空数组[]作为参数；只能传入序号数组，如果要做复杂的选择算法，请先在其它地方算好序号数组后后调用此方法；此方法会清除其它选择状态，如选择第1,2行然后setSelections([3])最后结果中只有第3行，如setSelections([3,4]);setSelections([5,6])后只会选择5,6两行</b>。
         * @name omGrid#setSelections
         * @function
         * @param indexes 序号（第一行是0第二行是1）数组。
         * @returns jQuery对象
         * @example
         * //选择表格中第二行、第三行、第五行
         * $('.selector').omGrid('setSelections',[1,2,4]);
         * 
         */
        setSelections:function(indexes){
            var self=this;
            if(!$.isArray(indexes)){
                indexes=[indexes];
            }
            var selected=this.getSelections();
            $(selected).each(function(){
                self._rowDeSelect(this);
            });
            $(indexes).each(function(){
                self._rowSelect(this);
            });
            self._refreshHeaderCheckBox();
        },
        /**
         * 获取选择的行的行号或行记录。<b>注意：默认返回的是行序号组成的数组（如选择了第2行和第5行则返回[1,4]），如果要返回行记录JSON组成的数组需要传入一个true作为参数</b>。
         * @name omGrid#getSelections
         * @function
         * @param needRecords 参数为true时返回的不是行序号数组而是行记录数组。参数为空或不是true时返回行序号数组。
         * @returns jQuery对象
         * @example
         * var selectedIndexed = $('.selector').omGrid('getSelections');
         * var selectedRecords = $('.selector').omGrid('getSelections',true);
         * 
         */
        getSelections:function(needRecords){
            //needRecords=true时返回Record[],不设或为false时返回index[]
            var self=this,
            	$trs = self._getTrs(),
            	selectedTrs = $trs.filter('.om-state-highlight'),
            	result=[];
            if(needRecords){
            	var rowsData = self.getData().rows;
            	selectedTrs.each(function(index , tr){
            		result[result.length] = rowsData[$trs.index(tr)];
            	});
            }else{
            	selectedTrs.each(function(index , tr){
            		result[result.length] = $trs.index(tr);
            	});
            }
            return result;
        },
        destroy:function(){
        	var $el = this.element;
        	$el.closest('.om-grid').after($el).remove();
        }
    });
    
    $.om.lang.omGrid = {
        loadingMsg:'正在加载数据，请稍候...',
        emptyMsg:'没有数据',
        errorMsg:'取数出错',
        pageText:'第{index}页，共{totalPage}页',
        pageStat:'共{total}条数据，显示{from}-{to}条'
    };
})(jQuery);/*
 * $Id: om-messagebox.js,v 1.25 2012/06/28 02:13:57 licongping Exp $
 * operamasks-ui omMessageBox @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 *
 * Depends:
 *  om-core.js
 *  om-mouse.js
 *  om-draggable.js
 *  om-position.js
 */
 
(function( $, undefined ) {
	 var tmpl = '<div class="om-messageBox om-widget om-widget-content om-corner-all" tabindex="-1">'+
	                '<div class="om-messageBox-titlebar om-widget-header om-corner-top om-helper-clearfix">'+
	                    '<span class="om-messageBox-title"></span>'+
	                    '<a href="#" class="om-messageBox-titlebar-close om-corner-tr"><span class="om-icon om-icon-closethick"></span></a>' +
	                '</div>'+
	                '<div class="om-messageBox-content om-widget-content">'+
	                    '<table><tr vailgn="top">' +
	                        '<td class="om-messageBox-imageTd"><div class="om-messageBox-image"/>&nbsp;</td>' +
	                        '<td class="om-message-content-html"></td>' +
	                    '</tr></table>'+
	                '</div>'+
	                '<div class="om-messageBox-buttonpane om-widget-content om-corner-bottom om-helper-clearfix">'+
	                    '<div class="om-messageBox-buttonset"></div>'+
	                '</div>'+
	            '</div>';
	var _height = function(){
        // handle IE 6
        if ($.browser.msie && $.browser.version < 7) {
            var scrollHeight = Math.max(document.documentElement.scrollHeight, document.body.scrollHeight),
                offsetHeight = Math.max(document.documentElement.offsetHeight, document.body.offsetHeight);
            return (scrollHeight < offsetHeight) ?  $(window).height() : scrollHeight;
        // handle "good" browsers
        } else {
            return $(document).height();
        }
	};
	var _width = function() {
        // handle IE
        if ( $.browser.msie ) {
            var scrollWidth = Math.max(document.documentElement.scrollWidth, document.body.scrollWidth),
                offsetWidth = Math.max(document.documentElement.offsetWidth, document.body.offsetWidth);
            return (scrollWidth < offsetWidth) ? $(window).width() : scrollWidth;
        // handle "good" browsers
        } else {
            return $(document).width();
        }
    };
	var close = function(messageBox, mask, handler, value){
	    if (messageBox.hasClass('om-messageBox-waiting')) {
	        return;
	    }
	    handler ? handler(value) : jQuery.noop();
	    messageBox.remove();
	    mask.remove();
	};
    var _show = function(config){
        var onClose = config.onClose;
        var messageBox = $(tmpl).appendTo(document.body).css('z-index', 1500).position({
            of:window,
            collision: 'fit'
        }).omDraggable({
            containment: 'document',
            cursor:'move',
            handle: '.om-messageBox-titlebar'
        }).hide().keydown(function(event){
            if (event.keyCode && event.keyCode === $.om.keyCode.ESCAPE) {
                close(messageBox, mask, null, false);
                event.preventDefault();
            }
        });
        var mask = $('<div class="om-widget-overlay"/>').appendTo(document.body).show().css({height:_height(),width:_width()});
        var closeBut = messageBox.find('span.om-messageBox-title').html(config.title).next().hover(function(){
            $(this).addClass('om-state-hover');
        }, function(){
            $(this).removeClass('om-state-hover');
        }).focus(function(){
            $(this).addClass('om-state-focus');
        }).blur(function(){
            $(this).removeClass('om-state-focus');
        }).click(function(event){
            close(messageBox, mask, null, false);
            return false;
        }).bind('mousedown mouseup', function(){
            $(this).toggleClass('om-state-mousedown');
        });
        messageBox.find('div.om-messageBox-image').addClass('om-messageBox-image-' + config.type);
        var content = config.content;
        if (config.type == 'prompt') {
            content = content || '';
            content += '<br/><input id="om-messageBox-prompt-input" type="text"/>';
        }
        messageBox.find('td.om-message-content-html').html(content);
        var buttonSet = messageBox.find('div.om-messageBox-buttonset');
        switch (config.type) {
            case 'confirm':
                buttonSet.html('<button id="confirm">确定</button><button id="cancel">取消</button>');
                if($.fn.omButton){
	                buttonSet.find("button#confirm").omButton({
	                	width:60,
	                	onClick:function(event){
	                		close(messageBox, mask, onClose, true);
	                	}
	                });
	                buttonSet.find("button#cancel").omButton({
	                	width:60,
	                	onClick:function(event){
	                		close(messageBox, mask, onClose, false);
	                	}
	                });
                }
                break;
            case 'prompt':
                buttonSet.html('<button id="confirm">确定</button><button id="cancel">取消</button>');
                if($.fn.omButton){
	                buttonSet.find("button#confirm").omButton({
	                	width:60,
	                	onClick:function(event){
	                        var returnValue = onClose ? onClose($('#om-messageBox-prompt-input').val()) : jQuery.noop();
	                        if (returnValue !== false) {
	                            messageBox.remove();
	                            mask.remove();
	                        }
	                	}
	                });
	                buttonSet.find("button#cancel").omButton({
	                	width:60,
	                	onClick:function(event){
	                		close(messageBox, mask, onClose, false);
	                	}
	                });
                }
                break;
            case 'waiting':
                messageBox.addClass('om-messageBox-waiting');
                mask.addClass('om-messageBox-waiting');
                closeBut.hide(); //不显示关闭按钮
                buttonSet.parent().hide(); //不显示下面的按钮面板
                messageBox.find(">.om-messageBox-content").addClass("no-button om-corner-bottom");
                break;
            default:
                buttonSet.html('<button id="confirm">确定</button>');
	            if($.fn.omButton){
		            buttonSet.find("button#confirm").omButton({
		            	width:60,
		            	onClick:function(event){
		            		close(messageBox, mask, onClose, true);
		            	}
		            });
	            }
        }
        var buts = $('button',buttonSet);
        buts.width("100%");
        messageBox.show();
        var okBut = buts.first()[0];
        okBut ? okBut.focus() : messageBox.focus();
    };
     /**
      * @name omMessageBox
      * @class
      * omMessageBox用于提供提示信息的弹出窗口，类似于JavaScript中使用alert()、confirm()、prompt()函数时出现的那种提示信息的弹出窗口。<br/><br/>
      * <br/>
      * <h2>有以下特点：</h2><br/>
      * <ul>
      *     <li>有较好的浏览器兼容性</li>
      *     <li>可以定义标题、内容，并且标题和内容可以使用html代码</li>
      *     <li>标题栏有关闭按钮，也可以按Esc键关闭</li>
      *     <li>支持丰富的提示（图标不同）</li>
      *     <li>可以监听关闭事件</li>
      * </ul>
      * <br/>
      * <h2>提供了以下工具方法：</h2><br/>
      * <ul>
      *     <li>
      *         <b>$.omMessageBox.alert(config)</b><br/>
      *         弹出一个Alert提示，仅有一个“确定”按钮。其中config有以下配置项：<br/>
      *         <ul style="margin-left:40px">
      *             <li>type：alert提示的类型，类型不同时弹出窗口左边的图标会不同。String类型，可选的值有'alert'、'success'、'error'、'question'、'warning'。默认值为'alert'。</li>
      *             <li>title：弹出窗口的标题文字，String类型，可以使用普通字符串，也可以使用html代码。默认值为'提示'。</li>
      *             <li>content：弹出窗口的提示内容，String类型，可以使用普通字符串，也可以使用html代码。无默认值。</li>
      *             <li>onClose：弹出窗口关闭时的回调函数，Function类型，点击"确定"按钮来关闭弹出窗口时，Function的参数value值为true，按ESC键关闭弹出窗口时，Function的参数value值为false。无默认值。</li>
      *         </ul>
      *         <br/>使用方式如下：<br/>
      *         <pre>
      *             $.omMessageBox.alert({
      *                 type:'error',
      *                 title:'失败',
      *                 content:'不能删除&lt;font color="red">admin&lt;/font>用户',
      *                 onClose:function(value){
      *                     alert('do something');
      *                 }
      *             });
      *         </pre>
      *     </li>
      *     <li>
      *         <b>$.omMessageBox.confirm(config)</b><br/>
      *         弹出一个Confirm提示，有“确定”和“取消”按钮。其中config有以下配置项：<br/>
      *         <ul style="margin-left:40px">
      *             <li>title：弹出窗口的标题文字，String类型，可以使用普通字符串，也可以使用html代码。默认值为'确认'。</li>
      *             <li>content：弹出窗口的提示内容，String类型，可以使用普通字符串，也可以使用html代码。无默认值。</li>
      *             <li>onClose：弹出窗口关闭时的回调函数，Function类型，点击"确定"按钮来关闭弹出窗口时，Function的参数value值为true，点击“取消”按钮或按ESC键关闭弹出窗口时，Function的参数value值为false。无默认值。</li>
      *         </ul>
      *         <br/>使用方式如下：<br/>
      *         <pre>
      *             $.omMessageBox.confirm({
      *                 title:'确认删除',
      *                 content:'删除用户后，它所有的发帖和回帖将同时删除（不可恢复），你确定要这样做吗？',
      *                 onClose:function(value){
      *                     alert(value?'开始删除操作':'不删除了');
      *                 }
      *             });
      *         </pre>
      *     </li>
      *     <li>
      *         <b>$.omMessageBox.prompt(config)</b><br/>
      *         弹出一个Prompt提示，有一个输入框和“确定”和“取消”按钮。其中config有以下配置项：
      *         <ul style="margin-left:40px">
      *             <li>title：弹出窗口的标题文字，String类型，可以使用普通字符串，也可以使用html代码。默认值为'请输入'。</li>
      *             <li>content：弹出窗口的提示内容，String类型，可以使用普通字符串，也可以使用html代码。无默认值。</li>
      *             <li>onClose：弹出窗口关闭时的回调函数，Function类型，点击"确定"按钮来关闭弹出窗口时，Function的参数value值为用户在输入框里输入的字符串（一定是字符串），点击“取消”按钮或按ESC键关闭弹出窗口时，Function的参数value值为false。无默认值。<b>注意：在此方法中返回false将会阻止弹出窗口关闭。</b></li>
      *         </ul>
      *         <br/>使用方式如下：<br/>
      *         <pre>
      *             $.omMessageBox.prompt({
      *                 title:'商品数量',
      *                 content:'请输入你要购买的商品的数量（你的余额最多只能购买12千克）：',
      *                 onClose:function(value){
      *                     if(value===false){ //按了取消或ESC
      *                         alert('取消购买');
      *                         return;
      *                     }
      *                     if(value==''){
      *                         alert('数量不能为空');
      *                         return false; //不关闭弹出窗口
      *                     }
      *                     if(value-0+'' !== value){
      *                         alert('只能输入数字');
      *                         return false;  //不关闭弹出窗口
      *     `               }
      *                     if(value&lt;0 || value&gt;12){
      *                         alert('请输入0-12之间的数字（可带小数）');
      *                         return false; //不关闭弹出窗口
      *                     }else{
      *                         alert('开始购买'+value+'千克商品');
      *                     }
      *                 }
      *             });
      *         </pre>
      *     </li>
      *     <li>
      *         <b>$.omMessageBox.waiting(config | 'close')</b><br/>
      *         弹出一个Prompt提示，有一个输入框和“确定”和“取消”按钮。该提示窗口没有关闭按钮，也不可以按ESC关闭。如果参数是'close'时表示关闭上次弹出的Waiting提示窗口。如果是config时表示要弹出一个Waiting提示窗口，其中config有以下配置项：
      *         <ul style="margin-left:40px">
      *             <li>title：弹出窗口的标题文字，String类型，可以使用普通字符串，也可以使用html代码。默认值为'请稍候'。</li>
      *             <li>content：弹出窗口的提示内容，String类型，可以使用普通字符串，也可以使用html代码。无默认值。</li>
      *         </ul>
      *         <br/>使用方式如下：<br/>
      *         <pre>
      *             //弹出提示
      *             $.omMessageBox.waiting({
      *                 title:'请稍候',
      *                 content:'服务器正在处理您的请求，请稍候...',
      *             });
      * 
      *             //关闭提示
      *             $.omMessageBox.waiting('close');
      *         </pre>
      *     </li>
      * </ul>
      */
    $.omMessageBox = {
        alert: function(config){
            config = config || {};
            config.title = config.title || '提示';
            config.type = config.type || 'alert';
            _show(config);
        },
        confirm: function(config){
            config = config || {};
            config.title = config.title || '确认';
            config.type = 'confirm';
            _show(config);
        },
        prompt: function(config){
            config = config || {};
            config.title = config.title || '请输入';
            config.type = 'prompt';
            _show(config);
        },
        waiting: function(config){
            if (config === 'close') {
                $('.om-messageBox-waiting').remove();
                return;
            }
            config = config || {};
            config.title = config.title || '请等待';
            config.type = 'waiting';
            _show(config);
        }
    };
}(jQuery));/*
 * $Id: om-messagetip.js,v 1.11 2012/06/21 03:09:30 wangfan Exp $
 * operamasks-ui omMessageBox @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 *
 * Depends:
 *  om-core.js
 */
 
(function( $, undefined ) {
     /**
      * @name omMessageTip
      * @class
      * omMessageTip用于右下角弹出提示窗口（像QQ新闻一样）。<br/><br/>
      * <br/>
      * <h2>有以下特点：</h2><br/>
      * <ul>
      *     <li>不中断用户操作（非模态窗口提示）</li>
      *     <li>有较好的浏览器兼容性</li>
      *     <li>可以定义标题、内容，并且标题和内容可以使用html代码</li>
      *     <li>支持丰富的提示（图标不同）</li>
      *     <li>可以监听关闭事件</li>
      *     <li>弹出提示和关闭提示时有简单动画</li>
      *     <li>较轻量（仅简单的提示功能和定时消失功能，不可改变提示窗口大小，不可拖动提示窗口位置）</li>
      * </ul>
      * <br/>
      * 该组件非常轻量，功能也较少，如果需要中断用户操作，请使用omDialog或omMessageBox组件。内容区也仅能放html代码，如果有较复杂的内容请使用omDialog组件。
      * <br/>
      * <h2>提供了以下工具方法：</h2><br/>
      * <ul>
      *     <li>
      *         <b>$.omMessageTip.show(config)</b><br/>
      *         从当前页面右下角弹出一个非中断提示，弹出的提示可以关闭。其中config有以下配置项：<br/>
      *         <ul style="margin-left:40px">
      *             <li>type：提示的类型，类型不同时弹出窗口左边的图标会不同。String类型，可选的值有'alert'、'success'、'error'、'question'、'warning'、'waiting'。默认值为'alert'。</li>
      *             <li>title：弹出窗口的标题文字，String类型，可以使用普通字符串，也可以使用html代码。默认值为'提示'。</li>
      *             <li>content：弹出窗口的提示内容，String类型，可以使用普通字符串，也可以使用html代码。无默认值。</li>
      *             <li>onClose：弹出窗口关闭时的无参回调函数，Function类型。</li>
      *             <li>timeout：弹出窗口持续的时间，单位为毫秒，窗口弹出后经过这么长的时间后自动关闭（如果有onClose回调函数，会自动触发它），Int类型。默认值为无穷大（即不自动关闭）</li>
      *         </ul>
      *         <br/>使用方式如下：<br/>
      *         <pre>
      *             $.omMessageTip.show({
      *                 type:'warning',
      *                 title:'提醒',
      *                 content:'请选择你要删除的记录（可以选择一条或多条）！'
      *             });
      *             $.omMessageTip.show({
      *                 type:'error',
      *                 title:'数据非法',
      *                 content:'&lt;font color="red">123456&lt;/font>不是有效的邮箱地址！',
      *                 onClose:function(){
      *                     $('#emial').focus();
      *                 }
      *             });
      *         </pre>
      *     </li>
      * </ul>
      */
    $.omMessageTip = {
        show: function(config){
            config = $.extend({
                title : '提醒',
                content : '&#160;',
                type : 'alert'
            },config);
            var html = '<div class="om-messageTip om-widget om-corner-all" tabindex="-1">'+
                    '<div class="om-widget-header om-corner-top om-helper-clearfix">'+
                        '<span class="om-messageTip-title">'+config.title+'</span>'+
                        '<a href="#" class="om-messageTip-titlebar-close om-corner-tr"><span class="om-icon-closethick"></span></a>' +
                    '</div>'+
                    '<div class="om-messageTip-content om-widget-content om-corner-bottom">'+
                        '<div class="om-messageTip-image om-messageTip-image-'+config.type+'"></div>' +
                        '<div class="om-messageTip-content-body">'+config.content+'</div>' +
                    '</div>'+
                '</div>';
            var messageTip = $(html).appendTo(document.body).css('z-index', 3000).hide();
            var result = {d:messageTip,l:config.onClose};
            messageTip.find('a.om-messageTip-titlebar-close')
                .bind('mouseenter mouseleave',function(){
                    $(this).toggleClass('om-state-hover');
                })
                .bind('focus blur',function(){
                    $(this).toggleClass('om-state-focus');
                })
                .bind('mousedown mouseup', function(){
                    $(this).toggleClass('om-state-mousedown');
                })
                .click(function(event){
                    $.omMessageTip._close(result);
                    return false;
                });
            messageTip.slideDown('slow');
            
            var timer;
            function timeout(time){
            	timer = setTimeout(function(){
                    $.omMessageTip._close(result);
                },time);
            }
            if(config.timeout){ //定时关闭
              timeout(config.timeout);
            }
            
            messageTip.bind('mouseover', function(){
            		clearTimeout(timer);
            }).bind('mouseout', function(){
            	if(timer){
            		timeout(config.timeout);
            	}
            });
            return messageTip;
        },
        _close : function(result){
            result.d.slideUp('slow');
            if(result.l){
                result.l(); //调用onClose回调函数
            }
            setTimeout(function(){
                result.d.remove();
            },1000);
        }
    };
}(jQuery));/*
 * $Id: om-numberfield.js,v 1.70 2012/06/26 06:31:06 wangfan Exp $
 * operamasks-ui omNumberField @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 *
 * Depends:
 *  om-core.js
 */
(function($) {
    
    // 设置小数精度
    var fixPrecision = function(value, c, p) {
        var v = value.indexOf(".");       
        if (isNaN(value) && value != ".") {
            for (; isNaN(value);) {
                value = value.substring(0, value.length - 1);
            }
        }
        if(!p.allowNegative && value.indexOf("-")!= -1){
        	var array=value.split("-");
        	value=array.join("");
        }
        if(!p.allowDecimals&&v!=-1 || value.charAt(value.length-1)==='.'){
            return value.substring(0, v);
         }
        if(v!=-1){
            value=value.substring(0,v+p.decimalPrecision+1);
        }
        return value.length > 0 ? parseFloat(value) : "";
    };

    /** 
     * @name omNumberField
     * @class 数字输入框组件，只能输入数字，字符自动过滤掉。<br/>
     * @constructor
     * @description 构造函数. 
     * @param p 标准config对象：{}
     * @example
     * $('numberFielddiv').omNumberField({decimalPrecision:3});
     */
    $.omWidget("om.omNumberField", {
        options: /** @lends omNumberField.prototype */ 
        {
            /**
             * 是否允许输入小数。
             * @default true
             * @type Boolean
             * @example
             * $('#input').omNumberField({allowDecimals:true});
             */
            allowDecimals: true,  //是否允许输入小数
            /**
             * 是否允许输入负数。
             * @default true
             * @type Boolean
             * @example
             * $('#input').omNumberField({allowNegative:true});
             */
            allowNegative: true,  //是否允许输入负数
            /**
             * 精确到小数点后几位。
             * @default 2
             * @type Number
             * @example
             * $('#input').omNumberField({decimalPrecision:2});
             */
            decimalPrecision: 2, //精确到小数点后几位
            /**
             * 是否禁用组件。
             * @default false
             * @type Boolean
             * @example
             * $('#input').omNumberField({disabled:true});
             */
            disabled: false,
            /**
             * 在输入框失去焦点时触发的方法。
             * @event
             * @param value 当前输入框的值
             * @param event jQuery.Event对象
             * @default emptyFn
             * @example
             * $('#input').omNumberField({onBlur:function(value,event){alert('now the value is'+value);}});
             */
            onBlur: function(value){},
            /**
             * 是否只读。
             * @default false
             * @type Boolean
             * @example
             * $('#input').omNumberField({readOnly:true});
             */
            readOnly: false            
        },

        _create : function() {
            // 允许输入的字符
            var options = this.options,
            	self = this;
            this.element.addClass("om-numberfield om-widget om-state-default om-state-nobg")
            			.css("ime-mode" , "disabled");
			
            this.element.keypress(function(e) {
                if (e.which == null && (e.charCode != null || e.keyCode != null)) {
                    e.which = e.charCode != null ? e.charCode : e.keyCode;
                }
                var k = e.which;
                if (k === 8 || (k == 46 && e.button == -1) || k === 0) {
                    return;
                }
                var character = String.fromCharCode(k);
                $.data(this,"character",character);
                var allowed = $.data(this, "allowed");
                if (allowed.indexOf(character) === -1||($(this).val().indexOf("-") !== -1 && character == "-")
                        || ($(this).val().indexOf(".") !== -1 && character == ".")) {
                    e.preventDefault();
                }
            }).focus(function(){
            	$(this).addClass('om-state-focus');
            }).blur(function(e){
                $(this).removeClass('om-state-focus');
            	var character = $.data(this,"character");
                this.value=fixPrecision(this.value, character, options);
                self._trigger("onBlur",e,this.value);
            }).keydown(function(e){
            	self._checkLast(this);
            	
            	//Chrome并不支持css属性ime-mode,无法阻止拼音输入，但当使用输入法时，事件的e.which===229恒成立.
            	if(229 === e.which){
            		e.preventDefault();
            	}
            }).keyup(function(e){//在Chrome中文输入法下，输入  ，。等字符不会触发input框的keypress事件
            	self._checkLast(this);
            }).bind('cut paste',function(e){
            	return false;
            });
        },
		
        _init : function() {
            var $ele = this.element,
                opts = this.options;
            
            if (typeof opts.disabled !== "boolean") {
                opts.disabled = $ele.attr("disabled");
            }

            if (opts.readOnly) {
                $ele.attr("readonly","readonly");
            }

            var character = $.data($ele[0], "character");
            
            this._buildAllowChars();
            
            if (opts.disabled) {
                this.disable();
            } else {
                this.enable();
            }
        },
        
		_checkLast : function(self){
			var v = self.value,
        		len = v.length;
        	if(v && $.data(self,"allowed").indexOf(v.charAt(len-1))===-1
        		|| v.indexOf('.') != v.lastIndexOf('.')
        		|| v.indexOf('-') != v.lastIndexOf('-')){
        		self.value = v = v.substring(0 , (len--)-1);
        	}
		},
		
        _buildAllowChars : function() {
            var allowed = "0123456789";

            // 允许输入的字符
            if (this.options.allowDecimals) {
                allowed = allowed + ".";
            }
            if (this.options.allowNegative) {
                allowed = allowed + "-";
            }
            if (this.options.readOnly) {
                allowed = "";
            }
            $.data(this.element[0], "allowed", allowed);
        },
        /**
         * 禁用组件。
         * @name omNumberField#disable
         * @function
         * @example
         * $('#input').omNumberField("disable")
         */
        disable : function() {
            this.element.attr("disabled", true)
                    .addClass("om-numberfield-disabled");
        },
        /**
         * 启用组件。
         * @name omNumberField#enable
         * @function
         * @example
         * $('#input').omNumberField("enable")
         */
        enable : function() {
            this.element.attr("disabled", false)
                    .removeClass("om-numberfield-disabled");
        }
    });
})(jQuery);/*
 * $Id: om-itemselector.js,v 1.15 2012/06/18 08:43:54 wangfan Exp $
 * operamasks-ui omCombo @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 *
 * Depends:
 *  om-core.js
 *  om-mouse.js
 *  om-sortable.js
 */
;(function($) {
    /**
     * @name omItemSelector
     * @class 左移右移组件。两个多选列表框，可以从左边将一些item移到右边，也可以从右边将一些item移到左边。<br/><br/>
     * <b>特点：</b><br/>
     * <ol>
     *      <li>可以使用本地数据源，也可以使用远程数据源</li>
     *      <li>包含常用的【左移】【右移】【全部左移】【全部右移】操作</li>
     *      <li>提供丰富的事件</li>
     * </ol>
     * 
     * <b>示例：</b><br/>
     * <pre>
     * &lt;script type="text/javascript" >
     * $(document).ready(function() {
     *     $('#itemselector').omItemSelector({
     *         dataSource:[
     *                  {text:'Java',value:'1'},
     *                  {text:'JavaScript',value:'2'},
     *                  {text:'C',value:'3'},
     *                  {text:'PHP',value:'4'},
     *                  {text:'ASP',value:'5'}
     *         ],
     *         width:250,
     *         height:200
     *     });
     * });
     * &lt;/script>
     * 
     * &lt;div id="itemselector"/>
     * </pre>
     * 
     * @constructor
     * @description 构造函数. 
     * @param p 标准config对象：{}
     */
    $.omWidget('om.omItemSelector', {
        options: /** @lends omItemSelector#*/{
             /**
              * 组件宽度。
              * @type String
              * @default '250px'
              * @example
              * width : '300px'
              */
             width : '300px',
             /**
              * 组件高度。
              * @type String
              * @default '200px'
              * @example
              * height : '300px'
              */
            height : '300px',
            /**
              * 数据源属性，可以设置为“后台获取数据的URL”或者“JSON数据”
              * @type Array[JSON],URL
              * @default []
              * @example
              * dataSource : '/data/items.json' 
              * 或者
              * dataSource : [{"value":"001","text":"张三"},{"value":"002","text":"李四"}]
              * 或者下面这种(这种非标准的itemData一定要配合clientFormatter使用)
              * dataSource : [{"value":"001","name":"张三"},{"value":"002","name":"李四"}]
              */
            dataSource : [],
            /**
              * 初始值。如dataSource:[{text:'abc',value:1},{text:'def',value:2},{text:'xyz',value:3}]且value:[2]，则左边显示1、3两条，右边显示第2条。
              * @type Array[JSON]
              * @default []
              * @example
              * value : [1,3]
              */
            value : [],
            /**
              * 每个dataSource中每个item如何显示到列表中。默认仅显示item数据的text属性值。
              * @type Function
              * @default 无
              * @example
              * //对于{text:'中国',value:'zh_CN'}将显示成'中国(zh_CN)'这样
              * clientFormatter  : function(itemData,index){
              *     return itemData.text+'('+itemData.value)+')';//返回一段html代码
              * }
              * 
              * //对于{name:'张三',role:'经理',value:'PM'}将显示成红色的'张三(经理)'这样
              * //对于{name:'李四',role:'普通员工',value:'EMP'}将显示成黑色的'李四'这样
              * clientFormatter  : function(itemData,index){
              *     if(itemData.role=='经理'){
              *         return '&lt;font color="red">'+itemData.name+'('+itemData.value)+')&lt;/font>';
              *     }else{
              *         return itemData.name;
              *     }
              * }
              */
            clientFormatter : false,
            /**
              * 左边可选项的标题。
              * @name omItemSelector#availableTitle
              * @type String
              * @default '可选项'
              * @example
              * availableTitle : '可加入的用户组'
              */
            //availableTitle : $.om.lang.omItemSelector.availableTitle,
            /**
              * 右边已选项的标题。
              * @name omItemSelector#selectedTitle
              * @type String
              * @default '已选项'
              * @example
              * selectedTitle : '已加入的用户组'
              */
            //selectedTitle : $.om.lang.omItemSelector.selectedTitle,
            /**
              * 是否自动排序。设为true后每次将item左移或右移时将会对item进行排序（按照dataSource中数据源的顺序）。<b>注意：启用此功能后将无法使用拖动排序功能，也不会显示【上移】【下移】【置顶】【置底】这几个按钮。</b>
              * @type Boolean
              * @default false
              * @example
              * autoSort : true
              */
            autoSort : false,
            /**
              * 给Ajax返回的原始数据的进行预处理的函数。其中参数data是服务器返回的数据。
              * @type Function
              * @field
              * @default 无
              * @example
              * preProcess  : function(data){
              *     return data;//这里返回处理后的数据
              * }
              */
            preProcess : function(data){
                return data;
            },
            /**
             * 以Ajax方式加载数据出错时的回调函数。可以在这里进行一些处理，比如以人性化的方式提示用户。
             * @event
             * @param xmlHttpRequest XMLHttpRequest对象
             * @param textStatus  错误类型
             * @param errorThrown  捕获的异常对象
             * @param event jQuery.Event对象
             * @type Function
             * @example
             * onError:function(xmlHttpRequest, textStatus, errorThrown,event){ 
             *      alert('取数出错');
             *  } 
             */
            onError : jQuery.noop,
            /**
             * 以Ajax方式加载数据成功时的回调函数。此方法在渲染可选项item之前执行。
             * @event
             * @param data Ajax请求返回的数据
             * @param textStatus 响应的状态
             * @param event jQuery.Event对象
             * @type Function
             * @example
             * onSuccess:function(data, textStatus, event){
             *     if(data.length==0){
             *          alert('没有数据！');
             *     } 
             * }
             */
            onSuccess : jQuery.noop,
            /**
             * 从左边将item移到右边之前执行的动作。如果返回false，则item不会进行移动。用户可以在这个事件中进行一些其它有用的处理，比如监听此方法然后return selectedItems.length &lt; 3就能实现“最多只能选择3个item”的功能
             * @event
             * @param itemDatas 正在移动的item对应的数据组成的数组，比如dataSource是[{text:'A',value:0},{text:'B',value:1}]，则移动A时itemDatas是[{text:'A',value:0}]；移动B时itemDatas是[{text:'B',value:1}]，同时移动A、B时itemDatas是[{text:'A',value:0},{text:'B',value:1}]
             * @param event jQuery.Event对象
             * @type Function
             * @example
             * onBeforeItemSelect:function(itemDatas,event){
             *     alert('即将加入到:'+itemDatas.length+'个群中');
             * }
             */
            onBeforeItemSelect : jQuery.noop,
            /**
             * 从右边将item移到左边之前执行的动作。如果返回false，则item不会进行移动。用户可以在这个事件中进行一些其它有用的处理，比如能实现“员工是基本角色，不可以退出该角色”的功能
             * @event
             * @param itemDatas 正在移动的item对应的数据组成的数组，比如dataSource是[{text:'A',value:0},{text:'B',value:1}]，则移动A时itemDatas是[{text:'A',value:0}]；移动B时itemDatas是[{text:'B',value:1}]，同时移动A、B时itemDatas是[{text:'A',value:0},{text:'B',value:1}]
             * @param event jQuery.Event对象
             * @type Function
             * @example
             * onBeforeItemDeselect:function(itemDatas,event){
             *     $.each(itemDatas,function(index,data){
             *         if(data.text=='员工'){
             *             alert('员工是基本角色，不可以退出该角色！');
             *             return false;
             *         } 
             *     });
             * }
             */
            onBeforeItemDeselect : jQuery.noop,
            /**
             * 从左边将item移到右边之后执行的动作。
             * @event
             * @param itemDatas 正在移动的item对应的数据组成的数组，比如dataSource是[{text:'A',value:0},{text:'B',value:1}]，则移动A时itemDatas是[{text:'A',value:0}]；移动B时itemDatas是[{text:'B',value:1}]，同时移动A、B时itemDatas是[{text:'A',value:0},{text:'B',value:1}]
             * @param event jQuery.Event对象
             * @type Function
             * @example
             * onItemSelect:function(itemDatas,event){
             *      alert('你刚刚选择了'+itemDatas.length+'个条目');
             * }
             */
            onItemSelect : jQuery.noop,
            /**
             * 从右边将item移到左边之后执行的动作。
             * @event
             * @param itemDatas 正在移动的item对应的数据组成的数组，比如dataSource是[{text:'A',value:0},{text:'B',value:1}]，则移动A时itemDatas是[{text:'A',value:0}]；移动B时itemDatas是[{text:'B',value:1}]，同时移动A、B时itemDatas是[{text:'A',value:0},{text:'B',value:1}]
             * @param event jQuery.Event对象
             * @type Function
             * @example
             * onItemDeselect :function(itemDatas,event){
             *      alert('你刚刚去掉了'+itemDatas.length+'个条目');
             * }
             */
            onItemDeselect  : jQuery.noop
        },
        _create:function(){
            this.element.addClass('om-itemselector om-widget').html('<table style="height:100%;width:100%" cellpadding="0" cellspacing="0"><tr><td class="om-itemselector-leftpanel"></td><td class="om-itemselector-toolbar"></td><td class="om-itemselector-rightpanel"></td></tr></table>');
            var tds=$('td',this.element);
            var thead = $('<thead></thead>');
            var cell = $('<th></th>').attr({axis:'checkboxCol',align:'center'})
            .append($('<div class="header"><span class="checkbox"/><span class="om-itemselector-title"/></div>'));
            $('<tr></tr>').append(cell).appendTo(thead);
            this.leftPanel = $('<table cellspacing="0" cellpadding="0"></table>').append(thead)
            .append('<tr><td><div class="om-itemselector-up"><span class="upbtn"/></div><div class="om-itemselector-items"><dl></dl></div><div class="om-itemselector-down"><span class="downbtn"/></div></tr></td>')
            .appendTo(tds.eq(0));
            this.toolbar=$('<div></div>').appendTo(tds.eq(1));
            this.rightPanel=this.leftPanel.clone().appendTo(tds.eq(2));
        },
        _init:function(){
            var op=this.options,
                dataSource=op.dataSource;
            this.leftPanel.find(".om-itemselector-title").html($.om.lang._get(op,"omItemSelector","availableTitle"));
            this.rightPanel.find(".om-itemselector-title").html($.om.lang._get(op,"omItemSelector","selectedTitle"));
            this.element.css({width:op.width,height:op.height});
            this._buildToolbar();
            this._resizePanel();//调整左右fieldset大小
            this._bindEvents();
            if(typeof dataSource ==='string'){
                var self=this;
                $.ajax({
                    url: dataSource,
                    method: 'GET',
                    dataType: 'json',
                    success: function(data, textStatus){
                        if (self._trigger("onSuccess",null,data,textStatus) === false) {
                            return;
                        }
                        data=op.preProcess(data);
                        op.dataSource=data;
                        self._buildList();
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown){
                    	self._trigger("onError",null,XMLHttpRequest, textStatus, errorThrown);
                    }
                });
            }else{
                this._buildList();
            }
             
            this._refreshPageButton(this.leftPanel);
            this._refreshPageButton(this.rightPanel);
        },
        _buildToolbar:function(){
            var html='',
                ALL_ICONS=['add','space','remove'];
            for(var i=0,len=ALL_ICONS.length; i<len; i++){
            	var icon=ALL_ICONS[i];
                html+='<div class="om-icon om-itemselector-tbar-'+icon+'"'+' title="'+($.om.lang._get({},"omItemSelector",icon+'IconTip') || '')+'"></div>';
            }
            this.toolbar.html(html);
        },
        _resizePanel:function(){
            var self = this, lp=self.leftPanel,
                rp=self.rightPanel,
                leftItemsContainer=$('.om-itemselector-items',lp),
                rightItemsContainer=$('.om-itemselector-items',rp),
                H=lp.parent().innerHeight()-leftItemsContainer.offset().top+lp.offset().top,
                W=($('tr',self.element).innerWidth()-self.toolbar.outerWidth())/2,
                innerW=lp.outerWidth(W).innerWidth();
            leftItemsContainer.outerHeight(H).width(innerW);
            rightItemsContainer.outerHeight(H).width(innerW);
            self.element.data("dltop",$('.om-itemselector-items >dl',lp).offset().top);
        },
        _buildList:function(){
            var op=this.options;
                dataSource = op.dataSource,
                value = op.value,
                fmt=op.clientFormatter,
                leftHtml='',
                rightHtml='',
                // {text:'abc',value:2}的value是否在value:[0,2,4]这样的数组中
                inArray=function(data,valueArr){
                    for(var i=0,len=valueArr.length;i<len;i++){
                        if(data.value===valueArr[i]){
                            return true;
                        }
                    }
                    return false;
                },
                buildHtml=fmt?function(index,data){
                    return '<dt _index="'+index+'">'+'<span class="checkbox"/>'+fmt(data,index)+'</dt>';
                }:function(index,data){
                    return '<dt _index="'+index+'">'+'<span class="checkbox"/>'+data.text+'</dt>';
                };
            if($.isArray(dataSource) && jQuery.isArray(value)){
                $.each(dataSource,function(index,data){
                    if(inArray(data,value)){//在value中，要放到右边
                        rightHtml+=buildHtml(index,data);
                    }else{//不在value中,放到左边
                        leftHtml+=buildHtml(index,data);
                    }
                });
            }
            $('.om-itemselector-items>dl',this.leftPanel).html(leftHtml);
            $('.om-itemselector-items>dl',this.rightPanel).html(rightHtml);
            var items = $('.om-itemselector-items'),itemdtH =$('>dl>dt',items).outerHeight(),
            h =items.outerHeight(),offset = itemdtH - h%itemdtH;
            items.outerHeight(h+offset);           
        },
        _bindEvents:function(){
            var self=this,
                toolbar=self.toolbar;
            //单击
            self.leftPanel.delegate('.om-itemselector-items>dl>dt','click.omItemSelector',function(e){
            	$(this).toggleClass( 'om-state-highlight' );
            	self._refreshHeaderCheckbox(self.leftPanel);
            });
            self.rightPanel.delegate('.om-itemselector-items>dl>dt','click',function(e){
            	$(this).toggleClass( 'om-state-highlight' );
            	self._refreshHeaderCheckbox(self.rightPanel);
            });
            //双击
            this.leftPanel.delegate('.om-itemselector-items>dl>dt','dblclick',function(){
            	$('.om-itemselector-items>dl>dt',self.element).removeClass('om-state-highlight');
            	$(this).addClass('om-state-highlight');
            	self._moveItemsToTarget('.om-state-highlight',true);
            });
            this.rightPanel.delegate('.om-itemselector-items>dl>dt','dblclick',function(){
            	$('.om-itemselector-items>dl>dt',self.element).removeClass('om-state-highlight');
            	$(this).addClass('om-state-highlight');
                self._moveItemsToTarget('.om-state-highlight',false);
            });
            //右移
            $('.om-itemselector-tbar-add',toolbar).click(function(){
                self._moveItemsToTarget('.om-state-highlight',true);
            });
            //左移
            $('.om-itemselector-tbar-remove',toolbar).click(function(){
                self._moveItemsToTarget('.om-state-highlight',false);
            });
            //全选
            $('.header span.checkbox', self.leftPanel).click(function(){
            	var panel = self.leftPanel, $dt =$('.om-itemselector-items>dl>dt',panel);
            	$(this).toggleClass("selected");
            	if($('div.om-itemselector-up:visible', panel).length > 0){
            		$dt = $(self._getPageItems(panel, $dt));
            	}
            	if($(this).hasClass("selected")){
            		$dt.addClass("om-state-highlight");
            	}else{
            		$dt.removeClass("om-state-highlight");
            	}
            });
            $('.header span.checkbox', self.rightPanel).click(function(){
            	var panel = self.rightPanel, $dt =$('.om-itemselector-items>dl>dt',panel);
            	$(this).toggleClass("selected");
            	if($('div.om-itemselector-up:visible', panel).length > 0){
            		$dt = $(self._getPageItems(panel, $dt));
            	}
            	if($(this).hasClass("selected")){
            		$dt.addClass("om-state-highlight");
            	}else{
            		$dt.removeClass("om-state-highlight");
            	}
            });
            //翻页
            self.element.delegate('div.om-itemselector-up:not([disabled])','click', function(){
            	self._page($(this),true);
            	self._refreshHeaderCheckbox($(this).parentsUntil('table').last().parent());
            });
            self.element.delegate('div.om-itemselector-down:not([disabled])','click',function(){
            	self._page($(this), false);
            	self._refreshHeaderCheckbox($(this).parentsUntil('table').last().parent());
            });
        },
        _page: function(btn, isup){
        	var $items = isup?btn.next():btn.prev(),
        	$dl = $items.children("dl"), h=$items.outerHeight()-2,dlH = $dl.outerHeight(),
        	dltop =$dl.offset().top, nextbtn, top = this.element.data("dltop")+20;
        	if(isup){
        		$dl.offset({top: dltop+h});
        		nextbtn = $items.next();
        		if((dltop =$dl.offset().top) >0||($dl.outerHeight()- dltop-top) >h){
        			nextbtn.removeAttr("disabled").removeClass("om-itemselector-down-disabled");
        		}
        		if(dltop > 0 && dltop < h){
        			btn.attr("disabled","disabled").addClass("om-itemselector-up-disabled");
        		}
        	}else{
        		$dl.offset({top: dltop-h});
        		nextbtn = $items.prev();
            	nextbtn.removeAttr("disabled").removeClass("om-itemselector-up-disabled");
            	if((dltop =$dl.offset().top) <0 && (dlH + dltop - top)<=h){
            		btn.attr("disabled","disabled").addClass("om-itemselector-down-disabled");
            	}
        	}
        },
        _refreshHeaderCheckbox: function(panel){
        	var $dt = $('.om-itemselector-items>dl>dt', panel);
        	$dt = $(this._getPageItems(panel, $dt));
        	var selects = $dt.filter('.om-state-highlight').length;
        	$('.header span.checkbox', panel).toggleClass("selected", selects>0 && $dt.length===selects);
        },
        _refreshPageButton: function(panel){
        	var $items=$('.om-itemselector-items',panel), $dl=$('.om-itemselector-items >dl', panel),
            $up = $('.om-itemselector-up', panel), $down = $('.om-itemselector-down', panel),
            itemsH =$items.outerHeight(),dlH = $dl.outerHeight(),
            dltop = $dl.offset().top, top = this.element.data("dltop")+20;
        	if(dlH > 20 &&(top - dltop) >=dlH){
        		$dl.offset({top: dltop+itemsH});
        		dltop = $dl.offset().top;
    		}
        	if(dlH > itemsH){
        		if($up.is(":hidden")){
            		$items.outerHeight(itemsH - $up.outerHeight()*2);
            		$up.show();
            		$down.show();
        		}
        		if(dltop>0 && dltop < itemsH){
            		$up.attr("disabled","disabled").addClass("om-itemselector-up-disabled");
            	}else{
            		$up.removeAttr("disabled").removeClass("om-itemselector-up-disabled");
            	}
        		if(dltop >0 ||(dlH + dltop- top) >(itemsH)){
        			$down.removeAttr("disabled").removeClass("om-itemselector-down-disabled");
        		}else{
        			$down.attr("disabled","disabled").addClass("om-itemselector-down-disabled");
        		}
        	}else{
        		if($up.is(":visible")){
        			$items.outerHeight(itemsH + $up.outerHeight()*2);
        			$up.hide();
        			$down.hide();
        		}
        	}
        	
        },
        _getPageItems:function(panel, $dt){
        	var items = $(".om-itemselector-items", panel), itemsH = items.outerHeight(),
        	$dl = $(">dl", items), pageItems =[], hasPageButton = items.next(":visible").length>0,
        	dtH = $dt.outerHeight(), num = itemsH/dtH, dltop = $dl.offset().top,
        	top = this.element.data("dltop");
            dltop = hasPageButton ? top - dltop+20 : top-dltop;
        		pageItems = $.grep($dt, function(n, i){
        			return i < num*(dltop/itemsH + 1)-1 && i >= num*(dltop/itemsH);
        		});
        	return pageItems;
        },
        select:function(indexs){
        	if(!$.isArray(indexes)){
                indexes=[indexes];
            }
            for(var i=0, len = indexs.length; i<len ; i++){
            	$('.om-itemselector-items>dl>dt[_index="'+indexs[i]+'"]').addClass("om-state-highlight");
            }
        	
        },
        _moveItemsToTarget:function(selector,isLeftToRight){
            var self = this, fromPanel=isLeftToRight?this.leftPanel:this.rightPanel,
                selectedItems=$('.om-itemselector-items>dl>dt'+selector,fromPanel);
            if(selectedItems.size()==0)
                return;
            var toPanel=isLeftToRight?this.rightPanel:this.leftPanel,
                op=this.options,
                itemData=[];
            selectedItems.each(function(){
                itemData.push(op.dataSource[$(this).attr('_index')]);
            });
            //先触发onBeforeItemSelect或onBeforeItemDeselect事件，然后移动并触发onItemSelect或onItemDeselect事件
            if(isLeftToRight){
                if(self._trigger("onBeforeItemSelect",null,itemData)===false){
                    return;
                }
                selectedItems.appendTo($('.om-itemselector-items>dl',toPanel)).removeClass("om-state-highlight");
                
                self._trigger("onItemSelect",null,itemData);
            }else{
                if(self._trigger("onBeforeItemDeselect",null,itemData)===false){
                    return;
                }
                selectedItems.appendTo($('.om-itemselector-items>dl',toPanel)).removeClass("om-state-highlight");
                self._trigger("onItemDeselect",null,itemData);
            }
            self._refreshHeaderCheckbox(fromPanel);
            self._refreshPageButton(fromPanel);
            self._refreshPageButton(toPanel);
        },
        
        /**
         * 得到或设置组件的value值。
         * @function
         * @name omItemSelector#value
         * @param v 设置的值，不设置表示获取值
         * @returns 如果没有参数时表示getValue()返回combo的value值，比如dataSource:[{text:'abc',value:true},{text:'def',value:2},{text:'xyz',value:'x'}]选择了第2条和第三条，则getValue返回[2,'x']。如果有参数时表示setValue(newValue)返回jQuery对象。
         * 
         */
        value:function(newValue){
            if(arguments.length==0){ //getValue
                var op=this.options,
                    selectedItems=$('.om-itemselector-items>dl>dt',this.rightPanel),
                    returnValue=[];
                selectedItems.each(function(){
                    returnValue.push(op.dataSource[$(this).attr('_index')].value);
                });
                return returnValue;
            }else{ //setValue
                if($.isArray(newValue)){
                    this.options.value=newValue;
                    this._buildList();
                }
            }
        }
    });
    
    $.om.lang.omItemSelector = {
        availableTitle:'可选项',
        selectedTitle:'已选项',
        addIconTip:'右移',
        removeIconTip:'左移'       
    };
})(jQuery);/*
 * $Id: om-borderlayout.js,v 1.19 2012/06/18 08:40:56 licongping Exp $
 * operamasks-ui omBorderLayout @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 *
 * Depends:
 * om-core.js
 * om-mouse.js
 * om-resizable.js
 * om-panel.js
 */
    /** 
     * @name omBorderLayout
     * @class omBroderLayout是页面布局的基础组件.把页面拆分为north,south,west,center,east（上、下、左、中、右）5个区域，除了center是必须设置的之外其他的都是可选的。<br/>
     * <b>特点：</b><br/>
     * <ol>
     * 		<li>以omPanel作为子组件，五个区域(north,south,west,center,east)都用omPanel实现。即每个panel都支持omPanel中的属性设置。</li>
     * 		<li>在omPanel的基础上添加两个个属性：region表示所属区域，resizable表示区域是否可拖拉改变大小。</li>
     * 		<li>左边(west)和右边(east)的面板还可设置expandToTop或者expandToBottom属性分别拉伸面板至顶部或底部。</li>
     * 		<li>可设置每个区域之间的间隔大小。</li>
     * 		<li>可设置borderLayout自动适应父容器的大小。</li>
     * </ol>
     * <b>示例：</b><br/>
     * <pre>
     * &lt;script type="text/javascript" &gt;
     * $(document).ready(function() {
     *     $('#page').omBorderLayout({
     *     panels:[{ 
     *        id:"north-panel", 
     *        title:"This is north panel", 
     *        region:"north", 
     *        resizable:true, 
     *        collapsible:true 
     *    },{ 
     *        id:"center-panel", 
     *        title:"This is center panel", 
     *        region:"center" 
     *    },{ 
     *        id:"west-panel", 
     *        title:"This is west panel", 
     *        region:"west", 
     *        resizable:true, 
     *        collapsible:true, 
     *        width:200 
     *    },{ 
     *        id:"east-panel", 
     *        title:"This is east panel", 
     *        region:"east", 
     *        resizable:true, 
     *        collapsible:true, 
     *        width:100 
     *    }], 
     *    spacing:3 
     * });
     * &lt;/script&gt;
     * 
     * &lt;div id="page" style="width:800px;height:600px;"&gt;
     *	&lt;div id="north-panel" /&gt;
     *	&lt;div id="center-panel" /&gt;
     *	&lt;div id="west-panel" /&gt;
     *	&lt;div id="east-panel" /&gt;
	 * &lt;/div&gt;
	 * </pre>
     * @constructor
     * @description 构造函数. 
     * @param p 标准config对象：{}
     */
(function($) {
	$.omWidget("om.omBorderLayout", {
		options : /** @lends omBorderLayout#*/{
            /**
             * 设置borderlayout每个区域的panel。
             * @name omBorderLayout#panels
             * @default ""
             * @type Array
             * @example
		     *     $('#page').omBorderLayout({
		     *     panels:[{ 
		     *        id:"north-panel", 
		     *        title:"This is north panel", 
		     *        region:"north", 
		     *        resizable:true, 
		     *        collapsible:true 
		     *    },{ 
		     *        id:"center-panel", 
		     *        title:"This is center panel", 
		     *        region:"center" 
		     *    },{ 
		     *        id:"west-panel", 
		     *        title:"This is west panel", 
		     *        region:"west", 
		     *        resizable:true, 
		     *        collapsible:true, 
		     *        width:200 
		     *    },{ 
		     *        id:"east-panel", 
		     *        title:"This is east panel", 
		     *        region:"east", 
		     *        resizable:true, 
		     *        collapsible:true, 
		     *        width:100 
		     *    }], 
		     *    spacing:3 
		     * });
             */
			// panels:"",
            /**
             * 设置面板是否自动充满父容器。
             * @default false
             * @type Boolean
             * @example
             * $('#page').omBorderLayout({fit : true});
             */
			fit : false,
            /**
             * 设置区域的panel之间的间隔。只能设置为数字，单位是px。
             * @default 5
             * @type Number
             * @example
             * $('#page').omBorderLayout({spacing : 3});
             */
			spacing : 5,
            /**
             * 设置是否隐藏panel的header上的收缩按钮，当hideCollapsBtn设置为true且panel为collapsible时收缩/展开的按钮出现在panel之间的分隔条上。
             * @default false
             * @type Boolean
             * @example
             * $('#page').omBorderLayout({hideCollapsBtn : true});
             */			
			hideCollapsBtn : false,
            /**
             * 面板开始拖拽改变大小时触发。
             * @event
             * @type Function
             * @default emptyFn
             * @param element 被拖拽的面板的jquery对象
             * @param event jQuery.Event对象
             * @name omBorderlayout#onBeforeDrag
             * @example
             * $('#page').omBorderLayout({onBeforeDrag : function(element,event){alert("开始拖拽 "+element.attr("region")+" 区域");});
             */
			onBeforeDrag : function(element,event){},
			/**
			 * 面板拖拽改变大小结束时触发。
			 * @event
			 * @type Function
			 * @default emptyFn
			 * @param element 被拖拽的面板的jquery对象
			 * @param event jQuery.Event对象
			 * @name omBorderlayout#onAfterDrag
			 * @example
			 * $('#page').omBorderLayout({onAfterDrag : function(element,event){alert("拖拽 "+element.attr("region")+" 区域结束");});
			 */
			onAfterDrag : function(element,event){}
		},
		_create : function() {
			if(!this.options.panels) return;
			// 设置region拖拉改变宽度的最小值
			this._minWidth = 50;
			// 设置region拖拉改变高度的最小值
			this._minHeight = 28;
			this._buildRegion();
			this._resizeRegion(true);
			$(window).resize($.proxy(this, "_resizeRegion"));
		},
		// 获取区域的大小，如果区域被隐藏了则获取代理区域(regionProxy)的大小，如果代理区域也被隐藏则返回0
		_getRegionSize : function(region){
			var $region = this._getRegion(region),
				$proxy = this._getRegionProxy(region),
				size = {};
			size.width = this._regionVisible($region)?$region.outerWidth(true):
				(this._regionVisible($proxy)?$proxy.outerWidth(true):0);
			size.height = this._regionVisible($region)?$region.outerHeight(true):
				(this._regionVisible($proxy)?$proxy.outerHeight(true):0);
			return size;
		},
		_resizeRegion : function(init) {
			var $centerRegion = this._getRegion("center"),
				$northRegion = this._getRegion("north"),
				$southRegion = this._getRegion("south"),
				$westRegion = this._getRegion("west"),
				$eastRegion = this._getRegion("east"),

				$northProxy = this._getRegionProxy("north"),
				$southProxy = this._getRegionProxy("south"),
				$westProxy = this._getRegionProxy("west"),
				$eastProxy = this._getRegionProxy("east"),
				
				northHeight = this._getRegionSize("north").height;
				southHeight = this._getRegionSize("south").height;
				westWidth = this._getRegionSize("west").width;
				eastWidth = this._getRegionSize("east").width;
				centerWidth = this._getRegionSize("center").width;
				layoutWidth = this.element.width();
				layoutHeight = this.element.height();
				
				westOpt = this._getPanelOpts("west");
				eastOpt = this._getPanelOpts("east");
			
			$centerRegion.css({top:northHeight,left:westWidth});
			$centerRegion.find(">.om-panel-body").omPanel("resize",{
				height:layoutHeight - northHeight - southHeight
			});
			// 初始化后需要拖拽动态改变center panel的宽度
			if(!init){
				$centerRegion.find(">.om-panel-body").omPanel("resize",{
					width:layoutWidth - westWidth - eastWidth
				});
			}

			var centerHeight = $centerRegion.outerHeight(true);
			if($northRegion){
				// 根据左右（west、east）两边的面板是否会拉伸至顶部来计算northPanel的宽度
				var northWidth = layoutWidth - (westOpt.expandToTop?westWidth:0) - (eastOpt.expandToTop?eastWidth:0);
				$northRegion.find(">.om-panel-body").omPanel("resize",{width:northWidth});
				$northRegion.css({left:westOpt.expandToTop?westWidth:0});
				if($northProxy){
						$northProxy.outerWidth(northWidth)
								   .css({left:westOpt.expandToTop?westWidth:0});
				}
			}
			if($southRegion){
				// 根据左右（west、east）两边的面板是否会拉伸至底部来计算northPanel的宽度
				var southWidth = layoutWidth - (westOpt.expandToBottom?westWidth:0) - (eastOpt.expandToBottom?eastWidth:0);
				$southRegion.find(">.om-panel-body").omPanel("resize",{width:southWidth});
				$southRegion.css({top:layoutHeight-$southRegion.outerHeight(true),left:westOpt.expandToBottom?westWidth:0});
				if($southProxy){
					$southProxy.outerWidth(southWidth)
							   .css({left:westOpt.expandToBottom?westWidth:0});
				}
			}
			if($westRegion){
				var westTop = westOpt.expandToTop?0:northHeight;
				var westHeight = centerHeight + (westOpt.expandToBottom?southHeight:0) + (westOpt.expandToTop?northHeight:0);
				$westRegion.css({top:westTop});
				$westRegion.find(">.om-panel-body").omPanel("resize",{height:westHeight});
				if($westProxy){
					$westProxy.css({top:westTop});
					$westProxy.outerHeight(westHeight);
				}
			}
			if($eastRegion){
				var eastTop = eastOpt.expandToTop?0:northHeight;
				var eastHeight = centerHeight + (eastOpt.expandToBottom?southHeight:0) + (eastOpt.expandToTop?northHeight:0);
				$eastRegion.css({top:eastTop});
				$eastRegion.find(">.om-panel-body").omPanel("resize",{height:eastHeight});
				if($eastProxy){
					$eastProxy.css({top:eastTop});
					$eastProxy.outerHeight(eastHeight);
				}
			}
			// 初始化的时候如果center或east或west的宽度没有设置则自动调整宽度为自适应
			if(init){
				var fitEastWidth = this._getPanelOpts("east") && !this._getPanelOpts("east").width;
				var fitWestWidth = this._getPanelOpts("west") && !this._getPanelOpts("west").width;
				var fitCenterWidth = !this._getPanelOpts("center").width;
				if(fitEastWidth || fitWestWidth || fitCenterWidth){
					if(!fitCenterWidth && fitEastWidth && fitWestWidth){
						eastWidth = westWidth = (layoutWidth-centerWidth)/2;
					} else if(fitCenterWidth && !fitEastWidth && fitWestWidth){
						centerWidth = westWidth = (layoutWidth-eastWidth)/2;
					} else if(fitCenterWidth && fitEastWidth && !fitWestWidth){
						centerWidth = eastWidth = (layoutWidth-westWidth)/2;
					} else if(fitCenterWidth && fitEastWidth && fitWestWidth){
						eastWidth = westWidth = centerWidth = layoutWidth/3;
					}
				}
				if(fitCenterWidth){
					$centerRegion.find(">.om-panel-body").omPanel("resize",{width:Math.floor(layoutWidth - westWidth - eastWidth)});
				}
				if(fitEastWidth){
					$eastRegion.find(">.om-panel-body").omPanel("resize",{width:Math.ceil(layoutWidth - westWidth - centerWidth) - this.options.spacing});
				}
				if(fitWestWidth){
					$westRegion.find(">.om-panel-body").omPanel("resize",{width:Math.ceil(layoutWidth - eastWidth - centerWidth) - this.options.spacing});
					$centerRegion.css({left:$westRegion.width() + this.options.spacing});
				}
			}
			
		},
		_regionVisible : function($region){
			return $region && $region.css("display") != "none";
		},
		_createRegionProxy : function(panel,showCollapsTrigger){
			var _self = this;
			var proxyHtml = "";
			if(showCollapsTrigger){
				proxyHtml = "<div class=\"om-borderlayout-proxy om-borderlayout-trigger-proxy-"+panel.region+"\" proxy=\""+panel.region+"\">" +
							"<div class=\"om-borderlayout-expand-trigger\">"+
							"</div>"+
							"</div>";
				var $proxy = $(proxyHtml);
				if(panel.region == "west" || panel.region == "east"){
					$proxy.width(_self.options.spacing);
				} else if(panel.region == "north" || panel.region == "south"){
					$proxy.height(_self.options.spacing);
				}
				(function(panel){
					$proxy.find(".om-borderlayout-expand-trigger").click(function(){
						_self.expandRegion(panel.region);
					});
				})(panel);
			} else{
				proxyHtml = "<div class=\"om-borderlayout-proxy om-borderlayout-proxy-"+panel.region+"\" proxy=\""+panel.region+"\">" +
							"<div class=\"om-panel-title\"></div>"+
							"<div class=\"om-panel-tool\">"+
							"<div class=\"om-icon panel-tool-expand\">"+
							"</div>"+
							"</div>"+
							"</div>";
				var $proxy = $(proxyHtml);
				(function(panel){
					$proxy.find(".panel-tool-expand").hover(function(){
						$(this).toggleClass("panel-tool-expand-hover");
					}).click(function(){
						_self.expandRegion(panel.region);
					});
				})(panel);
			}
			$proxy.hover(function(){
				$(this).toggleClass("om-borderlayout-proxy-hover");
			}).appendTo(this.element);
		},
		// 构建布局框架
		_buildRegion : function() {
			var _self = this;
			var $layout = this.element;
			this.element.addClass("om-borderlayout");
			if(this.options.hideCollapsBtn){
				this.element.addClass("om-borderlayout-hide-collaps-btn");
			}
			if (this.options.fit) {
				$layout.css({
					"width" : "100%",
					"height" : "100%"
				});
			}
			for ( var i = 0; i < this.options.panels.length; i++) {
				var panel = $.extend({},this.options.panels[i]);
				var $panelEl = this.element.find("#" + panel.id);
				// 是否在panel之间的间隔上显示收缩/展开panel的trigger按钮
				var showCollapsTrigger = panel.collapsible && _self.options.hideCollapsBtn;
				// 添加代理工具条
				if(panel.collapsible && panel.region != "center"){
					this._createRegionProxy(panel,showCollapsTrigger);
				}
				
				// 扩展panel初始化参数，添加一些必要的事件
				if(panel.collapsible){
					$.extend(panel,{
						collapsible:false
					});
					if(!_self.options.hideCollapsBtn){
						$.extend(panel,{
							tools:[{
								iconCls:["panel-tool-collapse","panel-tool-collapse-hover"],
								handler:function(widget){
									_self.collapseRegion(widget.element.parent().attr("region"));
								}
							}]
						});
					}
				}
				if(panel.closable){
					var oldPanelOnClose = panel.onClose;
					$.extend(panel,{
						onClose:function(){
							oldPanelOnClose && oldPanelOnClose.call($panelEl[0]);
							_self._resizeRegion();
						}
					});
				}
				
				
				// 构建panel组件
				$panelEl.omPanel(panel);
				
				// 初始化north和south的宽度
				if(panel.region == "north" || panel.region == "south"){
					$panelEl.omPanel("resize",{"width":$layout.width()});
				}
				
				var margin = "0",
					spacing = this.options.spacing + "px";
				// 给panel添加resize功能
				if(panel.resizable && panel.region != "center"){
					var handles = "";
						handleClass = {};
					if(panel.region == "west"){
						handles = "e";
						handleClass.width = spacing;
						handleClass.right = "-" + spacing;
					} else if(panel.region == "east"){
						handles = "w";
						handleClass.width = spacing;
						handleClass.left = "-" + spacing;
					} else if(panel.region == "south"){
						handles = "n";
						handleClass.height = spacing;
						handleClass.top = "-" + spacing;
					} else if(panel.region == "north"){
						handles = "s";
						handleClass.height = spacing;
						handleClass.bottom = "-" + spacing;
					}
					$panelEl.parent().omResizable({
						handles : handles,
						helper : "om-borderlayout-resizable-helper-" + handles,
						stop : function(ui,event){
							$layout.find(">.om-borderlayout-mask").remove();
							ui.element.find(">.om-panel-body").omPanel("resize",ui.size);
							_self._resizeRegion();
							// 监听拖拽改变panel大小的事件
							_self.options.onAfterDrag && _self._trigger("onAfterDrag",null,ui.element);
						},
						start : function(ui,event){
							var helper = ui.element.omResizable("option","helper");
							// 修改resizable的helper的宽/高为spacing大小
							$("body").find("." + helper).css("border-width",_self.options.spacing);
							// 限制拖拉改变大小的范围
							var region = ui.element.attr("region"),
								maxWidth = $layout.width() - 2*_self._minWidth,
								maxHeight = $layout.height() - 2*_self._minHeight;
							if(region == "west"){
								maxWidth = $layout.width() - (_self._getRegionSize("east").width + _self._minWidth);
								ui.element.omResizable( "option", "maxWidth", maxWidth );
							} else if(region == "east"){
								maxWidth = $layout.width() - (_self._getRegionSize("west").width + _self._minWidth);
								ui.element.omResizable( "option", "maxWidth", maxWidth );
							} else if(region == "north"){
								maxHeight = $layout.height() - (_self._getRegionSize("south").height + _self._minHeight + _self.options.spacing);
								ui.element.omResizable( "option", "maxHeight", maxHeight );
							} else if(region == "south"){
								maxHeight = $layout.height() - (_self._getRegionSize("north").height + _self._minHeight + _self.options.spacing);
								ui.element.omResizable( "option", "maxHeight", maxHeight );
							}
							$('<div class="om-borderlayout-mask"></div>').css({
								width:$layout.width(),
								height:$layout.height()
							}).appendTo($layout);
							// 监听拖拽改变panel大小的事件
							_self.options.onBeforeDrag && _self._trigger("onBeforeDrag",null,ui.element);
						},
						minWidth : _self._minWidth,
						minHeight : _self._minHeight
						
					});
					$panelEl.parent().find(".om-resizable-handle").css(handleClass);
					margin = (panel.region == "south" ? spacing : 0) + " " +
							 (panel.region == "west" ? spacing : 0) + " " +
							 (panel.region == "north" ? spacing : 0) + " " +
							 (panel.region == "east" ? spacing : 0);
					
					// 如果隐藏收缩panel的按钮，则在panel之间的间隔条上显示收缩/展开trigger按钮
					if(showCollapsTrigger){
						var $collapsTrigger = $("<div class='om-borderlayout-collaps-trigger-"+panel.region+"'></div>");
						(function($panel){$collapsTrigger.click(function(){
							_self.collapseRegion($panel.attr("region"));
						});})($panelEl.parent());
						$panelEl.parent().find(".om-resizable-handle").append($collapsTrigger);
					}
				}
				
				$panelEl.parent()
					   .addClass("om-borderlayout-region")
					   .addClass("om-borderlayout-region-" + panel.region)
					   .css("margin",margin)
					   .attr("region",panel.region);
				//添加样式使borderlayout中使用panel样式和borderlayout中内嵌的panel body样式区分。以防发生样式覆盖的问题。
				$panelEl.addClass("om-borderlayout-region-body");
				//添加header class用来区别borderlayout和borderlayout中内嵌的panel使用的tools 图片
				$panelEl.prev().addClass("om-borderlayout-region-header");
			}
		},
		_getRegion : function(region){
			var $regionEl = this.element.find(">[region=\""+region+"\"]");
			return $regionEl.size()>0?$regionEl:false;
		},
		_getRegionProxy : function(region){
			var $proxyEl = this.element.find(">[proxy=\""+region+"\"]");
			return $proxyEl.size()>0?$proxyEl:false;
		},
		_getPanelOpts : function(region){
			for(var i = 0; i < this.options.panels.length; i++){
				if(region == this.options.panels[i].region){
					return this.options.panels[i];
				}
			}
			return false;
		},
        /**
         * 折叠某个区域的panel。
         * @name omBorderLayout#collapseRegion
         * @function
         * @param region 区域名称
         * @example
         * //折叠north区域的panel
         * $('#page').omBorderLayout('collapseRegion', 'north');
         */
		collapseRegion : function(region){
			var panel = this._getPanelOpts(region);
			if(!panel || !panel.collapsible){
				return;
			}
			var $region = this._getRegion(region);
				$body = $region.find(">.om-panel-body");
			if($region){
				var panelInstance = $.data($body[0],"omPanel");
				if(panelInstance.options.closed) return;
				if(panel.onBeforeCollapse && panelInstance._trigger("onBeforeCollapse") === false){
					return false;
				}
				$region.hide();
				panel.onCollapse && panelInstance._trigger("onCollapse");
				this._getRegionProxy(region).show();
				this._resizeRegion();
			}
		},
		/**
		 * 展开某个区域的panel。
		 * @name omBorderLayout#expandRegion
		 * @function
		 * @param region 区域名称
		 * @example
		 * //展开north区域的panel
		 * $('#page').omBorderLayout('expandRegion', 'north');
		 */
		expandRegion : function(region){
			var panel = this._getPanelOpts(region);
			if(!panel || !panel.collapsible){
				return;
			}
			var $region = this._getRegion(region);
				$body = $region.find(">.om-panel-body");
			if($region){
				var panelInstance = $.data($body[0],"omPanel");
				if(panelInstance.options.closed) return;
				if(panel.onBeforeExpand && panelInstance._trigger("onBeforeExpand") === false){
					return false;
				}
				$region.show();
				panel.onExpand && panelInstance._trigger("onExpand");
				this._getRegionProxy(region).hide();
				this._resizeRegion();
			}
		},
		/**
		 * 关闭某个区域的panel。
		 * @name omBorderLayout#closeRegion
		 * @function
		 * @param region 区域名称
		 * @example
		 * //关闭north区域的panel
		 * $('#page').omBorderLayout('closeRegion', 'north');
		 */
		closeRegion : function(region){
			var panel = this._getPanelOpts(region);
			if(!panel || !panel.closable){
				return;
			}
			var $region = this._getRegion(region);
				$body = $region.find(">.om-panel-body");
			if($region){
				var panelInstance = $.data($body[0],"omPanel");
				if(panelInstance.options.closed) return;
				
				$region.find(">.om-panel-body").omPanel("close");
				this._getRegionProxy(region).hide();
				this._resizeRegion();
			}
		},
		/**
		 * 打开某个区域的panel。
		 * @name omBorderLayout#openRegion
		 * @function
		 * @param region 区域名称
		 * @example
		 * //打开north区域的panel
		 * $('#page').omBorderLayout('openRegion', 'north');
		 */
		openRegion : function(region){
			var panel = this._getPanelOpts(region);
			if(!panel || !panel.closable){
				return;
			}
			var $region = this._getRegion(region);
				$body = $region.find(">.om-panel-body");
			if($region){
				var panelInstance = $.data($body[0],"omPanel");
				if(!panelInstance.options.closed) return;
				
				$region.find(">.om-panel-body").omPanel("open");
				this._getRegionProxy(region).hide();
				this._resizeRegion();
			}
		}

	});
})(jQuery);/*
 * $Id: om-grid-sort.js,v 1.5 2012/05/29 00:52:30 chentianzhen Exp $
 * operamasks-ui omGrid @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 * 
 * Depends:
 *  om-grid.js
 */

(function($) {
	/**
	 * 分页导航按钮样式与类别
	 */
	var pagerNvg = [
		{cls:"pFirst" , type:"first"},
		{cls:"pPrev" , type:"prev"},
		{cls:"pNext" , type:"next"},
		{cls:"pLast" , type:"last"},
		{cls:"pReload" , type:"reload"}
	];	
	function needChange(self , type){
		var oldPage = self._oldPage,
		    nowPage = self.pageData.nowPage;
		if("input" === type){
			return oldPage != $('.pControl input', self.element.closest('.om-grid')).val();
		}else if("reload" === type){
			return true;
		}else{
			return oldPage != nowPage;
		}
	}
	
    $.omWidget.addInitListener('om.omGrid',function(){
        var self = this,
            cm = this._getColModel(),
            tds = this._getHeaderCols().filter("[axis^='col']"),
            $pDiv = this.pDiv;
        
        $(tds).each(function(index){
            var sortFn=cm[index].sort;
            if(sortFn){
                var _this=$(this).click(function(){
                    var sortCol = cm[index].name;
                    var removeClass = _this.hasClass('asc')?'asc'
                                    : _this.hasClass('desc')?'desc'
                                    : null;
                    var sortDir=(removeClass=='asc'?'desc':'asc');
                    tds.removeClass('asc desc');
                    _this.addClass(sortDir);
                    
                    var extraData = self._extraData;
                    delete extraData.sortBy;
                    delete extraData.sortDir;
                    switch(sortFn){
                        case 'serverSide':
                            extraData.sortBy=sortCol;
                            extraData.sortDir=sortDir;
                            self.reload();
                            return;
                        case 'clientSide':
                            sortFn=function(obj1,obj2){
                                var v1=obj1[sortCol],v2=obj2[sortCol];
                                return v1==v2?0:v1>v2?1:-1;
                            };
                            break;
                        default:
                            // do nothing,keep sortFn==cm[index].sort
                    }
                    var datas = self.pageData.data;
                    if(removeClass==null){//从未排序变成升序排列
                        datas.rows=datas.rows.sort(sortFn);
                    }else{//升序变成降序，或降序变成升序，只需要反转数据即可
                        datas.rows=datas.rows.reverse();
                    }
                    self.refresh();
                });
                _this.children().first().append('<img class="om-grid-sortIcon" src="data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw=="></img>');
            }
        });
        //监听分页条按钮的事件，如果为客户端排序，每次都要清除掉排序的状态
        for(var i=0,len=pagerNvg.length; i<len; i++){
        	(function(i){
        		$pDiv.find("."+pagerNvg[i].cls).click(function(){
					var change = needChange(self , pagerNvg[i].type);
					tds.each(function(index){
						var $headerCol = $(this);
						if(change && ($headerCol.hasClass('asc') || $headerCol.hasClass('desc')) &&  "serverSide" !== cm[index].sort){
							$headerCol.removeClass('asc desc');
						}
					});
				});	 
        	})(i);
		}
        $('.pControl input', $pDiv).keydown(function(e){
        	var change = needChange(self , "input");
        	if (e.keyCode == $.om.keyCode.ENTER) {
        		tds.each(function(index){
					var $headerCol = $(this);
					if(change && ($headerCol.hasClass('asc') || $headerCol.hasClass('desc')) &&  "serverSide" !== cm[index].sort){
						$headerCol.removeClass('asc desc');
					}
				});
        	}
        });
        
        /**
	     * 清空omGrid的排序状态。比如先在第一列上进行了降序排列，以后每次取数时都是按这个降序排列条件来取数，如果要清空排序条件，调用本方法即可，调用后再次取数时就跟从未进行过排序一样。<br/>
	     * <b>注意：此方法仅清空排序状态，并不立即取数。</b>
	     * @function
	     * @example
	     *  $('.selector').omGrid('clearSort');
	     */
	    this.clearSort=function(){
	        var extraData = this._extraData;
	        extraData.sortBy = undefined;
	        extraData.sortDir = undefined;
	        this._getHeaderCols().removeClass('asc desc');
	        //$('tr:first th[axis^="col"]',this.thead).removeClass('asc desc');
	    };
    });
})(jQuery);/*
 * $Id: om-grid-rowexpander.js,v 1.7 2012/06/29 09:22:20 chentianzhen Exp $
 * operamasks-ui omGrid @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 * 
 * Depends:
 *  om-grid.js
 */

(function($) {
    $.omWidget.addInitListener('om.omGrid',function(){
        var self = this,
        	ops = this.options,
            rdp = ops.rowDetailsProvider, //渲染行详情的function
            cm = ops.colModel,
            $thead = this.thead;
            
        if( !(rdp && cm.length>0) ){
            return;
        }
        
        ops._onResizableCallbacks.push(_resizeRowExpander);
        ops._onResizableStopCallbacks.push(_resizeRowExpander);
        
        $thead.find("th:first").before('<th align="center" axis="expenderCol" class="expenderCol" rowspan='
        								+($.isArray(cm[0])?cm.length:1)+'><div style="text-align: center; width: 14px;"></div></th>');
        var autoExpandColIndex = -1,
            allColsWidth = 0;
            
        cm = this._getColModel();//确保此时cm为最简单形式的colModel
        $(cm).each(function(i){
        	if(cm[i].width == 'autoExpand'){
                autoExpandColIndex = i;
            }else{
                allColsWidth += cm[i].width;
            }
        });

        var expenderWidth = $thead.find('th[axis="expenderCol"]').width();
        if(autoExpandColIndex != -1){ //说明有某列要自动扩充
        	$thead.find('th[axis="col'+autoExpandColIndex+'"] >div')
        		.css("width" , "-="+expenderWidth);
        }else if(ops.autoFit){
            var percent = expenderWidth/allColsWidth;
            $thead.find('th[axis^="col"] >div')
            	.each(function(i){
            		$(this).css('width' , "-=" + parseInt(cm[i].width*percent));
            	});
        }
        
        var colCount= this._getHeaderCols().size();//总共列数
        this.tbody.delegate('td.expenderCol >div','click',function(event){
            var _this = $(this),
                $row = _this.closest('tr'),
                $next = $row.next('tr');
                
            if($next.hasClass('rowExpand-rowDetails')){ //已经构造过了，直接显示/隐藏
                $next.toggle();
            }else{ //没有构造过，则构造并显示
                var rowIndex = self._getTrs().index($row);
                    rowData = self._getRowData(rowIndex),
                    rowDetails = rdp? rdp(rowData,rowIndex):'&#160;';
                $row.after('<tr class="rowExpand-rowDetails"><td colspan="'+(colCount-1)+'"><div class="rowExpand-rowDetails-content">'+rowDetails+'</div></td></tr>');
            }
            
            _this.toggleClass('rowExpand-expanded').parent()
            	.attr('rowspan' , _this.hasClass('rowExpand-expanded')? 2:1);

			_resizeRowExpander.call(self);
            return false; //不触发onRowSelect和onRowClick事件
        });
    });
    
    function _resizeRowExpander(){
    	var $grid = this.element; 
    	var $rowExpander = $grid.find("tr.rowExpand-rowDetails");
    	
    	if($rowExpander.length == 0){
    		return ;
    	}
    	
    	//计算行详情的宽度
    	var $ht = this.hDiv.find("table");
    	var width = $ht.width() - $ht.find("th.expenderCol").outerWidth() - parseInt($rowExpander.find("> td").css("border-right-width"));

    	$rowExpander.each(function(){
    		$(this).find(".rowExpand-rowDetails-content").outerWidth(width);
    	});
    }
})(jQuery);/*
 * $Id: om-grid-roweditor.js,v 1.31 2012/06/29 09:21:04 chentianzhen Exp $
 * operamasks-ui omGrid @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 * 
 * Depends:
 *  om-grid.js
 *  om-button.js
 */

(function($) {
	var own = Object.prototype.hasOwnProperty;
	
	/**
	 *  行编辑插件初始化监听器
	 */
    $.omWidget.addInitListener('om.omGrid',function(){
    	var self = this,
    		$elem = self.element,
    		ops = self.options,
    		cm = this._getColModel();
    		
    	self._triggered = false;//是否已经触发过一次编辑了。
    	//如果所有列都不可编辑，那么_globalEditable=false,这时候所有原生的行都不可编辑，但如果此时设置了options.editMode=="insert",
    	//则新添加进来的行还是可以编辑的。
    	self._globalEditable = false;
    	
    	/**
    	 * <span style='color:red'>(作用于行编辑插件)</span>编辑模式。
    	 * 可选值为"all","insert"，默认为"all"。"all"表示所有行都是可编辑的，但如果所有的列都是不可编辑的，那么行编辑插件仍然会失效。
    	 * 如果有这样的需求，所有的行都是不可编辑的，但是此表格可以动态添加新行，而且这些新行在持久化到后台之前是可以编辑的，那么就要使用"insert"模式了。 
    	 * @name omGrid#editMode
    	 * @type String
    	 * @default "all"
    	 * @example
    	 * $('.selector').omGrid({width : 600, editMode:"insert");
    	 */
    	ops.editMode = ops.editMode || "all";//默认为all模式，即所有行都可以编辑
    	self._allEditMode = ops.editMode=="all";
    		
    	for(var i=0,len=cm.length; i<len; i++){
    		self._globalEditable = cm[i]["editor"]? true : false;
    		if(self._globalEditable){
    			break;
    		}
    	}
    	
    	if(!_isEditable(self)){
    		return ;//不开启编辑功能，该插件任何方法都会失效。
    	}
		self._editComps = {};//缓存每一列的编辑组件，key为colModel中的name,value为{model:模型,instance:组件实例,type:类型}
		self._errorHolders = {};//  name:div   每个编辑单元格对应的出错信息容器
		self._colWidthResize;//如果grid可编辑，在拖动标题栏改变列宽时，当前并不处于编辑状态，则设置此值为true,这样在显示编辑条时由此依据要重新计算各个组件的宽度
		
		ops._onRefreshCallbacks.push(_onRefresh);//只有数据刷新后才可以进行初始化
		ops._onResizableStopCallbacks.push(_onResizable);
		ops._onResizeCallbacks.push(_onResize);//动态改变宽高时要重新计算编辑条的大小
		
        this.tbody.delegate('tr.om-grid-row','dblclick',function(event){
        	editRenderer.call(this , self);
        }); 
        this.tbody.delegate('tr.om-grid-row','click',function(event){
        	if(self._triggered && self._editView.editing){
				if(self._validator){
					self._validator.valid() && editRenderer.call(this , self);
				}else{
					editRenderer.call(this , self);
				}
        	}
        });
        
        var btnScrollTimer;
        $elem.parent().scroll(function(){
        	if(self._triggered){
        		if(btnScrollTimer){
        			clearTimeout(btnScrollTimer);
        		}
        		btnScrollTimer = setTimeout(function(){
    				var pos = _getEditBtnPosition(self);
            		self._editView.editBtn.animate({"left":pos.left,"top":pos.top},self._editView.editing?"fast":0);
            		btnScrollTimer = null;
    			} , 300);
        	}
        });  
        
        /**
	 	*  添加行编辑插件的接口
	 	*/
	 	$.extend(this , {
	 		/** <span style='color:red'>(作用于行编辑插件)。</span>取消编辑状态。如果当前某行正处于编辑状态，取消此次的行编辑，相当于点击了行编辑条的“取消”按钮。
	 		 * @function 
	 		 * @name omGrid#cancelEdit
	 		 * @returns jQuery对象
	 		 * @example
	 		 * $(".selector").omGrid("cancelEdit");
	 		 */
	 		cancelEdit : 
	 			function(cancelBtn/*内部使用，当点击“按钮”也会调用此方法进行处理*/){
	 				var $ev = this._editView,
	 					ops = this.options;
			    	if(!_isEditable(this) || !this._triggered || (this._triggered && !$ev.editing)){
			    		return ;
			    	}
			    	$ev.view.hide();
			    	$ev.editing = false;
			    	if(this._rowAdding){
			    		this.deleteRow( this._getTrs().index(this.tbody.find("tr[_grid_row_id='"+self._editView.rowId+"']")) );
			    		this._rowAdding = false;
			    	}
					_resetForm(this);
					cancelBtn && $(cancelBtn).blur();
					ops.onCancelEdit && ops.onCancelEdit.call(this);
	    		},
	    	
	    	/** <span style='color:red'>(作用于行编辑插件)。</span>取消当前所有未提交到后台的改变，也即恢复所有行的原始数据。
	    	 * @function
	    	 * @name omGrid#cancelChanges
	    	 * @returns jQuery对象
	    	 * @example
	    	 * $(".selector").omGrid("cancelChanges");
	    	 */
	    	cancelChanges :
	    		function(){       	
	    			this.cancelEdit();		
	    			if(_noChanges(this)){
	    				return ;
	    			}
	    			_clearCache(this);
	    			_resetForm(this);
	    			this.refresh();
	    		},	
	    
	    	/** <span style='color:red'>(作用于行编辑插件)。</span>设置某一行进入编辑状态，如果此行正处于编辑状态中，则什么也不做。如果别的行正处于编辑状态中，则取消那一行此次编辑，然后本行进入编辑状态。 
	    	 * @function
	    	 * @param index 行索引，从0开始 
	    	 * @name omGrid#editRow
	    	 * @returns jQuery对象
	    	 * @example
	    	 * $(".selector").omGrid("editRow" , 1);
	    	 */
	    	editRow : function(index){
	    		if(!_isEditable(this)){
	    			return ;
	    		}
	    		editRenderer.call(this._getTrs().eq(index)[0] , this);
	    	},
	    	
	    	/** <span style='color:red'>(作用于行编辑插件)。</span>删除行，如果指定行是新添加的并未保存到后台，则进行物理删除；如果指定行是原本就存在的，则只是隐藏并进行标记,当调用了saveChanges后才进行物理删除。
	    	 * @function
	    	 * @param index 行索引，从0开始；或者为行索引数组(一般由getSelections得到)
	    	 * @name omGrid#deleteRow
	    	 * @returns jQuery对象
	    	 * @example
	    	 * $(".selector").omGrid("deleteRow" , 0);<br />
	    	 * 或者$(".selector").omGrid("deleteRow" , $(".selector").omGrid("getSelections"));
	    	 */
	    	deleteRow : function(index){
				if(!_isEditable(this) || (this._triggered && this._editView.editing) ){
					return ;
				}

	    		var $trs = this._getTrs(),
	    			self = this;
	    		if(!$.isArray(index)){
	    			index = [index];
	    		}
				index.sort(function(first , second){
					return second - first;//从大到小
				});	
				
				$(index).each(function(i , value){
					var $tr = $trs.eq(value),
						$next = $tr.next(),
						rowId = _getRowId($tr);
		    		if($tr.attr("_insert")){
		    			delete self._changeData["insert"][rowId];
		    			$tr.remove();
		    			if($next.hasClass("rowExpand-rowDetails")){//行详情
		    				$next.remove();
		    			}
		    		}else{
		    			self._changeData["delete"][rowId] = self._rowIdDataMap[rowId];
		    			$tr.attr("_delete", "true").hide();
		    			if($next.hasClass("rowExpand-rowDetails")){//行详情
		        			$next.hide();
		        		}
		        	}
				});
				this._refreshHeaderCheckBox();
	        },
	 
	        /** <span style='color:red'>(作用于行编辑插件)。</span>获取所有未保存的修改。如果没有指定type,返回的是所有的修改，格式为: {update:[],insert:[],delete:[]}，如果指定了参数，如
	    	 *  指定了"update"，则返回 [{},{}]
	    	 * @function
	    	 * @param type 可选值为："insert","update","delete" 
	    	 * @name omGrid#getChanges
	    	 * @returns 若指定了类型，返回[]，否则返回{update:[],insert:[],delete:[]}
	    	 * @example
	    	 * $(".selector").omGrid("getChanges" , "update");
	    	 */
	    	getChanges : function(type){
	    		var data = {"update":[] , "insert":[] , "delete":[]},
	    			reqType = type? type : "update",
	    			cData = this._changeData,
	    			i;
	    			
	    		if(reqType === "update"){
	    			var uData = cData[reqType];
	    			for(i in uData){
	    				own.call(uData , i) && data[reqType].push($.extend(true , {} , this._rowIdDataMap[i] , uData[i]));
	    			}
	    			reqType = type? type : "insert";
	    		}
	    		if(reqType === "insert"){
	    			var iData = cData[reqType];
	    			for(i in iData){
	    				own.call(iData , i) && data[reqType].push(iData[i]);
	    			}
	    			reqType = type? type : "delete";
	    		}
	    		if(reqType === "delete"){
	    			var dData = cData[reqType];
	    			for(i in dData){
	    				own.call(dData , i) && data[reqType].push(dData[i]);
	    			}
	    		}
	    		if(type){
	    			return data[type];
	    		}else{
	    			return data;
	    		}
	    	},
	 
		 	/** <span style='color:red'>(作用于行编辑插件)。</span>在指定位置动态插入一行。
	    	 * @function
	    	 * @param index 行索引，从0开始，或为"begin","end"分别表示在表格最前和最后插入行。
	    	 * @param rowData 插入的新行的初始值
	    	 * @param forceAdd 强制添加，设为true表示直接添加，不会弹出编辑框
	    	 * @name omGrid#insertRow
	    	 * @returns jQuery对象
	    	 * @example
	    	 * $(".selector").omGrid("insertRow");//插入最前面<br/>
	    	 * $(".selector").omGrid("insertRow" , 1);//插入索引1的位置<br/>
	    	 * $(".selector").omGrid("insertRow" , "end" , {id:"1"});//在末尾插入，并使用指定数据初始化<br/>
	    	 * $(".selector").omGrid("insertRow" , true);//插入最前面，并直接添加，不显示编辑框<br/>
	    	 * $(".selector").omGrid("insertRow" , 0 , {id:"2"} , true);//用指定数据在最前面插入新行，并且不显示编辑框<br/>
	    	 */
	    	insertRow : function(index , rowData , forceAdd){
	    		var ops = this.options,
	    			cm = this._getColModel(),
	    			$elem = this.element;
	    		
	    		if(!_isEditable(this) || this._rowAdding){
	    			return ;
	    		}
	    		//insertRow({id:10})
	    		if($.isPlainObject(index)){
	    			rowData = index;
	    			index = 0;
	    		}
	    		//insertRow(true)
	    		if(index === true){
	    			rowData = {};
	    			index = 0;
	    			forceAdd = true;
	    		}
	    		var $trs = this._getTrs(),
	    			rd = {};
	    		index = ("begin"==index || index==undefined)? 0 : ("end"==index? $trs.length : index);
	    
	    		for(var i=0,len=cm.length; i<len; i++){
	    			rd[cm[i]["name"]] = "";//默认都为空值
	        	}
	    		this._changeData["insert"][this._guid] = $.extend(true , rd , rowData);
	    		
	    		//创建新行
	    		var rowValues=this._buildRowCellValues(cm,rd,index),
	    			trContent = [],
	    			rowClasses=ops.rowClasses;
	    			isRowClassesFn= (typeof rowClasses === 'function'),
	    			rowCls = isRowClassesFn? rowClasses(index,rd):rowClasses[index % rowClasses.length],
	    			tdTmp = "<td align='$' abbr='$' class='grid-cell-dirty $'><div align='$' class='$' style='width:$px'>$</div></td>";//td模板
	    			    			
	    		trContent.push("<tr class='om-grid-row " + rowCls + "' _grid_row_id="+(this._guid++)+" _insert='true'>");
	    		this._getHeaderCols().each(function(i){
	                var axis = $(this).attr('axis'),
	                	wrap=false,
	                	html,
	                	cols,
	                	j;
	                if(axis == 'indexCol'){
	                    html="<a class='om-icon'>新行</a>";
	                }else if(axis == 'checkboxCol'){
	                    html = '<span class="checkbox"/>';
	                }else if(axis.substring(0,3)=='col'){
	                    var colIndex=axis.substring(3);
	                    html=rowValues[colIndex];
	                    if(cm[colIndex].wrap){
							wrap=true;
						} 
	                }else{
	                    html='';
	                }
	                cols = [this.align , this.abbr , axis , this.align , wrap?'wrap':'', $('div',$(this)).width() , html];
	                j=0;
	                trContent.push(tdTmp.replace(/\$/g , function(){
	                	return cols[j++];
	                }));
	            });
	            trContent.push("</tr>");
	    		
	    		var $tr = $(trContent.join(" ")),
	    			$destTr,
	    			$next;
	    		if(index==0){
	    			$tr.prependTo($elem.find(">tbody"));
	    		}else{
	    			$destTr = $trs.eq(index-1);
	    			$next = $destTr.next();
	    			$destTr = $next.hasClass("rowExpand-rowDetails")? $next : $destTr;//处理行详情
	    			$destTr.after($tr);
	    		}
	    		if(!forceAdd){
	    			this.editRow(index);
	    			//正在添加新行标志，如果这时候校验通不过，新增行在编辑正确并保存之前双击其它行是没有反应的。此外，如果用户点击取消，这时候会删除新增的行,在第一次点击"保存"按钮时要设置此值为false。
	    			this._rowAdding = true;
	    		}
	    	},
	    	
	    	/** <span style='color:red'>(作用于行编辑插件)。</span>保存客户端数据。注意，此方法不会提交请求到后台，而是会认为所有的数据改变都已经成功提交到后台去了，所以它会清除所有脏数据标志。
	    	 * 一般情况下，在您自己的保存方法事件回调中，调用getChanges方法获取当前所有的改变，并自己提交到后台，然后在成功回调方法中再调用本方法。
	    	 * 一旦调用本方法后，cancelChanges方法是无法对调用此方法前的所有改变起作用的。
	    	 * @function
	    	 * @name omGrid#saveChanges
	    	 * @returns jQuery对象
	    	 * @example
	    	 * $(".selector").omGrid("saveChanges");
	    	 */
	    	saveChanges : function(){
	    		this.cancelEdit();
				if(_noChanges(this)){
					return ;
				}
				var uData = this._changeData["update"],
					$trs = this.element.find("tr.om-grid-row"),
					newRowsData = [];
				for(var i in uData){
					if(own.call(uData , i)){
						$.extend(true , this._rowIdDataMap[i] , uData[i]);
					}
				}
				var self = this;
				$trs.each(function(index , tr){
					var $tr = $(tr);
					if($tr.attr("_delete")){
						$tr.remove();
					}else{
						newRowsData.push(_getRowData(self , tr));
					}
				});
				this.pageData.data.rows = newRowsData;
				_clearCache(this);
				_resetForm(this);//重置编辑表单，清除错误信息
				this.refresh();//重用dataSource中的数据进行刷新
	    	},
	    	/**
             * 编辑一行之前执行的方法。
             * @event
             * @name omGrid#onBeforeEdit
             * @param rowIndex 行号（从0开始）
             * @param rowData 选择的行所代表的JSON对象
             * @default 无
             * @example
             *  $(".selector").omGrid({
             *      onBeforeEdit:function(rowIndex , rowData){
             *          alert("您将编辑的记录索引为:" + rowIndex);
             *      }
             *  });
             */
             onBeforeEdit : function(rowIndex , rowData){},
             /**
             * 编辑一行之后执行的方法。
             * @event
             * @name omGrid#onAfterEdit
             * @param rowIndex 行号（从0开始）
             * @param rowData 选择的行所代表的JSON对象
             * @default 无
             * @example
             *  $(".selector").omGrid({
             *      onAfterEdit:function(rowIndex , rowData){
             *          alert("您刚刚编辑的记录索引为:" + rowIndex);
             *      }
             *  });
             */
             onAfterEdit : function(rowIndex , rowData){},
             /**
             * 取消编辑一行时执行的方法。
             * @event
             * @name omGrid#onCancelEdit
             * @default 无
             * @example
             *  $(".selector").omGrid({
             *      onCancelEdit:function(){
             *          alert("您取消了编辑状态");
             *      }
             *  });
             */
             onCancelEdit : function(){},
	    	/**
	    	 * (覆盖)获取最新的所有数据
	    	 */
	    	getData : function(){
	    		var result = this.pageData.data,
					$trs = this._getTrs(),
					self = this;
					
	    		if(_isEditable(this) && _hasChange(this)){
	    			result = {total:result.total};
	    			result.rows = [];
					$trs.each(function(index , tr){
	    				result.rows.push(self._getRowData(index));
					});
				}
				return result;
	    	},
	    	/**
	    	 * (覆盖)获取最新的行数据。由于可以插入新的数据行，所以此方法要进行重写。
	    	 */
	    	_getRowData : function(index){
	    		var $tr = this._getTrs().eq(index),
	    			rowId = _getRowId($tr),
	    			rowData;
	    		if(_noChanges(this)){
	    			return this.pageData.data.rows[index];
	    		}
	    		if($tr.attr("_insert")){
	    			rowData = this._changeData.insert[rowId];
	    		}else{
	    			var origRowData = this._rowIdDataMap[rowId],
	    				uData = this._changeData["update"];
	    			rowData = origRowData;
	    			if(uData[rowId]){
	    				rowData = $.extend(true , {} , origRowData , uData[rowId]);
	    			}
	    		}
	    		return rowData;
	    	}
	 	});    
    });
    
    function editRenderer(self){
    	var $tr = $(this),
    		$elem = self.element,
    		$editRow,
    		$editForm,
    		scrollLeft,
    		cm = self._getColModel(),
    		editComp,//{name:{type:组件类型,model:列对应model,instance:组件实例}}
    		lastValue,
    		ops = self.options;
    	
    	if(!_isEditable(self)){
    		return ;
    	}
    		
    	//如果是insertEditMode模式，那么除非该行是新增的，否则不可编辑，直接返回
    	if(!self._allEditMode && !$tr.attr("_insert")){
    		return ;
    	}
    	
    	//当前行正处于编辑状态，直接返回
    	if(self._triggered  
    		&& self._editView.editing
    		&& _getRowId($tr) == self._editView.rowId){
			return ;        		
    	}
    	
    	if(self._rowAdding){//处于新增行编辑中，在新增行第一次保存前此方法不响应。
    		return ;
    	}
    	
    	var rowIndex = self._getTrs().index($tr),
    		rowData = self._getRowData(rowIndex);
    		
    	if(ops.onBeforeEdit && ops.onBeforeEdit.call(self , rowIndex , rowData) === false){
    		return ;
    	}
    	_showEditView(self , $tr);
    	$editRow = self._editView.editRow;
    	$editForm = $editRow.find(">.grid-edit-form");
    	scrollLeft = $elem.parent().scrollLeft();
    	
    	self._getHeaderCols().each(function(index){
        	var axis = $(this).attr('axis'),
				model,
				$cell = $tr.find("td:eq("+index+")"),
				name,//编辑组件input域的名字，这是校验所必需的
				compKey;//指editComps的key
        	if(axis.substring(0,3)=='col'){
             	var colIndex=axis.substring(3);
             	model = cm[colIndex];
        	}else{
        		if($.isEmptyObject(self._editComps)){//保证再次编辑其它行时不会重复添加 padding-left.
        			$editRow.css("padding-left", parseInt($editRow.css("padding-left")) + $cell.outerWidth());
        		}
        		return ;
        	}
        	var editor = model.editor;
        	if(!self._triggered){
        		//如果列不可编辑，则默认type="text"
            	//列不可编辑的条件: 
            	//(1)colModel没有editor属性
            	//(2)colModel有editor属性，并且editor有editable属性，那么editable===false则不进行编辑，或者editable为函数且返回false也不进行编辑。
				if(!editor || 
	            	(editor && 
	            		(editor.editable===false || 
	                    	($.isFunction(editor.editable) && editor.editable()===false) ) ) ){
					var renderer = editor && editor.renderer;
	       			model.editor = editor = {};
	       			editor.type = "text";
	       			if(renderer){
	       				editor.renderer = renderer;
	       				editor.type = "custom";
	       			}
	       			editor.editable = false;
	   			}else{
	   				editor.type = editor.type || "text";
	   				editor.editable = true;
	   				if(editor.rules){
	   					self._validate = true;//只有需要检验才需要进行检验
	   				}
	   			}
	   			compKey = model.editor.name || model.name;
	   			editor.options = editor.options || {};
	   			self._editComps[compKey] = {};
        	}else{
        		compKey = model.editor.name || model.name;
        	}
        	editComp = self._editComps[compKey];
   			lastValue = (lastValue=_getLastValue(self , $tr , model))==undefined? "":lastValue;
   			
   			//可编辑并且可校验，添加对应的出错信息显示容器
   			if(!self._triggered && editor.editable && editor.rules){
   				self._errorHolders[compKey] = $("<div class='errorContainer' style='display:none'></div>").appendTo($elem.parent());
   			}
   			var $ins = editComp.instance,
   				$wrapper,
   				type = editor.type;
   			if(!$ins){
   				$wrapper = $("<div style='position:absolute'></div>").css({left:$cell.position().left+scrollLeft,top:3}).appendTo($editForm).addClass("grid-edit-wrapper"); 
   				$ins = editComp.instance = $("<input></input>").attr({"name":compKey,"id":compKey}).appendTo($wrapper);
   				if("text"!=type && "custom"!=type){//实例化组件
   					$ins[type](editor.options);
   				}
   				if("omCalendar"==type || "omCombo"==type){
   					var $parent = $ins.parent();
   					if("omCalendar"==type){
   						$ins.val(lastValue).width($cell.outerWidth()-24);
   						$ins.width($cell.outerWidth(true) - ($parent.outerWidth(true) - $ins.width()));
   					}else{
   						//omCombo由于原来的input被隐藏了，为了可以校验，要把id和name移到显示的那个代理input
						$ins.next("input").attr({id:$ins.prop("id") , name:$ins.prop("name")});
						$ins.attr({id:"",name:""});
						$ins.next("input").width($cell.outerWidth(true) - ($parent.outerWidth(true) - $ins.next("input").width()));		
   					}
   				}else{
   					if("text"==type){
   						$ins.addClass("grid-edit-text");
   						if(!editor.editable){
   							$ins.attr("readonly" , "readonly").addClass("readonly-text");
   						}
   					}
   					if("custom"==type){
   						$ins = editComp.instance = $wrapper.html(editor.renderer.call(self , lastValue , rowData));
   					}
   					$ins.width($cell.outerWidth(true) - ($ins.outerWidth(true) - $ins.width()));
   				}
   				editComp.model = model;
				editComp.type = type;
				editComp.id = model.name;
				if("custom" != type){//非"custom"类型失去焦点要隐藏错误信息
					var $target = "omCombo"==type? $ins.next() : $ins;
					$target.blur(function(event){
						var eHolder = self._errorHolders[compKey];
						eHolder && eHolder.hide();
					});
				}
   			}
			switch(type){
				case "omCalendar":
					$ins = $ins.val(lastValue).omCalendar();
					break;
				case "omNumberField":
					$ins.val(lastValue).trigger("blur");//进行错误处理
					break;
				case "omCombo":
					$ins.omCombo("value" , lastValue);
					break;
				case "text":
					$ins.val(lastValue);
					break;
				case "custom":
					$ins.html( editor.renderer.call(self , lastValue , rowData) );
				default:;
			}
        });
        !self._triggered && self._validate && _bindValidation(self);
        
        self._validator && self._validator.form();//触发校验
    	self._triggered = true;
    }
    
    /**
     * 清除更改数据缓存
     */
    function _clearCache(self){
    	self._changeData = {"update":{},"insert":{},"delete":{}};
    }
    
    function _noChanges(self){
    	return !_isEditable(self) || !_hasChange(self);	
    }
    
    function _getEditBtnPosition(self){
		var $elem = self.element,
			$bDiv = $elem.parent(),
			ev = self._editView,
			$editView = ev.view,
			$editBtn = ev.editBtn,
			$editRow = ev.editRow,
			pos = {};
			
		pos.top = $editRow.height();
		if($elem.width() < $bDiv.width()){
			pos.left = $elem.width()/2 - $editBtn.width()/2;
		}else{
			pos.left = $bDiv.scrollLeft() + $bDiv.width()/2 - $editBtn.width()/2;
		}
		return pos;
    }
    
    function _onRefresh(){
    	if(_isEditable(this)){
    		_clearCache(this);
    		_buildRowIdDataMap(this);
    		if(this._triggered){
				_resetForm(this);
				this._editView.view.hide();
				this._editView.editing = false;
				this.hDiv.scrollLeft(0);
				this.element.parent().scrollLeft(0);
    		}
    	}
	}
    
    function _isEditable(self){
    	return !self._allEditMode || self._globalEditable;
    }
    
	//建立rowId与原生行数据的一一映射。
	function _buildRowIdDataMap(self){
		var rowsData = self.getData().rows;
    	self._rowIdDataMap = {};
    	self._getTrs().each(function(index , tr){
    		//行的索引与原生数据的映射，方便通过rowId获取原生行的数据。
    		self._rowIdDataMap[_getRowId(tr)] = rowsData[index];
    	});
	}
	
	//重置校验表单，同时清除错误信息
	function _resetForm(self){
		if(self._validator){
			self._validator.resetForm();
			//清空错误信息
			$.each(self._errorHolders,function(name , holder){
    			holder.empty().hide();
			});
		}
	}
	
	function _hasChange(self){
		var changeData = self._changeData;
		return !($.isEmptyObject(changeData["update"]) && $.isEmptyObject(changeData["insert"]) && $.isEmptyObject(changeData["delete"]));
	} 
	
    //显示可编辑的视图
	function _showEditView(self , tr){
		var $elem = self.element,
			$editView = $elem.next(".grid-edit-view"),
			$editBtn,
			$editRow,
			position = $(tr).position(),
			scrollTop = $elem.parent().scrollTop(),
			ops = self.options;
		if($editView.length == 0){
			$editView = $("<div class='grid-edit-view'><div class='body-wrapper'><div class='grid-edit-row'><form class='grid-edit-form'></form></div>"
					+"<div class='gird-edit-btn'><input type='button' class='ok' value='确定'/><input type='button' class='cancel' value='取消'/></div></div></div>")
				.width($elem.outerWidth())
				.insertAfter($elem);
			var $editBtn = $editView.find(".gird-edit-btn"),
			$editRow = $editBtn.prev(".grid-edit-row"),
			pos;
			self._editView = {view:$editView , editRow:$editRow , editBtn:$editBtn};//进行缓存
			pos = _getEditBtnPosition(self);
			$editBtn.css({"left": pos.left,"top":pos.top});
			//绑定按钮的事件
			var $okBtn = $editBtn.find("input.ok").omButton(),
				$cancelBtn = $editBtn.find("input.cancel").omButton();
			$okBtn.click(function(){
				//这里再次进行校验，主要是由于用户如果自己设置那些编辑输入域的值是不会触发校验的
				if(self._validator && !self._validator.form()){
					return ;
				}
				//由于闭包的原因，这里不可以直接使用tr,不然永远都是指向同一个tr
				var $tr = $elem.find("tr[_grid_row_id='"+self._editView.rowId+"']");
				_saveEditValue(self , $tr);
				$editView.hide();
				self._editView.editing = false;
				$okBtn.blur();
				if(self._rowAdding){
					self._refreshHeaderCheckBox();				 	
				 	self._rowAdding = false;	
				}
				var rowIndex = self._getTrs().index($tr);
				ops.onAfterEdit && ops.onAfterEdit.call(self , rowIndex , self._getRowData(rowIndex));
			});
			$cancelBtn.click(function(){
				self.cancelEdit(this);
			});
		}
		self._editView.rowId = _getRowId(tr);//当前正在编辑的行的id
		if(self._editView.editing){//如果当前正在编辑，那么是需要进行动画的
			$editView.animate({"top":position.top + scrollTop}, "fast");
		}else{
			$editView.css({"top":position.top + scrollTop});
			self._editView.editing = true;
			$editView.show();
			if(self._colWidthResize){
				_resizeView(self);
				self._colWidthResize = false;
			}
		}
	}
	
	function _onResize(){
		if(!_isEditable(this) || !this._triggered){
			return ;
		}
		if(this._editView.editing){
			this.element.parent().scrollLeft(0);
			var self = this;
			//在ff和chrome下，如果不用异步执行，位置计算不太准确，估计跟scrollLeft有关
			setTimeout(function(){
				_resizeView(self);
			} , 0);
		}else{
			this._colWidthResize = true;
		}
	}
	
	/**
	 * $col以及differWidth只在用鼠标改变标题列大小时才会有值，这种情况下，为了提高效率，只有被改变的列对应的编辑组件要改变宽度。
	 */
	function _resizeView(self , $col , differWidth){
		var $elem = self.element,
			view = self._editView,
			$editView = view.view,
			scrollLeft = $elem.parent().scrollLeft(),
			$editBtn = view.editBtn,
			updated;
		
		$editView.width($elem.outerWidth());
		self._getHeaderCols().each(function(index , th){
			var id = $(th).attr("abbr"),
				$th = $(th),
				target = $col && $col.prop("abbr")===$th.prop("abbr");
			if(target){
				updated = true;
			}
			//鼠标拖动列，目标列前面的列不变
			if($col && !updated){
				return ;
			}
			
			$.each(self._editComps , function(name , comp){
				var $ins = comp.instance,
					type = comp.type;
				if(id == comp.id){
					//鼠标拖动列，目标列后面的列改变left即可
					if(!target && $col){
						$ins.closest("div.grid-edit-wrapper").css("left" , "+="+differWidth);
						return false;
					}
					if(!target){
						$ins.closest("div.grid-edit-wrapper").width($th.outerWidth()).css("left" , $th.position().left + scrollLeft);
					}
					//改变编辑输入域组件的宽度
					if("omCalendar"==type || "omCombo"==type){
						var $parent = $ins.parent();
						if("omCalendar"==type){
	   						$ins.width($th.outerWidth()-24);
	   						$ins.width($th.outerWidth(true) - ($parent.outerWidth(true) - $ins.width()));
	   					}else{
	   						$ins.next("input").width($th.outerWidth(true) - ($parent.outerWidth(true) - $ins.next("input").width()));
	   					}
					}else{
						$ins.width($th.outerWidth(true) - ($ins.outerWidth(true) - $ins.width()));
					}
				}
			});
		});
		var pos = _getEditBtnPosition(self);
		if($col){
			$editBtn.animate({"left":pos.left,"top":pos.top},"fast");
		}else{
			$editBtn.css({"left":pos.left,"top":pos.top});
		}
		
	}
	
	function _onResizable($th , differWidth){
    	//如果不是处于编辑状态，由于编辑条是隐藏状态的，这时候计算各个编辑组件宽度很可能会是错误的，所以非编辑状态下什么也不处理。留到显示编辑条时再做处理
    	if(!_isEditable(this) || !this._triggered || !this._editView.editing){
    		this._colWidthResize = true;
    		return ;
    	}
    	_resizeView(this , $th , differWidth);
    }
	
	function _saveEditValue(self , tr){
		var $tr = $(tr),
			$editRow = self._editView.editRow,
			comps = self._editComps,
			rowId = _getRowId($tr),
			index = self._getTrs().index($tr),
			rowData = self._getRowData(index);
			
		$.each(comps , function(name , comp){
			var key = comp.model.name,
				newValue = _getCompValue(self , $tr , comp),
				originalValue,
				html,
				updateRowData;
				
			if($tr.attr("_insert")){
				self._changeData.insert[rowId][key] = newValue;
			}else{
				originalValue = _getRowData(self , tr)[key];
				updateRowData = self._changeData.update[rowId];
				//注意，""==0为true,false==""为true,这里为了正确进行比较，全部转化为字符串再进行比较。
				if(String(newValue) === String(originalValue)){
					_toggleDirtyFlag($tr , comp.model , false);
					updateRowData && delete updateRowData[key];
					$.isEmptyObject(updateRowData) && delete self._changeData.update[rowId];
				}else{
					_toggleDirtyFlag($tr , comp.model , true);
					updateRowData = self._changeData.update[rowId] = updateRowData || {};
					updateRowData[key] = newValue;
				}
			}
			//更新回表格
			if(comp.model.renderer){
				html = comp.model.renderer(newValue , rowData ,index);
			}else{
				html = newValue==undefined?"" : newValue;
			}
			$tr.find("td[abbr='"+key+"'] >div").html(html);
		});
	}
	
	//更换更改标志样式，show=true表示显示数据已被更改样式
	function _toggleDirtyFlag($tr , model , show){
		$tr.find("td[abbr='"+model.name+"']").toggleClass("grid-cell-dirty" , show);
	}
	
	function _getRowId(tr){
		return $(tr).attr("_grid_row_id");
	}
	
	//获取编辑条中列对应的组件的值
	function _getCompValue(self , $tr , comp){
		var value,
			rowData = _getRowData(self , $tr),
			$ins = comp.instance;
		switch(comp.type){
		case "omCalendar":
			value = $ins.val();
		case "omNumberField":
			value = $ins.val();
			break;
		case "omCombo":
			value = $ins.omCombo("value");
			break;
		case "text":
			value = $ins.val();
			break;
		case "custom":
			if(comp.model.editor.getValue){
				return comp.model.editor.getValue.call($ins , rowData , comp.model.name);
			}
		default:
			break;	
		}
		return value;
	}
	
	//获取某列最新的值，如果是新增的，从新增里边获取，如果更新过了，从更新里边获取最新值
	function _getLastValue(self , $tr , model){
		var value,
			name = model.name;
		if($tr.attr("_insert")){
			//新增的话从insert里边拿到最新的值
			value = _getRowData(self , $tr)[name];
		}else{
			var updateData = self._changeData.update[_getRowId($tr)];
			if(updateData && updateData[name] != null){//此数据被更新过了
				value = updateData[name];//最新值
			}else{//获取原始值
				value = _getRowData(self , $tr)[name];
			}
		}
		return value;
	}
	
	//获取原始行数据，如果是新添加进去的，获取的是新添加进去的行数据
	function _getRowData(self , tr){
		var rowId = _getRowId(tr);
		return $(tr).attr("_insert")?self._changeData.insert[rowId] : self._rowIdDataMap[rowId];
	}
	
	function _bindValidation(self){
		var $editForm = self._editView.editRow.find(">.grid-edit-form"),
			valiCfg = {},
			rules = valiCfg.rules = {},
			messages = valiCfg.messages = {},
			colModel = self._getColModel();
		$.each(colModel , function(index , model){
			var customRules = model.editor.rules;
			if(customRules){
				var r = rules[model.editor.name || model.name] = {},
					msg = messages[model.editor.name || model.name] = {};
				if(customRules.length>0 && !$.isArray(customRules[0])){
					var temp = [];
					temp.push(customRules);//包装成[[],[]]这种统一形式
					customRules = temp;
				}
				for(var i=0,len=customRules.length; i<len; i++){
					var name = customRules[i][0];//检验类型
					r[name]  = customRules[i][1] == undefined? true : customRules[i][1]; //没有定义值的统一传 true
					if(customRules[i][2]){
						msg[name] = customRules[i][2];
					}
				}
			}
		});	
		
		$.extend(valiCfg , {
			onkeyup : function(element){
				this.element(element);
			},
			//必须覆盖此方法，不然会默认生成错误信息容器，而错误信息的产生已经在showErrows处理了，所以此方法什么也不做
			errorPlacement : function(error, element){
			},
			showErrors : function(errorMap, errorList){
				if(errorList && errorList.length > 0){
		        	$.each(errorList,function(index,obj){
		        		var $elem = $(obj.element),
		        			name = $elem.attr("name");
		        		var errorHolder = self._errorHolders[name];
		        		if(errorHolder){
		        			var docPos = $elem.offset(),
		        				tablePos = self.element.offset();
		        			errorHolder.css({left:docPos.left-tablePos.left+$elem.outerWidth(),top:docPos.top-tablePos.top+$elem.outerHeight()}).html(obj.message);
		        			if($elem.is(":focus")){
		        				errorHolder.show();
		        			}
		        		}
	 	            });
		    	}else{
		    		$.each(this.currentElements , function(index , elem){
		    			var errorHolder = self._errorHolders[$(elem).attr("name")];
		    			errorHolder && errorHolder.empty().hide();
		    		});
		    	}
		    	//处理"确定"按钮的状态
		    	var $okBtn = self._editView.editBtn.find("input.ok"),
		    		correct = true;
		    	$.each(self._errorHolders,function(name , errorHolder){
		    		if(!errorHolder.is(":empty")){
		    			return correct = false;
		    		}
		    	});
		    	correct ? $okBtn.omButton("enable"): $okBtn.omButton("disable");
		    	this.defaultShowErrors();
			}
		});
		self._validator = $editForm.validate(valiCfg);
		
		//绑定鼠标事件
		$.each(self._editComps , function(name , comp){
			var editor = comp.model.editor;
			if(editor.editable && editor.rules){
				var key = editor.name || comp.model.name,
					errorHolder = self._errorHolders[key],
					$target = comp.type=="omCombo"? comp.instance.next("input"):comp.instance;
					
				$target.mouseover(function(){
					if(errorHolder && !errorHolder.is(":empty")){
						errorHolder.show();
					}
				})
				.mouseout(function(){
					errorHolder && errorHolder.hide();
				});
			}
		}); 
	}
})(jQuery);/*
 * $Id: om-grid-headergroup.js,v 1.9 2012/06/13 05:37:55 chentianzhen Exp $
 * operamasks-ui omGrid @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 * 
 * Depends:
 *  om-grid.js
 */

(function($) {

	$.omWidget.addBeforeInitListener('om.omGrid',function(){
		var cm = this._getColModel();
		if(!$.isArray(cm) || cm.length<=0 || !$.isArray(cm[0])){
			return ;
		}
		_buildBasicColModel(this);
		this.hDiv.addClass("hDiv-group-header");
		
		//把方法绑定在实例上，这样非多表头实例不受影响
		$.extend(this , {
			_getColModel : function(){
				return this._colModel;
			},
			_getHeaderCols : function(){
				var result = [],
					op = this.options,
					$hDiv = this.hDiv,
					$ths = $hDiv.find("th[axis^='col']");
					$($ths).each(function(){result.push(this);});
					
					result.sort(function(first , second){
						return first.axis.slice(3) - second.axis.slice(3);
					});
					
					!op.singleSelect && result.unshift($hDiv.find("th[axis='checkboxCol']")[0]);					
					op.showIndex && result.unshift($hDiv.find("th[axis='indexCol']")[0]);
					op.rowDetailsProvider && result.unshift($hDiv.find("th[axis='expenderCol']")[0]);
					
					return $(result);//此处返回jquery对象主要跟omGrid中的原生方法返回值保持一致
			},
			_buildTableHead : function(){
				var op=this.options,
	                $elem=this.element,
	                $grid = $elem.closest('.om-grid'),
	                cm= op.colModel,
	                allColsWidth = 0, //colModel的宽度
	                autoExpandColIndex = -1,
	                tmp = "<th class='$' $ $ $ $ $><div class='$' style='text-align:$; $'>$</div></th>",
	                content = ["<thead>"],
	                cols,
	                item,
	                rowHeader,
	                $thead;
	            for(var i=0,row=cm.length; i<row; i++){
	            	content.push("<tr>");
	            	if(i == 0){//行号和多选框标题列永远在第一行
	            		if(op.showIndex){
	            			content.push("<th class='indexCol data-header-"+row+"' align='center' axis='indexCol' rowspan="+row+"><div class='indexheader' style='text-align:center;width:25px;' /></th>");
	            		}
	            		if(!op.singleSelect){
	            			content.push("<th class='checkboxCol data-header-"+row+"' align='center' axis='checkboxCol' rowspan="+row+"><div class='checkboxheader' style='text-align:center;width:17px;'><span class='checkbox'/></div></th>");
	            		}
	            	}
	            	rowHeader = cm[i];
	            	for(var j=0,col=rowHeader.length; j<col; j++){
	            		item = rowHeader[j];
	            		var cmWidth = item.width || 60,
	            			cmAlign = item.align || 'center',
	            			name = item.name;
	            		if(item.name && cmWidth == 'autoExpand'){
		                    cmWidth = 0;
		                    autoExpandColIndex = _getColIndex(this,item);
		                }
		                var cls = item.wrap?"wrap" : "";
		                
		                cls += (item.name? " data-header-" : " group-header-")+(item.rowspan?item.rowspan:1);
		                cols = [cls, 
		                		item.align?"align="+item.align:"", 
		                		name?"axis=col"+_getColIndex(this,item):"", 
		                		name?"abbr="+name:"", 
		                		item.rowspan?"rowspan="+item.rowspan:"" ,
		                		item.colspan?"colspan="+item.colspan:"",  
		                		item.wrap?"wrap":"", 
		                		cmAlign, 
		                		name?"width:"+cmWidth+"px":"" , 
		                		item.header];
		                _buildTh(content , cols , tmp);
		                
		                if(item.name){
		                	allColsWidth += cmWidth;
		                }
	            	}
	            	content.push("</tr>");
	            }
	            content.push("</thead>");
	            $('table',this.hDiv).html(content.join(""));
	            $thead = $('thead',this.hDiv);
	            
	            this._fixHeaderWidth(autoExpandColIndex , allColsWidth);
	            this.thead = $thead;
	            $thead = null;
			}
		});

	});
	
	function _getColIndex(self , header){
		var cm = self._getColModel();
		for(var i=0,len=cm.length; i<len; i++){
			if(cm[i].name == header.name){
				return i;
			}
		}
	}
	
	function _buildTh(content , cols , tmp){
    	var j=0;
		content.push(tmp.replace(/\$/g , function(){
			return cols[j++];
		}));
	}
	
	/**
	 * 创建只包含最基本数据的colModel。也就是创建出没有多表头时的基本colModel。注意，生成的colModel是具有顺序的。
	 */
	function _buildBasicColModel(self){
		//基本colModel缓存引用
		self._colModel = [];
		var cm = self._getColModel(),
			matrix = [],
			realRowHeader = [],//colModel中非最后一行的具有真实数据意义的header缓存
			rows = 1,
			cols = 1,
			rowHeader,
			item,
			colIndex,//每个header从哪一列开始渲染
			len = cm.length;

		for(var i=0; i<len; i++){
			matrix[i] = [];
		}
		for(var i=0,row=len; i<row; i++){
			rowHeader = cm[i];
			
			for(var j=0,col=rowHeader.length; j<col; j++){
				item = rowHeader[j];
				rows = item.rowspan || 1;
				cols = item.colspan || 1;
				
				colIndex = _checkMatrix(matrix , i , rows , cols);
				if(item.name){
					realRowHeader.push({header:item , colIndex:colIndex});
				}
			}
		}
		realRowHeader.sort(function(first , second){
			return first.colIndex - second.colIndex;
		});
		i=0;
		while(realRowHeader[i] && self._colModel.push(realRowHeader[i++].header));
	}
	
	function _checkMatrix(matrix , index , rows , cols){
		var i=0;
		while(matrix[index][i] && ++i);
		for(var j=index; j<index+rows; j++){
			for(var k=i; k<i+cols; k++){
				matrix[j][k] = true;
			}
		}
		return i;
	}
		
})(jQuery);