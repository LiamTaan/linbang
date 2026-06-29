package cn.iocoder.yudao.module.linbang.service.risk;

import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userrestrictrecord.UserRestrictRecordDO;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayOrderNotifyReqDTO;

import java.math.BigDecimal;

public interface LinbangRiskFacade {

    void validateBeforeCreateOrder(Long authUserId, MemberUserDO loginUser);

    void fillOrderDeposit(OrderInfoDO order);

    void validateBeforeAcceptOrder(Long authUserId, MemberUserDO loginUser, MerchantInfoDO merchant, OrderInfoDO order);

    void handleOrderCancelled(MemberUserDO loginUser, OrderInfoDO order, String cancelReason);

    UserRestrictRecordDO getActiveRestrict(Long userId, String restrictType);

    Long createRiskEvent(String bizType, Long bizId, String riskType, String riskLevel,
                         String hitRuleCode, String remark, String relatedUserIds);

    void applySelfDealPenalty(MemberUserDO user, MerchantInfoDO merchant, OrderInfoDO order);

    Long freezeUserFunds(Long userId, String sourceBizType, Long sourceBizId, BigDecimal amount, String reason);

    void releaseFrozenFunds(Long recordId, Long releasedBy, String releaseRemark);

    void addUserToBlacklist(Long userId, String blackType, String reason, java.time.LocalDateTime endTime);

    Long createRestrictRecord(Long userId, String restrictType, String sourceRuleCode, String sourceBizType,
                              Long sourceBizId, String reason, java.time.LocalDateTime endTime);

    void releaseRestrictRecordByBiz(String sourceBizType, Long sourceBizId, String restrictType,
                                    Long releasedBy, String releaseRemark);

    void syncPunishLogs();

    boolean markDepositPaid(PayOrderNotifyReqDTO notifyReqDTO);
}
