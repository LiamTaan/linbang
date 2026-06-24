package cn.iocoder.yudao.module.linbang.controller.admin.messagerecord.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
public class MessageRecordRespVO {

    @ExcelProperty("主键")
    private Long id;

    @ExcelProperty("模板ID")
    private Long templateId;

    @ExcelProperty("推送任务ID")
    private Long pushTaskId;

    @ExcelProperty("接收用户ID")
    private Long receiverUserId;

    @ExcelProperty("接收用户编号")
    private String receiverUserNo;

    @ExcelProperty("接收用户昵称")
    private String receiverUserNickname;

    @ExcelProperty("接收用户手机号")
    private String receiverUserMobile;

    @ExcelProperty("渠道类型")
    private String channelType;

    @ExcelProperty("业务类型")
    private String bizType;

    @ExcelProperty("业务ID")
    private Long bizId;

    @ExcelProperty("发送状态")
    private String sendStatus;

    @ExcelProperty("发送时间")
    private LocalDateTime sendTime;

    @ExcelProperty("失败原因")
    private String failReason;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
