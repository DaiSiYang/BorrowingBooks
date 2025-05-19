package com.example.borrowingbooks.filter;


import com.example.borrowingbooks.common.RedisExpireTime;
import com.example.borrowingbooks.common.RedisKeyConstants;
import com.example.borrowingbooks.entity.LoginUser;
import com.example.borrowingbooks.service.IUserService;
import com.example.borrowingbooks.utils.JwtUtil;
import com.example.borrowingbooks.utils.RedisUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import static com.example.borrowingbooks.common.RedisExpireTime.USER_INFO;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private IUserService userService;
    @Resource
    private RedisUtil redisUtil;
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 从请求头获取Token
        String token = parseToken(request);
        if (token != null && jwtUtil.validateToken(token)) {
            // 提取用户名并加载用户信息
            long userId;
            try {
                String Id = jwtUtil.extractUserId(token);
                userId = Long.parseLong(Id);
                String redisKey = String.format(RedisKeyConstants.User.TOKEN, userId);
                if (!token.equals(redisUtil.get(redisKey))) {
                    throw new RuntimeException("Token已失效");
                }
                redisUtil.expire(redisKey, USER_INFO, TimeUnit.MINUTES);
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
            // 从Redis中获取用户信息
            String UserInfoKey = String.format(RedisKeyConstants.User.INFO, userId);
            LoginUser loginUser = redisUtil.getJson(UserInfoKey, LoginUser.class);
            if (Objects.isNull(loginUser)){
                throw new RuntimeException("用户信息不存在");
            }
            redisUtil.expire(UserInfoKey, USER_INFO, TimeUnit.MINUTES);

            // 构建认证对象
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    loginUser, null, loginUser.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String parseToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
