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
import com.creation.platform.entity.User;

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
}
