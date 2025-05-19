package com.example.borrowingbooks.service.impl;

import com.example.borrowingbooks.entity.Book;
import com.example.borrowingbooks.mapper.BookMapper;
import com.example.borrowingbooks.service.IBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 图书表 服务实现类
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements IBookService {

}
