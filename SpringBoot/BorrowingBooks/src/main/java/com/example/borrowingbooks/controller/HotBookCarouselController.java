package com.example.borrowingbooks.controller;


import com.example.borrowingbooks.common.Result;
import com.example.borrowingbooks.entity.HotBookCarousel;
import com.example.borrowingbooks.service.IHotBookCarouselService;
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
 * 热门图书轮播图表 前端控制器
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-21
 */
@RestController
@RequestMapping("/hot-book-carousel")
@Slf4j
public class HotBookCarouselController {

    private static final String HOT_BOOK_CAROUSEL_KEY = "book:hot:carousel";

    @Resource
    private IHotBookCarouselService  hotBookCarouselService;
    @Resource
    private RedisUtil redis;

    @GetMapping("list")
    public Result<List<HotBookCarousel>> getAllHotBookCarousel(){
        log.info("获取所有热门图书轮播图");
        List<HotBookCarousel> list = redis.getJsonList(HOT_BOOK_CAROUSEL_KEY, HotBookCarousel.class);
        if (list != null){
            log.info("从 Redis 中获取数据");
            return Result.ok(list);
        }
        log.info("从数据库中获取数据");
        list = hotBookCarouselService.list();
        if (list.isEmpty()){
            redis.setJson(HOT_BOOK_CAROUSEL_KEY, Collections.emptyList(), 2);
            return Result.fail("热门图书轮播图数据为空");
        }
        boolean set = redis.setJson(HOT_BOOK_CAROUSEL_KEY, list, 30);
        if (!set){
            log.error("Redis 写入失败，Key: {}", HOT_BOOK_CAROUSEL_KEY);
            return Result.fail("缓存失败");
        }
        return Result.ok();
    }
}
