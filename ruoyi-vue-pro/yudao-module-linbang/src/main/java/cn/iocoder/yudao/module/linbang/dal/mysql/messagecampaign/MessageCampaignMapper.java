package cn.iocoder.yudao.module.linbang.dal.mysql.messagecampaign;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo.MessageCampaignPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageCampaignPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagecampaign.MessageCampaignDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageCampaignMapper extends BaseMapperX<MessageCampaignDO> {

    default PageResult<MessageCampaignDO> selectPage(MessageCampaignPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MessageCampaignDO>()
                .likeIfPresent(MessageCampaignDO::getCampaignName, reqVO.getCampaignName())
                .eqIfPresent(MessageCampaignDO::getSourceType, reqVO.getSourceType())
                .eqIfPresent(MessageCampaignDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(MessageCampaignDO::getExecuteStatus, reqVO.getExecuteStatus())
                .eqIfPresent(MessageCampaignDO::getTargetMode, reqVO.getTargetMode())
                .eqIfPresent(MessageCampaignDO::getSceneCode, reqVO.getSceneCode())
                .eqIfPresent(MessageCampaignDO::getMessageCategory, reqVO.getMessageCategory())
                .betweenIfPresent(MessageCampaignDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MessageCampaignDO::getId));
    }

    default PageResult<MessageCampaignDO> selectAppPage(Long applicantUserId, AppMessageCampaignPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MessageCampaignDO>()
                .eq(MessageCampaignDO::getApplicantUserId, applicantUserId)
                .eqIfPresent(MessageCampaignDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(MessageCampaignDO::getExecuteStatus, reqVO.getExecuteStatus())
                .orderByDesc(MessageCampaignDO::getId));
    }
}
