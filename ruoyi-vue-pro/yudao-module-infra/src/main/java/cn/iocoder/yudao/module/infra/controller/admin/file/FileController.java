package cn.iocoder.yudao.module.infra.controller.admin.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.http.HttpUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.*;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_IS_EMPTY;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_SIZE_EXCEED;
import static cn.iocoder.yudao.module.infra.framework.file.core.utils.FileTypeUtils.getMineType;
import static cn.iocoder.yudao.module.infra.framework.file.core.utils.FileTypeUtils.isImage;

@Tag(name = "管理后台 - 文件存储")
@RestController
@RequestMapping("/infra/file")
@Validated
@Slf4j
public class FileController {

    private static final long MAX_FILE_SIZE_BYTES = FileService.MAX_FILE_SIZE_BYTES;

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "模式一：后端上传文件")
    @Parameter(name = "file", description = "文件附件", required = true,
            schema = @Schema(type = "string", format = "binary"))
    public CommonResult<String> uploadFile(@Valid FileUploadReqVO uploadReqVO) throws Exception {
        MultipartFile file = uploadReqVO.getFile();
        validateFileSize(file);
        byte[] content = IoUtil.readBytes(file.getInputStream());
        return success(fileService.createFile(content, file.getOriginalFilename(),
                uploadReqVO.getDirectory(), file.getContentType()));
    }

    @GetMapping("/presigned-url")
    @Operation(summary = "获取文件预签名地址（上传）", description = "模式二第 1 步：前端直传对象存储。"
            + "上传请求必须使用响应中的 uploadContentType，且请求体字节数必须与 size 一致。")
    @Parameters({
            @Parameter(name = "name", description = "文件名称", required = true),
            @Parameter(name = "size", description = "文件大小，单位字节；范围 1 至 20 MiB", required = true),
            @Parameter(name = "directory", description = "文件目录")
    })
    public CommonResult<FilePresignedUrlRespVO> getFilePresignedUrl(
            @RequestParam("name") String name,
            @RequestParam("size") Long size,
            @RequestParam(value = "directory", required = false) String directory) {
        return success(fileService.presignPutUrl(name, size, directory));
    }

    @PostMapping("/create")
    @Operation(summary = "创建文件", description = "模式二第 2 步：完成当前用户自己的上传预约。"
            + "服务端校验真实大小和 MIME 类型，将文件从隔离暂存路径提升到最终路径并重写不可信元数据。")
    public CommonResult<Long> createFile(@Valid @RequestBody FileCreateReqVO createReqVO) {
        return success(fileService.createFile(createReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得文件")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:file:query')")
    public CommonResult<FileRespVO> getFile(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(fileService.getFile(id), FileRespVO.class));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除文件")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:file:delete')")
    public CommonResult<Boolean> deleteFile(@RequestParam("id") Long id) throws Exception {
        fileService.deleteFile(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除文件")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('infra:file:delete')")
    public CommonResult<Boolean> deleteFileList(@RequestParam("ids") List<Long> ids) throws Exception {
        fileService.deleteFileList(ids);
        return success(true);
    }

    @GetMapping("/{configId}/get/**")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "下载文件")
    @Parameter(name = "configId", description = "配置编号", required = true)
    public ResponseEntity<byte[]> getFileContent(HttpServletRequest request,
                                                 @PathVariable("configId") Long configId) throws Exception {
        // 获取请求的路径
        String path = StrUtil.subAfter(request.getRequestURI(), "/get/", false);
        if (StrUtil.isEmpty(path)) {
            throw new IllegalArgumentException("结尾的 path 路径必须传递");
        }
        // 解码，解决中文、%、+ 等特殊字符路径的问题
        // https://gitee.com/zhijiantianya/ruoyi-vue-pro/pulls/807/
        // https://gitee.com/zhijiantianya/ruoyi-vue-pro/pulls/1432/
        path = HttpUtils.decodeUrlPath(path);

        FileDO file = getReadableFileMetadata(configId, path);
        if (file == null) {
            log.warn("[getFileContent][configId({}) path({}) 文件不存在]", configId, path);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        byte[] content = getValidatedFileContent(file);
        if (content == null) {
            log.warn("[getFileContent][configId({}) path({}) 文件内容不存在]", configId, path);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        String filename = StrUtil.isNotEmpty(file.getName()) ? file.getName() : FileUtil.getName(path);
        String mineType = StrUtil.isNotBlank(file.getType()) ? file.getType() : getMineType(content, filename);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(parseMediaType(mineType));
        addSecurityHeaders(headers);
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                buildContentDisposition(isSafeInlineType(mineType) ? "inline" : "attachment", filename));
        if (StrUtil.containsIgnoreCase(mineType, "video")) {
            headers.set("Accept-Ranges", "bytes");
            headers.setContentLength(content.length);
        }
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    @GetMapping("/{configId}/preview/**")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "预览文件")
    @Parameter(name = "configId", description = "配置编号", required = true)
    public ResponseEntity<byte[]> previewFileContent(HttpServletRequest request,
                                                     @PathVariable("configId") Long configId) throws Exception {
        String path = StrUtil.subAfter(request.getRequestURI(), "/preview/", false);
        if (StrUtil.isEmpty(path)) {
            throw new IllegalArgumentException("结尾的 path 路径必须传递");
        }
        path = HttpUtils.decodeUrlPath(path);
        FileDO file = getReadableFileMetadata(configId, path);
        if (file == null) {
            log.warn("[previewFileContent][configId({}) path({}) 文件不存在]", configId, path);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        byte[] content = getValidatedFileContent(file);
        if (content == null) {
            log.warn("[previewFileContent][configId({}) path({}) 文件内容不存在]", configId, path);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        String filename = StrUtil.isNotEmpty(file.getName()) ? file.getName() : FileUtil.getName(path);
        String mineType = StrUtil.isNotBlank(file.getType()) ? file.getType() : getMineType(content, filename);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(parseMediaType(mineType));
        addSecurityHeaders(headers);
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                buildContentDisposition(isSafeInlineType(mineType) ? "inline" : "attachment", filename));
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    private FileDO getReadableFileMetadata(Long configId, String path) {
        FileDO file = fileService.getFileByConfigIdAndPath(configId, path);
        if (file == null || file.getSize() == null || file.getSize() < 0L) {
            return null;
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw exception(FILE_SIZE_EXCEED);
        }
        return file;
    }

    private byte[] getValidatedFileContent(FileDO file) throws Exception {
        byte[] content = fileService.getFileContent(file.getConfigId(), file.getPath(), MAX_FILE_SIZE_BYTES);
        if (content != null && content.length > MAX_FILE_SIZE_BYTES) {
            throw exception(FILE_SIZE_EXCEED);
        }
        return content;
    }

    private static void addSecurityHeaders(HttpHeaders headers) {
        headers.set("X-Content-Type-Options", "nosniff");
        headers.set("Content-Security-Policy", "sandbox; default-src 'none'");
    }

    private static boolean isSafeInlineType(String mineType) {
        if (StrUtil.isBlank(mineType)) {
            return false;
        }
        String normalizedType = StrUtil.subBefore(mineType, ';', false).trim().toLowerCase(Locale.ROOT);
        return (isImage(normalizedType) && !"image/svg+xml".equals(normalizedType))
                || StrUtil.startWith(normalizedType, "audio/")
                || StrUtil.startWith(normalizedType, "video/")
                || MediaType.APPLICATION_PDF_VALUE.equals(normalizedType);
    }

    private static void validateFileSize(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw exception(FILE_IS_EMPTY);
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw exception(FILE_SIZE_EXCEED);
        }
    }

    private static MediaType parseMediaType(String mineType) {
        if (StrUtil.isBlank(mineType)) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        try {
            return MediaType.parseMediaType(mineType);
        } catch (Exception ex) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private static String buildContentDisposition(String dispositionType, String filename) {
        String safeFilename = StrUtil.blankToDefault(filename, "download");
        try {
            return StrUtil.format("{}; filename*=UTF-8''{}",
                    dispositionType, java.net.URLEncoder.encode(safeFilename, "UTF-8").replace("+", "%20"));
        } catch (UnsupportedEncodingException ex) {
            return StrUtil.format("{}; filename=\"{}\"", dispositionType, safeFilename);
        }
    }

    @GetMapping("/page")
    @Operation(summary = "获得文件分页")
    @PreAuthorize("@ss.hasPermission('infra:file:query')")
    public CommonResult<PageResult<FileRespVO>> getFilePage(@Valid FilePageReqVO pageVO) {
        PageResult<FileDO> pageResult = fileService.getFilePage(pageVO);
        return success(BeanUtils.toBean(pageResult, FileRespVO.class));
    }

}
