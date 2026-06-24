package cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantcategory.vo.*;

/**
 * 服务类目表 Mapper
 *
 * @author dawn
 */
@Mapper
public interface MerchantServiceCategoryMapper extends BaseMapperX<MerchantServiceCategoryDO> {

    default PageResult<MerchantServiceCategoryDO> selectPage(MerchantServiceCategoryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MerchantServiceCategoryDO>()
                .eqIfPresent(MerchantServiceCategoryDO::getParentId, reqVO.getParentId())
                .likeIfPresent(MerchantServiceCategoryDO::getCategoryName, reqVO.getCategoryName())
                .eqIfPresent(MerchantServiceCategoryDO::getCategoryLevel, reqVO.getCategoryLevel())
                .eqIfPresent(MerchantServiceCategoryDO::getSortNo, reqVO.getSortNo())
                .eqIfPresent(MerchantServiceCategoryDO::getIcon, reqVO.getIcon())
                .eqIfPresent(MerchantServiceCategoryDO::getDefaultPricingMode, reqVO.getDefaultPricingMode())
                .eqIfPresent(MerchantServiceCategoryDO::getSupportSplit, reqVO.getSupportSplit())
                .eqIfPresent(MerchantServiceCategoryDO::getSupportInvoice, reqVO.getSupportInvoice())
                .eqIfPresent(MerchantServiceCategoryDO::getRiskLevel, reqVO.getRiskLevel())
                .eqIfPresent(MerchantServiceCategoryDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MerchantServiceCategoryDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MerchantServiceCategoryDO::getId));
    }

}