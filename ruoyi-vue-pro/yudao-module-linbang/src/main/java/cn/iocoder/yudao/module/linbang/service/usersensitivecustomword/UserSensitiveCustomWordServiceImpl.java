package cn.iocoder.yudao.module.linbang.service.usersensitivecustomword;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.usersensitivecustomword.vo.UserSensitiveCustomWordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.usersensitivecustomword.vo.UserSensitiveCustomWordRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.usersensitivecustomword.UserSensitiveCustomWordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.usersensitivecustomword.UserSensitiveCustomWordMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Service
public class UserSensitiveCustomWordServiceImpl implements UserSensitiveCustomWordService {

    @Resource
    private UserSensitiveCustomWordMapper userSensitiveCustomWordMapper;
    @Resource
    private MemberUserMapper memberUserMapper;

    @Override
    public PageResult<UserSensitiveCustomWordRespVO> getPage(UserSensitiveCustomWordPageReqVO reqVO) {
        List<Long> userIds = reqVO.getUserKeyword() == null ? null
                : convertList(memberUserMapper.selectListByKeyword(reqVO.getUserKeyword()), MemberUserDO::getId);
        PageResult<UserSensitiveCustomWordDO> page = userSensitiveCustomWordMapper.selectAdminPage(reqVO, userIds);
        List<UserSensitiveCustomWordRespVO> list = BeanUtils.toBean(page.getList(), UserSensitiveCustomWordRespVO.class);
        Map<Long, MemberUserDO> userMap = convertSet(list, UserSensitiveCustomWordRespVO::getUserId,
                item -> item.getUserId() != null).isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(convertSet(list, UserSensitiveCustomWordRespVO::getUserId,
                item -> item.getUserId() != null)), MemberUserDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user != null) {
                item.setUserNo(user.getUserNo());
                item.setUserNickname(user.getNickname());
                item.setUserMobile(user.getMobile());
            }
        });
        return new PageResult<>(list, page.getTotal());
    }
}
