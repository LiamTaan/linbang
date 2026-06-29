package cn.iocoder.yudao.module.linbang.service.userdevice;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.userdevice.vo.UserDevicePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.userdevice.vo.UserDeviceRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userdevice.UserDeviceDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.userdevice.UserDeviceMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Service
public class UserDeviceServiceImpl implements UserDeviceService {

    @Resource
    private UserDeviceMapper userDeviceMapper;
    @Resource
    private MemberUserMapper memberUserMapper;

    @Override
    public PageResult<UserDeviceRespVO> getPage(UserDevicePageReqVO reqVO) {
        List<Long> userIds = reqVO.getUserId() != null ? null : null;
        PageResult<UserDeviceDO> page = userDeviceMapper.selectPage(reqVO, userIds);
        List<UserDeviceRespVO> list = BeanUtils.toBean(page.getList(), UserDeviceRespVO.class);
        Map<Long, MemberUserDO> userMap = convertSet(list, UserDeviceRespVO::getUserId, item -> item.getUserId() != null).isEmpty()
                ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(convertSet(list, UserDeviceRespVO::getUserId, item -> item.getUserId() != null)),
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
}
