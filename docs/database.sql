-- =====================================================
-- 个人创作记录与作品分享平台 - 数据库建表脚本
-- MySQL 版本: 8.0+
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS creation_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE creation_platform;

-- =====================================================
-- 1. 用户表 (user)
-- =====================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `nickname` VARCHAR(100) DEFAULT NULL COMMENT '昵称',
    `avatar_url` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `role` TINYINT NOT NULL DEFAULT 1 COMMENT '角色: 1-普通用户 2-管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态: 1-正常 0-禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- 2. 创作草稿表 (creation_record)
-- =====================================================
DROP TABLE IF EXISTS `creation_record`;
CREATE TABLE `creation_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '草稿ID',
    `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
    `title` VARCHAR(200) DEFAULT NULL COMMENT '草稿标题',
    `content` TEXT COMMENT '草稿内容',
    `category_id` BIGINT DEFAULT NULL COMMENT '所属分类ID',
    `description` VARCHAR(1000) DEFAULT NULL COMMENT '创作说明/灵感记录',
    `word_count` INT DEFAULT 0 COMMENT '字数统计',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='创作草稿表';

-- =====================================================
-- 3. 正式作品表 (artwork)
-- =====================================================
DROP TABLE IF EXISTS `artwork`;
CREATE TABLE `artwork` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '作品ID',
    `user_id` BIGINT NOT NULL COMMENT '作者ID',
    `title` VARCHAR(200) NOT NULL COMMENT '作品标题',
    `content` LONGTEXT COMMENT '作品正文内容',
    `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图URL',
    `description` VARCHAR(1000) DEFAULT NULL COMMENT '作品简介/创作说明',
    `category_id` BIGINT DEFAULT NULL COMMENT '所属分类ID',
    `ai_summary` TEXT COMMENT 'AI生成的作品分析与摘要（后续LLM接入使用）',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `word_count` INT DEFAULT 0 COMMENT '作品字数',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '作品状态: 1-已发布 0-草稿',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='正式作品表';

-- =====================================================
-- 4. 分类表 (category)
-- =====================================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '分类描述',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- =====================================================
-- 5. 标签表 (tag)
-- =====================================================
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
    `color` VARCHAR(20) DEFAULT NULL COMMENT '标签颜色',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- =====================================================
-- 6. 作品标签关联表 (artwork_tag_relation)
-- =====================================================
DROP TABLE IF EXISTS `artwork_tag_relation`;
CREATE TABLE `artwork_tag_relation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `artwork_id` BIGINT NOT NULL COMMENT '作品ID',
    `tag_id` BIGINT NOT NULL COMMENT '标签ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_artwork_tag` (`artwork_id`, `tag_id`),
    KEY `idx_artwork_id` (`artwork_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作品标签关联表';

-- =====================================================
-- 7. 评论表 (comment)
-- =====================================================
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `artwork_id` BIGINT NOT NULL COMMENT '作品ID',
    `user_id` BIGINT NOT NULL COMMENT '评论用户ID',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID（用于回复）',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_artwork_id` (`artwork_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- =====================================================
-- 8. 互动表 (user_interaction)
-- 记录用户对作品的点赞/收藏等互动行为
-- =====================================================
DROP TABLE IF EXISTS `user_interaction`;
CREATE TABLE `user_interaction` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '互动ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `artwork_id` BIGINT NOT NULL COMMENT '作品ID',
    `interaction_type` TINYINT NOT NULL COMMENT '互动类型: 1-点赞 2-收藏',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_artwork_type` (`user_id`, `artwork_id`, `interaction_type`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_artwork_id` (`artwork_id`),
    KEY `idx_interaction_type` (`interaction_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='互动表（点赞/收藏）';

-- =====================================================
-- 初始化数据
-- =====================================================

-- 插入默认管理员账号 (密码: admin123，使用BCrypt加密)
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 2, 1);

-- 插入默认分类
INSERT INTO `category` (`name`, `description`, `sort_order`) VALUES
('文学创作', '包括小说、散文、诗歌等文学类作品', 1),
('技术文档', '技术教程、学习笔记等专业文档', 2),
('生活随笔', '日常生活记录、感悟分享', 3),
('学习笔记', '课堂笔记、读书笔记等', 4);

-- 插入默认标签
INSERT INTO `tag` (`name`, `color`) VALUES
('原创', '#1989fa'),
('转载', '#909399'),
('精选', '#e6a23c'),
('热门', '#f56c6c'),
('AI辅助', '#67c23a');

-- =====================================================
-- 注意事项
-- =====================================================
-- 1. 本脚本适用于 MySQL 8.0+
-- 2. 所有表均采用 InnoDB 引擎，字符集 utf8mb4
-- 3. 通用字段说明：
--    - id: 主键，使用 BIGINT 自增
--    - create_time: 创建时间
--    - update_time: 更新时间
--    - is_deleted: 逻辑删除标识（0-未删除 1-已删除）
-- 4. user_interaction 表通过 (user_id, artwork_id, interaction_type) 组合唯一键防止重复点赞/收藏
-- 5. 管理员初始密码为 admin123（加密后的BCrypt值），建议首次登录后修改
