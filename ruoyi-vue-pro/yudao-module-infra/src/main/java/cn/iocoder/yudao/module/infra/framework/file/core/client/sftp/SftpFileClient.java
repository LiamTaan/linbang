package cn.iocoder.yudao.module.infra.framework.file.core.client.sftp;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.FtpConfig;
import cn.hutool.extra.ssh.JschRuntimeException;
import cn.hutool.extra.ssh.Sftp;
import cn.iocoder.yudao.module.infra.framework.file.core.client.AbstractFileClient;
import com.google.common.io.ByteStreams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Sftp 文件客户端
 *
 * @author 芋道源码
 */
public class SftpFileClient extends AbstractFileClient<SftpFileClientConfig> {

    /**
     * 连接超时时间，单位：毫秒
     */
    private static final Long CONNECTION_TIMEOUT = 3000L;
    /**
     * 读写超时时间，单位：毫秒
     */
    private static final Long SO_TIMEOUT = 10000L;

    private Sftp sftp;

    public SftpFileClient(Long id, SftpFileClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {
        // 初始化 Sftp 对象
        FtpConfig ftpConfig = new FtpConfig(config.getHost(), config.getPort(), config.getUsername(), config.getPassword(),
                CharsetUtil.CHARSET_UTF_8, null, null);
        ftpConfig.setConnectionTimeout(CONNECTION_TIMEOUT);
        ftpConfig.setSoTimeout(SO_TIMEOUT);
        this.sftp = new Sftp(ftpConfig);
    }

    @Override
    public String upload(byte[] content, String path, String type) {
        // 执行写入
        String filePath = getFilePath(path);
        String fileName = FileUtil.getName(filePath);
        String dir = StrUtil.removeSuffix(filePath, fileName);
        reconnectIfTimeout();
        sftp.mkDirs(dir); // 需要创建父目录，不然会报错
        try (InputStream input = new ByteArrayInputStream(content)) {
            boolean success = sftp.upload(dir, fileName, input);
            if (!success) {
                throw new JschRuntimeException(StrUtil.format("上传文件到目标目录 ({}) 失败", filePath));
            }
        } catch (Exception ex) {
            throw new JschRuntimeException(ex);
        }
        // 拼接返回路径
        return super.formatFileUrl(config.getDomain(), path);
    }

    @Override
    public void delete(String path) {
        String filePath = getFilePath(path);
        reconnectIfTimeout();
        sftp.delFile(filePath);
    }

    @Override
    public byte[] getContent(String path) {
        String filePath = getFilePath(path);
        reconnectIfTimeout();
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            sftp.download(filePath, output);
            return output.toByteArray();
        } catch (Exception ex) {
            throw new JschRuntimeException(ex);
        }
    }

    @Override
    public byte[] getContent(String path, long maxBytes) throws Exception {
        if (maxBytes < 0 || maxBytes >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("maxBytes must be between 0 and Integer.MAX_VALUE - 1");
        }
        String filePath = getFilePath(path);
        try {
            reconnectIfTimeout();
            try (InputStream input = sftp.getClient().get(filePath)) {
                return ByteStreams.toByteArray(ByteStreams.limit(input, maxBytes + 1));
            }
        } catch (JschRuntimeException ex) {
            throw ex;
        }
    }

    private String getFilePath(String path) {
        return config.getBasePath() + "/" + path;
    }

    private synchronized void reconnectIfTimeout() {
        sftp.reconnectIfTimeout();
    }

}
