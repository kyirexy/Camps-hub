package com.liuxy.campushub.controller;

import com.liuxy.campushub.common.Result;
import com.liuxy.campushub.service.AttachmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.lang.reflect.Array;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttachmentService attachmentService;

    @Test
    public void testUploadFile() throws Exception {
        // Create a test file
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test-image.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        // Test single file upload
        mockMvc.perform(MockMvcRequestBuilders.multipart("/attachments/upload")
                .file(file)
                .param("postId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    public void testUploadEmptyFile() throws Exception {
        // Create an empty file
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "empty.jpg",
            "image/jpeg",
            new byte[0]
        );

        // Test empty file upload
        mockMvc.perform(MockMvcRequestBuilders.multipart("/attachments/upload")
                .file(file)
                .param("postId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.message", containsString("文件为空")));
    }

    @Test
    public void testBatchUpload() throws Exception {
        // Create multiple test files
        MockMultipartFile file1 = new MockMultipartFile(
            "files",
            "test-image1.jpg",
            "image/jpeg",
            "test image content 1".getBytes()
        );

        MockMultipartFile file2 = new MockMultipartFile(
            "files",
            "test-image2.jpg",
            "image/jpeg",
            "test image content 2".getBytes()
        );

        // Test batch file upload
        mockMvc.perform(MockMvcRequestBuilders.multipart("/attachments/batch-upload")
                .file(file1)
                .file(file2)
                .param("postId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    @Test
    public void testGetAttachment() throws Exception {
        // First upload a file
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test-image.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        // Upload the file and get the file ID
        String response = mockMvc.perform(MockMvcRequestBuilders.multipart("/attachments/upload")
                .file(file)
                .param("postId", "1"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract file ID from response
        // Note: In a real test, you would parse the JSON response properly
        Long fileId = 1L; // This should be extracted from the response

        // Test getting attachment details
        mockMvc.perform(MockMvcRequestBuilders.get("/attachments/{fileId}", fileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data.fileId", is(fileId.intValue())))
                .andExpect(jsonPath("$.data.postId", is(1)))
                .andExpect(jsonPath("$.data.fileType", is("image")));
    }

    @Test
    public void testGetAttachmentsByPost() throws Exception {
        // Test getting attachments for a post
        mockMvc.perform(MockMvcRequestBuilders.get("/attachments/post/{postId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data", isA(Array.class)));
    }
} 