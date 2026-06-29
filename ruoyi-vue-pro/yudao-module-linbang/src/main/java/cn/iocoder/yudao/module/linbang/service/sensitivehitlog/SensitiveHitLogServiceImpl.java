package cn.iocoder.yudao.module.linbang.service.sensitivehitlog;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitivehitlog.vo.SensitiveHitLogDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitivehitlog.vo.SensitiveHitLogPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitivehitlog.vo.SensitiveHitLogRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.sensitivehitlog.SensitiveHitLogDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.sensitivehitlog.SensitiveHitLogMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.SENSITIVE_HIT_LOG_NOT_EXISTS;

@Service
public class SensitiveHitLogServiceImpl implements SensitiveHitLogService {

    @Resource
    private SensitiveHitLogMapper sensitiveHitLogMapper;
    @Resource
    private MemberUserMapper memberUserMapper;

    @Override
    public PageResult<SensitiveHitLogRespVO> getPage(SensitiveHitLogPageReqVO reqVO) {
        List<Long> userIds = reqVO.getUserKeyword() == null ? null
                : convertList(memberUserMapper.selectListByKeyword(reqVO.getUserKeyword()), MemberUserDO::getId);
        PageResult<SensitiveHitLogDO> page = sensitiveHitLogMapper.selectPage(reqVO, userIds);
        List<SensitiveHitLogRespVO> list = BeanUtils.toBean(page.getList(), SensitiveHitLogRespVO.class);
        Map<Long, MemberUserDO> userMap = convertSet(list, SensitiveHitLogRespVO::getUserId, item -> item.getUserId() != null).isEmpty()
                ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(convertSet(list, SensitiveHitLogRespVO::getUserId, item -> item.getUserId() != null)),
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
    public SensitiveHitLogDetailRespVO getDetail(Long id) {
        SensitiveHitLogDO record = sensitiveHitLogMapper.selectById(id);
        if (record == null) {
            throw exception(SENSITIVE_HIT_LOG_NOT_EXISTS);
        }
        SensitiveHitLogDetailRespVO respVO = BeanUtils.toBean(record, SensitiveHitLogDetailRespVO.class);
        Map<Long, MemberUserDO> userMap = convertSet(Collections.singletonList(respVO), SensitiveHitLogRespVO::getUserId,
                item -> item.getUserId() != null).isEmpty()
                ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(convertSet(Collections.singletonList(respVO),
                SensitiveHitLogRespVO::getUserId, item -> item.getUserId() != null)), MemberUserDO::getId);
        MemberUserDO user = userMap.get(respVO.getUserId());
        if (user != null) {
            respVO.setUserNo(user.getUserNo());
            respVO.setUserNickname(user.getNickname());
            respVO.setUserMobile(user.getMobile());
        }
        return respVO;
    }
}
