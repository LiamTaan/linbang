package cn.iocoder.yudao.module.linbang.service.certexemption;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.certexemption.vo.CertExemptionAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.certexemption.vo.CertExemptionDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.certexemption.vo.CertExemptionPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.certexemption.vo.CertExemptionRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.certexemption.CertExemptionApplyDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.certexemption.CertExemptionApplyMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.CERT_EXEMPTION_APPLY_NOT_EXISTS;

@Service
@Validated
public class CertExemptionServiceImpl implements CertExemptionService {

    @Resource
    private CertExemptionApplyMapper certExemptionApplyMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MemberUserQualificationMapper memberUserQualificationMapper;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;

    @Override
    public PageResult<CertExemptionRespVO> getPage(CertExemptionPageReqVO reqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(reqVO.getUserKeyword());
        if (StrUtil.isNotBlank(reqVO.getUserKeyword()) && matchedUserIds.isEmpty()) {
            return PageResult.empty();
        }
        PageResult<CertExemptionApplyDO> pageResult = certExemptionApplyMapper.selectPage(reqVO, matchedUserIds);
        List<CertExemptionRespVO> list = BeanUtils.toBean(pageResult.getList(), CertExemptionRespVO.class);
        fillDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public CertExemptionDetailRespVO getDetail(Long id) {
        CertExemptionApplyDO apply = getRequiredApply(id);
        MemberUserDO user = apply.getUserId() == null ? null : memberUserMapper.selectById(apply.getUserId());
        MerchantInfoDO merchant = apply.getMerchantId() == null ? null : merchantInfoMapper.selectById(apply.getMerchantId());
        MemberUserQualificationDO qualification = apply.getQualificationId() == null ? null : memberUserQualificationMapper.selectById(apply.getQualificationId());
        List<CertExemptionApplyDO> relatedApplies = apply.getUserId() == null ? Collections.emptyList()
                : certExemptionApplyMapper.selectListByUserId(apply.getUserId());

        CertExemptionDetailRespVO respVO = BeanUtils.toBean(apply, CertExemptionDetailRespVO.class);
        if (user != null) {
            respVO.setUser(BeanUtils.toBean(user, CertExemptionDetailRespVO.UserRespVO.class));
            respVO.setUserNo(user.getUserNo());
            respVO.setUserNickname(user.getNickname());
            respVO.setUserMobile(user.getMobile());
        }
        if (merchant != null) {
            respVO.setMerchant(BeanUtils.toBean(merchant, CertExemptionDetailRespVO.MerchantRespVO.class));
            respVO.setMerchantName(merchant.getMerchantName());
        }
        if (qualification != null) {
            respVO.setQualification(BeanUtils.toBean(qualification, CertExemptionDetailRespVO.QualificationRespVO.class));
            respVO.setQualificationName(qualification.getQualificationName());
        }
        respVO.setRelatedApplies(relatedApplies.stream()
                .filter(item -> !Objects.equals(item.getId(), apply.getId()))
                .limit(10)
                .map(item -> BeanUtils.toBean(item, CertExemptionDetailRespVO.RelatedApplyRespVO.class))
                .collect(Collectors.toList()));
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(@Valid CertExemptionAuditReqVO reqVO) {
        CertExemptionApplyDO apply = getRequiredApply(reqVO.getId());
        certExemptionApplyMapper.updateById(CertExemptionApplyDO.builder()
                .id(apply.getId())
                .auditStatus(reqVO.getAuditStatus())
                .auditRemark(reqVO.getAuditRemark())
                .rejectReason(reqVO.getRejectReason())
                .auditBy(SecurityFrameworkUtils.getLoginUserId())
                .auditTime(LocalDateTime.now())
                .build());
        if (apply.getUserId() != null) {
            messagePushDispatchService.dispatchSingle("lb_cert_exemption_audited", "证件豁免审核结果通知", "CERT_EXEMPTION",
                    apply.getId(), apply.getUserId(), "您的证件豁免审核结果已更新，请及时查看。");
        }
    }

    private CertExemptionApplyDO getRequiredApply(Long id) {
        CertExemptionApplyDO apply = certExemptionApplyMapper.selectById(id);
        if (apply == null) {
            throw exception(CERT_EXEMPTION_APPLY_NOT_EXISTS);
        }
        return apply;
    }

    private void fillDisplayInfo(List<CertExemptionRespVO> list) {
        Set<Long> userIds = convertSet(list, CertExemptionRespVO::getUserId, Objects::nonNull);
        Set<Long> merchantIds = convertSet(list, CertExemptionRespVO::getMerchantId, Objects::nonNull);
        Set<Long> qualificationIds = convertSet(list, CertExemptionRespVO::getQualificationId, Objects::nonNull);
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? Collections.emptyMap() : convertMap(memberUserMapper.selectBatchIds(userIds), MemberUserDO::getId);
        Map<Long, MerchantInfoDO> merchantMap = merchantIds.isEmpty() ? Collections.emptyMap() : convertMap(merchantInfoMapper.selectBatchIds(merchantIds), MerchantInfoDO::getId);
        Map<Long, MemberUserQualificationDO> qualificationMap = qualificationIds.isEmpty() ? Collections.emptyMap() : convertMap(memberUserQualificationMapper.selectBatchIds(qualificationIds), MemberUserQualificationDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user != null) {
                item.setUserNo(user.getUserNo());
                item.setUserNickname(user.getNickname());
                item.setUserMobile(user.getMobile());
            }
            MerchantInfoDO merchant = merchantMap.get(item.getMerchantId());
            if (merchant != null) {
                item.setMerchantName(merchant.getMerchantName());
            }
            MemberUserQualificationDO qualification = qualificationMap.get(item.getQualificationId());
            if (qualification != null) {
                item.setQualificationName(qualification.getQualificationName());
            }
        });
    }

    private List<Long> resolveMatchedUserIds(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return Collections.emptyList();
        }
        return memberUserMapper.selectListByKeyword(keyword).stream()
                .map(MemberUserDO::getId)
                .collect(Collectors.toList());
    }
}
