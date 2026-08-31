package com.ylmao.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FileResourceDto {

    public record FileList(
            @Size(max = 64, message = "目录参数不合法") String folderId,
            @Size(max = 255, message = "文件名参数不合法") String originalName
    ) {
    }

    /** 上传接口非文件字段；MultipartFile 仍由 Controller 单独接收。 */
    public record FileUpload(
            @Size(max = 64, message = "目录参数不合法") String folderId,
            @NotBlank(message = "上传场景不能为空") @Size(max = 32, message = "上传场景参数不合法") String fileScene,
            @Min(value = 0, message = "是否需登录参数不合法")
            @Max(value = 1, message = "是否需登录参数不合法")
            Integer needLogin,
            Boolean forceOverwrite
    ) {
    }

    public record FileUpdate(
            @NotBlank(message = "文件ID不能为空") String fileId,
            @NotBlank(message = "所属目录不能为空") String folderId,
            @NotBlank(message = "文件名不能为空") String originalName,
            @NotNull(message = "是否需登录参数不合法")
            @Min(value = 0, message = "是否需登录参数不合法")
            @Max(value = 1, message = "是否需登录参数不合法")
            Integer needLogin
    ) {
    }
}
