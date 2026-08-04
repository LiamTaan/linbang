package cn.iocoder.yudao.module.infra.service.file;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.http.HttpUtils;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FileCreateReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FilePageReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FilePresignedUrlRespVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.dal.mysql.file.FileMapper;
import cn.iocoder.yudao.module.infra.framework.file.core.client.FileClient;
import cn.iocoder.yudao.module.infra.framework.file.core.utils.FilePathUtils;
import cn.iocoder.yudao.module.infra.framework.file.core.utils.FileTypeUtils;
import com.google.common.annotations.VisibleForTesting;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static cn.hutool.core.date.DatePattern.PURE_DATE_PATTERN;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_IS_EMPTY;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_NOT_EXISTS;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_SIZE_EXCEED;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_TYPE_INVALID;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_UPLOAD_RESERVATION_INVALID;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_UPLOAD_SIZE_MISMATCH;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_UPLOAD_TOO_MANY_PENDING;

/**
 * 文件 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private static final String PENDING_UPLOAD_PREFIX = ".pending/";
    private static final long PRESIGNED_UPLOAD_TTL_MINUTES = 10L;
    private static final int MAX_ACTIVE_PRESIGNED_UPLOADS = 20;
    private static final int EXPIRED_UPLOAD_CLEANUP_BATCH_SIZE = 20;
    private static final int GLOBAL_EXPIRED_UPLOAD_CLEANUP_BATCH_SIZE = 100;
    private static final int GLOBAL_EXPIRED_UPLOAD_CLEANUP_MAX_BATCHES = 10;

    /**
     * 上传文件的前缀，是否包含日期（yyyyMMdd）
     *
     * 目的：按照日期，进行分目录
     */
    static boolean PATH_PREFIX_DATE_ENABLE = true;
    /**
     * 上传文件的后缀，是否启用
     *
     * 使用 128 位随机 UUID，兼顾唯一性并避免公开文件路径可预测。
     */
    static boolean PATH_SUFFIX_TIMESTAMP_ENABLE = true;
    /**
     * 后缀是否作为上级目录
     *
     * true：{@code yyyyMMdd/<后缀>/原文件名.ext}；保留原文件名
     * false：{@code yyyyMMdd/原文件名_<后缀>.ext}；后缀拼到文件名
     */
    static boolean PATH_SUFFIX_AS_DIRECTORY = true;

    @Resource
    private FileConfigService fileConfigService;

    @Resource
    private FileMapper fileMapper;

    private static final Set<String> BLOCKED_CONTENT_TYPES = new HashSet<>(Arrays.asList(
            "text/html", "application/xhtml+xml", "image/svg+xml", "text/javascript",
            "application/javascript", "application/x-javascript", "application/ecmascript",
            "text/ecmascript", "text/css", "text/vbscript", "application/x-sh",
            "application/x-httpd-php", "application/x-msdownload", "application/x-dosexec",
            "application/hta", "application/wasm", "application/xml", "text/xml"));
    private static final Set<String> BLOCKED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "html", "htm", "xhtml", "svg", "js", "mjs", "jsx", "ts", "tsx", "css",
            "php", "phtml", "jsp", "jspx", "asp", "aspx", "cgi", "sh", "bash", "bat",
            "cmd", "com", "exe", "dll", "msi", "jar", "war", "class", "ps1", "vbs"));

    @Override
    public PageResult<FileDO> getFilePage(FilePageReqVO pageReqVO) {
        return fileMapper.selectPage(pageReqVO);
    }

    @Override
    @SneakyThrows
    public String createFile(byte[] content, String name, String directory, String type) {
        return createFileInfo(content, name, directory, type).getUrl();
    }

    @Override
    @SneakyThrows
    public FileDO createFileInfo(byte[] content, String name, String directory, String type) {
        validateUploadContent(content);
        // 1.1 处理 name 的合法性，禁止携带目录路径
        name = FilePathUtils.validateFileName(name);
        validateFileExtension(name);

        // 1.2 处理 name 为空的情况
        if (StrUtil.isEmpty(name)) {
            name = DigestUtil.sha256Hex(content);
        }
        // Never trust a client supplied MIME type. Detect it from the bytes and file name.
        type = detectSafeContentType(content, name);
        if (StrUtil.isEmpty(FileUtil.extName(name))) {
            // 如果 name 没有后缀 type，则补充后缀
            String extension = FileTypeUtils.getExtension(type);
            if (StrUtil.isNotEmpty(extension)) {
                name = name + extension;
            }
        }

        // 2.1 生成上传的 path，需要保证唯一
        String path = generateUploadPath(name, directory, true);
        // 2.2 上传到文件存储器
        FileClient client = fileConfigService.getMasterFileClient();
        Assert.notNull(client, "客户端(master) 不能为空");
        String url = client.upload(content, path, type);

        // 3. 保存到数据库
        FileDO file = new FileDO().setConfigId(client.getId())
                .setName(name).setPath(path).setUrl(url)
                .setType(type).setSize((long) content.length);
        try {
            int inserted = fileMapper.insert(file);
            if (inserted != 1) {
                throw new IllegalStateException("File metadata insert did not affect exactly one row");
            }
        } catch (RuntimeException ex) {
            cleanupUploadedObject(client, path, ex);
            throw ex;
        }
        return file;
    }

    @VisibleForTesting
    String generateUploadPath(String name, String directory) {
        return generateUploadPath(name, directory, false);
    }

    private String generateUploadPath(String name, String directory, boolean forceUnique) {
        // 1.1 处理 name 和 directory 的合法性
        name = FilePathUtils.validateFileName(name);
        FilePathUtils.validatePath(name);
        FilePathUtils.validateDirectory(directory);
        // 1.2 生成前缀、后缀
        String prefix = null;
        if (PATH_PREFIX_DATE_ENABLE) {
            prefix = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), PURE_DATE_PATTERN);
        }
        String suffix = null;
        if (forceUnique || PATH_SUFFIX_TIMESTAMP_ENABLE) {
            suffix = UUID.randomUUID().toString().replace("-", "");
        }

        // 2.1 先拼接 suffix 后缀
        if (StrUtil.isNotEmpty(suffix)) {
            if (PATH_SUFFIX_AS_DIRECTORY) {
                name = suffix + StrUtil.SLASH + name;
            } else {
                String ext = FileUtil.extName(name);
                if (StrUtil.isNotEmpty(ext)) {
                    name = FileUtil.mainName(name) + StrUtil.C_UNDERLINE + suffix + StrUtil.DOT + ext;
                } else {
                    name = name + StrUtil.C_UNDERLINE + suffix;
                }
            }
        }
        // 2.2 再拼接 prefix 前缀
        if (StrUtil.isNotEmpty(prefix)) {
            name = prefix + StrUtil.SLASH + name;
        }
        // 2.3 最后拼接 directory 目录
        if (StrUtil.isNotEmpty(directory)) {
            name = directory + StrUtil.SLASH + name;
        }
        FilePathUtils.validatePath(name);
        return name;
    }

    @Override
    @SneakyThrows
    public FilePresignedUrlRespVO presignPutUrl(String name, long size, String directory) {
        validatePresignedUploadSize(size);
        String validatedName = FilePathUtils.validateFileName(name);
        validateFileExtension(validatedName);
        String path = generateUploadPath(validatedName, directory, true);
        String pendingPath = buildPendingUploadPath(path);

        FileClient fileClient = fileConfigService.getMasterFileClient();
        Assert.notNull(fileClient, "客户端(master) 不能为空");
        String ownerKey = getCurrentUploadOwnerKey();
        LocalDateTime activeAfter = LocalDateTime.now().minusMinutes(PRESIGNED_UPLOAD_TTL_MINUTES);
        cleanupExpiredPendingUploads(ownerKey, activeAfter);
        if (fileMapper.selectActivePendingUploadCount(ownerKey, activeAfter) >= MAX_ACTIVE_PRESIGNED_UPLOADS) {
            throw exception(FILE_UPLOAD_TOO_MANY_PENDING);
        }

        String uploadUrl = fileClient.presignPutUrl(pendingPath, PRESIGNED_UPLOAD_CONTENT_TYPE, size);
        String visitUrl = fileClient.presignGetUrl(path, null);
        // A negative size marks an upload reservation and also stores the exact expected byte length.
        FileDO reservation = new FileDO().setConfigId(fileClient.getId())
                .setName(validatedName).setPath(path).setUrl(visitUrl).setSize(-size);
        reservation.setUpdater(ownerKey);
        fileMapper.insert(reservation);
        return new FilePresignedUrlRespVO().setConfigId(fileClient.getId())
                .setPath(path).setUploadUrl(uploadUrl).setUploadContentType(PRESIGNED_UPLOAD_CONTENT_TYPE)
                .setUrl(visitUrl);
    }

    @Override
    public String presignGetUrl(String url, Integer expirationSeconds) {
        FileClient fileClient = fileConfigService.getMasterFileClient();
        return fileClient.presignGetUrl(url, expirationSeconds);
    }

    @Override
    @SneakyThrows
    public Long createFile(FileCreateReqVO createReqVO) {
        FilePathUtils.validatePath(createReqVO.getPath());
        FileDO reservation = fileMapper.selectLatestByConfigIdAndPath(
                createReqVO.getConfigId(), createReqVO.getPath());
        String ownerKey = getCurrentUploadOwnerKey();
        if (reservation == null || !ownerKey.equals(reservation.getUpdater()) || reservation.getSize() == null) {
            throw exception(FILE_UPLOAD_RESERVATION_INVALID);
        }
        FileClient client = fileConfigService.getFileClient(reservation.getConfigId());
        Assert.notNull(client, "客户端({}) 不能为空", reservation.getConfigId());
        String pendingPath = buildPendingUploadPath(reservation.getPath());
        if (reservation.getSize() > 0L) {
            deletePendingUploadBestEffort(client, pendingPath, reservation.getId());
            return reservation.getId();
        }
        if (reservation.getSize() >= 0L) {
            throw exception(FILE_UPLOAD_RESERVATION_INVALID);
        }

        byte[] content = client.getContent(pendingPath, MAX_FILE_SIZE_BYTES);
        String name;
        String type;
        try {
            validateUploadContent(content);
            long expectedSize = Math.negateExact(reservation.getSize());
            if (content.length != expectedSize) {
                throw exception(FILE_UPLOAD_SIZE_MISMATCH);
            }
            name = FilePathUtils.validateFileName(reservation.getName());
            validateFileExtension(name);
            type = detectSafeContentType(content, name);
        } catch (RuntimeException ex) {
            cleanupRejectedPresignedUpload(reservation, client, false, ex);
            throw ex;
        }

        String url;
        try {
            // Promote from the isolated pending key to the final key and rewrite all untrusted metadata.
            url = client.upload(content, reservation.getPath(), type);
        } catch (Exception ex) {
            cleanupRejectedPresignedUpload(reservation, client, true, ex);
            throw ex;
        }
        FileDO completedFile = new FileDO().setId(reservation.getId())
                .setName(name).setUrl(HttpUtils.removeUrlQuery(url))
                .setType(type).setSize((long) content.length);
        completedFile.setUpdater(ownerKey);
        try {
            int updated = fileMapper.updateById(completedFile);
            if (updated != 1) {
                throw new IllegalStateException("File reservation update did not affect exactly one row");
            }
        } catch (RuntimeException ex) {
            cleanupRejectedPresignedUpload(reservation, client, true, ex);
            throw ex;
        }
        deletePendingUploadBestEffort(client, pendingPath, reservation.getId());
        return reservation.getId();
    }

    @Override
    public FileDO getFile(Long id) {
        return validateFileExists(id);
    }

    @Override
    public void deleteFile(Long id) throws Exception {
        // 1.1 校验存在
        FileDO file = validateFileExists(id);
        // 1.2 校验路径合法性，避免误删文件存储器中的其他文件
        FilePathUtils.validatePath(file.getPath());

        // 2.1 从文件存储器中删除
        FileClient client = fileConfigService.getFileClient(file.getConfigId());
        Assert.notNull(client, "客户端({}) 不能为空", file.getConfigId());
        client.delete(resolveStoredPath(file));

        // 2.2 删除记录
        fileMapper.deleteById(id);
    }

    @Override
    @SneakyThrows
    public void deleteFileList(List<Long> ids) {
        List<FileDO> files = fileMapper.selectByIds(ids);
        Exception firstFailure = null;
        for (FileDO file : files) {
            try {
                deleteStoredFile(file);
            } catch (Exception ex) {
                if (firstFailure == null) {
                    firstFailure = ex;
                } else {
                    firstFailure.addSuppressed(ex);
                }
            }
        }
        if (firstFailure != null) {
            throw firstFailure;
        }
    }

    private FileDO validateFileExists(Long id) {
        FileDO fileDO = fileMapper.selectById(id);
        if (fileDO == null) {
            throw exception(FILE_NOT_EXISTS);
        }
        return fileDO;
    }

    @Override
    public byte[] getFileContent(Long configId, String path) throws Exception {
        // 1. 校验路径合法性
        FilePathUtils.validatePath(path);

        // 2.1 获取客户端
        FileClient client = fileConfigService.getFileClient(configId);
        Assert.notNull(client, "客户端({}) 不能为空", configId);
        // 2.2 获取文件内容
        return client.getContent(path);
    }

    @Override
    public byte[] getFileContent(Long configId, String path, long maxBytes) throws Exception {
        FilePathUtils.validatePath(path);
        if (maxBytes < 0 || maxBytes >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("maxBytes must be between 0 and Integer.MAX_VALUE - 1");
        }
        FileClient client = fileConfigService.getFileClient(configId);
        Assert.notNull(client, "客户端({}) 不能为空", configId);
        return client.getContent(path, maxBytes);
    }

    @Override
    public FileDO getFileByConfigIdAndPath(Long configId, String path) {
        return fileMapper.selectLatestByConfigIdAndPath(configId, path);
    }

    @Override
    public int cleanExpiredPendingUploads() {
        LocalDateTime expireBefore = LocalDateTime.now().minusMinutes(PRESIGNED_UPLOAD_TTL_MINUTES);
        Long afterId = null;
        int cleaned = 0;
        for (int batch = 0; batch < GLOBAL_EXPIRED_UPLOAD_CLEANUP_MAX_BATCHES; batch++) {
            List<FileDO> expiredUploads = fileMapper.selectExpiredPendingUploads(expireBefore, afterId,
                    GLOBAL_EXPIRED_UPLOAD_CLEANUP_BATCH_SIZE);
            if (expiredUploads.isEmpty()) {
                break;
            }
            for (FileDO expiredUpload : expiredUploads) {
                afterId = expiredUpload.getId();
                if (cleanupExpiredPendingUpload(expiredUpload)) {
                    cleaned++;
                }
            }
            if (expiredUploads.size() < GLOBAL_EXPIRED_UPLOAD_CLEANUP_BATCH_SIZE) {
                break;
            }
        }
        return cleaned;
    }

    private void validateUploadContent(byte[] content) {
        if (content == null || content.length == 0) {
            throw exception(FILE_IS_EMPTY);
        }
        if (content.length > FileService.MAX_FILE_SIZE_BYTES) {
            throw exception(FILE_SIZE_EXCEED);
        }
    }

    private void validatePresignedUploadSize(long size) {
        if (size <= 0L) {
            throw exception(FILE_IS_EMPTY);
        }
        if (size > MAX_FILE_SIZE_BYTES) {
            throw exception(FILE_SIZE_EXCEED);
        }
    }

    private String detectSafeContentType(byte[] content, String name) {
        String contentOnlyType = FileTypeUtils.getMineType(content);
        validateContentType(contentOnlyType);
        String resolvedType = FileTypeUtils.getMineType(content, name);
        validateContentType(resolvedType);
        return resolvedType;
    }

    private void validateContentType(String type) {
        if (StrUtil.isBlank(type)) {
            return;
        }
        String normalizedType = StrUtil.subBefore(type, ';', false).trim().toLowerCase(Locale.ROOT);
        if (BLOCKED_CONTENT_TYPES.contains(normalizedType) || normalizedType.endsWith("+xml")) {
            throw exception(FILE_TYPE_INVALID);
        }
    }

    private void validateFileExtension(String name) {
        String extension = FileUtil.extName(name);
        if (StrUtil.isNotBlank(extension)
                && BLOCKED_EXTENSIONS.contains(extension.toLowerCase(Locale.ROOT))) {
            throw exception(FILE_TYPE_INVALID);
        }
    }

    private String getCurrentUploadOwnerKey() {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null || loginUser.getId() == null || loginUser.getUserType() == null) {
            throw exception(FILE_UPLOAD_RESERVATION_INVALID);
        }
        return loginUser.getUserType() + ":" + loginUser.getId();
    }

    private String buildPendingUploadPath(String finalPath) {
        String pendingPath = PENDING_UPLOAD_PREFIX + finalPath;
        FilePathUtils.validatePath(pendingPath);
        return pendingPath;
    }

    private String resolveStoredPath(FileDO file) {
        return file.getSize() != null && file.getSize() < 0L
                ? buildPendingUploadPath(file.getPath()) : file.getPath();
    }

    private void cleanupExpiredPendingUploads(String ownerKey, LocalDateTime expireBefore) {
        List<FileDO> expiredUploads = fileMapper.selectExpiredPendingUploads(
                ownerKey, expireBefore, EXPIRED_UPLOAD_CLEANUP_BATCH_SIZE);
        for (FileDO expiredUpload : expiredUploads) {
            cleanupExpiredPendingUpload(expiredUpload);
        }
    }

    private boolean cleanupExpiredPendingUpload(FileDO expiredUpload) {
        FileClient client = fileConfigService.getFileClient(expiredUpload.getConfigId());
        if (client == null) {
            log.warn("[cleanupExpiredPendingUpload][fileId({}) configId({}) client missing]",
                    expiredUpload.getId(), expiredUpload.getConfigId());
            return false;
        }
        try {
            client.delete(buildPendingUploadPath(expiredUpload.getPath()));
            return fileMapper.deleteById(expiredUpload.getId()) > 0;
        } catch (Exception ex) {
            log.warn("[cleanupExpiredPendingUpload][fileId({}) cleanup failed]", expiredUpload.getId(), ex);
            return false;
        }
    }

    private void cleanupRejectedPresignedUpload(FileDO reservation, FileClient client,
                                                 boolean deleteFinalObject, Throwable original) {
        boolean objectCleanupSucceeded = true;
        try {
            client.delete(buildPendingUploadPath(reservation.getPath()));
        } catch (Exception cleanupException) {
            objectCleanupSucceeded = false;
            original.addSuppressed(cleanupException);
            log.error("[cleanupRejectedPresignedUpload][fileId({}) pending object cleanup failed]",
                    reservation.getId(), cleanupException);
        }
        if (deleteFinalObject) {
            try {
                client.delete(reservation.getPath());
            } catch (Exception cleanupException) {
                objectCleanupSucceeded = false;
                original.addSuppressed(cleanupException);
                log.error("[cleanupRejectedPresignedUpload][fileId({}) final object cleanup failed]",
                        reservation.getId(), cleanupException);
            }
        }
        if (objectCleanupSucceeded) {
            try {
                fileMapper.deleteById(reservation.getId());
            } catch (RuntimeException cleanupException) {
                original.addSuppressed(cleanupException);
                log.error("[cleanupRejectedPresignedUpload][fileId({}) metadata cleanup failed]",
                        reservation.getId(), cleanupException);
            }
        }
    }

    private void cleanupUploadedObject(FileClient client, String path, RuntimeException original) {
        try {
            client.delete(path);
        } catch (Exception cleanupException) {
            original.addSuppressed(cleanupException);
            log.error("[cleanupUploadedObject][path({}) cleanup failed]", path, cleanupException);
        }
    }

    private void deleteStoredFile(FileDO file) throws Exception {
        FilePathUtils.validatePath(file.getPath());
        FileClient client = fileConfigService.getFileClient(file.getConfigId());
        Assert.notNull(client, "客户端({}) 不能为空", file.getConfigId());
        client.delete(resolveStoredPath(file));
        fileMapper.deleteById(file.getId());
    }

    private void deletePendingUploadBestEffort(FileClient client, String pendingPath, Long fileId) {
        try {
            client.delete(pendingPath);
        } catch (Exception ex) {
            log.warn("[deletePendingUploadBestEffort][fileId({}) pending cleanup failed]", fileId, ex);
        }
    }

}
