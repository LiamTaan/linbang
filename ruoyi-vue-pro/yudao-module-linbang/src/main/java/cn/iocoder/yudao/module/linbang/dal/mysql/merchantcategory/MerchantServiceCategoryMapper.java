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

    default List<MerchantServiceCategoryDO> selectList(MerchantServiceCategoryListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<MerchantServiceCategoryDO>()
                .eqIfPresent(MerchantServiceCategoryDO::getParentId, reqVO.getParentId())
                .likeIfPresent(MerchantServiceCategoryDO::getCategoryName, reqVO.getCategoryName())
                .eqIfPresent(MerchantServiceCategoryDO::getCategoryLevel, reqVO.getCategoryLevel())
                .eqIfPresent(MerchantServiceCategoryDO::getSortNo, reqVO.getSortNo())
                .eqIfPresent(MerchantServiceCategoryDO::getIcon, reqVO.getIcon())
                .eqIfPresent(MerchantServiceCategoryDO::getDefaultPricingMode, reqVO.getDefaultPricingMode())
                .likeIfPresent(MerchantServiceCategoryDO::getSupportedPricingModes, reqVO.getSupportedPricingMode())
                .eqIfPresent(MerchantServiceCategoryDO::getSupportSplit, reqVO.getSupportSplit())
                .eqIfPresent(MerchantServiceCategoryDO::getSupportInvoice, reqVO.getSupportInvoice())
                .eqIfPresent(MerchantServiceCategoryDO::getRiskLevel, reqVO.getRiskLevel())
                .eqIfPresent(MerchantServiceCategoryDO::getLaborCategoryFlag, reqVO.getLaborCategoryFlag())
                .eqIfPresent(MerchantServiceCategoryDO::getForceAgreementType, reqVO.getForceAgreementType())
                .eqIfPresent(MerchantServiceCategoryDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MerchantServiceCategoryDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(MerchantServiceCategoryDO::getCategoryLevel)
                .orderByAsc(MerchantServiceCategoryDO::getParentId)
                .orderByAsc(MerchantServiceCategoryDO::getSortNo)
                .orderByAsc(MerchantServiceCategoryDO::getId));
    }

    default PageResult<MerchantServiceCategoryDO> selectPage(MerchantServiceCategoryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MerchantServiceCategoryDO>()
                .eqIfPresent(MerchantServiceCategoryDO::getParentId, reqVO.getParentId())
                .likeIfPresent(MerchantServiceCategoryDO::getCategoryName, reqVO.getCategoryName())
                .eqIfPresent(MerchantServiceCategoryDO::getCategoryLevel, reqVO.getCategoryLevel())
                .eqIfPresent(MerchantServiceCategoryDO::getSortNo, reqVO.getSortNo())
                .eqIfPresent(MerchantServiceCategoryDO::getIcon, reqVO.getIcon())
                .eqIfPresent(MerchantServiceCategoryDO::getDefaultPricingMode, reqVO.getDefaultPricingMode())
                .likeIfPresent(MerchantServiceCategoryDO::getSupportedPricingModes, reqVO.getSupportedPricingMode())
                .eqIfPresent(MerchantServiceCategoryDO::getSupportSplit, reqVO.getSupportSplit())
                .eqIfPresent(MerchantServiceCategoryDO::getSupportInvoice, reqVO.getSupportInvoice())
                .eqIfPresent(MerchantServiceCategoryDO::getRiskLevel, reqVO.getRiskLevel())
                .eqIfPresent(MerchantServiceCategoryDO::getLaborCategoryFlag, reqVO.getLaborCategoryFlag())
                .eqIfPresent(MerchantServiceCategoryDO::getForceAgreementType, reqVO.getForceAgreementType())
                .eqIfPresent(MerchantServiceCategoryDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MerchantServiceCategoryDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(MerchantServiceCategoryDO::getCategoryLevel)
                .orderByAsc(MerchantServiceCategoryDO::getParentId)
                .orderByAsc(MerchantServiceCategoryDO::getSortNo)
                .orderByAsc(MerchantServiceCategoryDO::getId));
    }

}
