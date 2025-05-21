package com.example.borrowingbooks.service.impl;

import com.example.borrowingbooks.entity.HotBooks;
import com.example.borrowingbooks.mapper.HotBooksMapper;
import com.example.borrowingbooks.service.IHotBooksService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 热门图书表 服务实现类
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-21
 */
@Service
public class HotBooksServiceImpl extends ServiceImpl<HotBooksMapper, HotBooks> implements IHotBooksService {

}
