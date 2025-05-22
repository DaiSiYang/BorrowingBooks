package com.example.borrowingbooks.service;

import com.example.borrowingbooks.DTO.BookDTO;
import com.example.borrowingbooks.DTO.PageDTO;
import com.example.borrowingbooks.VO.BookVO;
import com.example.borrowingbooks.common.Result;
import com.example.borrowingbooks.entity.Book;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 图书表 服务类
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
public interface IBookService extends IService<Book> {

    Result<List<BookVO>> getAllBook(PageDTO pageDTO);

    Result<BookVO> getBookById(Long id);

    Result<String> borrow(Long id);

    Result<String> deleteBook(Long id);

    Result<String> updateBook(BookDTO bookDTO);

    Result<String> saveBook(BookDTO bookDTO);
}
