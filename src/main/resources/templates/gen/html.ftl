<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" xmlns:sa="https://www.thymeleaf.org/extras/sa-token">
<head th:replace="~{include :: header('${functionName}管理')}"></head>
<body class="pear-container">
<div class="layui-card">
    <div class="layui-card-body">
        <form class="layui-form" action="">
            <div class="layui-form-item">
<#list listQueryColumns as col>
                <div class="layui-form-item layui-inline">
                    <label class="layui-form-label">${col.columnComment!col.fieldName}</label>
                    <div class="layui-input-inline">
                        <input type="text" name="${col.fieldName}" maxlength="${col.columnSize?c}" placeholder="请输入${col.columnComment!col.fieldName}" class="layui-input">
                    </div>
                </div>
</#list>
                <div class="layui-form-item layui-inline">
                    <button class="pear-btn pear-btn-md pear-btn-primary" lay-submit lay-filter="${moduleName}-query">
                        <i class="layui-icon layui-icon-search"></i>
                        查询
                    </button>
                    <button type="reset" class="pear-btn pear-btn-md">
                        <i class="layui-icon layui-icon-refresh"></i>
                        重置
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>
<div class="layui-card">
    <div class="layui-card-body">
        <table id="${moduleName}-table" lay-filter="${moduleName}-table"></table>
    </div>
</div>

<script type="text/html" id="${moduleName}-toolbar">
    <button sa:hasPermission="${permPrefix}:insert" class="pear-btn pear-btn-primary pear-btn-md" lay-event="add">
        <i class="layui-icon layui-icon-add-1"></i>
        新增
    </button>
    <button sa:hasPermission="${permPrefix}:delete" class="pear-btn pear-btn-danger pear-btn-md" lay-event="batchRemove">
        <i class="layui-icon layui-icon-delete"></i>
        删除
    </button>
</script>

<script type="text/html" id="${moduleName}-bar">
    <button sa:hasPermission="${permPrefix}:update" class="pear-btn pear-btn-primary pear-btn-sm" lay-event="edit"><i class="layui-icon layui-icon-edit"></i></button>
    <button sa:hasPermission="${permPrefix}:delete" class="pear-btn pear-btn-danger pear-btn-sm" lay-event="remove"><i class="layui-icon layui-icon-delete"></i></button>
</script>

