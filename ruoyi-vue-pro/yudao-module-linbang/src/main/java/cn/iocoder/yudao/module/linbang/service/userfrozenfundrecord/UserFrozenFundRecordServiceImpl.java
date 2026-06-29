package cn.iocoder.yudao.module.linbang.service.userfrozenfundrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.userfrozenfundrecord.vo.UserFrozenFundRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.userfrozenfundrecord.vo.UserFrozenFundRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.userfrozenfundrecord.vo.UserFrozenFundRecordRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userfrozenfundrecord.UserFrozenFundRecordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.userfrozenfundrecord.UserFrozenFundRecordMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.USER_FROZEN_FUND_RECORD_NOT_EXISTS;

@Service
public class UserFrozenFundRecordServiceImpl implements UserFrozenFundRecordService {

    @Resource
    private UserFrozenFundRecordMapper userFrozenFundRecordMapper;
    @Resource
    private MemberUserMapper memberUserMapper;

    @Override
    public PageResult<UserFrozenFundRecordRespVO> getPage(UserFrozenFundRecordPageReqVO reqVO) {
        List<Long> userIds = reqVO.getUserKeyword() == null ? null
                : convertList(memberUserMapper.selectListByKeyword(reqVO.getUserKeyword()), MemberUserDO::getId);
        PageResult<UserFrozenFundRecordDO> page = userFrozenFundRecordMapper.selectPage(reqVO, userIds);
        List<UserFrozenFundRecordRespVO> list = BeanUtils.toBean(page.getList(), UserFrozenFundRecordRespVO.class);
        Map<Long, MemberUserDO> userMap = convertSet(list, UserFrozenFundRecordRespVO::getUserId, item -> item.getUserId() != null).isEmpty()
                ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(convertSet(list, UserFrozenFundRecordRespVO::getUserId, item -> item.getUserId() != null)),
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
    public UserFrozenFundRecordDetailRespVO getDetail(Long id) {
        UserFrozenFundRecordDO record = userFrozenFundRecordMapper.selectById(id);
        if (record == null) {
            throw exception(USER_FROZEN_FUND_RECORD_NOT_EXISTS);
        }
        UserFrozenFundRecordDetailRespVO respVO = BeanUtils.toBean(record, UserFrozenFundRecordDetailRespVO.class);
        fillUserFields(Collections.singletonList(respVO));
        return respVO;
    }

    private void fillUserFields(List<? extends UserFrozenFundRecordRespVO> list) {
        Map<Long, MemberUserDO> userMap = convertSet(list, UserFrozenFundRecordRespVO::getUserId, item -> item.getUserId() != null).isEmpty()
                ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(convertSet(list, UserFrozenFundRecordRespVO::getUserId, item -> item.getUserId() != null)),
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
