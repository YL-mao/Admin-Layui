layui.define(['jquery', 'layer', 'table', 'element', 'code'], function (exports) {
    "use strict";

    var MOD_NAME = 'common',
        $ = layui.jquery,
        layer = layui.layer,
        table = layui.table,
        element = layui.element,
        code = layui.code;

    var ajaxGuardBound = false;
    var codeHighlightBound = false;

    var common = new function () {

        /** Layui 表格统一响应字段映射（后端 R.page：code=200）。 */
        this.TABLE_RESPONSE = {
            statusName: 'code',
            statusCode: 200,
            msgName: 'msg',
            countName: 'count',
            dataName: 'data'
        };

        /**
         * 取表格/树表勾选行的某字段，逗号拼接（替代原 checkField，兼容 treeTable）。
         */
        this.getCheckedFieldIds = function (obj, field) {
            var tableApi = layui.treeTable || table;
            var rows = tableApi.checkStatus(obj.config.id).data;
            if (!rows.length) {
                return '';
            }
            var ids = '';
            for (var i = 0; i < rows.length; i++) {
                ids += rows[i][field] + ',';
            }
            return ids.substring(0, ids.length - 1);
        };

        /** 当前是否为移动端 */
        this.isModile = function () {
            return $(window).width() <= 768;
        };

        /** 统一 Ajax 成功判定：全站 R 契约 code === 200。 */
        this.isAjaxSuccess = function (result) {
            return !!(result && result.code === 200);
        };

        /** 从 XHR 响应体解析后端业务错误消息。 */
        this.getAjaxErrorMsg = function (xhr, defaultMsg) {
            var result = xhr && xhr.responseJSON;
            if (!result && xhr && xhr.responseText) {
                try {
                    result = JSON.parse(xhr.responseText);
                } catch (e) {
                    result = null;
                }
            }
            return (result && result.msg) || defaultMsg;
        };

        /**
         * 底层 JSON Ajax；默认带 X-Requested-With，POST/PUT/PATCH 自动 JSON.stringify。
         */
        this.requestJson = function (options) {
            options = options || {};
            var settings = $.extend({
                type: 'GET',
                dataType: 'json',
                headers: {'X-Requested-With': 'XMLHttpRequest'}
            }, options);

            if (settings.data != null && typeof settings.data !== 'string') {
                if (settings.type === 'GET' || settings.type === 'get'
                    || settings.type === 'DELETE' || settings.type === 'delete') {
                    // GET/DELETE 保持 jQuery 默认 query 序列化（如 ids 参数）
                } else {
                    settings.contentType = settings.contentType || 'application/json';
                    settings.data = JSON.stringify(settings.data);
                }
            }

            var userError = settings.error;
            var showMsg = settings.showMsg !== false;
            settings.error = function (xhr) {
                if (showMsg) {
                    layer.msg(common.getAjaxErrorMsg(xhr, settings.defaultMsg || '请求失败'), {
                        icon: 2,
                        time: 1000
                    });
                }
                if (typeof userError === 'function') {
                    userError(xhr);
                }
            };

            return $.ajax(settings);
        };

        this.getJson = function (url, data, options) {
            options = $.extend({}, options, {url: url, type: 'GET', data: data});
            return common.requestJson(options);
        };

        this.postJson = function (url, data, options) {
            options = $.extend({}, options, {url: url, type: 'POST', data: data});
            return common.requestJson(options);
        };

        this.putJson = function (url, data, options) {
            options = $.extend({}, options, {url: url, type: 'PUT', data: data});
            return common.requestJson(options);
        };

        this.patchJson = function (url, data, options) {
            options = $.extend({}, options, {url: url, type: 'PATCH', data: data});
            return common.requestJson(options);
        };

        this.deleteJson = function (url, data, options) {
            options = $.extend({}, options, {url: url, type: 'DELETE', data: data});
            return common.requestJson(options);
        };

        /**
         * 唯一性校验：R.ok(true) 表示可用（不重复）。
         */
        this.checkUnique = function (url, params, callback) {
            common.getJson(url, params, {showMsg: false}).done(function (result) {
                callback(common.isAjaxSuccess(result) && result.data === true);
            }).fail(function () {
                layer.msg('校验失败', {icon: 2, time: 1000});
                callback(false);
            });
        };

        /** 全局 Ajax 401/403/404 处理；apiPath 由 include.html 注入 window.apiPath。 */
        this.initAjaxGuard = function () {
            if (ajaxGuardBound) {
                return;
            }
            ajaxGuardBound = true;
            var redirectingToLogin = false;
            $(document).ajaxComplete(function (event, xhr, settings) {
                var result = xhr.responseJSON;
                if (!result && xhr.responseText) {
                    try {
                        result = JSON.parse(xhr.responseText);
                    } catch (e) {
                        result = null;
                    }
                }
                var responseCode = result && result.code;
                var status = xhr.status || responseCode;
                if ((status === 401 || responseCode === 401) && !redirectingToLogin) {
                    redirectingToLogin = true;
                    top.location.href = (window.apiPath || '') + '/login';
                    return;
                }
                if (settings && settings.error) {
                    return;
                }
                if (status === 403 || responseCode === 403) {
                    layer.msg((result && result.msg) || '无权限访问', {icon: 2, time: 1000});
                } else if (status === 404 || responseCode === 404) {
                    layer.msg((result && result.msg) || '资源不存在', {icon: 2, time: 1000});
                }
            });
        };

        /** 渲染页面内 Layui 代码块，并在折叠面板展开时补渲染。 */
        this.initCodeHighlight = function () {
            if (codeHighlightBound) {
                return;
            }
            codeHighlightBound = true;
            function renderCode(elem) {
                if (typeof code !== 'function') {
                    return;
                }
                var nodes = elem ? $(elem) : $('.layui-code');
                nodes.filter(':not([lay-code-index])').each(function () {
                    code({elem: this});
                });
            }

            renderCode();
            element.on('collapse', function (data) {
                renderCode(data.content.find('.layui-code'));
            });
        };

        /** footer 入口：挂载依赖并初始化整站 Ajax 守卫与代码高亮。 */
        this.initPage = function () {
            if (typeof window.initDictUtil === 'function' && layui.$) {
                window.initDictUtil(layui.$);
            }
            common.initAjaxGuard();
            common.initCodeHighlight();
        };
    };

    // common 实例化后再挂全局，避免构造函数内 common 尚未赋值。
    if (typeof window !== 'undefined') {
        window.TABLE_RESPONSE = common.TABLE_RESPONSE;
        window.isAjaxSuccess = common.isAjaxSuccess;
        window.getAjaxErrorMsg = common.getAjaxErrorMsg;
        window.requestJson = common.requestJson;
        window.getJson = common.getJson;
        window.postJson = common.postJson;
        window.putJson = common.putJson;
        window.patchJson = common.patchJson;
        window.deleteJson = common.deleteJson;
        window.checkUnique = common.checkUnique;
        window.getCheckedFieldIds = common.getCheckedFieldIds;
        if (layui.$) {
            window.$ = layui.$;
            window.jQuery = layui.$;
            if (typeof window.initDictUtil === 'function') {
                window.initDictUtil(layui.$);
            }
        }
    }

    exports(MOD_NAME, common);
});
