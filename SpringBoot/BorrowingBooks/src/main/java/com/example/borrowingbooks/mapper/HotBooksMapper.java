package com.example.borrowingbooks.mapper;

import com.example.borrowingbooks.entity.HotBooks;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 热门图书表 Mapper 接口
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-21
 */
@Mapper
public interface HotBooksMapper extends BaseMapper<HotBooks> {

}
