# 创作平台 - 后端开发日志

---

- **日期**：2026-03-22
- **完成功能**：作品流接口支持按标签（tagId）筛选过滤
- **核心技术点**：
  - 使用 MyBatis `<script>` 标签实现动态 SQL
  - 使用 `EXISTS` 子查询过滤标签，避免 JOIN 导致的分页总数错误（`SELECT 1` 方式，不产生额外列）
  - `<if test='tagId != null'>` 实现条件动态拼接，tagId 为 null 时自动跳过过滤条件
  - Mapper → Service → Controller 三层参数链式传递，均支持可选参数
- **修改的文件**：
  - `ArtworkMapper.java` — selectFeedPage 改写为动态 SQL，增加 tagId 参数
  - `ArtworkService.java` — getFeedPage 方法签名增加 Long tagId 参数
  - `ArtworkServiceImpl.java` — getFeedPage 实现增加 tagId 参数并透传给 Mapper
  - `ArtworkController.java` — getFeed 接口增加 `@RequestParam(required = false) Long tagId` 参数
- **遗留问题/下一步**：前端联调验证分页 + 标签过滤效果；后续可扩展支持多标签筛选（IN 查询）

---

- **日期**：2026-03-22
- **完成功能**：修复 artwork_tag_relation 表唯一索引冲突问题（标签重复选择时 DuplicateKeyException）
- **核心技术点**：
  - 引入 `INSERT ... ON DUPLICATE KEY UPDATE` 语法，当唯一键冲突时将 `is_deleted` 置为 0 恢复记录，避免先删后插的两步操作
  - 对比点赞功能的 UUID 方案（单用户无并发），标签场景更适合 `ON DUPLICATE KEY UPDATE`，因为同一用户同一作品可能多次修改标签
  - ServiceImpl 中仍保留逻辑删除旧关联（`this.remove`），双重保障；Mapper 层 `INSERT OR RECOVER` 作为兜底
- **修改的文件**：
  - `ArtworkTagRelationMapper.java` — 新增 `insertOrRecover` 方法，使用 `ON DUPLICATE KEY UPDATE is_deleted = 0`
  - `ArtworkTagRelationServiceImpl.java` — `setTags` 方法中 `this.save(relation)` 替换为 `baseMapper.insertOrRecover(artworkId, tagId)`
- **遗留问题/下一步**：前端联调标签选择交互；后续可考虑批量插入优化（MyBatis `<foreach>` 一次 SQL 插入多条）

---

- **日期**：2026-03-22
- **完成功能**：打通作品发布链路与标签关联（前端发布时可同时选择标签）
- **核心技术点**：
  - `ArtworkPublishDTO` 新增 `tagIds` 字段（`List<Long>`），前端可直接传入标签 ID 数组
  - `ArtworkServiceImpl.publishArtwork` 中，在 `artworkMapper.insert` 获取自增 ID 后，调用 `artworkTagRelationService.setTags` 保存关联关系
  - 整体事务由 `@Transactional(rollbackFor = Exception.class)` 保证，作品创建与标签关联要么同时成功，要么同时回滚
- **修改的文件**：
  - `ArtworkPublishDTO.java` — 新增 `private List<Long> tagIds;` 字段及 `import java.util.List;`
  - `ArtworkServiceImpl.java` — 注入 `ArtworkTagRelationService`，在 `publishArtwork` 末尾追加标签关联保存逻辑
- **遗留问题/下一步**：前端发布页需要接入标签选择组件；后续可考虑作品编辑页同步支持标签修改（复用 `setTags` 逻辑）

---

- **日期**：2026-03-22
- **完成功能**：作品信息流查询时自动注入标签数据（作品列表中展示每篇文章的标签）
- **核心技术点**：
  - `ArtworkVO` 新增 `List<Tag> tags` 字段，用于承载标签查询结果
  - `ArtworkServiceImpl.getFeedPage` 中，Mapper 查询完成后，遍历分页结果逐条查询标签关联并填充 VO
  - 使用 `artworkTagRelationService.getTagIdsByArtworkId` + `tagService.listByIds` 两步查：先取 ID 列表再批量查实体，避免 JOIN 带来的分页翻倍问题
  - 该模式与浏览量/点赞数等统计字段一致，均在 Service 层手动填充而非混入 Mapper SQL
