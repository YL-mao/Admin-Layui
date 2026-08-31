package com.ylmao.admin.vo;

import com.ylmao.admin.entity.Folder;
import lombok.Data;

@Data
public class FolderVo {

    /** 目录树选项，供文件页左侧树与上级目录选择。 */
    public record FolderOptionVo(String folderId, String parentId, String folderName, Integer isBuiltin,
                                 Integer orderNum) {

        public static FolderOptionVo from(Folder folder) {
            return new FolderOptionVo(folder.getFolderId(), folder.getParentId(), folder.getFolderName(),
                    folder.getIsBuiltin(), folder.getOrderNum());
        }
    }
}
