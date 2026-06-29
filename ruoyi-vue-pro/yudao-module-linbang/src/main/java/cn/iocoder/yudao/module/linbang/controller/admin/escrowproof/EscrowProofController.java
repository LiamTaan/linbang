package cn.iocoder.yudao.module.linbang.controller.admin.escrowproof;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.escrowproof.vo.EscrowProofPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.escrowproof.vo.EscrowProofRespVO;
import cn.iocoder.yudao.module.linbang.service.escrowproof.EscrowProofService;
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

@Tag(name = "管理后台 - 托管凭证")
@RestController
@RequestMapping("/wallet/escrow-proof")
@Validated
public class EscrowProofController {

    @Resource
    private EscrowProofService escrowProofService;

    @GetMapping("/page")
    @Operation(summary = "获得托管凭证分页")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:escrow-proof:query')")
    public CommonResult<PageResult<EscrowProofRespVO>> getEscrowProofPage(@Valid EscrowProofPageReqVO reqVO) {
        return success(escrowProofService.getEscrowProofPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得托管凭证详情")
    @Parameter(name = "id", description = "托管凭证 ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:escrow-proof:query')")
    public CommonResult<EscrowProofRespVO> getEscrowProof(@RequestParam("id") Long id) {
        return success(escrowProofService.getEscrowProof(id));
    }
}
