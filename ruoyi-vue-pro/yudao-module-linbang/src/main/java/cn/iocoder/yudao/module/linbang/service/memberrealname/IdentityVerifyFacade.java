package cn.iocoder.yudao.module.linbang.service.memberrealname;

import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;

public interface IdentityVerifyFacade {

    IdentityVerifyResult startVerify(MemberUserRealNameDO realName);

    class IdentityVerifyResult {
        private final String verifyProvider;
        private final String verifyFlowNo;
        private final String livenessStatus;
        private final String faceVerifyStatus;
        private final String verifyStatus;
        private final String failReason;

        public IdentityVerifyResult(String verifyProvider, String verifyFlowNo, String livenessStatus,
                                    String faceVerifyStatus, String verifyStatus, String failReason) {
            this.verifyProvider = verifyProvider;
            this.verifyFlowNo = verifyFlowNo;
            this.livenessStatus = livenessStatus;
            this.faceVerifyStatus = faceVerifyStatus;
            this.verifyStatus = verifyStatus;
            this.failReason = failReason;
        }

        public String getVerifyProvider() {
            return verifyProvider;
        }

        public String getVerifyFlowNo() {
            return verifyFlowNo;
        }

        public String getLivenessStatus() {
            return livenessStatus;
        }

        public String getFaceVerifyStatus() {
            return faceVerifyStatus;
        }

        public String getVerifyStatus() {
            return verifyStatus;
        }

        public String getFailReason() {
            return failReason;
        }
    }
}
