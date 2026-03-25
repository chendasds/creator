# 创作平台 - 后端开发日志

---

- **日期**：2026-03-24
- **完成功能**：点赞/收藏列表 SQL 补全点赞数（likeCount）和评论数（commentCount）
- **核心技术点**：
  - `selectCollectedArtworks` / `selectLikedArtworks` 的 SELECT 字段追加两条标量子查询：`likeCount`（点赞数）和 `commentCount`（评论数），与信息流 `selectFeedPage` 保持完全一致的字段口径
  - `likeCount` 子查询使用别名 `ui2` 避免与外层 JOIN 的 `ui` 表别名冲突：`SELECT COUNT(*) FROM user_interaction ui2 WHERE ui2.artwork_id = a.id AND ui2.interaction_type = 1 AND ui2.is_deleted = 0`
  - `commentCount` 子查询：`SELECT COUNT(*) FROM comment co WHERE co.artwork_id = a.id AND co.is_deleted = 0`，逻辑删除过滤
  - `ArtworkVO.likeCount` / `commentCount` 字段已在之前的信息流功能中存在（`selectFeedPage` 中使用过），无需新建字段
- **修改的文件**：
  - `ArtworkMapper.java` — `selectCollectedArtworks` / `selectLikedArtworks` 两条 SQL SELECT 字段列表追加 `likeCount` 和 `commentCount` 两个子查询

---

- **日期**：2026-03-24
- **完成功能**：个人主页点赞/收藏列表支持右侧『热门标签』点击筛选 + 修复逻辑删除遗漏
- **核心技术点**：
  - `ArtworkMapper.selectCollectedArtworks` / `selectLikedArtworks` 改造为 `<script>` 动态 SQL，增加 `tagId` 可选参数
  - 标签筛选使用 `EXISTS` 子查询：`AND EXISTS (SELECT 1 FROM artwork_tag_relation atr WHERE atr.artwork_id = a.id AND atr.tag_id = #{tagId} AND atr.is_deleted = 0)`，避免 JOIN 带来的数据重复
  - **修复遗漏**：`ui.is_deleted = 0` 条件补全，确保已取消的点赞/收藏（逻辑删除记录）不混入列表
  - Service 层 `getCollections` / `getLikes` 方法签名增加 `Long tagId` 参数，Mapper 调用时透传；查询完成后调用 `attachTagsToArtworks` 补全每个作品的标签详情（`List<Tag> tags`）
  - Controller 层两个接口均以 `@RequestParam(required = false) Long tagId` 接收前端筛选参数，可选参数不传则返回全部
- **修改的文件**：
  - `ArtworkMapper.java` — `selectCollectedArtworks` / `selectLikedArtworks` 改用 `<script>` 包裹，方法签名增加 `@Param("tagId") Long tagId`，WHERE 追加 `AND ui.is_deleted = 0` 及 `<if test='tagId != null'>` EXISTS 条件
  - `UserInteractionService.java` — 接口签名 `getCollections` 增加 `Long tagId` 参数；`getLikes` 增加 `Long tagId` 参数
  - `UserInteractionServiceImpl.java` — 注入 `ArtworkTagRelationService` 和 `TagService`；新增私有方法 `attachTagsToArtworks` 遍历填充标签；`getCollections` / `getLikes` 方法签名及实现均同步增加 `tagId` 并透传给 Mapper
  - `UserInteractionController.java` — `getCollections` 和 `getLikes` 两接口增加 `@RequestParam(required = false) Long tagId` 参数，透传给 Service
- **遗留问题/下一步**：前端接入 `?tagId=xxx` 参数实现标签筛选 UI 联动；`attachTagsToArtworks` 存在 N+1 查询问题，后续可优化为批量一次查出所有作品的标签关联

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
- **遗留问题/下一步**：前端"关注流"Tab 与全局信息流 Tab 切换；前端展示关注/取关按钮并调用 `/api/follow/toggle` 和 `/api/follow/check`；前端实现排序 Tab（推荐/最新）切换并传入对应 `sortType`；前端热门分类 Tab 调用 `/api/category/public/hot`；前端搜索框调用 `/api/artwork/feed?keyword=xxx`；前端用户搜索下拉调用 `/api/user/public/search?keyword=xxx`

---



