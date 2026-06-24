package cn.iocoder.yudao.module.linbang.dal.mysql.helpfeedback;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfeedback.vo.HelpFeedbackPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.help.feedback.vo.AppHelpFeedbackPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.helpfeedback.HelpFeedbackDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HelpFeedbackMapper extends BaseMapperX<HelpFeedbackDO> {

    default PageResult<HelpFeedbackDO> selectAdminPage(HelpFeedbackPageReqVO reqVO, List<Long> matchedUserIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<HelpFeedbackDO>()
                .inIfPresent(HelpFeedbackDO::getUserId, matchedUserIds)
                .eqIfPresent(HelpFeedbackDO::getFeedbackType, reqVO.getFeedbackType())
                .eqIfPresent(HelpFeedbackDO::getContactMobile, reqVO.getContactMobile())
                .eqIfPresent(HelpFeedbackDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(HelpFeedbackDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(HelpFeedbackDO::getId));
    }

    default PageResult<HelpFeedbackDO> selectAppPage(Long userId, AppHelpFeedbackPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<HelpFeedbackDO>()
                .eq(HelpFeedbackDO::getUserId, userId)
                .eqIfPresent(HelpFeedbackDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(HelpFeedbackDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(HelpFeedbackDO::getId));
    }
}
