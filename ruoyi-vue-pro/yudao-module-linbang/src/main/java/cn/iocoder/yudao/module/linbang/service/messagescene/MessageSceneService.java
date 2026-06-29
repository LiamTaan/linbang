package cn.iocoder.yudao.module.linbang.service.messagescene;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.messagescene.vo.MessageScenePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagescene.vo.MessageSceneRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagescene.vo.MessageSceneSaveReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagescene.MessageSceneDO;

public interface MessageSceneService {

    PageResult<MessageSceneRespVO> getPage(MessageScenePageReqVO reqVO);

    MessageSceneDO getMessageScene(String sceneCode);

    MessageSceneRespVO get(Long id);

    Long create(MessageSceneSaveReqVO reqVO);

    void update(MessageSceneSaveReqVO reqVO);

    void updateStatus(Long id, String status);
}
