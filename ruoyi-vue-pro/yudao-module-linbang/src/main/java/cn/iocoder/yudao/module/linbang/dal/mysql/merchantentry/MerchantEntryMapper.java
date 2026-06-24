package cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantentry.vo.*;

/**
 * 服务商入驻申请表 Mapper
 *
 * @author dawn
 */
@Mapper
public interface MerchantEntryMapper extends BaseMapperX<MerchantEntryDO> {

    default PageResult<MerchantEntryDO> selectPage(MerchantEntryPageReqVO reqVO, Collection<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MerchantEntryDO>()
                .eqIfPresent(MerchantEntryDO::getMerchantId, reqVO.getMerchantId())
                .inIfPresent(MerchantEntryDO::getUserId, userIds)
                .eqIfPresent(MerchantEntryDO::getEntryNo, reqVO.getEntryNo())
                .eqIfPresent(MerchantEntryDO::getRegionCode, reqVO.getRegionCode())
                .eqIfPresent(MerchantEntryDO::getFirstAuditStatus, reqVO.getFirstAuditStatus())
                .eqIfPresent(MerchantEntryDO::getFirstAuditBy, reqVO.getFirstAuditBy())
                .betweenIfPresent(MerchantEntryDO::getFirstAuditTime, reqVO.getFirstAuditTime())
                .eqIfPresent(MerchantEntryDO::getFinalAuditStatus, reqVO.getFinalAuditStatus())
                .eqIfPresent(MerchantEntryDO::getFinalAuditBy, reqVO.getFinalAuditBy())
                .betweenIfPresent(MerchantEntryDO::getFinalAuditTime, reqVO.getFinalAuditTime())
                .eqIfPresent(MerchantEntryDO::getStatus, reqVO.getStatus())
                .eqIfPresent(MerchantEntryDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(MerchantEntryDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MerchantEntryDO::getId));
    }

}
