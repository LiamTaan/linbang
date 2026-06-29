package cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.messagerecord.vo.MessageRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageRecordMapper extends BaseMapperX<MessageRecordDO> {

    default MessageRecordDO selectByDedupeKey(String dedupeKey) {
        return selectOne(MessageRecordDO::getDedupeKey, dedupeKey);
    }

    default PageResult<MessageRecordDO> selectPage(MessageRecordPageReqVO reqVO, List<Long> matchedReceiverUserIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MessageRecordDO>()
                .eqIfPresent(MessageRecordDO::getTemplateId, reqVO.getTemplateId())
                .eqIfPresent(MessageRecordDO::getCampaignId, reqVO.getCampaignId())
                .inIfPresent(MessageRecordDO::getReceiverUserId, matchedReceiverUserIds)
                .eqIfPresent(MessageRecordDO::getSceneCode, reqVO.getSceneCode())
                .eqIfPresent(MessageRecordDO::getMessageCategory, reqVO.getMessageCategory())
                .eqIfPresent(MessageRecordDO::getChannelType, reqVO.getChannelType())
                .eqIfPresent(MessageRecordDO::getBizType, reqVO.getBizType())
                .eqIfPresent(MessageRecordDO::getReadStatus, reqVO.getReadStatus())
                .eqIfPresent(MessageRecordDO::getSendStatus, reqVO.getSendStatus())
                .betweenIfPresent(MessageRecordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MessageRecordDO::getId));
    }

    default PageResult<MessageRecordDO> selectAppPage(Long userId, AppMessageRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MessageRecordDO>()
                .eq(MessageRecordDO::getReceiverUserId, userId)
                .eqIfPresent(MessageRecordDO::getSendStatus, reqVO.getSendStatus())
                .eqIfPresent(MessageRecordDO::getMessageCategory, reqVO.getMessageCategory())
                .eqIfPresent(MessageRecordDO::getReadStatus, reqVO.getReadStatus())
                .orderByDesc(MessageRecordDO::getId));
    }
}
