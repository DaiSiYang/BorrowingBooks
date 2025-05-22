package com.example.borrowingbooks.controller;


import com.example.borrowingbooks.VO.BorrowRecordVO;
import com.example.borrowingbooks.common.Result;
import com.example.borrowingbooks.entity.BorrowRecord;
import com.example.borrowingbooks.service.IBorrowRecordService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 借阅记录表 前端控制器
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
@RestController
@RequestMapping("/borrowRecord")
@Slf4j
public class BorrowRecordController {

    @Resource
    private IBorrowRecordService borrowRecordService;

    @GetMapping("/list")
    public Result<List<BorrowRecordVO>> list() {
        log.info("获取借阅记录列表");
        return Result.ok(borrowRecordService.listVO());
    }

}