- **日期**：2026-03-23
- **完成功能**：新增用户关键字搜索接口
- **修改的文件**：
  - `UserMapper.java` — 新增 `searchUsers(String keyword)` 方法（模糊匹配 `nickname`/`username`，LIMIT 5），新增 `import org.apache.ibatis.annotations.Param`、`import org.apache.ibatis.annotations.Select`、`import java.util.List`
  - `UserController.java` — 新增 `GET /api/user/public/search` 公开接口，空关键字直接返回空列表，新增 `import com.creation.platform.mapper.UserMapper`、`import jakarta.annotation.Autowired private UserMapper userMapper`
- **前端使用**：`GET /api/user/public/search?keyword=关键字`，最多返回 5 条匹配用户（昵称或用户名含关键字即命中）

---

- **日期**：2026-03-23
- **完成功能**：信息流接口新增关键字搜索（模糊匹配标题 + 简介）
- **修改的文件**：
  - `ArtworkMapper.java` — `selectFeedPage` SQL 在 `WHERE` 后追加 `<if test='keyword != null and keyword != ""'> AND (a.title LIKE CONCAT('%', #{keyword}, '%') OR a.description LIKE CONCAT('%', #{keyword}, '%')) </if>`，方法签名新增 `@Param("keyword") String keyword`
  - `ArtworkService.java` — `getFeedPage` 签名增加 `String keyword`
  - `ArtworkServiceImpl.java` — `getFeedPage` 签名 + 实现均透传 `keyword` 给 Mapper
  - `ArtworkController.java` — `getFeed` 新增 `@RequestParam(required = false) String keyword`，文档注释更新
- **前端使用**：`GET /api/artwork/feed?keyword=关键字`，可与 `sortType`、`categoryId`、`isFollowFeed` 等参数叠加组合过滤

---

- **日期**：2026-03-23
- **完成功能**：新增热门分类接口（按已发布文章数倒序，取前8个）
- **修改的文件**：
  - `CategoryMapper.java` — 新增 `selectHotCategories()` 方法（LEFT JOIN + GROUP BY + COUNT + LIMIT 8），新增 `import java.util.List`
  - `CategoryService.java` — 新增 `List<Category> getHotCategories()` 接口
  - `CategoryServiceImpl.java` — 实现 `getHotCategories()`，直接委托 `baseMapper.selectHotCategories()`
  - `CategoryController.java` — 新增 `GET /api/category/public/hot` 公开接口
- **前端使用**：`GET /api/category/public/hot`，返回最多 8 个热门分类（已发布文章数最多的分类排在前面）

---

- **日期**：2026-03-23
- **完成功能**：全站强制登录，拦截器白名单收紧
- **修改的文件**：
  - `WebConfig.java` — `excludePathPatterns` 删除了所有业务公开接口（`/api/artwork/feed`、`/api/user/public/**` 等），仅保留登录/注册、静态资源、Swagger 文档四类路径
- **影响范围**：所有业务接口（信息流、分类、标签、作品详情、用户数据面板、关注/取关等）均强制要求携带有效 JWT Token，未登录请求统一返回 401


- **日期**：2026-03-23
- **完成功能**：信息流支持热度推荐排序
- **核心算法**：`热度分 = view_count * 1 + likeCount * 5 + commentCount * 10`，热度分相同时按时间倒序保序
- **修改的文件**：
  - `ArtworkMapper.java` — `selectFeedPage` 新增 `sortType` 参数，SQL 末尾改为 `<choose>` 动态 ORDER BY（`recommend` 执行热度公式，`time` 或其他走时间倒序）
  - `ArtworkService.java` — `getFeedPage` 签名增加 `String sortType`
  - `ArtworkServiceImpl.java` — `getFeedPage` 实现透传 `sortType` 给 Mapper
  - `ArtworkController.java` — `getFeed` 新增 `@RequestParam(defaultValue = "recommend") String sortType`，文档注释更新
- **前端使用方式**：
  - `GET /api/artwork/feed?sortType=recommend`（默认，热度推荐）
  - `GET /api/artwork/feed?sortType=time`（最新发布）
  - 两者均可与 `isFollowFeed`、`categoryId`、`tagId` 等参数叠加

---

- **日期**：2026-03-23
- **完成功能**：将密码加密从 MD5 全面替换为 BCryptPasswordEncoder
- **核心技术点**：
  - 引入 `spring-security-crypto` 轻量级依赖（仅加密模块，不引入整个 starter）
  - `BCryptPasswordEncoder` 使用 BCrypt 强哈希算法，自带盐值，支持自适应计算强度（默认 cost=10），相较 MD5 不可逆且防彩虹表攻击
  - 注册时：`passwordEncoder.encode(password)` 加密新密码并存储
  - 登录时：`passwordEncoder.matches(rawPassword, encodedPassword)` 进行安全比对，底层自动提取盐值重新计算哈希后对比，无需手动处理盐
  - `passwordEncoder` 以 `private static final` 单例模式持有，避免重复创建开销
