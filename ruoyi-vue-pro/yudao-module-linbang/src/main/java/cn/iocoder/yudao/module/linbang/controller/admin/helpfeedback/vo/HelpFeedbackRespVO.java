package cn.iocoder.yudao.module.linbang.controller.admin.helpfeedback.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
public class HelpFeedbackRespVO {

    @ExcelProperty("主键")
    private Long id;

    @ExcelProperty("用户ID")
    private Long userId;

    @ExcelProperty("用户编号")
    private String userNo;

    @ExcelProperty("用户昵称")
    private String userNickname;

    @ExcelProperty("用户手机号")
    private String userMobile;

    @ExcelProperty("反馈分类")
    private String feedbackType;

    @ExcelProperty("反馈内容")
    private String content;

    @ExcelProperty("联系电话")
    private String contactMobile;

    @ExcelProperty("附件地址")
    private String attachmentUrls;

    @ExcelProperty("处理状态")
    private String status;

    @ExcelProperty("处理人")
    private Long handleBy;

    @ExcelProperty("处理备注")
    private String handleRemark;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
