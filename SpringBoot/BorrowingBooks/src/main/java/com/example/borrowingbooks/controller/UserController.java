package com.example.borrowingbooks.controller;


import com.example.borrowingbooks.DTO.UserDTO;
import com.example.borrowingbooks.VO.UserJurisdictionVO;
import com.example.borrowingbooks.VO.UserVO;
import com.example.borrowingbooks.common.Result;
import com.example.borrowingbooks.entity.User;
import com.example.borrowingbooks.service.IUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private IUserService userService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody UserDTO userDTO) {
        log.info("register userDTO: {}", userDTO);
        return userService.register(userDTO);
    }

    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody UserDTO userDTO) {
        log.info("login userDTO: {}", userDTO);
        return userService.login(userDTO);
    }

    @GetMapping()
    public Result<List<UserJurisdictionVO>> getUserJurisdiction() {
        log.info("获取全部用户信息及权限信息");
        return userService.getUserJurisdiction();
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody User user) {
        log.info("update user: {}", user);
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        log.info("delete user id: {}", id);
        return userService.deleteUser(id);
    }
}