- **修改的文件**：
  - `pom.xml` — 新增 `spring-security-crypto` 依赖
  - `UserServiceImpl.java` — 移除 `DigestUtils` import，新增 `BCryptPasswordEncoder` import 及静态实例，改造 `register` 和 `login` 方法中的密码处理逻辑
- **遗留问题/下一步**：现有用户的 MD5 密码无法直接用于 BCrypt 登录，需设计密码迁移策略（如首次登录时自动用 BCrypt 重写密码）；旧数据库中的 MD5 密码需要逐步引导用户修改密码后完成迁移

---

- **日期**：2026-03-24
- **完成功能**：新增修改密码接口 `PUT /api/user/password`（需登录）
- **核心技术点**：
  - 新建 `PasswordUpdateDTO` 作为请求体载体，包含 `oldPassword`（旧密码明文）和 `newPassword`（新密码明文）两个字段
  - Service 层复用已有的 `BCryptPasswordEncoder` 实例：`passwordEncoder.matches(raw, encoded)` 校验旧密码，`passwordEncoder.encode(raw)` 加密新密码，全程不暴露明文
  - 旧密码校验失败直接 `throw new RuntimeException("原密码错误")`，由全局异常处理器统一处理并返回客户端
  - 接口无返回值数据（`Result<Void>`），成功时直接返回提示语"密码修改成功，请重新登录"
- **新建的文件**：
  - `dto/PasswordUpdateDTO.java` — 新建，`oldPassword` / `newPassword` 两个字段，使用 `@Data` Lombok 注解
- **修改的文件**：
  - `UserService.java` — 新增 `boolean updatePassword(Long userId, PasswordUpdateDTO dto)` 接口声明
  - `UserServiceImpl.java` — 实现 `updatePassword`：查询用户 → BCrypt 校验旧密码 → BCrypt 加密新密码 → `updateById` 保存
  - `UserController.java` — 新增 `PUT /api/user/password` 接口，需登录，从 `HttpServletRequest` 提取 `userId` 注入 Service
- **遗留问题/下一步**：前端设置页接入该接口；可考虑新增密码强度校验（长度、字符类型等）；建议旧 MD5 密码用户首次登录时强制引导修改密码

---

- **日期**：2026-03-24
- **完成功能**：User 实体新增 6 个扩展字段，配套 DTO 拆分及 Settings 接口
- **核心技术点**：
  - `User.java` 新增 6 个字段（`phone`、`bio`、`gender`、`hideCollections`、`disableNotifications`、`watermark`），所有字段均为"可空/有默认值"的灵活设计，不影响已有数据
  - `UserUpdateDTO` 扩展支持 `bio`、`gender`，专用于"个人资料"更新（昵称、头像、简介、性别）
  - 新建 `UserSettingsDTO`，包含 `phone`、`hideCollections`、`disableNotifications`、`watermark`，专用于"账号设置"更新（手机号、隐私偏好、通知偏好、水印偏好）
  - 两个 DTO 职责分离，Controller 层独立暴露 `PUT /profile`（资料）和 `PUT /settings`（设置）两条路径，职责边界清晰
  - `updateProfile` 在原有的 `nickname`/`email`/`avatarUrl` 基础上追加了 `bio` 和 `gender` 的判空赋值
  - `updateSettings` 使用"构造 User 实体 + 只 set 需要修改的字段"策略，避免查询开销；`MyMetaObjectHandler` 的自动填充机制会忽略 null 字段，保证其他字段不被意外覆盖
- **新建的文件**：
  - `dto/UserSettingsDTO.java` — 新建，包含 `phone`/`hideCollections`/`disableNotifications`/`watermark` 四个隐私偏好字段
- **修改的文件**：
  - `entity/User.java` — 在 `email` 字段后追加 6 个新字段（含注释）
  - `dto/UserUpdateDTO.java` — 新增 `bio`、`gender` 两个字段
  - `UserService.java` — 新增 `boolean updateSettings(Long userId, UserSettingsDTO dto)` 接口声明
  - `UserServiceImpl.java` — `updateProfile` 追加 `bio`/`gender` 赋值；新增 `updateSettings` 实现（构造 User → set 四个字段 → updateById）
  - `UserController.java` — 新增 `PUT /api/user/settings` 接口，需登录，成功返回"设置更新成功"

