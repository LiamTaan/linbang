package cn.iocoder.yudao.module.linbang.dal.mysql.messagescene;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.messagescene.vo.MessageScenePageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagescene.MessageSceneDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageSceneMapper extends BaseMapperX<MessageSceneDO> {

    default PageResult<MessageSceneDO> selectPage(MessageScenePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MessageSceneDO>()
                .likeIfPresent(MessageSceneDO::getSceneCode, reqVO.getSceneCode())
                .likeIfPresent(MessageSceneDO::getSceneName, reqVO.getSceneName())
                .eqIfPresent(MessageSceneDO::getMessageCategory, reqVO.getMessageCategory())
                .eqIfPresent(MessageSceneDO::getBizType, reqVO.getBizType())
                .eqIfPresent(MessageSceneDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MessageSceneDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(MessageSceneDO::getId));
    }
}
