package cn.iocoder.yudao.module.linbang.service.merchantinfo;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Objects;

@Service
public class MerchantAccessStateService {

    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MerchantEntryMapper merchantEntryMapper;
    @Resource
    private MemberUserRealNameMapper memberUserRealNameMapper;
    @Resource
    private MemberUserQualificationMapper memberUserQualificationMapper;

    public boolean refreshMerchantAcceptStatus(Long userId) {
        if (userId == null) {
            return false;
        }
        MerchantInfoDO merchant = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, userId)
                .last("LIMIT 1"));
        if (merchant == null) {
            return false;
        }
        boolean enabled = canMerchantAccept(userId, merchant);
        if (!Objects.equals(merchant.getAcceptStatus(), enabled ? "ENABLE" : "DISABLE")) {
            merchantInfoMapper.updateById(MerchantInfoDO.builder()
                    .id(merchant.getId())
                    .acceptStatus(enabled ? "ENABLE" : "DISABLE")
                    .build());
        }
        return enabled;
    }

    public boolean canMerchantAccept(Long userId) {
        if (userId == null) {
            return false;
        }
        MerchantInfoDO merchant = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, userId)
                .last("LIMIT 1"));
        return merchant != null && canMerchantAccept(userId, merchant);
    }

    private boolean canMerchantAccept(Long userId, MerchantInfoDO merchant) {
        if (!Objects.equals(merchant.getStatus(), "ENABLE")) {
            return false;
        }
        MerchantEntryDO latestEntry = merchantEntryMapper.selectOne(new LambdaQueryWrapperX<MerchantEntryDO>()
                .eq(MerchantEntryDO::getUserId, userId)
                .orderByDesc(MerchantEntryDO::getCreateTime, MerchantEntryDO::getId)
                .last("LIMIT 1"));
        if (latestEntry == null
                || !Objects.equals(latestEntry.getFinalAuditStatus(), "APPROVED")
                || !Objects.equals(latestEntry.getProgressStatus(), "APPROVED_ENABLED")) {
            return false;
        }
        MemberUserRealNameDO realName = memberUserRealNameMapper.selectByUserId(userId);
        if (realName == null || !Objects.equals(realName.getAuditStatus(), "APPROVED")) {
            return false;
        }
        long totalQualificationCount = memberUserQualificationMapper.selectCount(new LambdaQueryWrapperX<MemberUserQualificationDO>()
                .eq(MemberUserQualificationDO::getUserId, userId));
        if (totalQualificationCount == 0) {
            return true;
        }
        long validApprovedCount = memberUserQualificationMapper.selectCount(new LambdaQueryWrapperX<MemberUserQualificationDO>()
                .eq(MemberUserQualificationDO::getUserId, userId)
                .eq(MemberUserQualificationDO::getAuditStatus, "APPROVED")
                .and(wrapper -> wrapper.isNull(MemberUserQualificationDO::getValidEndDate)
                        .or()
                        .ge(MemberUserQualificationDO::getValidEndDate, LocalDate.now())));
        return validApprovedCount > 0;
    }
}
