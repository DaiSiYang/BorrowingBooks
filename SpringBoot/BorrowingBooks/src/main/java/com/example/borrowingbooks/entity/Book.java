package com.example.borrowingbooks.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 图书表
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("book")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图书ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 书名
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 出版社
     */
    private String publisher;

    /**
     * 出版日期
     */
    private LocalDate publishDate;

    /**
     * ISBN号
     */
    private String isbn;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 状态(1-可借阅,0-已借出,2-已损坏,3-已下架)
     */
    private Integer status;

    /**
     * 馆藏位置
     */
    private String location;

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

    private String coverImage;

    private Integer stock;
}
