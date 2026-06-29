package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointStatusUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointUpdateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantservicepoint.MerchantServicePointDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantservicepoint.MerchantServicePointMapper;
import cn.iocoder.yudao.module.linbang.service.map.AmapLocationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_AUTH_REQUIRED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_SERVICE_POINT_NOT_EXISTS;

@Service
@Validated
public class AppMerchantServicePointServiceImpl implements AppMerchantServicePointService {

    @Resource
    private MerchantServicePointMapper merchantServicePointMapper;
    @Resource
    private AmapLocationService amapLocationService;
    @Resource
    private AppMerchantOperatorContextService merchantOperatorContextService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createServicePoint(Long authUserId, AppMerchantServicePointCreateReqVO reqVO) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredServicePointManageContext(authUserId);
        MerchantInfoDO merchantInfo = context.getMerchant();
        AmapLocationService.ResolvedAddress resolvedAddress = resolveAddress(reqVO);
        MerchantServicePointDO servicePoint = MerchantServicePointDO.builder()
                .merchantId(merchantInfo.getId())
                .pointName(reqVO.getPointName())
                .province(resolvedAddress.getProvince())
                .city(resolvedAddress.getCity())
                .district(resolvedAddress.getDistrict())
                .street(resolvedAddress.getStreet())
                .detailAddress(resolvedAddress.getDetailAddress())
                .longitude(resolvedAddress.getLongitude())
                .latitude(resolvedAddress.getLatitude())
                .serviceRadiusKm(reqVO.getServiceRadiusKm())
                .status("ENABLE")
                .build();
        merchantServicePointMapper.insert(servicePoint);
        return servicePoint.getId();
    }

    @Override
    public PageResult<AppMerchantServicePointRespVO> getServicePointPage(Long authUserId, AppMerchantServicePointPageReqVO reqVO) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredContext(authUserId);
        MerchantInfoDO merchantInfo = context.getMerchant();
        List<MerchantServicePointDO> records = merchantOperatorContextService.filterVisibleServicePoints(context,
                merchantServicePointMapper.selectList(new LambdaQueryWrapperX<MerchantServicePointDO>()
                        .eq(MerchantServicePointDO::getMerchantId, merchantInfo.getId())
                        .likeIfPresent(MerchantServicePointDO::getPointName, StringUtils.trimToNull(reqVO.getPointName()))
                        .eqIfPresent(MerchantServicePointDO::getStatus, reqVO.getStatus())
                        .orderByDesc(MerchantServicePointDO::getId)));
        int safePageNo = reqVO.getPageNo() == null || reqVO.getPageNo() < 1 ? 1 : reqVO.getPageNo();
        int safePageSize = reqVO.getPageSize() == null || reqVO.getPageSize() < 1 ? 10 : reqVO.getPageSize();
        int fromIndex = Math.min((safePageNo - 1) * safePageSize, records.size());
        int toIndex = Math.min(fromIndex + safePageSize, records.size());
        List<AppMerchantServicePointRespVO> list = records.subList(fromIndex, toIndex).stream().map(item -> {
            AppMerchantServicePointRespVO respVO = BeanUtils.toBean(item, AppMerchantServicePointRespVO.class);
            respVO.setMainAccountOperator(context.isMainAccount());
            respVO.setManageable(context.isMainAccount() || context.getSafeVisibleServicePointIds().contains(item.getId()));
            return respVO;
        }).collect(java.util.stream.Collectors.toList());
        return new PageResult<>(list, (long) records.size());
    }

    @Override
    public AppMerchantServicePointRespVO getServicePoint(Long authUserId, Long id) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredContext(authUserId);
        MerchantInfoDO merchantInfo = context.getMerchant();
        MerchantServicePointDO servicePoint = merchantServicePointMapper.selectByIdAndMerchantId(id, merchantInfo.getId());
        if (servicePoint == null) {
            throw exception(MERCHANT_SERVICE_POINT_NOT_EXISTS);
        }
        merchantOperatorContextService.validateVisibleServicePoint(context, servicePoint.getId());
        AppMerchantServicePointRespVO respVO = BeanUtils.toBean(servicePoint, AppMerchantServicePointRespVO.class);
        respVO.setMainAccountOperator(context.isMainAccount());
        respVO.setManageable(context.isMainAccount() || context.getSafeVisibleServicePointIds().contains(servicePoint.getId()));
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateServicePoint(Long authUserId, AppMerchantServicePointUpdateReqVO reqVO) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredServicePointManageContext(authUserId);
        MerchantInfoDO merchantInfo = context.getMerchant();
        MerchantServicePointDO existed = merchantServicePointMapper.selectByIdAndMerchantId(reqVO.getId(), merchantInfo.getId());
        if (existed == null) {
            throw exception(MERCHANT_SERVICE_POINT_NOT_EXISTS);
        }
        merchantOperatorContextService.validateVisibleServicePoint(context, existed.getId());
        AmapLocationService.ResolvedAddress resolvedAddress = resolveAddress(reqVO);
        merchantServicePointMapper.updateById(MerchantServicePointDO.builder()
                .id(existed.getId())
                .pointName(reqVO.getPointName())
                .province(resolvedAddress.getProvince())
                .city(resolvedAddress.getCity())
                .district(resolvedAddress.getDistrict())
                .street(resolvedAddress.getStreet())
                .detailAddress(resolvedAddress.getDetailAddress())
                .longitude(resolvedAddress.getLongitude())
                .latitude(resolvedAddress.getLatitude())
                .serviceRadiusKm(reqVO.getServiceRadiusKm())
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateServicePointStatus(Long authUserId, AppMerchantServicePointStatusUpdateReqVO reqVO) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredServicePointManageContext(authUserId);
        MerchantInfoDO merchantInfo = context.getMerchant();
        MerchantServicePointDO existed = merchantServicePointMapper.selectByIdAndMerchantId(reqVO.getId(), merchantInfo.getId());
        if (existed == null) {
            throw exception(MERCHANT_SERVICE_POINT_NOT_EXISTS);
        }
        merchantOperatorContextService.validateVisibleServicePoint(context, existed.getId());
        merchantServicePointMapper.updateById(MerchantServicePointDO.builder()
                .id(existed.getId())
                .status(reqVO.getStatus())
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteServicePoint(Long authUserId, Long id) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredServicePointManageContext(authUserId);
        MerchantInfoDO merchantInfo = context.getMerchant();
        MerchantServicePointDO existed = merchantServicePointMapper.selectByIdAndMerchantId(id, merchantInfo.getId());
        if (existed == null) {
            throw exception(MERCHANT_SERVICE_POINT_NOT_EXISTS);
        }
        merchantOperatorContextService.validateVisibleServicePoint(context, existed.getId());
        merchantServicePointMapper.deleteById(id);
    }

    private AmapLocationService.ResolvedAddress resolveAddress(AppMerchantServicePointCreateReqVO reqVO) {
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
