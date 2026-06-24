package cn.iocoder.yudao.module.linbang.service.merchantservicepoint;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint.vo.MerchantServicePointDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint.vo.MerchantServicePointPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint.vo.MerchantServicePointRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint.vo.MerchantServicePointSaveReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategoryrel.MerchantCategoryRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantservicepoint.MerchantServicePointDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategoryrel.MerchantCategoryRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantservicepoint.MerchantServicePointMapper;
import cn.iocoder.yudao.module.linbang.service.map.AmapLocationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_INFO_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_SERVICE_POINT_NOT_EXISTS;

@Service
@Validated
public class MerchantServicePointServiceImpl implements MerchantServicePointService {

    @Resource
    private MerchantServicePointMapper merchantServicePointMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MerchantEntryMapper merchantEntryMapper;
    @Resource
    private MerchantCategoryRelMapper merchantCategoryRelMapper;
    @Resource
    private MerchantServiceCategoryMapper merchantServiceCategoryMapper;
    @Resource
    private AmapLocationService amapLocationService;

    @Override
    public Long createMerchantServicePoint(MerchantServicePointSaveReqVO createReqVO) {
        validateMerchantExists(createReqVO.getMerchantId());
        AmapLocationService.ResolvedAddress resolvedAddress = resolveAddress(createReqVO);
        MerchantServicePointDO merchantServicePoint = BeanUtils.toBean(createReqVO, MerchantServicePointDO.class)
                .setProvince(resolvedAddress.getProvince())
                .setCity(resolvedAddress.getCity())
                .setDistrict(resolvedAddress.getDistrict())
                .setStreet(resolvedAddress.getStreet())
                .setDetailAddress(resolvedAddress.getDetailAddress())
                .setLongitude(resolvedAddress.getLongitude())
                .setLatitude(resolvedAddress.getLatitude());
        merchantServicePointMapper.insert(merchantServicePoint);
        return merchantServicePoint.getId();
    }

    @Override
    public void updateMerchantServicePoint(MerchantServicePointSaveReqVO updateReqVO) {
        validateMerchantServicePointExists(updateReqVO.getId());
        validateMerchantExists(updateReqVO.getMerchantId());
        AmapLocationService.ResolvedAddress resolvedAddress = resolveAddress(updateReqVO);
        MerchantServicePointDO updateObj = BeanUtils.toBean(updateReqVO, MerchantServicePointDO.class)
                .setProvince(resolvedAddress.getProvince())
                .setCity(resolvedAddress.getCity())
                .setDistrict(resolvedAddress.getDistrict())
                .setStreet(resolvedAddress.getStreet())
                .setDetailAddress(resolvedAddress.getDetailAddress())
                .setLongitude(resolvedAddress.getLongitude())
                .setLatitude(resolvedAddress.getLatitude());
        merchantServicePointMapper.updateById(updateObj);
    }

    @Override
    public void deleteMerchantServicePoint(Long id) {
        validateMerchantServicePointExists(id);
        merchantServicePointMapper.deleteById(id);
    }

    @Override
    public void deleteMerchantServicePointListByIds(List<Long> ids) {
        merchantServicePointMapper.deleteByIds(ids);
    }

    @Override
    public MerchantServicePointDO getMerchantServicePoint(Long id) {
        return merchantServicePointMapper.selectById(id);
    }

