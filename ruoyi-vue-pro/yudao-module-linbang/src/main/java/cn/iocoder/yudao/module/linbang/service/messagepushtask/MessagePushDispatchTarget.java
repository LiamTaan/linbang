package cn.iocoder.yudao.module.linbang.service.messagepushtask;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessagePushDispatchTarget {

    private Long receiverUserId;

    private Long bizId;
}
