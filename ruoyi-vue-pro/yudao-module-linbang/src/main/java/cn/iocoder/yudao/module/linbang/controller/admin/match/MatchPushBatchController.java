package cn.iocoder.yudao.module.linbang.controller.admin.match;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.MatchPushBatchPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.MatchPushBatchRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.matchpushbatch.MatchPushBatchDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.matchpushbatch.MatchPushBatchMapper;
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
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 邻里推送批次")
@RestController
@RequestMapping("/linbang/match/push-batch")
@Validated
public class MatchPushBatchController {

    @Resource
    private MatchPushBatchMapper matchPushBatchMapper;

    @GetMapping("/page")
    @Operation(summary = "分页获取推送批次")
    @PreAuthorize("@ss.hasPermission('linbang:match:push-batch:query')")
    public CommonResult<PageResult<MatchPushBatchRespVO>> page(@Valid MatchPushBatchPageReqVO reqVO) {
        PageResult<MatchPushBatchDO> pageResult = matchPushBatchMapper.selectPage(reqVO, reqVO.getUnitId(), reqVO.getStatus());
        return success(new PageResult<>(pageResult.getList().stream().map(this::convert).collect(Collectors.toList()), pageResult.getTotal()));
    }

    @GetMapping("/get")
    @Operation(summary = "获取推送批次详情")
    @PreAuthorize("@ss.hasPermission('linbang:match:push-batch:query')")
    public CommonResult<MatchPushBatchRespVO> get(@RequestParam("id") Long id) {
        MatchPushBatchDO batch = matchPushBatchMapper.selectById(id);
        return success(batch == null ? null : convert(batch));
    }

    private MatchPushBatchRespVO convert(MatchPushBatchDO batch) {
        MatchPushBatchRespVO respVO = new MatchPushBatchRespVO();
        respVO.setId(batch.getId());
        respVO.setOrderId(batch.getOrderId());
        respVO.setUnitId(batch.getUnitId());
        respVO.setStageNo(batch.getStageNo());
        respVO.setPushBatchNo(batch.getPushBatchNo());
        respVO.setRadiusStartKm(batch.getRadiusStartKm());
        respVO.setRadiusEndKm(batch.getRadiusEndKm());
        respVO.setPlannedAt(batch.getPlannedAt());
        respVO.setExpiredAt(batch.getExpiredAt());
        respVO.setStatus(batch.getStatus());
        respVO.setTriggerType(batch.getTriggerType());
        return respVO;
    }
}
