package cn.iocoder.yudao.module.linbang.controller.admin.userfrozenfundrecord.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserFrozenFundRecordDetailRespVO extends UserFrozenFundRecordRespVO {

    private Long releasedBy;
    private LocalDateTime releasedTime;
    private String releaseRemark;
}
