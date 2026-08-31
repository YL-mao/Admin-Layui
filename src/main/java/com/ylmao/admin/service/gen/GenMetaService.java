package com.ylmao.admin.service.gen;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.dto.GenDto;
import com.ylmao.admin.entity.Perm;
import com.ylmao.admin.mapper.PermMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenMetaService {

    private static final Set<String> EXCLUDED_TABLES = Set.of("sys_operate_log");

    private final DataSource dataSource;
    private final PermMapper permMapper;

    public List<GenDto.TableItem> listTables() {
        try (Connection connection = dataSource.getConnection()) {
            String catalog = connection.getCatalog();
            String schema = resolveSchema(connection);
            List<GenDto.TableItem> tables = new ArrayList<>();
            try (ResultSet rs = connection.getMetaData().getTables(catalog, schema, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    if (tableName == null || EXCLUDED_TABLES.contains(tableName.toLowerCase(Locale.ROOT))) {
                        continue;
                    }
                    String remark = rs.getString("REMARKS");
                    if (remark == null || remark.isBlank()) {
                        remark = loadTableComment(connection, catalog, tableName);
                    }
                    tables.add(new GenDto.TableItem(tableName, remark == null ? "" : remark));
                }
            }
            tables.sort(Comparator.comparing(GenDto.TableItem::tableName));
            return tables;
        } catch (SQLException e) {
            throw new BusinessException("读取数据表列表失败");
        }
    }

    public List<GenColumnModel> loadColumnModels(String tableName) {
        if (tableName == null || tableName.isBlank()) {
            throw new BusinessException("表名不能为空");
        }
        try (Connection connection = dataSource.getConnection()) {
            String catalog = connection.getCatalog();
            String schema = resolveSchema(connection);
            Set<String> primaryKeys = loadPrimaryKeys(connection, catalog, schema, tableName);
            Map<String, String> comments = loadColumnComments(connection, catalog, tableName);
            List<GenColumnModel> columns = new ArrayList<>();
            try (ResultSet rs = connection.getMetaData().getColumns(catalog, schema, tableName, "%")) {
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    String typeName = rs.getString("TYPE_NAME");
                    int columnSize = rs.getInt("COLUMN_SIZE");
                    int nullable = rs.getInt("NULLABLE");
                    String remark = rs.getString("REMARKS");
                    if (remark == null || remark.isBlank()) {
                        remark = comments.getOrDefault(columnName, "");
                    }
                    columns.add(GenColumnModel.builder()
                            .columnName(columnName)
                            .fieldName(GenNaming.columnToFieldName(columnName))
                            .javaType(GenNaming.mapJavaType(typeName))
                            .jdbcTypeName(typeName)
                            .columnComment(remark)
                            .columnSize(columnSize)
                            .nullable(nullable == DatabaseMetaData.columnNullable)
                            .primaryKey(primaryKeys.contains(columnName))
                            .build());
                }
            }
            columns.sort(Comparator.comparingInt(c -> c.isPrimaryKey() ? 0 : 1));
            return columns;
        } catch (SQLException e) {
            throw new BusinessException("读取表字段失败");
        }
    }

    public List<GenDto.ColumnPreview> loadColumnPreview(String tableName) {
        String moduleName = GenNaming.tableToModuleName(tableName);
        return enrichForCodegen(moduleName, loadColumnModels(tableName)).stream()
                .map(c -> new GenDto.ColumnPreview(
                        c.getColumnName(),
                        c.getColumnComment(),
                        c.getJavaType(),
                        c.isPrimaryKey(),
                        c.isFormField(),
                        c.isListDisplayField(),
                        c.isListQueryField(),
                        c.isPrimaryKey() || c.isAuditColumn()))
                .toList();
    }

    /** 可选上级：目录与菜单（不含按钮），供代码生成挂载。 */
    public List<GenDto.ParentMenuItem> listParentMenuOptions() {
        LambdaQueryWrapper<Perm> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Perm::getPermType, 0, 1)
                .eq(Perm::getIsEnabled, 1)
                .orderByAsc(Perm::getOrderNum)
                .orderByAsc(Perm::getPermPath);
        return permMapper.selectList(wrapper).stream()
                .map(p -> new GenDto.ParentMenuItem(
                        p.getPermId(),
                        p.getParentId(),
                        p.getPermName(),
                        p.getPermPath(),
                        p.getPermType()))
                .toList();
    }

    /**
     * 按前端勾选覆盖 Java 类型、表单/展示/查询；主键与审计列不可进表单/查询，主键不可进展示。
     */
    public List<GenColumnModel> applyColumnOptions(List<GenColumnModel> columns, List<GenDto.ColumnOption> options) {
        if (options == null || options.isEmpty()) {
            return columns;
        }
        Map<String, GenDto.ColumnOption> optionMap = options.stream()
                .collect(Collectors.toMap(GenDto.ColumnOption::columnName, Function.identity(), (a, b) -> b, LinkedHashMap::new));
        List<GenColumnModel> result = new ArrayList<>(columns.size());
        for (GenColumnModel col : columns) {
            GenDto.ColumnOption option = optionMap.get(col.getColumnName());
            if (option == null) {
                result.add(col);
                continue;
            }
            String javaType;
            try {
                javaType = GenNaming.normalizeJavaType(option.javaType());
            } catch (IllegalArgumentException ex) {
                throw new BusinessException(ex.getMessage());
            }
            boolean locked = col.isPrimaryKey() || col.isAuditColumn();
            String comment = option.columnComment() == null || option.columnComment().isBlank()
                    ? col.getColumnComment()
                    : option.columnComment().trim();
            result.add(col.toBuilder()
                    .javaType(javaType)
                    .columnComment(comment)
                    .formField(!locked && Boolean.TRUE.equals(option.formField()))
                    .listQueryField(!locked && Boolean.TRUE.equals(option.listQueryField()))
                    .listDisplayField(!col.isPrimaryKey() && Boolean.TRUE.equals(option.listDisplayField()))
                    .build());
        }
        return result;
    }

    public String loadParentPermPath(String parentPermId) {
        Perm perm = permMapper.selectById(parentPermId);
        if (perm == null || perm.getPermPath() == null || perm.getPermPath().isBlank()) {
            throw new BusinessException("上级菜单权限不存在");
        }
        return perm.getPermPath();
    }

    /** 根据模块规则标记表单、查询、唯一校验等列属性；注释默认用库 COMMENT 原文。 */
    public List<GenColumnModel> enrichForCodegen(String moduleName, List<GenColumnModel> rawColumns) {
        String expectedNameCol = moduleName + "_name";
        String expectedCodeCol = moduleName + "_code";
        List<GenColumnModel> result = new ArrayList<>(rawColumns.size());
        for (GenColumnModel col : rawColumns) {
            boolean audit = GenNaming.isAuditColumn(col.getColumnName());
            String comment = (col.getColumnComment() == null || col.getColumnComment().isBlank())
                    ? col.getColumnName()
                    : col.getColumnComment().trim();
            result.add(col.toBuilder()
                    .columnComment(comment)
                    .auditColumn(audit)
                    .formField(!audit && !col.isPrimaryKey())
                    .listQueryField(col.getColumnName().equalsIgnoreCase(expectedNameCol)
                            || col.getColumnName().equalsIgnoreCase(expectedCodeCol))
                    // 审计列默认不进列表；生成前仍可在页面勾选展示。
                    .listDisplayField(!audit && !col.isPrimaryKey())
                    .nameField(col.getColumnName().equalsIgnoreCase(expectedNameCol))
                    .codeField(col.getColumnName().equalsIgnoreCase(expectedCodeCol))
                    .enabledField("is_enabled".equalsIgnoreCase(col.getColumnName()))
                    .orderNumField("order_num".equalsIgnoreCase(col.getColumnName()))
                    .build());
        }
        return result;
    }

    private Set<String> loadPrimaryKeys(Connection connection, String catalog, String schema, String tableName)
            throws SQLException {
        Set<String> keys = new HashSet<>();
        try (ResultSet rs = connection.getMetaData().getPrimaryKeys(catalog, schema, tableName)) {
            while (rs.next()) {
                keys.add(rs.getString("COLUMN_NAME"));
            }
        }
        return keys;
    }

    private String resolveSchema(Connection connection) throws SQLException {
        String schema = connection.getSchema();
        return (schema == null || schema.isBlank()) ? null : schema;
    }

    private String loadTableComment(Connection connection, String catalog, String tableName) throws SQLException {
        String sql = """
                SELECT TABLE_COMMENT FROM information_schema.TABLES
                WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?
                LIMIT 1
                """;
        try (var ps = connection.prepareStatement(sql)) {
            ps.setString(1, catalog);
            ps.setString(2, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("TABLE_COMMENT");
                }
            }
        }
        return "";
    }

    private Map<String, String> loadColumnComments(Connection connection, String catalog, String tableName)
            throws SQLException {
        Map<String, String> map = new LinkedHashMap<>();
        String sql = """
                SELECT COLUMN_NAME, COLUMN_COMMENT FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?
                ORDER BY ORDINAL_POSITION
                """;
        try (var ps = connection.prepareStatement(sql)) {
            ps.setString(1, catalog);
            ps.setString(2, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("COLUMN_NAME"), rs.getString("COLUMN_COMMENT"));
                }
            }
        }
        return map;
    }
}
