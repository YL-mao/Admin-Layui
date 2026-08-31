layui.define(['jquery', 'element'], function (exports) {
	"use strict";

	var MOD_NAME = 'message',
		$ = layui.jquery,
		element = layui.element;

	var message = function (opt) {
		this.option = opt;
		this.clickCallback = null;
		this._noticeObserver = null;
	};

	message.prototype.render = function (opt) {
		var option = {
			elem: opt.elem,
			url: opt.url ? opt.url : false,
			height: opt.height,
			data: opt.data
		};
		var instance = new message(option);
		if (option.url !== false) {
			instance.paint();
		}
		return instance;
	}

	message.prototype.paint = function () {
		var option = this.option;
		if (!option || option.url === false) {
			return;
		}
		option.data = getData(option.url);
		$(option.elem).html(createHtml(option));
		bindNoticeLayout(option, this);
		if (this.clickCallback) {
			this.click(this.clickCallback);
		}
	}

	message.prototype.click = function (callback) {
		this.clickCallback = callback;
		var elem = this.option.elem;
		$(elem).off('click.pearNoticeItem').on('click.pearNoticeItem', '.pear-notice-item', function (event) {
			event.preventDefault();
			var $item = $(this);
			callback(
				$item.attr("notice-id"),
				$item.attr("notice-title"),
				$item.attr("notice-context"),
				$item.attr("notice-form")
			);
		});
	}

	/** 重新拉取 header 数据并重绘铃铛区域。 */
	message.prototype.reload = function () {
		this.paint();
		return this;
	}

	/** 顶部铃铛接口统一按 R 契约取 data 数组。 */
	function unwrapMessageList(result) {
		if (result && result.code === 200 && Array.isArray(result.data)) {
			return result.data;
		}
		return Array.isArray(result) ? result : [];
	}

	/** 同步请求获取数据（带 Ajax 标识头，Session 过期时走 JSON 401）。 */
	function getData(url) {
		var data = [];
		$.ajax({
			url: url,
			type: 'GET',
			dataType: 'json',
			async: false,
			headers: {'X-Requested-With': 'XMLHttpRequest'},
			success: function (result) {
				data = unwrapMessageList(result);
			}
		});
		return data;
	}

	function bindNoticeLayout(option, instance) {
		var targetNode = document.querySelector(option.elem + ' .pear-notice');
		if (instance._noticeObserver) {
			instance._noticeObserver.disconnect();
			instance._noticeObserver = null;
		}
		if (targetNode) {
			var mutationObserver = new MutationObserver(function () {
				if (getComputedStyle(targetNode).display !== 'none') {
					var rect = targetNode.getBoundingClientRect();
					if (rect.right > $(window).width()) {
						var elemRight = document.querySelector(option.elem).getBoundingClientRect().right;
						var offsetRight = 20;
						targetNode.style.right = elemRight - $(window).width() + offsetRight + 'px';
						targetNode.style.left = 'unset';
					}
				}
			});
			mutationObserver.observe(targetNode, {
				attributes: true,
				childList: false,
				subtree: false,
				attributeOldValue: false,
				attributeFilter: ['class']
			});
			instance._noticeObserver = mutationObserver;
		}
		setTimeout(function () {
			element.init();
			$(option.elem + " .layui-tab-title li").off('click.pearNoticeTab').on('click.pearNoticeTab', function () {
				$(this).siblings().removeClass('layui-this');
				$(this).addClass('layui-this');
			});
		}, 300);
	}

	/** 拼 HTML 前转义，避免公告标题/摘要以脚本形式执行。 */
	function escapeHtml(value) {
		if (value == null || value === '') {
			return '';
		}
		return String(value)
			.replace(/&/g, '&amp;')
			.replace(/</g, '&lt;')
			.replace(/>/g, '&gt;')
			.replace(/"/g, '&quot;')
			.replace(/'/g, '&#39;');
	}

	function createHtml(option) {

		var count = 0;
		var noticeTitle = '<ul class="layui-tab-title">';
		var noticeContent = '<div class="layui-tab-content" style="height:' + option.height + ';overflow-x: hidden;padding:0px;">';


		// 根据 data 便利数据
		$.each(option.data, function (i, item) {
			var tabTitle = escapeHtml(item.title);

			if (i === 0) {
				noticeTitle += '<li class="layui-this">' + tabTitle + '</li>';
				noticeContent += '<div class="layui-tab-item layui-show">';
			} else {
				noticeTitle += '<li>' + tabTitle + '</li>';
				noticeContent += '<div class="layui-tab-item">';
			}

			$.each(item.children, function (i, note) {
				count++;
				var noteTitle = escapeHtml(note.title);
				var noteContext = escapeHtml(note.context);
				var noteForm = escapeHtml(note.form);
				var noteId = escapeHtml(note.id);
				var noteTime = escapeHtml(note.time);
				noticeContent += '<div class="pear-notice-item" notice-form="' + noteForm + '" notice-context="' + noteContext +
					'" notice-title="' + noteTitle + '" notice-id="' + noteId + '">' ;

				if (note.icon) {
					noticeContent += '<span class="pear-notice-type-icon pear-notice-icon-type-' + escapeHtml(note.noticeType || 1) + '">'
						+ '<i class="layui-icon ' + escapeHtml(note.icon) + '"></i></span>';
				} else if (note.avatar) {
					noticeContent +='<img src="' + escapeHtml(note.avatar) + '"/>';
				}

				noticeContent +='<div style="display:inline-block;">' + noteTitle + '</div>' +
					'<div class="pear-notice-end">' + noteTime + '</div>' +
					'</div>';
			})

			// 空内容
			if(item.children.length==0){
				noticeContent +='<div class="pear-empty"><div class="pear-empty-image"><svg class="pear-empty-img-default" width="184" height="152" viewBox="0 0 184 152"><g fill="none" fill-rule="evenodd"><g transform="translate(24 31.67)"><ellipse class="pear-empty-img-default-ellipse" cx="67.797" cy="106.89" rx="67.797" ry="12.668"></ellipse><path class="pear-empty-img-default-path-1" d="M122.034 69.674L98.109 40.229c-1.148-1.386-2.826-2.225-4.593-2.225h-51.44c-1.766 0-3.444.839-4.592 2.225L13.56 69.674v15.383h108.475V69.674z"></path><path class="pear-empty-img-default-path-2" d="M101.537 86.214L80.63 61.102c-1.001-1.207-2.507-1.867-4.048-1.867H31.724c-1.54 0-3.047.66-4.048 1.867L6.769 86.214v13.792h94.768V86.214z" transform="translate(13.56)"></path><path class="pear-empty-img-default-path-3" d="M33.83 0h67.933a4 4 0 0 1 4 4v93.344a4 4 0 0 1-4 4H33.83a4 4 0 0 1-4-4V4a4 4 0 0 1 4-4z"></path><path class="pear-empty-img-default-path-4" d="M42.678 9.953h50.237a2 2 0 0 1 2 2V36.91a2 2 0 0 1-2 2H42.678a2 2 0 0 1-2-2V11.953a2 2 0 0 1 2-2zM42.94 49.767h49.713a2.262 2.262 0 1 1 0 4.524H42.94a2.262 2.262 0 0 1 0-4.524zM42.94 61.53h49.713a2.262 2.262 0 1 1 0 4.525H42.94a2.262 2.262 0 0 1 0-4.525zM121.813 105.032c-.775 3.071-3.497 5.36-6.735 5.36H20.515c-3.238 0-5.96-2.29-6.734-5.36a7.309 7.309 0 0 1-.222-1.79V69.675h26.318c2.907 0 5.25 2.448 5.25 5.42v.04c0 2.971 2.37 5.37 5.277 5.37h34.785c2.907 0 5.277-2.421 5.277-5.393V75.1c0-2.972 2.343-5.426 5.25-5.426h26.318v33.569c0 .617-.077 1.216-.221 1.789z"></path></g><path class="pear-empty-img-default-path-5" d="M149.121 33.292l-6.83 2.65a1 1 0 0 1-1.317-1.23l1.937-6.207c-2.589-2.944-4.109-6.534-4.109-10.408C138.802 8.102 148.92 0 161.402 0 173.881 0 184 8.102 184 18.097c0 9.995-10.118 18.097-22.599 18.097-4.528 0-8.744-1.066-12.28-2.902z"></path><g class="pear-empty-img-default-g" transform="translate(149.65 15.383)"><ellipse cx="20.654" cy="3.167" rx="2.849" ry="2.815"></ellipse><path d="M5.698 5.63H0L2.898.704zM9.259.704h4.985V5.63H9.259z"></path></g></g></svg></div><p class="pear-empty-description">暂无数据</p></div>';
			}
			noticeContent += '</div>';
		})

		var notice;
		if (count > 0){
			notice = '<li class="layui-nav-item" lay-unselect="">' +
				'<a href="javascript:;" class="notice layui-icon layui-icon-notice"><span class="layui-badge-dot"></span></a>' +
				'<div class="layui-nav-child layui-tab pear-notice" style="margin-top: 0px;padding:0px;">';
		}else {
			notice = '<li class="layui-nav-item" lay-unselect="">' +
				'<a href="javascript:;" class="notice layui-icon layui-icon-notice"></a>' +
				'<div class="layui-nav-child layui-tab pear-notice" style="margin-top: 0px;padding:0px;">';
		}

		noticeTitle += '</ul>';
		noticeContent += '</div>';
		notice += noticeTitle;
		notice += noticeContent;
		notice += '</div></li>';
		return notice;
	}

	exports(MOD_NAME, new message());
})
