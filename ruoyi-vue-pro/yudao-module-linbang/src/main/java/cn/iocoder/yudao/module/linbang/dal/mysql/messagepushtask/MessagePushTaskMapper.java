package cn.iocoder.yudao.module.linbang.dal.mysql.messagepushtask;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo.MessagePushTaskPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagepushtask.MessagePushTaskDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessagePushTaskMapper extends BaseMapperX<MessagePushTaskDO> {

    default PageResult<MessagePushTaskDO> selectPage(MessagePushTaskPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MessagePushTaskDO>()
                .likeIfPresent(MessagePushTaskDO::getTaskName, reqVO.getTaskName())
                .eqIfPresent(MessagePushTaskDO::getChannelType, reqVO.getChannelType())
                .eqIfPresent(MessagePushTaskDO::getStatus, reqVO.getStatus())
                .eqIfPresent(MessagePushTaskDO::getBizType, reqVO.getBizType())
                .betweenIfPresent(MessagePushTaskDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MessagePushTaskDO::getId));
    }
}
