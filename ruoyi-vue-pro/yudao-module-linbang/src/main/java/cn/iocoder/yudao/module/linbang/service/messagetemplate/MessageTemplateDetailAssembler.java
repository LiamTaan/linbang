package cn.iocoder.yudao.module.linbang.service.messagetemplate;

import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate.vo.MessageTemplateDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class MessageTemplateDetailAssembler {

    private MessageTemplateDetailAssembler() {
    }

    static List<MessageTemplateDetailRespVO.MessageRecordSimpleRespVO> buildRecords(List<MessageRecordDO> records,
                                                                                    Map<Long, MemberUserDO> userMap) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }
        return records.stream().map(record -> {
            MessageTemplateDetailRespVO.MessageRecordSimpleRespVO respVO = new MessageTemplateDetailRespVO.MessageRecordSimpleRespVO();
            respVO.setId(record.getId());
            respVO.setReceiverUserId(record.getReceiverUserId());
            MemberUserDO user = record.getReceiverUserId() == null ? null : userMap.get(record.getReceiverUserId());
            if (user != null) {
                respVO.setReceiverUserNo(user.getUserNo());
                respVO.setReceiverUserNickname(user.getNickname());
                respVO.setReceiverUserMobile(user.getMobile());
            }
            respVO.setChannelType(record.getChannelType());
            respVO.setBizType(record.getBizType());
            respVO.setBizId(record.getBizId());
            respVO.setSendStatus(record.getSendStatus());
            respVO.setSendTime(record.getSendTime());
            respVO.setFailReason(record.getFailReason());
            respVO.setCreateTime(record.getCreateTime());
            return respVO;
        }).collect(Collectors.toList());
    }

    static List<MessageTemplateDetailRespVO.ChannelStatRespVO> buildChannelStats(Map<String, Long> channelCountMap) {
        if (channelCountMap == null || channelCountMap.isEmpty()) {
            return Collections.emptyList();
        }
        return channelCountMap.entrySet().stream().map(entry -> {
            MessageTemplateDetailRespVO.ChannelStatRespVO respVO = new MessageTemplateDetailRespVO.ChannelStatRespVO();
            respVO.setChannelType(entry.getKey());
            respVO.setRecordCount(entry.getValue().intValue());
            return respVO;
        }).collect(Collectors.toList());
    }
}
