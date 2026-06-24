package cn.iocoder.yudao.module.linbang.service.partnerinfo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo.PartnerInfoDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo.PartnerInfoPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo.PartnerInfoRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantpricereport.MerchantPriceReportDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerinfo.PartnerInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerregionrel.PartnerRegionRelDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaint.ComplaintMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantpricereport.MerchantPriceReportMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.partnerinfo.PartnerInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.partnerregionrel.PartnerRegionRelMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
    private ComplaintMapper complaintMapper;
    @Resource
    private MerchantPriceReportMapper merchantPriceReportMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
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
                .map(item -> item.getAdcode() == null ? "" : item.getAdcode())
                .collect(Collectors.toList());
        List<Long> merchantIds = merchantEntryMapper.selectList(new LambdaQueryWrapperX<MerchantEntryDO>()
                        .inIfPresent(MerchantEntryDO::getRegionCode, regionAdcodes)
                        .eq(MerchantEntryDO::getStatus, "APPROVED"))
                .stream()
                .map(MerchantEntryDO::getMerchantId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        List<OrderInfoDO> orders = merchantIds.isEmpty() ? Collections.emptyList()
                : orderInfoMapper.selectList(new LambdaQueryWrapperX<OrderInfoDO>()
                .in(OrderInfoDO::getMerchantId, merchantIds));
        List<Long> orderIds = orders.stream()
                .map(OrderInfoDO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<MerchantPriceReportDO> priceReports = regionAdcodes.isEmpty() ? Collections.emptyList()
                : merchantPriceReportMapper.selectList(new LambdaQueryWrapperX<MerchantPriceReportDO>()
                .in(MerchantPriceReportDO::getRegionCode, regionAdcodes)
                .orderByDesc(MerchantPriceReportDO::getId));
        Set<Long> merchantIdsForReport = convertSet(priceReports, MerchantPriceReportDO::getMerchantId,
                item -> item.getMerchantId() != null);
        Map<Long, MerchantInfoDO> merchantMap = merchantIdsForReport.isEmpty() ? Collections.emptyMap()
                : convertMap(merchantInfoMapper.selectBatchIds(merchantIdsForReport), MerchantInfoDO::getId);
        Set<Long> categoryIdsForReport = convertSet(priceReports, MerchantPriceReportDO::getCategoryId,
                item -> item.getCategoryId() != null);
        Map<Long, MerchantServiceCategoryDO> categoryMap = categoryIdsForReport.isEmpty() ? Collections.emptyMap()
                : convertMap(merchantServiceCategoryMapper.selectBatchIds(categoryIdsForReport), MerchantServiceCategoryDO::getId);

        return PartnerInfoDetailAssembler.build(partnerInfo, user, regions, regionAdcodes, orders, orderIds, priceReports,
                countPendingEntries(regionAdcodes), countPendingComplaints(orderIds), countPendingPriceReports(regionAdcodes),
                merchantMap, categoryMap);
    }

    @Override
    public PartnerInfoDO getPartnerInfoByUserId(Long userId) {
        return partnerInfoMapper.selectOne(PartnerInfoDO::getUserId, userId);
    }

    @Override
    public List<String> getPartnerRegionAdcodes(Long partnerId) {
        return partnerRegionRelMapper.selectListByPartnerId(partnerId).stream()
                .map(item -> item.getAdcode() == null ? "" : item.getAdcode())
                .collect(Collectors.toList());
    }

    private Long countPendingEntries(List<String> regionAdcodes) {
        if (regionAdcodes == null || regionAdcodes.isEmpty()) {
            return 0L;
        }
        return merchantEntryMapper.selectCount(new LambdaQueryWrapperX<MerchantEntryDO>()
                .in(MerchantEntryDO::getRegionCode, regionAdcodes)
                .eq(MerchantEntryDO::getStatus, "PENDING"));
    }

    private Long countPendingComplaints(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return 0L;
        }
        return complaintMapper.selectCount(new LambdaQueryWrapperX<ComplaintDO>()
                .in(ComplaintDO::getOrderId, orderIds)
                .in(ComplaintDO::getStatus, "PENDING", "PROCESSING"));
    }

    private Long countPendingPriceReports(List<String> regionAdcodes) {
        if (regionAdcodes == null || regionAdcodes.isEmpty()) {
            return 0L;
        }
        return merchantPriceReportMapper.selectCount(new LambdaQueryWrapperX<MerchantPriceReportDO>()
                .in(MerchantPriceReportDO::getRegionCode, regionAdcodes)
                .eq(MerchantPriceReportDO::getStatus, "PENDING"));
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
