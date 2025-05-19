package com.example.borrowingbooks.service.impl;

import com.example.borrowingbooks.DTO.UserDTO;
import com.example.borrowingbooks.VO.UserVO;
import com.example.borrowingbooks.common.Result;
import com.example.borrowingbooks.entity.LoginUser;
import com.example.borrowingbooks.entity.User;
import com.example.borrowingbooks.mapper.UserMapper;
import com.example.borrowingbooks.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.borrowingbooks.utils.JwtUtil;
import com.example.borrowingbooks.utils.RedisUtil;
import com.example.borrowingbooks.utils.RegexUtils;
import com.example.borrowingbooks.utils.SnowflakeUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

import static com.example.borrowingbooks.common.RedisExpireTime.USER_INFO;
import static com.example.borrowingbooks.common.RedisKeyConstants.User.INFO;
import static com.example.borrowingbooks.common.RedisKeyConstants.User.TOKEN;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author DaiSY
 * @since 2025-05-19
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private RedisUtil  redisUtil;

    @Resource
    private PasswordEncoder  passwordEncoder;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public Result<String> register(UserDTO userDTO) {
        log.info("用户注册：{}", userDTO);
        if (userDTO.getUsername() == null) {
            return Result.fail("用户名不能为空");
        }
        if (userDTO.getPassword() == null){
            return Result.fail("密码不能为空");
        }
        if (userDTO.getEmail() == null){
            return Result.fail("邮箱不能为空");
        }
        boolean validEmail = RegexUtils.isValidEmail(userDTO.getEmail());
        if (!validEmail) {
            return Result.fail("邮箱格式错误");
        }

        String password = passwordEncoder.encode(userDTO.getPassword());
        Long id = SnowflakeUtil.nextId();
        String realName = generateUsername("user_", 8);

        User user = User.builder()
                .id(id)
                .username(userDTO.getUsername())
                .password(password)
                .realName(realName)
                .email(userDTO.getEmail())
                .roleId(2)
                .status(1)
                .avatarUrl("https://www.google.com.hk/url?sa=i&url=https%3A%2F%2Fwww.doyo.cn%2Fapp%2F485270.html&psig=AOvVaw3zwYccwizKP6as6Cnh2W2W&ust=1747749053063000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCNCGhuPWr40DFQAAAAAdAAAAABAE")
                .isDeleted(0)
                .build();
        boolean save = this.save(user);
        if (!save){
            return Result.fail("注册失败");
        }
        return Result.ok("注册成功");
    }

    @Override
    public Result<UserVO> login(UserDTO userDTO) {
        log.info("用户登录：{}", userDTO);
        if (userDTO.getUsername() == null){
            return Result.fail("用户名不能为空");
        }
        if (userDTO.getPassword() == null){
            return Result.fail("密码不能为空");
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)){
            return Result.fail("登录失败");
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        Long id = loginUser.getUser().getId();
        User byId = this.getById(id);
        if (byId == null){
            return Result.fail("用户不存在");
        }
        String token = jwtUtil.generateTokenFromField(String.valueOf(id));
        UserVO build = UserVO.builder()
                .id(byId.getId())
                .username(byId.getUsername())
                .realName(byId.getRealName())
                .phone(byId.getPhone())
                .email(byId.getEmail())
                .roleId(byId.getRoleId())
                .createTime(byId.getCreateTime())
                .updateTime(byId.getUpdateTime())
                .avatarUrl(byId.getAvatarUrl())
                .token(token)
                .build();
        String TokenKey = String.format(TOKEN, id);
        String  UserInfoKey = String.format(INFO, id);
        boolean set = redisUtil.set(TokenKey, token, USER_INFO);
        boolean json = redisUtil.setJson(UserInfoKey, loginUser, USER_INFO);
        if (!set || !json){
            log.error("Redis 写入失败，TokenKey: {}, UserInfoKey: {}", TokenKey, UserInfoKey);
            return Result.fail("Redis 写入失败");
        }
        return Result.ok(build);
    }

    public static String generateUsername(String prefix, int length) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 截取指定长度的字符
        String username = uuid.substring(0, Math.min(length, uuid.length()));
        return prefix + username;
    }
}
