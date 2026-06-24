package cn.iocoder.yudao.module.linbang.controller.app.pay;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppPayRefundCreateReqVO;
import cn.iocoder.yudao.module.linbang.service.app.pay.AppPayRefundService;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayRefundNotifyReqDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
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

    @PostMapping("/update-refunded")
    @Operation(summary = "更新邻里订单退款结果")
    @PermitAll
    public CommonResult<Boolean> updateRefunded(@Valid @RequestBody PayRefundNotifyReqDTO notifyReqDTO) {
        appPayRefundService.updateRefunded(notifyReqDTO);
        return success(Boolean.TRUE);
    }

}
