package cn.iocoder.yudao.module.linbang.dal.mysql.merchantpricereport;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo.MerchantPriceReportPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantpricereport.MerchantPriceReportDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MerchantPriceReportMapper extends BaseMapperX<MerchantPriceReportDO> {

    default PageResult<MerchantPriceReportDO> selectPage(MerchantPriceReportPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MerchantPriceReportDO>()
                .eqIfPresent(MerchantPriceReportDO::getMerchantId, reqVO.getMerchantId())
                .eqIfPresent(MerchantPriceReportDO::getPartnerId, reqVO.getPartnerId())
                .eqIfPresent(MerchantPriceReportDO::getCategoryId, reqVO.getCategoryId())
                .eqIfPresent(MerchantPriceReportDO::getRegionCode, reqVO.getRegionCode())
                .eqIfPresent(MerchantPriceReportDO::getStatus, reqVO.getStatus())
                .eqIfPresent(MerchantPriceReportDO::getAuditStatus, reqVO.getAuditStatus())
                .betweenIfPresent(MerchantPriceReportDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MerchantPriceReportDO::getId));
    }
}
