package cn.iocoder.yudao.module.linbang.service.messagepushtask;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessagePushDispatchTarget {

    private Long receiverUserId;

    private Long bizId;

    private String dedupeKey;

    public MessagePushDispatchTarget(Long receiverUserId, Long bizId) {
        this.receiverUserId = receiverUserId;
        this.bizId = bizId;
    }
}
