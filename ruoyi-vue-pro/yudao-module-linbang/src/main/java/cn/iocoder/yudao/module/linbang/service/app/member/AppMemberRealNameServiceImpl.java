package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo.AppMemberRealNameCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo.AppMemberRealNameRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

@Service
@Validated
public class AppMemberRealNameServiceImpl implements AppMemberRealNameService {

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MemberUserRealNameMapper memberUserRealNameMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrUpdateRealName(Long authUserId, AppMemberRealNameCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MemberUserRealNameDO existed = memberUserRealNameMapper.selectByUserId(loginUser.getId());
        if (existed == null) {
            MemberUserRealNameDO record = MemberUserRealNameDO.builder()
                    .userId(loginUser.getId())
                    .realName(reqVO.getRealName())
                    .idCardNo(reqVO.getIdCardNo())
                    .idCardFrontFileId(reqVO.getIdCardFrontFileId())
                    .idCardBackFileId(reqVO.getIdCardBackFileId())
                    .holdCardFileId(reqVO.getHoldCardFileId())
                    .auditStatus("PENDING")
                    .build();
            memberUserRealNameMapper.insert(record);
            return record.getId();
        }

        memberUserRealNameMapper.updateById(MemberUserRealNameDO.builder()
                .id(existed.getId())
                .realName(reqVO.getRealName())
                .idCardNo(reqVO.getIdCardNo())
                .idCardFrontFileId(reqVO.getIdCardFrontFileId())
                .idCardBackFileId(reqVO.getIdCardBackFileId())
                .holdCardFileId(reqVO.getHoldCardFileId())
                .livenessResult(null)
                .faceVerifyResult(null)
                .auditStatus("PENDING")
                .auditRemark(null)
                .auditBy(null)
                .auditTime(null)
                .rejectReason(null)
                .build());
        return existed.getId();
    }

    @Override
    public AppMemberRealNameRespVO getRealName(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MemberUserRealNameDO record = memberUserRealNameMapper.selectByUserId(loginUser.getId());
        if (record == null) {
            return null;
        }
        AppMemberRealNameRespVO respVO = new AppMemberRealNameRespVO();
        respVO.setId(record.getId());
        respVO.setUserId(record.getUserId());
        respVO.setRealName(record.getRealName());
        respVO.setIdCardNo(record.getIdCardNo());
        respVO.setIdCardFrontFileId(record.getIdCardFrontFileId());
        respVO.setIdCardBackFileId(record.getIdCardBackFileId());
        respVO.setHoldCardFileId(record.getHoldCardFileId());
        respVO.setLivenessResult(record.getLivenessResult());
        respVO.setFaceVerifyResult(record.getFaceVerifyResult());
        respVO.setAuditStatus(record.getAuditStatus());
        respVO.setAuditRemark(record.getAuditRemark());
        respVO.setRejectReason(record.getRejectReason());
        respVO.setCreateTime(record.getCreateTime());
        return respVO;
    }
}
