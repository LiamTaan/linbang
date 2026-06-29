package cn.iocoder.yudao.module.linbang.service.punishlog;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.punishlog.vo.PunishLogDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.punishlog.vo.PunishLogPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.punishlog.vo.PunishLogRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.punishlog.PunishLogDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.punishlog.PunishLogMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PUNISH_LOG_NOT_EXISTS;

@Service
public class PunishLogServiceImpl implements PunishLogService {

    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private PunishLogMapper punishLogMapper;
    @Resource
    private PunishLogWriteService punishLogWriteService;

    @Override
    public PageResult<PunishLogRespVO> getPage(PunishLogPageReqVO reqVO) {
        PageResult<PunishLogDO> page = punishLogMapper.selectPage(reqVO);
        List<PunishLogRespVO> logs = BeanUtils.toBean(page.getList(), PunishLogRespVO.class);
        logs.forEach(item -> item.setRecordId(item.getId()));
        Map<Long, MemberUserDO> userMap = buildUserMap(logs);
        logs.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user != null) {
                item.setUserNo(user.getUserNo());
                item.setUserNickname(user.getNickname());
                item.setUserMobile(user.getMobile());
            }
        });
        return new PageResult<>(logs, page.getTotal());
    }

    @Override
    public PunishLogDetailRespVO getDetail(Long id) {
        PunishLogDO record = punishLogMapper.selectById(id);
        if (record == null) {
            throw exception(PUNISH_LOG_NOT_EXISTS);
        }
        PunishLogDetailRespVO respVO = BeanUtils.toBean(record, PunishLogDetailRespVO.class);
        respVO.setRecordId(respVO.getId());
        Map<Long, MemberUserDO> userMap = buildUserMap(Collections.singletonList(respVO));
        MemberUserDO user = userMap.get(respVO.getUserId());
        if (user != null) {
            respVO.setUserNo(user.getUserNo());
            respVO.setUserNickname(user.getNickname());
            respVO.setUserMobile(user.getMobile());
        }
        return respVO;
    }

    private Map<Long, MemberUserDO> buildUserMap(List<PunishLogRespVO> logs) {
        java.util.Set<Long> userIds = convertSet(logs, PunishLogRespVO::getUserId, item -> item.getUserId() != null);
        return userIds.isEmpty() ? java.util.Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(userIds), MemberUserDO::getId);
    }
}
