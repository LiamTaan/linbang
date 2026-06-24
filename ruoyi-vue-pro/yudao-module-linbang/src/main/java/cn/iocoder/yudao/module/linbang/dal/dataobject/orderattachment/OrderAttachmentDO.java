package cn.iocoder.yudao.module.linbang.dal.dataobject.orderattachment;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("lb_order_attachment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAttachmentDO {

    @TableId
    private Long id;

    private Long orderId;

    private Long fileId;

    private String fileType;

    private Integer sortNo;

}
