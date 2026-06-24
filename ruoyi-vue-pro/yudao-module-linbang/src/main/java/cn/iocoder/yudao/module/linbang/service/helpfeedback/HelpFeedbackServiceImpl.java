package cn.iocoder.yudao.module.linbang.service.helpfeedback;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfeedback.vo.HelpFeedbackDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfeedback.vo.HelpFeedbackPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfeedback.vo.HelpFeedbackRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.help.feedback.vo.AppHelpFeedbackCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.help.feedback.vo.AppHelpFeedbackPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.helpfeedback.HelpFeedbackDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.helpfeedback.HelpFeedbackMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.HELP_FEEDBACK_NOT_EXISTS;

@Service
@Validated
public class HelpFeedbackServiceImpl implements HelpFeedbackService {

    @Resource
    private HelpFeedbackMapper helpFeedbackMapper;
    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MemberUserMapper memberUserMapper;

    @Override
    public Long createHelpFeedback(Long userId, AppHelpFeedbackCreateReqVO reqVO) {
        memberUserService.getOrCreateMemberUser(userId);
        HelpFeedbackDO feedback = BeanUtils.toBean(reqVO, HelpFeedbackDO.class);
        feedback.setUserId(userId);
        feedback.setStatus("PENDING");
        helpFeedbackMapper.insert(feedback);
        return feedback.getId();
    }

    @Override
    public PageResult<HelpFeedbackDO> getAppHelpFeedbackPage(Long userId, AppHelpFeedbackPageReqVO reqVO) {
        return helpFeedbackMapper.selectAppPage(userId, reqVO);
    }

    @Override
    public HelpFeedbackDO getAppHelpFeedback(Long userId, Long id) {
        HelpFeedbackDO feedback = getHelpFeedback(id);
        if (!userId.equals(feedback.getUserId())) {
            throw exception(HELP_FEEDBACK_NOT_EXISTS);
        }
        return feedback;
    }

    @Override
    public PageResult<HelpFeedbackRespVO> getAdminHelpFeedbackPage(HelpFeedbackPageReqVO reqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(reqVO.getUserKeyword());
        if (StrUtil.isNotBlank(reqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<HelpFeedbackDO> pageResult = helpFeedbackMapper.selectAdminPage(reqVO, matchedUserIds);
        List<HelpFeedbackRespVO> list = BeanUtils.toBean(pageResult.getList(), HelpFeedbackRespVO.class);
        fillUserDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public HelpFeedbackDO getHelpFeedback(Long id) {
        HelpFeedbackDO feedback = helpFeedbackMapper.selectById(id);
        if (feedback == null) {
            throw exception(HELP_FEEDBACK_NOT_EXISTS);
        }
        return feedback;
    }

    @Override
    public HelpFeedbackDetailRespVO getHelpFeedbackDetail(Long id) {
        HelpFeedbackDO feedback = getHelpFeedback(id);
        MemberUserDO user = feedback.getUserId() == null ? null : memberUserMapper.selectById(feedback.getUserId());
        List<HelpFeedbackDO> sameUserFeedbacks = feedback.getUserId() == null ? Collections.emptyList()
                : helpFeedbackMapper.selectList(new LambdaQueryWrapperX<HelpFeedbackDO>()
                .eq(HelpFeedbackDO::getUserId, feedback.getUserId())
                .orderByDesc(HelpFeedbackDO::getId));
        List<HelpFeedbackDO> sameTypeFeedbacks = feedback.getFeedbackType() == null ? Collections.emptyList()
                : helpFeedbackMapper.selectList(new LambdaQueryWrapperX<HelpFeedbackDO>()
                .eq(HelpFeedbackDO::getFeedbackType, feedback.getFeedbackType())
                .orderByDesc(HelpFeedbackDO::getId));
        return HelpFeedbackDetailAssembler.build(feedback, user, sameUserFeedbacks, sameTypeFeedbacks);
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private void fillUserDisplayInfo(List<HelpFeedbackRespVO> list) {
        java.util.Set<Long> userIds = convertSet(list, HelpFeedbackRespVO::getUserId,
                item -> item.getUserId() != null);
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(userIds), MemberUserDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user == null) {
                return;
            }
            item.setUserNo(user.getUserNo());
            item.setUserNickname(user.getNickname());
            item.setUserMobile(user.getMobile());
        });
    }
}
