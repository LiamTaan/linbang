package cn.iocoder.yudao.module.linbang.controller.admin.userrestrictrecord.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRestrictRecordDetailRespVO extends UserRestrictRecordRespVO {

    private Long releasedBy;
    private LocalDateTime releasedTime;
    private String releaseRemark;
}
