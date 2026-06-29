package cn.iocoder.yudao.module.linbang.dal.mysql.memberpoint;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberpoint.MemberPointRecordDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberPointRecordMapper extends BaseMapperX<MemberPointRecordDO> {

    default PageResult<MemberPointRecordDO> selectPageByUserId(Long userId, PageParam pageParam) {
        return selectPage(pageParam, new LambdaQueryWrapperX<MemberPointRecordDO>()
                .eq(MemberPointRecordDO::getUserId, userId)
                .orderByDesc(MemberPointRecordDO::getId));
    }
}
