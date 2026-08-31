package com.ylmao.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ylmao.admin.entity.FileResource;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FileResourceVo {

    public record FileListVo(String fileId, String folderId, String originalName, String storageType,
                             String fileSuffix, String contentType, Long fileSize, String fileScene,
                             Integer needLogin, String accessUrl,
                             @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createTime) {

        public static FileListVo from(FileResource file, String accessUrl) {
            return new FileListVo(file.getFileId(), file.getFolderId(), file.getOriginalName(),
                    file.getStorageType(), file.getFileSuffix(), file.getContentType(), file.getFileSize(),
                    file.getFileScene(), file.getNeedLogin(), accessUrl, file.getCreateTime());
        }
    }

    /** 前端上传规则：各场景允许后缀与大小上限。 */
    public record UploadRulesVo(long maxFileSizeMb,
                                List<String> imageExtensions,
                                List<String> documentExtensions,
                                List<String> excelExtensions) {
    }

    /** 删除前引用检测结果；referenced=true 时前端应提示确认。 */
    public record CheckRefResult(boolean referenced, String message) {
    }
}
