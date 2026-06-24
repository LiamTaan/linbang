package cn.iocoder.yudao.module.linbang.service.messagetemplate;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate.vo.MessageTemplateDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate.vo.MessageTemplatePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate.vo.MessageTemplateSaveReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagetemplate.MessageTemplateDO;

public interface MessageTemplateService {

    PageResult<MessageTemplateDO> getMessageTemplatePage(MessageTemplatePageReqVO reqVO);

    Long createMessageTemplate(MessageTemplateSaveReqVO reqVO);

    void updateMessageTemplate(MessageTemplateSaveReqVO reqVO);

    MessageTemplateDO getMessageTemplate(Long id);

    MessageTemplateDetailRespVO getMessageTemplateDetail(Long id);
}
