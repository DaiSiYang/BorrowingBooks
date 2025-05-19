package com.example.borrowingbooks.service.impl;

import com.example.borrowingbooks.entity.BookCategory;
import com.example.borrowingbooks.mapper.BookCategoryMapper;
import com.example.borrowingbooks.service.IBookCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 图书分类表 服务实现类
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
@Service
public class BookCategoryServiceImpl extends ServiceImpl<BookCategoryMapper, BookCategory> implements IBookCategoryService {

}
