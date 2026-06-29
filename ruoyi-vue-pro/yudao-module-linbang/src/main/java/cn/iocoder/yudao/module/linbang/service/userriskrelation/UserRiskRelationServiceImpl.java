package cn.iocoder.yudao.module.linbang.service.userriskrelation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.userriskrelation.vo.UserRiskRelationPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.userriskrelation.vo.UserRiskRelationRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userriskrelation.UserRiskRelationDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.userriskrelation.UserRiskRelationMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Service
public class UserRiskRelationServiceImpl implements UserRiskRelationService {

    @Resource
    private UserRiskRelationMapper userRiskRelationMapper;
    @Resource
    private MemberUserMapper memberUserMapper;

    @Override
    public PageResult<UserRiskRelationRespVO> getPage(UserRiskRelationPageReqVO reqVO) {
        List<Long> userIds = null;
        PageResult<UserRiskRelationDO> page = userRiskRelationMapper.selectPage(reqVO, userIds);
        List<UserRiskRelationRespVO> list = BeanUtils.toBean(page.getList(), UserRiskRelationRespVO.class);
        Set<Long> allUserIds = convertSet(list, UserRiskRelationRespVO::getUserId, item -> item.getUserId() != null);
        allUserIds.addAll(convertSet(list, UserRiskRelationRespVO::getRelatedUserId, item -> item.getRelatedUserId() != null));
        Map<Long, MemberUserDO> userMap = allUserIds.isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(allUserIds), MemberUserDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user != null) {
                item.setUserNo(user.getUserNo());
                item.setUserNickname(user.getNickname());
                item.setUserMobile(user.getMobile());
            }
            MemberUserDO relatedUser = userMap.get(item.getRelatedUserId());
            if (relatedUser != null) {
                item.setRelatedUserNo(relatedUser.getUserNo());
                item.setRelatedUserNickname(relatedUser.getNickname());
                item.setRelatedUserMobile(relatedUser.getMobile());
            }
        });
        return new PageResult<>(list, page.getTotal());
    }
}
