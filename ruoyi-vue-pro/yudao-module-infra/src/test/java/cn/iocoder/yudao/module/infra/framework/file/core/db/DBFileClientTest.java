package cn.iocoder.yudao.module.infra.framework.file.core.db;

import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileContentDO;
import cn.iocoder.yudao.module.infra.dal.mysql.file.FileContentMapper;
import cn.iocoder.yudao.module.infra.framework.file.core.client.db.DBFileClient;
import cn.iocoder.yudao.module.infra.framework.file.core.client.db.DBFileClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DBFileClientTest {

    @Test
    void getContent_readsOnlyLatestRow() {
        FileContentMapper mapper = mock(FileContentMapper.class);
        DBFileClient client = buildClient(mapper);
        byte[] content = new byte[]{1, 2, 3};
        when(mapper.selectLatestByConfigIdAndPath(10L, "avatar/test.jpg"))
                .thenReturn(new FileContentDO().setContent(content));

        assertArrayEquals(content, client.getContent("avatar/test.jpg"));

        verify(mapper).selectLatestByConfigIdAndPath(10L, "avatar/test.jpg");
    }

    @Test
    void getContentWithLimit_usesDatabasePrefixQuery() {
        FileContentMapper mapper = mock(FileContentMapper.class);
        DBFileClient client = buildClient(mapper);
        byte[] content = new byte[]{1, 2, 3};
        when(mapper.selectLatestContentPrefix(10L, "avatar/test.jpg", 1025)).thenReturn(content);

        assertArrayEquals(content, client.getContent("avatar/test.jpg", 1024L));

        verify(mapper).selectLatestContentPrefix(10L, "avatar/test.jpg", 1025);
    }

    private DBFileClient buildClient(FileContentMapper mapper) {
        DBFileClientConfig config = new DBFileClientConfig();
        config.setDomain("https://cdn.example");
        DBFileClient client = new DBFileClient(10L, config);
        ReflectionTestUtils.setField(client, "fileContentMapper", mapper);
        return client;
    }

}
