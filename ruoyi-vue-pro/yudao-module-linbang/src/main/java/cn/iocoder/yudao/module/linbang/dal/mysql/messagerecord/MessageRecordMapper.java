package cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.messagerecord.vo.MessageRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageRecordMapper extends BaseMapperX<MessageRecordDO> {

    default PageResult<MessageRecordDO> selectPage(MessageRecordPageReqVO reqVO, List<Long> matchedReceiverUserIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MessageRecordDO>()
                .eqIfPresent(MessageRecordDO::getTemplateId, reqVO.getTemplateId())
                .inIfPresent(MessageRecordDO::getReceiverUserId, matchedReceiverUserIds)
                .eqIfPresent(MessageRecordDO::getChannelType, reqVO.getChannelType())
                .eqIfPresent(MessageRecordDO::getBizType, reqVO.getBizType())
                .eqIfPresent(MessageRecordDO::getSendStatus, reqVO.getSendStatus())
                .betweenIfPresent(MessageRecordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MessageRecordDO::getId));
    }
}
