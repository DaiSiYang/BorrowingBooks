package com.example.borrowingbooks.service;

import com.example.borrowingbooks.VO.BorrowRecordVO;
import com.example.borrowingbooks.entity.BorrowRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 借阅记录表 服务类
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
public interface IBorrowRecordService extends IService<BorrowRecord> {

    List<BorrowRecordVO> listVO();
}
