package com.example.borrowingbooks.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.borrowingbooks.DTO.UserDTO;
import com.example.borrowingbooks.VO.PermissionVO;
import com.example.borrowingbooks.VO.UserJurisdictionVO;
import com.example.borrowingbooks.VO.UserVO;
import com.example.borrowingbooks.common.Result;
import com.example.borrowingbooks.entity.LoginUser;
import com.example.borrowingbooks.entity.Permission;
import com.example.borrowingbooks.entity.User;
import com.example.borrowingbooks.mapper.UserMapper;
import com.example.borrowingbooks.service.IPermissionService;
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

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final static String USER_ALL_INFO = "user:all:info";

    @Resource
    private RedisUtil  redisUtil;

    @Resource
    private PasswordEncoder  passwordEncoder;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private IPermissionService permissionService;

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
        boolean set = redisUtil.set(TokenKey, token, 30);
        boolean json = redisUtil.setJson(UserInfoKey, loginUser, 30);
        if (!set || !json){
            log.error("Redis 写入失败，TokenKey: {}, UserInfoKey: {}", TokenKey, UserInfoKey);
            return Result.fail("Redis 写入失败");
        }
        log.info("用户登录成功：{}", build);
        return Result.ok(build);
    }

    @Override
    public Result<List<UserJurisdictionVO>> getUserJurisdiction() {

        List<UserJurisdictionVO> jsonList = redisUtil.getJsonList(USER_ALL_INFO, UserJurisdictionVO.class);
        if (jsonList != null){
            log.info("从Redis中获取用户权限");
            return Result.ok(jsonList);
        }
        log.info("从数据库中获取用户权限");
        List<User> users = this.list();
        if (users.isEmpty()) {
            log.error("用户为空");
            redisUtil.setJson(USER_ALL_INFO, Collectors.toList(), 2);
            return Result.fail("用户为空");
        }

        List<UserJurisdictionVO> resultList = users.stream().map(user -> {
            List<Permission> permissions = permissionService.list(
                    new LambdaQueryWrapper<Permission>()
                            .eq(Permission::getRoleId, user.getRoleId())
            );

            List<PermissionVO> permissionVOS = permissions.stream().map(p -> {
                return PermissionVO.builder()
                        .permissionCode(p.getPermissionCode())
                        .permissionName(p.getPermissionName())
                        .description(p.getDescription())
                        .build();
            }).collect(Collectors.toList());

            return UserJurisdictionVO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .phone(user.getPhone())
                    .email(user.getEmail())
                    .roleId(user.getRoleId())
                    .createTime(user.getCreateTime())
                    .avatarUrl(user.getAvatarUrl())
                    .roleName(permissions.isEmpty() ? null : permissions.get(0).getRoleName())
                    .permissions(permissionVOS)
                    .build();
        }).collect(Collectors.toList());
        log.info("获取用户权限成功：{}", resultList);
        boolean json = redisUtil.setJson(USER_ALL_INFO, resultList, 30);
        if (!json){
            log.error("Redis 写入失败，Key: {}", USER_ALL_INFO);
            return Result.fail("Redis 写入失败");
        }
        return Result.ok(resultList);
    }

    @Override
    public Result<String> updateUser(User user) {
        if (user == null){
            log.error("用户不能为空");
            return Result.fail("用户不能为空");
        }
        if (user.getId() == null){
            log.error("用户ID不能为空");
            return Result.fail("用户ID不能为空");
        }
        boolean byId = this.updateById(user);
        if (!byId){
            log.error("用户 {} 更新失败", user.getId());
            return Result.fail("用户更新失败");
        }
        boolean delete = redisUtil.delete(USER_ALL_INFO);
        if (!delete){
            log.error("删除缓存 {} 失败", USER_ALL_INFO);
        }
        return Result.ok("用户更新成功");
    }

    @Override
    public Result<String> deleteUser(Long id) {
        if (id == null){
            log.error("用户ID不能为空");
            return Result.fail("用户ID不能为空");
        }
        boolean delete = this.removeById(id);
        if (!delete){
            log.error("用户 {} 删除失败", id);
            return Result.fail("用户删除失败");
        }
        boolean b = redisUtil.delete(USER_ALL_INFO);
        if (!b){
            log.error("删除缓存 {} 失败", USER_ALL_INFO);
        }
        return Result.ok("用户删除成功");
    }

    public static String generateUsername(String prefix, int length) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 截取指定长度的字符
        String username = uuid.substring(0, Math.min(length, uuid.length()));
        return prefix + username;
    }
}
