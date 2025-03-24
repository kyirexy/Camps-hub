package com.liuxy.campushub.dto;

import org.hibernate.validator.constraints.Range;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 30, message = "真实姓名长度不能超过30个字符")
    private String realName;

    @NotBlank(message = "学号不能为空")
    @Pattern(regexp = "^\\d{12}$", message = "学号必须是12位数字")
    private String studentNumber;

    @Pattern(regexp = "^[MFO]$", message = "性别必须是M、F或O")
    private String gender = "O";

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.edu$", message = "必须是有效的教育邮箱")
    private String email;

    @NotNull(message = "院系ID不能为空")
    private Integer collegeId;

    @NotBlank(message = "专业不能为空")
    @Size(max = 30, message = "专业名称长度不能超过30个字符")
    private String major;

    @NotNull(message = "年级不能为空")
    @Min(value = 2000, message = "年级不能小于2000")
    @Max(value = 2100, message = "年级不能大于2100")
    @org.hibernate.validator.constraints.Range(min = 2000, max = 2155, message = "年级需在2000-2155之间")
    private Integer grade;

    @Size(max = 100, message = "个人简介不能超过100个字符")
    @Size(max = 200, message = "个人简介长度不能超过200个字符")
    private String bio;

    @NotBlank(message = "教务密码不能为空")
    private String jwPassword;
}