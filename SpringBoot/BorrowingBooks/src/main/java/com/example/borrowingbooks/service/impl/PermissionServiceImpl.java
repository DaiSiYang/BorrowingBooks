package com.example.borrowingbooks.service.impl;

import com.example.borrowingbooks.entity.Permission;
import com.example.borrowingbooks.mapper.PermissionMapper;
import com.example.borrowingbooks.service.IPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

}
