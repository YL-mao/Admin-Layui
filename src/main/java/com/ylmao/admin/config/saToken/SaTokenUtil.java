package com.ylmao.admin.config.saToken;

import cn.dev33.satoken.stp.StpUtil;
import com.ylmao.admin.common.OnlineSessionKeys;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.entity.User;

/**
 * 封装 Sa-Token 常用操作
 * 
 * @author kong
 *
 */
public class SaTokenUtil {

	/**
	 * 获取登录用户model
	 */
	public static User getUser() {
		// 登录时直接存入 User，读取时按类型还原，避免反射拷贝
		Object object = StpUtil.getSession().get("user");
		if (object instanceof User user) {
			return user;
		}
		return null;
	}

	/**
	 * set用户
	 */
	public static void setUser(User user) {
		StpUtil.getSession().set("user", user);
	}

	/**
	 * 获取登录用户id
	 */
	public static String getUserId() {
		return StpUtil.getLoginIdAsString();
	}

	/**
	 * 获取登录用户Account
	 */
	public static String getLoginAccount() {
		User user = getUser();
		if (user == null) {
			throw new BusinessException("用户不存在");
		}
		return user.getUserAccount();
	}

	/**
	 * 获取登录用户ip
	 * 
	 * @return
	 * @author fuce
	 * @Date 2019年11月21日 上午9:58:26
	 */
	public static String getIp() {
		return StpUtil.getTokenSession().getString(OnlineSessionKeys.IP);
	}

	/**
	 * 判断是否登录
	 * 
	 * @return
	 * @author fuce
	 * @Date 2019年11月21日 上午9:58:26
	 */
	public static boolean isLogin() {
		return StpUtil.isLogin();
	}

}
