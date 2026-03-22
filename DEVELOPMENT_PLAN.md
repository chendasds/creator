# Creation Platform 开发计划书

> 项目：个人创作记录与作品分享平台
> 技术栈：Spring Boot 3 + MyBatis-Plus + MySQL 8 + JWT + Vue 3
> 文档版本：v1.0 | 更新日期：2026-03-15

---

## 一、进度审计（已完成功能模块）

### 1.1 用户与认证模块 ✅
| 功能 | 状态 | 说明 |
|------|------|------|
| 用户注册 | ✅ 完成 | `POST /api/user/register` |
| 用户登录 | ✅ 完成 | `POST /api/user/login`，返回 JWT Token |
| JWT 拦截器鉴权 | ✅ 完成 | `JwtInterceptor`，支持 Token 续期 |
| 个人信息获取 | ✅ 完成 | `GET /api/user/profile`，脱敏返回 |
| 个人信息修改 | ✅ 完成 | `PUT /api/user/profile`，部分更新 |

### 1.2 作品管理模块 ✅
| 功能 | 状态 | 说明 |
|------|------|------|
| 作品发布 | ✅ 完成 | `POST /api/artwork/publish` |
| 作品列表（分页） | ✅ 完成 | `GET /api/artwork/feed` |
| 作品详情 | ✅ 完成 | `GET /api/artwork/detail/{id}`，含点赞/收藏状态 |
| 浏览量统计 | ✅ 完成 | `PUT /api/artwork/view/{id}` |
| 创作草稿管理 | ✅ 完成 | `CreationRecordController` 完整 CRUD |
| 作品审核（后台） | ✅ 完成 | `PUT /api/artwork/admin/audit` |
| 作品删除 | ⚠️ 待加固 | 已加 SQL 鉴权，但缺少级联删除子评论 |

### 1.3 互动模块 ✅
| 功能 | 状态 | 说明 |
|------|------|------|
| 点赞/收藏切换 | ✅ 完成 | `POST /api/interaction/toggle` |
| 互动状态查询 | ✅ 完成 | `GET /api/interaction/check` |

### 1.4 评论模块 ✅
| 功能 | 状态 | 说明 |
|------|------|------|
| 评论发布（支持楼中楼） | ✅ 完成 | `POST /api/comment/add` |
| 评论树查询 | ✅ 完成 | `GET /api/comment/list` |
| 评论安全删除（SQL 鉴权） | ✅ 完成 | `@Update` 带 `user_id` 条件 |
| 评论级联删除 | ✅ 完成 | `@Transactional` + 递归 `cascadeDeleteChildren` |

### 1.5 分类与标签模块 ✅
| 功能 | 状态 | 说明 |
|------|------|------|
| 分类管理 | ✅ 完成 | `CategoryController` CRUD |
| 标签管理 | ✅ 完成 | `TagController` CRUD |
| 作品标签关联 | ✅ 完成 | `ArtworkTagRelationController` |

### 1.6 文件上传模块 ⚠️
| 功能 | 状态 | 说明 |
|------|------|------|
| 图片上传 | ⚠️ 基本完成 | `POST /api/file/upload`，使用 local mock 服务 |

### 1.7 基础架构 ✅
| 功能 | 状态 | 说明 |
|------|------|------|
| 统一响应结构 | ✅ 完成 | `Result<T>` 类，code/message/data |
| 逻辑删除 | ✅ 完成 | `@TableLogic` 全局配置 |
| 自动填充 | ✅ 完成 | `MetaObjectHandler` 处理创建/更新时间 |
| 跨域配置 | ✅ 完成 | `WebMvcConfig` |

---

## 二、缺陷报告

### 2.1 高危缺陷（必须修复）

#### 🔴 缺陷 #1：UserController CRUD 接口鉴权缺失
**文件**: `UserController.java`
**描述**: 以下接口完全没有鉴权，任何人可调用：
- `GET /api/user/list` — 返回所有用户列表（含密码哈希）
- `GET /api/user/{id}` — 查看任意用户信息
- `POST /api/user` — 创建用户（绕过后台注册）
- `PUT /api/user` — 修改任意用户
- `DELETE /api/user/{id}` — 删除任意用户

**影响**: 任意访客可读写整个用户表，数据完全无安全边界。

```java
// 当前代码 - 无任何鉴权
@GetMapping("/list")
public List<User> list() {
    return userService.list();  // 返回完整用户列表，含密码哈希
}

@PutMapping
public boolean updateById(@RequestBody User user) {
    return userService.updateById(user);  // 可修改任意用户
}
```

**修复方案**: 这些是 MyBatis-Plus 自动生成的 CRUD，属于调试接口，应**直接删除**。完整的管理员操作用后台管理接口代替。

