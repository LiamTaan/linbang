package cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
public class MerchantServicePointRespVO {

    @ExcelProperty("ID")
    private Long id;

    @ExcelProperty("服务商ID")
    private Long merchantId;

    @ExcelProperty("服务商名称")
    private String merchantName;

    @ExcelProperty("联系人")
    private String contactName;

    @ExcelProperty("联系手机")
    private String contactMobile;

    @ExcelProperty("服务点名称")
    private String pointName;

    @ExcelProperty("省")
    private String province;

    @ExcelProperty("市")
    private String city;

    @ExcelProperty("区")
    private String district;

    @ExcelProperty("街道")
    private String street;

    @ExcelProperty("详细地址")
    private String detailAddress;

    @ExcelProperty("经度")
    private BigDecimal longitude;

    @ExcelProperty("纬度")
    private BigDecimal latitude;

    @ExcelProperty("服务半径(公里)")
    private BigDecimal serviceRadiusKm;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
