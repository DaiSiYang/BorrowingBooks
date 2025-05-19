package com.example.borrowingbooks.controller;


import com.example.borrowingbooks.DTO.UserDTO;
import com.example.borrowingbooks.VO.UserVO;
import com.example.borrowingbooks.common.Result;
import com.example.borrowingbooks.service.IUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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
}
