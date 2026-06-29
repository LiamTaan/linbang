package cn.iocoder.yudao.module.linbang.service.messagefeedback;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.messagefeedback.vo.MessageFeedbackStatPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagefeedback.vo.MessageFeedbackStatRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;

public interface MessageFeedbackStatService {

    PageResult<MessageFeedbackStatRespVO> getPage(MessageFeedbackStatPageReqVO reqVO);

    MessageFeedbackStatRespVO get(Long id);

    void refreshByRecord(MessageRecordDO record);

    void refreshByTaskId(Long pushTaskId);

    void refreshByCampaignId(Long campaignId);
}
