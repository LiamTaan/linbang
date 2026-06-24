package cn.iocoder.yudao.module.linbang.controller.admin.creditrecord.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 信用记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CreditRecordRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "用户ID")
    @ExcelProperty("用户ID")
    private Long userId;

    @Schema(description = "用户编号", example = "LBU123456")
    @ExcelProperty("用户编号")
    private String userNo;

    @Schema(description = "用户昵称", example = "邻里用户")
    @ExcelProperty("用户昵称")
    private String userNickname;

    @Schema(description = "用户手机号", example = "13800138000")
    @ExcelProperty("用户手机号")
    private String userMobile;

    @Schema(description = "服务商ID")
    @ExcelProperty("服务商ID")
    private Long merchantId;

    @Schema(description = "服务商名称")
    @ExcelProperty("服务商名称")
    private String merchantName;

    @Schema(description = "服务商联系人")
    @ExcelProperty("服务商联系人")
    private String merchantContactName;

    @Schema(description = "服务商联系电话")
    @ExcelProperty("服务商联系电话")
    private String merchantContactMobile;

    @Schema(description = "信用规则ID")
    @ExcelProperty("信用规则ID")
    private Long ruleId;

    @Schema(description = "规则编码")
    @ExcelProperty("规则编码")
    private String ruleCode;

    @Schema(description = "规则名称")
    @ExcelProperty("规则名称")
    private String ruleName;

    @Schema(description = "分值变动")
    @ExcelProperty("分值变动")
    private Integer scoreChange;

    @Schema(description = "变动前分值")
    @ExcelProperty("变动前分值")
    private Integer beforeScore;

    @Schema(description = "变动后分值")
    @ExcelProperty("变动后分值")
    private Integer afterScore;

    @Schema(description = "触发类型")
    @ExcelProperty("触发类型")
    private String triggerType;

    @Schema(description = "业务类型")
    @ExcelProperty("业务类型")
    private String bizType;

    @Schema(description = "业务ID")
    @ExcelProperty("业务ID")
    private Long bizId;

    @Schema(description = "业务对象")
    @ExcelProperty("业务对象")
    private String bizDisplay;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
