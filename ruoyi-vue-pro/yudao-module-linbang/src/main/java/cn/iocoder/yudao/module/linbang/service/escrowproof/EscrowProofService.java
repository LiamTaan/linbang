package cn.iocoder.yudao.module.linbang.service.escrowproof;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.escrowproof.vo.EscrowProofPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.escrowproof.vo.EscrowProofRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.escrowproof.EscrowProofDO;

import java.math.BigDecimal;

public interface EscrowProofService {

    EscrowProofDO createLockedProof(Long orderId, Long unitId, Long userId, Long merchantId,
                                    BigDecimal escrowAmount, String lockReason);

    void unlockProof(Long orderId, Long unitId, String unlockReason);

    void refundProof(Long orderId, Long unitId, String unlockReason);

    PageResult<EscrowProofRespVO> getEscrowProofPage(EscrowProofPageReqVO reqVO);

    EscrowProofRespVO getEscrowProof(Long id);
}
