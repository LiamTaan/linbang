package cn.iocoder.yudao.module.linbang.service.messagerecord;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.messagerecord.vo.MessageRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagetemplate.MessageTemplateDO;

final class MessageRecordDetailAssembler {

    private MessageRecordDetailAssembler() {
    }

    static MessageRecordDetailRespVO build(MessageRecordDO record, MemberUserDO receiverUser, MessageTemplateDO template) {
        MessageRecordDetailRespVO respVO = BeanUtils.toBean(record, MessageRecordDetailRespVO.class);
        if (receiverUser != null) {
            respVO.setReceiverUserNo(receiverUser.getUserNo());
            respVO.setReceiverUserNickname(receiverUser.getNickname());
            respVO.setReceiverUserMobile(receiverUser.getMobile());
        }
        if (template != null) {
            MessageRecordDetailRespVO.TemplateSummaryRespVO templateRespVO =
                    BeanUtils.toBean(template, MessageRecordDetailRespVO.TemplateSummaryRespVO.class);
            templateRespVO.setId(template.getId());
            respVO.setTemplate(templateRespVO);
        }
        return respVO;
    }
}
