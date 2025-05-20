package com.example.borrowingbooks.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.borrowingbooks.DTO.PageDTO;
import com.example.borrowingbooks.VO.BookVO;
import com.example.borrowingbooks.common.Result;
import com.example.borrowingbooks.entity.Book;
import com.example.borrowingbooks.mapper.BookMapper;
import com.example.borrowingbooks.service.IBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.borrowingbooks.utils.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 图书表 服务实现类
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
@Service
@Slf4j
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements IBookService {

    private static final String BOOK_LIST = "book:list";

    @Resource
    private RedisUtil redis;

    @Override
    public Result<List<BookVO>> getAllBook(PageDTO pageDTO) {
        String cacheKey = BOOK_LIST + ":" + pageDTO.getCurrentPage() + ":" + pageDTO.getPageSize();
        List<BookVO> jsonList = redis.getJsonList(cacheKey, BookVO.class);
        if (jsonList != null){
            log.info("从 Redis 中获取数据");
            return Result.ok(jsonList);
        }
        log.info("从数据库中获取数据");
        Page<Book> page = new Page<>(pageDTO.getCurrentPage(), pageDTO.getPageSize());
        Page<Book> bookPage = this.page(page, new LambdaQueryWrapper<Book>()
                .eq(Book::getIsDeleted, 0)
                .orderByDesc(Book::getCreateTime));
        if (bookPage == null){
            log.error("数据库中没有数据");
            return Result.fail("没有数据");
        }
        List<BookVO> collect = bookPage.getRecords().stream().map(book -> BookVO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .publishDate(book.getPublishDate())
                .isbn(book.getIsbn())
                .categoryId(book.getCategoryId())
                .status(book.getStatus())
                .location(book.getLocation())
                .createTime(book.getCreateTime())
                .coverImage(book.getCoverImage())
                .build()).toList();
        boolean json = redis.setJson(cacheKey, collect, 30);
        if (!json){
            log.error("Redis 写入失败，Key: {}", cacheKey);
            return Result.fail("Redis 写入失败");
        }
        return Result.ok(collect);
    }
}
