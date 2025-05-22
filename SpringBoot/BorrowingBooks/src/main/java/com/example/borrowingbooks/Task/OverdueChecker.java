package com.example.borrowingbooks.Task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.example.borrowingbooks.entity.BorrowRecord;
import com.example.borrowingbooks.mapper.BorrowRecordMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OverdueChecker {

    @Resource
    private BorrowRecordMapper borrowRecordMapper;

    @Scheduled(cron = "0 0 12 * * ?") // 每天12:00触发
    @Transactional(rollbackFor = Exception.class)  // 添加事务管理
    public void checkOverdueRecords() {
        log.info("开始执行逾期检查任务");

        // 查找未归还且应归还日期已过的记录（状态 1 为“借出中”）
        List<BorrowRecord> overdueList = borrowRecordMapper.selectList(
                new LambdaQueryWrapper<BorrowRecord>()
                        .eq(BorrowRecord::getStatus, 1)
                        .lt(BorrowRecord::getReturnDate, LocalDateTime.now())
        );

        if (CollectionUtils.isEmpty(overdueList)) {
            log.info("没有逾期借阅记录");
            return;
        }

        // 直接通过 Mapper 批量更新状态为逾期（状态 2）
        int updatedCount = updateBatchStatus(overdueList, 2);

        if (updatedCount == overdueList.size()) {
            log.info("成功更新 {} 条逾期借阅记录", updatedCount);
        } else {
            log.error("部分逾期记录更新失败，预期更新 {} 条，实际更新 {} 条",
                    overdueList.size(), updatedCount);
        }
    }

    /**
     * 批量更新借阅记录状态
     */
    private int updateBatchStatus(List<BorrowRecord> records, Integer targetStatus) {
        int count = 0;
        for (BorrowRecord record : records) {
            LambdaUpdateWrapper<BorrowRecord> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper
                    .eq(BorrowRecord::getId, record.getId())
                    .set(BorrowRecord::getStatus, targetStatus);

            count += borrowRecordMapper.update(null, updateWrapper);
        }
        return count;
    }
}
