package cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate.vo.MessageTemplateDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate.vo.MessageTemplatePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate.vo.MessageTemplateRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate.vo.MessageTemplateSaveReqVO;
import cn.iocoder.yudao.module.linbang.service.messagetemplate.MessageTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 消息模板")
@RestController
@RequestMapping("/message/template")
@Validated
public class MessageTemplateController {

    @Resource
    private MessageTemplateService messageTemplateService;

    @GetMapping("/page")
    @Operation(summary = "获得消息模板分页")
    @PreAuthorize("@ss.hasPermission('linbang:message:template:query')")
    public CommonResult<PageResult<MessageTemplateRespVO>> getMessageTemplatePage(@Valid MessageTemplatePageReqVO reqVO) {
        return success(BeanUtils.toBean(messageTemplateService.getMessageTemplatePage(reqVO), MessageTemplateRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得消息模板")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:message:template:query')")
    public CommonResult<MessageTemplateDetailRespVO> getMessageTemplate(@RequestParam("id") Long id) {
        return success(messageTemplateService.getMessageTemplateDetail(id));
    }

    @PostMapping("/create")
    @Operation(summary = "创建消息模板")
    @PreAuthorize("@ss.hasPermission('linbang:message:template:create')")
    public CommonResult<Long> createMessageTemplate(@Valid @RequestBody MessageTemplateSaveReqVO reqVO) {
        return success(messageTemplateService.createMessageTemplate(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新消息模板")
    @PreAuthorize("@ss.hasPermission('linbang:message:template:update')")
    public CommonResult<Boolean> updateMessageTemplate(@Valid @RequestBody MessageTemplateSaveReqVO reqVO) {
        messageTemplateService.updateMessageTemplate(reqVO);
        return success(true);
    }
}
