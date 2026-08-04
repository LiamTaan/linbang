package cn.iocoder.yudao.module.linbang.service.memberrealname;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.service.merchantinfo.MerchantAccessStateService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberRealNameExpiryService {

    private static final int SCAN_BATCH_SIZE = 500;

    private static final String EXPIRE_REJECT_REASON = "身份证已到期，请更新证件信息后重新提交实名认证";

    @Resource
    private MemberUserRealNameMapper memberUserRealNameMapper;
    @Resource
    private MerchantAccessStateService merchantAccessStateService;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;

    @Scheduled(cron = "0 10 2 * * ?")
    public void scanExpiredRealNames() {
        LocalDate today = LocalDate.now();
        while (true) {
            List<MemberUserRealNameDO> records = memberUserRealNameMapper.selectList(
                    new LambdaQueryWrapperX<MemberUserRealNameDO>()
                            .eq(MemberUserRealNameDO::getAuditStatus, "APPROVED")
                            .lt(MemberUserRealNameDO::getIdCardValidEnd, today)
                            .orderByAsc(MemberUserRealNameDO::getId)
                            .last("LIMIT " + SCAN_BATCH_SIZE));
            if (records.isEmpty()) {
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            for (MemberUserRealNameDO record : records) {
                int updated = memberUserRealNameMapper.update(null,
                        new LambdaUpdateWrapper<MemberUserRealNameDO>()
                                .eq(MemberUserRealNameDO::getId, record.getId())
                                .eq(MemberUserRealNameDO::getAuditStatus, "APPROVED")
                                .lt(MemberUserRealNameDO::getIdCardValidEnd, today)
                                .set(MemberUserRealNameDO::getAuditStatus, "REJECTED")
                                .set(MemberUserRealNameDO::getAuditRemark, "系统自动驳回：身份证已到期")
                                .set(MemberUserRealNameDO::getRejectReason, EXPIRE_REJECT_REASON)
                                .set(MemberUserRealNameDO::getAuditTime, now));
                if (updated == 0) {
                    continue;
                }
                merchantAccessStateService.refreshMerchantAcceptStatus(record.getUserId());
                messagePushDispatchService.dispatchSingleIdempotent("", "实名认证已失效，请更新证件后重新提交",
                        "REAL_NAME", record.getId(), record.getUserId(), EXPIRE_REJECT_REASON,
                        "real-name-expired:" + record.getId());
            }
        }
    }
}
