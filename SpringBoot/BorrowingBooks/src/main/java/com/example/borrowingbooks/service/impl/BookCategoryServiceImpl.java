package com.example.borrowingbooks.service.impl;

import com.example.borrowingbooks.VO.BookCategoryVO;
import com.example.borrowingbooks.common.Result;
import com.example.borrowingbooks.entity.BookCategory;
import com.example.borrowingbooks.mapper.BookCategoryMapper;
import com.example.borrowingbooks.service.IBookCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.borrowingbooks.utils.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 图书分类表 服务实现类
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
@Service
@Slf4j
public class BookCategoryServiceImpl extends ServiceImpl<BookCategoryMapper, BookCategory> implements IBookCategoryService {

    private static final String BOOK_CATEGORY = "book:category";

    @Resource
    private RedisUtil  redisUtil;

    @Override
    public Result<List<BookCategoryVO>> getAllBookCategory() {
        List<BookCategoryVO> jsonList = redisUtil.getJsonList(BOOK_CATEGORY, BookCategoryVO.class);
        if (jsonList != null){
            log.info("从Redis中获取图书分类");
            return Result.ok(jsonList);
        }

        log.info("从数据库中获取图书分类");
        List<BookCategory> list = this.list();
        if (list.isEmpty()){
            log.error("图书分类为空");
            return Result.fail("图书分类为空");
        }

        List<BookCategoryVO> categoryVOList = list.stream().map(bookCategory -> BookCategoryVO.builder()
                .id(bookCategory.getId())
                .categoryName(bookCategory.getCategoryName())
                .description(bookCategory.getDescription())
                .createTime(bookCategory.getCreateTime())
                .build()).collect(Collectors.toList());

        if (!redisUtil.setJson(BOOK_CATEGORY, categoryVOList, 30)){
            log.error("Redis 写入失败，Key: {}", BOOK_CATEGORY);
            return Result.fail("Redis 写入失败");
        }

        return Result.ok(categoryVOList);
    }
}
