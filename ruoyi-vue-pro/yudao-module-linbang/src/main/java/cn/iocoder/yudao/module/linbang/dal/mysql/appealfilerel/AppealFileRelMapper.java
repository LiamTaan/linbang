package cn.iocoder.yudao.module.linbang.dal.mysql.appealfilerel;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appealfilerel.AppealFileRelDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AppealFileRelMapper extends BaseMapperX<AppealFileRelDO> {

    default List<AppealFileRelDO> selectListByAppealId(Long appealId) {
        return selectList(new LambdaQueryWrapperX<AppealFileRelDO>()
                .eq(AppealFileRelDO::getAppealId, appealId)
                .orderByAsc(AppealFileRelDO::getId));
    }
}
