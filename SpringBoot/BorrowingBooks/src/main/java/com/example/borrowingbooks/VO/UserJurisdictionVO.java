package com.example.borrowingbooks.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJurisdictionVO {
    private Long id;
    private String username;
    private String phone;
    private String email;
    private Integer roleId;
    private String avatarUrl;
    private String roleName;
    private LocalDateTime createTime;
    private List<PermissionVO> permissions;
}
