package com.ylmao.admin.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/** 代码生成接口的请求/响应契约。 */
public final class GenDto {

    private GenDto() {
    }

    /** 数据库表清单项，供前端下拉选择。 */
    public record TableItem(String tableName, String tableComment) {
    }

    /** 上级菜单下拉项（目录 / 菜单）。 */
    public record ParentMenuItem(
            String permId,
            String parentId,
            String permName,
            String permPath,
            Integer permType
    ) {
    }

    /** 列预览，供生成前确认并勾选字段映射。 */
    public record ColumnPreview(
            String columnName,
            String columnComment,
            String javaType,
            boolean primaryKey,
            boolean formField,
            /** 是否作为列表表格展示列。 */
            boolean listDisplayField,
            /** 是否作为列表页查询条件。 */
            boolean listQueryField,
            /** 主键与审计字段不可改表单/查询勾选；展示仅锁定主键。 */
            boolean optionLocked
    ) {
    }

    /** 前端勾选后回传的列覆盖项。 */
    public record ColumnOption(
            @NotBlank(message = "列名不能为空") String columnName,
            @Size(max = 100, message = "字段注释参数不合法") String columnComment,
            @NotBlank(message = "Java 类型不能为空") String javaType,
            @NotNull(message = "是否表单不能为空") Boolean formField,
            @NotNull(message = "是否列表展示不能为空") Boolean listDisplayField,
            @NotNull(message = "是否查询不能为空") Boolean listQueryField
    ) {
    }

    /** 代码生成请求体：仅内存渲染 ZIP，不写服务端磁盘。 */
    public record Generate(
            @NotBlank(message = "表名不能为空") String tableName,
            @NotBlank(message = "模块名不能为空") @Size(max = 64, message = "模块名参数不合法") String moduleName,
            @NotBlank(message = "类名不能为空") @Size(max = 64, message = "类名参数不合法") String className,
            @NotBlank(message = "业务名称不能为空") @Size(max = 64, message = "业务名称参数不合法") String functionName,
            @NotBlank(message = "权限前缀不能为空") @Size(max = 128, message = "权限前缀参数不合法") String permPrefix,
            @NotBlank(message = "上级菜单权限ID不能为空") String parentPermId,
            /** Controller / 模板目录段，如 demo 或 system。 */
            @NotBlank(message = "生成目录不能为空") @Size(max = 64, message = "生成目录参数不合法") String packagePath,
            @Size(max = 64, message = "作者参数不合法") String author,
            @NotEmpty(message = "字段配置不能为空") @Valid List<ColumnOption> columns
    ) {
    }
}
