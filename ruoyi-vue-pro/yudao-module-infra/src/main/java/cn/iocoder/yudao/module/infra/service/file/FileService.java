package cn.iocoder.yudao.module.infra.service.file;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FileCreateReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FilePageReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FilePresignedUrlRespVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import javax.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 文件 Service 接口
 *
 * @author 芋道源码
 */
public interface FileService {

    long MAX_FILE_SIZE_BYTES = 20L * 1024 * 1024;
    String PRESIGNED_UPLOAD_CONTENT_TYPE = "application/octet-stream";

    /**
     * 获得文件分页
     *
     * @param pageReqVO 分页查询
     * @return 文件分页
     */
    PageResult<FileDO> getFilePage(FilePageReqVO pageReqVO);

    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param content   文件内容
     * @param name      文件名称，允许空
     * @param directory 目录，允许空
     * @param type      文件的 MIME 类型，允许空
     * @return 文件路径
     */
    String createFile(@NotEmpty(message = "文件内容不能为空") byte[] content,
                      String name, String directory, String type);

    /**
     * 保存文件，并返回完整文件记录
     *
     * @param content   文件内容
     * @param name      文件名称，允许空
     * @param directory 目录，允许空
     * @param type      文件的 MIME 类型，允许空
     * @return 文件记录
     */
    FileDO createFileInfo(@NotEmpty(message = "文件内容不能为空") byte[] content,
                          String name, String directory, String type);

    /**
     * 生成文件预签名地址信息，用于上传
     *
     * @param name      文件名
     * @param size      文件大小，单位字节
     * @param directory 目录
     * @return 预签名地址信息
     */
    FilePresignedUrlRespVO presignPutUrl(@NotEmpty(message = "文件名不能为空") String name, long size,
                                         String directory);
    /**
     * 生成文件预签名地址信息，用于读取
     *
     * @param url 完整的文件访问地址
     * @param expirationSeconds 访问有效期，单位秒
     * @return 文件预签名地址
     */
    String presignGetUrl(String url, Integer expirationSeconds);

    /**
     * 创建文件
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createFile(FileCreateReqVO createReqVO);
    FileDO getFile(Long id);

    /**
     * 删除文件
     *
     * @param id 编号
     */
    void deleteFile(Long id) throws Exception;

    /**
     * 批量删除文件
     *
     * @param ids 编号列表
     */
    void deleteFileList(List<Long> ids) throws Exception;

    /**
     * 获得文件内容
     *
     * @param configId 配置编号
     * @param path     文件路径
     * @return 文件内容
     */
    byte[] getFileContent(Long configId, String path) throws Exception;

    /**
     * 受限大小读取文件，最多返回 maxBytes + 1 字节。
     *
     * @param configId 文件配置编号
     * @param path 文件路径
     * @param maxBytes 最大允许字节数
     * @return 文件内容；返回长度大于 maxBytes 表示文件超限
     */
    byte[] getFileContent(Long configId, String path, long maxBytes) throws Exception;

    /**
     * 获得文件
     *
     * @param configId 配置编号
     * @param path     文件路径
     * @return 文件
     */
    FileDO getFileByConfigIdAndPath(Long configId, String path);

    /**
     * 清理已过期且未完成的预签名上传。
     *
     * @return 已清理的文件记录数
     */
    int cleanExpiredPendingUploads();

}
