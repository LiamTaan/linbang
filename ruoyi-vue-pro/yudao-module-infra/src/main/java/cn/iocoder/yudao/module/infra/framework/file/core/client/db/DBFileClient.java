package cn.iocoder.yudao.module.infra.framework.file.core.client.db;

import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileContentDO;
import cn.iocoder.yudao.module.infra.dal.mysql.file.FileContentMapper;
import cn.iocoder.yudao.module.infra.framework.file.core.client.AbstractFileClient;

/**
 * 基于 DB 存储的文件客户端的配置类
 *
 * @author 芋道源码
 */
public class DBFileClient extends AbstractFileClient<DBFileClientConfig> {

    private FileContentMapper fileContentMapper;

    public DBFileClient(Long id, DBFileClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {
        fileContentMapper = SpringUtil.getBean(FileContentMapper.class);
    }

    @Override
    public String upload(byte[] content, String path, String type) {
        FileContentDO contentDO = new FileContentDO().setConfigId(getId())
                .setPath(path).setContent(content);
        fileContentMapper.insert(contentDO);
        // 拼接返回路径
        return super.formatFileUrl(config.getDomain(), path);
    }

    @Override
    public void delete(String path) {
        fileContentMapper.deleteByConfigIdAndPath(getId(), path);
    }

    @Override
    public byte[] getContent(String path) {
        FileContentDO latest = fileContentMapper.selectLatestByConfigIdAndPath(getId(), path);
        return latest == null ? null : latest.getContent();
    }

    @Override
    public byte[] getContent(String path, long maxBytes) {
        if (maxBytes < 0 || maxBytes >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("maxBytes must be between 0 and Integer.MAX_VALUE - 1");
        }
        return fileContentMapper.selectLatestContentPrefix(getId(), path, (int) maxBytes + 1);
    }

}
