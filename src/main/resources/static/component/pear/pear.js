window.rootPath = (function (src) {
	src = document.currentScript
		? document.currentScript.src
		: document.scripts[document.scripts.length - 1].src;
	return src.substring(0, src.lastIndexOf("/") + 1);
})();

function isPearStaticMode() {
	if (window.location.protocol === "file:") {
		return true;
	}

	try {
		return window.location.protocol === "about:" &&
			window.parent &&
			window.parent !== window &&
			window.parent.PEAR_IS_STATIC === true;
	} catch (e) {
		return false;
	}
}

window.PEAR_IS_STATIC = isPearStaticMode();

if (window.PEAR_IS_STATIC && !window.PEAR_STATIC_DATA) {
	try {
		if (window.parent && window.parent !== window && window.parent.PEAR_STATIC_DATA) {
			window.PEAR_STATIC_DATA = window.parent.PEAR_STATIC_DATA;
		}
	} catch (e) {}
}

function installPearStaticAdapter($) {
	if (!$ || !window.PEAR_IS_STATIC || !window.PEAR_STATIC_DATA || $.pearStaticAdapterInstalled) {
		return;
	}

	var routes = (window.PEAR_STATIC_DATA && window.PEAR_STATIC_DATA.routes) || {};
	var pages = (window.PEAR_STATIC_DATA && window.PEAR_STATIC_DATA.pages) || {};
	var scripts = (window.PEAR_STATIC_DATA && window.PEAR_STATIC_DATA.scripts) || {};
	var mock = (window.PEAR_STATIC_DATA && window.PEAR_STATIC_DATA.mock) || {};
	var originalAjax = $.ajax;
	var OriginalXHR = window.XMLHttpRequest;

	function clone(data) {
		return data == null ? data : JSON.parse(JSON.stringify(data));
	}

	function getBaseHref() {
		return document.baseURI || window.location.href;
	}

	function normalize(url) {
		var value = String(url || "").replace(/\\/g, "/");
		try {
			return decodeURIComponent(new URL(value, getBaseHref()).pathname).replace(/\\/g, "/");
		} catch (e) {
			return value.replace(/[?#].*$/, "");
		}
	}

	function trimLeadingSlash(path) {
		return String(path || "").replace(/^\/+/, "");
	}

	function matchCollection(url, collection, cloneValue) {
		var pathname = trimLeadingSlash(normalize(url));
		var keys = Object.keys(collection);
		var fallbackKey = null;

		for (var i = 0; i < keys.length; i++) {
			var key = trimLeadingSlash(keys[i]);
			if (pathname === key) {
				return cloneValue ? clone(collection[key]) : collection[key];
			}
			if (pathname.slice(-key.length) === key) {
				if (fallbackKey === null || key.length > fallbackKey.length) {
					fallbackKey = keys[i];
				}
			}
		}

		if (fallbackKey !== null) {
			return cloneValue ? clone(collection[fallbackKey]) : collection[fallbackKey];
		}

		return undefined;
	}

	function matchRoute(url) {
		var pathname = normalize(url);
		var matchedRoute = matchCollection(url, routes, true);
		if (matchedRoute !== undefined) {
			return matchedRoute;
		}

		if (/\/system\/user\/save$/.test(pathname)) {
			return clone(mock.save || { success: true, msg: "保存成功" });
		}
		if (/\/batchRemove\//.test(pathname)) {
			return clone(mock.batchRemove || { success: true, msg: "批量删除成功" });
		}
		if (/\/remove\//.test(pathname)) {
			return clone(mock.remove || { success: true, msg: "删除成功" });
		}

		return undefined;
	}

	function matchText(url, collection) {
		return matchCollection(url, collection, false);
	}

	function absoluteUrl(url) {
		return new URL(url, getBaseHref()).href;
	}

	function escapeHtml(html) {
		return String(html || "")
			.replace(/&/g, "&amp;")
			.replace(/</g, "&lt;")
			.replace(/>/g, "&gt;");
	}

	function replaceDocumentCodeBlocks(html) {
		return html.replace(
			/<pre class="layui-code"[^>]*>\s*<textarea[^>]*class="layui-code-source"[^>]*>([\s\S]*?)<\/textarea>\s*<\/pre>/gi,
			function (_, code) {
				return '<pre class="pear-static-code-block"><code>' + escapeHtml(code) + '</code></pre>';
			}
		);
	}

	function buildSrcdoc(url) {
		if (window.location.protocol === "file:") {
			return null;
		}
		var html = matchText(url, pages);
		if (html === undefined) {
			return null;
		}
		if (/\/view\/document\//.test(normalize(url))) {
			html = replaceDocumentCodeBlocks(html);
		}
		var href = absoluteUrl(url);
		var baseHref = href.slice(0, href.lastIndexOf("/") + 1);
		if (/<head[^>]*>/i.test(html)) {
			return html.replace(/<head([^>]*)>/i, '<head$1><base href="' + baseHref + '">');
		}
		return '<base href="' + baseHref + '">' + html;
	}

	function applyFrame(iframeEl, url) {
		if (!iframeEl) {
			return;
		}
		var srcdoc = buildSrcdoc(url);
		if (srcdoc !== null) {
			iframeEl.setAttribute("data-frame-src", url);
			iframeEl.setAttribute("src", "about:blank");
			iframeEl.setAttribute("srcdoc", srcdoc);
			return;
		}
		iframeEl.removeAttribute("srcdoc");
		iframeEl.setAttribute("data-frame-src", url);
		iframeEl.setAttribute("src", url);
	}

	function resolveFilePayload(url) {
		var jsonData = matchRoute(url);
		if (jsonData !== undefined) {
			return {
				contentType: "application/json",
				body: JSON.stringify(jsonData)
			};
		}

		var html = matchText(url, pages);
		if (html !== undefined) {
			return {
				contentType: "text/html",
				body: html
			};
		}

		var scriptText = matchText(url, scripts);
		if (scriptText !== undefined) {
			return {
				contentType: "application/javascript",
				body: scriptText
			};
		}

		return undefined;
	}

		$.ajax = function (options) {
			if (!options || !options.url) {
				return originalAjax.apply(this, arguments);
			}

		var opt = typeof options === "string" ? { url: options } : options;
		var payload = resolveFilePayload(opt.url);

		if (payload === undefined) {
			return originalAjax.apply(this, arguments);
		}

		var responseText = payload.body;
		var responseData = responseText;
		var dataType = String(opt.dataType || "").toLowerCase();
		var isScript = dataType === "script" || payload.contentType === "application/javascript";
		var isJson = dataType === "json" || payload.contentType === "application/json";

		if (isJson) {
			responseData = JSON.parse(responseText);
		}

			var deferred = $.Deferred();
			var cancelled = false;
			var jqXHR = deferred.promise({
				readyState: 4,
				status: 200,
			statusText: "success",
			responseJSON: isJson ? responseData : undefined,
			responseText: responseText,
			setRequestHeader: function () {},
			getAllResponseHeaders: function () { return ""; },
			getResponseHeader: function () { return null; },
				overrideMimeType: function () {},
				abort: function () {
					cancelled = true;
					if (typeof opt.complete === "function") {
						opt.complete(jqXHR, "abort");
					}
					deferred.rejectWith(opt.context || jqXHR, [jqXHR, "abort"]);
				}
			});

			if (typeof opt.beforeSend === "function") {
				var beforeSendResult = opt.beforeSend.call(opt.context || jqXHR, jqXHR, opt);
				if (beforeSendResult === false) {
					cancelled = true;
					if (typeof opt.complete === "function") {
						opt.complete(jqXHR, "abort");
					}
					deferred.rejectWith(opt.context || jqXHR, [jqXHR, "abort"]);
					return jqXHR;
				}
			}

			function resolve() {
				if (cancelled) {
					return;
				}
				if (isScript) {
					$.globalEval(responseText);
				}
			if (typeof opt.success === "function") {
				opt.success(responseData, "success", jqXHR);
			}
			if (typeof opt.complete === "function") {
				opt.complete(jqXHR, "success");
			}
			deferred.resolveWith(opt.context || jqXHR, [responseData, "success", jqXHR]);
		}

		if (opt.async === false || $.ajaxSettings.async === false) {
			resolve();
		} else {
			window.setTimeout(resolve, 0);
		}

		return jqXHR;
	};

	$.pearStaticAdapterInstalled = true;
	window.PEAR_IS_STATIC = true;
	window.PEAR_STATIC_HELPER = window.PEAR_STATIC_HELPER || {};
	window.PEAR_STATIC_HELPER.applyFrame = applyFrame;
	window.PEAR_STATIC_HELPER.buildSrcdoc = buildSrcdoc;

	if (!window.PEAR_STATIC_XHR_INSTALLED) {
		function StaticXMLHttpRequest() {
			this.headers = {};
			this.readyState = 0;
			this.status = 0;
			this.statusText = "";
			this.responseText = "";
			this.response = null;
			this.async = true;
			this.method = "GET";
			this.url = "";
			this.onreadystatechange = null;
			this.onload = null;
			this.onerror = null;
			this._delegate = null;
		}

		StaticXMLHttpRequest.prototype.open = function (method, url, async, username, password) {
			this.method = method || "GET";
			this.url = url;
			this.async = async !== false;
			this.readyState = 1;
			var payload = resolveFilePayload(url);
			if (payload === undefined) {
				this._delegate = new OriginalXHR();
				this._delegate.onreadystatechange = this.onreadystatechange;
				this._delegate.onload = this.onload;
				this._delegate.onerror = this.onerror;
				this._delegate.open(method, url, async, username, password);
			}
		};

		StaticXMLHttpRequest.prototype.setRequestHeader = function (key, value) {
			this.headers[key] = value;
			if (this._delegate) {
				this._delegate.setRequestHeader(key, value);
			}
		};

		StaticXMLHttpRequest.prototype.getResponseHeader = function (name) {
			if (this._delegate) {
				return this._delegate.getResponseHeader(name);
			}
			if (!this._contentType) {
				return null;
			}
			return String(name || "").toLowerCase() === "content-type" ? this._contentType : null;
		};

		StaticXMLHttpRequest.prototype.getAllResponseHeaders = function () {
			if (this._delegate) {
				return this._delegate.getAllResponseHeaders();
			}
			return this._contentType ? "Content-Type: " + this._contentType : "";
		};

		StaticXMLHttpRequest.prototype.overrideMimeType = function (mimeType) {
			if (this._delegate && this._delegate.overrideMimeType) {
				this._delegate.overrideMimeType(mimeType);
			}
		};

		StaticXMLHttpRequest.prototype.abort = function () {
			if (this._delegate) {
				this._delegate.abort();
				return;
			}
			this.status = 0;
			this.statusText = "abort";
		};

		StaticXMLHttpRequest.prototype.send = function (data) {
			var payload = resolveFilePayload(this.url);
			if (payload === undefined) {
				if (this._delegate) {
					this._delegate.send(data);
				}
				return;
			}

			var self = this;
			function complete() {
				self.readyState = 4;
				self.status = 200;
				self.statusText = "OK";
				self._contentType = payload.contentType;
				self.responseText = payload.body;
				self.response = payload.body;
				if (typeof self.onreadystatechange === "function") {
					self.onreadystatechange();
				}
				if (typeof self.onload === "function") {
					self.onload();
				}
			}

			if (this.async) {
				window.setTimeout(complete, 0);
			} else {
				complete();
			}
		};

		window.XMLHttpRequest = StaticXMLHttpRequest;
		window.PEAR_STATIC_XHR_INSTALLED = true;
	}
}

layui.config({
	base: rootPath + "module/",
	version: "3.40.1-static-20260503"
}).extend({
	admin: "admin", 	         // 框架布局组件
	common: "common",            // 公共方法封装
	menu: "menu",		         // 数据菜单组件
	frame: "frame", 	         // 内容页面组件
	peartab: "tab",		         // 多选项卡组件
	echarts: "echarts",          // 数据图表组件
	echartsTheme: "echartsTheme",// 数据图表主题
	encrypt: "encrypt",		     // 数据加密组件
	select: "select",	         // 下拉多选组件
	drawer: "drawer",	         // 抽屉弹层组件
	notice: "notice",	         // 消息提示组件
	step:"step",		         // 分布表单组件
	tag:"tag",			         // 多标签页组件
	popup:"popup",               // 弹层封装
	treetable:"treetable",       // 树状表格
	dtree:"dtree",			     // 树结构
	tinymce:"tinymce/tinymce",   // 编辑器
	area:"area",			     // 省市级联  
	count:"count",			     // 数字滚动
	topBar: "topBar",		     // 置顶组件
	button: "button",		     // 加载按钮
	design: "design",		     // 表单设计
	card: "card",			     // 数据卡片组件
	loading: "loading",		     // 加载组件
	cropper:"cropper",		     // 裁剪组件
	convert:"convert",		     // 数据转换
	yaml:"yaml",			     // yaml 解析组件
	context: "context",		     // 上下文组件
	http: "http",			     // 网络请求组件
	theme: "theme",			     // 主题转换
	message: "message",          // 通知组件
	toast: "toast",              // 消息通知
	iconPicker: "iconPicker",    // 图标选择
	nprogress: "nprogress",      // 进度过渡
	watermark:"watermark/watermark", //水印组件
	fullscreen:"fullscreen",     //全屏组件
	popover:"popover/popover"    //汽泡组件
});

installPearStaticAdapter(layui.jquery || layui.$ || window.jQuery);

layui.use(['jquery', 'layer', 'theme'], function () {
	installPearStaticAdapter(layui.jquery);
	layui.theme.changeTheme(window, false);
});
