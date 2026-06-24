package cn.iocoder.yudao.module.linbang.service.helpfeedback;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfeedback.vo.HelpFeedbackDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.helpfeedback.HelpFeedbackDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class HelpFeedbackDetailAssembler {

    private HelpFeedbackDetailAssembler() {
    }

    static HelpFeedbackDetailRespVO build(HelpFeedbackDO feedback, MemberUserDO user, List<HelpFeedbackDO> sameUserFeedbacks,
                                          List<HelpFeedbackDO> sameTypeFeedbacks) {
        HelpFeedbackDetailRespVO respVO = BeanUtils.toBean(feedback, HelpFeedbackDetailRespVO.class);
        if (user != null) {
            respVO.setUser(BeanUtils.toBean(user, HelpFeedbackDetailRespVO.UserRespVO.class));
        }
        respVO.setSummary(buildSummary(sameUserFeedbacks, sameTypeFeedbacks));
        respVO.setRelatedFeedbacks(buildRelatedFeedbacks(sameUserFeedbacks, feedback.getId()));
        return respVO;
    }

    private static HelpFeedbackDetailRespVO.SummaryRespVO buildSummary(List<HelpFeedbackDO> sameUserFeedbacks,
                                                                       List<HelpFeedbackDO> sameTypeFeedbacks) {
        HelpFeedbackDetailRespVO.SummaryRespVO summary = new HelpFeedbackDetailRespVO.SummaryRespVO();
        summary.setSameUserFeedbackCount(sameUserFeedbacks == null ? 0 : sameUserFeedbacks.size());
        summary.setSameTypeFeedbackCount(sameTypeFeedbacks == null ? 0 : sameTypeFeedbacks.size());
        List<HelpFeedbackDO> source = sameUserFeedbacks == null ? Collections.emptyList() : sameUserFeedbacks;
        summary.setPendingCount((int) source.stream().filter(item -> "PENDING".equalsIgnoreCase(item.getStatus())).count());
        summary.setProcessingCount((int) source.stream().filter(item -> "PROCESSING".equalsIgnoreCase(item.getStatus())).count());
        summary.setFinishedCount((int) source.stream().filter(item -> "FINISHED".equalsIgnoreCase(item.getStatus())).count());
        return summary;
    }

    private static List<HelpFeedbackDetailRespVO.RelatedFeedbackRespVO> buildRelatedFeedbacks(List<HelpFeedbackDO> sameUserFeedbacks,
                                                                                               Long currentId) {
        if (sameUserFeedbacks == null || sameUserFeedbacks.isEmpty()) {
            return Collections.emptyList();
        }
        return sameUserFeedbacks.stream()
                .filter(item -> currentId == null || !currentId.equals(item.getId()))
                .limit(10)
                .map(item -> BeanUtils.toBean(item, HelpFeedbackDetailRespVO.RelatedFeedbackRespVO.class))
                .collect(Collectors.toList());
    }
}
