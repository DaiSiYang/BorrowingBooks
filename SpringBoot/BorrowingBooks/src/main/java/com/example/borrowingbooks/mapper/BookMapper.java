package com.example.borrowingbooks.mapper;

import com.example.borrowingbooks.entity.Book;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 图书表 Mapper 接口
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {

}
