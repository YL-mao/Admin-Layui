layui.define(['jquery', 'element'], function(exports) {
	"use strict";

	var $ = layui.jquery;

	var frame = function(opt) {
		this.option = opt;
	};

	frame.prototype.render = function(opt) {
		var option = {
			elem: opt.elem,
			url: opt.url,
			title: opt.title,
			width: opt.width,
			height: opt.height,
			done: opt.done ? opt.done : function() {
			}
		}
		renderContent(option);
		$("#" + option.elem).width(option.width);
		$("#" + option.elem).height(option.height);
		return new frame(option);
	}

	frame.prototype.changePage = function(url, loading) {
		var $frameLoad = $("#" + this.option.elem).find(".pear-frame-loading");
		var $frame = $("#" + this.option.elem + " iframe");
		if (window.PEAR_STATIC_HELPER && window.PEAR_IS_STATIC) {
			window.PEAR_STATIC_HELPER.applyFrame($frame[0], url);
		} else {
			$frame.attr("src", url);
		}
		renderContentLoading($frame, $frameLoad, loading);
	}

	frame.prototype.changePageByElement = function(elem, url, title, loading) {
		var $frameLoad = $("#" + elem).find(".pear-frame-loading");
		var $frame = $("#" + elem + " iframe");
		if (window.PEAR_STATIC_HELPER && window.PEAR_IS_STATIC) {
			window.PEAR_STATIC_HELPER.applyFrame($frame[0], url);
		} else {
			$frame.attr("src", url);
		}
		$("#" + elem + " .title").html(title);
		renderContentLoading($frame, $frameLoad, loading);
	}

	frame.prototype.refresh = function(loading) {
		var $frameLoad = $("#" + this.option.elem).find(".pear-frame-loading");
		var $frame = $("#" + this.option.elem).find("iframe");
		var url = $frame.attr("data-frame-src") || $frame.attr("src");
		if (window.PEAR_STATIC_HELPER && window.PEAR_IS_STATIC) {
			window.PEAR_STATIC_HELPER.applyFrame($frame[0], url);
		} else {
			$frame.attr("src", url);
		}
		renderContentLoading($frame, $frameLoad, loading);
	}

	function getFrameSrc(url) {
		return window.PEAR_IS_STATIC ? "about:blank" : url;
	}

	function renderContent(option) {
		var iframe = `<iframe class='pear-frame-content' data-frame-src='${option.url}' style='width:100%;height:100%;'  scrolling='auto' frameborder='0' src='${getFrameSrc(option.url)}' allowfullscreen='true' ></iframe>`;
		var loading = `<div class="pear-frame-loading">
			<div class="ball-loader">
			<span></span><span></span><span></span><span></span>
			</div>
			</div></div>`;
		$("#" + option.elem).html("<div class='pear-frame'>" + iframe + loading + "</div>");
		if (window.PEAR_STATIC_HELPER && window.PEAR_IS_STATIC) {
			window.PEAR_STATIC_HELPER.applyFrame($("#" + option.elem).find("iframe")[0], option.url);
		}
	}
	
	function renderContentLoading (iframeEl, loadingEl, isLoading) {
		if (isLoading) {
			loadingEl.css({
				display: 'block'
			});
			$(iframeEl).on('load', function() {
				loadingEl.fadeOut(1000);
			})
		}
	}

	exports('frame', new frame());
});
