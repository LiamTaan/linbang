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
import javax.annotation.security.PermitAll;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 文件存储")
@RestController
@RequestMapping("/infra/file")
@Validated
@Slf4j
public class AppFileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "模式一：表单直传。接口会完成上传并落库，直接返回 fileId 和 url。"
            + "首页发布需求等需要 attachmentFileIds 的场景，优先使用该接口即可。")
    @Parameter(name = "file", description = "文件附件", required = true,
            schema = @Schema(type = "string", format = "binary"))
    @PermitAll
    public CommonResult<AppFileUploadRespVO> uploadFile(@Valid AppFileUploadReqVO uploadReqVO) throws Exception {
        MultipartFile file = uploadReqVO.getFile();
        byte[] content = IoUtil.readBytes(file.getInputStream());
        FileDO fileDO = fileService.createFileInfo(content, file.getOriginalFilename(),
                uploadReqVO.getDirectory(), file.getContentType());
        AppFileUploadRespVO respVO = new AppFileUploadRespVO();
        respVO.setFileId(fileDO.getId());
        respVO.setUrl(fileDO.getUrl());
        return success(respVO);
    }

    @GetMapping("/presigned-url")
    @Operation(summary = "获取文件预签名地址（上传）", description = "模式二第 1 步：获取前端直传文件存储的预签名地址。"
            + "适用于七牛、阿里云 OSS 等对象存储。直传成功后，再调用 create 接口登记文件并获取 fileId。")
    @Parameters({
            @Parameter(name = "name", description = "文件名称", required = true),
            @Parameter(name = "directory", description = "文件目录")
    })
    public CommonResult<FilePresignedUrlRespVO> getFilePresignedUrl(
            @RequestParam("name") String name,
            @RequestParam(value = "directory", required = false) String directory) {
        return success(fileService.presignPutUrl(name, directory));
    }

    @PostMapping("/create")
    @Operation(summary = "创建文件", description = "模式二第 2 步：前端直传成功后登记文件，接口返回 fileId。"
            + "首页发布需求接口的 attachmentFileIds 就传这里返回的 fileId 列表。")
    @PermitAll
    public CommonResult<Long> createFile(@Valid @RequestBody AppFileCreateReqVO createReqVO) {
        FileCreateReqVO serviceReqVO = new FileCreateReqVO();
        serviceReqVO.setConfigId(createReqVO.getConfigId());
        serviceReqVO.setPath(createReqVO.getPath());
        serviceReqVO.setName(createReqVO.getName());
        serviceReqVO.setUrl(createReqVO.getUrl());
        serviceReqVO.setType(createReqVO.getType());
        serviceReqVO.setSize(createReqVO.getSize());
        return success(fileService.createFile(serviceReqVO));
    }

}
