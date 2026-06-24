package cn.iocoder.yudao.module.linbang.dal.dataobject.appealfilerel;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("lb_appeal_file_rel")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppealFileRelDO {

    @TableId
    private Long id;

    private Long appealId;

    private Long fileId;

}