<#if hasIsEnabled>
<script type="text/html" id="${moduleName}-status" th:inline="none">
{{# if(d.isEnabled == 1){ }}
    <input sa:hasPermission="${permPrefix}:updateEnabled" type="checkbox" name="isEnabled" value="{{d.${pkFieldName}}}" lay-skin="switch" lay-text="启用|停用" lay-filter="${moduleName}-status" checked />
{{# } else { }}
    <input sa:hasPermission="${permPrefix}:updateEnabled" type="checkbox" name="isEnabled" value="{{d.${pkFieldName}}}" lay-skin="switch" lay-text="启用|停用" lay-filter="${moduleName}-status" />
{{# } }}
</script>
</#if>

<script type="text/html" id="${moduleName}-form">
    <form class="layui-form" lay-filter="${moduleName}-form" action="">
        <div class="mainBox">
            <div class="main-container">
        <input type="hidden" name="${pkFieldName}">
<#list formColumns as col>
        <div class="layui-form-item">
            <label class="layui-form-label">${col.columnComment!col.fieldName}</label>
            <div class="layui-input-block">
    <#if col.enabledField>
                <input type="radio" name="isEnabled" value="1" title="启用">
                <input type="radio" name="isEnabled" value="0" title="停用" checked>
    <#elseif col.javaType == "String">
                <input type="text" name="${col.fieldName}"<#if !col.nullable || col.nameField || col.codeField> lay-verify="required<#if col.codeField>|codeFormat</#if>"</#if> maxlength="${col.columnSize?c}" placeholder="请输入${col.columnComment!col.fieldName}" autocomplete="off"
                       class="layui-input">
    <#elseif col.orderNumField>
                <input type="number" name="${col.fieldName}" lay-verify="required|number" placeholder="请输入排序号"
                       autocomplete="off" class="layui-input" value="1">
    <#else>
                <input type="text" name="${col.fieldName}"<#if !col.nullable> lay-verify="required"</#if> placeholder="请输入${col.columnComment!col.fieldName}" autocomplete="off"
                       class="layui-input">
    </#if>
            </div>
        </div>
</#list>
            </div>
        </div>
        <div class="bottom">
            <div class="button-container">
                <button sa:hasPermissionOr="${permPrefix}:insert,${permPrefix}:update" type="submit" class="pear-btn pear-btn-primary pear-btn-sm" lay-submit lay-filter="${moduleName}-save">
                    <i class="layui-icon layui-icon-ok"></i>
                    提交
                </button>
                <button type="reset" class="pear-btn pear-btn-sm">
                    <i class="layui-icon layui-icon-refresh"></i>
                    重置
                </button>
            </div>
        </div>
    </form>
</script>

<th:block th:insert="~{include :: footer}"/>
<script>
    layui.use(['common', 'table', 'form', 'jquery'], function () {
        let table = layui.table;
        let form = layui.form;
        let $ = layui.jquery;
        let MODULE_PATH = apiPath + "/${moduleName}";
        let formMode = 'add';

<#if hasCodeField>
        form.verify({
            codeFormat: function (value) {
                if (value && !/^[A-Za-z0-9_-]+$/.test(value)) {
                    return '编码只能包含字母、数字、下划线和中划线';
                }
            }
        });
</#if>

        let cols = [
            [
                {type: 'checkbox'},
<#list listDisplayColumns as col>
    <#if col.enabledField && hasIsEnabled>
                {title: '${col.columnComment!col.fieldName}', field: '${col.fieldName}', align: 'center', templet: '#${moduleName}-status'},
    <#else>
                {title: '${col.columnComment!col.fieldName}', field: '${col.fieldName}', align: 'center'},
    </#if>
</#list>
                {title: '操作', toolbar: '#${moduleName}-bar', align: 'center'}
            ]
        ];

        // 从后台分页加载${functionName}列表。
        table.render({
            response: TABLE_RESPONSE,
            elem: '#${moduleName}-table',
            url: MODULE_PATH + '/list',
            page: true,
            cols: cols,
            skin: 'line',
            toolbar: '#${moduleName}-toolbar',
            defaultToolbar: [{
                title: '刷新',
                layEvent: 'refresh',
                icon: 'layui-icon-refresh',
            }, 'filter', 'print', 'exports']
        });

        table.on('tool(${moduleName}-table)', function (obj) {
            if (obj.event === 'remove') {
                window.remove(obj);
            } else if (obj.event === 'edit') {
                window.edit(obj);
            }
        });

        table.on('toolbar(${moduleName}-table)', function (obj) {
            if (obj.event === 'add') {
                window.add();
            } else if (obj.event === 'refresh') {
                window.refresh();
            } else if (obj.event === 'batchRemove') {
                window.batchRemove(obj);
            }
        });

        form.on('submit(${moduleName}-query)', function (data) {
            table.reload('${moduleName}-table', {where: data.field});
            return false;
        });

<#if hasIsEnabled>
        form.on('switch(${moduleName}-status)', function (obj) {
            let isEnabled = obj.elem.checked ? 1 : 0;
            // 调用后台切换${functionName}启停状态。
            patchJson(MODULE_PATH + '/updateEnabled', {
                ${pkFieldName}: this.value,
                isEnabled: isEnabled
            }, {
                defaultMsg: '状态更新失败',
                success: function (result) {
                    if (isAjaxSuccess(result)) {
                        layer.msg(result.msg, {icon: 1, time: 1000});
                    } else {
                        obj.elem.checked = !obj.elem.checked;
                        form.render('checkbox');
                        layer.msg(result.msg || '状态更新失败', {icon: 2, time: 1000});
                    }
                },
                error: function () {
                    obj.elem.checked = !obj.elem.checked;
                    form.render('checkbox');
                }
            });
        });
</#if>

<#if hasCodeField>
        function checkCodeUnique(code, callback) {
            checkUnique(MODULE_PATH + '/checkCode', {${codeColumn.fieldName}: code}, callback);
        }
</#if>
<#if hasNameField>
        function checkNameUnique(name, callback) {
            checkUnique(MODULE_PATH + '/checkName', {${nameColumn.fieldName}: name}, callback);
        }
</#if>

        function saveRow(data) {
            let url = formMode === 'add' ? MODULE_PATH + '/add' : MODULE_PATH + '/update';
            let loading = layer.load();
            (formMode === 'add' ? postJson : putJson)(url, data.field, {
                defaultMsg: '操作失败',
                success: function (result) {
                    layer.close(loading);
                    if (isAjaxSuccess(result)) {
                        layer.msg(result.msg, {icon: 1, time: 1000}, function () {
                            layer.closeAll('page');
                            table.reload('${moduleName}-table');
                        });
                    } else {
                        layer.msg(result.msg || '操作失败', {icon: 2, time: 1000});
                    }
                },
                error: function () {
                    layer.close(loading);
                }
            });
        }

        form.on('submit(${moduleName}-save)', function (data) {
<#if hasCodeField || hasNameField>
            function doSave() {
                saveRow(data);
            }
</#if>
<#if hasCodeField && hasNameField>
            let originalCode = $(data.form).find('[name="${codeColumn.fieldName}"]').attr('data-original-code');
            let originalName = $(data.form).find('[name="${nameColumn.fieldName}"]').attr('data-original-name');
            let codeUnchanged = data.field.${pkFieldName} && originalCode === data.field.${codeColumn.fieldName};
            let nameUnchanged = data.field.${pkFieldName} && originalName === data.field.${nameColumn.fieldName};

            function checkNameThenSave() {
                if (nameUnchanged) {
                    doSave();
                    return;
                }
                checkNameUnique(data.field.${nameColumn.fieldName}, function (unique) {
                    if (unique) {
                        doSave();
                    } else {
                        layer.msg('${functionName}名称已存在', {icon: 2, time: 1000});
                    }
                });
            }

            if (codeUnchanged) {
                checkNameThenSave();
                return false;
            }
            checkCodeUnique(data.field.${codeColumn.fieldName}, function (unique) {
                if (unique) {
                    checkNameThenSave();
                } else {
                    layer.msg('${functionName}编码已存在', {icon: 2, time: 1000});
                }
            });
            return false;
<#elseif hasCodeField>
            let originalCode = $(data.form).find('[name="${codeColumn.fieldName}"]').attr('data-original-code');
            let codeUnchanged = data.field.${pkFieldName} && originalCode === data.field.${codeColumn.fieldName};
            if (codeUnchanged) {
                doSave();
                return false;
            }
            checkCodeUnique(data.field.${codeColumn.fieldName}, function (unique) {
                if (unique) {
                    doSave();
                } else {
                    layer.msg('${functionName}编码已存在', {icon: 2, time: 1000});
                }
            });
            return false;
<#elseif hasNameField>
            let originalName = $(data.form).find('[name="${nameColumn.fieldName}"]').attr('data-original-name');
            let nameUnchanged = data.field.${pkFieldName} && originalName === data.field.${nameColumn.fieldName};
            if (nameUnchanged) {
                doSave();
                return false;
            }
            checkNameUnique(data.field.${nameColumn.fieldName}, function (unique) {
                if (unique) {
                    doSave();
                } else {
                    layer.msg('${functionName}名称已存在', {icon: 2, time: 1000});
                }
            });
            return false;
<#else>
            saveRow(data);
            return false;
</#if>
        });

        function openForm(mode, row) {
            formMode = mode;
            layer.open({
                type: 1,
                title: mode === 'add' ? '新增${functionName}' : '修改${functionName}',
                offset: mode === 'add' ? 'b' : 'r',
                anim: mode === 'add' ? 'slideUp' : 'slideLeft',
                shade: 0.1,
                shadeClose: true,
                id: mode === 'add' ? '${moduleName}-add-layer' : '${moduleName}-edit-layer',
                area: mode === 'add' ? ['800px', '70%'] : ['520px', '70%'],
                content: $('#${moduleName}-form').html(),
                success: function (layero) {
                    form.render();
                    if (mode === 'edit' && row) {
<#if hasCodeField>
                        layero.find('[name="${codeColumn.fieldName}"]').attr('data-original-code', row.${codeColumn.fieldName});
</#if>
<#if hasNameField>
                        layero.find('[name="${nameColumn.fieldName}"]').attr('data-original-name', row.${nameColumn.fieldName});
</#if>
                        form.val('${moduleName}-form', row);
                    } else {
                        form.val('${moduleName}-form', {
                            ${pkFieldName}: '',
<#list formColumns as col>
    <#if col.enabledField>
                            isEnabled: '0',
    <#elseif col.orderNumField>
                            ${col.fieldName}: 1,
    <#else>
                            ${col.fieldName}: '',
    </#if>
</#list>
                        });
                    }
                    form.render();
                }
            });
        }

        window.add = function () {
            openForm('add', null);
        };

        window.edit = function (obj) {
            openForm('edit', obj.data);
        };

        window.remove = function (obj) {
            layer.confirm('确定要删除该${functionName}吗？', {icon: 3, title: '提示'}, function (index) {
                layer.close(index);
                let loading = layer.load();
                deleteJson(MODULE_PATH + '/delete', {ids: obj.data.${pkFieldName}}, {
                    defaultMsg: '删除失败',
                    success: function (result) {
                        layer.close(loading);
                        if (isAjaxSuccess(result)) {
                            layer.msg(result.msg, {icon: 1, time: 1000}, function () {
                                obj.del();
                            });
                        } else {
                            layer.msg(result.msg || '删除失败', {icon: 2, time: 1000});
                        }
                    },
                    error: function () {
                        layer.close(loading);
                    }
                });
            });
        };

        window.batchRemove = function (obj) {
            let ids = getCheckedFieldIds(obj, '${pkFieldName}');
            if (ids === '') {
                layer.msg('未选中数据', {icon: 3, time: 1000});
                return false;
            }
            layer.confirm('确定要删除这些${functionName}吗？', {icon: 3, title: '提示'}, function (index) {
                layer.close(index);
                let loading = layer.load();
                deleteJson(MODULE_PATH + '/delete', {ids: ids}, {
                    defaultMsg: '删除失败',
                    success: function (result) {
                        layer.close(loading);
                        if (isAjaxSuccess(result)) {
                            layer.msg(result.msg, {icon: 1, time: 1000}, function () {
                                table.reload('${moduleName}-table');
                            });
                        } else {
                            layer.msg(result.msg || '删除失败', {icon: 2, time: 1000});
                        }
                    },
                    error: function () {
                        layer.close(loading);
                    }
                });
            });
        };

        window.refresh = function () {
            table.reload('${moduleName}-table');
        };
    });
</script>
</body>
</html>
