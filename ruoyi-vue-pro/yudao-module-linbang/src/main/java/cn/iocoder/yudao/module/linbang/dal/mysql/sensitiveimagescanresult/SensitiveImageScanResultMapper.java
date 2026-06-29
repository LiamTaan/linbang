package cn.iocoder.yudao.module.linbang.dal.mysql.sensitiveimagescanresult;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitiveimagescanresult.vo.SensitiveImageScanResultPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.sensitiveimagescanresult.SensitiveImageScanResultDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SensitiveImageScanResultMapper extends BaseMapperX<SensitiveImageScanResultDO> {

    default PageResult<SensitiveImageScanResultDO> selectPage(SensitiveImageScanResultPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SensitiveImageScanResultDO>()
                .eqIfPresent(SensitiveImageScanResultDO::getSceneType, reqVO.getSceneType())
                .eqIfPresent(SensitiveImageScanResultDO::getUserId, reqVO.getUserId())
                .eqIfPresent(SensitiveImageScanResultDO::getBizType, reqVO.getBizType())
                .eqIfPresent(SensitiveImageScanResultDO::getBizId, reqVO.getBizId())
                .eqIfPresent(SensitiveImageScanResultDO::getScanStatus, reqVO.getScanStatus())
                .betweenIfPresent(SensitiveImageScanResultDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SensitiveImageScanResultDO::getId));
    }
}
