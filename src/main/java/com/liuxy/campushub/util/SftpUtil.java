package com.liuxy.campushub.util;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Vector;
import java.util.UUID;

@Component
public class SftpUtil {
    private static final Logger logger = LoggerFactory.getLogger(SftpUtil.class);

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

    private ChannelSftp channelSftp;
    private Session session;

    public void connect() throws JSchException {
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            logger.info("正在连接SFTP服务器 - 主机: {}, 端口: {}, 用户名: {}", host, port, username);
            session.connect();
            logger.info("SFTP服务器连接成功");

            Channel channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            logger.info("SFTP通道已打开");
        } catch (JSchException e) {
            logger.error("SFTP连接失败: {}", e.getMessage());
            throw e;
        }
    }

    public void disconnect() {
        if (channelSftp != null && channelSftp.isConnected()) {
            channelSftp.disconnect();
            logger.info("SFTP通道已关闭");
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
            logger.info("SFTP会话已关闭");
        }
    }

    private void ensureDirectoryExists(String path) throws IOException {
        try {
            logger.info("检查目录是否存在: {}", path);
            channelSftp.ls(path);
            logger.info("目录已存在: {}", path);
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                logger.info("目录不存在，尝试创建: {}", path);
                try {
                    channelSftp.mkdir(path);
                    logger.info("成功创建目录: {}", path);
                } catch (SftpException ex) {
                    logger.error("创建目录失败: {} - {}", path, ex.getMessage());
                    throw new IOException("创建目录失败: " + ex.getMessage());
                }
            } else {
                logger.error("检查目录失败: {} - {}", path, e.getMessage());
                throw new IOException("检查目录失败: " + e.getMessage());
            }
        }
    }

    /**
     * 上传文件到SFTP服务器
     *
     * @param file 要上传的文件
     * @param remotePath 远程路径
     * @return 上传后的文件名
     */
    public String uploadFile(MultipartFile file, String remotePath) throws IOException {
        if (channelSftp == null || !channelSftp.isConnected()) {
            try {
                connect();
            } catch (JSchException e) {
                throw new IOException("SFTP连接失败: " + e.getMessage());
            }
        }

        try {
            logger.info("开始SFTP上传文件，远程路径: {}", remotePath);
            logger.info("SFTP连接状态: {}", channelSftp.isConnected() ? "已连接" : "未连接");

            // 生成文件名：年月日_时分秒_随机UUID.扩展名
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            
            // 上传文件
            String fullPath = remotePath + "/" + filename;
            logger.info("开始上传文件: {}", fullPath);
            try (InputStream inputStream = file.getInputStream()) {
                channelSftp.put(inputStream, fullPath);
                logger.info("文件上传成功: {}", fullPath);
                return filename;
            }
        } catch (SftpException e) {
            logger.error("SFTP上传文件失败: {}", e.getMessage());
            throw new IOException("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("文件上传过程中发生错误: {}", e.getMessage());
            throw new IOException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除SFTP服务器上的文件
     *
     * @param remotePath 远程路径
     * @param fileName 文件名
     */
    public void deleteFile(String remotePath, String fileName) throws IOException {
        if (channelSftp == null || !channelSftp.isConnected()) {
            try {
                connect();
            } catch (JSchException e) {
                throw new IOException("SFTP连接失败: " + e.getMessage());
            }
        }

        try {
            String fullPath = remotePath + "/" + fileName;
            logger.info("开始删除文件: {}", fullPath);
            channelSftp.rm(fullPath);
            logger.info("文件删除成功: {}", fullPath);
        } catch (SftpException e) {
            logger.error("删除文件失败: {}", e.getMessage());
            throw new IOException("删除文件失败: " + e.getMessage());
        }
    }

    /**
     * 列出目录下的文件
     *
     * @param remotePath 远程路径
     * @return 文件列表
     */
    public Vector<ChannelSftp.LsEntry> listFiles(String remotePath) throws IOException {
        if (channelSftp == null || !channelSftp.isConnected()) {
            try {
                connect();
            } catch (JSchException e) {
                throw new IOException("SFTP连接失败: " + e.getMessage());
            }
        }

        try {
            logger.info("开始列出目录内容: {}", remotePath);
            Vector<ChannelSftp.LsEntry> list = channelSftp.ls(remotePath);
            logger.info("成功列出目录内容，文件数量: {}", list.size());
            return list;
        } catch (SftpException e) {
            logger.error("列出目录内容失败: {}", e.getMessage());
            throw new IOException("列出目录内容失败: " + e.getMessage());
        }
    }

    /**
     * 下载文件
     *
     * @param remotePath 远程路径
     * @param fileName 文件名
     * @return 文件输入流
     */
    public InputStream downloadFile(String remotePath, String fileName) throws SftpException {
        logger.info("开始下载文件，路径: {}, 文件名: {}", remotePath, fileName);
        channelSftp.cd(remotePath);
        return channelSftp.get(fileName);
    }
} 