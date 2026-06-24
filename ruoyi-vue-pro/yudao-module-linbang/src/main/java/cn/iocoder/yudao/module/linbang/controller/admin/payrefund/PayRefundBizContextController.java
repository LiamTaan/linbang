package cn.iocoder.yudao.module.linbang.controller.admin.payrefund;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.admin.payrefund.vo.PayRefundBizContextRespVO;
import cn.iocoder.yudao.module.linbang.service.payrefund.PayRefundBizContextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 退款业务上下文")
@RestController
@RequestMapping("/pay/refund-context")
@Validated
public class PayRefundBizContextController {

    @Resource
    private PayRefundBizContextService payRefundBizContextService;

    @GetMapping("/get")
    @Operation(summary = "获得退款业务上下文")
    @Parameter(name = "payRefundId", description = "支付退款单 ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasAnyPermissions('pay:refund:query', 'linbang:pay:refund:audit')")
    public CommonResult<PayRefundBizContextRespVO> getPayRefundBizContext(@RequestParam("payRefundId") Long payRefundId) {
        return success(payRefundBizContextService.getPayRefundBizContext(payRefundId));
    }

}
