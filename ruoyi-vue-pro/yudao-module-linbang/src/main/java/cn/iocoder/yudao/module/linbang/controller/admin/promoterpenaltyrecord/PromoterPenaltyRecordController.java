package cn.iocoder.yudao.module.linbang.controller.admin.promoterpenaltyrecord;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.promoterpenaltyrecord.vo.PromoterPenaltyRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoterpenaltyrecord.vo.PromoterPenaltyRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoterpenaltyrecord.vo.PromoterPenaltyRecordRespVO;
import cn.iocoder.yudao.module.linbang.service.promoterpenaltyrecord.PromoterPenaltyRecordService;
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

@Tag(name = "管理后台 - 推广员处罚记录")
@RestController
@RequestMapping("/promote/penalty")
@Validated
public class PromoterPenaltyRecordController {

    @Resource
    private PromoterPenaltyRecordService promoterPenaltyRecordService;

    @GetMapping("/page")
    @Operation(summary = "获取推广员处罚记录分页")
    @PreAuthorize("@ss.hasPermission('linbang:promote:penalty:query')")
    public CommonResult<PageResult<PromoterPenaltyRecordRespVO>> getPage(@Valid PromoterPenaltyRecordPageReqVO reqVO) {
        return success(promoterPenaltyRecordService.getAdminPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取推广员处罚记录详情")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:promote:penalty:query')")
    public CommonResult<PromoterPenaltyRecordDetailRespVO> get(@RequestParam("id") Long id) {
        return success(promoterPenaltyRecordService.getDetail(id));
    }
}
