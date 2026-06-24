package cn.iocoder.yudao.module.linbang.service.blacklist;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.blacklist.vo.BlacklistDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.blacklist.vo.BlacklistPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.blacklist.vo.BlacklistRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.blacklist.BlacklistDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.riskevent.RiskEventDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.blacklist.BlacklistMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.riskevent.RiskEventMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.BLACKLIST_NOT_EXISTS;

@Service
@Validated
public class BlacklistServiceImpl implements BlacklistService {

    @Resource
    private BlacklistMapper blacklistMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private RiskEventMapper riskEventMapper;

    @Override
    public BlacklistDetailRespVO getBlacklistDetail(Long id) {
        BlacklistDO blacklist = blacklistMapper.selectById(id);
        if (blacklist == null) {
            throw exception(BLACKLIST_NOT_EXISTS);
        }
        MemberUserDO user = memberUserMapper.selectById(blacklist.getUserId());
        List<RiskEventDO> recentRiskEvents = riskEventMapper.selectList(new LambdaQueryWrapperX<RiskEventDO>()
                .eq(RiskEventDO::getBizType, "USER")
                .eq(RiskEventDO::getBizId, blacklist.getUserId())
                .orderByDesc(RiskEventDO::getCreateTime, RiskEventDO::getId)
                .last("LIMIT 10"));

        BlacklistDetailRespVO respVO = BeanUtils.toBean(blacklist, BlacklistDetailRespVO.class);
        respVO.setUser(BlacklistDetailAssembler.buildUser(user));
        respVO.setRecentRiskEvents(BlacklistDetailAssembler.buildRiskEvents(recentRiskEvents));
        return respVO;
    }

    @Override
    public PageResult<BlacklistRespVO> getBlacklistPage(BlacklistPageReqVO reqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(reqVO.getUserKeyword());
        if (StrUtil.isNotBlank(reqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<BlacklistDO> pageResult = blacklistMapper.selectPage(reqVO, matchedUserIds);
        List<BlacklistRespVO> list = BeanUtils.toBean(pageResult.getList(), BlacklistRespVO.class);
        fillUserDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private void fillUserDisplayInfo(List<BlacklistRespVO> list) {
        java.util.Set<Long> userIds = convertSet(list, BlacklistRespVO::getUserId,
                item -> item.getUserId() != null);
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? java.util.Collections.emptyMap()
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
