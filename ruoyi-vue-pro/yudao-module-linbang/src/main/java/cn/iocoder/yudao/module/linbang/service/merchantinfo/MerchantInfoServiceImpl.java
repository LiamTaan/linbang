package cn.iocoder.yudao.module.linbang.service.merchantinfo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantinfo.vo.MerchantInfoRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantinfo.vo.MerchantInfoDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategoryrel.MerchantCategoryRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.service.reviewcomment.MerchantReviewMetricsResp;
import cn.iocoder.yudao.module.linbang.service.reviewcomment.ReviewCommentMetricsService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantinfo.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantservicepoint.MerchantServicePointDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategoryrel.MerchantCategoryRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantservicepoint.MerchantServicePointMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

/**
 * 服务商信息表 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class MerchantInfoServiceImpl implements MerchantInfoService {

    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MerchantEntryMapper merchantEntryMapper;
    @Resource
    private MerchantCategoryRelMapper merchantCategoryRelMapper;
    @Resource
    private MerchantServiceCategoryMapper merchantServiceCategoryMapper;
    @Resource
    private MerchantServicePointMapper merchantServicePointMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private ReviewCommentMetricsService reviewCommentMetricsService;

    @Override
    public Long createMerchantInfo(MerchantInfoSaveReqVO createReqVO) {
        // 插入
        MerchantInfoDO merchantInfo = BeanUtils.toBean(createReqVO, MerchantInfoDO.class);
        merchantInfoMapper.insert(merchantInfo);

        // 返回
        return merchantInfo.getId();
    }

    @Override
    public void updateMerchantInfo(MerchantInfoSaveReqVO updateReqVO) {
        // 校验存在
        validateMerchantInfoExists(updateReqVO.getId());
        // 更新
        MerchantInfoDO updateObj = BeanUtils.toBean(updateReqVO, MerchantInfoDO.class);
        merchantInfoMapper.updateById(updateObj);
    }

    @Override
    public void deleteMerchantInfo(Long id) {
        // 校验存在
        validateMerchantInfoExists(id);
        // 删除
        merchantInfoMapper.deleteById(id);
    }

    @Override
        public void deleteMerchantInfoListByIds(List<Long> ids) {
        // 删除
        merchantInfoMapper.deleteByIds(ids);
        }


    private void validateMerchantInfoExists(Long id) {
        if (merchantInfoMapper.selectById(id) == null) {
            throw exception(MERCHANT_INFO_NOT_EXISTS);
        }
    }

    @Override
    public MerchantInfoDO getMerchantInfo(Long id) {
        return merchantInfoMapper.selectById(id);
    }

    @Override
    public MerchantInfoDetailRespVO getMerchantInfoDetail(Long id) {
        MerchantInfoDO merchantInfo = merchantInfoMapper.selectById(id);
        if (merchantInfo == null) {
            throw exception(MERCHANT_INFO_NOT_EXISTS);
        }

        MerchantEntryDO latestEntry = merchantEntryMapper.selectOne(new LambdaQueryWrapperX<MerchantEntryDO>()
                .eq(MerchantEntryDO::getMerchantId, merchantInfo.getId())
                .orderByDesc(MerchantEntryDO::getId)
                .last("LIMIT 1"));
        List<MerchantCategoryRelDO> categoryRels = merchantCategoryRelMapper.selectListByMerchantId(merchantInfo.getId());
        Map<Long, MerchantServiceCategoryDO> categoryMap = buildCategoryMap(categoryRels);
        List<MerchantServicePointDO> servicePoints = merchantServicePointMapper.selectList(new LambdaQueryWrapperX<MerchantServicePointDO>()
                .eq(MerchantServicePointDO::getMerchantId, merchantInfo.getId())
                .orderByDesc(MerchantServicePointDO::getId));

        MerchantInfoDetailRespVO respVO = BeanUtils.toBean(merchantInfo, MerchantInfoDetailRespVO.class);
        fillReviewMetrics(respVO, merchantInfo.getId());
        if (latestEntry != null) {
            respVO.setEntryId(latestEntry.getId());
            respVO.setEntryNo(latestEntry.getEntryNo());
            respVO.setRegionCode(latestEntry.getRegionCode());
            respVO.setEntryStatus(latestEntry.getStatus());
            respVO.setFirstAuditStatus(latestEntry.getFirstAuditStatus());
            respVO.setFinalAuditStatus(latestEntry.getFinalAuditStatus());
        }
        respVO.setServicePointCount(servicePoints.size());
        respVO.setEnabledServicePointCount((int) servicePoints.stream()
                .filter(point -> Objects.equals(point.getStatus(), "ENABLE"))
                .count());
        respVO.setCategories(categoryRels.stream().map(rel -> {
            MerchantServiceCategoryDO category = categoryMap.get(rel.getCategoryId());
            MerchantInfoDetailRespVO.MerchantCategoryItem item = new MerchantInfoDetailRespVO.MerchantCategoryItem();
            item.setCategoryId(rel.getCategoryId());
            if (category != null) {
                item.setCategoryName(category.getCategoryName());
                item.setParentId(category.getParentId());
                item.setCategoryLevel(category.getCategoryLevel());
                item.setDefaultPricingMode(category.getDefaultPricingMode());
                item.setSupportSplit(category.getSupportSplit());
                item.setSupportInvoice(category.getSupportInvoice());
            }
            return item;
        }).collect(Collectors.toList()));
        respVO.setServicePoints(servicePoints.stream().map(point -> {
            MerchantInfoDetailRespVO.ServicePointItem item = new MerchantInfoDetailRespVO.ServicePointItem();
            item.setServicePointId(point.getId());
            item.setPointName(point.getPointName());
            item.setProvince(point.getProvince());
            item.setCity(point.getCity());
            item.setDistrict(point.getDistrict());
            item.setStreet(point.getStreet());
            item.setDetailAddress(point.getDetailAddress());
            item.setLongitude(point.getLongitude());
            item.setLatitude(point.getLatitude());
            item.setServiceRadiusKm(point.getServiceRadiusKm());
            item.setStatus(point.getStatus());
            return item;
        }).collect(Collectors.toList()));
        return respVO;
    }

    @Override
    public PageResult<MerchantInfoRespVO> getMerchantInfoPage(MerchantInfoPageReqVO pageReqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(pageReqVO.getUserKeyword());
        if (StrUtil.isNotBlank(pageReqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<MerchantInfoDO> pageResult = merchantInfoMapper.selectPage(pageReqVO, matchedUserIds);
        List<MerchantInfoRespVO> list = BeanUtils.toBean(pageResult.getList(), MerchantInfoRespVO.class);
        fillUserDisplayInfo(list);
        fillReviewMetrics(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private void fillUserDisplayInfo(List<MerchantInfoRespVO> list) {
        Set<Long> userIds = convertSet(list, MerchantInfoRespVO::getUserId,
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

    private void fillReviewMetrics(List<MerchantInfoRespVO> list) {
        list.forEach(item -> fillReviewMetrics(item, item.getId()));
    }

    private void fillReviewMetrics(MerchantInfoRespVO item, Long merchantId) {
        MerchantReviewMetricsResp metrics = reviewCommentMetricsService.calculateMerchantMetrics(merchantId);
        item.setCompositeScore(metrics.getCompositeScore());
        item.setPositiveRate(metrics.getPositiveRate());
        item.setInPositivePriorityPool(metrics.getInPositivePriorityPool());
    }

    private void fillReviewMetrics(MerchantInfoDetailRespVO item, Long merchantId) {
        MerchantReviewMetricsResp metrics = reviewCommentMetricsService.calculateMerchantMetrics(merchantId);
        item.setCompositeScore(metrics.getCompositeScore());
        item.setPositiveRate(metrics.getPositiveRate());
        item.setInPositivePriorityPool(metrics.getInPositivePriorityPool());
    }

    private Map<Long, MerchantServiceCategoryDO> buildCategoryMap(List<MerchantCategoryRelDO> categoryRels) {
        if (CollUtil.isEmpty(categoryRels)) {
            return Collections.emptyMap();
        }
        Set<Long> categoryIds = categoryRels.stream()
                .map(MerchantCategoryRelDO::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(categoryIds)) {
            return Collections.emptyMap();
        }
        return merchantServiceCategoryMapper.selectBatchIds(categoryIds).stream()
                .collect(Collectors.toMap(MerchantServiceCategoryDO::getId, item -> item));
    }

}
