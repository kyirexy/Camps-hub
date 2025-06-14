package com.liuxy.campushub.aiutil;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.liuxy.campushub.aidto.ResponseMsg;
import com.liuxy.campushub.aidto.StatusResp;
import com.liuxy.campushub.aidto.SummaryResp;
import com.liuxy.campushub.aidto.UploadResp;
import com.liuxy.campushub.aidto.chat.ChatExtends;
import com.liuxy.campushub.aidto.chat.ChatMessage;
import com.liuxy.campushub.aidto.chat.ChatRequest;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * ChatDoc API工具类
 * 提供讯飞开放平台ChatDoc相关API的调用实现
 * 包括文件上传、状态查询、聊天对话、文档总结等功能
 *
 * @author mlxu11
 * @version 2024/08/29 13:59
 **/
@Component
public class ChatDocUtil {

    /**
     * HTTP客户端配置
     * 设置连接池大小为100，保持连接时间5分钟
     * 读取超时时间设置为10分钟
     */
    private final static OkHttpClient client = new OkHttpClient().newBuilder()
            .connectionPool(new ConnectionPool(100, 5, TimeUnit.MINUTES))
            .readTimeout(60*10, TimeUnit.SECONDS)
            .build();

    /**
     * 上传文件到ChatDoc平台
     *
     * @param filePath 待上传文件的本地路径
     * @param url      上传接口URL
     * @param appId    应用ID
     * @param secret   应用密钥
     * @return 上传响应结果，包含文件ID等信息
     */
    public UploadResp upload(String filePath, String url, String appId, String secret) {
        File file = new File(filePath);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("file", file.getName(),
                RequestBody.create(MediaType.parse("multipart/form-data"), file));
        builder.addFormDataPart("fileType", "wiki");
        RequestBody body = builder.build();
        long ts = System.currentTimeMillis() / 1000;
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("appId", appId)
                .addHeader("timestamp", String.valueOf(ts))
                .addHeader("signature", ApiAuthUtil.getSignature(appId, secret, ts))
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (Objects.equals(response.code(), 200)) {
                String respBody = response.body().string();
                return JSONUtil.toBean(respBody, UploadResp.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 查询文档处理状态
     *
     * @param statusUrl 状态查询接口URL
     * @param fileId    文件ID，多个文件ID用英文逗号分隔
     * @param appId     应用ID
     * @param secret    应用密钥
     * @return 文档状态信息
     */
    public StatusResp status(String statusUrl, String fileId, String appId, String secret) {
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("fileIds", fileId)
                .build();
        long ts = System.currentTimeMillis() / 1000;
        Request request = new Request.Builder()
                .url(statusUrl)
                .method("POST",body)
                .addHeader("appId", appId)
                .addHeader("timestamp", String.valueOf(ts))
                .addHeader("signature", ApiAuthUtil.getSignature(appId, secret, ts))
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (Objects.equals(response.code(), 200)) {
                String respBody = response.body().string();
                System.out.println(respBody);
                return JSONUtil.toBean(respBody, StatusResp.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 发起文档问答对话
     * 通过WebSocket与服务器建立连接，发送问题并接收流式回答
     *
     * @param chatUrl  聊天接口URL
     * @param fileId   文件ID
     * @param question 用户问题
     * @param appId    应用ID
     * @param secret   应用密钥
     * @return CompletableFuture<String> 返回异步的回答结果
     */
    public CompletableFuture<String> chat(String chatUrl, String fileId, String question, String appId, String secret) {
        CompletableFuture<String> future = new CompletableFuture<>();
        
        ChatMessage message = new ChatMessage();
        message.setRole("user");
        message.setContent(question);
        
        ChatExtends chatExtends = ChatExtends.builder()
                .spark(true)
                .build();
        ChatRequest request = ChatRequest.builder()
                .fileIds(Collections.singletonList(fileId))
                .topN(3)
                .messages(Collections.singletonList(message))
                .build();

        long ts = System.currentTimeMillis() / 1000;
        String signature = ApiAuthUtil.getSignature(appId, secret, ts);
        String requestUrl = chatUrl + "?" + "appId=" + appId + "&timestamp=" + ts + "&signature=" + signature;
        
        Request wsRequest = (new Request.Builder()).url(requestUrl).build();
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();
        StringBuffer buffer = new StringBuffer();
        
        WebSocket webSocket = okHttpClient.newWebSocket(wsRequest, new WebSocketListener() {
            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosed(webSocket, code, reason);
                webSocket.close(1002, "websocket finish");
                okHttpClient.connectionPool().evictAll();
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                webSocket.close(1001, "websocket finish");
                okHttpClient.connectionPool().evictAll();
                future.completeExceptionally(t);
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                JSONObject jsonObject = JSONUtil.parseObj(text);
                String content = jsonObject.getStr("content");
                if (StrUtil.isNotEmpty(content)) {
                    buffer.append(content);
                }
                if (Objects.equals(jsonObject.getInt("status"), 2)) {
                    String result = buffer.toString();
                    future.complete(result);
                    webSocket.close(1000, "websocket finish");
                    okHttpClient.connectionPool().evictAll();
                }
            }
        });
        
        webSocket.send(JSONUtil.toJsonStr(request));
        return future;
    }

    /**
     * 发起文档总结任务
     *
     * @param SummaryStartUrl 总结任务启动接口URL
     * @param fileId         文件ID
     * @param appId         应用ID
     * @param secret        应用密钥
     * @return 任务响应信息
     */
    public ResponseMsg start(String SummaryStartUrl, String fileId, String appId, String secret) {
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("fileId", fileId)
                .build();

        long ts = System.currentTimeMillis() / 1000;
        Request request = new Request.Builder()
                .url(SummaryStartUrl)
                .method("POST",body)
                .addHeader("appId", appId)
                .addHeader("timestamp", String.valueOf(ts))
                .addHeader("signature", ApiAuthUtil.getSignature(appId, secret, ts))
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (Objects.equals(response.code(), 200)) {
                String respBody = response.body().string();
                return JSONUtil.toBean(respBody, ResponseMsg.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 查询文档总结结果
     *
     * @param SummaryStartUrl 总结结果查询接口URL
     * @param fileId         文件ID
     * @param appId         应用ID
     * @param secret        应用密钥
     * @return 总结结果信息
     */
    public SummaryResp query(String SummaryStartUrl, String fileId, String appId, String secret) {
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("fileId", fileId)
                .build();

        long ts = System.currentTimeMillis() / 1000;
        Request request = new Request.Builder()
                .url(SummaryStartUrl)
                .method("POST",body)
                .addHeader("appId", appId)
                .addHeader("timestamp", String.valueOf(ts))
                .addHeader("signature", ApiAuthUtil.getSignature(appId, secret, ts))
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (Objects.equals(response.code(), 200)) {
                String respBody = response.body().string();
                return JSONUtil.toBean(respBody, SummaryResp.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}