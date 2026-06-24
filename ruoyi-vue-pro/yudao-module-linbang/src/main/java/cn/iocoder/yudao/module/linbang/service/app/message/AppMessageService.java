package cn.iocoder.yudao.module.linbang.service.app.message;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;

public interface AppMessageService {

    PageResult<MessageRecordDO> getMessageRecordPage(Long userId, AppMessageRecordPageReqVO reqVO);

    AppMessageRecordDetailRespVO getMessageRecord(Long userId, Long id);
}
