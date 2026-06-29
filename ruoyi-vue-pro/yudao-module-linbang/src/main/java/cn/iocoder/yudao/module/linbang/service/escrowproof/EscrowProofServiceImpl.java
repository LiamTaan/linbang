package cn.iocoder.yudao.module.linbang.service.escrowproof;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.escrowproof.vo.EscrowProofPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.escrowproof.vo.EscrowProofRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.escrowproof.EscrowProofDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.escrowproof.EscrowProofMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.WALLET_FLOW_NOT_EXISTS;

@Service
@Validated
public class EscrowProofServiceImpl implements EscrowProofService {

    @Resource
    private EscrowProofMapper escrowProofMapper;

    @Override
    public EscrowProofDO createLockedProof(Long orderId, Long unitId, Long userId, Long merchantId,
                                           BigDecimal escrowAmount, String lockReason) {
        EscrowProofDO proof = EscrowProofDO.builder()
                .proofNo("LBEP" + IdUtil.getSnowflakeNextIdStr())
                .orderId(orderId)
                .unitId(unitId)
                .userId(userId)
                .merchantId(merchantId)
                .escrowAmount(escrowAmount)
                .proofStatus("LOCKED")
                .lockReason(lockReason)
                .build();
        escrowProofMapper.insert(proof);
        return proof;
    }

    @Override
    public void unlockProof(Long orderId, Long unitId, String unlockReason) {
        EscrowProofDO proof = escrowProofMapper.selectOne(new LambdaQueryWrapperX<EscrowProofDO>()
                .eq(EscrowProofDO::getOrderId, orderId)
                .eqIfPresent(EscrowProofDO::getUnitId, unitId)
                .eq(EscrowProofDO::getProofStatus, "LOCKED")
                .orderByDesc(EscrowProofDO::getId)
                .last("LIMIT 1"));
        if (proof == null) {
            return;
        }
        escrowProofMapper.updateById(EscrowProofDO.builder()
                .id(proof.getId())
                .proofStatus("UNLOCKED")
                .unlockReason(unlockReason)
                .build());
    }

    @Override
    public void refundProof(Long orderId, Long unitId, String unlockReason) {
        EscrowProofDO proof = escrowProofMapper.selectOne(new LambdaQueryWrapperX<EscrowProofDO>()
                .eq(EscrowProofDO::getOrderId, orderId)
                .eqIfPresent(EscrowProofDO::getUnitId, unitId)
                .eq(EscrowProofDO::getProofStatus, "LOCKED")
                .orderByDesc(EscrowProofDO::getId)
                .last("LIMIT 1"));
        if (proof == null) {
            return;
        }
        escrowProofMapper.updateById(EscrowProofDO.builder()
                .id(proof.getId())
                .proofStatus("REFUNDED")
                .unlockReason(unlockReason)
                .build());
    }

    @Override
    public PageResult<EscrowProofRespVO> getEscrowProofPage(EscrowProofPageReqVO reqVO) {
        PageResult<EscrowProofDO> pageResult = escrowProofMapper.selectPage(reqVO);
        return new PageResult<>(BeanUtils.toBean(pageResult.getList(), EscrowProofRespVO.class), pageResult.getTotal());
    }

    @Override
    public EscrowProofRespVO getEscrowProof(Long id) {
        EscrowProofDO proof = escrowProofMapper.selectById(id);
        if (proof == null) {
            throw exception(WALLET_FLOW_NOT_EXISTS);
        }
        return BeanUtils.toBean(proof, EscrowProofRespVO.class);
    }
}
