package cn.iocoder.yudao.module.linbang.controller.app.pay;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppPayRefundCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppPayRefundPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppPayRefundRespVO;
import cn.iocoder.yudao.module.linbang.service.app.pay.AppPayRefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 邻里退款")
@RestController
@RequestMapping("/pay/refund")
@Validated
public class AppPayRefundController {

    @Resource
    private AppPayRefundService appPayRefundService;

    @PostMapping("/create")
    @Operation(summary = "创建退款申请")
    public CommonResult<Long> createRefund(@Valid @RequestBody AppPayRefundCreateReqVO reqVO) {
        return success(appPayRefundService.createRefund(getLoginUserId(), reqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "获取退款记录分页")
    public CommonResult<PageResult<AppPayRefundRespVO>> getRefundPage(@Valid AppPayRefundPageReqVO reqVO) {
        return success(appPayRefundService.getRefundPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取退款记录详情")
    public CommonResult<AppPayRefundRespVO> getRefund(@RequestParam("id") Long id) {
        return success(appPayRefundService.getRefund(getLoginUserId(), id));
    }
}
