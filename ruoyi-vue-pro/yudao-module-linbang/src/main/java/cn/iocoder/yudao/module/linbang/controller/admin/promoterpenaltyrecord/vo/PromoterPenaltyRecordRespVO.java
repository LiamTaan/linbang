package cn.iocoder.yudao.module.linbang.controller.admin.promoterpenaltyrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 推广员处罚记录 Response VO")
@Data
public class PromoterPenaltyRecordRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;
    private Long promoterId;
    private Long userId;
    private String userNo;
    private String userNickname;
    private String userMobile;
    private Long contentId;
    private String contentTitle;
    private String penaltyAction;
    private Integer scoreChange;
    private String reason;
    private String status;
    private LocalDateTime createTime;
}
