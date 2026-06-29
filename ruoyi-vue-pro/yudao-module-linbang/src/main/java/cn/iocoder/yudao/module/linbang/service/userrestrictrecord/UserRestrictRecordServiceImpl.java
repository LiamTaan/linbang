package cn.iocoder.yudao.module.linbang.service.userrestrictrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.userrestrictrecord.vo.UserRestrictRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.userrestrictrecord.vo.UserRestrictRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.userrestrictrecord.vo.UserRestrictRecordRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userrestrictrecord.UserRestrictRecordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.userrestrictrecord.UserRestrictRecordMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.USER_RESTRICT_RECORD_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
public class UserRestrictRecordServiceImpl implements UserRestrictRecordService {

    @Resource
    private UserRestrictRecordMapper userRestrictRecordMapper;
    @Resource
    private MemberUserMapper memberUserMapper;

    @Override
    public PageResult<UserRestrictRecordRespVO> getPage(UserRestrictRecordPageReqVO reqVO) {
        List<Long> userIds = reqVO.getUserKeyword() == null ? null
                : convertList(memberUserMapper.selectListByKeyword(reqVO.getUserKeyword()), MemberUserDO::getId);
        PageResult<UserRestrictRecordDO> page = userRestrictRecordMapper.selectPage(reqVO, userIds);
        List<UserRestrictRecordRespVO> list = BeanUtils.toBean(page.getList(), UserRestrictRecordRespVO.class);
        Map<Long, MemberUserDO> userMap = convertSet(list, UserRestrictRecordRespVO::getUserId, item -> item.getUserId() != null).isEmpty()
                ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(convertSet(list, UserRestrictRecordRespVO::getUserId, item -> item.getUserId() != null)),
                MemberUserDO::getId);
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

    @Override
    public UserRestrictRecordDetailRespVO getDetail(Long id) {
        UserRestrictRecordDO record = userRestrictRecordMapper.selectById(id);
        if (record == null) {
            throw exception(USER_RESTRICT_RECORD_NOT_EXISTS);
        }
        UserRestrictRecordDetailRespVO respVO = BeanUtils.toBean(record, UserRestrictRecordDetailRespVO.class);
        fillUserFields(Collections.singletonList(respVO));
        return respVO;
    }

    private void fillUserFields(List<? extends UserRestrictRecordRespVO> list) {
        Map<Long, MemberUserDO> userMap = convertSet(list, UserRestrictRecordRespVO::getUserId, item -> item.getUserId() != null).isEmpty()
                ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(convertSet(list, UserRestrictRecordRespVO::getUserId, item -> item.getUserId() != null)),
                MemberUserDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user != null) {
                item.setUserNo(user.getUserNo());
                item.setUserNickname(user.getNickname());
                item.setUserMobile(user.getMobile());
            }
        });
    }
}
