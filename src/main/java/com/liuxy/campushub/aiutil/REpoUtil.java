package com.liuxy.campushub.aiutil;

import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 知识库操作工具类
 * 提供讯飞开放平台ChatDoc知识库相关API的调用实现
 * 包括知识库的创建、文件管理、查询等功能
 * 详细接口文档请查看 https://www.xfyun.cn/doc/spark/ChatDoc-API.html
 **/
@Component
public class REpoUtil {

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
     * 发送POST请求到知识库相关接口
     * 用于创建知识库、添加文件、查询知识库列表等操作
     *
     * @param url    接口URL
     * @param appId  应用ID
     * @param secret 应用密钥
     * @param body   请求体
     * @return 接口响应内容，失败返回null
     */
    public String repoReqHttp(String url, String appId, String secret, RequestBody body) {
        long ts = System.currentTimeMillis() / 1000;
        Request request = new Request.Builder()
                .url(url)
                .method("POST",body)  //POST 请求
                .addHeader("appId", appId)
                .addHeader("timestamp", String.valueOf(ts))
                .addHeader("signature", ApiAuthUtil.getSignature(appId, secret, ts))
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (Objects.equals(response.code(), 200)) {
                String respBody = response.body().string();
                System.out.println(respBody);
                return respBody;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 发送GET请求到知识库相关接口
     * 用于查询知识库信息、文件状态等操作
     *
     * @param url    接口URL
     * @param appId  应用ID
     * @param secret 应用密钥
     * @return 接口响应内容，失败返回null
     */
    public String repoReqGetHttp(String url, String appId, String secret) {
        long ts = System.currentTimeMillis() / 1000;
        Request request = new Request.Builder()
                .url(url)
                .get() // GET 请求
                .addHeader("appId", appId)
                .addHeader("timestamp", String.valueOf(ts))
                .addHeader("signature", ApiAuthUtil.getSignature(appId, secret, ts))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            // 打印并返回响应内容
            String respBody = response.body().string();
            System.out.println(respBody);
            return respBody;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
