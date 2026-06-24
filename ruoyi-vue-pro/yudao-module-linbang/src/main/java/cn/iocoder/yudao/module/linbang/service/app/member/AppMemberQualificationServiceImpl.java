package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class AppMemberQualificationServiceImpl implements AppMemberQualificationService {

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MemberUserQualificationMapper memberUserQualificationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createQualification(Long authUserId, @Valid AppMemberQualificationCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MemberUserQualificationDO qualification = MemberUserQualificationDO.builder()
                .userId(loginUser.getId())
                .qualificationType(reqVO.getQualificationType())
                .qualificationName(reqVO.getQualificationName())
                .qualificationNo(reqVO.getQualificationNo())
                .fileId(reqVO.getFileId())
                .validStartDate(reqVO.getValidStartDate())
                .validEndDate(reqVO.getValidEndDate())
                .auditStatus("PENDING")
                .build();
        memberUserQualificationMapper.insert(qualification);
        return qualification.getId();
    }

    @Override
    public PageResult<AppMemberQualificationRespVO> getQualificationPage(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        List<AppMemberQualificationRespVO> list = memberUserQualificationMapper.selectListByUserId(loginUser.getId())
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
        return new PageResult<>(list, (long) list.size());
    }

    private AppMemberQualificationRespVO convert(MemberUserQualificationDO qualification) {
        AppMemberQualificationRespVO respVO = new AppMemberQualificationRespVO();
        respVO.setId(qualification.getId());
        respVO.setUserId(qualification.getUserId());
        respVO.setQualificationType(qualification.getQualificationType());
        respVO.setQualificationName(qualification.getQualificationName());
        respVO.setQualificationNo(qualification.getQualificationNo());
        respVO.setFileId(qualification.getFileId());
        respVO.setValidStartDate(qualification.getValidStartDate());
        respVO.setValidEndDate(qualification.getValidEndDate());
        respVO.setAuditStatus(qualification.getAuditStatus());
        respVO.setAuditRemark(qualification.getAuditRemark());
        respVO.setRejectReason(qualification.getRejectReason());
        respVO.setCreateTime(qualification.getCreateTime());
        return respVO;
    }

}