    @Override
    public MerchantServicePointDetailRespVO getMerchantServicePointDetail(Long id) {
        MerchantServicePointDO point = merchantServicePointMapper.selectById(id);
        if (point == null) {
            throw exception(MERCHANT_SERVICE_POINT_NOT_EXISTS);
        }
        MerchantInfoDO merchant = point.getMerchantId() == null ? null : merchantInfoMapper.selectById(point.getMerchantId());
        MerchantEntryDO latestEntry = point.getMerchantId() == null ? null : merchantEntryMapper.selectOne(
                new LambdaQueryWrapperX<MerchantEntryDO>()
                        .eq(MerchantEntryDO::getMerchantId, point.getMerchantId())
                        .orderByDesc(MerchantEntryDO::getId)
                        .last("LIMIT 1"));
        List<MerchantServicePointDO> merchantPoints = point.getMerchantId() == null ? Collections.emptyList()
                : merchantServicePointMapper.selectList(new LambdaQueryWrapperX<MerchantServicePointDO>()
                .eq(MerchantServicePointDO::getMerchantId, point.getMerchantId())
                .orderByDesc(MerchantServicePointDO::getId));
        List<MerchantCategoryRelDO> categoryRels = point.getMerchantId() == null ? Collections.emptyList()
                : merchantCategoryRelMapper.selectListByMerchantId(point.getMerchantId());
        Map<Long, MerchantServiceCategoryDO> categoryMap = buildCategoryMap(categoryRels);
        return MerchantServicePointDetailAssembler.build(point, merchant, latestEntry, merchantPoints, categoryRels, categoryMap);
    }

    @Override
    public PageResult<MerchantServicePointRespVO> getMerchantServicePointPage(MerchantServicePointPageReqVO pageReqVO) {
        PageResult<MerchantServicePointDO> pageResult = merchantServicePointMapper.selectAdminPage(pageReqVO);
        List<MerchantServicePointRespVO> list = BeanUtils.toBean(pageResult.getList(), MerchantServicePointRespVO.class);
        fillMerchantDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    private void fillMerchantDisplayInfo(List<MerchantServicePointRespVO> list) {
        Set<Long> merchantIds = convertSet(list, MerchantServicePointRespVO::getMerchantId,
                item -> item.getMerchantId() != null);
        Map<Long, MerchantInfoDO> merchantMap = CollectionUtils.isEmpty(merchantIds) ? Collections.emptyMap()
                : convertMap(merchantInfoMapper.selectBatchIds(merchantIds), MerchantInfoDO::getId);
        list.forEach(item -> {
            MerchantInfoDO merchant = merchantMap.get(item.getMerchantId());
            if (merchant == null) {
                return;
            }
            item.setMerchantName(merchant.getMerchantName());
            item.setContactName(merchant.getContactName());
            item.setContactMobile(merchant.getContactMobile());
        });
    }

    private Map<Long, MerchantServiceCategoryDO> buildCategoryMap(List<MerchantCategoryRelDO> categoryRels) {
        if (CollectionUtils.isEmpty(categoryRels)) {
            return Collections.emptyMap();
        }
        Set<Long> categoryIds = categoryRels.stream()
                .map(MerchantCategoryRelDO::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(categoryIds)) {
            return Collections.emptyMap();
        }
        return merchantServiceCategoryMapper.selectBatchIds(categoryIds).stream()
                .collect(Collectors.toMap(MerchantServiceCategoryDO::getId, item -> item));
    }

    private void validateMerchantServicePointExists(Long id) {
        if (merchantServicePointMapper.selectById(id) == null) {
            throw exception(MERCHANT_SERVICE_POINT_NOT_EXISTS);
        }
    }

    private void validateMerchantExists(Long merchantId) {
        if (merchantInfoMapper.selectById(merchantId) == null) {
            throw exception(MERCHANT_INFO_NOT_EXISTS);
        }
    }

    private AmapLocationService.ResolvedAddress resolveAddress(MerchantServicePointSaveReqVO reqVO) {
        return amapLocationService.resolveAddress(AmapLocationService.ResolveAddressRequest.builder()
                .province(reqVO.getProvince())
                .city(reqVO.getCity())
                .district(reqVO.getDistrict())
                .street(reqVO.getStreet())
                .detailAddress(reqVO.getDetailAddress())
                .longitude(reqVO.getLongitude())
                .latitude(reqVO.getLatitude())
                .build());
    }
}
