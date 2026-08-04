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
    @Operation(summary = "创建退款申请",
            description = "仅允许对原支付单发起退款申请；退款需要后台审核，审核通过后由支付模块按原支付单渠道发起原路退款，用户不能选择退款渠道。支持整单退款和单元维度退款，退款成功回调后冲减托管金额并同步订单/单元状态。")
    public CommonResult<Long> createRefund(@Valid @RequestBody AppPayRefundCreateReqVO reqVO) {
        return success(appPayRefundService.createRefund(getLoginUserId(), reqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "获取退款记录分页",
            description = "查询当前用户退款申请。退款状态来自支付模块，审核状态表示平台审核进度；退款始终原路退回，不返回也不接收退款渠道选择。")
    public CommonResult<PageResult<AppPayRefundRespVO>> getRefundPage(@Valid AppPayRefundPageReqVO reqVO) {
        return success(appPayRefundService.getRefundPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取退款记录详情",
            description = "查看单笔退款申请详情。退款成功表示支付通道已确认原路退款成功；退款失败时 channelErrorMsg 返回不包含渠道内部信息的稳定业务说明。")
    public CommonResult<AppPayRefundRespVO> getRefund(@RequestParam("id") Long id) {
        return success(appPayRefundService.getRefund(getLoginUserId(), id));
    }
}
