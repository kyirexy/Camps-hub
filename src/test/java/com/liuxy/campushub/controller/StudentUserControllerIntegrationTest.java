package com.liuxy.campushub.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentUserControllerIntegrationTest {

    @Test
    public void testRegisterInvalidRequest() throws Exception {
        // 测试密码复杂度不足
        String invalidPasswordRequest = "{\"username\":\"testuser\",\"password\":\"1234\",\"studentNumber\":\"20230003\",\"collegeId\":1,\"major\":\"网络工程\",\"grade\":2023,\"phone\":\"13800138002\",\"email\":\"test3@example.com\"}";
        mockMvc.perform(post("/api/v1/student/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPasswordRequest))
                .andExpect(status().isBadRequest());

        // 测试缺少必填字段
        String missingFieldRequest = "{\"username\":\"testuser\",\"password\":\"Test@1234\",\"studentNumber\":\"20230003\"}";
        mockMvc.perform(post("/api/v1/student/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(missingFieldRequest))
                .andExpect(status().isBadRequest());
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRegisterSuccess() throws Exception {
        String jsonRequest = "{\"username\":\"testuser\",\"password\":\"Test@1234\",\"realName\":\"测试用户\",\"studentNumber\":\"20230001\",\"collegeId\":1,\"major\":\"计算机科学\",\"grade\":2023,\"phone\":\"13800138000\",\"email\":\"test@example.com\"}";

        mockMvc.perform(post("/api/v1/student/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterDuplicateUsername() throws Exception {
        String jsonRequest = "{\"username\":\"existinguser\",\"password\":\"Test@1234\",\"studentNumber\":\"20230002\",\"collegeId\":1,\"major\":\"软件工程\",\"grade\":2023,\"phone\":\"13800138001\",\"email\":\"test2@example.com\"}";

        mockMvc.perform(post("/api/v1/student/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isConflict());
    }

    @Test
    public void testRegisterBoundaryValues() throws Exception {
        // 测试用户名边界值
        String minUsername = "{\"username\":\"tes\",\"password\":\"Test@1234\",\"realName\":\"测\",\"studentNumber\":\"202300000001\",\"collegeId\":1,\"major\":\"计算机\",\"grade\":2023,\"phone\":\"+8613800138000\",\"email\":\"test@example.edu\",\"jwPassword\":\"password\"}";
        mockMvc.perform(post("/api/v1/student/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(minUsername))
                .andExpect(status().isOk());

        // 测试密码最小长度
        String minPassword = "{\"username\":\"boundary\",\"password\":\"Test12\",\"realName\":\"测试用户\",\"studentNumber\":\"202300000002\",\"collegeId\":1,\"major\":\"软件工程\",\"grade\":2023,\"phone\":\"13800138001\",\"email\":\"test2@example.edu\",\"jwPassword\":\"password\"}";
        mockMvc.perform(post("/api/v1/student/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(minPassword))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidFormats() throws Exception {
        // 测试非法性别值
        String invalidGender = "{\"username\":\"testuser\",\"password\":\"Test@1234\",\"realName\":\"测试用户\",\"studentNumber\":\"202300000003\",\"gender\":\"X\",\"collegeId\":1,\"major\":\"网络工程\",\"grade\":2023,\"phone\":\"13800138002\",\"email\":\"test3@example.edu\",\"jwPassword\":\"password\"}";
        mockMvc.perform(post("/api/v1/student/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidGender))
                .andExpect(status().isBadRequest());

        // 测试超长专业名称
        String longMajor = "{\"username\":\"testuser\",\"password\":\"Test@1234\",\"realName\":\"测试用户\",\"studentNumber\":\"202300000004\",\"collegeId\":1,\"major\":\"这是一个超过三十个字符长度的非法专业名称字段测试用例\",\"grade\":2023,\"phone\":\"13800138003\",\"email\":\"test4@example.edu\",\"jwPassword\":\"password\"}";
        mockMvc.perform(post("/api/v1/student/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(longMajor))
                .andExpect(status().isBadRequest());
    }
}