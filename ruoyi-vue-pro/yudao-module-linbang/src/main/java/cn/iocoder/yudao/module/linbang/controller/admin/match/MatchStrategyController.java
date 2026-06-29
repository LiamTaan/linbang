package cn.iocoder.yudao.module.linbang.controller.admin.match;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.MatchStrategyRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.MatchStrategyUpdateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.matchstrategy.MatchStrategyDO;
import cn.iocoder.yudao.module.linbang.service.match.MatchStrategyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 邻里匹配策略")
@RestController
@RequestMapping("/linbang/match/strategy")
@Validated
public class MatchStrategyController {

    @Resource
    private MatchStrategyService matchStrategyService;

    @GetMapping("/get")
    @Operation(summary = "获取当前生效匹配策略")
    @PreAuthorize("@ss.hasPermission('linbang:match:strategy:query')")
    public CommonResult<MatchStrategyRespVO> get() {
        MatchStrategyDO strategy = matchStrategyService.getEnabledStrategy();
        if (strategy == null) {
            return success(null);
        }
        MatchStrategyRespVO respVO = new MatchStrategyRespVO();
        respVO.setId(strategy.getId());
        respVO.setStrategyCode(strategy.getStrategyCode());
        respVO.setStrategyName(strategy.getStrategyName());
        respVO.setStageConfigJson(strategy.getStageConfigJson());
        respVO.setMaxStageCount(strategy.getMaxStageCount());
        respVO.setMaxRadiusKm(strategy.getMaxRadiusKm());
        respVO.setFlowAdviceTemplate(strategy.getFlowAdviceTemplate());
        respVO.setAutoRefundEnabled(strategy.getAutoRefundEnabled());
        respVO.setAutoRefundRetryTimes(strategy.getAutoRefundRetryTimes());
        respVO.setStatus(strategy.getStatus());
        return success(respVO);
    }

    @PutMapping("/update")
    @Operation(summary = "更新匹配策略")
    @PreAuthorize("@ss.hasPermission('linbang:match:strategy:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody MatchStrategyUpdateReqVO reqVO) {
        matchStrategyService.updateEnabledStrategy(MatchStrategyDO.builder()
                .strategyCode(reqVO.getStrategyCode())
                .strategyName(reqVO.getStrategyName())
                .stageConfigJson(reqVO.getStageConfigJson())
                .maxStageCount(reqVO.getMaxStageCount())
                .maxRadiusKm(reqVO.getMaxRadiusKm())
                .flowAdviceTemplate(reqVO.getFlowAdviceTemplate())
                .autoRefundEnabled(reqVO.getAutoRefundEnabled())
                .autoRefundRetryTimes(reqVO.getAutoRefundRetryTimes())
                .build());
        return success(Boolean.TRUE);
    }
}
