package cn.iocoder.yudao.module.linbang.dal.mysql.messagefeedbackstat;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.messagefeedback.vo.MessageFeedbackStatPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagefeedbackstat.MessageFeedbackStatDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageFeedbackStatMapper extends BaseMapperX<MessageFeedbackStatDO> {

    default MessageFeedbackStatDO selectByStatKey(String statKey) {
        return selectOne(new LambdaQueryWrapperX<MessageFeedbackStatDO>()
                .eq(MessageFeedbackStatDO::getStatKey, statKey)
                .last("LIMIT 1"));
    }

    default MessageFeedbackStatDO selectByStatKeyForUpdate(String statKey) {
        return selectOne(new LambdaQueryWrapperX<MessageFeedbackStatDO>()
                .eq(MessageFeedbackStatDO::getStatKey, statKey)
                .last("LIMIT 1 FOR UPDATE"));
    }

    default PageResult<MessageFeedbackStatDO> selectPage(MessageFeedbackStatPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MessageFeedbackStatDO>()
                .eqIfPresent(MessageFeedbackStatDO::getSceneCode, reqVO.getSceneCode())
                .eqIfPresent(MessageFeedbackStatDO::getMessageCategory, reqVO.getMessageCategory())
                .eqIfPresent(MessageFeedbackStatDO::getChannelType, reqVO.getChannelType())
                .eqIfPresent(MessageFeedbackStatDO::getTemplateId, reqVO.getTemplateId())
                .eqIfPresent(MessageFeedbackStatDO::getCampaignId, reqVO.getCampaignId())
                .betweenIfPresent(MessageFeedbackStatDO::getStatDate, reqVO.getStatDate())
                .orderByDesc(MessageFeedbackStatDO::getId));
    }
}
