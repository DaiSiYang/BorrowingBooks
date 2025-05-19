package com.example.borrowingbooks.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.example.borrowingbooks.entity.LoginUser;
import com.example.borrowingbooks.entity.Permission;
import com.example.borrowingbooks.entity.User;
import com.example.borrowingbooks.mapper.PermissionMapper;
import com.example.borrowingbooks.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private UserMapper userService;
    @Resource
    private PermissionMapper permissionMapper;
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户信息
        User user = userService.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (Objects.isNull(user)) {
            throw new RuntimeException("用户名或密码错误");
        }
        List<Permission> permissionList = permissionMapper.selectList(
                new LambdaQueryWrapper<Permission>().eq(Permission::getRoleId, user.getId())
        );

        List<String> permissions = permissionList.stream()
                .map(Permission::getPermissionCode)  // 假设权限字段叫 code
                .collect(Collectors.toList());
        // 把用户信息封装为UserDetails返回 可以创建一个LoginUser继承UserDetails
        return new LoginUser(user,permissions);
    }
}
