package cn.iocoder.yudao.module.infra.controller.app.file;

import cn.hutool.core.io.IoUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FileCreateReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FilePresignedUrlRespVO;
import cn.iocoder.yudao.module.infra.controller.app.file.vo.AppFileCreateReqVO;
import cn.iocoder.yudao.module.infra.controller.app.file.vo.AppFileUploadReqVO;
import cn.iocoder.yudao.module.infra.controller.app.file.vo.AppFileUploadRespVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_IS_EMPTY;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_SIZE_EXCEED;

@Tag(name = "用户 App - 文件存储")
@RestController
@RequestMapping("/infra/file")
@Validated
@Slf4j
public class AppFileController {

    private static final long MAX_FILE_SIZE_BYTES = FileService.MAX_FILE_SIZE_BYTES;

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "模式一：表单直传。接口会完成上传并落库，直接返回 fileId 和 url；单文件最大 20 MiB，服务端会按实际文件内容识别 MIME 类型并拒绝可执行或网页脚本文件。"
            + "首页发布需求等需要 attachmentFileIds 的场景，优先使用该接口即可。")
    @Parameter(name = "file", description = "文件附件", required = true,
            schema = @Schema(type = "string", format = "binary"))
    public CommonResult<AppFileUploadRespVO> uploadFile(@Valid AppFileUploadReqVO uploadReqVO) throws Exception {
        MultipartFile file = uploadReqVO.getFile();
        validateFileSize(file);
        byte[] content = IoUtil.readBytes(file.getInputStream());
        FileDO fileDO = fileService.createFileInfo(content, file.getOriginalFilename(),
                uploadReqVO.getDirectory(), file.getContentType());
        AppFileUploadRespVO respVO = new AppFileUploadRespVO();
        respVO.setFileId(fileDO.getId());
        respVO.setUrl(fileDO.getUrl());
        return success(respVO);
    }

    @GetMapping("/presigned-url")
    @Operation(summary = "获取文件预签名地址（上传）", description = "模式二第 1 步：获取前端直传对象存储的预签名地址。"
            + "上传时必须使用响应中的 uploadContentType，且请求体字节数必须与 size 一致；直传成功后，再调用 create 接口完成安全校验并获取 fileId。")
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
    @Operation(summary = "创建文件", description = "模式二第 2 步：前端直传成功后完成文件。接口只接受当前登录用户自己的上传预约，"
            + "会按真实字节校验大小和 MIME 类型，将文件从隔离暂存路径提升到最终路径并重写不可信元数据，然后返回 fileId。"
            + "首页发布需求接口的 attachmentFileIds 就传这里返回的 fileId 列表。")
    public CommonResult<Long> createFile(@Valid @RequestBody AppFileCreateReqVO createReqVO) throws Exception {
        FileCreateReqVO serviceReqVO = new FileCreateReqVO();
        serviceReqVO.setConfigId(createReqVO.getConfigId());
        serviceReqVO.setPath(createReqVO.getPath());
        return success(fileService.createFile(serviceReqVO));
    }

    private void validateFileSize(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw exception(FILE_IS_EMPTY);
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw exception(FILE_SIZE_EXCEED);
        }
    }

}
