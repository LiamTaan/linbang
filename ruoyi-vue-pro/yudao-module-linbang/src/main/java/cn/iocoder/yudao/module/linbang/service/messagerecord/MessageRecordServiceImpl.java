package cn.iocoder.yudao.module.linbang.service.messagerecord;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.messagerecord.vo.MessageRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagerecord.vo.MessageRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagerecord.vo.MessageRecordRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagetemplate.MessageTemplateDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord.MessageRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagetemplate.MessageTemplateMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_RECORD_NOT_EXISTS;

@Service
@Validated
public class MessageRecordServiceImpl implements MessageRecordService {

    @Resource
    private MessageRecordMapper messageRecordMapper;
    @Resource
    private MessageTemplateMapper messageTemplateMapper;
    @Resource
    private MemberUserMapper memberUserMapper;

    @Override
    public PageResult<MessageRecordRespVO> getMessageRecordPage(MessageRecordPageReqVO reqVO) {
        List<Long> matchedReceiverUserIds = resolveMatchedUserIds(reqVO.getReceiverUserKeyword());
        if (StrUtil.isNotBlank(reqVO.getReceiverUserKeyword()) && CollUtil.isEmpty(matchedReceiverUserIds)) {
            return PageResult.empty();
        }
        PageResult<MessageRecordDO> pageResult = messageRecordMapper.selectPage(reqVO, matchedReceiverUserIds);
        List<MessageRecordRespVO> list = BeanUtils.toBean(pageResult.getList(), MessageRecordRespVO.class);
        fillReceiverUserDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public MessageRecordDetailRespVO getMessageRecordDetail(Long id) {
        MessageRecordDO record = messageRecordMapper.selectById(id);
        if (record == null) {
            throw exception(MESSAGE_RECORD_NOT_EXISTS);
        }
        MemberUserDO receiverUser = record.getReceiverUserId() == null ? null : memberUserMapper.selectById(record.getReceiverUserId());
        MessageTemplateDO template = record.getTemplateId() == null ? null : messageTemplateMapper.selectById(record.getTemplateId());
        return MessageRecordDetailAssembler.build(record, receiverUser, template);
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private void fillReceiverUserDisplayInfo(List<MessageRecordRespVO> list) {
        java.util.Set<Long> receiverUserIds = convertSet(list, MessageRecordRespVO::getReceiverUserId,
                item -> item.getReceiverUserId() != null);
        Map<Long, MemberUserDO> userMap = receiverUserIds.isEmpty() ? java.util.Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(receiverUserIds), MemberUserDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getReceiverUserId());
            if (user == null) {
                return;
            }
            item.setReceiverUserNo(user.getUserNo());
            item.setReceiverUserNickname(user.getNickname());
            item.setReceiverUserMobile(user.getMobile());
        });
    }
}
