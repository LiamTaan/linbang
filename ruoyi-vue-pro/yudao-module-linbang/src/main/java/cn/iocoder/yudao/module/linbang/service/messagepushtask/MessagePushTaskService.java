package cn.iocoder.yudao.module.linbang.service.messagepushtask;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo.MessagePushTaskDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo.MessagePushTaskPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo.MessagePushTaskRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagepushtask.MessagePushTaskDO;

public interface MessagePushTaskService {

    PageResult<MessagePushTaskRespVO> getPushTaskPage(MessagePushTaskPageReqVO reqVO);

    MessagePushTaskDetailRespVO getPushTaskDetail(Long id);

    Long createTask(MessagePushTaskDO taskDO);

    void updateTaskResult(Long id, String status, int successCount, int failCount);
}