- **修改的文件**：
  - `ArtworkVO.java` — 新增 `private List<Tag> tags;` 字段及 `import java.util.List`
  - `ArtworkServiceImpl.java` — 注入 `TagService`，改造 `getFeedPage` 遍历 records 并调用 `setTags`
- **遗留问题/下一步**：N+1 查询问题仍存在（每条作品额外 2 次 DB 查询），后续可优化为一次 SQL 批量查出所有作品的标签关联；前端信息流页面需渲染标签列表 UI

---

- **日期**：2026-03-22
- **完成功能**：作品详情接口注入标签数据（`GET /api/artwork/detail/{id}` 返回完整标签列表）
- **核心技术点**：
  - `ArtworkVO.tags` 字段补充 `@TableField(exist = false)`，明确标注为非数据库持久化字段，与 MyBatis-Plus 逻辑删除等注解保持一致
  - `getArtworkDetail` 在收藏数 `collectCount` 统计之后、return 之前追加标签查询逻辑，与浏览量/点赞数/收藏数等统计字段的填充时机对齐
  - 两步查模式复用：`getTagIdsByArtworkId` → `listByIds`，与信息流 `getFeedPage` 完全一致，保证数据一致性
- **修改的文件**：
  - `ArtworkVO.java` — `tags` 字段补充 `@TableField(exist = false)` 注解
  - `ArtworkServiceImpl.java` — `getArtworkDetail` 方法末尾追加标签查询与 `setTags` 注入逻辑
- **遗留问题/下一步**：前端详情页需渲染标签列表 UI；可考虑将标签查询下沉到 Mapper 层联表一次查出，消除详情页的 2 次额外 DB 查询

---

- **日期**：2026-03-22
- **完成功能**：作品信息流支持按 `categoryId` 分类筛选（与 `tagId` 筛选并行，双条件可自由组合）
- **核心技术点**：
  - `ArtworkMapper.selectFeedPage` SQL 中，在 `WHERE a.status = 1 AND a.is_deleted = 0` 之后追加 `<if test='categoryId != null'> AND a.category_id = #{categoryId} </if>`，位置优先于 tagId 过滤，保持条件拼接顺序清晰
  - `categoryId` 条件直接过滤主表字段，无须 EXISTS 子查询，与 `tagId` 共存互不干扰
  - Mapper → Service → Controller 三层参数链式透传，均支持可选参数（`@RequestParam(required = false)`）
  - 与后台管理 `selectArtworkPage` 的 categoryId 逻辑保持一致
- **修改的文件**：
  - `ArtworkMapper.java` — `selectFeedPage` 方法签名增加 `@Param("categoryId") Long categoryId`，SQL 中追加 categoryId 过滤条件
  - `ArtworkService.java` — `getFeedPage` 方法签名增加 `Long categoryId` 参数
  - `ArtworkServiceImpl.java` — `getFeedPage` 实现透传 `categoryId` 给 Mapper
  - `ArtworkController.java` — `getFeed` 接口增加 `@RequestParam(required = false) Long categoryId` 参数
- **遗留问题/下一步**：前端首页分类 Tab 或下拉框接入 `categoryId` 参数；后续可扩展支持分类+标签双条件同时筛选（目前已天然支持，联调验证即可）

---

- **日期**：2026-03-22
- **完成功能**：新增用户创作数据统计接口 `GET /api/user/stats`（需登录，返回当前用户的文章数、总浏览量、获赞数、粉丝数）
- **核心技术点**：
  - 新建 `UserStatsVO` 作为数据载体，四项指标独立字段，无冗余关联
  - `ArtworkMapper.selectTotalViewsByUserId`：使用 `COALESCE(SUM(view_count), 0)` 聚合浏览量，null 时安全返回 0
  - `UserInteractionMapper.selectTotalLikesByUserId`：JOIN artwork 表确保只统计已发布（status=1）且未删除作品的点赞，排除草稿/删除作品的点赞计数
  - 文章数统计复用 `artworkMapper.selectCount` + `LambdaQueryWrapper`，与浏览量/获赞数统计方式保持一致
  - 粉丝数暂以 0 填充，未来接入关注系统后直接替换 SQL
