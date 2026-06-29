package cn.iocoder.yudao.module.linbang.dal.mysql.certexemption;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.certexemption.vo.CertExemptionPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.certexemption.CertExemptionApplyDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface CertExemptionApplyMapper extends BaseMapperX<CertExemptionApplyDO> {

    default List<CertExemptionApplyDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<CertExemptionApplyDO>()
                .eq(CertExemptionApplyDO::getUserId, userId)
                .orderByDesc(CertExemptionApplyDO::getId));
    }

    default PageResult<CertExemptionApplyDO> selectPageByUserId(cn.iocoder.yudao.framework.common.pojo.PageParam pageParam, Long userId) {
        return selectPage(pageParam, new LambdaQueryWrapperX<CertExemptionApplyDO>()
                .eq(CertExemptionApplyDO::getUserId, userId)
                .orderByDesc(CertExemptionApplyDO::getId));
    }

    default PageResult<CertExemptionApplyDO> selectPage(CertExemptionPageReqVO reqVO, Collection<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CertExemptionApplyDO>()
                .eqIfPresent(CertExemptionApplyDO::getUserId, reqVO.getUserId())
                .inIfPresent(CertExemptionApplyDO::getUserId, userIds)
                .eqIfPresent(CertExemptionApplyDO::getMerchantId, reqVO.getMerchantId())
                .eqIfPresent(CertExemptionApplyDO::getExemptionType, reqVO.getExemptionType())
                .eqIfPresent(CertExemptionApplyDO::getAuditStatus, reqVO.getAuditStatus())
                .betweenIfPresent(CertExemptionApplyDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CertExemptionApplyDO::getId));
    }
}
