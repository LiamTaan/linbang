package cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberRoleApplyRespVO {

    private Long id;

    private Long userId;

    private String userNo;

    private String userNickname;

    private String userMobile;

    private String applyRoleCode;

    private String applyReason;

    private String resourceDesc;

    private String auditStatus;

    private String auditRemark;

    private String rejectReason;

    private Long auditBy;

    private LocalDateTime auditTime;

    private LocalDateTime createTime;
}
