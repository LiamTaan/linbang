package cn.iocoder.yudao.module.linbang.controller.admin.reviewcomment.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 评价 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ReviewCommentRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "27657")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "25795")
    private Long orderId;

    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    private String orderNo;

    @Schema(description = "单元ID", example = "18837")
    private Long unitId;

    @Schema(description = "单元号")
    @ExcelProperty("单元号")
    private String unitNo;

    @Schema(description = "评价发起人", requiredMode = Schema.RequiredMode.REQUIRED, example = "351")
    @ExcelProperty("评价发起人")
    private Long fromUserId;

    @Schema(description = "发起人编号", example = "LBU123456")
    @ExcelProperty("发起人编号")
    private String fromUserNo;

    @Schema(description = "发起人昵称", example = "评价人")
    @ExcelProperty("发起人昵称")
    private String fromUserNickname;

    @Schema(description = "发起人手机号", example = "13800138000")
    @ExcelProperty("发起人手机号")
    private String fromUserMobile;

    @Schema(description = "评价目标用户", requiredMode = Schema.RequiredMode.REQUIRED, example = "219")
    @ExcelProperty("评价目标用户")
    private Long toUserId;

    @Schema(description = "目标用户编号", example = "LBU654321")
    @ExcelProperty("目标用户编号")
    private String toUserNo;

    @Schema(description = "目标用户昵称", example = "被评价人")
    @ExcelProperty("目标用户昵称")
    private String toUserNickname;

    @Schema(description = "目标用户手机号", example = "13800138001")
    @ExcelProperty("目标用户手机号")
    private String toUserMobile;

    @Schema(description = "星级", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("星级")
    private Integer starLevel;

    @Schema(description = "评价内容")
    @ExcelProperty("评价内容")
    private String content;

    @Schema(description = "是否自动评价", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否自动评价")
    private Boolean isAutoReview;

    @Schema(description = "自动评价文字是否已补充")
    @ExcelProperty("内容已补充")
    private Boolean isContentSupplemented;

    @Schema(description = "评价可编辑截止时间")
    @ExcelProperty("编辑截止时间")
    private LocalDateTime editDeadlineTime;

    @Schema(description = "最后编辑时间")
    @ExcelProperty("最后编辑时间")
    private LocalDateTime lastEditTime;

    @Schema(description = "编辑次数")
    @ExcelProperty("编辑次数")
    private Integer editCount;

    @Schema(description = OpenApiSchemaConstants.REVIEW_STATUS, requiredMode = Schema.RequiredMode.REQUIRED,
            example = "ENABLE")
    @ExcelProperty("状态")
    private String status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