---

#### 🔴 缺陷 #2：ArtworkController 管理接口无权限校验
**文件**: `ArtworkController.java`
**描述**: `adminPage` 和 `audit` 是管理员接口，但没有校验当前用户是否为管理员。

```java
@GetMapping("/admin/page")
public Result<Page<ArtworkVO>> adminPage(...)  // 无 role 校验

@PutMapping("/admin/audit")
public Result<Boolean> audit(...)  // 无 role 校验
```

**影响**: 任何登录用户都能访问后台管理接口，操作用户作品。

**修复方案**: 在接口中校验 `user.getRole() == 2` 或使用 `@PreAuthorize("hasRole('ADMIN')")`。

---

#### 🔴 缺陷 #3：AdminIndexController 仪表盘无权限校验
**文件**: `AdminIndexController.java`
**描述**: 仪表盘接口无鉴权，可被任意登录用户访问。

```java
@GetMapping("/dashboard")
public Result<DashboardVO> dashboard(HttpServletRequest request) {
    Long userId = (Long) request.getAttribute("userId");
    // 缺少 role == 2 的校验
}
```

---

#### 🔴 缺陷 #4：UserServiceImpl 使用 MD5 加密密码
**文件**: `UserServiceImpl.java`
**描述**: 使用 `DigestUtils.md5DigestAsHex` 加密密码，该算法已被证明不安全。

```java
String encryptedPassword = DigestUtils.md5DigestAsHex((password + "").getBytes());
```

**影响**: 数据库泄漏后密码可被彩虹表快速破解。

**修复方案**: 改用 Spring Security BCryptPasswordEncoder。

---

### 2.2 中危缺陷（建议修复）

#### 🟡 缺陷 #5：浏览量更新存在并发覆盖
**文件**: `ArtworkServiceImpl.java`
**描述**: `incrementViewCount` 先查后改，在高并发下会丢失更新。

```java
Artwork artwork = artworkMapper.selectById(id);
artworkVO.setViewCount(artworkVO.getViewCount() + 1);
artworkMapper.updateById(artwork);  // 读写分离，并发丢失
```

**修复方案**: 使用 SQL 原子更新：
```java
@Update("UPDATE artwork SET view_count = view_count + 1 WHERE id = #{id}")
int incrementViewCount(Long id);
```

---

#### 🟡 缺陷 #6：ArtworkServiceImpl 存在 N+4 查询
**文件**: `ArtworkServiceImpl.java`
**描述**: 获取作品详情时执行了 4 次独立的 count 查询。

```java
long likedCount = interactionMapper.selectCount(likedWrapper);       // 查询1
long collectedCount = interactionMapper.selectCount(collectedWrapper); // 查询2
long likeCount = interactionMapper.selectCount(likeCountWrapper);     // 查询3
long collectCount = interactionMapper.selectCount(collectCountWrapper); // 查询4
```

**修复方案**: 使用一条 SQL 一次性统计所有数据：
```sql
SELECT
    SUM(CASE WHEN interaction_type = 1 THEN 1 ELSE 0 END) as like_count,
    SUM(CASE WHEN interaction_type = 2 THEN 1 ELSE 0 END) as collect_count,
    SUM(CASE WHEN interaction_type = 1 AND user_id = #{userId} THEN 1 ELSE 0 END) as liked,
    SUM(CASE WHEN interaction_type = 2 AND user_id = #{userId} THEN 1 ELSE 0 END) as collected
FROM user_interaction
WHERE artwork_id = #{artworkId} AND is_deleted = 0
```

---

#### 🟡 缺陷 #7：FileController 无文件安全校验
**文件**: `FileController.java`
**描述**: 没有文件类型、文件大小、文件内容的校验。

**影响**: 可上传任意文件（包括 `.jsp`、`.html` 恶意脚本），可能造成安全漏洞。

**修复方案**:
```java
// 1. 文件类型白名单
private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");

// 2. UUID 生成文件名
String savedFileName = UUID.randomUUID() + "." + extension;

// 3. 文件大小精确校验
if (file.getSize() > 5 * 1024 * 1024) {
    return Result.error(400, "文件大小不能超过5MB");
}
```

---

#### 🟡 缺陷 #8：category/tag/artwork 等基础 CRUD 接口无权限校验
**文件**: 各 `*Controller.java`
**描述**: CategoryController、TagController、CreationRecordController 等的 save/update/remove 方法无鉴权。

| 文件 | 受影响接口 |
|------|-----------|
| `CategoryController.java` | save, updateById, removeById |
| `TagController.java` | 所有 CRUD |
| `ArtworkTagRelationController.java` | setTags |
| `CommentController.java` | updateById |

