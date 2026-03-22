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
