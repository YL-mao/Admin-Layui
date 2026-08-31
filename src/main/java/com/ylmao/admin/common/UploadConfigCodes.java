package com.ylmao.admin.common;

/** 上传类配置编码契约，与 sys_config.config_code 对齐。 */
public final class UploadConfigCodes {

    public static final String ENABLED = "upload.enabled";
    public static final String STORAGE_TYPE = "upload.storType";
    public static final String LOCAL_PATH = "upload.locPath";
    public static final String PUBLIC_URL_PREFIX = "upload.pubUrlPfx";
    public static final String MAX_FILE_SIZE_MB = "upload.maxFileSzMb";
    public static final String IMAGE_EXTENSIONS = "upload.imgExts";
    public static final String DOCUMENT_EXTENSIONS = "upload.docsExts";
    public static final String EXCEL_EXTENSIONS = "upload.excelExts";

    /** 内置「未分类」虚拟目录主键。 */
    public static final String UNCLASSIFIED_FOLDER_ID = "1229000000000000001";

    private UploadConfigCodes() {
    }
}
