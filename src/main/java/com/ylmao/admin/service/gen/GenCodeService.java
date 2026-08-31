package com.ylmao.admin.service.gen;

import cn.hutool.core.util.StrUtil;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.dto.GenDto;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class GenCodeService {

    private static final String ADMIN_ROLE_ID = "488243256161730560";

    private final GenMetaService genMetaService;
    private Configuration freemarkerConfig;
    private final AtomicLong idSeq = new AtomicLong(0);

    @PostConstruct
    void initFreemarker() {
        freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        freemarkerConfig.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "templates/gen");
        freemarkerConfig.setDefaultEncoding("UTF-8");
        freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        freemarkerConfig.setLocale(Locale.CHINA);
    }

    public byte[] generateZip(GenDto.Generate request) {
        validateGenerateRequest(request);
        List<GenColumnModel> columns = genMetaService.applyColumnOptions(
                genMetaService.enrichForCodegen(
                        request.moduleName().toLowerCase(Locale.ROOT),
                        genMetaService.loadColumnModels(request.tableName())),
                request.columns());
        GenColumnModel pkColumn = columns.stream().filter(GenColumnModel::isPrimaryKey).findFirst()
                .orElseThrow(() -> new BusinessException("表缺少主键，无法生成代码"));
        String parentPath = genMetaService.loadParentPermPath(request.parentPermId());
        GenCodegenModel model = buildModel(request, columns, pkColumn, parentPath);
        Map<String, String> files = renderAll(model);
        return zipFiles(files);
    }

    private void validateGenerateRequest(GenDto.Generate request) {
        if (!request.moduleName().matches("[a-z][a-z0-9_]*")) {
            throw new BusinessException("模块名需为小写字母开头的标识");
        }
        if (!request.className().matches("[A-Z][A-Za-z0-9]*")) {
            throw new BusinessException("类名需为 PascalCase");
        }
        if (!request.packagePath().matches("[a-z][a-z0-9_]*")) {
            throw new BusinessException("生成目录需为小写字母开头的标识");
        }
        if (!request.permPrefix().matches("[a-z][a-z0-9_:]*")) {
            throw new BusinessException("权限前缀格式不合法");
        }
    }

    private GenCodegenModel buildModel(GenDto.Generate request, List<GenColumnModel> columns,
                                       GenColumnModel pkColumn, String parentPermPath) {
        String moduleName = request.moduleName().toLowerCase(Locale.ROOT);
        String packagePath = request.packagePath().toLowerCase(Locale.ROOT);
        GenColumnModel nameCol = columns.stream().filter(GenColumnModel::isNameField).findFirst().orElse(null);
        GenColumnModel codeCol = columns.stream().filter(GenColumnModel::isCodeField).findFirst().orElse(null);
        boolean hasEnabled = columns.stream().anyMatch(GenColumnModel::isEnabledField);
        boolean hasOrderNum = columns.stream().anyMatch(GenColumnModel::isOrderNumField);
        List<GenColumnModel> formColumns = columns.stream().filter(GenColumnModel::isFormField).toList();
        List<GenColumnModel> listQueryColumns = columns.stream().filter(GenColumnModel::isListQueryField).toList();
        List<GenColumnModel> listDisplayColumns = columns.stream().filter(GenColumnModel::isListDisplayField).toList();
        Map<String, String> permIds = allocatePermIds(hasEnabled);
        String author = StrUtil.blankToDefault(request.author(), "codegen");
        return GenCodegenModel.builder()
                .tableName(request.tableName())
                .moduleName(moduleName)
                .className(request.className())
                .functionName(request.functionName())
                .permPrefix(request.permPrefix())
                .parentPermId(request.parentPermId())
                .parentPermPath(parentPermPath)
                .packagePath(packagePath)
                .author(author)
                .pkFieldName(pkColumn.getFieldName())
                .pkColumnName(pkColumn.getColumnName())
                .pkJavaType(pkColumn.getJavaType())
                .hasIsEnabled(hasEnabled)
                .hasOrderNum(hasOrderNum)
                .hasNameField(nameCol != null)
                .hasCodeField(codeCol != null)
                .nameColumn(nameCol)
                .codeColumn(codeCol)
                .allColumns(columns)
                .formColumns(formColumns)
                .listQueryColumns(listQueryColumns)
                .listDisplayColumns(listDisplayColumns)
                .permIds(permIds)
                .adminRoleId(ADMIN_ROLE_ID)
                .build();
    }

    private Map<String, String> allocatePermIds(boolean hasEnabled) {
        Map<String, String> ids = new LinkedHashMap<>();
        ids.put("menu", nextId());
        ids.put("insert", nextId());
        ids.put("delete", nextId());
        ids.put("update", nextId());
        ids.put("select", nextId());
        if (hasEnabled) {
            ids.put("updateEnabled", nextId());
        }
        return ids;
    }

    /** 运行时生成固定长度的数字 ID，供 ZIP 内 perm SQL 使用。 */
    private String nextId() {
        long base = System.currentTimeMillis();
        long seq = idSeq.incrementAndGet() % 1000;
        return String.valueOf(base * 1000 + seq);
    }

    private Map<String, String> renderAll(GenCodegenModel model) {
        Map<String, String> paths = new LinkedHashMap<>();
        String cn = model.getClassName();
        String module = model.getModuleName();
        String packagePath = model.getPackagePath();
        paths.put("src/main/java/com/ylmao/admin/entity/" + cn + ".java", render("entity.ftl", model));
        paths.put("src/main/java/com/ylmao/admin/dto/" + cn + "Dto.java", render("dto.ftl", model));
        paths.put("src/main/java/com/ylmao/admin/vo/" + cn + "Vo.java", render("vo.ftl", model));
        paths.put("src/main/java/com/ylmao/admin/mapper/" + cn + "Mapper.java", render("mapper.ftl", model));
        paths.put("src/main/java/com/ylmao/admin/service/" + cn + "Service.java", render("service.ftl", model));
        paths.put("src/main/java/com/ylmao/admin/controller/" + packagePath + "/" + cn + "Controller.java", render("controller.ftl", model));
        paths.put("src/main/resources/templates/" + packagePath + "/" + module + ".html", render("html.ftl", model));
        paths.put("sql/" + model.getTableName() + "_perm.sql", render("permSql.ftl", model));
        return paths;
    }

    private String render(String templateName, GenCodegenModel model) {
        try {
            Template template = freemarkerConfig.getTemplate(templateName);
            StringWriter writer = new StringWriter();
            template.process(model, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {
            throw new BusinessException("渲染模板失败：" + templateName);
        }
    }

    private byte[] zipFiles(Map<String, String> files) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos, StandardCharsets.UTF_8)) {
            for (Map.Entry<String, String> entry : files.entrySet()) {
                ZipEntry zipEntry = new ZipEntry(entry.getKey());
                zos.putNextEntry(zipEntry);
                zos.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }
            zos.finish();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new BusinessException("打包 ZIP 失败");
        }
    }
}
