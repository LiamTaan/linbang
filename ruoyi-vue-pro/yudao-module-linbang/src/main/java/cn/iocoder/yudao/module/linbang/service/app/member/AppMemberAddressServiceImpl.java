package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.member.address.vo.AppMemberAddressCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.address.vo.AppMemberAddressResolveLocationReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.address.vo.AppMemberAddressResolveLocationRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.address.vo.AppMemberAddressRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.address.vo.AppMemberAddressUpdateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberaddress.MemberUserAddressDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberaddress.MemberUserAddressMapper;
import cn.iocoder.yudao.module.linbang.service.map.AmapLocationService;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_ADDRESS_NOT_EXISTS;

@Service
@Validated
public class AppMemberAddressServiceImpl implements AppMemberAddressService {

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MemberUserAddressMapper memberUserAddressMapper;
    @Resource
    private AmapLocationService amapLocationService;

    @Override
    public PageResult<AppMemberAddressRespVO> getAddressPage(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        List<AppMemberAddressRespVO> list = memberUserAddressMapper.selectListByUserId(loginUser.getId()).stream()
                .map(this::convert)
                .collect(Collectors.toList());
        return new PageResult<>(list, (long) list.size());
    }

    @Override
    public AppMemberAddressRespVO getAddress(Long authUserId, Long id) {
        return convert(getOwnedAddress(authUserId, id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAddress(Long authUserId, AppMemberAddressCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        AmapLocationService.ResolvedAddress resolvedAddress = resolveAddress(reqVO);
        if (Boolean.TRUE.equals(reqVO.getIsDefault())) {
            clearDefaultAddress(loginUser.getId());
        }
        MemberUserAddressDO address = MemberUserAddressDO.builder()
                .userId(loginUser.getId())
                .receiverName(reqVO.getReceiverName())
                .receiverMobile(reqVO.getReceiverMobile())
                .province(resolvedAddress.getProvince())
                .city(resolvedAddress.getCity())
                .district(resolvedAddress.getDistrict())
                .street(resolvedAddress.getStreet())
                .detailAddress(resolvedAddress.getDetailAddress())
                .longitude(resolvedAddress.getLongitude())
                .latitude(resolvedAddress.getLatitude())
                .adcode(resolvedAddress.getAdcode())
                .isDefault(reqVO.getIsDefault())
                .build();
        memberUserAddressMapper.insert(address);
        return address.getId();
    }

    @Override
    public AppMemberAddressResolveLocationRespVO resolveLocation(AppMemberAddressResolveLocationReqVO reqVO) {
        AmapLocationService.ResolvedAddress resolvedAddress = amapLocationService.resolveAddress(AmapLocationService.ResolveAddressRequest.builder()
                .longitude(reqVO.getLongitude())
                .latitude(reqVO.getLatitude())
                .detailAddress(reqVO.getDetailAddress())
                .build());
        AppMemberAddressResolveLocationRespVO respVO = new AppMemberAddressResolveLocationRespVO();
        respVO.setProvince(resolvedAddress.getProvince());
        respVO.setCity(resolvedAddress.getCity());
        respVO.setDistrict(resolvedAddress.getDistrict());
        respVO.setStreet(resolvedAddress.getStreet());
        respVO.setDetailAddress(StrUtil.blankToDefault(resolvedAddress.getDetailAddress(), reqVO.getDetailAddress()));
        respVO.setLongitude(resolvedAddress.getLongitude());
        respVO.setLatitude(resolvedAddress.getLatitude());
        respVO.setAdcode(resolvedAddress.getAdcode());
        respVO.setDisplayAddress(buildDisplayAddress(respVO));
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(Long authUserId, AppMemberAddressUpdateReqVO reqVO) {
        MemberUserAddressDO existed = getOwnedAddress(authUserId, reqVO.getId());
        AmapLocationService.ResolvedAddress resolvedAddress = resolveAddress(reqVO);
        if (Boolean.TRUE.equals(reqVO.getIsDefault())) {
            clearDefaultAddress(existed.getUserId());
        }
        memberUserAddressMapper.updateById(MemberUserAddressDO.builder()
                .id(existed.getId())
                .receiverName(reqVO.getReceiverName())
                .receiverMobile(reqVO.getReceiverMobile())
                .province(resolvedAddress.getProvince())
                .city(resolvedAddress.getCity())
                .district(resolvedAddress.getDistrict())
                .street(resolvedAddress.getStreet())
                .detailAddress(resolvedAddress.getDetailAddress())
                .longitude(resolvedAddress.getLongitude())
                .latitude(resolvedAddress.getLatitude())
                .adcode(resolvedAddress.getAdcode())
                .isDefault(reqVO.getIsDefault())
                .build());
    }

    @Override
    public void deleteAddress(Long authUserId, Long id) {
        MemberUserAddressDO existed = getOwnedAddress(authUserId, id);
        memberUserAddressMapper.deleteById(existed.getId());
    }

    private MemberUserAddressDO getOwnedAddress(Long authUserId, Long id) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MemberUserAddressDO address = memberUserAddressMapper.selectOne(new LambdaQueryWrapperX<MemberUserAddressDO>()
                .eq(MemberUserAddressDO::getId, id)
                .eq(MemberUserAddressDO::getUserId, loginUser.getId()));
        if (address == null) {
            throw exception(MEMBER_USER_ADDRESS_NOT_EXISTS);
        }
        return address;
    }

    private void clearDefaultAddress(Long userId) {
        memberUserAddressMapper.update(null, new LambdaUpdateWrapper<MemberUserAddressDO>()
                .eq(MemberUserAddressDO::getUserId, userId)
                .set(MemberUserAddressDO::getIsDefault, Boolean.FALSE));
    }

    private AmapLocationService.ResolvedAddress resolveAddress(AppMemberAddressCreateReqVO reqVO) {
        return amapLocationService.resolveAddress(AmapLocationService.ResolveAddressRequest.builder()
                .province(reqVO.getProvince())
                .city(reqVO.getCity())
                .district(reqVO.getDistrict())
                .street(reqVO.getStreet())
                .detailAddress(reqVO.getDetailAddress())
                .longitude(reqVO.getLongitude())
                .latitude(reqVO.getLatitude())
                .adcode(reqVO.getAdcode())
                .build());
    }

    private AppMemberAddressRespVO convert(MemberUserAddressDO address) {
        if (address == null) {
            return null;
        }
        AppMemberAddressRespVO respVO = new AppMemberAddressRespVO();
        respVO.setId(address.getId());
        respVO.setUserId(address.getUserId());
        respVO.setReceiverName(address.getReceiverName());
        respVO.setReceiverMobile(address.getReceiverMobile());
        respVO.setProvince(address.getProvince());
        respVO.setCity(address.getCity());
        respVO.setDistrict(address.getDistrict());
        respVO.setStreet(address.getStreet());
        respVO.setDetailAddress(address.getDetailAddress());
        respVO.setLongitude(address.getLongitude());
        respVO.setLatitude(address.getLatitude());
        respVO.setAdcode(address.getAdcode());
        respVO.setIsDefault(address.getIsDefault());
        respVO.setCreateTime(address.getCreateTime());
        return respVO;
    }

    private String buildDisplayAddress(AppMemberAddressResolveLocationRespVO respVO) {
        StringBuilder builder = new StringBuilder();
        appendIfPresent(builder, respVO.getProvince());
        appendIfPresent(builder, respVO.getCity());
        appendIfPresent(builder, respVO.getDistrict());
        appendIfPresent(builder, respVO.getStreet());
        appendIfPresent(builder, respVO.getDetailAddress());
        return builder.toString();
    }

    private void appendIfPresent(StringBuilder builder, String value) {
        String trimmed = StrUtil.trimToEmpty(value);
        if (StrUtil.isNotBlank(trimmed)) {
            builder.append(trimmed);
        }
    }
}
