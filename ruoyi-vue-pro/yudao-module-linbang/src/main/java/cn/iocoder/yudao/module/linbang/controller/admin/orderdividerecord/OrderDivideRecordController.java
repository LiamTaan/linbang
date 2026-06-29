package cn.iocoder.yudao.module.linbang.controller.admin.orderdividerecord;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.orderdividerecord.vo.OrderDivideRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.orderdividerecord.vo.OrderDivideRecordRespVO;
import cn.iocoder.yudao.module.linbang.service.orderdividerecord.OrderDivideRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 分账明细")
@RestController
@RequestMapping("/wallet/divide-record")
@Validated
public class OrderDivideRecordController {

    @Resource
    private OrderDivideRecordService orderDivideRecordService;

    @GetMapping("/page")
    @Operation(summary = "获得分账明细分页")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:divide-record:query')")
    public CommonResult<PageResult<OrderDivideRecordRespVO>> getOrderDivideRecordPage(@Valid OrderDivideRecordPageReqVO reqVO) {
        return success(orderDivideRecordService.getOrderDivideRecordPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得分账明细详情")
    @Parameter(name = "id", description = "分账明细 ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:divide-record:query')")
    public CommonResult<OrderDivideRecordRespVO> getOrderDivideRecord(@RequestParam("id") Long id) {
        return success(orderDivideRecordService.getOrderDivideRecord(id));
    }

}
