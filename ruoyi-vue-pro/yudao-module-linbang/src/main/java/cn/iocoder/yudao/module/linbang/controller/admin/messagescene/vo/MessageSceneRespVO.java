package cn.iocoder.yudao.module.linbang.controller.admin.messagescene.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
public class MessageSceneRespVO {

    @ExcelProperty("主键")
    private Long id;

    @ExcelProperty("场景编码")
    private String sceneCode;

    @ExcelProperty("场景名称")
    private String sceneName;

    @ExcelProperty("消息分类")
    private String messageCategory;

    @ExcelProperty("默认渠道")
    private String defaultChannels;

    @ExcelProperty("强制短信")
    private Boolean mandatorySms;

    @ExcelProperty("支持语音")
    private Boolean voiceEnabled;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("业务类型")
    private String bizType;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
