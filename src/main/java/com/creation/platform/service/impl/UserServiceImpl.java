package com.creation.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creation.platform.dto.PasswordUpdateDTO;
import com.creation.platform.dto.UserSettingsDTO;
import com.creation.platform.dto.UserUpdateDTO;
import com.creation.platform.entity.Artwork;
import com.creation.platform.entity.User;
import com.creation.platform.mapper.ArtworkMapper;
import com.creation.platform.mapper.UserInteractionMapper;
import com.creation.platform.mapper.UserMapper;
import com.creation.platform.service.UserService;
import com.creation.platform.vo.UserStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@SuppressWarnings("all")
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private ArtworkMapper artworkMapper;

    @Autowired
    private UserInteractionMapper userInteractionMapper;

    @Autowired
    private com.creation.platform.mapper.UserFollowMapper userFollowMapper;

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
        String encryptedPassword = passwordEncoder.encode(password);

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

        // 校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
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
        if (dto.getBio() != null) {
            user.setBio(dto.getBio());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        return this.updateById(user);
    }

    @Override
    public boolean updateSettings(Long userId, UserSettingsDTO dto) {
        User user = new User();
        user.setId(userId);
        user.setPhone(dto.getPhone());
        user.setHideCollections(dto.getHideCollections());
        user.setDisableNotifications(dto.getDisableNotifications());
        user.setWatermark(dto.getWatermark());
        return this.updateById(user);
    }

    @Override
    public boolean updatePassword(Long userId, PasswordUpdateDTO dto) {
        User user = this.getById(userId);
        if (user == null) {
            return false;
        }

        // 1. 使用 BCrypt 校验旧密码是否正确
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 2. 使用 BCrypt 加密并保存新密码
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        return this.updateById(user);
    }

    @Override
    public UserStatsVO getUserStats(Long userId) {
        UserStatsVO stats = new UserStatsVO();

        // 1. 已发布的文章数
        Long articleCount = artworkMapper.selectCount(
                new LambdaQueryWrapper<Artwork>()
                        .eq(Artwork::getUserId, userId)
                        .eq(Artwork::getStatus, 1));
        stats.setArticleCount(articleCount.intValue());

        // 2. 总浏览量
        stats.setTotalViews(artworkMapper.selectTotalViewsByUserId(userId));

        // 3. 获赞数
        stats.setTotalLikes(userInteractionMapper.selectTotalLikesByUserId(userId));

        // 4. 粉丝数（查询 followee_id 为当前用户的正常记录数）
        Long fanCount = userFollowMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.creation.platform.entity.UserFollow>()
                        .eq(com.creation.platform.entity.UserFollow::getFolloweeId, userId));
        stats.setFanCount(fanCount.intValue());

        return stats;
    }
}
