package cn.iocoder.yudao.module.linbang.dal.mysql.messageoptimization;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.messageoptimization.vo.MessageOptimizationPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messageoptimization.MessageOptimizationDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageOptimizationMapper extends BaseMapperX<MessageOptimizationDO> {

    default PageResult<MessageOptimizationDO> selectPage(MessageOptimizationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MessageOptimizationDO>()
                .eqIfPresent(MessageOptimizationDO::getRefType, reqVO.getRefType())
                .eqIfPresent(MessageOptimizationDO::getSceneCode, reqVO.getSceneCode())
                .eqIfPresent(MessageOptimizationDO::getMessageCategory, reqVO.getMessageCategory())
                .eqIfPresent(MessageOptimizationDO::getChannelType, reqVO.getChannelType())
                .likeIfPresent(MessageOptimizationDO::getOwner, reqVO.getOwner())
                .betweenIfPresent(MessageOptimizationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MessageOptimizationDO::getId));
    }
}
