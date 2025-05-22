package com.example.borrowingbooks.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.repository.AbstractRepository;
import com.example.borrowingbooks.DTO.BookDTO;
import com.example.borrowingbooks.DTO.PageDTO;
import com.example.borrowingbooks.VO.BookVO;
import com.example.borrowingbooks.common.RedisLock;
import com.example.borrowingbooks.common.Result;
import com.example.borrowingbooks.entity.Book;
import com.example.borrowingbooks.entity.BorrowRecord;
import com.example.borrowingbooks.entity.LoginUser;
import com.example.borrowingbooks.mapper.BookMapper;
import com.example.borrowingbooks.service.IBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.borrowingbooks.service.IBorrowRecordService;
import com.example.borrowingbooks.utils.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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
    private static final String BOOK_LOCK = "book:lock:";

    @Resource
    private RedisUtil redis;
    @Resource
    private IBorrowRecordService borrowRecordService;
    @Resource
    private RedisLock redisLock;

    @Override
    public Result<List<BookVO>> getAllBook(PageDTO pageDTO) {
        String cacheKey = BOOK_LIST;
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
                .stock(book.getStock())
                .build()).toList();
        boolean json = redis.setJson(cacheKey, collect, 30);
        if (!json){
            log.error("Redis 写入失败，Key: {}", cacheKey);
            return Result.fail("Redis 写入失败");
        }
        return Result.ok(collect);
    }

    @Override
    public Result<BookVO> getBookById(Long id){
        if (id == null){
            return Result.fail("图书ID不存在");
        }
        Book byId = this.getById(id);
        if (byId == null){
            log.error("图书不存在");
            return Result.fail("图书不存在");
        }
        BookVO.BookVOBuilder builder = BookVO.builder()
                .id(byId.getId())
                .title(byId.getTitle())
                .author(byId.getAuthor())
                .publisher(byId.getPublisher())
                .publishDate(byId.getPublishDate())
                .isbn(byId.getIsbn())
                .categoryId(byId.getCategoryId())
                .status(byId.getStatus())
                .location(byId.getLocation())
                .stock(byId.getStock())
                .createTime(byId.getCreateTime());

        return Result.ok( builder.build());
    }

    @Override
    @Transactional
    public Result<String> borrow(Long id) {
        log.info("借阅图书 {}", id);
        if (id == null){
            return Result.fail("图书ID不存在");
        }
        String key = String.format(BOOK_LOCK, id);
        String string = UUID.randomUUID().toString();
        boolean lock = redisLock.tryLock(key, string);
        try {
            if (!lock){
                log.error("图书 {} 正在借阅中", id);
                return Result.fail("图书正在借阅中");
            }
            Book byId = this.getById(id);
            if (byId == null){
                log.error("图书不存在");
                return Result.fail("图书不存在");
            }
            if (byId.getStock() <= 0){
                log.error("图书 {} 库存不足", id);
                return Result.fail("图书库存不足");
            }
            byId.setStock(byId.getStock() - 1);
            boolean update = this.updateById(byId);
            if (!update){
                log.error("图书 {} 更新失败", id);
                return Result.fail("图书更新失败");
            }
            // 添加借阅记录
            LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = loginUser.getUser().getId();
            BorrowRecord build = BorrowRecord.builder()
                    .userId(userId)
                    .bookId(Math.toIntExact(id))
                    .borrowDate(LocalDateTime.now())
                    .returnDate(LocalDateTime.now().plusDays(7))
                    .status(1)
                    .build();
            boolean save = borrowRecordService.save(build);
            if (!save){
                log.error("图书 {} 添加借阅记录失败", id);
                return Result.fail("图书添加借阅记录失败");
            }
            log.info("图书 {} 借阅成功", id);
            return Result.ok("借阅成功");
        } finally {
            redisLock.unlock(key, string);
        }
    }

    @Override
    public Result<String> deleteBook(Long id) {
        if (id == null){
            return Result.fail("图书ID不存在");
        }
        if (this.removeById(id)){
            return Result.ok("删除成功");
        }
        redis.delete(BOOK_LIST);
        return Result.fail("删除失败");
    }

    @Override
    public Result<String> updateBook(BookDTO bookDTO) {
        if (bookDTO == null){
            return Result.fail("图书信息不存在");
        }
        if (bookDTO.getId() == null){
            return Result.fail("图书ID不存在");
        }
        Book book = new Book();
        BeanUtils.copyProperties(bookDTO, book);
        boolean b = this.updateById(book);
        if (b){
            log.info("图书 {} 更新成功", bookDTO.getId());
            redis.delete(BOOK_LIST);
            log.info("删除缓存 {}", BOOK_LIST);
            return Result.ok("更新成功");
        }
        log.info("图书 {} 更新失败", bookDTO.getId());
        return Result.fail("更新失败");
    }
}
