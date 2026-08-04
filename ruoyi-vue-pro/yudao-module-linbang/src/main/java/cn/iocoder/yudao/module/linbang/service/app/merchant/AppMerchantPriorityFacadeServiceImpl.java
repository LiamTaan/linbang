package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.prioritypool.vo.AppPriorityPoolCurrentRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.showcasereward.vo.AppShowcaseRewardCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.showcasereward.vo.AppShowcaseRewardRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.ShowcaseRewardRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.prioritypoolrecord.PriorityPoolRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.showcasereward.ShowcaseRewardDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.prioritypoolrecord.PriorityPoolRecordMapper;
import cn.iocoder.yudao.module.linbang.service.match.ShowcaseRewardService;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_INFO_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.REVIEW_ACCESS_DENIED;

@Service
@Validated
public class AppMerchantPriorityFacadeServiceImpl implements AppMerchantPriorityFacadeService {

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private PriorityPoolRecordMapper priorityPoolRecordMapper;
    @Resource
    private ShowcaseRewardService showcaseRewardService;
    @Resource
    private FileService fileService;

    @Override
    public AppPriorityPoolCurrentRespVO getCurrentPriorityPool(Long authUserId) {
        MerchantInfoDO merchant = getRequiredMerchant(authUserId);
        PriorityPoolRecordDO record = priorityPoolRecordMapper.selectCurrentByMerchantId(merchant.getId());
        AppPriorityPoolCurrentRespVO respVO = new AppPriorityPoolCurrentRespVO();
        respVO.setMerchantId(merchant.getId());
        if (record != null) {
            respVO.setStatus(record.getStatus());
            respVO.setReasonCode(record.getReasonCode());
            respVO.setReasonRemark(record.getReasonRemark());
            respVO.setEffectiveTime(record.getEffectiveTime());
            respVO.setExpireTime(record.getExpireTime());
        }
        return respVO;
    }

    @Override
    public Long createShowcaseReward(Long authUserId, @Valid AppShowcaseRewardCreateReqVO reqVO) {
        MerchantInfoDO merchant = getRequiredMerchant(authUserId);
        validateOwnedEvidenceFiles(authUserId, reqVO.getEvidenceFileIdsJson());
        return showcaseRewardService.createReward(ShowcaseRewardDO.builder()
                .merchantId(merchant.getId())
                .userId(merchant.getUserId())
                .title(reqVO.getTitle())
                .description(reqVO.getDescription())
                .evidenceFileIdsJson(reqVO.getEvidenceFileIdsJson())
                .build());
    }

    @Override
    public PageResult<AppShowcaseRewardRespVO> getShowcaseRewardPage(Long authUserId) {
        MerchantInfoDO merchant = getRequiredMerchant(authUserId);
        List<AppShowcaseRewardRespVO> list = showcaseRewardService.getRewardListByMerchantId(merchant.getId()).stream()
                .map(this::convertReward)
                .collect(Collectors.toList());
        return new PageResult<>(list, (long) list.size());
    }

    @Override
    public AppShowcaseRewardRespVO getShowcaseReward(Long authUserId, Long id) {
        MerchantInfoDO merchant = getRequiredMerchant(authUserId);
        ShowcaseRewardRespVO reward = showcaseRewardService.getReward(id);
        if (reward == null || !merchant.getId().equals(reward.getMerchantId())) {
            return null;
        }
        return convertReward(reward);
    }

    private AppShowcaseRewardRespVO convertReward(ShowcaseRewardRespVO reward) {
        AppShowcaseRewardRespVO respVO = new AppShowcaseRewardRespVO();
        respVO.setId(reward.getId());
        respVO.setMerchantId(reward.getMerchantId());
        respVO.setTitle(reward.getTitle());
        respVO.setDescription(reward.getDescription());
        respVO.setEvidenceFileIdsJson(reward.getEvidenceFileIdsJson());
        respVO.setAuditStatus(reward.getAuditStatus());
        respVO.setAuditRemark(reward.getAuditRemark());
        respVO.setRejectReason(reward.getRejectReason());
        respVO.setPriorityEnabled(reward.getPriorityEnabled());
        respVO.setEffectiveStartTime(reward.getEffectiveStartTime());
        respVO.setEffectiveEndTime(reward.getEffectiveEndTime());
        respVO.setCreateTime(reward.getCreateTime());
        return respVO;
    }

    private MerchantInfoDO getRequiredMerchant(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantInfoDO merchant = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, loginUser.getId())
                .last("LIMIT 1"));
        if (merchant == null) {
            throw exception(MERCHANT_INFO_NOT_EXISTS);
        }
        return merchant;
    }

    private void validateOwnedEvidenceFiles(Long authUserId, String fileIdsJson) {
        if (fileIdsJson == null || fileIdsJson.trim().isEmpty()) {
            return;
        }
        List<Long> ids;
        try {
            ids = JsonUtils.parseArray(fileIdsJson, Long.class);
        } catch (RuntimeException ex) {
            throw exception(REVIEW_ACCESS_DENIED);
        }
        if (ids == null || ids.size() > 10 || ids.stream().anyMatch(Objects::isNull)
                || ids.size() != new HashSet<>(ids).size()) {
            throw exception(REVIEW_ACCESS_DENIED);
        }
        for (Long id : ids) {
            FileDO file = fileService.getFile(id);
            String type = file == null ? null : file.getType();
            if (file == null || !Objects.equals(file.getCreator(), String.valueOf(authUserId))
                    || !(cn.hutool.core.util.StrUtil.startWithIgnoreCase(type, "image/")
                    || cn.hutool.core.util.StrUtil.startWithIgnoreCase(type, "video/")
                    || "application/pdf".equalsIgnoreCase(type))) {
                throw exception(REVIEW_ACCESS_DENIED);
            }
        }
    }
}