- **修改的文件**：
  - `vo/UserStatsVO.java` — **新建**，四个字段：`articleCount / totalViews / totalLikes / fanCount`
  - `ArtworkMapper.java` — 新增 `selectTotalViewsByUserId` 方法
  - `UserInteractionMapper.java` — 新增 `selectTotalLikesByUserId` 方法（JOIN artwork 确保数据准确性）
  - `UserService.java` — 新增 `getUserStats(Long userId)` 接口声明
  - `UserServiceImpl.java` — 注入 `ArtworkMapper` 和 `UserInteractionMapper`，实现 `getUserStats`
  - `UserController.java` — 新增 `GET /stats` 接口，需登录拦截器校验
- **遗留问题/下一步**：前端个人中心页接入 `/stats` 接口展示四项指标；后续接入关注系统后 `fanCount` 替换为真实粉丝数查询

---

- **日期**：2026-03-22
- **完成功能**：新增关注系统核心代码（Entity / Mapper / Service / ServiceImpl 四层架构）
- **核心技术点**：
  - `UserFollow` 实体使用 `@TableLogic` 注解实现逻辑删除，`@TableField(fill = FieldFill.INSERT)` 自动填充创建时间，与其他实体保持一致
  - `UserFollowMapper.toggleFollow` 复用 `INSERT ... ON DUPLICATE KEY UPDATE` 模式，与点赞/收藏逻辑完全一致，实现原子性的关注/取消关注切换
  - `ServiceImpl` 继承 `ServiceImpl<UserFollowMapper, UserFollow>`，复用 MyBatis-Plus 的 CRUD 能力
  - 数据库唯一索引应建在 `(follower_id, followee_id)` 上，`ON DUPLICATE KEY UPDATE` 依赖该唯一键工作
- **新建的文件**：
  - `entity/UserFollow.java` — 实体：`id / followerId / followeeId / createTime / isDeleted`
  - `mapper/UserFollowMapper.java` — Mapper：继承 `BaseMapper`，新增 `toggleFollow` 方法
  - `service/UserFollowService.java` — Service 接口：定义 `toggleFollow` 方法
  - `service/impl/UserFollowServiceImpl.java` — Service 实现：`toggleFollow` 调用 Mapper
- **遗留问题/下一步**：Controller 层暴露关注/取关接口（需要登录拦截器）；`fanCount` 已替换为真实查询

---

- **日期**：2026-03-22
- **完成功能**：改造信息流接口支持 `userId` 过滤 + 新增公开用户数据面板接口（支持类似知乎的个人主页功能）
- **核心技术点**：
  - `ArtworkMapper.selectFeedPage` SQL 中追加 `<if test='userId != null'> AND a.user_id = #{userId} </if>`，位置优先于 categoryId，与标签过滤组合使用时保持逻辑清晰
  - 信息流接口 `getFeedPage` 三层参数链式透传 `userId`（Mapper → Service → Controller），支持四条件自由组合：`tagId` / `categoryId` / `userId` + 分页
  - 新增 `GET /api/user/public/stats/{id}` 公开接口，无需 Token，直接复用 `userService.getUserStats(id)` 查询指定用户的创作数据
  - `WebConfig` 放行用户公开接口：`/api/user/public/**`、`/api/user/{id}`（他人主页查询），以及信息流 `/api/artwork/feed` 和作品详情 `/api/artwork/detail/**`，实现未登录用户可浏览公开内容
- **修改的文件**：
  - `ArtworkMapper.java` — `selectFeedPage` SQL 追加 userId 过滤条件，方法签名增加 `@Param("userId") Long userId`
  - `ArtworkService.java` — `getFeedPage` 方法签名增加 `Long userId` 参数
  - `ArtworkServiceImpl.java` — `getFeedPage` 实现透传 `userId` 给 Mapper
  - `ArtworkController.java` — `getFeed` 接口增加 `@RequestParam(required = false) Long userId` 参数
  - `UserController.java` — 新增 `GET /api/user/public/stats/{id}` 公开接口
  - `WebConfig.java` — 放行 `/api/user/public/**`、`/api/user/{id}`、`/api/artwork/feed`、`/api/artwork/detail/**`
- **遗留问题/下一步**：前端个人主页接入 `/api/user/public/stats/{id}` 和 `/api/artwork/feed?userId=xxx`；后续可新增关注/粉丝列表查询接口

---