---

### 2.3 低危缺陷（可优化）

#### 🟢 缺陷 #9：Result 类泛型使用不一致
**文件**: `Result.java`
**描述**: 部分接口直接返回 `Result.error(code, message)`，部分返回 `Result.success("msg")`。

---

#### 🟢 缺陷 #10：缺少全局异常处理
**描述**: 没有 `@ControllerAdvice` 统一处理运行时异常，抛出 RuntimeException 会直接暴露堆栈。

**修复方案**:
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntime(RuntimeException e) {
        return Result.error(500, e.getMessage());
    }
}
```

---

#### 🟢 缺陷 #11：缺少参数校验
**描述**: DTO 类无 `@NotBlank`、`@NotNull` 等校验注解。

**修复方案**: 在 `UserUpdateDTO`、`CommentDTO`、`ArtworkPublishDTO` 等类上添加：
```java
@NotBlank(message = "昵称不能为空")
private String nickname;
```

---

#### 🟢 缺陷 #12：创作草稿 (creation_record) 与正式作品 (artwork) 关系模糊
**描述**: 两张表结构高度相似（都有 title/content/description/word_count），缺少业务边界说明。作品发布时没有从草稿转换的逻辑。

---

## 三、后续计划（Todo List）

### Phase 1: 安全加固（优先级 P0）

- [ ] **T1.1** 删除 UserController 中的调试 CRUD 接口（getById, list, save, updateById, removeById）
- [ ] **T1.2** 给所有管理接口添加角色校验（role == 2），包括 adminPage、audit、dashboard
- [ ] **T1.3** 迁移密码加密：从 MD5 改为 BCryptPasswordEncoder
- [ ] **T1.4** 添加参数校验：给所有 DTO 添加 @Valid 注解和校验注解
- [ ] **T1.5** 添加全局异常处理器 GlobalExceptionHandler
- [ ] **T1.6** 文件上传安全：添加文件类型校验、大小校验、UUID 文件名

### Phase 2: 性能优化（优先级 P1）

- [ ] **T2.1** 修复浏览量并发问题：改用 SQL 原子更新 `view_count = view_count + 1`
- [ ] **T2.2** 修复作品详情的 N+4 查询：合并为单条统计 SQL
- [ ] **T2.3** 评论查询优化：考虑添加 Redis 缓存热门作品评论树
- [ ] **T2.4** 作品列表分页添加缓存

### Phase 3: 业务完善（优先级 P2）

- [ ] **T3.1** 实现"从草稿发布作品"功能（creation_record → artwork）
- [ ] **T3.2** 添加评论级联删除的深度限制，防止递归过深
- [ ] **T3.3** 实现"我的作品"接口：`GET /api/artwork/my`
- [ ] **T3.4** 添加搜索功能：按标题/内容关键词搜索作品
- [ ] **T3.5** 实现收藏列表接口：`GET /api/interaction/collections`
- [ ] **T3.6** 添加"我的仪表盘"：展示个人作品统计、互动统计

### Phase 4: 简历亮点功能（优先级 P2）

- [ ] **T4.1** 接入阿里云/百度 AI 开放平台，实现**AI 写作助手**（根据标题生成创作大纲）
- [ ] **T4.2** 接入 AI 对话，对作品进行**智能分析摘要**（存储在 ai_summary 字段）
- [ ] **T4.3** 添加**操作日志**记录：记录用户关键操作（发布、删除、修改），写入日志表
- [ ] **T4.4** 实现**消息通知**功能：被点赞、评论时通知用户
- [ ] **T4.5** 添加**关注功能**：用户之间可以互相关注

### Phase 5: 基础设施（优先级 P3）

- [ ] **T5.1** 添加 Redis 缓存层（登录 Token、热门作品数据）
- [ ] **T5.2** 编写 Swagger/OpenAPI 接口文档
- [ ] **T5.3** 配置 Spring Boot Actuator 监控接口
- [ ] **T5.4** 添加接口限流（防止刷接口）
- [ ] **T5.5** 编写单元测试（至少覆盖 Service 层关键业务逻辑）

---

## 四、技术建议（毕设答辩加分项）

### 💡 建议一：AI 辅助创作功能（核心亮点）

**实现方案**：接入阿里云 DashScope 或百度千帆 API，在用户发布作品后自动生成"AI 分析摘要"存入 `artwork.ai_summary` 字段。

```java
// 示例接口设计
@PostMapping("/api/artwork/{id}/analyze")
public Result<String> analyzeArtwork(@PathVariable Long id) {
    Artwork artwork = artworkService.getById(id);
    String summary = aiService.generateSummary(artwork.getTitle(), artwork.getContent());
    artwork.setAiSummary(summary);
    artworkService.updateById(artwork);
    return Result.success(summary);
}
```

**答辩话术**：
> "系统接入了阿里云大模型 API，当用户发布作品后，可以调用 AI 对作品进行多维度分析，包括内容主题提炼、创作风格评估和优化建议。AI 生成的分析结果会存储到数据库中，用户可以在作品详情页查看。"

**所需时间**: 约 1 天

---

### 💡 建议二：Redis 热点数据缓存（技术深度体现）

**实现方案**：对访问频繁的数据添加 Redis 缓存。

```java
@Cacheable(value = "artwork:feed", key = "#page + ':' + #size")
public Page<ArtworkVO> getFeed(int page, int size) { ... }

