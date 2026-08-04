package cn.iocoder.yudao.module.linbang.controller.admin.userfrozenfundrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "管理后台 - 用户资金冻结 Response VO")
public class UserFrozenFundRecordDetailRespVO extends UserFrozenFundRecordRespVO {

    @Schema(description = "解冻操作人后台用户 ID")
    private Long releasedBy;
    @Schema(description = "资金解冻时间")
    private LocalDateTime releasedTime;
    @Schema(description = "解冻备注")
    private String releaseRemark;
}
