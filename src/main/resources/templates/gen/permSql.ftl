-- ${functionName}模块菜单与按钮权限（表 ${tableName} 需已存在）
-- 解压后执行本脚本，并为管理员角色绑定权限

<#assign menuId = permIds["menu"]>
<#assign menuPath = parentPermPath + "," + menuId>
INSERT INTO `sys_perm` VALUES ('${menuId}', '${functionName}管理', '${functionName}管理', '/${moduleName}/listView', 0, '${parentPermId}', '${menuPath}', '${permPrefix}:view', 1, 'layui-icon layui-icon-template-1', 1, 1, '${author}', NOW(), '', NULL, 0);

<#assign insertId = permIds["insert"]>
INSERT INTO `sys_perm` VALUES ('${insertId}', '${functionName}添加', '${functionName}添加', '/${moduleName}/add', 0, '${menuId}', '${menuPath},${insertId}', '${permPrefix}:insert', 2, '', 1, 1, '${author}', NOW(), '', NULL, 0);

<#assign deleteId = permIds["delete"]>
INSERT INTO `sys_perm` VALUES ('${deleteId}', '${functionName}删除', '${functionName}删除', '/${moduleName}/delete', 0, '${menuId}', '${menuPath},${deleteId}', '${permPrefix}:delete', 2, '', 2, 1, '${author}', NOW(), '', NULL, 0);

<#assign updateId = permIds["update"]>
INSERT INTO `sys_perm` VALUES ('${updateId}', '${functionName}修改', '${functionName}修改', '/${moduleName}/update', 0, '${menuId}', '${menuPath},${updateId}', '${permPrefix}:update', 2, '', 3, 1, '${author}', NOW(), '', NULL, 0);

<#assign selectId = permIds["select"]>
INSERT INTO `sys_perm` VALUES ('${selectId}', '${functionName}集合', '${functionName}集合', '/${moduleName}/list', 0, '${menuId}', '${menuPath},${selectId}', '${permPrefix}:select', 2, '', 4, 1, '${author}', NOW(), '', NULL, 0);

<#if hasIsEnabled>
<#assign enabledId = permIds["updateEnabled"]>
INSERT INTO `sys_perm` VALUES ('${enabledId}', '${functionName}状态修改', '${functionName}状态修改', '/${moduleName}/updateEnabled', 0, '${menuId}', '${menuPath},${enabledId}', '${permPrefix}:updateEnabled', 2, '', 5, 1, '${author}', NOW(), '', NULL, 0);
</#if>

-- 管理员角色绑定（role_id=${adminRoleId}）
INSERT INTO `sys_perm_role` VALUES ('${menuId}pr01', '${adminRoleId}', '${menuId}', '${author}', NOW());
INSERT INTO `sys_perm_role` VALUES ('${insertId}pr01', '${adminRoleId}', '${insertId}', '${author}', NOW());
INSERT INTO `sys_perm_role` VALUES ('${deleteId}pr01', '${adminRoleId}', '${deleteId}', '${author}', NOW());
INSERT INTO `sys_perm_role` VALUES ('${updateId}pr01', '${adminRoleId}', '${updateId}', '${author}', NOW());
INSERT INTO `sys_perm_role` VALUES ('${selectId}pr01', '${adminRoleId}', '${selectId}', '${author}', NOW());
<#if hasIsEnabled>
INSERT INTO `sys_perm_role` VALUES ('${enabledId}pr01', '${adminRoleId}', '${enabledId}', '${author}', NOW());
</#if>