@CacheEvict(value = "artwork:feed", allEntries = true)
public void publishArtwork(...) { ... }
```

**答辩话术**：
> "考虑到作品列表是高并发访问的接口，我在这一层加入了 Redis 缓存。当作品列表查询时，先从 Redis 中获取缓存数据，如果缓存未命中再查数据库，并将结果写入缓存。同时，当有新作品发布或作品数据变化时，我会主动清除对应的缓存，保证数据一致性。"

**所需时间**: 约 1-2 天

---

### 💡 建议三：全局操作日志与数据追踪（工程规范体现）

**实现方案**：使用 Spring AOP 拦截所有写操作，记录操作日志。

```java
@Aspect
@Component
public class OperationLogAspect {
    @Around("@annotation(OperationLog)")
    public Object logOperation(ProceedingJoinPoint point) throws Throwable {
        // 记录操作人、操作类型、操作内容、操作时间
        operationLogService.save(buildLog(point));
        return point.proceed();
    }
}
```

**答辩话术**：
> "我在系统中设计了一套操作日志记录机制。通过 Spring AOP 切面编程，自动拦截所有增删改操作，将操作人、操作类型、操作详情和时间记录到日志表中。这样既满足了审计需求，也体现了工程的规范性。"

**所需时间**: 约 1 天

---

## 五、数据库设计评估

### 优点 ✅
1. **字符集正确**: 全部使用 `utf8mb4`，支持 emoji 和特殊字符
2. **逻辑删除**: 所有表都有 `is_deleted` 字段，配合 `@TableLogic` 实现软删除
3. **索引合理**: artwork 表有 `idx_create_time` 支持排序查询，`user_interaction` 有复合唯一键防重复
4. **字段命名规范**: 蛇形命名，有注释，有统一的时间字段规范

### 待改进点 ⚠️
1. **user 表注释说 BCrypt，但代码用 MD5**: database.sql 注释提到 BCrypt，但实际 UserServiceImpl 用的是 MD5，**必须统一**
2. **creation_record 和 artwork 高度重复**: 两张表都有 title/content/description/word_count，建议合并或明确边界
3. **缺少外键约束**: 表之间没有 FOREIGN KEY，高级联删除/更新需要代码层面保证一致性
4. **comment 表没有索引**: 在 article_id + is_deleted 上缺少联合索引，影响按作品查询评论的性能

---

## 六、接口文档速查

| 模块 | 接口 | 方法 | 鉴权 | 状态 |
|------|------|------|------|------|
| 用户 | `/api/user/register` | POST | 无 | ✅ |
| 用户 | `/api/user/login` | POST | 无 | ✅ |
| 用户 | `/api/user/profile` | GET | JWT | ✅ |
| 用户 | `/api/user/profile` | PUT | JWT | ✅ |
| 作品 | `/api/artwork/feed` | GET | 无 | ✅ |
| 作品 | `/api/artwork/detail/{id}` | GET | 无 | ✅ |
| 作品 | `/api/artwork/publish` | POST | JWT | ✅ |
| 作品 | `/api/artwork/admin/page` | GET | JWT | ⚠️ 需加 role 校验 |
| 作品 | `/api/artwork/admin/audit` | PUT | JWT | ⚠️ 需加 role 校验 |
| 互动 | `/api/interaction/toggle` | POST | JWT | ✅ |
| 互动 | `/api/interaction/check` | GET | JWT | ✅ |
| 评论 | `/api/comment/add` | POST | JWT | ✅ |
| 评论 | `/api/comment/list` | GET | 无 | ✅ |
| 评论 | `/api/comment/{id}` | DELETE | JWT | ✅ |
| 文件 | `/api/file/upload` | POST | 无 | ⚠️ 需加固 |
| 管理 | `/api/admin/dashboard` | GET | JWT | ⚠️ 需加 role 校验 |

---

*本计划书将随项目迭代持续更新*
