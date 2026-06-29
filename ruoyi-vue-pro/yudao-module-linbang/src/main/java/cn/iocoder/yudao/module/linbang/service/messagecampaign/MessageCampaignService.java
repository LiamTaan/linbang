package cn.iocoder.yudao.module.linbang.service.messagecampaign;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo.MessageCampaignPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo.MessageCampaignRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo.MessageCampaignSaveReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageCampaignCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageCampaignPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagecampaign.MessageCampaignDO;

public interface MessageCampaignService {

    PageResult<MessageCampaignRespVO> getPage(MessageCampaignPageReqVO reqVO);

    MessageCampaignRespVO get(Long id);

    MessageCampaignDO getDO(Long id);

    Long create(MessageCampaignSaveReqVO reqVO);

    Long createUserDirected(Long userId, AppMessageCampaignCreateReqVO reqVO);

    PageResult<MessageCampaignRespVO> getAppPage(Long userId, AppMessageCampaignPageReqVO reqVO);

    void approve(Long id, String auditRemark);

    void reject(Long id, String rejectReason);

    void executeNow(Long id);

    int executeScheduledCampaigns();

    void cancel(Long id, String reason);

    void withdrawByUser(Long userId, Long id);
}
