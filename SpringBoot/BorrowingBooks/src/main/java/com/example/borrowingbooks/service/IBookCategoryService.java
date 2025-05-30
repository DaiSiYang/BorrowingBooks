package com.example.borrowingbooks.service;

import com.example.borrowingbooks.VO.BookCategoryVO;
import com.example.borrowingbooks.common.Result;
import com.example.borrowingbooks.entity.BookCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 图书分类表 服务类
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
public interface IBookCategoryService extends IService<BookCategory> {

    Result<List<BookCategoryVO>> getAllBookCategory();
}
