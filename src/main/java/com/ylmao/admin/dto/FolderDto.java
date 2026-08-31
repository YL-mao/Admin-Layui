package com.ylmao.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FolderDto {

    public record FolderInsert(
            String parentId,
            @NotBlank(message = "目录名称不能为空") String folderName,
            @NotNull(message = "目录排序不能为空") Integer orderNum
    ) {
    }

    public record FolderUpdate(
            @NotBlank(message = "目录ID不能为空") String folderId,
            String parentId,
            @NotBlank(message = "目录名称不能为空") String folderName,
            @NotNull(message = "目录排序不能为空") Integer orderNum
    ) {
    }
}
