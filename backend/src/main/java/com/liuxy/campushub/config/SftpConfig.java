package com.liuxy.campushub.config;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class SftpConfig {
    private static final Logger logger = LoggerFactory.getLogger(SftpConfig.class);

    @Value("${sftp.host}")
    private String host;

    @Value("${sftp.port}")
    private int port;

    @Value("${sftp.username}")
    private String username;

    @Value("${sftp.password}")
    private String password;

    @Value("${sftp.remote-path}")
    private String remotePath;

    @Bean
    public ChannelSftp channelSftp() throws Exception {
        logger.info("开始配置SFTP连接，主机: {}, 端口: {}, 用户名: {}, 远程路径: {}", 
            host, port, username, remotePath);
        
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        session.setPassword(password);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        logger.info("正在连接SFTP服务器...");
        session.connect();
        logger.info("SFTP会话连接成功");

        logger.info("正在打开SFTP通道...");
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();
        logger.info("SFTP通道连接成功");

        // 验证远程目录
        try {
            logger.info("验证远程目录: {}", remotePath);
            channelSftp.cd(remotePath);
            logger.info("远程目录验证成功");
        } catch (Exception e) {
            logger.error("远程目录验证失败: {}", e.getMessage());
            throw e;
        }

        return channelSftp;
    }
} 