package cn.iocoder.yudao.module.linbang.controller.admin.blacklist.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
public class BlacklistRespVO {

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

    @ExcelProperty("黑名单类型")
    private String blackType;

    @ExcelProperty("原因")
    private String reason;

    @ExcelProperty("开始时间")
    private LocalDateTime startTime;

    @ExcelProperty("结束时间")
    private LocalDateTime endTime;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
