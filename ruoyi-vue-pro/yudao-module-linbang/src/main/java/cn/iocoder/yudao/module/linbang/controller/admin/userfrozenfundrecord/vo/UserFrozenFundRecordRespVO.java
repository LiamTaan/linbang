package cn.iocoder.yudao.module.linbang.controller.admin.userfrozenfundrecord.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserFrozenFundRecordRespVO {

    private Long id;
    private Long userId;
    private String userNo;
    private String userNickname;
    private String userMobile;
    private Long walletAccountId;
    private BigDecimal frozenAmount;
    private BigDecimal releasedAmount;
    private String status;
    private String sourceBizType;
    private Long sourceBizId;
    private String reason;
    private LocalDateTime createTime;
}