---

- **日期**：2026-03-24
- **完成功能**：新增获取用户收藏列表接口 `GET /api/interaction/collections/{userId}`，集成隐私保护逻辑
- **核心技术点**：
  - `ArtworkMapper.selectCollectedArtworks`：`INNER JOIN user_interaction` 按 `interaction_type=2`（收藏）过滤，`ORDER BY ui.create_time DESC` 按收藏时间倒序
  - `UserInteractionServiceImpl.getCollections` 隐私三层判断：①用户不存在返回 404；②非本人且对方 `hideCollections=1` 返回 403 "该用户已隐藏收藏列表"；③否则正常返回收藏列表
  - `Integer.valueOf(1)` 替代直接比较 `==`，规避自动拆箱 NPE
  - Service 层注入 `UserMapper`（用于查询目标用户隐私设置）和 `ArtworkMapper`（用于执行收藏列表 SQL）
- **修改的文件**：
  - `ArtworkMapper.java` — 新增 `selectCollectedArtworks` 方法（`@Select` 联表查询）
  - `UserInteractionService.java` — 新增 `Result<List<Artwork>> getCollections(Long targetUserId, Long currentUserId)` 接口声明
  - `UserInteractionServiceImpl.java` — 注入 `UserMapper`、`ArtworkMapper`；实现 `getCollections` 隐私检查 + 收藏列表查询
  - `UserInteractionController.java` — 新增 `GET /api/interaction/collections/{userId}` 接口，需登录，从 `request` 提取 `currentUserId`

---

- **日期**：2026-03-24
- **完成功能**：新增点赞列表接口 `GET /api/interaction/likes/{userId}`，同时修正收藏列表 SQL 关联字段
- **核心技术点**：
  - `UserInteraction` 实体字段为 `artworkId`，而**非** `targetId`；之前收藏 SQL 错误地用了 `ui.target_id`，本次一并修正为 `ui.artwork_id`，确保收藏/点赞两条 SQL 均正确
  - `getLikes` 与 `getCollections` 对比：点赞列表默认公开，无需传 `currentUserId` 做隐私校验，直接查询返回即可；收藏列表受 `hideCollections` 字段控制
  - `selectLikedArtworks` 与 `selectCollectedArtworks` SQL 结构一致，仅 `interaction_type` 分别为 `1`（点赞）和 `2`（收藏）
- **修改的文件**：
  - `ArtworkMapper.java` — `selectCollectedArtworks` SQL 修正 `ui.target_id` → `ui.artwork_id`；新增 `selectLikedArtworks`（`interaction_type=1`）
  - `UserInteractionService.java` — 新增 `Result<List<Artwork>> getLikes(Long targetUserId)` 接口声明
  - `UserInteractionServiceImpl.java` — 实现 `getLikes`：查询用户存在性 → 调用 `artworkMapper.selectLikedArtworks` → `Result.success`
  - `UserInteractionController.java` — 新增 `GET /api/interaction/likes/{userId}` 接口，无需登录

---

- **日期**：2026-03-24
- **完成功能**：点赞/收藏列表返回类型由 `Artwork` 升级为 `ArtworkVO`，与"我的发布"Feed 展示字段完全对齐
- **核心技术点**：
  - `ArtworkVO` 相比 `Artwork` 额外包含 `authorName`（作者昵称）、`categoryName`（分类名），前端展示卡片时无需二次查询
  - `selectCollectedArtworks` 和 `selectLikedArtworks` SQL 均改为联表 `LEFT JOIN user u`、`LEFT JOIN category c`，显式列出目标字段而非 `SELECT a.*`，避免返回冗余字段
  - Mapper 返回类型 `List<Artwork>` → `List<ArtworkVO>`，Service 层 `List<Artwork>` → `List<ArtworkVO>`，Controller 层 `Result<List<Artwork>>` → `Result<List<ArtworkVO>>`，三层泛型同步修正，保持一致性
- **修改的文件**：
  - `ArtworkMapper.java` — 两条 SQL 均改为显式字段列表 + `LEFT JOIN` 联表，返回类型改为 `List<ArtworkVO>`
  - `UserInteractionService.java` — `getCollections`、`getLikes` 返回类型从 `Result<List<Artwork>>` 改为 `Result<List<ArtworkVO>>`
  - `UserInteractionServiceImpl.java` — 实现类对应方法同步改为 `List<ArtworkVO>`
  - `UserInteractionController.java` — 两接口返回类型同步改为 `Result<List<ArtworkVO>>`