- **日期**：2026-03-22
- **完成功能**：`ArtworkVO` 新增 `userId` 字段，三个查询方法均补全该字段映射
- **核心技术点**：
  - `ArtworkVO` 新增 `userId` 字段（`@TableField(exist = false)` 不适用，VO 中有 getter/setter 即可被 MyBatis 自动映射，无需标注）
  - 三处 SQL 均追加 `a.user_id AS userId`，确保字段别名与 VO 属性名一致，MyBatis 自动映射
  - `selectFeedPage`（信息流）、`selectDetailById`（作品详情）、`selectArtworkPage`（后台管理）三个联表查询均补全，前端从任意列表入口均能拿到 `userId` 跳转个人主页
- **修改的文件**：
  - `ArtworkVO.java` — 新增 `private Long userId;` 字段
  - `ArtworkMapper.java` — `selectArtworkPage` / `selectFeedPage` / `selectDetailById` 三处 SQL SELECT 列表均追加 `a.user_id AS userId`
- **遗留问题/下一步**：前端确认 `userId` 能正确透传到个人主页跳转链接；`categoryId` 可用于前端展示当前分类标签及跳转筛选

---

- **日期**：2026-03-22
- **完成功能**：`ArtworkVO` 新增 `categoryId` 字段，三个查询方法均补全该字段映射
- **修改的文件**：
  - `ArtworkVO.java` — 新增 `private Long categoryId;` 字段
  - `ArtworkMapper.java` — `selectArtworkPage` / `selectFeedPage` / `selectDetailById` 三处 SQL SELECT 列表均追加 `a.category_id AS categoryId`

---

- **日期**：2026-03-23
- **完成功能**：关注功能 Controller 层接口 + 关注流（仅看我关注的人）支持
- **核心技术点**：
  - `UserFollowMapper.checkFollowStatus` 通过 `@Select` 直接查询 `is_deleted` 字段，返回 `Integer`（null=从未关注，0=已关注，1=已取关），结合前端可展示关注/取关按钮状态
  - `UserFollowController` 两个接口：`POST /api/follow/toggle`（切换关注）和 `GET /api/follow/check?followeeId=xxx`（查询关注状态）
  - `ArtworkMapper.selectFeedPage` 新增 `followerId` 参数，SQL 追加 IN 子查询 `AND a.user_id IN (SELECT followee_id FROM user_follow WHERE follower_id = #{followerId} AND is_deleted = 0)`，仅返回当前用户已关注作者的已发布作品
  - `ArtworkController.getFeed` 新增 `isFollowFeed` 参数（默认 `false`），为 `true` 时从 JWT 提取当前用户 ID 作为 `followerId`，未登录时返回 401；与其他参数（`tagId`、`categoryId`、`userId`）可叠加使用
  - SQL 中 `followerId` 条件优先于 `userId`/`categoryId`/`tagId`，确保语义正确：关注流场景下不应出现他人作品
- **修改的文件**：
  - `UserFollowMapper.java` — 新增 `checkFollowStatus` 方法
  - `UserFollowController.java` — 新增（关注/取关 + 状态查询两个接口）
  - `ArtworkMapper.java` — `selectFeedPage` SQL 追加 `followerId` IN 子查询过滤，方法签名增加 `@Param("followerId") Long followerId`
  - `ArtworkService.java` — `getFeedPage` 签名增加 `Long followerId` 参数
  - `ArtworkServiceImpl.java` — `getFeedPage` 实现透传 `followerId` 给 Mapper
  - `ArtworkController.java` — `getFeed` 新增 `HttpServletRequest request` 参数、`@RequestParam(defaultValue = "false") Boolean isFollowFeed` 参数，未登录关注流返回 401
- **遗留问题/下一步**：前端"关注流"Tab 与全局信息流 Tab 切换；前端展示关注/取关按钮并调用 `/api/follow/toggle` 和 `/api/follow/check`

---

- **日期**：2026-03-23
- **完成功能**：全站强制登录，拦截器白名单收紧
- **修改的文件**：
  - `WebConfig.java` — `excludePathPatterns` 删除了所有业务公开接口（`/api/artwork/feed`、`/api/user/public/**` 等），仅保留登录/注册、静态资源、Swagger 文档四类路径
- **影响范围**：所有业务接口（信息流、分类、标签、作品详情、用户数据面板、关注/取关等）均强制要求携带有效 JWT Token，未登录请求统一返回 401