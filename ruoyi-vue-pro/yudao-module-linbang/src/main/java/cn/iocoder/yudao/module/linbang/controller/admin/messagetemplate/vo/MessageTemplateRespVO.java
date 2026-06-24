package cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
public class MessageTemplateRespVO {

    @ExcelProperty("主键")
    private Long id;

    @ExcelProperty("模板编码")
    private String templateCode;

    @ExcelProperty("模板名称")
    private String templateName;

    @ExcelProperty("模板类型")
    private String templateType;

    @ExcelProperty("渠道类型")
    private String channelType;

    @ExcelProperty("模板内容")
    private String content;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
