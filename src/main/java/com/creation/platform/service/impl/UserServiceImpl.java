package com.creation.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creation.platform.dto.UserUpdateDTO;
import com.creation.platform.entity.User;
import com.creation.platform.mapper.UserMapper;
import com.creation.platform.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@SuppressWarnings("all")
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public String register(String username, String password) {
        // 查询账号是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User existingUser = this.getOne(wrapper);
        if (existingUser != null) {
            return "账号已存在";
        }

        // 加密密码
        String encryptedPassword = DigestUtils.md5DigestAsHex((password + "").getBytes());

        // 创建新用户
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encryptedPassword);
        newUser.setRole(1); // 默认普通用户
        newUser.setStatus(1); // 正常状态

        this.save(newUser);
        return null;
    }

    @Override
    public User login(String username, String password) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = this.getOne(wrapper);

        if (user == null) {
            return null;
        }

        // 加密前端传来的密码，与数据库比对
        String encryptedPassword = DigestUtils.md5DigestAsHex((password + "").getBytes());
        if (!encryptedPassword.equals(user.getPassword())) {
            return null;
        }

        // 密码设为null，防止泄露
        user.setPassword(null);
        return user;
    }

    @Override
    public User getProfile(Long userId) {
        User user = this.getById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public boolean updateProfile(Long userId, UserUpdateDTO dto) {
        User user = this.getById(userId);
        if (user == null) {
            return false;
        }
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }
        return this.updateById(user);
    }
}
