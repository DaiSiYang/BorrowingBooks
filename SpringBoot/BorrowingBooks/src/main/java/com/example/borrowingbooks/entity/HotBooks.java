package com.example.borrowingbooks.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 热门图书表
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("hot_books")
public class HotBooks implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 图书标题
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 封面图片链接
     */
    private String cover;

    /**
     * 评分，范围 0.0 - 10.0
     */
    private BigDecimal rating;

    /**
     * 图书描述
     */
    @TableField("`desc`")
    private String desc;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
