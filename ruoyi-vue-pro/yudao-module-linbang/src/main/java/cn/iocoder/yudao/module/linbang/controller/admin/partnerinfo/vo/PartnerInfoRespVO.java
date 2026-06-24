package cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ExcelIgnoreUnannotated
public class PartnerInfoRespVO {

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

    @ExcelProperty("合作商名称")
    private String partnerName;

    @ExcelProperty("联系人")
    private String contactName;

    @ExcelProperty("联系人手机号")
    private String contactMobile;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    private List<String> regionAdcodes;
}
