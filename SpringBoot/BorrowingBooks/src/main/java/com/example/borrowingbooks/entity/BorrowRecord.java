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
 * 借阅记录表
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("borrow_record")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BorrowRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 借阅用户ID
     */
    private Integer userId;

    /**
     * 图书ID
     */
    private Integer bookId;

    /**
     * 借阅日期
     */
    private LocalDateTime borrowDate;

    /**
     * 应归还日期
     */
    private LocalDateTime returnDate;

    /**
     * 实际归还日期
     */
    private LocalDateTime actualReturnDate;

    /**
     * 状态(1-借阅中,0-已归还,2-逾期,3-续借)
     */
    private Boolean status;

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
