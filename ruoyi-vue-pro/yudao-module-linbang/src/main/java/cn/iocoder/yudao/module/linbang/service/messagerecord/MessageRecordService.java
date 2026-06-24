package cn.iocoder.yudao.module.linbang.service.messagerecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.messagerecord.vo.MessageRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagerecord.vo.MessageRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagerecord.vo.MessageRecordRespVO;

public interface MessageRecordService {

    PageResult<MessageRecordRespVO> getMessageRecordPage(MessageRecordPageReqVO reqVO);

    MessageRecordDetailRespVO getMessageRecordDetail(Long id);
}
