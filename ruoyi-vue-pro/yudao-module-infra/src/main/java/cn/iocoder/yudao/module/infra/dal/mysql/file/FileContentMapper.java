package cn.iocoder.yudao.module.infra.dal.mysql.file;

import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileContentDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FileContentMapper extends BaseMapper<FileContentDO> {

    default void deleteByConfigIdAndPath(Long configId, String path) {
        this.delete(new LambdaQueryWrapper<FileContentDO>()
                .eq(FileContentDO::getConfigId, configId)
                .eq(FileContentDO::getPath, path));
    }

    default FileContentDO selectLatestByConfigIdAndPath(Long configId, String path) {
        return selectOne(new LambdaQueryWrapper<FileContentDO>()
                .eq(FileContentDO::getConfigId, configId)
                .eq(FileContentDO::getPath, path)
                .orderByDesc(FileContentDO::getId)
                .last("LIMIT 1"));
    }

    @Select("SELECT SUBSTRING(content, 1, #{maxLength}) FROM infra_file_content "
            + "WHERE config_id = #{configId} AND path = #{path} AND deleted = b'0' "
            + "ORDER BY id DESC LIMIT 1")
    byte[] selectLatestContentPrefix(@Param("configId") Long configId, @Param("path") String path,
                                     @Param("maxLength") int maxLength);

}
