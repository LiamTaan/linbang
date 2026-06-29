package cn.iocoder.yudao.module.linbang.dal.mysql.escrowproof;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.escrowproof.vo.EscrowProofPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.escrowproof.EscrowProofDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EscrowProofMapper extends BaseMapperX<EscrowProofDO> {

    default PageResult<EscrowProofDO> selectPage(EscrowProofPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EscrowProofDO>()
                .eqIfPresent(EscrowProofDO::getProofNo, reqVO.getProofNo())
                .eqIfPresent(EscrowProofDO::getOrderId, reqVO.getOrderId())
                .eqIfPresent(EscrowProofDO::getUnitId, reqVO.getUnitId())
                .eqIfPresent(EscrowProofDO::getUserId, reqVO.getUserId())
                .eqIfPresent(EscrowProofDO::getProofStatus, reqVO.getProofStatus())
                .orderByDesc(EscrowProofDO::getId));
    }
}
