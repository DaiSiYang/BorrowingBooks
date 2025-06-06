-- ----------------------------
-- 用户表结构（添加状态和逻辑删除字段）
-- ----------------------------
CREATE TABLE `user` (
                        `id` BIGINT NOT NULL COMMENT '用户ID(雪花算法)',
                        `username` varchar(50) NOT NULL UNIQUE COMMENT '用户名',
                        `password` varchar(100) NOT NULL COMMENT '密码(加密存储)',
                        `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
                        `phone` char(11) DEFAULT NULL COMMENT '手机号',
                        `avatar_url` TEXT DEFAULT NULL COMMENT '头像URL',
                        `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
                        `role_id` int(11) NOT NULL DEFAULT 2 COMMENT '角色ID(1-管理员,2-普通用户)',
                        `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '用户状态(1-正常,0-禁用,2-锁定)',
                        `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-未删除,1-已删除)',
                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `idx_username` (`username`),
                        KEY `idx_status` (`status`),
                        KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 权限表结构
-- ----------------------------
CREATE TABLE `permission` (
                              `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
                              `role_id` int(11) NOT NULL COMMENT '角色ID',
                              `role_name` varchar(20) NOT NULL COMMENT '角色名称',
                              `permission_code` varchar(50) NOT NULL COMMENT '权限代码',
                              `permission_name` varchar(50) NOT NULL COMMENT '权限名称',
                              `description` varchar(100) DEFAULT NULL COMMENT '权限描述',
                              `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-未删除,1-已删除)',
                              PRIMARY KEY (`id`),
                              KEY `idx_role_id` (`role_id`),
                              KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- ----------------------------
-- 图书表结构（添加逻辑删除）
-- ----------------------------
CREATE TABLE `book` (
                        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '图书ID',
                        `title` varchar(100) NOT NULL COMMENT '书名',
                        `author` varchar(100) NOT NULL COMMENT '作者',
                        `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片URL',
                        `publisher` varchar(100) DEFAULT NULL COMMENT '出版社',
                        `publish_date` date DEFAULT NULL COMMENT '出版日期',
                        `isbn` varchar(20) DEFAULT NULL COMMENT 'ISBN号',
                        `category_id` int(11) DEFAULT NULL COMMENT '分类ID',
                        `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态(1-可借阅,0-已借出,2-已损坏,3-已下架)',
                        `location` varchar(50) DEFAULT NULL COMMENT '馆藏位置',
                        `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-未删除,1-已删除)',
                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        PRIMARY KEY (`id`),
                        KEY `idx_title` (`title`),
                        KEY `idx_author` (`author`),
                        KEY `idx_status` (`status`),
                        KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';
ALTER TABLE `book`
    ADD COLUMN `stock` int(11) NOT NULL DEFAULT 10 COMMENT '库存数量';
ALTER TABLE `book`
    ADD COLUMN `description` VARCHAR(500) NOT NULL DEFAULT '这个管理员很懒,还没有写简介' COMMENT '图书简介';

-- ----------------------------
-- 借阅记录表结构（添加逻辑删除）
-- ----------------------------
CREATE TABLE `borrow_record` (
                                 `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
                                 `user_id` INT(11) UNSIGNED NOT NULL COMMENT '借阅用户ID',
                                 `book_id` INT(11) UNSIGNED NOT NULL COMMENT '图书ID',
                                 `borrow_date` DATETIME NOT NULL COMMENT '借阅日期',
                                 `return_date` DATETIME DEFAULT NULL COMMENT '应归还日期',
                                 `actual_return_date` DATETIME DEFAULT NULL COMMENT '实际归还日期',
                                 `status` TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1-借阅中, 0-已归还, 2-逾期, 3-续借)',
                                 `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-未删除, 1-已删除)',
                                 `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_user_id` (`user_id`),
                                 KEY `idx_book_id` (`book_id`),
                                 KEY `idx_status` (`status`),
                                 KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';
ALTER TABLE borrow_record
    MODIFY COLUMN id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL COMMENT '借阅用户ID',
    MODIFY COLUMN book_id BIGINT UNSIGNED NOT NULL COMMENT '图书ID';

-- ----------------------------
-- 图书分类表结构（添加逻辑删除）
-- ----------------------------
CREATE TABLE `book_category` (
                                 `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                                 `category_name` varchar(50) NOT NULL COMMENT '分类名称',
                                 `description` varchar(100) DEFAULT NULL COMMENT '分类描述',
                                 `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-未删除,1-已删除)',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),

                                 KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

INSERT INTO book_category
( category_name, description) VALUES
                                            ('文学', '文学作品分类'),
                                            ('小说', '虚构类文学作品'),
                                            ( '散文', '散文随笔类作品'),
                                            ( '科技', '科学技术类书籍'),
                                            ('计算机', '计算机科学相关书籍'),
                                            ( '物理学', '物理学科普与专业书籍');
-- ----------------------------
-- 插入默认权限数据
-- ----------------------------
INSERT INTO `permission` (`role_id`, `role_name`, `permission_code`, `permission_name`, `description`, `is_deleted`) VALUES
                                                                                                                         (1, '管理员', 'user:manage', '用户管理', '管理系统用户', 0),
                                                                                                                         (1, '管理员', 'book:manage', '图书管理', '管理图书信息', 0),
                                                                                                                         (1, '管理员', 'borrow:manage', '借阅管理', '管理借阅记录', 0),
                                                                                                                         (2, '普通用户', 'book:view', '查看图书', '查看图书列表', 0),
                                                                                                                         (2, '普通用户', 'book:borrow', '借阅图书', '借阅图书', 0),
                                                                                                                         (2, '普通用户', 'book:return', '归还图书', '归还图书', 0);

