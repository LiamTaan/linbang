package cn.iocoder.yudao.module.linbang.service.messagescene;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.messagescene.vo.MessageScenePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagescene.vo.MessageSceneRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagescene.vo.MessageSceneSaveReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagescene.MessageSceneDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagescene.MessageSceneMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_SCENE_NOT_EXISTS;

@Service
@Validated
public class MessageSceneServiceImpl implements MessageSceneService {

    @Resource
    private MessageSceneMapper messageSceneMapper;

    @Override
    public PageResult<MessageSceneRespVO> getPage(MessageScenePageReqVO reqVO) {
        return BeanUtils.toBean(messageSceneMapper.selectPage(reqVO), MessageSceneRespVO.class);
    }

    @Override
    public MessageSceneDO getMessageScene(String sceneCode) {
        return messageSceneMapper.selectOne(new LambdaQueryWrapperX<MessageSceneDO>()
                .eq(MessageSceneDO::getSceneCode, sceneCode)
                .eq(MessageSceneDO::getStatus, "ENABLE")
                .last("LIMIT 1"));
    }

    @Override
    public MessageSceneRespVO get(Long id) {
        MessageSceneDO scene = validateExists(id);
        return BeanUtils.toBean(scene, MessageSceneRespVO.class);
    }

    @Override
    public Long create(MessageSceneSaveReqVO reqVO) {
        MessageSceneDO scene = BeanUtils.toBean(reqVO, MessageSceneDO.class);
        messageSceneMapper.insert(scene);
        return scene.getId();
    }

    @Override
    public void update(MessageSceneSaveReqVO reqVO) {
        validateExists(reqVO.getId());
        messageSceneMapper.updateById(BeanUtils.toBean(reqVO, MessageSceneDO.class));
    }

    @Override
    public void updateStatus(Long id, String status) {
        validateExists(id);
        messageSceneMapper.updateById(MessageSceneDO.builder().id(id).status(status).build());
    }

    private MessageSceneDO validateExists(Long id) {
        MessageSceneDO scene = messageSceneMapper.selectById(id);
        if (scene == null) {
            throw exception(MESSAGE_SCENE_NOT_EXISTS);
        }
        return scene;
    }
}
