package cn.iocoder.yudao.module.linbang.controller.app.message;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordRespVO;
import cn.iocoder.yudao.module.linbang.service.app.message.AppMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 消息中心")
@RestController
@RequestMapping("/message/record")
@Validated
public class AppMessageController {

    @Resource
    private AppMessageService appMessageService;

    @GetMapping("/page")
    @Operation(summary = "获取消息记录分页")
    public CommonResult<PageResult<AppMessageRecordRespVO>> getMessageRecordPage(@Valid AppMessageRecordPageReqVO reqVO) {
        return success(BeanUtils.toBean(appMessageService.getMessageRecordPage(getLoginUserId(), reqVO),
                AppMessageRecordRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获取消息记录详情")
    @Parameter(name = "id", required = true)
    public CommonResult<AppMessageRecordDetailRespVO> getMessageRecord(@RequestParam("id") Long id) {
        return success(appMessageService.getMessageRecord(getLoginUserId(), id));
    }
}
