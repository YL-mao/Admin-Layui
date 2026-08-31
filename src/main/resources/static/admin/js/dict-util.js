(function (window) {
    'use strict';

    var cache = {};
    var pendingRequests = {};
    var jqRef = null;

    // 注入 layui.$；字典 API 在脚本加载后即可用，避免与页面 layui.use 竞态。
    window.initDictUtil = function ($) {
        jqRef = $;
    };

    function get$() {
        return jqRef || window.$ || (typeof layui !== 'undefined' && layui.$);
    }

    function isDictAjaxSuccess(result) {
        if (typeof window.isAjaxSuccess === 'function') {
            return window.isAjaxSuccess(result);
        }
        return !!(result && result.code === 200);
    }

    // 优先走 common.getJson，未加载 common 时仍带 Ajax 标识头。
    function requestDictJson(url, data) {
        if (typeof window.getJson === 'function') {
            return window.getJson(url, data, {showMsg: false});
        }
        return get$().ajax({
            url: url,
            type: 'GET',
            data: data,
            dataType: 'json',
            headers: {'X-Requested-With': 'XMLHttpRequest'}
        });
    }

    window.DictTypeCode = {
        SYS_USER_SEX: 'sys_user_sex',
        SYS_POST_TYPE: 'sys_post_type',
        SYS_NOTICE_TYPE: 'sys_notice_type',
        SYS_NOTICE_RECEIVER_TYPE: 'sys_notice_receiver_type'
    };

    function normalizeCodes(codes) {
        if (!codes) {
            return [];
        }
        return (Array.isArray(codes) ? codes : [codes]).filter(function (code) {
            return !!code;
        });
    }

    function pickCodes(codes) {
        var result = {};
        normalizeCodes(codes).forEach(function (code) {
            result[code] = cache[code] || [];
        });
        return result;
    }

    function fetchBatch(codes) {
        var $ = get$();
        var missing = codes.filter(function (code) {
            return !cache[code];
        });
        if (!missing.length) {
            return $.Deferred().resolve(pickCodes(codes)).promise();
        }
        var key = missing.slice().sort().join(',');
        if (!pendingRequests[key]) {
            // 从后台批量加载字典选项，供表单下拉/单选渲染。
            pendingRequests[key] = requestDictJson(apiPath + '/dictData/optionsBatch', {
                dictTypeCodes: missing.join(',')
            }).done(function (result) {
                if (isDictAjaxSuccess(result) && result.data) {
                    Object.keys(result.data).forEach(function (code) {
                        cache[code] = result.data[code] || [];
                    });
                }
            }).always(function () {
                delete pendingRequests[key];
            });
        }
        return pendingRequests[key].then(function () {
            return pickCodes(codes);
        });
    }

    window.loadDict = function (codes) {
        return fetchBatch(normalizeCodes(codes));
    };

    window.getDictOptions = function (dictTypeCode) {
        return cache[dictTypeCode] || [];
    };

    window.dictLabel = function (dictTypeCode, value, defaultLabel) {
        var options = getDictOptions(dictTypeCode);
        var valueText = value == null ? '' : String(value);
        for (var i = 0; i < options.length; i++) {
            if (String(options[i].dictDataValue) === valueText) {
                return options[i].dictDataLabel;
            }
        }
        return defaultLabel || '';
    };

    window.buildDictLabelMap = function (dictTypeCode) {
        var map = {};
        getDictOptions(dictTypeCode).forEach(function (item) {
            map[item.dictDataValue] = item.dictDataLabel;
        });
        return map;
    };

    window.renderDictSelect = function ($select, options, config) {
        config = config || {};
        var html = '';
        if (config.emptyOption !== false) {
            html += '<option value="">' + (config.emptyLabel || '请选择') + '</option>';
        }
        (options || []).forEach(function (item) {
            html += '<option value="' + item.dictDataValue + '">' + item.dictDataLabel + '</option>';
        });
        $select.html(html);
        if (config.selectedValue != null && config.selectedValue !== '') {
            $select.val(String(config.selectedValue));
        } else if (config.defaultValue != null && config.defaultValue !== '') {
            $select.val(String(config.defaultValue));
        } else {
            var defaultItem = (options || []).find(function (item) {
                return item.isDefault === '1';
            });
            if (defaultItem) {
                $select.val(defaultItem.dictDataValue);
            }
        }
    };

    window.renderDictRadio = function ($container, name, options, config) {
        config = config || {};
        var html = '';
        var selectedValue = config.selectedValue != null && config.selectedValue !== ''
            ? String(config.selectedValue)
            : (config.defaultValue != null && config.defaultValue !== '' ? String(config.defaultValue) : '');
        if (!selectedValue) {
            var defaultItem = (options || []).find(function (item) {
                return item.isDefault === '1';
            });
            if (defaultItem) {
                selectedValue = defaultItem.dictDataValue;
            }
        }
        (options || []).forEach(function (item, index) {
            var checked = selectedValue
                ? String(item.dictDataValue) === selectedValue
                : index === 0;
            html += '<input type="radio" name="' + name + '" value="' + item.dictDataValue + '" title="'
                + item.dictDataLabel + '"' + (config.filter ? ' lay-filter="' + config.filter + '"' : '')
                + (checked ? ' checked' : '') + '>';
        });
        $container.html(html);
    };

    window.fillDictQuerySelect = function ($select, dictTypeCode, config) {
        config = config || {};
        renderDictSelect($select, getDictOptions(dictTypeCode), {
            emptyOption: true,
            emptyLabel: config.emptyLabel || '全部',
            selectedValue: config.selectedValue
        });
    };

})(window);
