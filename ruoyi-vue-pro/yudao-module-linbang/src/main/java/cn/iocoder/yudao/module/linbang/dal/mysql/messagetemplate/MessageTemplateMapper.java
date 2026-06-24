package cn.iocoder.yudao.module.linbang.dal.mysql.messagetemplate;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate.vo.MessageTemplatePageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagetemplate.MessageTemplateDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageTemplateMapper extends BaseMapperX<MessageTemplateDO> {

    default PageResult<MessageTemplateDO> selectPage(MessageTemplatePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MessageTemplateDO>()
                .likeIfPresent(MessageTemplateDO::getTemplateCode, reqVO.getTemplateCode())
                .likeIfPresent(MessageTemplateDO::getTemplateName, reqVO.getTemplateName())
                .eqIfPresent(MessageTemplateDO::getTemplateType, reqVO.getTemplateType())
                .eqIfPresent(MessageTemplateDO::getChannelType, reqVO.getChannelType())
                .eqIfPresent(MessageTemplateDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MessageTemplateDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MessageTemplateDO::getId));
    }
}
