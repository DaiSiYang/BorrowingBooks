package com.example.borrowingbooks.controller;


import com.example.borrowingbooks.VO.BookCategoryVO;
import com.example.borrowingbooks.common.Result;
import com.example.borrowingbooks.service.IBookCategoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 图书分类表 前端控制器
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
@RestController
@RequestMapping("/bookCategory")
@Slf4j
public class BookCategoryController {

    @Resource
    private IBookCategoryService bookCategoryService;

    @GetMapping("list")
    public Result<List<BookCategoryVO>> getAllBookCategory() {
        log.info("获取所有图书分类");
        return bookCategoryService.getAllBookCategory();
    }

}
