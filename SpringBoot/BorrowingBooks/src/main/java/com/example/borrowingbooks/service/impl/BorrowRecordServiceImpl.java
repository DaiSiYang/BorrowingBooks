package com.example.borrowingbooks.service.impl;

import com.example.borrowingbooks.entity.BorrowRecord;
import com.example.borrowingbooks.mapper.BorrowRecordMapper;
import com.example.borrowingbooks.service.IBorrowRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 借阅记录表 服务实现类
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
@Service
public class BorrowRecordServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements IBorrowRecordService {

}
