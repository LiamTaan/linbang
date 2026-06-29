package cn.iocoder.yudao.module.linbang.controller.admin.match;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.PriorityPoolFreezeReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.PriorityPoolPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.PriorityPoolRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.prioritypoolrecord.PriorityPoolRecordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.prioritypoolrecord.PriorityPoolRecordMapper;
import cn.iocoder.yudao.module.linbang.service.match.PriorityPoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 邻里优先池")
@RestController
@RequestMapping("/linbang/priority-pool")
@Validated
public class PriorityPoolController {

    @Resource
    private PriorityPoolRecordMapper priorityPoolRecordMapper;
    @Resource
    private PriorityPoolService priorityPoolService;

    @GetMapping("/page")
    @Operation(summary = "分页获取优先池名单")
    @PreAuthorize("@ss.hasPermission('linbang:priority-pool:query')")
    public CommonResult<PageResult<PriorityPoolRespVO>> page(@Valid PriorityPoolPageReqVO reqVO) {
        PageResult<PriorityPoolRecordDO> pageResult = priorityPoolRecordMapper.selectPage(reqVO, reqVO.getMerchantId(), reqVO.getStatus());
        return success(new PageResult<>(pageResult.getList().stream().map(this::convert).collect(Collectors.toList()), pageResult.getTotal()));
    }

    @GetMapping("/get")
    @Operation(summary = "获取优先池详情")
    @PreAuthorize("@ss.hasPermission('linbang:priority-pool:query')")
    public CommonResult<PriorityPoolRespVO> get(@RequestParam("id") Long id) {
        PriorityPoolRecordDO record = priorityPoolRecordMapper.selectById(id);
        return success(record == null ? null : convert(record));
    }

    @PostMapping("/freeze")
    @Operation(summary = "冻结优先池资格")
    @PreAuthorize("@ss.hasPermission('linbang:priority-pool:update')")
    public CommonResult<Boolean> freeze(@Valid @RequestBody PriorityPoolFreezeReqVO reqVO) {
        priorityPoolService.freezeCurrent(reqVO.getMerchantId(), reqVO.getReasonRemark());
        return success(Boolean.TRUE);
    }

    @PostMapping("/unfreeze")
    @Operation(summary = "解冻并重算优先池资格")
    @PreAuthorize("@ss.hasPermission('linbang:priority-pool:update')")
    public CommonResult<Boolean> unfreeze(@Valid @RequestBody PriorityPoolFreezeReqVO reqVO) {
        priorityPoolService.unfreezeByRecompute(reqVO.getMerchantId());
        return success(Boolean.TRUE);
    }

    private PriorityPoolRespVO convert(PriorityPoolRecordDO record) {
        PriorityPoolRespVO respVO = new PriorityPoolRespVO();
        respVO.setId(record.getId());
        respVO.setMerchantId(record.getMerchantId());
        respVO.setUserId(record.getUserId());
        respVO.setStatus(record.getStatus());
        respVO.setReasonCode(record.getReasonCode());
        respVO.setReasonRemark(record.getReasonRemark());
        respVO.setCurrentFlag(record.getCurrentFlag());
        respVO.setEffectiveTime(record.getEffectiveTime());
        respVO.setExpireTime(record.getExpireTime());
        return respVO;
    }
}
