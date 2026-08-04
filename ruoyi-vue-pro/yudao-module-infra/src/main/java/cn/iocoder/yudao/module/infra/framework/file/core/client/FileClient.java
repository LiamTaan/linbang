package cn.iocoder.yudao.module.infra.framework.file.core.client;

import java.util.Arrays;

/**
 * 文件客户端
 *
 * @author 芋道源码
 */
public interface FileClient {

    /**
     * 获得客户端编号
     *
     * @return 客户端编号
     */
    Long getId();

    /**
     * 上传文件
     *
     * @param content 文件流
     * @param path    相对路径
     * @return 完整路径，即 HTTP 访问地址
     * @throws Exception 上传文件时，抛出 Exception 异常
     */
    String upload(byte[] content, String path, String type) throws Exception;

    /**
     * 删除文件
     *
     * @param path 相对路径
     * @throws Exception 删除文件时，抛出 Exception 异常
     */
    void delete(String path) throws Exception;

    /**
     * 获得文件的内容
     *
     * @param path 相对路径
     * @return 文件的内容
     */
    byte[] getContent(String path) throws Exception;

    /**
     * 获得至多 {@code maxBytes + 1} 字节的文件内容，用于在不完整下载超大对象的前提下校验大小。
     *
     * @param path 相对路径
     * @param maxBytes 允许的最大字节数
     * @return 文件内容；返回长度大于 maxBytes 表示对象超限
     */
    default byte[] getContent(String path, long maxBytes) throws Exception {
        if (maxBytes < 0 || maxBytes >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("maxBytes must be between 0 and Integer.MAX_VALUE - 1");
        }
        byte[] content = getContent(path);
        if (content == null || content.length <= maxBytes) {
            return content;
        }
        int resultLength = (int) maxBytes + 1;
        return Arrays.copyOf(content, Math.min(content.length, resultLength));
    }

    // ========== 文件签名，目前仅 S3 支持 ==========

    /**
     * 获得文件预签名地址，用于上传
     *
     * @param path 相对路径
     * @param contentType 必须由上传请求使用的 MIME 类型
     * @param contentLength 必须由上传请求使用的内容长度
     * @return 文件预签名地址
     */
    default String presignPutUrl(String path, String contentType, long contentLength) {
        throw new UnsupportedOperationException("不支持的操作");
    }

    /**
     * 生成文件预签名地址，用于读取
     *
     * @param url 完整的文件访问地址
     * @param expirationSeconds 访问有效期，单位秒
     * @return 文件预签名地址
     */
    default String presignGetUrl(String url, Integer expirationSeconds) {
        throw new UnsupportedOperationException("不支持的操作");
    }

}
