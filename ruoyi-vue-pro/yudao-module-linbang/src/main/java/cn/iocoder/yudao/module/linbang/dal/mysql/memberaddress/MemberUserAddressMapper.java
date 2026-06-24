package cn.iocoder.yudao.module.linbang.dal.mysql.memberaddress;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberaddress.MemberUserAddressDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.memberaddress.vo.*;

/**
 * 用户地址表 Mapper
 *
 * @author dawn
 */
@Mapper
public interface MemberUserAddressMapper extends BaseMapperX<MemberUserAddressDO> {

    default List<MemberUserAddressDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<MemberUserAddressDO>()
                .eq(MemberUserAddressDO::getUserId, userId)
                .orderByDesc(MemberUserAddressDO::getIsDefault)
                .orderByDesc(MemberUserAddressDO::getId));
    }

    default PageResult<MemberUserAddressDO> selectPage(MemberUserAddressPageReqVO reqVO, Collection<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberUserAddressDO>()
                .inIfPresent(MemberUserAddressDO::getUserId, userIds)
                .likeIfPresent(MemberUserAddressDO::getReceiverName, reqVO.getReceiverName())
                .eqIfPresent(MemberUserAddressDO::getReceiverMobile, reqVO.getReceiverMobile())
                .eqIfPresent(MemberUserAddressDO::getProvince, reqVO.getProvince())
                .eqIfPresent(MemberUserAddressDO::getCity, reqVO.getCity())
                .eqIfPresent(MemberUserAddressDO::getDistrict, reqVO.getDistrict())
                .eqIfPresent(MemberUserAddressDO::getStreet, reqVO.getStreet())
                .eqIfPresent(MemberUserAddressDO::getDetailAddress, reqVO.getDetailAddress())
                .eqIfPresent(MemberUserAddressDO::getLongitude, reqVO.getLongitude())
                .eqIfPresent(MemberUserAddressDO::getLatitude, reqVO.getLatitude())
                .eqIfPresent(MemberUserAddressDO::getAdcode, reqVO.getAdcode())
                .eqIfPresent(MemberUserAddressDO::getIsDefault, reqVO.getIsDefault())
                .betweenIfPresent(MemberUserAddressDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MemberUserAddressDO::getId));
    }

}
