package cn.iocoder.yudao.module.infra.dal.mysql.file;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FilePageReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件操作 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface FileMapper extends BaseMapperX<FileDO> {

    default PageResult<FileDO> selectPage(FilePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FileDO>()
                .likeIfPresent(FileDO::getPath, reqVO.getPath())
                .likeIfPresent(FileDO::getType, reqVO.getType())
                .betweenIfPresent(FileDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(FileDO::getId));
    }

    default FileDO selectLatestByConfigIdAndPath(Long configId, String path) {
        return selectPage(new Page<FileDO>(1, 1, false), new LambdaQueryWrapperX<FileDO>()
                .eq(FileDO::getConfigId, configId)
                .eq(FileDO::getPath, path)
                .orderByDesc(FileDO::getId)).getRecords().stream().findFirst().orElse(null);
    }

    default List<FileDO> selectListByConfigIdAfterId(Long configId, Long afterId, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 1000));
        return selectPage(new Page<FileDO>(1, safeLimit, false), new LambdaQueryWrapperX<FileDO>()
                .eq(FileDO::getConfigId, configId)
                .gt(afterId != null, FileDO::getId, afterId)
                .orderByAsc(FileDO::getId)).getRecords();
    }

    default List<FileDO> selectExpiredPendingUploads(String ownerKey, LocalDateTime expireBefore, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        return selectPage(new Page<FileDO>(1, safeLimit, false), new LambdaQueryWrapperX<FileDO>()
                .eq(FileDO::getUpdater, ownerKey)
                .lt(FileDO::getSize, 0L)
                .le(FileDO::getCreateTime, expireBefore)
                .orderByAsc(FileDO::getCreateTime)).getRecords();
    }

    default List<FileDO> selectExpiredPendingUploads(LocalDateTime expireBefore, Long afterId, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        return selectPage(new Page<FileDO>(1, safeLimit, false), new LambdaQueryWrapperX<FileDO>()
                .lt(FileDO::getSize, 0L)
                .le(FileDO::getCreateTime, expireBefore)
                .gt(afterId != null, FileDO::getId, afterId)
                .orderByAsc(FileDO::getId)).getRecords();
    }

    default Long selectActivePendingUploadCount(String ownerKey, LocalDateTime activeAfter) {
        return selectCount(new LambdaQueryWrapperX<FileDO>()
                .eq(FileDO::getUpdater, ownerKey)
                .lt(FileDO::getSize, 0L)
                .gt(FileDO::getCreateTime, activeAfter));
    }

}
