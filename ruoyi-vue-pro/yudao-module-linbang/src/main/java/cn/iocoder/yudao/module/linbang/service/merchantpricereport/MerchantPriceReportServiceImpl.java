package cn.iocoder.yudao.module.linbang.service.merchantpricereport;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo.MerchantPriceReportAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo.MerchantPriceReportDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo.MerchantPriceReportPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo.MerchantPriceReportRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantpricereport.MerchantPriceReportDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerinfo.PartnerInfoDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantpricereport.MerchantPriceReportMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.partnerinfo.PartnerInfoMapper;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_PRICE_REPORT_AUDIT_STATUS_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_PRICE_REPORT_NOT_EXISTS;

@Service
@Validated
public class MerchantPriceReportServiceImpl implements MerchantPriceReportService {

    @Resource
    private MerchantPriceReportMapper merchantPriceReportMapper;
    @Resource
    private PartnerInfoMapper partnerInfoMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MerchantServiceCategoryMapper merchantServiceCategoryMapper;
    @Resource
    private MerchantEntryMapper merchantEntryMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;

    @Override
    public PageResult<MerchantPriceReportRespVO> getMerchantPriceReportPage(MerchantPriceReportPageReqVO reqVO) {
        PageResult<MerchantPriceReportDO> pageResult = merchantPriceReportMapper.selectPage(reqVO);
        List<MerchantPriceReportRespVO> list = BeanUtils.toBean(pageResult.getList(), MerchantPriceReportRespVO.class);
        fillDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public MerchantPriceReportDetailRespVO getMerchantPriceReportDetail(Long id) {
        MerchantPriceReportDO report = merchantPriceReportMapper.selectById(id);
        if (report == null) {
            throw exception(MERCHANT_PRICE_REPORT_NOT_EXISTS);
        }

        MerchantInfoDO merchantInfo = report.getMerchantId() == null ? null : merchantInfoMapper.selectById(report.getMerchantId());
        PartnerInfoDO partnerInfo = report.getPartnerId() == null ? null : partnerInfoMapper.selectById(report.getPartnerId());
        MerchantServiceCategoryDO category = report.getCategoryId() == null ? null : merchantServiceCategoryMapper.selectById(report.getCategoryId());
        MerchantEntryDO merchantEntry = report.getMerchantId() == null ? null : merchantEntryMapper.selectOne(
                new LambdaQueryWrapperX<MerchantEntryDO>()
                        .eq(MerchantEntryDO::getMerchantId, report.getMerchantId())
                        .orderByDesc(MerchantEntryDO::getId)
                        .last("limit 1"));
        MemberUserDO merchantEntryUser = merchantEntry == null || merchantEntry.getUserId() == null
                ? null : memberUserMapper.selectById(merchantEntry.getUserId());
        List<MerchantPriceReportDO> relatedReports = merchantPriceReportMapper.selectList(new LambdaQueryWrapperX<MerchantPriceReportDO>()
                .eqIfPresent(MerchantPriceReportDO::getPartnerId, report.getPartnerId())
                .eqIfPresent(MerchantPriceReportDO::getRegionCode, report.getRegionCode())
                .orderByDesc(MerchantPriceReportDO::getId));
        Map<Long, MerchantInfoDO> relatedMerchantMap = buildMerchantMap(relatedReports);
        Map<Long, PartnerInfoDO> relatedPartnerMap = buildPartnerMap(relatedReports);
        Map<Long, MerchantServiceCategoryDO> relatedCategoryMap = buildCategoryMap(relatedReports);

        return MerchantPriceReportDetailAssembler.build(report, merchantInfo, partnerInfo, category,
                merchantEntry, merchantEntryUser, relatedReports, relatedMerchantMap, relatedPartnerMap, relatedCategoryMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditMerchantPriceReport(MerchantPriceReportAuditReqVO reqVO) {
        MerchantPriceReportDO report = merchantPriceReportMapper.selectById(reqVO.getId());
        if (report == null) {
            throw exception(MERCHANT_PRICE_REPORT_NOT_EXISTS);
        }
        if (!"PENDING".equals(report.getAuditStatus())) {
            throw exception(MERCHANT_PRICE_REPORT_AUDIT_STATUS_INVALID);
        }
        MerchantInfoDO merchantInfo = report.getMerchantId() == null ? null : merchantInfoMapper.selectById(report.getMerchantId());
        merchantPriceReportMapper.updateById(MerchantPriceReportDO.builder()
                .id(reqVO.getId())
                .auditStatus(reqVO.getAuditStatus())
                .auditRemark(reqVO.getAuditRemark())
                .rejectReason(reqVO.getRejectReason())
                .auditBy(SecurityFrameworkUtils.getLoginUserId())
                .auditTime(LocalDateTime.now())
                .status("APPROVED".equals(reqVO.getAuditStatus()) ? "APPROVED" : "REJECTED")
                .build());
        messagePushDispatchService.dispatchSingle("lb_price_report_audited", "价格申报审核结果通知", "PRICE_REPORT",
                report.getId(), merchantInfo != null ? merchantInfo.getUserId() : null,
                "管理员审核价格申报后自动通知服务商");
    }

    private void fillDisplayInfo(List<MerchantPriceReportRespVO> list) {
        java.util.Set<Long> merchantIds = convertSet(list, MerchantPriceReportRespVO::getMerchantId,
                item -> item.getMerchantId() != null);
        Map<Long, MerchantInfoDO> merchantMap = merchantIds.isEmpty() ? Collections.emptyMap()
                : convertMap(merchantInfoMapper.selectBatchIds(merchantIds), MerchantInfoDO::getId);
        java.util.Set<Long> partnerIds = convertSet(list, MerchantPriceReportRespVO::getPartnerId,
                item -> item.getPartnerId() != null);
        Map<Long, PartnerInfoDO> partnerMap = partnerIds.isEmpty() ? Collections.emptyMap()
                : convertMap(partnerInfoMapper.selectBatchIds(partnerIds), PartnerInfoDO::getId);
        java.util.Set<Long> categoryIds = convertSet(list, MerchantPriceReportRespVO::getCategoryId,
                item -> item.getCategoryId() != null);
        Map<Long, MerchantServiceCategoryDO> categoryMap = categoryIds.isEmpty() ? Collections.emptyMap()
                : convertMap(merchantServiceCategoryMapper.selectBatchIds(categoryIds), MerchantServiceCategoryDO::getId);
        list.forEach(item -> {
            MerchantInfoDO merchant = merchantMap.get(item.getMerchantId());
            if (merchant != null) {
                item.setMerchantName(merchant.getMerchantName());
                item.setMerchantContactName(merchant.getContactName());
                item.setMerchantContactMobile(merchant.getContactMobile());
            }
            PartnerInfoDO partner = partnerMap.get(item.getPartnerId());
            if (partner != null) {
                item.setPartnerName(partner.getPartnerName());
            }
            MerchantServiceCategoryDO category = categoryMap.get(item.getCategoryId());
            if (category != null) {
                item.setCategoryName(category.getCategoryName());
            }
        });
    }

    private Map<Long, MerchantInfoDO> buildMerchantMap(List<MerchantPriceReportDO> relatedReports) {
        if (relatedReports == null || relatedReports.isEmpty()) {
            return Collections.emptyMap();
        }
        java.util.Set<Long> merchantIds = convertSet(relatedReports, MerchantPriceReportDO::getMerchantId,
                item -> item.getMerchantId() != null);
        return merchantIds.isEmpty() ? Collections.emptyMap()
                : convertMap(merchantInfoMapper.selectBatchIds(merchantIds), MerchantInfoDO::getId);
    }

    private Map<Long, PartnerInfoDO> buildPartnerMap(List<MerchantPriceReportDO> relatedReports) {
        if (relatedReports == null || relatedReports.isEmpty()) {
            return Collections.emptyMap();
        }
        java.util.Set<Long> partnerIds = convertSet(relatedReports, MerchantPriceReportDO::getPartnerId,
                item -> item.getPartnerId() != null);
        return partnerIds.isEmpty() ? Collections.emptyMap()
                : convertMap(partnerInfoMapper.selectBatchIds(partnerIds), PartnerInfoDO::getId);
    }

    private Map<Long, MerchantServiceCategoryDO> buildCategoryMap(List<MerchantPriceReportDO> relatedReports) {
        if (relatedReports == null || relatedReports.isEmpty()) {
            return Collections.emptyMap();
        }
        java.util.Set<Long> categoryIds = convertSet(relatedReports, MerchantPriceReportDO::getCategoryId,
                item -> item.getCategoryId() != null);
        return categoryIds.isEmpty() ? Collections.emptyMap()
                : convertMap(merchantServiceCategoryMapper.selectBatchIds(categoryIds), MerchantServiceCategoryDO::getId);
    }
}
