package cn.iocoder.yudao.module.linbang.service.memberrealname;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import org.springframework.stereotype.Service;

@Service
public class IdentityVerifyFacadeImpl implements IdentityVerifyFacade {

    private static final String PROVIDER = "LOCAL_STUB";

    @Override
    public IdentityVerifyResult startVerify(MemberUserRealNameDO realName) {
        String verifyStatus = "PASS";
        String failReason = null;
        if (realName.getHoldCardVideoFileId() == null || realName.getIdCardFrontFileId() == null
                || realName.getIdCardBackFileId() == null || realName.getHoldCardFileId() == null) {
            verifyStatus = "FAIL";
            failReason = "实名认证资料不完整，请补齐身份证正反面、手持照片和手持视频";
        }
        return new IdentityVerifyResult(PROVIDER, "VERIFY_" + IdUtil.getSnowflakeNextIdStr(),
                "PASS".equals(verifyStatus) ? "PASS" : "FAIL",
                "PASS".equals(verifyStatus) ? "PASS" : "FAIL",
                verifyStatus, failReason);
    }
}
