package com.liuxy.campushub.controller;

import com.liuxy.campushub.aidto.ResponseMsg;
import com.liuxy.campushub.aidto.StatusResp;
import com.liuxy.campushub.aidto.chat.ChatMessage;
import com.liuxy.campushub.aidto.chat.ChatRequest;
import com.liuxy.campushub.aiutil.ChatDocUtil;
import com.liuxy.campushub.dto.ChatResponse;
import com.liuxy.campushub.dto.SparkChatRequest;
import com.liuxy.campushub.dto.SparkChatResponse;
import com.liuxy.campushub.service.AIChatService;
import com.liuxy.campushub.service.SparkChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.Collections;
import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/ai")
public class AIChatController {
    
    private static final Logger logger = LoggerFactory.getLogger(AIChatController.class);
    
    private static final String APP_ID = "9c5bc113";
    private static final String SECRET = "NTYxNzdkOGFlN2FiYjZhZDY3NThlNjIw";
    private static final String CHAT_URL = "wss://chatdoc.xfyun.cn/openapi/chat";
    private static final String STATUS_URL = "https://chatdoc.xfyun.cn/openapi/v1/file/status";
    private static final String FILE_ID = "af76e4fbb58446ae8f9b7c0c46b7b677";
    
    @Autowired
    private AIChatService aiChatService;
    
    @Autowired
    private SparkChatService sparkChatService;
    
    @Autowired
    private ChatDocUtil chatDocUtil;
    
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody com.liuxy.campushub.dto.ChatRequest request) {
        logger.info("收到AI聊天请求: {}", request.getMessage());
        try {
            ChatResponse response = aiChatService.chat(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("AI聊天请求处理失败", e);
            return ResponseEntity.internalServerError()
                .body(ChatResponse.error("处理请求失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/spark/chat")
    public ResponseEntity<SparkChatResponse> sparkChat(@Valid @RequestBody SparkChatRequest request) {
        logger.info("收到星火知识库聊天请求: {}", request);
        try {
            SparkChatResponse response = sparkChatService.chat(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("星火知识库聊天请求处理失败", e);
            SparkChatResponse response = new SparkChatResponse();
            response.setCode(500);
            response.setMessage("处理请求失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/chatdoc/status")
    public ResponseEntity<Map<String, Object>> checkFileStatus() {
        logger.info("检查文件状态");
        try {
            StatusResp statusResp = chatDocUtil.status(STATUS_URL, FILE_ID, APP_ID, SECRET);
            List<StatusResp.Datas> dataList = statusResp.getData();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", statusResp.getCode());
            response.put("message", "success");
            response.put("data", dataList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("检查文件状态失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "检查文件状态失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/chatdoc/chat")
    public ResponseEntity<ChatRequest> chatWithDoc(@RequestParam String question) {
        logger.info("收到文档问答请求 - 问题: {}", question);
        try {
            // 先检查文件状态
            StatusResp statusResp = chatDocUtil.status(STATUS_URL, FILE_ID, APP_ID, SECRET);
            List<StatusResp.Datas> dataList = statusResp.getData();
            boolean isVectored = false;
            
            for (StatusResp.Datas data : dataList) {
                if ("vectored".equals(data.getFileStatus())) {
                    isVectored = true;
                    break;
                }
            }
            
            if (!isVectored) {
                ChatMessage errorMessage = new ChatMessage();
                errorMessage.setRole("assistant");
                errorMessage.setContent("文件未向量化，无法进行问答");
                
                return ResponseEntity.badRequest().body(
                    ChatRequest.builder()
                        .fileIds(Collections.singletonList(FILE_ID))
                        .messages(Collections.singletonList(errorMessage))
                        .build()
                );
            }
            
            // 进行问答并等待结果
            CompletableFuture<String> futureResponse = chatDocUtil.chat(CHAT_URL, FILE_ID, question, APP_ID, SECRET);
            String answer = futureResponse.get(30, TimeUnit.SECONDS); // 设置30秒超时
            
            // 构建响应消息
            ChatMessage userMessage = new ChatMessage();
            userMessage.setRole("user");
            userMessage.setContent(question);
            
            ChatMessage assistantMessage = new ChatMessage();
            assistantMessage.setRole("assistant");
            assistantMessage.setContent(answer);
            
            List<ChatMessage> messages = Arrays.asList(userMessage, assistantMessage);
            
            return ResponseEntity.ok(
                ChatRequest.builder()
                    .fileIds(Collections.singletonList(FILE_ID))
                    .messages(messages)
                    .topN(3)
                    .build()
            );
            
        } catch (Exception e) {
            logger.error("文档问答失败: {}", e.getMessage());
            ChatMessage errorMessage = new ChatMessage();
            errorMessage.setRole("assistant");
            errorMessage.setContent("处理请求失败: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(
                ChatRequest.builder()
                    .fileIds(Collections.singletonList(FILE_ID))
                    .messages(Collections.singletonList(errorMessage))
                    .build()
            );
        }
    }
} 