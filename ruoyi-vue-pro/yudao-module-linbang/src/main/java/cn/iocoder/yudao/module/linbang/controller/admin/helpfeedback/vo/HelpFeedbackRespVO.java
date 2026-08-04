package cn.iocoder.yudao.module.linbang.controller.admin.helpfeedback.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
@Schema(description = "管理后台 - 帮助反馈 Response VO")
public class HelpFeedbackRespVO {

    @ExcelProperty("主键")
    @Schema(description = "主键 ID")
    private Long id;

    @ExcelProperty("用户ID")
    @Schema(description = "平台用户 ID，关联用户档案")
    private Long userId;

    @ExcelProperty("用户编号")
    @Schema(description = "平台用户业务编号")
    private String userNo;

    @ExcelProperty("用户昵称")
    @Schema(description = "用户昵称")
    private String userNickname;

    @ExcelProperty("用户手机号")
    @Schema(description = "用户手机号")
    private String userMobile;

    @ExcelProperty("反馈分类")
    @Schema(description = "反馈分类，按平台帮助反馈分类字典展示，例如功能建议、异常反馈、投诉建议")
    private String feedbackType;

    @ExcelProperty("反馈内容")
    @Schema(description = "业务正文内容")
    private String content;

    @ExcelProperty("联系电话")
    @Schema(description = "联系人手机号")
    private String contactMobile;

    @ExcelProperty("附件地址")
    @Schema(description = "附件访问地址列表")
    private String attachmentUrls;

    @ExcelProperty("处理状态")
    @Schema(description = "反馈处理状态：PENDING 待处理、PROCESSING 处理中、FINISHED 已完结")
    private String status;

    @ExcelProperty("处理人")
    @Schema(description = "处理人后台用户 ID")
    private Long handleBy;

    @ExcelProperty("处理备注")
    @Schema(description = "处理备注")
    private String handleRemark;

    @ExcelProperty("创建时间")
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
}
