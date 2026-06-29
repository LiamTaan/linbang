package cn.iocoder.yudao.module.linbang.service.merchantsubaccount;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo.MerchantSubAccountPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo.MerchantSubAccountRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo.MerchantSubAccountSaveReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo.MerchantSubAccountServicePointUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo.MerchantSubAccountStatusUpdateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantservicepoint.MerchantServicePointDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantsubaccount.MerchantSubAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantsubaccountservicepointrel.MerchantSubAccountServicePointRelDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantservicepoint.MerchantServicePointMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantsubaccount.MerchantSubAccountMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantsubaccountservicepointrel.MerchantSubAccountServicePointRelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_AUTH_REQUIRED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_INFO_NOT_EXISTS;

@Service
@Validated
public class MerchantSubAccountAdminServiceImpl implements MerchantSubAccountAdminService {

    @Resource
    private MerchantSubAccountMapper merchantSubAccountMapper;
    @Resource
    private MerchantSubAccountServicePointRelMapper merchantSubAccountServicePointRelMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MerchantServicePointMapper merchantServicePointMapper;
    @Resource
    private MemberUserMapper memberUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(@Valid MerchantSubAccountSaveReqVO reqVO) {
        validateMerchant(reqVO.getMerchantId());
        MemberUserDO user = validateUser(reqVO.getUserId(), reqVO.getMobile());
        MerchantSubAccountDO subAccount = MerchantSubAccountDO.builder()
                .merchantId(reqVO.getMerchantId())
                .userId(user.getId())
                .mobile(user.getMobile())
                .permissionCodesJson(JsonUtils.toJsonString(reqVO.getPermissionCodes()))
                .status("ENABLE")
                .remark(reqVO.getRemark())
                .build();
        merchantSubAccountMapper.insert(subAccount);
        return subAccount.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(@Valid MerchantSubAccountSaveReqVO reqVO) {
        MerchantSubAccountDO existed = validateSubAccount(reqVO.getId());
        validateMerchant(reqVO.getMerchantId());
        MemberUserDO user = validateUser(reqVO.getUserId(), reqVO.getMobile());
        merchantSubAccountMapper.updateById(MerchantSubAccountDO.builder()
                .id(existed.getId())
                .merchantId(reqVO.getMerchantId())
                .userId(user.getId())
                .mobile(user.getMobile())
                .permissionCodesJson(JsonUtils.toJsonString(reqVO.getPermissionCodes()))
                .remark(reqVO.getRemark())
                .build());
    }

    @Override
    public PageResult<MerchantSubAccountRespVO> getPage(MerchantSubAccountPageReqVO reqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(reqVO.getUserKeyword());
        if (StrUtil.isNotBlank(reqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<MerchantSubAccountDO> pageResult = merchantSubAccountMapper.selectPage(reqVO, matchedUserIds);
        return new PageResult<>(assembleList(pageResult.getList()), pageResult.getTotal());
    }

    @Override
    public MerchantSubAccountRespVO get(Long id) {
        MerchantSubAccountDO subAccount = validateSubAccount(id);
        List<MerchantSubAccountRespVO> result = assembleList(Collections.singletonList(subAccount));
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(@Valid MerchantSubAccountStatusUpdateReqVO reqVO) {
        MerchantSubAccountDO existed = validateSubAccount(reqVO.getId());
        merchantSubAccountMapper.updateById(MerchantSubAccountDO.builder()
                .id(existed.getId())
                .status(reqVO.getStatus())
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateServicePoints(@Valid MerchantSubAccountServicePointUpdateReqVO reqVO) {
        MerchantSubAccountDO existed = validateSubAccount(reqVO.getId());
        List<MerchantServicePointDO> servicePoints = merchantServicePointMapper.selectListByMerchantId(existed.getMerchantId());
        Set<Long> validPointIds = convertSet(servicePoints, MerchantServicePointDO::getId);
        if (!validPointIds.containsAll(reqVO.getServicePointIds())) {
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

    private List<MerchantSubAccountRespVO> assembleList(List<MerchantSubAccountDO> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> merchantIds = convertSet(records, MerchantSubAccountDO::getMerchantId, Objects::nonNull);
        Set<Long> userIds = convertSet(records, MerchantSubAccountDO::getUserId, Objects::nonNull);
        List<Long> subAccountIds = convertList(records, MerchantSubAccountDO::getId);
        Map<Long, MerchantInfoDO> merchantMap = merchantIds.isEmpty() ? Collections.emptyMap()
                : convertMap(merchantInfoMapper.selectBatchIds(merchantIds), MerchantInfoDO::getId);
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectBatchIds(userIds), MemberUserDO::getId);
        Map<Long, List<MerchantSubAccountServicePointRelDO>> relMap = subAccountIds.isEmpty() ? Collections.emptyMap()
                : merchantSubAccountServicePointRelMapper.selectList(new LambdaQueryWrapperX<MerchantSubAccountServicePointRelDO>()
                .in(MerchantSubAccountServicePointRelDO::getSubAccountId, subAccountIds))
                .stream().collect(Collectors.groupingBy(MerchantSubAccountServicePointRelDO::getSubAccountId));
        Set<Long> pointIds = relMap.values().stream()
                .flatMap(List::stream)
                .map(MerchantSubAccountServicePointRelDO::getServicePointId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, MerchantServicePointDO> pointMap = pointIds.isEmpty() ? Collections.emptyMap()
                : convertMap(merchantServicePointMapper.selectBatchIds(pointIds), MerchantServicePointDO::getId);
        List<MerchantSubAccountRespVO> result = new ArrayList<>(records.size());
        for (MerchantSubAccountDO record : records) {
            MerchantSubAccountRespVO respVO = new MerchantSubAccountRespVO();
            respVO.setId(record.getId());
            respVO.setMerchantId(record.getMerchantId());
            respVO.setUserId(record.getUserId());
            respVO.setMobile(record.getMobile());
            respVO.setPermissionCodes(JsonUtils.parseArray(record.getPermissionCodesJson(), String.class));
            respVO.setStatus(record.getStatus());
            respVO.setRemark(record.getRemark());
            respVO.setCreateTime(record.getCreateTime());
            MerchantInfoDO merchant = merchantMap.get(record.getMerchantId());
            if (merchant != null) {
                respVO.setMerchantName(merchant.getMerchantName());
            }
            MemberUserDO user = userMap.get(record.getUserId());
            if (user != null) {
                respVO.setUserNo(user.getUserNo());
                respVO.setUserNickname(user.getNickname());
            }
            List<MerchantSubAccountServicePointRelDO> rels = relMap.getOrDefault(record.getId(), Collections.emptyList());
            List<Long> servicePointIds = rels.stream()
                    .map(MerchantSubAccountServicePointRelDO::getServicePointId)
                    .collect(Collectors.toList());
            respVO.setServicePointIds(servicePointIds);
            respVO.setServicePointNames(servicePointIds.stream()
                    .map(pointMap::get)
                    .filter(Objects::nonNull)
                    .map(MerchantServicePointDO::getPointName)
                    .collect(Collectors.toList()));
            result.add(respVO);
        }
        return result;
    }

    private MerchantInfoDO validateMerchant(Long merchantId) {
        MerchantInfoDO merchant = merchantInfoMapper.selectById(merchantId);
        if (merchant == null) {
            throw exception(MERCHANT_INFO_NOT_EXISTS);
        }
        return merchant;
    }

    private MemberUserDO validateUser(Long userId, String mobile) {
        MemberUserDO user = memberUserMapper.selectById(userId);
        if (user == null) {
            throw exception(MEMBER_USER_NOT_EXISTS);
        }
        if (!Objects.equals(user.getMobile(), mobile)) {
            throw exception(MERCHANT_AUTH_REQUIRED);
        }
        return user;
    }

    private MerchantSubAccountDO validateSubAccount(Long id) {
        MerchantSubAccountDO subAccount = merchantSubAccountMapper.selectById(id);
        if (subAccount == null) {
            throw exception(MERCHANT_AUTH_REQUIRED);
        }
        return subAccount;
    }

    private List<Long> resolveMatchedUserIds(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(keyword), MemberUserDO::getId);
    }
}
