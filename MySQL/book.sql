CREATE TABLE hot_book_carousel (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                   title VARCHAR(255) NOT NULL COMMENT '图书标题',
                                   author VARCHAR(255) NOT NULL COMMENT '作者',
                                   cover VARCHAR(512) NOT NULL COMMENT '封面图片链接',
                                   rating DECIMAL(2,1) DEFAULT NULL COMMENT '评分，范围 0.0 - 10.0',
                                   `desc` TEXT COMMENT '图书描述',
                                   `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='热门图书轮播图表';


CREATE TABLE hot_books (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                           title VARCHAR(255) NOT NULL COMMENT '图书标题',
                           author VARCHAR(255) NOT NULL COMMENT '作者',
                           cover VARCHAR(512) NOT NULL COMMENT '封面图片链接',
                           rating DECIMAL(2,1) DEFAULT NULL COMMENT '评分，范围 0.0 - 10.0',
                           `desc` TEXT COMMENT '图书描述',
                           create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='热门图书表';