package cn.iocoder.yudao.module.linbang.controller.admin.match;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.MatchPushBatchPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.MatchPushBatchRespVO;
import cn.iocoder.yudao.module.linbang.service.matchpushbatch.MatchPushBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 邻里推送批次")
@RestController
@RequestMapping("/linbang/match/push-batch")
@Validated
public class MatchPushBatchController {

    @Resource
    private MatchPushBatchService matchPushBatchService;

    @GetMapping("/page")
    @Operation(summary = "分页获取推送批次")
    @PreAuthorize("@ss.hasPermission('linbang:match:push-batch:query')")
    public CommonResult<PageResult<MatchPushBatchRespVO>> page(@Valid MatchPushBatchPageReqVO reqVO) {
        return success(matchPushBatchService.getMatchPushBatchPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取推送批次详情")
    @PreAuthorize("@ss.hasPermission('linbang:match:push-batch:query')")
    public CommonResult<MatchPushBatchRespVO> get(@RequestParam("id") Long id) {
        return success(matchPushBatchService.getMatchPushBatch(id));
    }
}
