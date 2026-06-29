package cn.iocoder.yudao.module.linbang.service.memberpoint;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberpoint.MemberPointRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberpoint.MemberPointRecordMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

@Service
@Validated
public class MemberPointRecordServiceImpl implements MemberPointRecordService {

    @Resource
    private MemberPointRecordMapper memberPointRecordMapper;
    @Resource
    private MemberUserService memberUserService;

    @Override
    public Integer getUserPointBalance(Long userId) {
        MemberUserDO user = memberUserService.getOrCreateMemberUser(userId);
        return user.getPointBalance() == null ? 0 : user.getPointBalance();
    }

    @Override
    public PageResult<MemberPointRecordDO> getPointRecordPage(Long userId, PageParam pageParam) {
        memberUserService.getOrCreateMemberUser(userId);
        return memberPointRecordMapper.selectPageByUserId(userId, pageParam);
    }
}
