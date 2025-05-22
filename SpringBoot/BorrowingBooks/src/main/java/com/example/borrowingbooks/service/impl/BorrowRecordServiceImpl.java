package com.example.borrowingbooks.service.impl;

import com.example.borrowingbooks.VO.BorrowRecordVO;
import com.example.borrowingbooks.common.Result;
import com.example.borrowingbooks.entity.Book;
import com.example.borrowingbooks.entity.BorrowRecord;
import com.example.borrowingbooks.entity.User;
import com.example.borrowingbooks.mapper.BookMapper;
import com.example.borrowingbooks.mapper.BorrowRecordMapper;
import com.example.borrowingbooks.mapper.UserMapper;
import com.example.borrowingbooks.service.IBookService;
import com.example.borrowingbooks.service.IBorrowRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.borrowingbooks.service.IUserService;
import com.example.borrowingbooks.utils.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 借阅记录表 服务实现类
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
@Service
@Slf4j
public class BorrowRecordServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements IBorrowRecordService {
    private static final String BOOK_BORROW_RECORD = "book:borrowRecord";

    @Resource
    private BorrowRecordMapper borrowRecordMapper;  // 显式注入
    @Resource
    private BookMapper bookMapper;
    @Resource
    private UserMapper userMapper;  // 新增 UserMapper 依赖
    @Resource
    private RedisUtil redisUtil;

    @Override
    public List<BorrowRecordVO> listVO() {
        // 1. 尝试从 Redis 获取缓存
        List<BorrowRecordVO> cacheList = redisUtil.getJsonList(BOOK_BORROW_RECORD, BorrowRecordVO.class);
        if (cacheList != null) {
            log.info("从Redis中获取借阅记录列表");
            return cacheList;
        }

        // 2. 查询数据库
        log.info("从数据库中获取借阅记录列表");
        List<BorrowRecord> list = this.list();
        if (list.isEmpty()) {
            log.debug("无借阅记录");
            redisUtil.setJson(BOOK_BORROW_RECORD, Collections.emptyList(), 3);
            return Collections.emptyList();
        }

        // 3. 提取用户和图书ID
        Set<Long> userIds = list.stream()
                .map(BorrowRecord::getUserId)
                .collect(Collectors.toSet());
        Set<Integer> bookIds = list.stream()
                .map(BorrowRecord::getBookId)
                .collect(Collectors.toSet());

        // 4. 批量查询用户和图书（直接使用Mapper）
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        Map<Integer, Book> bookMap = bookMapper.selectBatchIds(bookIds).stream()
                .collect(Collectors.toMap(Book::getId, Function.identity()));

        // 5. 构建VO列表
        List<BorrowRecordVO> result = list.stream()
                .map(record -> {
                    User user = userMap.get(record.getUserId());
                    Book book = bookMap.get(record.getBookId());

                    if (user == null || book == null) {
                        log.warn("借阅记录 {} 关联的用户或图书不存在", record.getId());
                        return null;
                    }

                    return BorrowRecordVO.builder()
                            .Image(book.getCoverImage())
                            .isbn(book.getIsbn())
                            .title(book.getTitle())
                            .author(book.getAuthor())
                            .username(user.getUsername())
                            .borrowDate(record.getBorrowDate())
                            .returnDate(record.getReturnDate())
                            .actualReturnDate(record.getActualReturnDate())
                            .status(record.getStatus())
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 6. 写入缓存
        if (!redisUtil.setJson(BOOK_BORROW_RECORD, result, 30)) {
            log.error("Redis缓存写入失败");
        }

        return result;
    }
}
