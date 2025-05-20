package com.example.borrowingbooks.controller;


import com.example.borrowingbooks.DTO.PageDTO;
import com.example.borrowingbooks.VO.BookVO;
import com.example.borrowingbooks.common.Result;
import com.example.borrowingbooks.service.IBookService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 图书表 前端控制器
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
@Slf4j
@RestController
@RequestMapping("/book")
public class BookController {

    @Resource
    private IBookService bookService;

    @PostMapping("list")
    public Result<List<BookVO>> getAllBook(@RequestBody PageDTO pageDTO) {
        log.info("获取所有图书 {}" + pageDTO);
        return bookService.getAllBook(pageDTO);
    }
}
