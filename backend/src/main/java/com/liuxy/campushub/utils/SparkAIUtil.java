package com.liuxy.campushub.utils;

import com.alibaba.fastjson.JSONObject;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.springframework.stereotype.Component;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Component
public class SparkAIUtil {
    
    private final OkHttpClient okHttpClient;
    private final JSONObject config;
    
    public SparkAIUtil(OkHttpClient okHttpClient, JSONObject config) {
        this.okHttpClient = okHttpClient;
        this.config = config;
    }
    
    public String getAppId() {
        return config.getString("appId");
    }
    
    public WebSocket createWebSocket(WebSocketListener listener) throws Exception {
        String url = config.getString("url");
        String apiKey = config.getString("apiKey");
        String apiSecret = config.getString("apiSecret");
        String appId = config.getString("appId");

        // 将 https 转换为 wss
        String wsUrl = url.replace("https://", "wss://");
        String authUrl = getAuthUrl(wsUrl, apiKey, apiSecret);
        
        Request request = new Request.Builder()
                .url(authUrl)
                .build();
        
        return okHttpClient.newWebSocket(request, listener);
    }
    
    private String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URI uri = new URI(hostUrl);
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        
        String preStr = "host: " + uri.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + uri.getPath() + " HTTP/1.1";
        
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);
        
        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"",
                apiKey, "hmac-sha256", "host date request-line", sha);
        
        // 构建查询参数
        String queryParams = String.format("authorization=%s&date=%s&host=%s",
                Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8)),
                date,
                uri.getHost());
        
        // 构建完整的 URL
        String fullUrl = String.format("%s?%s", hostUrl, queryParams);
        
        return fullUrl;
    }
} 