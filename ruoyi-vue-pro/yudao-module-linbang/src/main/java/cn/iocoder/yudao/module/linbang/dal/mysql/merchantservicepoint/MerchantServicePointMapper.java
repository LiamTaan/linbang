package cn.iocoder.yudao.module.linbang.dal.mysql.merchantservicepoint;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint.vo.MerchantServicePointPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantservicepoint.MerchantServicePointDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MerchantServicePointMapper extends BaseMapperX<MerchantServicePointDO> {

    default PageResult<MerchantServicePointDO> selectAdminPage(MerchantServicePointPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MerchantServicePointDO>()
                .eqIfPresent(MerchantServicePointDO::getMerchantId, reqVO.getMerchantId())
                .likeIfPresent(MerchantServicePointDO::getPointName, reqVO.getPointName())
                .eqIfPresent(MerchantServicePointDO::getProvince, reqVO.getProvince())
                .eqIfPresent(MerchantServicePointDO::getCity, reqVO.getCity())
                .eqIfPresent(MerchantServicePointDO::getDistrict, reqVO.getDistrict())
                .eqIfPresent(MerchantServicePointDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MerchantServicePointDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MerchantServicePointDO::getId));
    }

    default MerchantServicePointDO selectByIdAndMerchantId(Long id, Long merchantId) {
        return selectOne(new LambdaQueryWrapperX<MerchantServicePointDO>()
                .eq(MerchantServicePointDO::getId, id)
                .eq(MerchantServicePointDO::getMerchantId, merchantId));
    }

    default List<MerchantServicePointDO> selectListByMerchantId(Long merchantId) {
        return selectList(new LambdaQueryWrapperX<MerchantServicePointDO>()
                .eq(MerchantServicePointDO::getMerchantId, merchantId)
                .orderByDesc(MerchantServicePointDO::getStatus)
                .orderByDesc(MerchantServicePointDO::getId));
    }

}
