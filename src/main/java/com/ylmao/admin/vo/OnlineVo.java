package com.ylmao.admin.vo;

public final class OnlineVo {

    private OnlineVo() {
    }

    public record OnlineListVo(
            String tokenValue,
            /* 列表截断展示用。 */
            String tokenDisplay,
            String userId,
            String userAccount,
            String userName,
            String loginIp,
            String loginTime,
            String browser,
            String systemOs,
            /* 剩余有效期秒；-1 永久。 */
            Long timeoutSeconds,
            /* 剩余有效期中文说明。 */
            String timeoutText,
            /* 是否当前登录会话（禁止强退）。 */
            Boolean self
    ) {
    }
}
