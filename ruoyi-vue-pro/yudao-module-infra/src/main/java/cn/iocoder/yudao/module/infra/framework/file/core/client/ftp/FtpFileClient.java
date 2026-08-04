package cn.iocoder.yudao.module.infra.framework.file.core.client.ftp;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpConfig;
import cn.hutool.extra.ftp.FtpException;
import cn.hutool.extra.ftp.FtpMode;
import cn.iocoder.yudao.module.infra.framework.file.core.client.AbstractFileClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Ftp 文件客户端
 *
 * @author 芋道源码
 */
public class FtpFileClient extends AbstractFileClient<FtpFileClientConfig> {

    /**
     * 连接超时时间，单位：毫秒
     */
    private static final Long CONNECTION_TIMEOUT = 3000L;
    /**
     * 读写超时时间，单位：毫秒
     */
    private static final Long SO_TIMEOUT = 10000L;

    private Ftp ftp;

    public FtpFileClient(Long id, FtpFileClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {
        // 初始化 Ftp 对象：https://gitee.com/zhijiantianya/yudao-cloud/pulls/207/
        FtpConfig ftpConfig = new FtpConfig(config.getHost(), config.getPort(), config.getUsername(), config.getPassword(),
                CharsetUtil.CHARSET_UTF_8, null, null);
        ftpConfig.setConnectionTimeout(CONNECTION_TIMEOUT);
        ftpConfig.setSoTimeout(SO_TIMEOUT);
        this.ftp = new Ftp(ftpConfig, FtpMode.valueOf(config.getMode()));
    }

    @Override
    public String upload(byte[] content, String path, String type) {
        // 执行写入
        String filePath = getFilePath(path);
        String fileName = FileUtil.getName(filePath);
        String dir = StrUtil.removeSuffix(filePath, fileName);
        reconnectIfTimeout();
        boolean success = ftp.upload(dir, fileName, new ByteArrayInputStream(content)); // 不需要主动创建目录，ftp 内部已经处理（见源码）
        if (!success) {
            throw new FtpException(StrUtil.format("上传文件到目标目录 ({}) 失败", filePath));
        }
        // 拼接返回路径
        return super.formatFileUrl(config.getDomain(), path);
    }

    @Override
    public void delete(String path) {
        String filePath = getFilePath(path);
        reconnectIfTimeout();
        ftp.delFile(filePath);
    }

    @Override
    public byte[] getContent(String path) {
        String filePath = getFilePath(path);
        String fileName = FileUtil.getName(filePath);
        String dir = StrUtil.removeSuffix(filePath, fileName);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        reconnectIfTimeout();
        ftp.download(dir, fileName, out);
        return out.toByteArray();
    }

    @Override
    public byte[] getContent(String path, long maxBytes) {
        if (maxBytes < 0 || maxBytes >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("maxBytes must be between 0 and Integer.MAX_VALUE - 1");
        }
        String filePath = getFilePath(path);
        String fileName = FileUtil.getName(filePath);
        String dir = StrUtil.removeSuffix(filePath, fileName);
        LimitedOutputStream out = new LimitedOutputStream(maxBytes + 1);
        reconnectIfTimeout();
        ftp.download(dir, fileName, out);
        return out.toByteArray();
    }

    private String getFilePath(String path) {
        return config.getBasePath() + StrUtil.SLASH + path;
    }

    private synchronized void reconnectIfTimeout() {
        ftp.reconnectIfTimeout();
    }

    private static final class LimitedOutputStream extends OutputStream {

        private final ByteArrayOutputStream delegate = new ByteArrayOutputStream();
        private long remaining;

        private LimitedOutputStream(long limit) {
            this.remaining = limit;
        }

        @Override
        public void write(int value) {
            if (remaining > 0) {
                delegate.write(value);
                remaining--;
            }
        }

        @Override
        public void write(byte[] bytes, int offset, int length) {
            int acceptedLength = (int) Math.min(remaining, length);
            if (acceptedLength > 0) {
                delegate.write(bytes, offset, acceptedLength);
                remaining -= acceptedLength;
            }
        }

        private byte[] toByteArray() {
            return delegate.toByteArray();
        }
    }

}
