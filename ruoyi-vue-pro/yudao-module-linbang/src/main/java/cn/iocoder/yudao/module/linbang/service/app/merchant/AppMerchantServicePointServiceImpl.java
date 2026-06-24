package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointStatusUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointUpdateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantservicepoint.MerchantServicePointDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantservicepoint.MerchantServicePointMapper;
import cn.iocoder.yudao.module.linbang.service.map.AmapLocationService;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_AUTH_REQUIRED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_SERVICE_POINT_NOT_EXISTS;

@Service
@Validated
public class AppMerchantServicePointServiceImpl implements AppMerchantServicePointService {

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MerchantServicePointMapper merchantServicePointMapper;
    @Resource
    private AmapLocationService amapLocationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createServicePoint(Long authUserId, AppMerchantServicePointCreateReqVO reqVO) {
        MerchantInfoDO merchantInfo = getCurrentMerchant(authUserId);
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
        MerchantInfoDO merchantInfo = getCurrentMerchant(authUserId);
        PageResult<MerchantServicePointDO> pageResult = merchantServicePointMapper.selectPage(reqVO,
                new LambdaQueryWrapperX<MerchantServicePointDO>()
                        .eq(MerchantServicePointDO::getMerchantId, merchantInfo.getId())
                        .likeIfPresent(MerchantServicePointDO::getPointName, StringUtils.trimToNull(reqVO.getPointName()))
                        .eqIfPresent(MerchantServicePointDO::getStatus, reqVO.getStatus())
                        .orderByDesc(MerchantServicePointDO::getId));
        return BeanUtils.toBean(pageResult, AppMerchantServicePointRespVO.class);
    }

    @Override
    public AppMerchantServicePointRespVO getServicePoint(Long authUserId, Long id) {
        MerchantInfoDO merchantInfo = getCurrentMerchant(authUserId);
        MerchantServicePointDO servicePoint = merchantServicePointMapper.selectByIdAndMerchantId(id, merchantInfo.getId());
        if (servicePoint == null) {
            throw exception(MERCHANT_SERVICE_POINT_NOT_EXISTS);
        }
        return BeanUtils.toBean(servicePoint, AppMerchantServicePointRespVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateServicePoint(Long authUserId, AppMerchantServicePointUpdateReqVO reqVO) {
        MerchantInfoDO merchantInfo = getCurrentMerchant(authUserId);
        MerchantServicePointDO existed = merchantServicePointMapper.selectByIdAndMerchantId(reqVO.getId(), merchantInfo.getId());
        if (existed == null) {
            throw exception(MERCHANT_SERVICE_POINT_NOT_EXISTS);
        }
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
        MerchantInfoDO merchantInfo = getCurrentMerchant(authUserId);
        MerchantServicePointDO existed = merchantServicePointMapper.selectByIdAndMerchantId(reqVO.getId(), merchantInfo.getId());
        if (existed == null) {
            throw exception(MERCHANT_SERVICE_POINT_NOT_EXISTS);
        }
        merchantServicePointMapper.updateById(MerchantServicePointDO.builder()
                .id(existed.getId())
                .status(reqVO.getStatus())
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteServicePoint(Long authUserId, Long id) {
        MerchantInfoDO merchantInfo = getCurrentMerchant(authUserId);
        MerchantServicePointDO existed = merchantServicePointMapper.selectByIdAndMerchantId(id, merchantInfo.getId());
        if (existed == null) {
            throw exception(MERCHANT_SERVICE_POINT_NOT_EXISTS);
        }
        merchantServicePointMapper.deleteById(id);
    }

    private MerchantInfoDO getCurrentMerchant(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantInfoDO merchantInfo = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, loginUser.getId()));
        if (merchantInfo == null) {
            throw exception(MERCHANT_AUTH_REQUIRED);
        }
        return merchantInfo;
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
