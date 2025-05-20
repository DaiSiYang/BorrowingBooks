package com.example.borrowingbooks.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 图书分类表
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("book_category")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 逻辑删除(0-未删除,1-已删除)
     */
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
