package cn.iocoder.yudao.module.linbang.service.app.message;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo.MessageCampaignRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageCampaignCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageCampaignPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageFeedbackReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageSendReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageSettingRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageSettingUpdateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;

public interface AppMessageService {

    PageResult<MessageRecordDO> getMessageRecordPage(Long userId, AppMessageRecordPageReqVO reqVO);

    AppMessageRecordDetailRespVO getMessageRecord(Long userId, Long id);

    Long sendMessage(Long userId, AppMessageSendReqVO reqVO);

    Long getUnreadCount(Long userId);

    void markRead(Long userId, Long id);

    void markAllRead(Long userId, String messageCategory);

    void submitExposeFeedback(Long userId, AppMessageFeedbackReqVO reqVO);

    void submitClickFeedback(Long userId, AppMessageFeedbackReqVO reqVO);

    void submitVoicePlayedFeedback(Long userId, AppMessageFeedbackReqVO reqVO);

    void recordExternalClick(Long recordId);

    String resolveRedirectTarget(Long recordId, String targetUrl);

    AppMessageSettingRespVO getMessageSetting(Long userId);

    void updateMessageSetting(Long userId, AppMessageSettingUpdateReqVO reqVO);

    Long createDirectedCampaign(Long userId, AppMessageCampaignCreateReqVO reqVO);

    PageResult<MessageCampaignRespVO> getDirectedCampaignPage(Long userId, AppMessageCampaignPageReqVO reqVO);

    MessageCampaignRespVO getDirectedCampaign(Long userId, Long id);

    void withdrawDirectedCampaign(Long userId, Long id);
}
