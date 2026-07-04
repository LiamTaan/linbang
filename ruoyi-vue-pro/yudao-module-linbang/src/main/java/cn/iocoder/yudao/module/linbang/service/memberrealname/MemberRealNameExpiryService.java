package cn.iocoder.yudao.module.linbang.service.memberrealname;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.service.merchantinfo.MerchantAccessStateService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberRealNameExpiryService {

    private static final String EXPIRE_REJECT_REASON = "身份证已到期，请更新证件信息后重新提交实名认证";

    @Resource
    private MemberUserRealNameMapper memberUserRealNameMapper;
    @Resource
    private MerchantAccessStateService merchantAccessStateService;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;

    @Scheduled(cron = "0 10 2 * * ?")
    public void scanExpiredRealNames() {
        List<MemberUserRealNameDO> records = memberUserRealNameMapper.selectList(new LambdaQueryWrapperX<MemberUserRealNameDO>()
                .eq(MemberUserRealNameDO::getAuditStatus, "APPROVED")
                .lt(MemberUserRealNameDO::getIdCardValidEnd, LocalDate.now())
                .orderByAsc(MemberUserRealNameDO::getId));
        if (records.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (MemberUserRealNameDO record : records) {
            memberUserRealNameMapper.updateById(MemberUserRealNameDO.builder()
                    .id(record.getId())
                    .auditStatus("REJECTED")
                    .auditRemark("系统自动驳回：身份证已到期")
                    .rejectReason(EXPIRE_REJECT_REASON)
                    .auditTime(now)
                    .build());
            merchantAccessStateService.refreshMerchantAcceptStatus(record.getUserId());
            messagePushDispatchService.dispatchSingleIdempotent("", "实名认证已失效，请更新证件后重新提交",
                    "REAL_NAME", record.getId(), record.getUserId(), EXPIRE_REJECT_REASON,
                    "real-name-expired:" + record.getId());
        }
    }
}
