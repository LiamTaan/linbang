package cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo;

import cn.hutool.core.util.StrUtil;
import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.orderinfo.vo.*;

/**
 * 订单主 Mapper
 *
 * @author dawn
 */
@Mapper
public interface OrderInfoMapper extends BaseMapperX<OrderInfoDO> {

    default List<OrderInfoDO> selectListByOrderNo(String orderNo) {
        if (StrUtil.isBlank(orderNo)) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<OrderInfoDO>()
                .like(OrderInfoDO::getOrderNo, orderNo)
                .orderByDesc(OrderInfoDO::getId));
    }

    default PageResult<OrderInfoDO> selectPage(OrderInfoPageReqVO reqVO, Collection<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OrderInfoDO>()
                .eqIfPresent(OrderInfoDO::getOrderNo, reqVO.getOrderNo())
                .inIfPresent(OrderInfoDO::getUserId, userIds)
                .eqIfPresent(OrderInfoDO::getMerchantId, reqVO.getMerchantId())
                .eqIfPresent(OrderInfoDO::getCategoryId, reqVO.getCategoryId())
                .eqIfPresent(OrderInfoDO::getTitle, reqVO.getTitle())
                .eqIfPresent(OrderInfoDO::getPricingMode, reqVO.getPricingMode())
                .eqIfPresent(OrderInfoDO::getBudgetAmount, reqVO.getBudgetAmount())
                .eqIfPresent(OrderInfoDO::getOrderAmount, reqVO.getOrderAmount())
                .eqIfPresent(OrderInfoDO::getServiceDurationDesc, reqVO.getServiceDurationDesc())
                .eqIfPresent(OrderInfoDO::getQuantity, reqVO.getQuantity())
                .eqIfPresent(OrderInfoDO::getRequireDesc, reqVO.getRequireDesc())
                .eqIfPresent(OrderInfoDO::getAddressId, reqVO.getAddressId())
                .eqIfPresent(OrderInfoDO::getProvince, reqVO.getProvince())
                .eqIfPresent(OrderInfoDO::getCity, reqVO.getCity())
                .eqIfPresent(OrderInfoDO::getDistrict, reqVO.getDistrict())
                .eqIfPresent(OrderInfoDO::getStreet, reqVO.getStreet())
                .eqIfPresent(OrderInfoDO::getDetailAddress, reqVO.getDetailAddress())
                .eqIfPresent(OrderInfoDO::getLongitude, reqVO.getLongitude())
                .eqIfPresent(OrderInfoDO::getLatitude, reqVO.getLatitude())
                .eqIfPresent(OrderInfoDO::getNeedInvoice, reqVO.getNeedInvoice())
                .eqIfPresent(OrderInfoDO::getNeedSplit, reqVO.getNeedSplit())
                .eqIfPresent(OrderInfoDO::getSplitStatus, reqVO.getSplitStatus())
                .eqIfPresent(OrderInfoDO::getAgreementConfirmed, reqVO.getAgreementConfirmed())
                .eqIfPresent(OrderInfoDO::getPayOrderId, reqVO.getPayOrderId())
                .eqIfPresent(OrderInfoDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(OrderInfoDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(OrderInfoDO::getId));
    }

}
