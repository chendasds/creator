/*
 * @Author: dingxiuchen 2745250790@qq.com
 * @Date: 2026-03-15 15:38:49
 * @LastEditors: dingxiuchen 2745250790@qq.com
 * @LastEditTime: 2026-03-15 17:12:41
 * @FilePath: \build-one\src\main\java\com\creation\platform\service\UserService.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.creation.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creation.platform.dto.PasswordUpdateDTO;
import com.creation.platform.dto.UserSettingsDTO;
import com.creation.platform.dto.UserUpdateDTO;
import com.creation.platform.entity.User;
import com.creation.platform.vo.UserStatsVO;

public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码（明文）
     * @return 注册结果信息
     */
    String register(String username, String password);

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码（明文）
     * @return 登录成功返回用户信息，密码设为null；失败返回null
     */
    User login(String username, String password);

    /**
     * 获取个人资料（脱敏）
     * @param userId 用户ID
     * @return 脱敏后的用户信息（密码为null）
     */
    User getProfile(Long userId);

    /**
     * 更新个人资料
     * @param userId 用户ID
     * @param dto 更新信息
     * @return 是否更新成功
     */
    boolean updateProfile(Long userId, UserUpdateDTO dto);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param dto 包含旧密码和新密码
     * @return 是否修改成功
     */
    boolean updatePassword(Long userId, PasswordUpdateDTO dto);

    /**
     * 更新账号隐私与偏好设置
     * @param userId 用户ID
     * @param dto 账号设置信息
     * @return 是否更新成功
     */
    boolean updateSettings(Long userId, UserSettingsDTO dto);

    /**
     * 获取当前登录用户的创作数据统计
     * @param userId 用户ID
     * @return 统计信息（文章数、浏览量、获赞数、粉丝数）
     */
    UserStatsVO getUserStats(Long userId);
}
