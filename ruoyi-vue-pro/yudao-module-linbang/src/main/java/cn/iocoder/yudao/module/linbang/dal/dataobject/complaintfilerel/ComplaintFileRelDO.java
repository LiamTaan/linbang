package cn.iocoder.yudao.module.linbang.dal.dataobject.complaintfilerel;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("lb_complaint_file_rel")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintFileRelDO {

    @TableId
    private Long id;

    private Long complaintId;

    private Long fileId;

}
