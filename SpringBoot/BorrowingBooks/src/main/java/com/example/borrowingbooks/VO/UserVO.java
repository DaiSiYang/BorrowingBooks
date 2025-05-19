package com.example.borrowingbooks.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVO {
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private Integer roleId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String avatarUrl;
    private String token;
}
