package com.ism.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {
    private String token;
    private Long userId;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String department;
    private String roleName;
    private String avatar;
    private List<String> roles;
    private List<String> permissions;
}
