package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.member.address.vo.AppMemberAddressCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.address.vo.AppMemberAddressNearbyPoiRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.address.vo.AppMemberAddressResolveLocationReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.address.vo.AppMemberAddressResolveLocationRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.address.vo.AppMemberAddressRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.address.vo.AppMemberAddressUpdateReqVO;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

public interface AppMemberAddressService {

    PageResult<AppMemberAddressRespVO> getAddressPage(Long authUserId);

    AppMemberAddressRespVO getAddress(Long authUserId, Long id);

    Long createAddress(Long authUserId, @Valid AppMemberAddressCreateReqVO reqVO);

    AppMemberAddressResolveLocationRespVO resolveLocation(@Valid AppMemberAddressResolveLocationReqVO reqVO);

    List<AppMemberAddressNearbyPoiRespVO> searchNearbyPois(BigDecimal longitude, BigDecimal latitude, String keywords, Integer radiusMeters);

    void updateAddress(Long authUserId, @Valid AppMemberAddressUpdateReqVO reqVO);

    void deleteAddress(Long authUserId, Long id);
}
