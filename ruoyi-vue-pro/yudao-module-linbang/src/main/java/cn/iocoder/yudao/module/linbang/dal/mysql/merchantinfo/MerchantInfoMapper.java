package cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantinfo.vo.*;

/**
 * 服务商信息表 Mapper
 *
 * @author dawn
 */
@Mapper
public interface MerchantInfoMapper extends BaseMapperX<MerchantInfoDO> {

    default PageResult<MerchantInfoDO> selectPage(MerchantInfoPageReqVO reqVO, Collection<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MerchantInfoDO>()
                .inIfPresent(MerchantInfoDO::getUserId, userIds)
                .likeIfPresent(MerchantInfoDO::getMerchantName, reqVO.getMerchantName())
                .likeIfPresent(MerchantInfoDO::getContactName, reqVO.getContactName())
                .eqIfPresent(MerchantInfoDO::getContactMobile, reqVO.getContactMobile())
                .eqIfPresent(MerchantInfoDO::getServiceScopeDesc, reqVO.getServiceScopeDesc())
                .eqIfPresent(MerchantInfoDO::getStatus, reqVO.getStatus())
                .eqIfPresent(MerchantInfoDO::getAcceptStatus, reqVO.getAcceptStatus())
                .eqIfPresent(MerchantInfoDO::getCreditScore, reqVO.getCreditScore())
                .eqIfPresent(MerchantInfoDO::getCreditLevel, reqVO.getCreditLevel())
                .betweenIfPresent(MerchantInfoDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MerchantInfoDO::getId));
    }

}
