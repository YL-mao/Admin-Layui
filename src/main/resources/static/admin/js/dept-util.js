(function (window) {
    'use strict';

    var seq = 0;

    // 扁平部门列表转 Layui tree 节点
    window.buildDeptTree = function (list, parentId, depth) {
        var nodes = [];
        depth = depth || 0;
        if (!list || !list.length) {
            return nodes;
        }
        list.forEach(function (item) {
            if (String(item.parentId) === String(parentId)) {
                var children = window.buildDeptTree(list, item.deptId, depth + 1);
                var node = {
                    id: item.deptId,
                    title: item.deptName
                };
                if (depth < 1) {
                    node.spread = true;
                }
                if (children.length) {
                    node.children = children;
                }
                nodes.push(node);
            }
        });
        return nodes;
    };

    window.findDeptName = function (list, deptId) {
        if (!deptId || !list || !list.length) {
            return '';
        }
        var item = list.find(function (d) {
            return String(d.deptId) === String(deptId);
        });
        return item ? item.deptName : '';
    };

    // 初始化部门下拉树；container 为弹层或表单根节点，list 为扁平部门数据
    window.initDeptDropdownSelect = function (options) {
        options = options || {};
        layui.use(['tree', 'dropdown', 'jquery'], function () {
            var tree = layui.tree;
            var dropdown = layui.dropdown;
            var $ = layui.jquery;
            var container = options.container ? $(options.container) : $(document);
            var list = options.list || [];
            var hiddenName = options.hiddenName || 'deptId';
            var inputSelector = options.inputSelector || '.layui-dept-dropdown-input';
            var rootParentId = options.rootParentId != null ? options.rootParentId : '0';
            var value = options.value || '';
            var minWidth = options.minWidth || 280;

            if (!list.length) {
                return;
            }
            var $input = container.find(inputSelector);
            if ($input.length === 0) {
                return;
            }
            var $hidden = container.find('[name="' + hiddenName + '"]');
            var treeData = window.buildDeptTree(list, rootParentId, 0);
            var dropdownId = 'deptDropdown-' + (++seq);

            if (value) {
                $hidden.val(value);
                $input.val(window.findDeptName(list, value));
            }

            dropdown.render({
                elem: $input[0],
                id: dropdownId,
                closeOnClick: false,
                content: '<div class="layui-dept-dropdown-tree-panel" style="padding:8px;max-height:280px;overflow:auto;"></div>',
                style: 'min-width: ' + Math.max($input.outerWidth() || 0, minWidth) + 'px;',
                ready: function (elemPanel) {
                    var $panel = $(elemPanel).find('.layui-dept-dropdown-tree-panel');
                    if ($panel.attr('data-rendered') === '1') {
                        return;
                    }
                    $panel.attr('data-rendered', '1');
                    tree.render({
                        elem: $panel[0],
                        data: treeData,
                        showLine: true,
                        click: function (obj) {
                            $hidden.val(obj.data.id);
                            $input.val(obj.data.title);
                            dropdown.close(dropdownId);
                        }
                    });
                }
            });
        });
    };
})(window);
