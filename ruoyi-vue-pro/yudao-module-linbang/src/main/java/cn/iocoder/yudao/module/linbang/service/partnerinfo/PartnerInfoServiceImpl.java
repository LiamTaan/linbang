package cn.iocoder.yudao.module.linbang.service.partnerinfo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo.PartnerInfoDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo.PartnerInfoPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo.PartnerInfoRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo.PartnerInfoUpdateRegionsReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantpricereport.MerchantPriceReportDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.dto.PartnerWorkbenchAggregateDTO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerinfo.PartnerInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerregionrel.PartnerRegionRelDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantpricereport.MerchantPriceReportMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.partnercoordination.PartnerCoordinationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.partnerinfo.PartnerInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.partnerregionrel.PartnerRegionRelMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PARTNER_INFO_NOT_EXISTS;

@Service
@Validated
public class PartnerInfoServiceImpl implements PartnerInfoService {

    @Resource
    private PartnerInfoMapper partnerInfoMapper;
    @Resource
    private PartnerRegionRelMapper partnerRegionRelMapper;
    @Resource
    private MerchantEntryMapper merchantEntryMapper;
    @Resource
    private MerchantPriceReportMapper merchantPriceReportMapper;
    @Resource
    private PartnerCoordinationMapper partnerCoordinationMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MemberUserRealNameMapper memberUserRealNameMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MerchantServiceCategoryMapper merchantServiceCategoryMapper;

