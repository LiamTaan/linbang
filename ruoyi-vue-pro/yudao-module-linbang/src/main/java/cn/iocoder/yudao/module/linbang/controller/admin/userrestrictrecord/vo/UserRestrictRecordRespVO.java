package cn.iocoder.yudao.module.linbang.controller.admin.userrestrictrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户限制记录 Response VO")
@Data
public class UserRestrictRecordRespVO {

    private Long id;
    private Long userId;
    private String userNo;
    private String userNickname;
    private String userMobile;
    private String restrictType;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String sourceRuleCode;
    private String sourceBizType;
    private Long sourceBizId;
    private String reason;
    private LocalDateTime createTime;
}
