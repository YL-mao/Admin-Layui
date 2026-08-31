/*
 Navicat Premium Dump SQL

 Source Server         : YLmao-admin
 Source Server Type    : MySQL
 Source Server Version : 80029 (8.0.29)
 Source Host           : localhost:3306
 Source Schema         : YLmao-admin

 Target Server Type    : MySQL
 Target Server Version : 80029 (8.0.29)
 File Encoding         : 65001

 Date: 31/08/2026 21:38:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置ID（雪花）',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配置名称',
  `config_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配置编码',
  `config_value` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配置值',
  `config_group` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'system' COMMENT '配置分组',
  `value_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'string' COMMENT '值类型：string/number/boolean/json',
  `is_builtin` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否内置：1是 0否',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用：1启用 0停用',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `config_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '配置说明',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0存在 1删除',
  PRIMARY KEY (`config_id`) USING BTREE,
  UNIQUE INDEX `uk_config_code`(`config_code` ASC) USING BTREE,
  INDEX `idx_config_group_order`(`config_group` ASC, `order_num` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_config_enabled`(`is_enabled` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES ('1227775834488705024', '系统名称', 'system.name', 'YLmao-admin', 'system', 'string', 1, 1, 1, '后台系统显示名称', '1662038524471218177', '2026-06-19 10:29:40', '1662038524471218177', '2026-07-18 23:06:52', 0);
INSERT INTO `sys_config` VALUES ('1227854583687155712', '系统简称', 'system.shortNm', 'YLmao', 'system', 'string', 1, 1, 3, '侧边栏折叠、移动端、小空间展示', 'system', '2026-06-19 15:42:35', '1662038524471218177', '2026-07-18 23:06:52', 0);
INSERT INTO `sys_config` VALUES ('1227854583691350016', '系统Logo', 'system.logo', '/static/admin/images/ylmao/logo.png', 'system', 'string', 1, 1, 4, '后台左上角 Logo', 'system', '2026-06-19 15:42:35', '1662038524471218177', '2026-07-18 23:06:52', 0);
INSERT INTO `sys_config` VALUES ('1227854583691350017', '浏览器图标', 'system.favicon', '/static/ico/favicon.ico', 'system', 'string', 1, 1, 5, '浏览器 tab 图标', 'system', '2026-06-19 15:42:35', '1662038524471218177', '2026-07-18 23:06:52', 0);
INSERT INTO `sys_config` VALUES ('1227854583695544320', '版权信息', 'system.copyright', 'Copyright © 2026 YLmao', 'system', 'string', 1, 1, 6, '登录页、页脚', 'system', '2026-06-19 15:42:35', '1662038524471218177', '2026-07-18 23:06:52', 0);
INSERT INTO `sys_config` VALUES ('1227854583695544321', '管理员邮箱', 'system.adminMail', 'q77373080@gmail.com', 'system', 'string', 1, 1, 7, '系统联系邮箱、异常通知联系人', 'system', '2026-06-19 15:42:35', '1662038524471218177', '2026-07-18 23:06:52', 0);
INSERT INTO `sys_config` VALUES ('1227854583695544322', '系统版本', 'system.version', '1.0.0', 'system', 'string', 1, 1, 8, '关于页面、页脚、诊断信息', 'system', '2026-06-19 15:42:35', '1662038524471218177', '2026-07-18 23:06:52', 0);
INSERT INTO `sys_config` VALUES ('1227854583699738624', '官网地址', 'system.website', 'www.baidu.com', 'system', 'string', 1, 1, 9, '登录页、关于页、品牌链接', 'system', '2026-06-19 15:42:35', '1662038524471218177', '2026-07-18 23:06:52', 0);
INSERT INTO `sys_config` VALUES ('1227854583699738625', '备案号', 'system.icp', '测试备案号', 'system', 'string', 1, 1, 10, '公网网站页脚备案', 'system', '2026-06-19 15:42:35', '1662038524471218177', '2026-07-18 23:06:52', 0);
INSERT INTO `sys_config` VALUES ('1227854583703932928', '公安备案号', 'system.policeIcp', '测试公安备案号', 'system', 'string', 1, 1, 11, '公网网站页脚公安备案', 'system', '2026-06-19 15:42:35', '1662038524471218177', '2026-07-18 23:06:52', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000011', '上传开关', 'upload.enabled', 'true', 'upload', 'boolean', 1, 1, 1, '是否允许后台上传文件', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-07-19 17:14:09', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000012', '存储方式', 'upload.storType', 'local', 'upload', 'string', 1, 1, 2, '上传存储方式：local 本地，oss/cos/minio 为后续扩展', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-07-19 17:14:09', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000013', '本地存储目录', 'upload.locPath', 'upload', 'upload', 'string', 1, 1, 3, '本地上传文件根目录，文件流接口按该目录读取文件', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-07-19 17:14:09', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000014', '公开访问前缀', 'upload.pubUrlPfx', '/upload', 'upload', 'string', 1, 1, 4, '上传文件访问接口前缀，由后端读取文件流返回，不使用静态资源映射', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-07-19 17:14:09', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000015', '单文件大小MB', 'upload.maxFileSzMb', '10', 'upload', 'number', 1, 1, 5, '业务层允许上传的单文件最大大小，单位 MB', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-07-19 17:14:09', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000016', '图片允许后缀', 'upload.imgExts', '[\"jpg\",\"jpeg\",\"png\",\"gif\",\"webp\"]', 'upload', 'json', 1, 1, 6, '图片上传场景允许的文件后缀，如头像、Logo、富文本图片', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-07-19 17:14:09', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000017', '文档允许后缀', 'upload.docsExts', '[\"pdf\",\"doc\",\"docx\",\"ppt\",\"pptx\",\"txt\"]', 'upload', 'json', 1, 1, 7, '普通文档附件上传场景允许的文件后缀', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-07-19 17:14:09', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000018', '表格允许后缀', 'upload.excelExts', '[\"xls\",\"xlsx\",\"csv\"]', 'upload', 'json', 1, 1, 8, '表格导入和模板上传场景允许的文件后缀', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-07-19 17:14:09', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000021', '登录日志开关', 'log.loginEn', 'true', 'log', 'boolean', 1, 1, 1, '是否记录登录、注销日志', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-07-19 17:41:13', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000022', '操作日志开关', 'log.operEn', 'true', 'log', 'boolean', 1, 1, 2, '是否记录后台业务操作日志', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-07-19 17:41:13', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000027', '日志保留天数', 'log.retainDays', '0', 'log', 'number', 1, 1, 7, '操作日志保留天数，0 表示不自动清理', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-07-19 17:41:13', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000031', '账号失败锁定次数', 'security.acctFailLim', '5', 'security', 'number', 1, 1, 1, '账号密码连续错误达到该次数后锁定；0不锁号', 'system', '2026-07-19 23:17:00', '1662038524471218177', '2026-08-27 09:53:53', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000032', 'IP失败拉黑阈值', 'security.ipFailLim', '10', 'security', 'number', 1, 1, 2, 'IP鉴权失败次数每达到该阈值的整数倍时自动拉黑或续期；0不自动拉黑', 'system', '2026-07-19 23:17:00', '1662038524471218177', '2026-08-27 09:53:53', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000033', '自动拉黑分钟数', 'security.autoBanMin', '60', 'security', 'number', 1, 1, 3, '自动拉黑每次叠加的分钟数；0不自动拉黑', 'system', '2026-08-27 10:39:46', '', NULL, 0);
INSERT INTO `sys_config` VALUES ('1227899000000000034', '验证码IP限流', 'security.capIpLim', '10', 'security', 'number', 1, 1, 4, '同一IP每窗口最多拉取验证码次数；0关闭该项', 'system', '2026-08-27 09:00:00', '1662038524471218177', '2026-08-27 09:53:53', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000035', '登录IP限流', 'security.loginIpLim', '20', 'security', 'number', 1, 1, 5, '同一IP每窗口最多提交登录次数；0关闭该项', 'system', '2026-08-27 09:00:00', '1662038524471218177', '2026-08-27 09:53:53', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000036', '登录账号限流', 'security.loginAcctLim', '15', 'security', 'number', 1, 1, 6, '同一账号每窗口最多被提交登录次数；0关闭该项', 'system', '2026-08-27 09:00:00', '1662038524471218177', '2026-08-27 09:53:53', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000037', '限流窗口分钟', 'security.winMinLim', '5', 'security', 'number', 1, 1, 7, '软拦固定窗口分钟数；0关闭整组软拦', 'system', '2026-08-27 09:00:00', '1662038524471218177', '2026-08-27 09:53:53', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000041', '任务扫描间隔秒', 'job.scanSecs', '600', 'job', 'number', 1, 1, 1, '定时扫描 sys_job 并重新注册触发的间隔秒数，最低 60，无上限', 'system', '2026-08-04 23:17:14', '1662038524471218177', '2026-08-26 11:40:37', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000051', '已读保留天数', 'notice.readDays', '0', 'notice', 'number', 1, 1, 1, '收件箱已读保留天数，0 表示不自动清理', 'system', '2026-08-25 00:00:00', '', NULL, 0);
INSERT INTO `sys_config` VALUES ('1227899000000000052', '未读保留天数', 'notice.unreadDays', '0', 'notice', 'number', 1, 1, 2, '收件箱未读保留天数，0 表示不自动清理', 'system', '2026-08-25 00:00:00', '', NULL, 0);

-- ----------------------------
-- Table structure for sys_demo
-- ----------------------------
DROP TABLE IF EXISTS `sys_demo`;
CREATE TABLE `sys_demo`  (
  `demo_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '演示ID（雪花）',
  `demo_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '演示名称',
  `demo_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '演示编码',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用（1启用 0停用）',
  `demo_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0存在 1删除',
  PRIMARY KEY (`demo_id`) USING BTREE,
  UNIQUE INDEX `uk_demo_code`(`demo_code` ASC) USING BTREE,
  UNIQUE INDEX `uk_demo_name`(`demo_name` ASC) USING BTREE,
  INDEX `idx_demo_enabled_order`(`is_enabled` ASC, `order_num` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '代码生成验收表示例' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_demo
-- ----------------------------

-- ----------------------------
-- Table structure for sys_department
-- ----------------------------
DROP TABLE IF EXISTS `sys_department`;
CREATE TABLE `sys_department`  (
  `department_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位id',
  `parent_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父id',
  `department_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位名称',
  `leader` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门负责人',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` int NULL DEFAULT NULL COMMENT '状态',
  `order_num` int NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`department_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_department
-- ----------------------------
INSERT INTO `sys_department` VALUES ('1', '0', 'v2', 'v2', '13012345678', 'v2@qq.com', 1, 1);
INSERT INTO `sys_department` VALUES ('2', '1', '技术部门', 'x某某', '13012345678', 'v2@qq.com', 1, 2);
INSERT INTO `sys_department` VALUES ('3', '1', '人事部门', 'a某某', '13012345678', 'v2@qq.com', 1, 3);
INSERT INTO `sys_department` VALUES ('4', '2', '开发一小组', 'b某某', '13012345678', 'v2@qq.com', 1, 4);
INSERT INTO `sys_department` VALUES ('5', '3', '销售部门', 'd某某', '13012345678', 'v2@qq.com', 1, 5);
INSERT INTO `sys_department` VALUES ('6', '5', '销售一组', 'e某某', '13012345678', 'v2@qq.com', 1, 6);

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `dept_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门id（雪花）',
  `parent_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '父部门id，根为0',
  `dept_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '部门路径，如 0,1001,1002',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `dept_leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '负责人',
  `leader_phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '负责人电话',
  `leader_email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '负责人邮箱',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用（1启用 0停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0存在 1删除',
  PRIMARY KEY (`dept_id`) USING BTREE,
  UNIQUE INDEX `uk_dept_parent_name`(`parent_id` ASC, `dept_name` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES ('1', '0', '0', 'v2', 1, 'v2', '13012345678', 'v2@qq.com', 1, 'migrate', '2026-05-29 23:28:04', '', NULL, 0);
INSERT INTO `sys_dept` VALUES ('2', '1', '0,1', '技术部门', 2, 'x某某', '13012345678', 'v2@qq.com', 1, 'migrate', '2026-05-29 23:28:04', '', NULL, 0);
INSERT INTO `sys_dept` VALUES ('3', '1', '0,1', '人事部门', 3, 'a某某', '13012345678', 'v2@qq.com', 1, 'migrate', '2026-05-29 23:28:04', '', NULL, 0);
INSERT INTO `sys_dept` VALUES ('4', '2', '0,1,2', '开发一小组1', 4, 'b某某', '13012345678', 'v2@qq.com', 1, 'migrate', '2026-05-29 23:28:04', '1662038524471218177', '2026-06-09 01:58:22', 0);
INSERT INTO `sys_dept` VALUES ('5', '3', '0,1,3', '销售部门', 5, 'd某某', '13012345678', 'v2@qq.com', 1, 'migrate', '2026-05-29 23:28:04', '1662038524471218177', '2026-06-20 16:24:56', 0);
INSERT INTO `sys_dept` VALUES ('6', '5', '0,1,3,5', '销售一组', 6, 'e某某', '13012345678', 'v2@qq.com', 1, 'migrate', '2026-05-29 23:28:04', '', NULL, 0);

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_data_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典数据ID（雪花）',
  `dict_type_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型编码',
  `dict_data_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典数据标签',
  `dict_data_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典数据值',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用（1启用 0停用）',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否默认（1是 0否）',
  `dict_data_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典数据说明',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0存在 1删除',
  PRIMARY KEY (`dict_data_id`) USING BTREE,
  UNIQUE INDEX `uk_dict_data_label`(`dict_type_code` ASC, `dict_data_label` ASC) USING BTREE,
  UNIQUE INDEX `uk_dict_data_value`(`dict_type_code` ASC, `dict_data_value` ASC) USING BTREE,
  INDEX `idx_dict_data_type_order`(`dict_type_code` ASC, `order_num` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES ('1227900000000000101', 'sys_user_sex', '男', '0', 1, 1, '0', '', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-06-20 16:25:09', 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000102', 'sys_user_sex', '女', '1', 1, 2, '1', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000201', 'sys_post_type', '管理岗', '1', 1, 1, '1', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000202', 'sys_post_type', '技术岗', '2', 1, 2, '0', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000203', 'sys_post_type', '运营岗', '3', 1, 3, '0', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000204', 'sys_post_type', '市场岗', '4', 1, 4, '0', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000301', 'sys_notice_type', '系统通知', '1', 1, 1, '1', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000302', 'sys_notice_type', '运营活动', '2', 1, 2, '0', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000303', 'sys_notice_type', '平台公告', '3', 1, 3, '0', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000304', 'sys_notice_type', '用户私信', '4', 1, 4, '0', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000401', 'sys_notice_receiver_type', '全体用户', '1', 1, 1, '1', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000402', 'sys_notice_receiver_type', '指定角色', '2', 1, 2, '0', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000403', 'sys_notice_receiver_type', '指定部门', '3', 1, 3, '0', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000404', 'sys_notice_receiver_type', '指定个人', '4', 1, 4, '0', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `dict_type_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典类型ID（雪花）',
  `dict_type_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型名称',
  `dict_type_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型编码',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用（1启用 0停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `dict_type_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型说明',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0存在 1删除',
  PRIMARY KEY (`dict_type_id`) USING BTREE,
  UNIQUE INDEX `uk_dict_type_code`(`dict_type_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES ('1227900000000000001', '用户性别', 'sys_user_sex', 1, 1, 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-06-20 16:29:49', '对应 sys_user.user_sex', 0);
INSERT INTO `sys_dict_type` VALUES ('1227900000000000002', '岗位类型', 'sys_post_type', 2, 1, 'system', '2026-06-20 00:00:00', '', NULL, '对应 sys_post.post_type', 0);
INSERT INTO `sys_dict_type` VALUES ('1227900000000000003', '公告类型', 'sys_notice_type', 3, 1, 'system', '2026-06-20 00:00:00', '', NULL, '对应 sys_notice.notice_type', 0);
INSERT INTO `sys_dict_type` VALUES ('1227900000000000004', '公告接收者类型', 'sys_notice_receiver_type', 4, 1, 'system', '2026-06-20 00:00:00', '', NULL, '对应 sys_notice.receiver_type', 0);

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `file_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件ID（雪花）',
  `folder_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属虚拟目录ID',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '原始文件名',
  `storage_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '物理存储键，如 2026/07/{fileId}.png',
  `storage_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'local' COMMENT '存储方式，当前仅 local',
  `file_suffix` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件后缀',
  `content_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'MIME类型',
  `file_size` bigint NOT NULL DEFAULT 0 COMMENT '文件大小（字节）',
  `file_scene` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'image' COMMENT '场景：image/document/excel',
  `need_login` tinyint(1) NOT NULL DEFAULT 1 COMMENT '预览是否需登录（1是 0否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0存在 1回收站',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '进入回收站时间',
  PRIMARY KEY (`file_id`) USING BTREE,
  INDEX `idx_file_folder`(`folder_id` ASC, `is_del` ASC) USING BTREE,
  INDEX `idx_file_name`(`folder_id` ASC, `original_name` ASC, `is_del` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件资源表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file
-- ----------------------------
INSERT INTO `sys_file` VALUES ('2078732893698113538', '1229000000000000001', 'codm_2025_09_25_20_16_11.png', '2026/07/2078732893698113538.png', 'local', 'png', 'image/png', 568090, 'image', 1, '1662038524471218177', '2026-07-19 14:45:18', '', NULL, 1, '2026-07-19 15:50:49');
INSERT INTO `sys_file` VALUES ('2078742928327151618', '1229000000000000001', '53e0912d-fb84-4146-b87b-c04577d00bc0.png', '2026/07/2078742928327151618.png', 'local', 'png', 'image/png', 1742236, 'image', 1, '1662038524471218177', '2026-07-19 15:25:10', '1662038524471218177', '2026-07-19 15:50:36', 0, NULL);
INSERT INTO `sys_file` VALUES ('2078743129913790465', '2078743076172173314', 'codm_2025_09_25_20_16_11.png', '2026/07/2078743129913790465.png', 'local', 'png', 'image/png', 568090, 'image', 1, '1662038524471218177', '2026-07-19 15:25:58', '1662038524471218177', '2026-07-19 15:33:52', 0, NULL);
INSERT INTO `sys_file` VALUES ('2078743155012505602', '2078743076172173314', '8164b70e-c255-4fad-8ca7-5d554d036164.png', '2026/07/2078743155012505602.png', 'local', 'png', 'image/png', 1165430, 'image', 1, '1662038524471218177', '2026-07-19 15:26:04', '', NULL, 0, NULL);
INSERT INTO `sys_file` VALUES ('2078743765380206593', '1229000000000000001', 'yunmeng-image-20260614024417.png', '2026/07/2078743765380206593.png', 'local', 'png', 'image/png', 1358896, 'image', 1, '1662038524471218177', '2026-07-19 15:28:30', '1662038524471218177', '2026-07-19 15:32:58', 0, NULL);
INSERT INTO `sys_file` VALUES ('2078744168989691905', '1229000000000000001', '72a72165-0cd3-49a9-89b6-553df4677e74.png', '2026/07/2078744168989691905.png', 'local', 'png', 'image/png', 478363, 'image', 1, '1662038524471218177', '2026-07-19 15:30:06', '1662038524471218177', '2026-07-19 15:33:05', 0, NULL);
INSERT INTO `sys_file` VALUES ('2078744233795883010', '1229000000000000001', '桌面图标.png', '2026/07/2078744233795883010.png', 'local', 'png', 'image/png', 319775, 'image', 1, '1662038524471218177', '2026-07-19 15:30:21', '1662038524471218177', '2026-07-19 15:33:28', 0, NULL);
INSERT INTO `sys_file` VALUES ('2078744456769277953', '1229000000000000001', 'yunmeng-image-20260614024417-removebg-preview.png', '2026/07/2078744456769277953.png', 'local', 'png', 'image/png', 383001, 'image', 1, '1662038524471218177', '2026-07-19 15:31:14', '1662038524471218177', '2026-07-19 15:33:01', 0, NULL);
INSERT INTO `sys_file` VALUES ('2078744499165302785', '2078743076172173314', 'yunmeng-image-20260614024417.png', '2026/07/2078744499165302785.png', 'local', 'png', 'image/png', 1358896, 'image', 1, '1662038524471218177', '2026-07-19 15:31:24', '', NULL, 0, NULL);
INSERT INTO `sys_file` VALUES ('2078744513312690178', '2078743076172173314', 'yunmeng-image-20260614024417-removebg-preview.png', '2026/07/2078744513312690178.png', 'local', 'png', 'image/png', 383001, 'image', 1, '1662038524471218177', '2026-07-19 15:31:28', '', NULL, 0, NULL);
INSERT INTO `sys_file` VALUES ('2078744526763823105', '2078743076172173314', '72a72165-0cd3-49a9-89b6-553df4677e74.png', '2026/07/2078744526763823105.png', 'local', 'png', 'image/png', 478363, 'image', 1, '1662038524471218177', '2026-07-19 15:31:31', '', NULL, 0, NULL);
INSERT INTO `sys_file` VALUES ('2078744551715737601', '2078743076172173314', '72a72165-0cd3-49a9-89b6-553df4677e74-removebg-preview.png', '2026/07/2078744551715737601.png', 'local', 'png', 'image/png', 58250, 'image', 1, '1662038524471218177', '2026-07-19 15:31:37', '', NULL, 0, NULL);
INSERT INTO `sys_file` VALUES ('2078744567779921922', '2078743076172173314', '托盘图标.png', '2026/07/2078744567779921922.png', 'local', 'png', 'image/png', 46899, 'image', 1, '1662038524471218177', '2026-07-19 15:31:41', '', NULL, 0, NULL);
INSERT INTO `sys_file` VALUES ('2078744579519778818', '2078743076172173314', 'yunmeng-image-20260614024729.png', '2026/07/2078744579519778818.png', 'local', 'png', 'image/png', 1317008, 'image', 1, '1662038524471218177', '2026-07-19 15:31:44', '', NULL, 0, NULL);
INSERT INTO `sys_file` VALUES ('2078744592689893377', '2078743076172173314', 'yunmeng-image-20260614024729-removebg-preview.png', '2026/07/2078744592689893377.png', 'local', 'png', 'image/png', 317225, 'image', 1, '1662038524471218177', '2026-07-19 15:31:47', '', NULL, 0, NULL);
INSERT INTO `sys_file` VALUES ('2078744607269294081', '2078743076172173314', '桌面图标.png', '2026/07/2078744607269294081.png', 'local', 'png', 'image/png', 319775, 'image', 1, '1662038524471218177', '2026-07-19 15:31:50', '1662038524471218177', '2026-07-19 16:56:17', 0, NULL);
INSERT INTO `sys_file` VALUES ('2078744933703585794', '1229000000000000001', '72a72165-0cd3-49a9-89b6-553df4677e74-removebg-preview.png', '2026/07/2078744933703585794.png', 'local', 'png', 'image/png', 58250, 'image', 1, '1662038524471218177', '2026-07-19 15:33:08', '', NULL, 0, NULL);
INSERT INTO `sys_file` VALUES ('2078744947318296578', '1229000000000000001', '托盘图标.png', '2026/07/2078744947318296578.png', 'local', 'png', 'image/png', 46899, 'image', 1, '1662038524471218177', '2026-07-19 15:33:11', '', NULL, 0, NULL);
INSERT INTO `sys_file` VALUES ('2078745033771290626', '1229000000000000001', 'yunmeng-image-20260614024729-removebg-preview.png', '2026/07/2078745033771290626.png', 'local', 'png', 'image/png', 317225, 'image', 1, '1662038524471218177', '2026-07-19 15:33:32', '', NULL, 1, '2026-07-19 15:42:24');
INSERT INTO `sys_file` VALUES ('2078745048895950849', '1229000000000000001', 'yunmeng-image-20260614024729.png', '2026/07/2078745048895950849.png', 'local', 'png', 'image/png', 1317008, 'image', 1, '1662038524471218177', '2026-07-19 15:33:36', '1662038524471218177', '2026-07-19 15:33:39', 0, NULL);
INSERT INTO `sys_file` VALUES ('2078771687356874754', '1229000000000000001', '13c57ceb-0eef-4e84-ac4e-0cb16c5533ab.png', '2026/07/19/2078771687356874754.png', 'local', 'png', 'image/png', 37409, 'image', 1, '1662038524471218177', '2026-07-19 17:19:27', '1662038524471218177', '2026-07-20 00:32:31', 0, NULL);
INSERT INTO `sys_file` VALUES ('2078772744359231489', '1229000000000000001', '新建文本文档 (2).txt', '2026/07/19/2078772744359231489.txt', 'local', 'txt', 'text/plain', 743, 'document', 1, '1662038524471218177', '2026-07-19 17:23:39', '', NULL, 0, NULL);
INSERT INTO `sys_file` VALUES ('2078772824181030913', '1229000000000000001', '账号.xlsx', '2026/07/19/2078772824181030913.xlsx', 'local', 'xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 9340, 'excel', 1, '1662038524471218177', '2026-07-19 17:23:58', '', NULL, 0, NULL);

-- ----------------------------
-- Table structure for sys_filter
-- ----------------------------
DROP TABLE IF EXISTS `sys_filter`;
CREATE TABLE `sys_filter`  (
  `filter_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '访问控制ID（雪花）',
  `filter_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型：IP / USER_ID / DEVICE',
  `filter_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '过滤值：IP / 用户ID / 设备标识',
  `filter_source` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'MANUAL' COMMENT '来源：MANUAL人工 AUTO自动',
  `filter_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '说明',
  `policy_mode` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'BLACK' COMMENT '策略：WHITE白名单 BLACK黑名单',
  `expire_time` datetime NOT NULL COMMENT '过期时间；永久为 9999-12-31 23:59:59',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用（1启用 0停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0存在 1删除',
  `uk_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci GENERATED ALWAYS AS (if((`is_del` = 0),`filter_type`,NULL)) STORED COMMENT '未删唯一：类型' NULL,
  `uk_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci GENERATED ALWAYS AS (if((`is_del` = 0),`filter_value`,NULL)) STORED COMMENT '未删唯一：值' NULL,
  PRIMARY KEY (`filter_id`) USING BTREE,
  UNIQUE INDEX `uk_filter_active`(`uk_type` ASC, `uk_value` ASC) USING BTREE,
  INDEX `idx_filter_type_value`(`filter_type` ASC, `filter_value` ASC) USING BTREE,
  INDEX `idx_filter_hit`(`is_del` ASC, `is_enabled` ASC, `policy_mode` ASC, `filter_type` ASC, `filter_value` ASC, `expire_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '访问控制（黑白名单）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_filter
-- ----------------------------

-- ----------------------------
-- Table structure for sys_folder
-- ----------------------------
DROP TABLE IF EXISTS `sys_folder`;
CREATE TABLE `sys_folder`  (
  `folder_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '目录ID（雪花）',
  `parent_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '父目录ID，根为0',
  `folder_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '目录路径，如 0,1001',
  `folder_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '目录名称',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `is_builtin` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否内置（1是 0否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0存在 1回收站',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '进入回收站时间',
  PRIMARY KEY (`folder_id`) USING BTREE,
  INDEX `idx_folder_parent`(`parent_id` ASC, `is_del` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件虚拟目录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_folder
-- ----------------------------
INSERT INTO `sys_folder` VALUES ('1229000000000000001', '0', '0', '未分类', 0, 1, 'system', '2026-07-19 00:00:00', '', NULL, 0, NULL);
INSERT INTO `sys_folder` VALUES ('2078743076172173314', '0', '0', '测试目录1', 1, 0, '1662038524471218177', '2026-07-19 15:25:45', '1662038524471218177', '2026-07-19 17:12:01', 0, NULL);

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`  (
  `job_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务ID（雪花）',
  `job_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务编码（与代码注册表对应）',
  `job_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `job_cron` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Cron 表达式（展示/下次执行计算）',
  `job_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务说明',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用：0-停用，1-启用（定时触发前校验）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-存在，1-删除',
  PRIMARY KEY (`job_id`) USING BTREE,
  UNIQUE INDEX `uk_job_code`(`job_code` ASC) USING BTREE,
  INDEX `idx_job_enabled_order`(`is_enabled` ASC, `order_num` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统内置定时任务表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job` VALUES ('1229000000000000101', 'operateLogRetention', '操作日志保留清理', '0 0 3 * * ?', '按 log.retentionDays 清理过期操作日志', 1, 0, 'system', '2026-07-22 00:00:00', '', NULL, 0);
INSERT INTO `sys_job` VALUES ('1229000000000000102', 'noticeExpireClean', '过期公告清理', '0 10 3 * * ?', '软删除已过期公告及收件箱关联', 2, 0, 'system', '2026-07-22 00:00:00', '', NULL, 0);
INSERT INTO `sys_job` VALUES ('1229000000000000103', 'noticeReadClean', '收件箱已读清理', '0 20 3 * * ?', '按 notice.readDays 软删收件箱已读记录', 3, 0, 'system', '2026-08-25 00:00:00', '', NULL, 0);
INSERT INTO `sys_job` VALUES ('1229000000000000104', 'noticeUnreadClean', '收件箱未读清理', '0 30 3 * * ?', '按 notice.unreadDays 软删收件箱未读记录', 4, 0, 'system', '2026-08-25 00:00:00', '', NULL, 0);

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`  (
  `job_log_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行日志ID（雪花）',
  `job_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务ID',
  `job_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务编码快照',
  `trigger_type` tinyint(1) NOT NULL COMMENT '触发方式：1-定时，2-手动',
  `run_status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行结果：SUCCESS成功，FAILED失败，SKIPPED跳过',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `cost_ms` bigint NOT NULL DEFAULT 0 COMMENT '耗时毫秒',
  `message` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '结果摘要或失败原因',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`) USING BTREE,
  INDEX `idx_job_log_job_start`(`job_id` ASC, `start_time` ASC) USING BTREE,
  INDEX `idx_job_log_code_start`(`job_code` ASC, `start_time` ASC) USING BTREE,
  INDEX `idx_job_log_job_status_end`(`job_id` ASC, `run_status` ASC, `end_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统内置定时任务执行日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------
INSERT INTO `sys_job_log` VALUES ('2079949670897569794', '1229000000000000101', 'operateLogRetention', 2, 'SUCCESS', '2026-07-22 23:20:20', '2026-07-22 23:20:20', 1, '清理操作日志 0 条', '2026-07-22 23:20:20');
INSERT INTO `sys_job_log` VALUES ('2079952393692925954', '1229000000000000102', 'noticeExpireClean', 2, 'SUCCESS', '2026-07-22 23:31:09', '2026-07-22 23:31:09', 14, '清理过期公告 1 条', '2026-07-22 23:31:09');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `notice_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '通知ID（雪花）',
  `notice_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '通知标题',
  `notice_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '通知内容（富文本/Markdown）',
  `notice_type` tinyint NOT NULL DEFAULT 1 COMMENT '通知类型：1-系统通知，2-运营活动，3-平台公告，4-用户私信',
  `receiver_type` tinyint NOT NULL DEFAULT 1 COMMENT '接收者类型：1-全体用户，2-指定角色，3-指定部门，4-指定个人',
  `receiver_ids` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接收目标ID，逗号分隔（角色/部门/用户）',
  `notice_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公告说明',
  `is_send` tinyint(1) NOT NULL DEFAULT 0 COMMENT '发布状态：0-草稿，1-已发布',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发布时间（已发布时写入）',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间（到期后不再展示）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-存在，1-删除',
  PRIMARY KEY (`notice_id`) USING BTREE,
  INDEX `idx_notice_type`(`notice_type` ASC) USING BTREE,
  INDEX `idx_notice_expire`(`expire_time` ASC) USING BTREE,
  INDEX `idx_notice_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_notice_send`(`is_send` ASC, `order_num` ASC, `send_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统通知公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES ('1225030694779097088', '测试公共', '测试内容', 4, 1, NULL, '', 1, 1, '2026-06-11 12:41:28', '2026-06-14 16:00:00', '', '2026-06-11 12:41:28', '1662038524471218177', '2026-07-22 23:31:09', 1);
INSERT INTO `sys_notice` VALUES ('1225060037265854464', '测试全体用户', '测试全体用户', 1, 1, NULL, '测试全体用户', 1, 0, '2026-06-11 14:38:04', NULL, '', '2026-06-11 14:38:04', '', '2026-06-11 22:38:03', 0);
INSERT INTO `sys_notice` VALUES ('1225060585608187904', '测试指定角色', '测试指定角色', 2, 2, '488243256161730560', '测试指定角色', 1, 0, '2026-06-11 14:40:14', NULL, '', '2026-06-11 14:40:14', '', '2026-06-11 22:40:14', 0);
INSERT INTO `sys_notice` VALUES ('1225061498532007936', '测试指定部门', '测试指定部门', 3, 3, '1,2,4,3,5,6', '测试指定部门', 1, 0, '2026-06-11 14:43:52', NULL, '', '2026-06-11 14:43:52', '', '2026-06-11 22:43:52', 0);
INSERT INTO `sys_notice` VALUES ('1225063874634584064', '测试指定用户', '测试指定用户', 4, 4, '1662038524471218177', '测试指定用户', 1, 0, '2026-06-11 14:53:19', NULL, '', '2026-06-11 14:53:19', '', '2026-06-11 22:53:18', 0);
INSERT INTO `sys_notice` VALUES ('2092583217764487169', '测试xss<script>alert(1)</script>', '<script>alert(1)</script>', 1, 1, NULL, '', 1, 0, '2026-08-26 20:01:32', '2026-08-29 00:00:00', '1662038524471218177', '2026-08-26 20:01:32', '', '2026-08-26 20:01:32', 0);

-- ----------------------------
-- Table structure for sys_notice_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice_user`;
CREATE TABLE `sys_notice_user`  (
  `user_notice_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户通知ID（雪花）',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `notice_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '通知ID',
  `is_read` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-存在，1-删除（用户侧隐藏）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（投递时间）',
  PRIMARY KEY (`user_notice_id`) USING BTREE,
  UNIQUE INDEX `uk_user_notice`(`user_id` ASC, `notice_id` ASC) USING BTREE,
  INDEX `idx_user_inbox`(`user_id` ASC, `is_del` ASC, `is_read` ASC) USING BTREE,
  INDEX `idx_notice_id`(`notice_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户通知已读状态表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice_user
-- ----------------------------
INSERT INTO `sys_notice_user` VALUES ('1225060037282631680', '1220945860838428672', '1225060037265854464', 0, NULL, 0, '2026-06-11 22:38:03');
INSERT INTO `sys_notice_user` VALUES ('1225060037286825984', '1221663362941849600', '1225060037265854464', 0, NULL, 0, '2026-06-11 22:38:03');
INSERT INTO `sys_notice_user` VALUES ('1225060037291020288', '1662038524471218177', '1225060037265854464', 1, '2026-06-18 16:18:18', 0, '2026-06-11 22:38:03');
INSERT INTO `sys_notice_user` VALUES ('1225060585629159424', '1220945860838428672', '1225060585608187904', 0, NULL, 0, '2026-06-11 22:40:14');
INSERT INTO `sys_notice_user` VALUES ('1225060585633353729', '1662038524471218177', '1225060585608187904', 1, '2026-06-18 16:18:07', 0, '2026-06-11 22:40:14');
INSERT INTO `sys_notice_user` VALUES ('1225061498548785152', '1220945860838428672', '1225061498532007936', 0, NULL, 0, '2026-06-11 22:43:52');
INSERT INTO `sys_notice_user` VALUES ('1225061498552979456', '1221663362941849600', '1225061498532007936', 0, NULL, 0, '2026-06-11 22:43:52');
INSERT INTO `sys_notice_user` VALUES ('1225061498557173760', '1662038524471218177', '1225061498532007936', 1, '2026-06-18 13:31:47', 0, '2026-06-11 22:43:52');
INSERT INTO `sys_notice_user` VALUES ('1225063874655555584', '1662038524471218177', '1225063874634584064', 1, '2026-06-18 11:46:54', 0, '2026-06-11 22:53:18');
INSERT INTO `sys_notice_user` VALUES ('2092583217798041602', '1662038524471218177', '2092583217764487169', 1, '2026-08-26 20:02:00', 0, '2026-08-26 20:01:32');
INSERT INTO `sys_notice_user` VALUES ('2092583217802235905', '1220945860838428672', '2092583217764487169', 0, NULL, 0, '2026-08-26 20:01:32');
INSERT INTO `sys_notice_user` VALUES ('2092583217810624514', '1221663362941849600', '2092583217764487169', 0, NULL, 0, '2026-08-26 20:01:32');

-- ----------------------------
-- Table structure for sys_operate_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_operate_log`;
CREATE TABLE `sys_operate_log`  (
  `operate_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作日志主键ID',
  `logging_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志类型：LOGIN/OPERATE',
  `business_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务类型：QUERY/ADD/UPDATE/DELETE/LOGIN/LOGOUT/OTHER',
  `operate_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作模块标题',
  `request_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'HTTP请求方式',
  `operate_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '后端类方法名',
  `request_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求接口路径',
  `request_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求URL参数',
  `request_body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求体内容',
  `response_body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '响应体内容',
  `is_success` tinyint(1) NULL DEFAULT NULL COMMENT '是否执行成功：1成功，0失败',
  `status_code` int NULL DEFAULT NULL COMMENT 'HTTP响应状态码',
  `error_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '异常类名',
  `error_msg` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '错误简要信息',
  `error_stack` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误堆栈详情',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人员用户ID',
  `operate_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人员姓名快照',
  `operate_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人员IP地址',
  `server_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务端IP地址',
  `user_agent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户浏览器UA串',
  `browser` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '浏览器类型',
  `system_os` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作系统',
  `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求链路ID',
  `cost_time` bigint NULL DEFAULT NULL COMMENT '操作耗时(毫秒)',
  `operate_time` datetime NULL DEFAULT NULL COMMENT '操作发生时间',
  PRIMARY KEY (`operate_id`) USING BTREE,
  INDEX `idx_operate_time`(`operate_time` ASC) USING BTREE,
  INDEX `idx_logging_type`(`logging_type` ASC) USING BTREE,
  INDEX `idx_business_type`(`business_type` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_is_success`(`is_success` ASC) USING BTREE,
  INDEX `idx_request_uri`(`request_uri` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_operate_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_perm
-- ----------------------------
DROP TABLE IF EXISTS `sys_perm`;
CREATE TABLE `sys_perm`  (
  `perm_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键（雪花）',
  `perm_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限名称',
  `perm_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限描述',
  `perm_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由/链接',
  `is_blank` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0本页 1新窗口',
  `parent_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '父节点',
  `perm_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '权限路径，如 0,1001,1002',
  `perm_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限标识',
  `perm_type` tinyint(1) NULL DEFAULT NULL COMMENT '0目录 1菜单 2按钮',
  `perm_icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限图标',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用/可见（1启用/可见 0停用/不可见）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`perm_id`) USING BTREE,
  UNIQUE INDEX `uk_perm_code`(`perm_code` ASC) USING BTREE,
  INDEX `idx_perm_parent_order`(`parent_id` ASC, `order_num` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限/菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_perm
-- ----------------------------
INSERT INTO `sys_perm` VALUES ('1220343202867974145', '工作空间', '工作空间', '', 0, '0', '0', NULL, 0, 'layui-icon layui-icon-console', 1, 1, 'migrate', '2026-06-10 22:37:45', '1662038524471218177', '2026-06-20 16:24:46', 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974146', '后台首页', '后台首页（工作台监控）', '/home/view', 0, '1220343202867974145', '0,1220343202867974145,1220343202867974146', 'home:view', 1, 'layui-icon layui-icon-home', 1, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974191', '用户管理', '用户管理', '/user/listView', 0, '1220343202867975001', '0,1220343202867975001,1220343202867974191', 'system:user:view', 1, 'layui-icon layui-icon-face-smile', 1, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974192', '用户添加', '用户添加', '/user/add', 0, '1220343202867974191', '0,1220343202867975001,1220343202867974191,1220343202867974192', 'system:user:insert', 2, '', 1, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974193', '用户删除', '用户删除', '/user/delete', 0, '1220343202867974191', '0,1220343202867975001,1220343202867974191,1220343202867974193', 'system:user:delete', 2, '', 2, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974194', '用户修改', '用户修改', '/user/update', 0, '1220343202867974191', '0,1220343202867975001,1220343202867974191,1220343202867974194', 'system:user:update', 2, '', 3, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974195', '用户集合', '用户集合', '/user/list', 0, '1220343202867974191', '0,1220343202867975001,1220343202867974191,1220343202867974195', 'system:user:select', 2, '', 4, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974196', '用户密码修改', '用户密码修改', '/user/updatePwd', 0, '1220343202867974191', '0,1220343202867975001,1220343202867974191,1220343202867974196', 'system:user:updatePwd', 2, '', 5, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974197', '用户状态修改', '用户状态修改', '/user/updateEnabled', 0, '1220343202867974191', '0,1220343202867975001,1220343202867974191,1220343202867974197', 'system:user:updateEnabled', 2, '', 6, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974198', '角色管理', '角色管理', '/role/listView', 0, '1220343202867975001', '0,1220343202867975001,1220343202867974198', 'system:role:view', 1, 'layui-icon layui-icon-face-cry', 2, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974199', '角色添加', '角色添加', '/role/add', 0, '1220343202867974198', '0,1220343202867975001,1220343202867974198,1220343202867974199', 'system:role:insert', 2, '', 1, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974200', '角色删除', '角色删除', '/role/delete', 0, '1220343202867974198', '0,1220343202867975001,1220343202867974198,1220343202867974200', 'system:role:delete', 2, '', 2, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974201', '角色修改', '角色修改', '/role/update', 0, '1220343202867974198', '0,1220343202867975001,1220343202867974198,1220343202867974201', 'system:role:update', 2, '', 3, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974202', '角色集合', '角色集合', '/role/list', 0, '1220343202867974198', '0,1220343202867975001,1220343202867974198,1220343202867974202', 'system:role:select', 2, '', 4, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974203', '角色授权', '角色授权', '/perm/rolePermTree', 0, '1220343202867974198', '0,1220343202867975001,1220343202867974198,1220343202867974203', 'system:role:auth', 2, '', 5, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974204', '角色状态修改', '角色状态修改', '/role/updateEnabled', 0, '1220343202867974198', '0,1220343202867975001,1220343202867974198,1220343202867974204', 'system:role:updateEnabled', 2, '', 6, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974205', '角色编码校验', '角色编码校验', '/role/checkCode', 0, '1220343202867974198', '0,1220343202867975001,1220343202867974198,1220343202867974205', 'system:role:checkCode', 2, '', 7, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974206', '权限管理', '权限管理', '/perm/listView', 0, '1220343202867975001', '0,1220343202867975001,1220343202867974206', 'system:perm:view', 1, 'layui-icon layui-icon-face-cry', 3, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974207', '权限添加', '权限添加', '/perm/add', 0, '1220343202867974206', '0,1220343202867975001,1220343202867974206,1220343202867974207', 'system:perm:insert', 2, '', 1, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974208', '权限删除', '权限删除', '/perm/delete', 0, '1220343202867974206', '0,1220343202867975001,1220343202867974206,1220343202867974208', 'system:perm:delete', 2, '', 2, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974209', '权限修改', '权限修改', '/perm/update', 0, '1220343202867974206', '0,1220343202867975001,1220343202867974206,1220343202867974209', 'system:perm:update', 2, '', 3, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974210', '权限集合', '权限集合', '/perm/list', 0, '1220343202867974206', '0,1220343202867975001,1220343202867974206,1220343202867974210', 'system:perm:select', 2, '', 4, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974211', '权限状态修改', '权限状态修改', '/perm/updateEnabled', 0, '1220343202867974206', '0,1220343202867975001,1220343202867974206,1220343202867974211', 'system:perm:updateEnabled', 2, '', 5, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974212', '上级权限选择', '上级权限选择', '/perm/selectParent', 0, '1220343202867974206', '0,1220343202867975001,1220343202867974206,1220343202867974212', 'system:perm:selectParent', 2, '', 6, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974213', '部门管理', '部门管理', '/dept/listView', 0, '1220343202867975001', '0,1220343202867975001,1220343202867974213', 'system:dept:view', 1, 'layui-icon layui-icon-face-cry', 4, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974214', '部门添加', '部门添加', '/dept/add', 0, '1220343202867974213', '0,1220343202867975001,1220343202867974213,1220343202867974214', 'system:dept:insert', 2, '', 1, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974215', '部门删除', '部门删除', '/dept/delete', 0, '1220343202867974213', '0,1220343202867975001,1220343202867974213,1220343202867974215', 'system:dept:delete', 2, '', 2, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974216', '部门修改', '部门修改', '/dept/update', 0, '1220343202867974213', '0,1220343202867975001,1220343202867974213,1220343202867974216', 'system:dept:update', 2, '', 3, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974217', '部门集合', '部门集合', '/dept/list', 0, '1220343202867974213', '0,1220343202867975001,1220343202867974213,1220343202867974217', 'system:dept:select', 2, '', 4, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974218', '部门状态修改', '部门状态修改', '/dept/updateEnabled', 0, '1220343202867974213', '0,1220343202867975001,1220343202867974213,1220343202867974218', 'system:dept:updateEnabled', 2, '', 5, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974219', '部门树查询', '部门树查询', '/dept/tree', 0, '1220343202867974213', '0,1220343202867975001,1220343202867974213,1220343202867974219', 'system:dept:tree', 2, '', 6, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974220', '岗位管理', '岗位管理', '/post/listView', 0, '1220343202867975001', '0,1220343202867975001,1220343202867974220', 'system:post:view', 1, 'layui-icon layui-icon-user', 5, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974221', '岗位添加', '岗位添加', '/post/add', 0, '1220343202867974220', '0,1220343202867975001,1220343202867974220,1220343202867974221', 'system:post:insert', 2, '', 1, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974222', '岗位删除', '岗位删除', '/post/delete', 0, '1220343202867974220', '0,1220343202867975001,1220343202867974220,1220343202867974222', 'system:post:delete', 2, '', 2, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974223', '岗位修改', '岗位修改', '/post/update', 0, '1220343202867974220', '0,1220343202867975001,1220343202867974220,1220343202867974223', 'system:post:update', 2, '', 3, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974224', '岗位集合', '岗位集合', '/post/list', 0, '1220343202867974220', '0,1220343202867975001,1220343202867974220,1220343202867974224', 'system:post:select', 2, '', 4, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974225', '岗位状态修改', '岗位状态修改', '/post/updateEnabled', 0, '1220343202867974220', '0,1220343202867975001,1220343202867974220,1220343202867974225', 'system:post:updateEnabled', 2, '', 5, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974226', '数据字典', '数据字典', '/dictType/listView', 0, '1220343202867975002', '0,1220343202867975002,1220343202867974226', 'system:dict:view', 1, 'layui-icon layui-icon-face-cry', 1, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974227', '字典类型添加', '字典类型添加', '/dictType/add', 0, '1220343202867974226', '0,1220343202867975002,1220343202867974226,1220343202867974227', 'system:dictType:insert', 2, '', 1, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974228', '字典类型删除', '字典类型删除', '/dictType/delete', 0, '1220343202867974226', '0,1220343202867975002,1220343202867974226,1220343202867974228', 'system:dictType:delete', 2, '', 2, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974229', '字典类型修改', '字典类型修改', '/dictType/update', 0, '1220343202867974226', '0,1220343202867975002,1220343202867974226,1220343202867974229', 'system:dictType:update', 2, '', 3, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974230', '字典类型集合', '字典类型集合', '/dictType/list', 0, '1220343202867974226', '0,1220343202867975002,1220343202867974226,1220343202867974230', 'system:dictType:select', 2, '', 4, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974231', '字典类型状态修改', '字典类型状态修改', '/dictType/updateEnabled', 0, '1220343202867974226', '0,1220343202867975002,1220343202867974226,1220343202867974231', 'system:dictType:updateEnabled', 2, '', 5, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974232', '字典数据添加', '字典数据添加', '/dictData/add', 0, '1220343202867974226', '0,1220343202867975002,1220343202867974226,1220343202867974232', 'system:dictData:insert', 2, '', 6, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974233', '字典数据删除', '字典数据删除', '/dictData/delete', 0, '1220343202867974226', '0,1220343202867975002,1220343202867974226,1220343202867974233', 'system:dictData:delete', 2, '', 7, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974234', '字典数据修改', '字典数据修改', '/dictData/update', 0, '1220343202867974226', '0,1220343202867975002,1220343202867974226,1220343202867974234', 'system:dictData:update', 2, '', 8, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974235', '字典数据集合', '字典数据集合', '/dictData/list', 0, '1220343202867974226', '0,1220343202867975002,1220343202867974226,1220343202867974235', 'system:dictData:select', 2, '', 9, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974236', '字典数据状态修改', '字典数据状态修改', '/dictData/updateEnabled', 0, '1220343202867974226', '0,1220343202867975002,1220343202867974226,1220343202867974236', 'system:dictData:updateEnabled', 2, '', 10, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974237', '字典数据默认修改', '字典数据默认修改', '/dictData/updateDefault', 0, '1220343202867974226', '0,1220343202867975002,1220343202867974226,1220343202867974237', 'system:dictData:updateDefault', 2, '', 11, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974238', '行为日志', '行为日志', '/operateLog/listView', 0, '1220343202867975003', '0,1220343202867975003,1220343202867974238', 'system:log:view', 1, 'layui-icon layui-icon-face-cry', 1, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974239', '日志集合', '日志集合', '/operateLog/list', 0, '1220343202867974238', '0,1220343202867975003,1220343202867974238,1220343202867974239', 'system:log:select', 2, '', 1, 1, 'migrate', '2026-06-10 22:37:45', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974253', '公告管理', '公告管理', '/notice/listView', 0, '1220343202867975002', '0,1220343202867975002,1220343202867974253', 'system:notice:view', 1, 'layui-icon layui-icon-notice', 3, 1, 'migrate', '2026-06-11 20:30:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974254', '公告添加', '公告添加', '/notice/add', 0, '1220343202867974253', '0,1220343202867975002,1220343202867974253,1220343202867974254', 'system:notice:insert', 2, '', 1, 1, 'migrate', '2026-06-11 20:30:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974255', '公告删除', '公告删除', '/notice/delete', 0, '1220343202867974253', '0,1220343202867975002,1220343202867974253,1220343202867974255', 'system:notice:delete', 2, '', 2, 1, 'migrate', '2026-06-11 20:30:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974256', '公告修改', '公告修改', '/notice/update', 0, '1220343202867974253', '0,1220343202867975002,1220343202867974253,1220343202867974256', 'system:notice:update', 2, '', 3, 1, 'migrate', '2026-06-11 20:30:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974257', '公告集合', '公告集合', '/notice/list', 0, '1220343202867974253', '0,1220343202867975002,1220343202867974253,1220343202867974257', 'system:notice:select', 2, '', 4, 1, 'migrate', '2026-06-11 20:30:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974258', '公告发布状态修改', '公告发布状态修改', '/notice/updateEnabled', 0, '1220343202867974253', '0,1220343202867975002,1220343202867974253,1220343202867974258', 'system:notice:updateEnabled', 2, '', 5, 1, 'migrate', '2026-06-11 20:30:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974259', '个人管理', '个人管理', '', 0, '0', '0,1220343202867974259', NULL, 0, 'layui-icon layui-icon-username', 9, 1, 'migrate', '2026-06-11 23:20:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974260', '公告列表', '公告列表', '/user/notice', 0, '1220343202867974259', '0,1220343202867974259,1220343202867974260', 'user:notice:view', 1, 'layui-icon layui-icon-list', 1, 1, 'migrate', '2026-06-11 23:20:00', '', '2026-06-11 15:38:03', 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974261', '个人资料', '个人资料', '/user/info', 0, '1220343202867974259', '0,1220343202867974259,1220343202867974261', 'user:info:view', 1, 'layui-icon layui-icon-username', 2, 1, 'migrate', '2026-06-11 23:20:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974262', '系统配置', '系统配置', '/config/listView', 0, '1220343202867975002', '0,1220343202867975002,1220343202867974262', 'system:config:view', 1, 'layui-icon layui-icon-set', 2, 1, 'migrate', '2026-06-19 00:00:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974263', '基础配置', '基础配置', '/config/group', 0, '1220343202867974262', '0,1220343202867975002,1220343202867974262,1220343202867974263', 'system:config:system', 2, '', 1, 1, 'migrate', '2026-06-19 00:00:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974265', '上传配置', '上传配置', '/file/listView', 0, '1220343202867974280', '0,1220343202867975002,1220343202867974280,1220343202867974265', 'system:config:upload', 2, '', 9, 1, 'migrate', '2026-06-19 00:00:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974266', '日志配置', '日志配置', '/operateLog/listView', 0, '1220343202867974238', '0,1220343202867975003,1220343202867974238,1220343202867974266', 'system:config:log', 2, '', 2, 1, 'migrate', '2026-06-19 00:00:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974267', '配置维护', '配置维护', '/config/list', 0, '1220343202867974262', '0,1220343202867975002,1220343202867974262,1220343202867974267', 'system:config:maintain', 2, '', 5, 1, 'migrate', '2026-06-19 00:00:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974268', '配置添加', '配置添加', '/config/add', 0, '1220343202867974262', '0,1220343202867975002,1220343202867974262,1220343202867974268', 'system:config:insert', 2, '', 6, 1, 'migrate', '2026-06-19 00:00:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974269', '配置删除', '配置删除', '/config/delete', 0, '1220343202867974262', '0,1220343202867975002,1220343202867974262,1220343202867974269', 'system:config:delete', 2, '', 7, 1, 'migrate', '2026-06-19 00:00:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974270', '配置修改', '配置修改', '/config/update', 0, '1220343202867974262', '0,1220343202867975002,1220343202867974262,1220343202867974270', 'system:config:update', 2, '', 8, 1, 'migrate', '2026-06-19 00:00:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974271', '配置状态修改', '配置状态修改', '/config/updateEnabled', 0, '1220343202867974262', '0,1220343202867975002,1220343202867974262,1220343202867974271', 'system:config:updateEnabled', 2, '', 9, 1, 'migrate', '2026-06-19 00:00:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974280', '文件管理', '文件管理', '/file/listView', 0, '1220343202867975002', '0,1220343202867975002,1220343202867974280', 'system:file:view', 1, 'layui-icon layui-icon-file', 4, 1, 'migrate', '2026-07-19 00:00:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974281', '文件集合', '文件集合', '/file/list', 0, '1220343202867974280', '0,1220343202867975002,1220343202867974280,1220343202867974281', 'system:file:select', 2, '', 1, 1, 'migrate', '2026-07-19 00:00:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974282', '目录树', '目录树', '/folder/tree', 0, '1220343202867974280', '0,1220343202867975002,1220343202867974280,1220343202867974282', 'system:file:tree', 2, '', 2, 1, 'migrate', '2026-07-19 00:00:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974283', '目录添加', '目录添加', '/folder/add', 0, '1220343202867974280', '0,1220343202867975002,1220343202867974280,1220343202867974283', 'system:file:folderInsert', 2, '', 3, 1, 'migrate', '2026-07-19 00:00:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974284', '目录修改', '目录修改', '/folder/update', 0, '1220343202867974280', '0,1220343202867975002,1220343202867974280,1220343202867974284', 'system:file:folderUpdate', 2, '', 4, 1, 'migrate', '2026-07-19 00:00:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974285', '目录删除', '目录删除', '/folder/delete', 0, '1220343202867974280', '0,1220343202867975002,1220343202867974280,1220343202867974285', 'system:file:folderDelete', 2, '', 5, 1, 'migrate', '2026-07-19 00:00:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974286', '文件修改', '文件修改', '/file/update', 0, '1220343202867974280', '0,1220343202867975002,1220343202867974280,1220343202867974286', 'system:file:update', 2, '', 6, 1, 'migrate', '2026-07-19 00:00:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974287', '文件删除', '文件删除', '/file/delete', 0, '1220343202867974280', '0,1220343202867975002,1220343202867974280,1220343202867974287', 'system:file:delete', 2, '', 7, 1, 'migrate', '2026-07-19 00:00:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974290', '用户解锁', '用户解锁', '/user/unlock', 0, '1220343202867974191', '0,1220343202867975001,1220343202867974191,1220343202867974290', 'system:user:unlock', 2, '', 7, 1, 'migrate', '2026-07-19 18:09:49', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974291', '安全配置', '安全配置', '/user/listView', 0, '1220343202867974191', '0,1220343202867975001,1220343202867974191,1220343202867974291', 'system:config:security', 2, '', 8, 1, 'migrate', '2026-07-19 23:07:48', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974292', '用户权限详情', '查看用户最终权限', '/user/perYLmaoetail', 0, '1220343202867974191', '0,1220343202867975001,1220343202867974191,1220343202867974292', 'system:user:perYLmaoetail', 2, '', 9, 1, 'migrate', '2026-07-20 23:56:20', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974293', '公告控制台', '查看公告投递与阅读情况', '/notice/consoleView', 0, '1220343202867974253', '0,1220343202867975002,1220343202867974253,1220343202867974293', 'system:notice:console', 2, '', 6, 1, 'migrate', '2026-07-22 21:50:27', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974294', '定时任务', '定时任务', '/job/listView', 0, '1220343202867975003', '0,1220343202867975003,1220343202867974294', 'system:job:view', 1, 'layui-icon layui-icon-time', 3, 1, 'migrate', '2026-07-22 23:10:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974295', '任务集合', '任务集合', '/job/list', 0, '1220343202867974294', '0,1220343202867975003,1220343202867974294,1220343202867974295', 'system:job:select', 2, '', 1, 1, 'migrate', '2026-07-22 23:10:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974296', '任务启停', '任务启停', '/job/updateEnabled', 0, '1220343202867974294', '0,1220343202867975003,1220343202867974294,1220343202867974296', 'system:job:updateEnabled', 2, '', 2, 1, 'migrate', '2026-07-22 23:10:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974297', '手动执行', '手动执行', '/job/run', 0, '1220343202867974294', '0,1220343202867975003,1220343202867974294,1220343202867974297', 'system:job:run', 2, '', 3, 1, 'migrate', '2026-07-22 23:10:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974298', '执行日志', '执行日志', '/job/logList', 0, '1220343202867974294', '0,1220343202867975003,1220343202867974294,1220343202867974298', 'system:job:log', 2, '', 4, 1, 'migrate', '2026-07-22 23:10:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974299', '任务配置', '任务配置', '/job/listView', 0, '1220343202867974294', '0,1220343202867975003,1220343202867974294,1220343202867974299', 'system:config:job', 2, '', 5, 1, 'migrate', '2026-08-04 23:17:14', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974300', '访问控制', '访问控制', '/filter/listView', 0, '1220343202867975003', '0,1220343202867975003,1220343202867974300', 'system:filter:view', 1, 'layui-icon layui-icon-auz', 2, 1, 'migrate', '2026-07-19 19:50:35', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974301', '访问控制集合', '访问控制集合', '/filter/list', 0, '1220343202867974300', '0,1220343202867975003,1220343202867974300,1220343202867974301', 'system:filter:select', 2, '', 1, 1, 'migrate', '2026-07-19 19:50:35', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974302', '访问控制添加', '访问控制添加', '/filter/add', 0, '1220343202867974300', '0,1220343202867975003,1220343202867974300,1220343202867974302', 'system:filter:insert', 2, '', 2, 1, 'migrate', '2026-07-19 19:50:35', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974303', '访问控制修改', '访问控制修改', '/filter/update', 0, '1220343202867974300', '0,1220343202867975003,1220343202867974300,1220343202867974303', 'system:filter:update', 2, '', 3, 1, 'migrate', '2026-07-19 19:50:35', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974304', '访问控制删除', '访问控制删除', '/filter/delete', 0, '1220343202867974300', '0,1220343202867975003,1220343202867974300,1220343202867974304', 'system:filter:delete', 2, '', 4, 1, 'migrate', '2026-07-19 19:50:35', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974305', '访问控制状态修改', '访问控制状态修改', '/filter/updateEnabled', 0, '1220343202867974300', '0,1220343202867975003,1220343202867974300,1220343202867974305', 'system:filter:updateEnabled', 2, '', 5, 1, 'migrate', '2026-07-19 19:50:35', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974306', '在线用户', '在线用户', '/online/listView', 0, '1220343202867975003', '0,1220343202867975003,1220343202867974306', 'system:online:view', 1, 'layui-icon layui-icon-user', 4, 1, 'migrate', '2026-08-05 13:50:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974307', '在线用户集合', '在线用户集合', '/online/list', 0, '1220343202867974306', '0,1220343202867975003,1220343202867974306,1220343202867974307', 'system:online:select', 2, '', 1, 1, 'migrate', '2026-08-05 13:50:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974308', '在线用户强退', '在线用户强退', '/online/kick', 0, '1220343202867974306', '0,1220343202867975003,1220343202867974306,1220343202867974308', 'system:online:kick', 2, '', 2, 1, 'migrate', '2026-08-05 13:50:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974309', '用户导出', '导出用户列表', '/user/export', 0, '1220343202867974191', '0,1220343202867975001,1220343202867974191,1220343202867974309', 'system:user:export', 2, '', 10, 1, 'migrate', '2026-08-05 15:30:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867974310', '公告配置', '公告配置', '/notice/listView', 0, '1220343202867974253', '0,1220343202867975002,1220343202867974253,1220343202867974310', 'system:config:notice', 2, '', 7, 1, 'migrate', '2026-08-25 00:00:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867975001', '组织权限', '组织与权限管理', '', 0, '0', '0,1220343202867975001', NULL, 0, 'layui-icon layui-icon-group', 2, 1, 'manual', '2026-08-26 17:01:34', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867975002', '系统设置', '系统基础配置', '', 0, '0', '0,1220343202867975002', NULL, 0, 'layui-icon layui-icon-set-fill', 3, 1, 'manual', '2026-08-26 17:01:34', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867975003', '安全运维', '安全与运维', '', 0, '0', '0,1220343202867975003', NULL, 0, 'layui-icon layui-icon-auz', 4, 1, 'manual', '2026-08-26 17:01:34', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867975100', '开发工具', '开发工具', '', 0, '0', '0,1220343202867975100', NULL, 0, 'layui-icon layui-icon-util', 90, 1, 'manual', '2026-08-26 20:30:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867975101', '代码生成', '代码生成', '/gen/listView', 0, '1220343202867975100', '0,1220343202867975100,1220343202867975101', 'system:gen:view', 1, 'layui-icon layui-icon-code-circle', 1, 1, 'manual', '2026-08-26 20:30:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867975102', '代码生成下载', '代码生成 ZIP 下载', '/gen/download', 0, '1220343202867975101', '0,1220343202867975100,1220343202867975101,1220343202867975102', 'system:gen:download', 2, '', 1, 1, 'manual', '2026-08-26 20:30:00', '', NULL, 0);
INSERT INTO `sys_perm` VALUES ('1220343202867975103', '接口文档', '接口文档', '/apidoc/listView', 0, '1220343202867975100', '0,1220343202867975100,1220343202867975103', 'system:apidoc:view', 1, 'layui-icon layui-icon-read', 2, 1, 'manual', '2026-08-26 22:50:00', '', NULL, 0);

-- ----------------------------
-- Table structure for sys_perm_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_perm_role`;
CREATE TABLE `sys_perm_role`  (
  `perm_role_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色权限ID（雪花）',
  `role_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色ID',
  `perm_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`perm_role_id`) USING BTREE,
  UNIQUE INDEX `uk_role_perm`(`role_id` ASC, `perm_id` ASC) USING BTREE,
  INDEX `idx_perm_id`(`perm_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色权限关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_perm_role
-- ----------------------------
INSERT INTO `sys_perm_role` VALUES ('2092790387260362754', '488243256161730560', '1220343202867974145', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387268751361', '488243256161730560', '1220343202867974146', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387272945665', '488243256161730560', '1220343202867974259', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387277139970', '488243256161730560', '1220343202867974260', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387281334274', '488243256161730560', '1220343202867974261', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387285528577', '488243256161730560', '1220343202867975001', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387298111490', '488243256161730560', '1220343202867974191', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387302305793', '488243256161730560', '1220343202867974192', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387310694402', '488243256161730560', '1220343202867974193', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387310694403', '488243256161730560', '1220343202867974194', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387319083009', '488243256161730560', '1220343202867974195', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387319083010', '488243256161730560', '1220343202867974196', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387331665922', '488243256161730560', '1220343202867974197', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387335860226', '488243256161730560', '1220343202867974290', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387340054530', '488243256161730560', '1220343202867974291', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387344248834', '488243256161730560', '1220343202867974292', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387348443137', '488243256161730560', '1220343202867974309', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387348443138', '488243256161730560', '1220343202867974198', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387356831746', '488243256161730560', '1220343202867974199', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387365220354', '488243256161730560', '1220343202867974200', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387369414658', '488243256161730560', '1220343202867974201', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387369414659', '488243256161730560', '1220343202867974202', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387377803265', '488243256161730560', '1220343202867974203', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387381997570', '488243256161730560', '1220343202867974204', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387381997571', '488243256161730560', '1220343202867974205', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387390386177', '488243256161730560', '1220343202867974206', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387394580481', '488243256161730560', '1220343202867974207', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387402969090', '488243256161730560', '1220343202867974208', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387402969091', '488243256161730560', '1220343202867974209', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387411357698', '488243256161730560', '1220343202867974210', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387415552001', '488243256161730560', '1220343202867974211', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387419746305', '488243256161730560', '1220343202867974212', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387423940610', '488243256161730560', '1220343202867974213', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387423940611', '488243256161730560', '1220343202867974214', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387428134914', '488243256161730560', '1220343202867974215', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387432329218', '488243256161730560', '1220343202867974216', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387436523522', '488243256161730560', '1220343202867974217', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387436523523', '488243256161730560', '1220343202867974218', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387440717825', '488243256161730560', '1220343202867974219', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387444912130', '488243256161730560', '1220343202867974220', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387453300738', '488243256161730560', '1220343202867974221', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387453300739', '488243256161730560', '1220343202867974222', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387461689345', '488243256161730560', '1220343202867974223', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387465883650', '488243256161730560', '1220343202867974224', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387465883651', '488243256161730560', '1220343202867974225', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387474272257', '488243256161730560', '1220343202867975002', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387474272258', '488243256161730560', '1220343202867974226', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387482660865', '488243256161730560', '1220343202867974227', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387486855170', '488243256161730560', '1220343202867974228', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387491049473', '488243256161730560', '1220343202867974229', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387495243778', '488243256161730560', '1220343202867974230', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387499438082', '488243256161730560', '1220343202867974231', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387507826689', '488243256161730560', '1220343202867974232', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387512020994', '488243256161730560', '1220343202867974233', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387512020995', '488243256161730560', '1220343202867974234', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387516215297', '488243256161730560', '1220343202867974235', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387520409602', '488243256161730560', '1220343202867974236', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387524603905', '488243256161730560', '1220343202867974237', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387532992513', '488243256161730560', '1220343202867974253', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387537186818', '488243256161730560', '1220343202867974254', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387541381122', '488243256161730560', '1220343202867974255', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387541381123', '488243256161730560', '1220343202867974256', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387541381124', '488243256161730560', '1220343202867974257', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387549769729', '488243256161730560', '1220343202867974258', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387553964033', '488243256161730560', '1220343202867974293', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387553964034', '488243256161730560', '1220343202867974310', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387562352642', '488243256161730560', '1220343202867974262', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387566546945', '488243256161730560', '1220343202867974263', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387566546946', '488243256161730560', '1220343202867974267', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387566546947', '488243256161730560', '1220343202867974268', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387574935553', '488243256161730560', '1220343202867974269', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387579129858', '488243256161730560', '1220343202867974270', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387587518466', '488243256161730560', '1220343202867974271', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387587518467', '488243256161730560', '1220343202867974280', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387595907073', '488243256161730560', '1220343202867974265', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387595907074', '488243256161730560', '1220343202867974281', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387604295682', '488243256161730560', '1220343202867974282', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387604295683', '488243256161730560', '1220343202867974283', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387612684290', '488243256161730560', '1220343202867974284', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387612684291', '488243256161730560', '1220343202867974285', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387621072898', '488243256161730560', '1220343202867974286', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387621072899', '488243256161730560', '1220343202867974287', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387629461505', '488243256161730560', '1220343202867975003', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387629461506', '488243256161730560', '1220343202867974238', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387637850114', '488243256161730560', '1220343202867974239', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387642044418', '488243256161730560', '1220343202867974266', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387650433026', '488243256161730560', '1220343202867974294', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387654627329', '488243256161730560', '1220343202867974295', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387654627330', '488243256161730560', '1220343202867974296', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387663015937', '488243256161730560', '1220343202867974297', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387663015938', '488243256161730560', '1220343202867974298', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387671404545', '488243256161730560', '1220343202867974299', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387671404546', '488243256161730560', '1220343202867974300', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387679793153', '488243256161730560', '1220343202867974301', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387688181761', '488243256161730560', '1220343202867974302', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387692376066', '488243256161730560', '1220343202867974303', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387696570370', '488243256161730560', '1220343202867974304', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387696570371', '488243256161730560', '1220343202867974305', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387709153282', '488243256161730560', '1220343202867974306', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387717541889', '488243256161730560', '1220343202867974307', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387717541890', '488243256161730560', '1220343202867974308', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387717541891', '488243256161730560', '1220343202867975100', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387725930497', '488243256161730560', '1220343202867975101', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387734319105', '488243256161730560', '1220343202867975102', '1662038524471218177', '2026-08-27 09:44:45');
INSERT INTO `sys_perm_role` VALUES ('2092790387738513409', '488243256161730560', '1220343202867975103', '1662038524471218177', '2026-08-27 09:44:45');

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `permission_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'id',
  `permission_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限名称',
  `permission_describe` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限描述',
  `permission_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '授权链接',
  `is_blank` int NULL DEFAULT 0 COMMENT '是否跳转 0 不跳转 1跳转',
  `parent_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '父节点id',
  `perms` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `type` int NULL DEFAULT NULL COMMENT '类型   0：目录   1：菜单   2：按钮',
  `icon` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `order_num` int NULL DEFAULT NULL COMMENT '排序',
  `is_visible` int NULL DEFAULT NULL COMMENT '是否可见 0：可见   1：不可见',
  PRIMARY KEY (`permission_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES ('10', '角色集合', '角色集合', '/role/roleList', 0, '9', 'system:role:select', 2, '', 4, 0);
INSERT INTO `sys_permission` VALUES ('11', '角色添加', '角色添加', '/role/roleInsert', 0, '9', 'system:role:insert', 2, 'entypo-plus-squared', 1, 0);
INSERT INTO `sys_permission` VALUES ('12', '角色删除', '角色删除', '/role/roleDelete', 0, '9', 'system:role:delete', 2, 'entypo-trash', 2, 0);
INSERT INTO `sys_permission` VALUES ('13', '角色修改', '角色修改', '/role/roleUpdate', 0, '9', 'system:role:update', 2, 'fa fa-wrench', 3, 0);
INSERT INTO `sys_permission` VALUES ('14', '权限展示', '权限展示', '/permission/permissionView', 0, '411522822607867904', 'system:permission:view', 1, 'fa fa-key', 3, 0);
INSERT INTO `sys_permission` VALUES ('15', '权限集合', '权限集合', '/permission/permissionList', 0, '14', 'system:permission:select', 2, '', 4, 0);
INSERT INTO `sys_permission` VALUES ('16', '权限添加', '权限添加', '/permission/permissionInsert', 0, '14', 'system:permission:insert', 2, 'entypo-plus-squared', 1, 0);
INSERT INTO `sys_permission` VALUES ('17', '权限删除', '权限删除', '/permission/permissionDelete', 0, '14', 'system:permission:delete', 2, 'entypo-trash', 2, 0);
INSERT INTO `sys_permission` VALUES ('18', '权限修改', '权限修改', '/permission/permissionUpdate', 0, '14', 'system:permission:update', 2, 'fa fa-wrench', 3, 0);
INSERT INTO `sys_permission` VALUES ('330365026642825216', '公告管理', '公告展示', '/notice/noticeView', 0, '592059865673760768', 'system:notice:view', 1, 'layui-icon layui-icon fa fa-telegram', 2, 0);
INSERT INTO `sys_permission` VALUES ('3303650266428252171', '公告集合', '公告集合', '/notice/noticeList', 0, '330365026642825216', 'system:notice:select', 2, '', 4, 0);
INSERT INTO `sys_permission` VALUES ('3303650266428252182', '公告添加', '公告添加', '/notice/noticeInsert', 0, '330365026642825216', 'system:notice:insert', 2, 'entypo-plus-squared', 1, 0);
INSERT INTO `sys_permission` VALUES ('3303650266428252193', '公告删除', '公告删除', '/notice/noticeDelete', 0, '330365026642825216', 'system:notice:delete', 2, 'entypo-trash', 2, 0);
INSERT INTO `sys_permission` VALUES ('3303650266428252204', '公告修改', '公告修改', '/notice/noticeUpdate', 0, '330365026642825216', 'system:notice:update', 2, 'fa fa-wrench', 3, 0);
INSERT INTO `sys_permission` VALUES ('4', '用户管理', '用户展示', '/user/userView', 0, '411522822607867904', 'system:user:view', 1, 'layui-icon icon icon-user', 1, 0);
INSERT INTO `sys_permission` VALUES ('410791701859405824', '岗位管理', '岗位展示', '/position/positionView', 0, '411522822607867904', 'system:position:view', 1, 'fa fa-vcard', 17, 0);
INSERT INTO `sys_permission` VALUES ('4107917018594058251', '岗位表集合', '岗位集合', '/position/positionList', 0, '410791701859405824', 'system:position:select', 2, '', 4, 0);
INSERT INTO `sys_permission` VALUES ('4107917018594058262', '岗位表添加', '岗位添加', '/position/positionInsert', 0, '410791701859405824', 'system:position:insert', 2, 'entypo-plus-squared', 1, 0);
INSERT INTO `sys_permission` VALUES ('4107917018594058273', '岗位表删除', '岗位删除', '/position/positionDelete', 0, '410791701859405824', 'system:position:delete', 2, 'entypo-trash', 2, 0);
INSERT INTO `sys_permission` VALUES ('4107917018594058284', '岗位表修改', '岗位修改', '/position/positionUpdate', 0, '410791701859405824', 'system:position:update', 2, 'fa fa-wrench', 3, 0);
INSERT INTO `sys_permission` VALUES ('410989805699207168', '部门管理', '部门展示', '/department/departmentView', 0, '411522822607867904', 'system:department:view', 1, 'fa fa-odnoklassniki', 18, 0);
INSERT INTO `sys_permission` VALUES ('4109898056992071691', '部门集合', '部门集合', '/department/departmentList', 0, '410989805699207168', 'system:department:select', 2, '', 4, 0);
INSERT INTO `sys_permission` VALUES ('4109898056992071702', '部门添加', '部门添加', '/department/departmentInsert', 0, '410989805699207168', 'system:department:insert', 2, 'entypo-plus-squared', 1, 0);
INSERT INTO `sys_permission` VALUES ('4109898056992071713', '部门删除', '部门删除', '/department/departmentDelete', 0, '410989805699207168', 'system:department:delete', 2, 'entypo-trash', 2, 0);
INSERT INTO `sys_permission` VALUES ('4109898056992071724', '部门修改', '部门修改', '/department/departmentUpdate', 0, '410989805699207168', 'system:department:update', 2, 'fa fa-wrench', 3, 0);
INSERT INTO `sys_permission` VALUES ('411522822607867904', '用户管理', NULL, '', 0, '0', '', 0, 'layui-icon layui-icon layui-icon-user', 1, 0);
INSERT INTO `sys_permission` VALUES ('486690002869157888', '用户密码修改', '用户密码修改', '', 0, '4', 'system:user:updatePwd', 2, 'layui-icon layui-icon entypo-tools', 4, 0);
INSERT INTO `sys_permission` VALUES ('496126970468237312', '日志展示', '日志管理', '/operateLog/operateLogView', 0, '592059865673760768', 'system:operateLog:view', 1, 'layui-icon layui-icon fa fa-info', 1, 0);
INSERT INTO `sys_permission` VALUES ('496127240363311104', '日志删除', '日志删除', '/operateLog/operateLogDelete', 0, '496126970468237312', 'system:operateLog:delete', 2, 'entypo-trash', 1, 0);
INSERT INTO `sys_permission` VALUES ('496127794879660032', '日志集合', '日志集合', '/operateLog/operateLogList', 0, '496126970468237312', 'system:operateLog:select', 2, NULL, 2, 0);
INSERT INTO `sys_permission` VALUES ('5', '用户集合', '用户集合', '', 0, '4', 'system:user:select', 2, 'layui-icon layui-icon layui-icon ', 5, 0);
INSERT INTO `sys_permission` VALUES ('592059865673760768', '系统管理', NULL, '', 0, '0', '', 0, 'layui-icon layui-icon layui-icon layui-icon-home', 9, 0);
INSERT INTO `sys_permission` VALUES ('6', '用户添加', '用户添加', '', 0, '4', 'system:user:insert', 2, 'layui-icon layui-icon entypo-plus-squared', 1, 0);
INSERT INTO `sys_permission` VALUES ('618918631769636864', '字典管理', '字典类型表展示', '/dictType/dictTypeView', 0, '592059865673760768', 'system:dictType:view', 1, 'layui-icon layui-icon fa fa-puzzle-piece', 3, 0);
INSERT INTO `sys_permission` VALUES ('6189186317738311681', '字典类型集合', '字典类型表集合', '/dictType/dictTypeList', 0, '618918631769636864', 'system:dictType:select', 2, NULL, 4, 0);
INSERT INTO `sys_permission` VALUES ('6189186317948026882', '字典类型添加', '字典类型表添加', '/dictType/dictTypeInsert', 0, '618918631769636864', 'system:dictType:insert', 2, NULL, 1, 0);
INSERT INTO `sys_permission` VALUES ('6189186317948026883', '字典类型删除', '字典类型表删除', '/dictType/dictTypeDelete', 0, '618918631769636864', 'system:dictType:delete', 2, NULL, 2, 0);
INSERT INTO `sys_permission` VALUES ('6189186317989969924', '字典类型修改', '字典类型表修改', '/dictType/dictTypeUpdate', 0, '618918631769636864', 'system:dictType:update', 2, NULL, 3, 0);
INSERT INTO `sys_permission` VALUES ('6192095214866268161', '字典数据集合', '字典数据集合', '/dictData/dictDataList', 0, '619836559427895296', 'system:dictData:select', 2, 'layui-icon ', 4, 0);
INSERT INTO `sys_permission` VALUES ('6192095214866268162', '字典数据添加', '字典数据添加', '/dictData/dictDataInsert', 0, '619836559427895296', 'system:dictData:insert', 2, 'layui-icon ', 1, 0);
INSERT INTO `sys_permission` VALUES ('6192095215075983363', '字典数据删除', '字典数据删除', '/dictData/dictDataDelete', 0, '619836559427895296', 'system:dictData:delete', 2, 'layui-icon ', 2, 0);
INSERT INTO `sys_permission` VALUES ('6192095215075983364', '字典数据修改', '字典数据修改', '/dictData/dictDataUpdate', 0, '619836559427895296', 'system:dictData:update', 2, 'layui-icon ', 3, 0);
INSERT INTO `sys_permission` VALUES ('619836559427895296', '字典数据管理', '字典数据管理', '/dictData/dictDataView', 0, '592059865673760768', 'system:dictData:view', 2, 'layui-icon ', 4, 0);
INSERT INTO `sys_permission` VALUES ('7', '用户删除', '用户删除', '', 0, '4', 'system:user:delete', 2, 'layui-icon layui-icon entypo-trash', 2, 0);
INSERT INTO `sys_permission` VALUES ('8', '用户修改', '用户修改', '', 0, '4', 'system:user:update', 2, 'layui-icon layui-icon fa fa-wrench', 3, 0);
INSERT INTO `sys_permission` VALUES ('9', '角色管理', '角色展示', '/role/roleView', 0, '411522822607867904', 'system:role:view', 1, 'layui-icon fa fa-group', 2, 0);
INSERT INTO `sys_permission` VALUES ('908276167033884672', '权限展示', NULL, '', 0, '9', 'system:role:tree', 2, 'layui-icon ', 5, 0);

-- ----------------------------
-- Table structure for sys_permission_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission_role`;
CREATE TABLE `sys_permission_role`  (
  `permission_role_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `role_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色id',
  `permission_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限id',
  PRIMARY KEY (`permission_role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色权限中间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_permission_role
-- ----------------------------
INSERT INTO `sys_permission_role` VALUES ('909838379225583616', '488243256161730560', '411522822607867904');
INSERT INTO `sys_permission_role` VALUES ('909838379334635520', '488243256161730560', '14');
INSERT INTO `sys_permission_role` VALUES ('909838379435298816', '488243256161730560', '15');
INSERT INTO `sys_permission_role` VALUES ('909838379582099456', '488243256161730560', '16');
INSERT INTO `sys_permission_role` VALUES ('909838379686957056', '488243256161730560', '17');
INSERT INTO `sys_permission_role` VALUES ('909838379791814656', '488243256161730560', '18');
INSERT INTO `sys_permission_role` VALUES ('909838379896672256', '488243256161730560', '4');
INSERT INTO `sys_permission_role` VALUES ('909838380005724160', '488243256161730560', '486690002869157888');
INSERT INTO `sys_permission_role` VALUES ('909838380114776064', '488243256161730560', '5');
INSERT INTO `sys_permission_role` VALUES ('909838380219633664', '488243256161730560', '6');
INSERT INTO `sys_permission_role` VALUES ('909838380324491264', '488243256161730560', '7');
INSERT INTO `sys_permission_role` VALUES ('909838380433543168', '488243256161730560', '8');
INSERT INTO `sys_permission_role` VALUES ('909838380538400768', '488243256161730560', '410791701859405824');
INSERT INTO `sys_permission_role` VALUES ('909838380639064064', '488243256161730560', '4107917018594058251');
INSERT INTO `sys_permission_role` VALUES ('909838380743921664', '488243256161730560', '4107917018594058262');
INSERT INTO `sys_permission_role` VALUES ('909838380852973568', '488243256161730560', '4107917018594058273');
INSERT INTO `sys_permission_role` VALUES ('909838380953636864', '488243256161730560', '4107917018594058284');
INSERT INTO `sys_permission_role` VALUES ('909838381054300160', '488243256161730560', '410989805699207168');
INSERT INTO `sys_permission_role` VALUES ('909838381159157760', '488243256161730560', '4109898056992071691');
INSERT INTO `sys_permission_role` VALUES ('909838381264015360', '488243256161730560', '4109898056992071702');
INSERT INTO `sys_permission_role` VALUES ('909838381364678656', '488243256161730560', '4109898056992071713');
INSERT INTO `sys_permission_role` VALUES ('909838381469536256', '488243256161730560', '4109898056992071724');
INSERT INTO `sys_permission_role` VALUES ('909838381570199552', '488243256161730560', '9');
INSERT INTO `sys_permission_role` VALUES ('909838381670862848', '488243256161730560', '10');
INSERT INTO `sys_permission_role` VALUES ('909838381771526144', '488243256161730560', '11');
INSERT INTO `sys_permission_role` VALUES ('909838381876383744', '488243256161730560', '12');
INSERT INTO `sys_permission_role` VALUES ('909838381977047040', '488243256161730560', '13');
INSERT INTO `sys_permission_role` VALUES ('909838382081904640', '488243256161730560', '908276167033884672');
INSERT INTO `sys_permission_role` VALUES ('909838382186762240', '488243256161730560', '592059865673760768');
INSERT INTO `sys_permission_role` VALUES ('909838382287425536', '488243256161730560', '330365026642825216');
INSERT INTO `sys_permission_role` VALUES ('909838382388088832', '488243256161730560', '3303650266428252171');
INSERT INTO `sys_permission_role` VALUES ('909838382488752128', '488243256161730560', '3303650266428252182');
INSERT INTO `sys_permission_role` VALUES ('909838382593609728', '488243256161730560', '3303650266428252193');
INSERT INTO `sys_permission_role` VALUES ('909838382694273024', '488243256161730560', '3303650266428252204');
INSERT INTO `sys_permission_role` VALUES ('909838382799130624', '488243256161730560', '496126970468237312');
INSERT INTO `sys_permission_role` VALUES ('909838382903988224', '488243256161730560', '496127240363311104');
INSERT INTO `sys_permission_role` VALUES ('909838383004651520', '488243256161730560', '496127794879660032');
INSERT INTO `sys_permission_role` VALUES ('909838383105314816', '488243256161730560', '618918631769636864');
INSERT INTO `sys_permission_role` VALUES ('909838383210172416', '488243256161730560', '6189186317738311681');
INSERT INTO `sys_permission_role` VALUES ('909838383310835712', '488243256161730560', '6189186317948026882');
INSERT INTO `sys_permission_role` VALUES ('909838383411499008', '488243256161730560', '6189186317948026883');
INSERT INTO `sys_permission_role` VALUES ('909838383512162304', '488243256161730560', '6189186317989969924');
INSERT INTO `sys_permission_role` VALUES ('909838383612825600', '488243256161730560', '619836559427895296');
INSERT INTO `sys_permission_role` VALUES ('909838383717683200', '488243256161730560', '6192095214866268161');
INSERT INTO `sys_permission_role` VALUES ('909838383818346496', '488243256161730560', '6192095214866268162');
INSERT INTO `sys_permission_role` VALUES ('909838383923204096', '488243256161730560', '6192095215075983363');
INSERT INTO `sys_permission_role` VALUES ('909838384019673088', '488243256161730560', '6192095215075983364');

-- ----------------------------
-- Table structure for sys_position
-- ----------------------------
DROP TABLE IF EXISTS `sys_position`;
CREATE TABLE `sys_position`  (
  `position_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位id',
  `position_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
  `order_num` int NULL DEFAULT NULL COMMENT '排序',
  `status` int NULL DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`position_id`, `position_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_position
-- ----------------------------
INSERT INTO `sys_position` VALUES ('410792368778907648', '总经理', 1, 1);
INSERT INTO `sys_position` VALUES ('410792443127140352', '技术经理', 2, 1);
INSERT INTO `sys_position` VALUES ('410792478929719296', '人事经理', 3, 1);
INSERT INTO `sys_position` VALUES ('411477874382606336', '员工', 4, 1);

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `post_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位ID（雪花）',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '岗位编码',
  `post_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位名称',
  `post_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '1管理岗 2技术岗 3运营岗 4市场岗',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用（1启用 0停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`post_id`) USING BTREE,
  UNIQUE INDEX `uk_post_code`(`post_code` ASC) USING BTREE,
  UNIQUE INDEX `uk_post_name`(`post_name` ASC) USING BTREE,
  INDEX `idx_post_enabled_order`(`is_enabled` ASC, `order_num` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '岗位表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES ('410792368778907648', 'POST_78907648', '总经理', 1, 1, 1, 'migrate', '2026-05-29 23:28:04', '1662038524471218177', '2026-06-20 16:25:02', 0);
INSERT INTO `sys_post` VALUES ('410792443127140352', 'POST_27140352', '技术经理', 2, 2, 1, 'migrate', '2026-05-29 23:28:04', '', NULL, 0);
INSERT INTO `sys_post` VALUES ('410792478929719296', 'POST_29719296', '人事经理', 1, 3, 1, 'migrate', '2026-05-29 23:28:04', '', NULL, 0);
INSERT INTO `sys_post` VALUES ('411477874382606336', 'POST_82606336', '员工', 2, 4, 1, 'migrate', '2026-05-29 23:28:04', '1662038524471218177', '2026-06-09 01:46:45', 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID（雪花）',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色名称',
  `role_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色编码',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用（1启用 0停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0存在 1删除',
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE INDEX `uk_role_name`(`role_name` ASC) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('488243256161730560', '管理员', 'admin', 1, 1, '', '2026-06-18 23:41:36', '1662038524471218177', '2026-06-20 16:24:33', 0);

-- ----------------------------
-- Table structure for sys_role_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_user`;
CREATE TABLE `sys_role_user`  (
  `role_user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户角色ID（雪花）',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户ID',
  `role_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`role_user_id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `idx_role_user_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户角色中间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_user
-- ----------------------------
INSERT INTO `sys_role_user` VALUES ('1220945860842622976', '1220945860838428672', '488243256161730560', '', '2026-06-18 23:41:36');
INSERT INTO `sys_role_user` VALUES ('2068369327669002242', '1221663362941849600', '488243256161730560', '1662038524471218177', '2026-06-20 16:24:11');
INSERT INTO `sys_role_user` VALUES ('495571139645542400', '1662038524471218177', '488243256161730560', '', '2026-06-18 23:41:36');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID（雪花）',
  `user_account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `user_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户密码（bcrypt cost=12）',
  `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名称',
  `user_sex` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '性别：0-男，1-女',
  `user_email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `user_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `dept_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门id',
  `post_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位id',
  `user_avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像访问地址，如 /upload/{fileId}',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用（1启用 0停用）',
  `is_lock` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否锁定（1锁定 0正常）；与启停独立，登录失败满 5 次自动锁定',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0存在 1删除',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `uk_user_account`(`user_account` ASC) USING BTREE,
  INDEX `idx_user_dept_id`(`dept_id` ASC) USING BTREE,
  INDEX `idx_user_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_user_enabled`(`is_del` ASC, `is_enabled` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1220945860838428672', 'test01', '$2a$12$Nl6syrPkjvm/7uZE56XiRuPUky5L2waDu9D9wvhKZfvNeRLelG01m', '测试', '1', '111@111.com', '18513303314', '1', '410792368778907648', NULL, 1, 0, '', '2026-05-31 06:09:48', '1662038524471218177', '2026-07-19 18:13:19', 0);
INSERT INTO `sys_user` VALUES ('1221663362941849600', 'test02', '$2a$12$Ju3T7jpxXmQMOJFD9bsXTeJ63J4V/MqDtq/4.KDrpdGpBqgTrkx9O', '测试02', '1', 'sga@qq.com', '13199998888', '3', '410792443127140352', NULL, 1, 0, '', '2026-06-02 05:40:53', '1662038524471218177', '2026-08-05 13:58:09', 0);
INSERT INTO `sys_user` VALUES ('1662038524471218177', 'admin', '$2a$12$Nl6syrPkjvm/7uZE56XiRuPUky5L2waDu9D9wvhKZfvNeRLelG01m', 'admin', '0', 'admin', '18513303314', '2', '410792368778907648', NULL, 1, 0, NULL, '2023-05-26 10:10:38', NULL, '2026-05-31 06:14:03', 0);

SET FOREIGN_KEY_CHECKS = 1;
