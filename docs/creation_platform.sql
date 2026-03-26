/*
 Navicat Premium Data Transfer

 Source Server         : root
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : localhost:3306
 Source Schema         : creation_platform

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 25/03/2026 10:44:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for artwork
-- ----------------------------
DROP TABLE IF EXISTS `artwork`;
CREATE TABLE `artwork`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '作品ID',
  `user_id` bigint(0) NOT NULL COMMENT '作者ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作品标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '作品正文内容',
  `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图URL',
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作品简介/创作说明',
  `category_id` bigint(0) DEFAULT NULL COMMENT '所属分类ID',
  `ai_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'AI生成的作品分析与摘要（后续LLM接入使用）',
  `view_count` int(0) NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `word_count` int(0) DEFAULT 0 COMMENT '作品字数',
  `status` tinyint(0) NOT NULL DEFAULT 1 COMMENT '作品状态: 1-已发布 0-草稿',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_category_id`(`category_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '正式作品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of artwork
-- ----------------------------
INSERT INTO `artwork` VALUES (1, 4, '我的第一部史诗巨作', '测试测试', NULL, NULL, 6, NULL, 12, 0, 1, '2026-03-18 16:27:34', '2026-03-25 10:36:27', 0);
INSERT INTO `artwork` VALUES (2, 1, '深入理解 Java 并发编程', '这里是并发编程的基础内容...', 'https://picsum.photos/seed/java/400/300', '万字长文解析JUC底层原理', 2, '本文详细解析了Java并发编程中的核心概念。', 1253, 8500, 1, '2026-03-18 16:45:58', '2026-03-24 18:01:49', 0);
INSERT INTO `artwork` VALUES (3, 1, '进击的巨人最终季观后感', '塔塔开！一直塔塔开！...', 'https://picsum.photos/seed/titan/400/300', '献出心脏吧！', 6, '作者抒发了对该番剧完结的强烈震撼与不舍。', 3223, 1500, 1, '2026-03-18 16:45:58', '2026-03-25 10:29:12', 0);
INSERT INTO `artwork` VALUES (4, 1, '海边的卡夫卡读书笔记', '正文还在构思中...', NULL, '村上春树代表作读后感', 1, NULL, 1, 0, 0, '2026-03-18 16:45:58', '2026-03-22 17:19:15', 0);
INSERT INTO `artwork` VALUES (5, 1, '2026年春季京都行', '樱花开得很美，记录一下...', 'https://picsum.photos/seed/kyoto/400/300', '记录一次说走就走的旅行', 3, '一篇充满生活气息的游记。', 562, 2000, 1, '2026-03-18 16:45:58', '2026-03-22 17:12:03', 0);
INSERT INTO `artwork` VALUES (6, 1, 'MyBatis-Plus 踩坑实录', '今天遇到了逻辑删除和唯一索引的冲突...', 'https://picsum.photos/seed/code/400/300', '记录开发个人创作平台时遇到的真实Bug', 4, '技术排错记录，主要涉及后端框架。', 124, 3000, 1, '2026-03-18 16:45:58', '2026-03-22 17:18:57', 0);
INSERT INTO `artwork` VALUES (7, 1, '某科学的超电磁炮设定解析', '正文内容未完成...', 'https://picsum.photos/seed/railgun/400/300', '深度解析学园都市的超能力设定', 6, NULL, 2, 0, 0, '2026-03-18 16:45:58', '2026-03-22 17:09:54', 0);
INSERT INTO `artwork` VALUES (8, 1, 'C语言指针的艺术', '指针是C语言的灵魂...', NULL, '从内存角度理解指针', 2, 'C语言基础进阶教程', 881, 4200, 1, '2026-03-18 16:45:58', '2026-03-22 16:38:05', 0);
INSERT INTO `artwork` VALUES (9, 1, '手撕平衡二叉树：从 AVL 到红黑树', '二叉树的旋转逻辑是很多人的噩梦...', 'https://picsum.photos/seed/tree/400/300', '深度解析树形结构的平衡算法与应用场景', 4, '本文通过图解方式详细介绍了平衡二叉树的底层原理。', 455, 5200, 1, '2026-03-19 17:19:41', '2026-03-25 10:36:46', 0);
INSERT INTO `artwork` VALUES (10, 1, '恶之花的低语：重读波德莱尔', '在那阴郁的巴黎街头，美与丑交织...', 'https://picsum.photos/seed/poetry/400/300', '《巴黎的忧郁》与现代性美学的碰撞', 1, '对波德莱尔诗歌风格及其历史背景的文学评论。', 218, 3100, 1, '2026-03-19 17:19:41', '2026-03-25 10:36:52', 0);
INSERT INTO `artwork` VALUES (11, 1, '自制操作系统的第一步：真模式与保护模式', '从 boot.asm 开始，我们要接管 CPU...', 'https://picsum.photos/seed/os/400/300', '从 8086 汇编到内核加载的硬核记录', 2, '系统级编程实践，涉及计算机底层架构。', 784, 12000, 1, '2026-03-19 17:19:41', '2026-03-21 11:52:51', 0);
INSERT INTO `artwork` VALUES (12, 1, '记忆宫殿：如何通过空间联想背下 3000 单词', '在你的脑海里建立一座名为“考研”的图书馆...', NULL, '利用心理学技巧高效备战英语词汇', 4, '介绍了一种科学的英语单词记忆方法。', 1504, 2600, 1, '2026-03-19 17:19:41', '2026-03-20 16:42:18', 0);
INSERT INTO `artwork` VALUES (13, 1, '当浅野一二 O 遇见伊藤润二：AI 绘画的风格融合', '通过 LoRA 模型，我尝试将两种极端的画风结合...', 'https://picsum.photos/seed/aiart/400/300', '实验记录：使用 Stable Diffusion 探索漫画风格边界', 3, '一次关于 AI 生成艺术与漫画美学的技术实践。', 962, 1800, 1, '2026-03-19 17:19:41', '2026-03-20 10:55:50', 0);
INSERT INTO `artwork` VALUES (14, 1, '用 Canvas 实现一个经典的推箱子游戏', '逻辑很简单，但关卡设计是灵魂...', 'https://picsum.photos/seed/game/400/300', 'Web 游戏开发入门实战：Sokoban 复刻版', 2, '基于前端技术的简单逻辑游戏实现教程。', 340, 4500, 1, '2026-03-19 17:19:41', '2026-03-21 10:31:49', 0);
INSERT INTO `artwork` VALUES (15, 1, '2026 考研倒计时：在代码与单词间寻找平衡', '凌晨两点的图书馆，只有键盘声和呼吸声...', NULL, '记录这段孤独但充满希望的备考时光', 3, '博主的考研心路历程分享。', 2244, 1200, 1, '2026-03-19 17:19:41', '2026-03-24 22:33:09', 0);
INSERT INTO `artwork` VALUES (16, 5, '伊朗现在是个什么情况呢？', '<p>1、总统<strong>佩泽希奇</strong>杨领导的文官政府完全控制不了革命卫队，不少网友猜测他是伊奸，我觉得暂时不好说。</p><p>2、伊朗目前可能权利最大的，是拉里贾尼，因为他能深刻影响小哈梅内伊和革命卫队，别看他只是个“秘书”。</p><p>3、小哈梅内伊才56岁，被选为最高领袖实际上打破了很多伊朗的传统。但他比他爹可强硬的多，也保守的多。然后他也被炸了。他跟内贾德还有一段“爱恨情仇”。</p><p>4、革命卫队目前的最高指挥官是艾哈迈德瓦西迪，他是圣城旅首任指挥官，堪称目前为止少有的元老级人物了。他还大力推动了伊朗导弹的发展，因为这哥们儿主修电气工程的。。。。当然了，绝对强硬派，常年被通缉，对美属于恐怖分子那种。</p><p>5、湾湾的帅化民说了一点我很赞同，伊朗估计早就明白他的空军海军一旦开战指定没啥用，所以压根儿人家也不怎么投资，全力研究导弹和导弹袭城战。目前看效果太明显了。伊朗不全是酒囊饭袋。</p><p>6、伊朗噶掉的那帮高层，现在看在伊朗那都是属于相对“温和派”，甚至“投降派”了。现在上来的全是有深沉大恨的强硬派，保守派。</p><p>题外话，内塔尼亚胡和特朗普估计这会儿都麻了，真是踢到钢板了<br><br></p>', 'https://pic1.zhimg.com/v2-409edae12ae38418eaf533266d8a1680_r.jpg?source=1def8aca', '伊朗局势', 1, NULL, 34, 532, 1, '2026-03-20 11:19:00', '2026-03-25 10:37:16', 0);
INSERT INTO `artwork` VALUES (17, 5, '伊朗', '<p><img src=\"https://pica.zhimg.com/80/v2-eb9766d69aa1ccb3a232a2c26ff8f0b7_1440w.webp?source=1def8aca\" alt=\"伊朗\" data-href=\"https://pica.zhimg.com/80/v2-eb9766d69aa1ccb3a232a2c26ff8f0b7_1440w.webp?source=1def8aca\" style=\"\"/></p><p><br></p><hr/><p><br></p><table style=\"width: auto;\"><tbody><tr><th colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></th><th colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></th><th colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></th><th colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></th><th colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></th></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td></tr><tr><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td><td colSpan=\"1\" rowSpan=\"1\" width=\"auto\"></td></tr></tbody></table><p>😁</p><p><br></p>', '', '伊朗', 1, NULL, 21, 1520, 1, '2026-03-20 11:32:28', '2026-03-22 17:17:35', 0);
INSERT INTO `artwork` VALUES (18, 5, '随便发', '<p><img src=\"http://localhost:45757/uploads/e2e4800cab58481f8a320128a09fabb6.png\" alt=\"\" data-href=\"\" style=\"\"/></p><p><img src=\"http://localhost:45757/uploads/3e5424e176034374a14d743c88280cae.png\" alt=\"\" data-href=\"\" style=\"\"/></p><p>随便发</p>', '', '随便发', 3, NULL, 46, 242, 1, '2026-03-20 11:51:41', '2026-03-24 22:29:25', 0);
INSERT INTO `artwork` VALUES (19, 6, '测试', '<p><img src=\"http://localhost:45757/uploads/12829fa202744155b2ba51e44ab21ebf.png\" alt=\"\" data-href=\"\" style=\"\"/></p><p><img src=\"http://localhost:45757/uploads/bff10fa1d7bd4e088e24f28148f3db74.jpg\" alt=\"\" data-href=\"\" style=\"\"/></p><p>测试测试</p>', '', '测试测试', 1, NULL, 16, 243, 1, '2026-03-22 17:11:42', '2026-03-25 10:19:28', 0);
INSERT INTO `artwork` VALUES (20, 6, '如何理解明朝是成也内阁败也内阁？', '<p><br></p><p>因为权力只会转移、不会消失；废了丞相之后，相权又到了哪里呢？</p><p>明代给出的答案是，到了皇帝手中。</p><p>废丞相、增皇权，这毫无疑问。侵夺相权一直是扩大皇权的重要途径，千年以来一直如此，明代只是更进一步，废除了丞相而已。</p><p>问题在于，<strong>权力越大，对能力的要求越大，而明代二太以外的皇帝都没有能力、或没有意识去行使空前庞大的皇权</strong>。</p><p>你可以把成熟的官僚系统当成一台精密的电脑，你不懂玩机技巧，电脑就是个看影视的播放器；你稍微懂点玩机技巧，excel也能帮你解决蛮多问题；你会编程语言，那么能做的事情就更多了，甚至可以自己编写程序、上限不可估量。<br><img src=\"http://localhost:45757/uploads/25ccc18f84c744b3b2255c8da9aae539.png\" alt=\"\" data-href=\"\" style=\"\"/><br><img src=\"http://localhost:45757/uploads/fa11cc0f5b95444eac165a1fa9d78ab8.png\" alt=\"\" data-href=\"\" style=\"\"/></p><p><img src=\"http://localhost:45757/uploads/4a751ded927e41b49be0e844c5951751.png\" alt=\"\" data-href=\"\" style=\"\"/></p><p><img src=\"http://localhost:45757/uploads/eca118271da945bfabf0b63994834ffc.png\" alt=\"\" data-href=\"\" style=\"\"/></p>', 'https://picx.zhimg.com/v2-b64dd4670a229666b2534baf958be584_720w.jpg?source=d16d100b', '因为权力只会转移、不会消失；废了丞相之后，相权又到了哪里呢？明代给出的答案是，到了皇帝手中。废丞相、增皇权，这毫无疑问。侵夺相权一直是扩大皇权的重要途径，千年以来一直如此，明代只是更进一步，废除了丞相而已。问题在于，权力越大，对能力的要求越大，而明代二太以外的皇帝都没有能力、或没有意识去行使空前庞大的皇权。你可以把成熟的官僚系统当成一台精密的电脑，你不懂玩机技巧，电脑就是个看影视的播放器；你稍微懂', 1, NULL, 12, 780, 1, '2026-03-22 18:43:58', '2026-03-25 10:42:47', 0);
INSERT INTO `artwork` VALUES (21, 6, '年轻的时候做什么事情对后半生有巨大的好处？', '<p><img src=\"http://localhost:45757/uploads/8105e2d43f8d45f3bacf6db47977dd31.png\" alt=\"\" data-href=\"\" style=\"\"/></p><p>之前看到的</p>', '', '之前看到的', 3, NULL, 19, 128, 1, '2026-03-22 18:53:52', '2026-03-25 10:36:58', 0);
INSERT INTO `artwork` VALUES (22, 6, '互联网晚报', '<p><br></p><p>我现在严重怀疑冯梦龙写下这系列故事的<strong>初心主旨</strong></p><p>杜十娘怒沉百宝箱，一直以来都视为一个反封建故事，还上了教科书<br>花魁杜十娘一片真心托付给举子李甲，后者却因为惧怕封建家族父母辜负杜十娘，最终有了怒沉百宝箱的结局。</p><p>但随着时代发展，大家突然发现，古典时代的文学著作，有了新的视角。<br>因为冯梦龙的故事不止一个杜十娘，还写了王美娘，也就是“卖油郎独占花魁”那个故事。<br>然后大家发现，这一系列故事，某种意义上可以视为古典女子婚恋教科书<br>冯梦龙又名大曲曲了属实是</p><p>卖油郎独占花魁这个故事里的女主王美娘，也是名动临安的貌美花魁，一开始也看不上卖油郎。<br>但是她比较有数，她明确意识到，自己的筹码并不多<br>王公子弟确实一掷千金，出手阔绰，但王公子弟，临安富商们对她未必有真心，万一所托非人，直接万劫不复。<br>哪怕对方不坏，富豪之家，名门大族，她一个青楼女子嫁进去后也只能为奴做妾。<br>而秦重这个卖油郎，虽然出身低微，但一片真心，对于她来说，这反而是一个更优解。<br>所以王美娘将自己积蓄拿出来，加上秦重全力以赴，最终成功赎身，拿到圆满结局。</p><p>反观杜十娘这边呢？<br>在当代描述中，李甲是不敢反抗封建家庭的富家公子，但现在大家发现，其实李甲恰恰也是最适合杜十娘的人。<br>李甲是太学生，还是名门望族，他的身世背景，也是能帮杜十娘完成赎身的人，并且他愿意把所有钱给杜十娘，甚至当掉衣服，对于一个太学生来说，这是压上所有前途了。也就是李甲对杜十娘也是全心全意的。<br>很多人说李甲懦弱，不敢反抗家庭，笑话，要是他有主见，一个布政使之子，还是太学生，会成天和青楼花魁混一块吗？<br>所以李甲就是最适合杜十娘的人，家族足够显赫，又什么都听杜十娘，傻白甜富二代了。<br>换成曲曲那边，看见这种案例，直接要笑开花</p><p>这个时候，杜十娘要想赎身拿到好结局，最佳办法就是拿出钱财，指挥李甲去运作赎身。<br>李甲家世显赫没主见，但你杜十娘不是有主见吗？你直接指挥他去操作啊，该运作运作，该给钱给钱，<br>你们两加起来什么都有了，这都天胡开局了，你还等什么呢？<br>是，李甲的家族那关不好过，但李甲是听你的呀</p><p>上策，发挥花魁人脉，帮李甲运作功名，双赢之下，李甲家族可能会接受<br>中策，让李甲帮忙赎身，然后自己买宅子住着，李甲家族可能睁一只眼闭一只眼<br>下策，让李甲赎身，然后算清钱财，各自走人。</p><p>然后我们的杜十娘坐拥这个局面，在干什么呢？<br>继续测试李甲的真心~看他是否一片真心<br>李甲作为富家公子常年被骂，但现在大家发现，李甲这完全背锅，因为他就正常发挥啊，<br>他没主见，所以愿意找杜十娘，但也因为没主见，不敢违抗家族，这和真不真心没关系，从始至终他就是这样的人，完全正常发挥。<br>倒是你杜十娘自己，拿着百宝箱巨款，坐拥神装ADC，在这泉水挂机玩女频呢？</p><p>有钱有人的天胡局面下，就是要让李甲抗压，搞人性测试，最后李甲崩了，她自己也没出路了。<br>所以杜十娘的悲剧杜十娘自己背大锅，这个合理吧？</p><p>冯梦龙把这几个故事放一块，显然是意有所指的<br>并不是一个简单的人心易变，他真正要说的可能是</p><p>你想要什么，就要付出什么，<br>你要找真心的，就不要嫌卖油郎穷<br>富家公子好操控，那就不要天天给他出题抗压，最后怪他没担当。</p><p>顺带一提，我严重怀疑，冯梦龙的小说是后来青楼女子必备教科书</p><p>因为不论是沈玉英和袁世凯，还是小凤仙和蔡锷，花魁们的梭哈水平都堪称稳准狠</p><p>很难说是不是吸取了前人教训。</p>', 'http://localhost:45757/uploads/a44f9df3319a40599eae9c92b30dbd9e.jpg', '我现在严重怀疑冯梦龙写下这系列故事的初心主旨杜十娘怒沉百宝箱，一直以来都视为一个反封建故事，还上了教科书花魁杜十娘一片真心托付给举子李甲，后者却因为惧怕封建家族父母辜负杜十娘，最终有了怒沉百宝箱的结局。但随着时代发展，大家突然发现，古典时代的文学著作，有了新的视角。因为冯梦龙的故事不止一个杜十娘，还写了王美娘，也就是“卖油郎独占花魁”那个故事。然后大家发现，这一系列故事，某种意义上可以视为古典女子', 3, NULL, 14, 1575, 1, '2026-03-22 21:18:28', '2026-03-25 10:36:22', 0);
INSERT INTO `artwork` VALUES (23, 1, '测试文学：百年孤独读后感', '<p>家族的第一个人被绑在树上...</p>', NULL, '马尔克斯经典著作读后感', 1, NULL, 175, 2500, 1, '2026-03-22 21:35:03', '2026-03-23 16:51:18', 0);
INSERT INTO `artwork` VALUES (24, 1, '测试文学：春日漫步随笔', '<p>春天的风总是带着泥土的气息...</p>', NULL, '一篇描写春天的散文', 1, NULL, 40, 800, 1, '2026-03-21 21:35:03', '2026-03-25 10:31:55', 0);
INSERT INTO `artwork` VALUES (25, 1, '测试文学：夏夜星空', '<p>满天繁星，那是宇宙的呼吸...</p>', NULL, '一首关于夏夜的现代诗', 1, NULL, 91, 120, 1, '2026-03-20 21:35:03', '2026-03-25 10:36:35', 0);
INSERT INTO `artwork` VALUES (26, 1, '测试文学：秋风落叶的哀愁', '<p>秋天是适合想念的季节...</p>', NULL, '秋季的感伤随笔', 1, NULL, 14, 1500, 1, '2026-03-19 21:35:03', '2026-03-25 10:36:41', 0);
INSERT INTO `artwork` VALUES (27, 1, '测试文学：冬日暖阳', '<p>阳光穿过树枝，洒在雪地上...</p>', NULL, '冬日里的短篇小说选段', 1, NULL, 56, 3000, 1, '2026-03-18 21:35:03', '2026-03-22 21:35:03', 0);
INSERT INTO `artwork` VALUES (28, 1, '测试文学：故乡的云', '<p>天边飘过故乡的云，它不停的向我召唤...</p>', NULL, '回忆故乡的经典散文', 1, NULL, 213, 1800, 1, '2026-03-17 21:35:03', '2026-03-25 10:42:52', 0);
INSERT INTO `artwork` VALUES (29, 6, '考上研究生是起点，硕士论文才是关键 ！', '<h1 style=\"text-align: start;\">考研的快乐，真的只有在考上那一刻才能感受到。</h1><p style=\"text-align: start;\"><span style=\"background-color: rgb(216, 68, 147);\">这句话一点也不假，它背后隐藏着考研的艰辛和读研的无奈。</span></p><p style=\"text-align: start;\">我个人的经验是，研究生生活与我预想的完全不同，充满了无聊和疲惫。我当时一心只想尽快毕业，拿到学位证和毕业证，无论是找工作还是考公，反正学校的生活让我感到疲惫不堪，想尽早脱身。</p><p style=\"text-align: start;\">然而，硕士毕业论文却成了我最大的难题。</p><p style=\"text-align: start;\">刚开始准备论文时，我有许多困惑，但导师只能给出一些大方向，无法提供具体的解决方法，感觉有些敷衍。我还要一边实习，一边做论文，实在是顾不上，结果两个月过去，论文的进度几乎为零。</p><p style=\"text-align: start;\">作为成年人，面对困难，就得想办法克服。我四处打听，终于找到了前辈们成功的经验：找一位校外辅导老师。我导师帮不上忙，那就找一个能手把手教我的人。</p><p style=\"text-align: start;\">刚开始找辅导老师时差点被骗，好在最后找到了一个很专业的老师，他的方法相当实用，在不到两个月的时间里，我在老师的指导下，顺利完成了论文，后续的盲审和答辩也都非常顺利。</p>', '', '考研的快乐，真的只有在考上那一刻才能感受到。这句话一点也不假，它背后隐藏着考研的艰辛和读研的无奈。我个人的经验是，研究生生活与我预想的完全不同，充满了无聊和疲惫。我当时一心只想尽快毕业，拿到学位证和毕业证，无论是找工作还是考公，反正学校的生活让我感到疲惫不堪，想尽早脱身。然而，硕士毕业论文却成了我最大的难题。刚开始准备论文时，我有许多困惑，但导师只能给出一些大方向，无法提供具体的解决方法，感觉有些', 4, NULL, 2, 693, 1, '2026-03-23 18:44:47', '2026-03-25 10:42:09', 0);
INSERT INTO `artwork` VALUES (30, 5, '考上研究生是起点，硕士论文才是关键 wake！', '<p style=\"text-align: start;\">考研的快乐，真的只有在考上那一刻才能感受到。</p><p style=\"text-align: start;\">这句话一点也不假，它背后隐藏着考研的艰辛和读研的无奈。</p><p style=\"text-align: start;\">我个人的经验是，研究生生活与我预想的完全不同，充满了无聊和疲惫。我当时一心只想尽快毕业，拿到学位证和毕业证，无论是找工作还是考公，反正学校的生活让我感到疲惫不堪，想尽早脱身。</p><p style=\"text-align: start;\">然而，硕士毕业论文却成了我最大的难题。</p><p style=\"text-align: start;\">刚开始准备论文时，我有许多困惑，但导师只能给出一些大方向，无法提供具体的解决方法，感觉有些敷衍。我还要一边实习，一边做论文，实在是顾不上，结果两个月过去，论文的进度几乎为零。</p><p style=\"text-align: start;\">作为成年人，面对困难，就得想办法克服。我四处打听，终于找到了前辈们成功的经验：找一位校外辅导老师。我导师帮不上忙，那就找一个能手把手教我的人。</p><p style=\"text-align: start;\">刚开始找辅导老师时差点被骗，好在最后找到了一个很专业的老师，他的方法相当实用，在不到两个月的时间里，我在老师的指导下，顺利完成了论文，后续的盲审和答辩也都非常顺利。</p>', '', '考研的快乐，真的只有在考上那一刻才能感受到。这句话一点也不假，它背后隐藏着考研的艰辛和读研的无奈。我个人的经验是，研究生生活与我预想的完全不同，充满了无聊和疲惫。我当时一心只想尽快毕业，拿到学位证和毕业证，无论是找工作还是考公，反正学校的生活让我感到疲惫不堪，想尽早脱身。然而，硕士毕业论文却成了我最大的难题。刚开始准备论文时，我有许多困惑，但导师只能给出一些大方向，无法提供具体的解决方法，感觉有些', 3, NULL, 3, 633, 1, '2026-03-24 12:05:41', '2026-03-25 10:42:31', 0);
INSERT INTO `artwork` VALUES (31, 8, '长大以后都明白了什么?', '<p><br></p><p><span style=\"background-color: rgb(216, 68, 147);\">比如，我曾经听过三个党员带头临时组建的党支部抗震救灾的事情。这个例子就是一个很典型的良性循环的顺序系统。</span></p><p><strong>第一次看的时候，很受震撼。</strong><span style=\"color: rgb(245, 219, 77);\">当事人参加旅游，在核心区域遇上了汶川大地震，因为是野外，有人幸存，有人受伤。</span>在地震第一波结束后，他们的幸存者做的第一件事就是和当地幸存人员一起成立临时三人党支部。然后清点力量，分配成后勤救援医疗，进行先自救再救人。救人差不多了之后，再组织人员向震区进发，路上不断有其他村的幸存者汇入，党支部不断扩大。等他们到了镇区，发现镇里的政府人员损失更加严重。残余的幸存者和他们一起扩大党支部，分成若干小组，救人的，组织车辆的，负责后勤的，照顾伤员的，在维持了一两天后。遇到了解放军，把党组织移交给部队，精壮人员配合部队一起行动。几天后，等大部队来了以后，按照指示陆续撤离。这就是一个典型的良性循环的顺序系统。</p><p>想要组成一个良性循环的顺序系统，需要几个必要的步骤。第一件事情就是<strong>确立核心</strong>。这群人组织这个系统是为了干嘛的？这一定要明确，这是所有合作共赢谈判的基础，如果说连这个都有分歧的话，后面遇到任何分歧，这个组织都会崩。一定要明确核心成员，明确组织的目的，明确具体分工等。<br><br>作者：Mr.v<br>链接：https://www.zhihu.com/question/654624988/answer/2005022865477628130<br>来源：知乎<br>著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。</p>', 'http://localhost:45757/uploads/81841e4d13394cf79f9127b4bc09a8fe.webp', '比如，我曾经听过三个党员带头临时组建的党支部抗震救灾的事情。这个例子就是一个很典型的良性循环的顺序系统。第一次看的时候，很受震撼。当事人参加旅游，在核心区域遇上了汶川大地震，因为是野外，有人幸存，有人受伤。在地震第一波结束后，他们的幸存者做的第一件事就是和当地幸存人员一起成立临时三人党支部。然后清点力量，分配成后勤救援医疗，进行先自救再救人。救人差不多了之后，再组织人员向震区进发，路上不断有其他村', 3, NULL, 8, 802, 1, '2026-03-24 21:03:06', '2026-03-24 22:36:20', 0);

-- ----------------------------
-- Table structure for artwork_tag_relation
-- ----------------------------
DROP TABLE IF EXISTS `artwork_tag_relation`;
CREATE TABLE `artwork_tag_relation`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `artwork_id` bigint(0) NOT NULL COMMENT '作品ID',
  `tag_id` bigint(0) NOT NULL COMMENT '标签ID',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_artwork_tag`(`artwork_id`, `tag_id`) USING BTREE,
  INDEX `idx_artwork_id`(`artwork_id`) USING BTREE,
  INDEX `idx_tag_id`(`tag_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '作品标签关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of artwork_tag_relation
-- ----------------------------
INSERT INTO `artwork_tag_relation` VALUES (1, 18, 1, '2026-03-22 18:31:16', 0);
INSERT INTO `artwork_tag_relation` VALUES (2, 18, 4, '2026-03-22 18:31:16', 0);
INSERT INTO `artwork_tag_relation` VALUES (3, 17, 4, '2026-03-22 18:31:16', 0);
INSERT INTO `artwork_tag_relation` VALUES (4, 17, 5, '2026-03-22 18:31:16', 0);
INSERT INTO `artwork_tag_relation` VALUES (5, 16, 2, '2026-03-22 18:31:16', 0);
INSERT INTO `artwork_tag_relation` VALUES (6, 16, 4, '2026-03-22 18:31:16', 0);
INSERT INTO `artwork_tag_relation` VALUES (7, 21, 3, '2026-03-22 18:53:52', 0);
INSERT INTO `artwork_tag_relation` VALUES (8, 21, 2, '2026-03-22 18:53:52', 0);
INSERT INTO `artwork_tag_relation` VALUES (9, 22, 2, '2026-03-22 21:18:28', 0);
INSERT INTO `artwork_tag_relation` VALUES (10, 30, 2, '2026-03-24 12:05:41', 0);

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类描述',
  `sort_order` int(0) NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '文学创作', '包括小说、散文、诗歌等文学类作品', 1, '2026-03-15 15:08:37', '2026-03-15 15:08:37', 0);
INSERT INTO `category` VALUES (2, '技术文档', '技术教程、学习笔记等专业文档', 2, '2026-03-15 15:08:37', '2026-03-15 15:08:37', 0);
INSERT INTO `category` VALUES (3, '生活随笔', '日常生活记录、感悟分享', 3, '2026-03-15 15:08:37', '2026-03-15 15:08:37', 0);
INSERT INTO `category` VALUES (4, '学习笔记', '课堂笔记、读书笔记、学习笔记等', 4, '2026-03-15 15:08:37', '2026-03-17 21:43:11', 0);
INSERT INTO `category` VALUES (6, '二次元', '二次元作品创作, 中国、日本、韩国包含', 1, '2026-03-17 16:09:31', '2026-03-18 10:11:42', 0);
INSERT INTO `category` VALUES (12, '诗歌鉴赏', '这是一个诗歌', 5, '2026-03-17 21:48:56', '2026-03-17 21:48:56', 0);
INSERT INTO `category` VALUES (16, '高德地图', '', 6, '2026-03-17 21:57:56', '2026-03-17 21:57:56', 0);
INSERT INTO `category` VALUES (19, '腾讯地图', 'test', 7, '2026-03-18 10:09:58', '2026-03-18 10:11:50', 1);

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `artwork_id` bigint(0) NOT NULL COMMENT '作品ID',
  `user_id` bigint(0) NOT NULL COMMENT '评论用户ID',
  `parent_id` bigint(0) DEFAULT NULL COMMENT '父评论ID（用于回复）',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_artwork_id`(`artwork_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, 15, 6, NULL, '可以可以', '2026-03-20 18:29:48', '2026-03-20 18:29:48', 0);
INSERT INTO `comment` VALUES (2, 16, 6, NULL, '伊朗局势', '2026-03-20 18:42:45', '2026-03-20 18:42:45', 0);
INSERT INTO `comment` VALUES (3, 15, 6, NULL, '自习室', '2026-03-20 18:52:07', '2026-03-22 17:12:26', 1);
INSERT INTO `comment` VALUES (4, 16, 6, 2, '可以可以', '2026-03-20 18:56:37', '2026-03-20 18:56:37', 0);
INSERT INTO `comment` VALUES (5, 15, 6, 1, '自习室', '2026-03-21 10:32:00', '2026-03-21 10:32:00', 0);
INSERT INTO `comment` VALUES (6, 15, 6, 3, '可以可以', '2026-03-21 10:35:55', '2026-03-22 17:12:26', 1);
INSERT INTO `comment` VALUES (7, 15, 5, NULL, '考研倒计时', '2026-03-21 10:43:37', '2026-03-21 11:07:52', 1);
INSERT INTO `comment` VALUES (8, 15, 5, 7, '👍', '2026-03-21 10:43:45', '2026-03-21 10:43:45', 0);
INSERT INTO `comment` VALUES (9, 15, 5, 8, '👍', '2026-03-21 10:44:02', '2026-03-21 11:07:36', 1);
INSERT INTO `comment` VALUES (10, 15, 5, 6, '赞', '2026-03-21 10:44:10', '2026-03-22 17:12:26', 1);
INSERT INTO `comment` VALUES (11, 15, 5, 10, '对', '2026-03-21 10:48:33', '2026-03-22 17:12:26', 1);
INSERT INTO `comment` VALUES (12, 15, 5, 6, '自习室', '2026-03-21 10:48:45', '2026-03-21 11:07:45', 1);
INSERT INTO `comment` VALUES (13, 15, 5, 5, '热心网易', '2026-03-21 10:57:39', '2026-03-21 10:57:39', 0);
INSERT INTO `comment` VALUES (14, 18, 6, NULL, '厉害厉害', '2026-03-21 11:13:33', '2026-03-21 11:15:31', 1);
INSERT INTO `comment` VALUES (15, 18, 5, 14, '确实, 我也觉得', '2026-03-21 11:14:00', '2026-03-21 11:14:00', 0);
INSERT INTO `comment` VALUES (16, 18, 6, 15, '是吧哈哈哈', '2026-03-21 11:14:23', '2026-03-21 11:14:23', 0);
INSERT INTO `comment` VALUES (17, 16, 5, 4, '可以可以', '2026-03-21 11:16:22', '2026-03-21 11:16:29', 1);
INSERT INTO `comment` VALUES (18, 17, 5, NULL, '伊朗与美国', '2026-03-21 11:22:34', '2026-03-21 11:23:17', 1);
INSERT INTO `comment` VALUES (19, 17, 6, 18, '对, 是的', '2026-03-21 11:22:48', '2026-03-21 11:23:17', 1);
INSERT INTO `comment` VALUES (20, 17, 5, 19, '你认为美国如何', '2026-03-21 11:23:05', '2026-03-21 11:23:17', 1);
INSERT INTO `comment` VALUES (21, 10, 5, NULL, '波德莱尔', '2026-03-21 11:25:34', '2026-03-21 11:25:34', 0);
INSERT INTO `comment` VALUES (22, 10, 5, NULL, '作者：暴力暖壶盖\n链接：https://www.zhihu.com/question/1968142825448273648/answer/2012987798370669055\n来源：知乎\n著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。\n\n身边有个女生就跟自己丈夫提过：生第一个跟你姓，生第二个跟我姓。你知道她丈夫怎么回她吗？他丈夫说那你就别指望我和我的父母对这两个孩子一碗水端平了。哪怕一胎是姑娘，二胎是小子，我会把我的钱和房子全给姑娘，这儿子一分钱也别想得到。俩人还吵了一架，女生觉得这男的不爱她，还觉得这男的自私，自己身上掉下来的肉，不能跟随自己的姓氏不能接受。后来有个女方娘家挺有学识的的长辈数落她，我觉得说到根上了，那个长辈说自私的不是你的丈夫，而是你自己。你甚至自私到忽略了孩子的感受，没有在你孩子的角度上考虑日后的问题。第一，孩子从冠母姓的那一刻起，他爷爷奶奶，叔叔伯伯，婶子大娘就不会拿他当自己家人来看待，本身亲戚关系现在都普遍淡漠，更何况你一个外姓人。家里老大名正言顺的接受亲孙子的待遇，老二就永远矮老大一头，就因为姓不一样。这两亲兄弟姐妹很容易产生隔阂，以', '2026-03-21 11:26:48', '2026-03-21 11:26:48', 0);
INSERT INTO `comment` VALUES (23, 10, 5, 22, '123', '2026-03-21 11:28:05', '2026-03-21 11:28:05', 0);
INSERT INTO `comment` VALUES (24, 10, 5, 23, '324', '2026-03-21 11:28:09', '2026-03-21 11:28:09', 0);
INSERT INTO `comment` VALUES (25, 10, 5, 24, '124', '2026-03-21 11:28:12', '2026-03-21 11:28:12', 0);
INSERT INTO `comment` VALUES (26, 10, 5, 25, '2345', '2026-03-21 11:28:17', '2026-03-21 11:28:17', 0);
INSERT INTO `comment` VALUES (27, 11, 5, NULL, '沙发', '2026-03-21 11:53:00', '2026-03-21 11:53:00', 0);
INSERT INTO `comment` VALUES (28, 18, 5, NULL, '沙发', '2026-03-21 11:56:07', '2026-03-21 11:56:07', 0);
INSERT INTO `comment` VALUES (29, 2, 6, NULL, '多大', '2026-03-22 17:02:23', '2026-03-22 17:02:23', 0);
INSERT INTO `comment` VALUES (30, 2, 6, NULL, '多大的', '2026-03-22 17:02:25', '2026-03-22 17:02:25', 0);
INSERT INTO `comment` VALUES (31, 15, 6, 13, '可以可以', '2026-03-22 17:12:16', '2026-03-22 17:12:16', 0);
INSERT INTO `comment` VALUES (32, 6, 6, NULL, '科技科技', '2026-03-22 17:19:03', '2026-03-22 17:19:03', 0);
INSERT INTO `comment` VALUES (33, 6, 6, 32, '对赌地', '2026-03-22 17:19:10', '2026-03-22 17:19:10', 0);
INSERT INTO `comment` VALUES (34, 1, 6, NULL, '科技科技', '2026-03-22 17:36:52', '2026-03-22 17:37:11', 1);
INSERT INTO `comment` VALUES (35, 1, 6, 34, '科技科技', '2026-03-22 17:36:58', '2026-03-22 17:37:11', 1);
INSERT INTO `comment` VALUES (36, 1, 6, 35, '可以可', '2026-03-22 17:37:05', '2026-03-22 17:37:11', 1);
INSERT INTO `comment` VALUES (37, 19, 6, NULL, '赞赞赞', '2026-03-22 17:37:35', '2026-03-22 17:37:35', 0);
INSERT INTO `comment` VALUES (38, 19, 5, 37, '印度', '2026-03-22 18:05:47', '2026-03-22 18:05:47', 0);
INSERT INTO `comment` VALUES (39, 15, 8, NULL, '考研加油', '2026-03-24 18:02:12', '2026-03-24 18:02:12', 0);
INSERT INTO `comment` VALUES (40, 31, 8, NULL, '为什么', '2026-03-24 22:36:06', '2026-03-24 22:36:22', 1);
INSERT INTO `comment` VALUES (41, 31, 8, 40, '因为', '2026-03-24 22:36:12', '2026-03-24 22:36:22', 1);
INSERT INTO `comment` VALUES (42, 31, 8, 41, '所以', '2026-03-24 22:36:17', '2026-03-24 22:36:22', 1);
INSERT INTO `comment` VALUES (43, 3, 8, NULL, '可以', '2026-03-24 22:39:40', '2026-03-24 22:39:56', 1);
INSERT INTO `comment` VALUES (44, 3, 8, 43, '因为', '2026-03-24 22:39:45', '2026-03-24 22:39:56', 1);
INSERT INTO `comment` VALUES (45, 3, 8, 44, '但是', '2026-03-24 22:39:50', '2026-03-24 22:39:56', 1);

-- ----------------------------
-- Table structure for creation_record
-- ----------------------------
DROP TABLE IF EXISTS `creation_record`;
CREATE TABLE `creation_record`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '草稿ID',
  `user_id` bigint(0) NOT NULL COMMENT '所属用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '草稿标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '草稿内容',
  `category_id` bigint(0) DEFAULT NULL COMMENT '所属分类ID',
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创作说明/灵感记录',
  `word_count` int(0) DEFAULT 0 COMMENT '字数统计',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_category_id`(`category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '创作草稿表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for private_message
-- ----------------------------
DROP TABLE IF EXISTS `private_message`;
CREATE TABLE `private_message`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint(0) NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint(0) NOT NULL COMMENT '接收者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息正文',
  `is_read` tinyint(0) NOT NULL DEFAULT 0 COMMENT '阅读状态: 0-未读 1-已读',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `is_deleted` tinyint(0) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_conversation`(`sender_id`, `receiver_id`) USING BTREE,
  INDEX `idx_receiver_read`(`receiver_id`, `is_read`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '私信消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签颜色',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (1, '原创', '#1989fa', '2026-03-15 15:08:37', '2026-03-15 15:08:37', 0);
INSERT INTO `tag` VALUES (2, '转载', '#909399', '2026-03-15 15:08:37', '2026-03-15 15:08:37', 0);
INSERT INTO `tag` VALUES (3, '精选', '#e6a23c', '2026-03-15 15:08:37', '2026-03-15 15:08:37', 0);
INSERT INTO `tag` VALUES (4, '人文', '#f56c6c', '2026-03-15 15:08:37', '2026-03-23 18:11:36', 0);
INSERT INTO `tag` VALUES (5, 'AI辅助', '#67c23a', '2026-03-15 15:08:37', '2026-03-15 15:08:37', 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密存储）',
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `avatar_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `bio` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个人简介',
  `gender` tinyint(0) NOT NULL DEFAULT 0 COMMENT '性别: 0-保密 1-男 2-女',
  `hide_collections` tinyint(0) NOT NULL DEFAULT 0 COMMENT '隐私设置: 是否隐藏收藏 (0-否 1-是)',
  `disable_notifications` tinyint(0) NOT NULL DEFAULT 0 COMMENT '隐私设置: 是否关闭系统通知 (0-否 1-是)',
  `watermark` tinyint(0) NOT NULL DEFAULT 1 COMMENT '隐私设置: 是否开启图片水印 (0-否 1-是)',
  `role` tinyint(0) NOT NULL DEFAULT 1 COMMENT '角色: 1-普通用户 2-管理员',
  `status` tinyint(0) NOT NULL DEFAULT 1 COMMENT '账号状态: 1-正常 0-禁用',
  `create_time` datetime(0) DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `is_deleted` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE,
  UNIQUE INDEX `uk_email`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', NULL, NULL, NULL, NULL, 0, 0, 0, 1, 2, 1, '2026-03-15 15:08:37', '2026-03-15 15:08:37', 0);
INSERT INTO `user` VALUES (4, 'test_user', 'password123', '测试助手', NULL, 'test@example.com', NULL, NULL, 0, 0, 0, 1, 1, 1, '2026-03-15 18:28:01', '2026-03-15 18:28:01', 0);
INSERT INTO `user` VALUES (5, 'test001', 'e10adc3949ba59abbe56e057f20f883e', 'wakewake001', 'http://localhost:45757/uploads/d1f94405ac62453ab0d023a09dca134e.png', '12341@qq.com', NULL, NULL, 0, 0, 0, 1, 1, 1, '2026-03-17 18:18:23', '2026-03-21 10:43:13', 0);
INSERT INTO `user` VALUES (6, 'dxc', 'c952edcd709116221ae1fc677949f07f', 'dingxiuchen', '', '123456@qq.com', NULL, NULL, 0, 0, 0, 1, 2, 1, '2026-03-20 18:27:12', '2026-03-20 18:27:25', 0);
INSERT INTO `user` VALUES (7, 'sww', 'cdc24098e9410fbf5daf084ac15236b6', 'wakewake001', NULL, NULL, NULL, NULL, 0, 0, 0, 1, 1, 1, '2026-03-23 17:55:04', '2026-03-24 12:04:01', 0);
INSERT INTO `user` VALUES (8, 'lsb', '$2a$10$A7qa4Eom9gC6D/uUZkdhHOj1smFePZBoBsSc8H8mrs1E9NiKT3s9K', '不吃香草', 'http://localhost:45757/uploads/dcf678ddab92451287ea748564eff75d.jpg', NULL, NULL, '见过王源', 2, 0, 1, 1, 1, 1, '2026-03-24 16:45:24', '2026-03-24 22:33:39', 0);
INSERT INTO `user` VALUES (9, 'zlb', '$2a$10$Qq6Aed8g/m9qa5sicczryO/WFsh/ebe5ZhbCuYJAoiHScCdLMrA1e', '张连波', '', NULL, NULL, '', 0, 0, 0, 1, 1, 1, '2026-03-24 18:45:01', '2026-03-24 20:51:46', 0);

-- ----------------------------
-- Table structure for user_follow
-- ----------------------------
DROP TABLE IF EXISTS `user_follow`;
CREATE TABLE `user_follow`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `follower_id` bigint(0) NOT NULL COMMENT '关注者ID (粉丝)',
  `followee_id` bigint(0) NOT NULL COMMENT '被关注者ID (创作者)',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  `is_deleted` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否取消关注: 0-正常 1-已取消',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_follow`(`follower_id`, `followee_id`) USING BTREE,
  INDEX `idx_follower`(`follower_id`) USING BTREE,
  INDEX `idx_followee`(`followee_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户关注表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_follow
-- ----------------------------
INSERT INTO `user_follow` VALUES (1, 6, 5, '2026-03-23 16:50:58', 0);
INSERT INTO `user_follow` VALUES (2, 6, 1, '2026-03-24 15:56:53', 1);
INSERT INTO `user_follow` VALUES (6, 7, 5, '2026-03-24 16:00:07', 0);
INSERT INTO `user_follow` VALUES (7, 7, 6, '2026-03-24 16:00:26', 0);
INSERT INTO `user_follow` VALUES (8, 7, 1, '2026-03-24 16:03:32', 0);
INSERT INTO `user_follow` VALUES (9, 9, 8, '2026-03-24 18:46:36', 0);
INSERT INTO `user_follow` VALUES (10, 8, 1, '2026-03-24 21:54:56', 0);
INSERT INTO `user_follow` VALUES (11, 8, 5, '2026-03-25 10:19:54', 0);

-- ----------------------------
-- Table structure for user_interaction
-- ----------------------------
DROP TABLE IF EXISTS `user_interaction`;
CREATE TABLE `user_interaction`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '互动ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `artwork_id` bigint(0) NOT NULL COMMENT '作品ID',
  `interaction_type` tinyint(0) NOT NULL COMMENT '互动类型: 1-点赞 2-收藏',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_artwork_type`(`user_id`, `artwork_id`, `interaction_type`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_artwork_id`(`artwork_id`) USING BTREE,
  INDEX `idx_interaction_type`(`interaction_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 77 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '互动表（点赞/收藏）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_interaction
-- ----------------------------
INSERT INTO `user_interaction` VALUES (11, 5, 18, 1, '2026-03-20 18:06:49', 1);
INSERT INTO `user_interaction` VALUES (12, 5, 18, 2, '2026-03-20 18:06:50', 1);
INSERT INTO `user_interaction` VALUES (27, 5, 17, 1, '2026-03-20 18:07:47', 1);
INSERT INTO `user_interaction` VALUES (28, 5, 17, 2, '2026-03-20 18:07:47', 1);
INSERT INTO `user_interaction` VALUES (46, 5, 15, 1, '2026-03-20 18:09:00', 0);
INSERT INTO `user_interaction` VALUES (47, 5, 15, 2, '2026-03-20 18:09:01', 0);
INSERT INTO `user_interaction` VALUES (48, 5, 14, 1, '2026-03-20 18:19:03', 0);
INSERT INTO `user_interaction` VALUES (51, 5, 14, 2, '2026-03-20 18:18:51', 0);
INSERT INTO `user_interaction` VALUES (56, 6, 15, 1, '2026-03-20 18:29:38', 0);
INSERT INTO `user_interaction` VALUES (57, 6, 16, 1, '2026-03-20 18:42:38', 0);
INSERT INTO `user_interaction` VALUES (58, 6, 1, 1, '2026-03-22 17:02:15', 0);
INSERT INTO `user_interaction` VALUES (59, 6, 1, 2, '2026-03-22 17:02:15', 0);
INSERT INTO `user_interaction` VALUES (60, 6, 2, 1, '2026-03-22 17:02:18', 0);
INSERT INTO `user_interaction` VALUES (61, 6, 2, 2, '2026-03-22 17:02:20', 0);
INSERT INTO `user_interaction` VALUES (62, 6, 18, 1, '2026-03-22 17:13:51', 0);
INSERT INTO `user_interaction` VALUES (63, 6, 19, 1, '2026-03-22 17:37:48', 0);
INSERT INTO `user_interaction` VALUES (64, 6, 19, 2, '2026-03-22 17:37:47', 1);
INSERT INTO `user_interaction` VALUES (72, 5, 19, 1, '2026-03-22 18:05:55', 0);
INSERT INTO `user_interaction` VALUES (73, 6, 3, 1, '2026-03-22 21:31:03', 0);
INSERT INTO `user_interaction` VALUES (74, 6, 22, 1, '2026-03-22 22:00:49', 0);
INSERT INTO `user_interaction` VALUES (75, 6, 21, 1, '2026-03-22 22:01:05', 1);
INSERT INTO `user_interaction` VALUES (77, 8, 31, 1, '2026-03-24 21:03:23', 0);
INSERT INTO `user_interaction` VALUES (78, 8, 3, 2, '2026-03-24 22:33:12', 0);
INSERT INTO `user_interaction` VALUES (79, 8, 15, 1, '2026-03-24 21:15:12', 0);
INSERT INTO `user_interaction` VALUES (80, 8, 3, 1, '2026-03-24 22:33:11', 0);
INSERT INTO `user_interaction` VALUES (85, 8, 22, 1, '2026-03-24 22:32:58', 1);
INSERT INTO `user_interaction` VALUES (86, 8, 16, 1, '2026-03-24 22:32:52', 1);
INSERT INTO `user_interaction` VALUES (87, 8, 18, 1, '2026-03-24 22:29:26', 1);

SET FOREIGN_KEY_CHECKS = 1;
