package com.example.borrowingbooks.controller;


import com.example.borrowingbooks.common.Result;
import com.example.borrowingbooks.entity.HotBooks;
import com.example.borrowingbooks.service.IHotBooksService;
import com.example.borrowingbooks.utils.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 热门图书表 前端控制器
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-21
 */
@RestController
@RequestMapping("/hot-books")
@Slf4j
public class HotBooksController {
    private static final String HOT_BOOK_KEY = "book:hot:list";
    @Resource
    private IHotBooksService hotBook;
    @Resource
    private RedisUtil redisUtil;

    @GetMapping("list")
    public Result<List<HotBooks>> getAllHotBook() {
      log.info("获取热门图书列表");
      List<HotBooks> list = redisUtil.getJsonList(HOT_BOOK_KEY, HotBooks.class);
      if (list != null){
          log.info("从 Redis 中获取数据");
          return Result.ok(list);
      }
      log.info("从数据库中获取数据");
      list = hotBook.list();
      if (list == null){
          log.info("数据库中没有数据,缓存空列表");
          redisUtil.setJson(HOT_BOOK_KEY, Collections.emptyList(), 2);
      }
      boolean json = redisUtil.setJson(HOT_BOOK_KEY, list, 30);
      if (!json){
          log.error("Redis 写入失败，Key: {}", HOT_BOOK_KEY);
          return Result.fail("缓存失败");
        }
      return Result.ok(list);
    }
}