    @Override
    public PageResult<PartnerInfoRespVO> getPartnerInfoPage(PartnerInfoPageReqVO reqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(reqVO.getUserKeyword());
        if (StrUtil.isNotBlank(reqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<PartnerInfoDO> pageResult = partnerInfoMapper.selectPage(reqVO, matchedUserIds);
        List<PartnerInfoRespVO> list = BeanUtils.toBean(pageResult.getList(), PartnerInfoRespVO.class);
        fillUserDisplayInfo(list);
        list.forEach(item -> item.setRegionAdcodes(getPartnerRegionAdcodes(item.getId())));
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public PartnerInfoDO getPartnerInfo(Long id) {
        return partnerInfoMapper.selectById(id);
    }

    @Override
    public PartnerInfoDetailRespVO getPartnerInfoDetail(Long id) {
        PartnerInfoDO partnerInfo = partnerInfoMapper.selectById(id);
        if (partnerInfo == null) {
            throw exception(PARTNER_INFO_NOT_EXISTS);
        }
        MemberUserDO user = partnerInfo.getUserId() == null ? null : memberUserMapper.selectById(partnerInfo.getUserId());

        List<PartnerRegionRelDO> regions = partnerRegionRelMapper.selectListByPartnerId(id);
        List<String> regionAdcodes = regions.stream()
                .filter(item -> "ENABLE".equalsIgnoreCase(item.getStatus()))
                .map(PartnerRegionRelDO::getAdcode)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        PartnerWorkbenchAggregateDTO aggregate = regionAdcodes.isEmpty()
                ? new PartnerWorkbenchAggregateDTO()
                : partnerCoordinationMapper.selectWorkbenchAggregate(regionAdcodes);
        List<MerchantPriceReportDO> priceReports = regionAdcodes.isEmpty() ? Collections.emptyList()
                : merchantPriceReportMapper.selectList(new LambdaQueryWrapperX<MerchantPriceReportDO>()
                .eq(MerchantPriceReportDO::getPartnerId, id)
                .in(MerchantPriceReportDO::getRegionCode, regionAdcodes)
                .orderByDesc(MerchantPriceReportDO::getId)
                .last("LIMIT 10"));
        Set<Long> merchantIdsForReport = convertSet(priceReports, MerchantPriceReportDO::getMerchantId,
                item -> item.getMerchantId() != null);
        Map<Long, MerchantInfoDO> merchantMap = merchantIdsForReport.isEmpty() ? Collections.emptyMap()
                : convertMap(merchantInfoMapper.selectBatchIds(merchantIdsForReport), MerchantInfoDO::getId);
        Set<Long> categoryIdsForReport = convertSet(priceReports, MerchantPriceReportDO::getCategoryId,
                item -> item.getCategoryId() != null);
        Map<Long, MerchantServiceCategoryDO> categoryMap = categoryIdsForReport.isEmpty() ? Collections.emptyMap()
                : convertMap(merchantServiceCategoryMapper.selectBatchIds(categoryIdsForReport), MerchantServiceCategoryDO::getId);

        return PartnerInfoDetailAssembler.build(partnerInfo, user, regions, regionAdcodes, aggregate, priceReports,
                countPendingEntries(regionAdcodes),
                aggregate == null || aggregate.getPendingDisputeCount() == null ? 0L : aggregate.getPendingDisputeCount(),
                countPriceReports(id, regionAdcodes, "PENDING"),
                countPriceReports(id, regionAdcodes, "APPROVED"),
                countPriceReports(id, regionAdcodes, "REJECTED"), merchantMap, categoryMap);
    }

    @Override
    public PartnerInfoDO getPartnerInfoByUserId(Long userId) {
        return partnerInfoMapper.selectByUserId(userId);
    }

    @Override
    public PartnerInfoDO getOrCreatePartner(Long userId) {
        PartnerInfoDO partnerInfo = getPartnerInfoByUserId(userId);
        if (partnerInfo != null) {
            return partnerInfo;
        }
        MemberUserDO user = memberUserMapper.selectById(userId);
        if (user == null) {
            throw exception(PARTNER_INFO_NOT_EXISTS);
        }
        MemberUserRealNameDO realName = memberUserRealNameMapper.selectByUserId(userId);
        String contactName = realName != null && StrUtil.isNotBlank(realName.getRealName())
                ? realName.getRealName()
                : StrUtil.blankToDefault(user.getNickname(), "区域合作商");
        partnerInfo = PartnerInfoDO.builder()
                .userId(userId)
                .partnerName(contactName)
                .contactName(contactName)
                .contactMobile(user.getMobile())
                .status("ENABLE")
                .build();
        try {
            partnerInfoMapper.insert(partnerInfo);
            return partnerInfo;
        } catch (DuplicateKeyException ex) {
            PartnerInfoDO concurrent = partnerInfoMapper.selectByUserIdForUpdate(userId);
            if (concurrent == null) {
                throw ex;
            }
            return concurrent;
        }
    }

    @Override
    public List<String> getPartnerRegionAdcodes(Long partnerId) {
        return partnerRegionRelMapper.selectListByPartnerId(partnerId).stream()
                .map(item -> item.getAdcode() == null ? "" : item.getAdcode())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePartnerRegions(PartnerInfoUpdateRegionsReqVO reqVO) {
        PartnerInfoDO partnerInfo = partnerInfoMapper.selectById(reqVO.getId());
        if (partnerInfo == null) {
            throw exception(PARTNER_INFO_NOT_EXISTS);
        }
        Map<String, PartnerInfoUpdateRegionsReqVO.RegionItem> regionMap = new LinkedHashMap<>();
        reqVO.getRegions().forEach(item -> regionMap.putIfAbsent(item.getAdcode(), item));
        partnerRegionRelMapper.deleteByPartnerId(reqVO.getId());
        regionMap.values().forEach(item -> partnerRegionRelMapper.insert(PartnerRegionRelDO.builder()
                .partnerId(reqVO.getId())
                .province(item.getProvince())
                .city(item.getCity())
                .district(item.getDistrict())
                .adcode(item.getAdcode())
                .status("ENABLE")
                .build()));
    }

    private Long countPendingEntries(List<String> regionAdcodes) {
        if (regionAdcodes == null || regionAdcodes.isEmpty()) {
            return 0L;
        }
        return merchantEntryMapper.selectCount(new LambdaQueryWrapperX<MerchantEntryDO>()
                .in(MerchantEntryDO::getRegionCode, regionAdcodes)
                .eq(MerchantEntryDO::getStatus, "PENDING"));
    }

    private Long countPriceReports(Long partnerId, List<String> regionAdcodes, String status) {
        if (regionAdcodes == null || regionAdcodes.isEmpty()) {
            return 0L;
        }
        return merchantPriceReportMapper.selectCount(new LambdaQueryWrapperX<MerchantPriceReportDO>()
                .eq(MerchantPriceReportDO::getPartnerId, partnerId)
                .in(MerchantPriceReportDO::getRegionCode, regionAdcodes)
                .eq(MerchantPriceReportDO::getStatus, status));
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private void fillUserDisplayInfo(List<PartnerInfoRespVO> list) {
        Set<Long> userIds = convertSet(list, PartnerInfoRespVO::getUserId,
                item -> item.getUserId() != null);
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(userIds), MemberUserDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user == null) {
                return;
            }
            item.setUserNo(user.getUserNo());
            item.setUserNickname(user.getNickname());
            item.setUserMobile(user.getMobile());
        });
    }
}
