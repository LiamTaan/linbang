package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount.vo.AppMerchantSubAccountCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount.vo.AppMerchantSubAccountRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount.vo.AppMerchantSubAccountServicePointUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount.vo.AppMerchantSubAccountStatusUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount.vo.AppMerchantSubAccountUpdateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantsubaccount.MerchantSubAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantsubaccountservicepointrel.MerchantSubAccountServicePointRelDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantservicepoint.MerchantServicePointMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantsubaccount.MerchantSubAccountMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantsubaccountservicepointrel.MerchantSubAccountServicePointRelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_AUTH_REQUIRED;

@Service
@Validated
public class AppMerchantSubAccountServiceImpl implements AppMerchantSubAccountService {

    @Resource
    private MerchantSubAccountMapper merchantSubAccountMapper;
    @Resource
    private MerchantSubAccountServicePointRelMapper merchantSubAccountServicePointRelMapper;
    @Resource
    private MerchantServicePointMapper merchantServicePointMapper;
    @Resource
    private AppMerchantOperatorContextService merchantOperatorContextService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Long authUserId, @Valid AppMerchantSubAccountCreateReqVO reqVO) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredMerchantManageContext(authUserId);
        MerchantInfoDO merchant = context.getMerchant();
        MerchantSubAccountDO subAccount = MerchantSubAccountDO.builder()
                .merchantId(merchant.getId())
                .userId(reqVO.getUserId())
                .mobile(reqVO.getMobile())
                .permissionCodesJson(JsonUtils.toJsonString(reqVO.getPermissionCodes()))
                .status("ENABLE")
                .remark(reqVO.getRemark())
                .build();
        merchantSubAccountMapper.insert(subAccount);
        return subAccount.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long authUserId, @Valid AppMerchantSubAccountUpdateReqVO reqVO) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredMerchantManageContext(authUserId);
        MerchantInfoDO merchant = context.getMerchant();
        MerchantSubAccountDO existed = validateSubAccountBelongsToMerchant(reqVO.getId(), merchant.getId());
        merchantSubAccountMapper.updateById(MerchantSubAccountDO.builder()
                .id(existed.getId())
                .userId(reqVO.getUserId())
                .mobile(reqVO.getMobile())
                .permissionCodesJson(JsonUtils.toJsonString(reqVO.getPermissionCodes()))
                .remark(reqVO.getRemark())
                .build());
    }

    @Override
    public PageResult<AppMerchantSubAccountRespVO> page(Long authUserId) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredMerchantManageContext(authUserId);
        MerchantInfoDO merchant = context.getMerchant();
        List<MerchantSubAccountDO> records = merchantSubAccountMapper.selectListByMerchantId(merchant.getId());
        List<AppMerchantSubAccountRespVO> list = records.stream().map(record -> {
            AppMerchantSubAccountRespVO respVO = new AppMerchantSubAccountRespVO();
            respVO.setId(record.getId());
            respVO.setMerchantId(record.getMerchantId());
            respVO.setUserId(record.getUserId());
            respVO.setMobile(record.getMobile());
            respVO.setPermissionCodes(JsonUtils.parseArray(record.getPermissionCodesJson(), String.class));
            respVO.setServicePointIds(merchantSubAccountServicePointRelMapper.selectListBySubAccountId(record.getId()).stream()
                    .map(MerchantSubAccountServicePointRelDO::getServicePointId)
                    .collect(Collectors.toList()));
            respVO.setStatus(record.getStatus());
            respVO.setRemark(record.getRemark());
            respVO.setCreateTime(record.getCreateTime());
            return respVO;
        }).collect(Collectors.toList());
        return new PageResult<>(list, (long) list.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long authUserId, @Valid AppMerchantSubAccountStatusUpdateReqVO reqVO) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredMerchantManageContext(authUserId);
        MerchantInfoDO merchant = context.getMerchant();
        MerchantSubAccountDO existed = validateSubAccountBelongsToMerchant(reqVO.getId(), merchant.getId());
        merchantSubAccountMapper.updateById(MerchantSubAccountDO.builder()
                .id(existed.getId())
                .status(reqVO.getStatus())
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateServicePoints(Long authUserId, @Valid AppMerchantSubAccountServicePointUpdateReqVO reqVO) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredMerchantManageContext(authUserId);
        MerchantInfoDO merchant = context.getMerchant();
        MerchantSubAccountDO existed = validateSubAccountBelongsToMerchant(reqVO.getId(), merchant.getId());
        long validPointCount = merchantServicePointMapper.selectListByMerchantId(merchant.getId()).stream()
                .filter(item -> reqVO.getServicePointIds().contains(item.getId()))
                .count();
        if (validPointCount != reqVO.getServicePointIds().size()) {
            throw exception(MERCHANT_AUTH_REQUIRED);
        }
        merchantSubAccountServicePointRelMapper.delete(new LambdaQueryWrapperX<MerchantSubAccountServicePointRelDO>()
                .eq(MerchantSubAccountServicePointRelDO::getSubAccountId, existed.getId()));
        for (Long servicePointId : reqVO.getServicePointIds()) {
            merchantSubAccountServicePointRelMapper.insert(MerchantSubAccountServicePointRelDO.builder()
                    .subAccountId(existed.getId())
                    .servicePointId(servicePointId)
                    .build());
        }
    }

    private MerchantSubAccountDO validateSubAccountBelongsToMerchant(Long subAccountId, Long merchantId) {
        MerchantSubAccountDO subAccount = merchantSubAccountMapper.selectById(subAccountId);
        if (subAccount == null || !Objects.equals(subAccount.getMerchantId(), merchantId)) {
            throw exception(MERCHANT_AUTH_REQUIRED);
        }
        return subAccount;
    }
}
