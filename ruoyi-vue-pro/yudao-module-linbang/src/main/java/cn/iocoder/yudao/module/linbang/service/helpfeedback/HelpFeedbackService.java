package cn.iocoder.yudao.module.linbang.service.helpfeedback;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfeedback.vo.HelpFeedbackDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfeedback.vo.HelpFeedbackPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfeedback.vo.HelpFeedbackRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.help.feedback.vo.AppHelpFeedbackCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.help.feedback.vo.AppHelpFeedbackPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.helpfeedback.HelpFeedbackDO;

public interface HelpFeedbackService {

    Long createHelpFeedback(Long userId, AppHelpFeedbackCreateReqVO reqVO);

    PageResult<HelpFeedbackDO> getAppHelpFeedbackPage(Long userId, AppHelpFeedbackPageReqVO reqVO);

    HelpFeedbackDO getAppHelpFeedback(Long userId, Long id);

    PageResult<HelpFeedbackRespVO> getAdminHelpFeedbackPage(HelpFeedbackPageReqVO reqVO);

    HelpFeedbackDO getHelpFeedback(Long id);

    HelpFeedbackDetailRespVO getHelpFeedbackDetail(Long id);
}
