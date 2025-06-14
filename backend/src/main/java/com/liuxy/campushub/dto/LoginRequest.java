package com.liuxy.campushub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "登录凭证不能为空")
    @Pattern(regexp = "^(\\d{11}|[a-zA-Z0-9_]{4,20})$", 
             message = "登录凭证格式不正确（支持手机号/学号/用户名）")
    private String loginId;

    @NotBlank(message = "密码不能为空")
    private String password;
}