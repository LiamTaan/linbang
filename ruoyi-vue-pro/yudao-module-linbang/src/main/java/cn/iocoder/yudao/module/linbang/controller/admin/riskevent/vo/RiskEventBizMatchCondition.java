package cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor
public class RiskEventBizMatchCondition {

    private String bizType;

    private Collection<Long> bizIds;

}
