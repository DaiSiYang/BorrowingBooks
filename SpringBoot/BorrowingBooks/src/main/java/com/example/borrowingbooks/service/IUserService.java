package com.example.borrowingbooks.service;

import com.example.borrowingbooks.DTO.UserDTO;
import com.example.borrowingbooks.VO.UserVO;
import com.example.borrowingbooks.common.Result;
import com.example.borrowingbooks.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
public interface IUserService extends IService<User> {

    Result<String> register(UserDTO userDTO);

    Result<UserVO> login(UserDTO userDTO);
}
