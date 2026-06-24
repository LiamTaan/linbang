package cn.iocoder.yudao.module.linbang.dal.mysql.complaintfilerel;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaintfilerel.ComplaintFileRelDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ComplaintFileRelMapper extends BaseMapperX<ComplaintFileRelDO> {

    default List<ComplaintFileRelDO> selectListByComplaintId(Long complaintId) {
        return selectList(new LambdaQueryWrapperX<ComplaintFileRelDO>()
                .eq(ComplaintFileRelDO::getComplaintId, complaintId)
                .orderByAsc(ComplaintFileRelDO::getId));
    }
}
