package cn.iocoder.yudao.module.linbang.service.app.message;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordDetailRespVO;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagetemplate.MessageTemplateDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord.MessageRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagetemplate.MessageTemplateMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_RECORD_NOT_EXISTS;

@Service
@Validated
public class AppMessageServiceImpl implements AppMessageService {

    @Resource
    private MessageRecordMapper messageRecordMapper;
    @Resource
    private MessageTemplateMapper messageTemplateMapper;

    @Override
    public PageResult<MessageRecordDO> getMessageRecordPage(Long userId, AppMessageRecordPageReqVO reqVO) {
        return messageRecordMapper.selectPage(reqVO, new LambdaQueryWrapperX<MessageRecordDO>()
                .eq(MessageRecordDO::getReceiverUserId, userId)
                .eqIfPresent(MessageRecordDO::getSendStatus, reqVO.getSendStatus())
                .orderByDesc(MessageRecordDO::getId));
    }

    @Override
    public AppMessageRecordDetailRespVO getMessageRecord(Long userId, Long id) {
        MessageRecordDO record = messageRecordMapper.selectOne(new LambdaQueryWrapperX<MessageRecordDO>()
                .eq(MessageRecordDO::getId, id)
                .eq(MessageRecordDO::getReceiverUserId, userId));
        if (record == null) {
            throw exception(MESSAGE_RECORD_NOT_EXISTS);
        }
        MessageTemplateDO template = record.getTemplateId() == null ? null : messageTemplateMapper.selectById(record.getTemplateId());
        AppMessageRecordDetailRespVO respVO = BeanUtils.toBean(record, AppMessageRecordDetailRespVO.class);
        if (template != null) {
            respVO.setTemplate(BeanUtils.toBean(template, AppMessageRecordDetailRespVO.TemplateRespVO.class));
        }
        return respVO;
    }
}
