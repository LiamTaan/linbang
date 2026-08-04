package cn.iocoder.yudao.module.infra.service.file;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.framework.test.core.util.AssertUtils;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FileCreateReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FilePageReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FilePresignedUrlRespVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.dal.mysql.file.FileMapper;
import cn.iocoder.yudao.module.infra.framework.file.core.client.FileClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils.buildTime;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.*;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_NOT_EXISTS;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_PATH_INVALID;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_TYPE_INVALID;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_UPLOAD_RESERVATION_INVALID;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_UPLOAD_SIZE_MISMATCH;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@Import({FileServiceImpl.class})
public class FileServiceImplTest extends BaseDbUnitTest {

    private static final Long LOGIN_USER_ID = 100L;
    private static final String LOGIN_OWNER_KEY = UserTypeEnum.ADMIN.getValue() + ":" + LOGIN_USER_ID;

    @Resource
    private FileServiceImpl fileService;

    @Resource
    private FileMapper fileMapper;

    @MockBean
    private FileConfigService fileConfigService;

    @BeforeEach
    public void setUp() {
        FileServiceImpl.PATH_PREFIX_DATE_ENABLE = true;
        FileServiceImpl.PATH_SUFFIX_TIMESTAMP_ENABLE = true;
        FileServiceImpl.PATH_SUFFIX_AS_DIRECTORY = true;
        LoginUser loginUser = new LoginUser();
        loginUser.setId(LOGIN_USER_ID);
        loginUser.setUserType(UserTypeEnum.ADMIN.getValue());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList()));
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGetFilePage() {
        // mock 数据
        FileDO dbFile = randomPojo(FileDO.class, o -> { // 等会查询到
            o.setPath("yunai");
            o.setType("image/jpg");
            o.setCreateTime(buildTime(2021, 1, 15));
        });
        fileMapper.insert(dbFile);
        // 测试 path 不匹配
        fileMapper.insert(ObjectUtils.cloneIgnoreId(dbFile, o -> o.setPath("tudou")));
        // 测试 type 不匹配
        fileMapper.insert(ObjectUtils.cloneIgnoreId(dbFile, o -> {
            o.setType("image/png");
        }));
        // 测试 createTime 不匹配
        fileMapper.insert(ObjectUtils.cloneIgnoreId(dbFile, o -> {
            o.setCreateTime(buildTime(2020, 1, 15));
        }));
        // 准备参数
        FilePageReqVO reqVO = new FilePageReqVO();
        reqVO.setPath("yunai");
        reqVO.setType("jp");
        reqVO.setCreateTime((new LocalDateTime[]{buildTime(2021, 1, 10), buildTime(2021, 1, 20)}));

        // 调用
        PageResult<FileDO> pageResult = fileService.getFilePage(reqVO);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        AssertUtils.assertPojoEquals(dbFile, pageResult.getList().get(0));
    }

    /**
     * content、name、directory、type 都非空
     */
    @Test
    public void testCreateFile_success_01() throws Exception {
        // 准备参数
        byte[] content = ResourceUtil.readBytes("file/erweima.jpg");
        String name = "单测文件名";
        String directory = randomString();
        String type = "image/jpeg";
        // mock Master 文件客户端
        FileClient client = mock(FileClient.class);
        when(fileConfigService.getMasterFileClient()).thenReturn(client);
        String url = randomString();
        AtomicReference<String> pathRef = new AtomicReference<>();
        when(client.upload(same(content), argThat(path -> {
            assertTrue(path.matches(directory + "/\\d{8}/[0-9a-f]{32}/" + name + "\\.jpg"));
            pathRef.set(path);
            return true;
        }), eq(type))).thenReturn(url);
        when(client.getId()).thenReturn(10L);
        // 调用
        String result = fileService.createFile(content, name, directory, type);
        // 断言
        assertEquals(result, url);
        // 校验数据
        FileDO file = fileMapper.selectOne(FileDO::getUrl, url);
        assertEquals(10L, file.getConfigId());
        assertEquals(pathRef.get(), file.getPath());
        assertEquals(url, file.getUrl());
        assertEquals(type, file.getType());
        assertEquals(content.length, file.getSize());
    }

    /**
     * content 非空，其它都空
     */
    @Test
    public void testCreateFile_success_02() throws Exception {
        // 准备参数
        byte[] content = ResourceUtil.readBytes("file/erweima.jpg");
        // mock Master 文件客户端
        String type = "image/jpeg";
        FileClient client = mock(FileClient.class);
        when(fileConfigService.getMasterFileClient()).thenReturn(client);
        String url = randomString();
        AtomicReference<String> pathRef = new AtomicReference<>();
        when(client.upload(same(content), argThat(path -> {
            assertTrue(path.matches("\\d{8}/[0-9a-f]{32}/6318848e882d8a7e7e82789d87608f684ee52d41966bfc8cad3ce15aad2b970e\\.jpg"));
            pathRef.set(path);
            return true;
        }), eq(type))).thenReturn(url);
        when(client.getId()).thenReturn(10L);
        // 调用
        String result = fileService.createFile(content, null, null, null);
        // 断言
        assertEquals(result, url);
        // 校验数据
        FileDO file = fileMapper.selectOne(FileDO::getUrl, url);
        assertEquals(10L, file.getConfigId());
        assertEquals(pathRef.get(), file.getPath());
        assertEquals(url, file.getUrl());
        assertEquals(type, file.getType());
        assertEquals(content.length, file.getSize());
    }

    @Test
    public void testDeleteFile_success() throws Exception {
        // mock 数据
        FileDO dbFile = randomPojo(FileDO.class, o -> o.setConfigId(10L).setPath("tudou.jpg"));
        fileMapper.insert(dbFile);// @Sql: 先插入出一条存在的数据
        // mock Master 文件客户端
        FileClient client = mock(FileClient.class);
        when(fileConfigService.getFileClient(eq(10L))).thenReturn(client);
        // 准备参数
        Long id = dbFile.getId();

        // 调用
        fileService.deleteFile(id);
        // 校验数据不存在了
        assertNull(fileMapper.selectById(id));
        // 校验调用
        verify(client).delete(eq("tudou.jpg"));
    }

    @Test
    public void testDeleteFile_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> fileService.deleteFile(id), FILE_NOT_EXISTS);
    }

    @Test
    public void testDeleteFile_pathInvalid() {
        // mock 数据
        FileDO dbFile = randomPojo(FileDO.class, o -> o.setConfigId(10L).setPath("../tudou.jpg"));
        fileMapper.insert(dbFile);

        // 调用，并断言异常
        assertServiceException(() -> fileService.deleteFile(dbFile.getId()), FILE_PATH_INVALID);
    }

    @Test
    public void testGetFileContent() throws Exception {
        // 准备参数
        Long configId = 10L;
        String path = "tudou.jpg";
        // mock 方法
        FileClient client = mock(FileClient.class);
        when(fileConfigService.getFileClient(eq(10L))).thenReturn(client);
        byte[] content = new byte[]{};
        when(client.getContent(eq("tudou.jpg"))).thenReturn(content);

        // 调用
        byte[] result = fileService.getFileContent(configId, path);
        // 断言
        assertSame(result, content);
    }

    @Test
    public void testGetFileContent_pathInvalid() {
        // 准备参数
        Long configId = 10L;
        String path = "../tudou.jpg";

        // 调用，并断言异常
        assertServiceException(() -> fileService.getFileContent(configId, path), FILE_PATH_INVALID);
    }

    @Test
    public void testGetFileContentWithLimit() throws Exception {
        Long configId = 10L;
        String path = "tudou.jpg";
        long maxBytes = 1024L;
        FileClient client = mock(FileClient.class);
        when(fileConfigService.getFileClient(configId)).thenReturn(client);
        byte[] content = new byte[]{1, 2, 3};
        when(client.getContent(path, maxBytes)).thenReturn(content);

        byte[] result = fileService.getFileContent(configId, path, maxBytes);

        assertSame(content, result);
        verify(client).getContent(path, maxBytes);
    }

    @Test
    public void testGetFileByConfigIdAndPath() {
        // mock 数据
        FileDO dbFile = randomPojo(FileDO.class, o -> o.setConfigId(10L).setPath("avatar/中文 100%+文件.jpg"));
        fileMapper.insert(dbFile);
        FileDO latestFile = ObjectUtils.cloneIgnoreId(dbFile, o -> o.setName("最新文件名.jpg"));
        fileMapper.insert(latestFile);
        fileMapper.insert(ObjectUtils.cloneIgnoreId(dbFile, o -> o.setPath("avatar/other.jpg")));
        fileMapper.insert(ObjectUtils.cloneIgnoreId(dbFile, o -> o.setConfigId(20L)));

        // 调用
        FileDO result = fileService.getFileByConfigIdAndPath(10L, "avatar/中文 100%+文件.jpg");

        // 断言
        AssertUtils.assertPojoEquals(latestFile, result);
    }

    @Test
    public void testPresignPutUrl_reservesIsolatedPathAndHeaders() throws Exception {
        FileClient client = mock(FileClient.class);
        when(client.getId()).thenReturn(10L);
        when(fileConfigService.getMasterFileClient()).thenReturn(client);
        AtomicReference<String> pendingPathRef = new AtomicReference<>();
        when(client.presignPutUrl(anyString(), eq(FileService.PRESIGNED_UPLOAD_CONTENT_TYPE), eq(2048L)))
                .thenAnswer(invocation -> {
                    pendingPathRef.set(invocation.getArgument(0));
                    return "https://storage.example/upload";
                });
        when(client.presignGetUrl(anyString(), isNull()))
                .thenAnswer(invocation -> "https://cdn.example/" + invocation.getArgument(0));

        FilePresignedUrlRespVO result = fileService.presignPutUrl("test.jpg", 2048L, "avatar");

        assertEquals(".pending/" + result.getPath(), pendingPathRef.get());
        assertEquals(FileService.PRESIGNED_UPLOAD_CONTENT_TYPE, result.getUploadContentType());
        assertTrue(result.getPath().matches("avatar/\\d{8}/[0-9a-f]{32}/test\\.jpg"));
        FileDO reservation = fileMapper.selectLatestByConfigIdAndPath(10L, result.getPath());
        assertNotNull(reservation);
        assertEquals(-2048L, reservation.getSize());
        assertEquals(LOGIN_OWNER_KEY, reservation.getUpdater());
        assertEquals(String.valueOf(LOGIN_USER_ID), reservation.getCreator());
    }

    @Test
    public void testCreateFileByPresignedPath_success() throws Exception {
        byte[] content = ResourceUtil.readBytes("file/erweima.jpg");
        FileDO reservation = insertPendingUpload("avatar/test.jpg", "test.jpg", content.length, LOGIN_OWNER_KEY);
        FileClient client = mock(FileClient.class);
        when(fileConfigService.getFileClient(10L)).thenReturn(client);
        when(client.getContent(".pending/avatar/test.jpg", FileService.MAX_FILE_SIZE_BYTES)).thenReturn(content);
        when(client.upload(same(content), eq("avatar/test.jpg"), eq("image/jpeg")))
                .thenReturn("https://www.iocoder.cn/test.jpg?token=server");
        FileCreateReqVO reqVO = randomPojo(FileCreateReqVO.class, o -> {
            o.setConfigId(10L);
            o.setPath("avatar/test.jpg");
            o.setName("attacker.html");
            o.setUrl("https://attacker.invalid/file");
            o.setType("text/html");
            o.setSize(1L);
        });

        Long fileId = fileService.createFile(reqVO);

        assertEquals(reservation.getId(), fileId);
        FileDO file = fileMapper.selectById(fileId);
        assertEquals("avatar/test.jpg", file.getPath());
        assertEquals("test.jpg", file.getName());
        assertEquals("https://www.iocoder.cn/test.jpg", file.getUrl());
        assertEquals("image/jpeg", file.getType());
        assertEquals(content.length, file.getSize());
        assertEquals(LOGIN_OWNER_KEY, file.getUpdater());
        verify(client).delete(".pending/avatar/test.jpg");
    }

    @Test
    public void testCreateFileByPresignedPath_rejectsDangerousContentAndCleansUp() throws Exception {
        byte[] content = "<html><script>alert(1)</script></html>".getBytes(StandardCharsets.UTF_8);
        FileDO reservation = insertPendingUpload("avatar/test.txt", "test.txt", content.length, LOGIN_OWNER_KEY);
        FileClient client = mock(FileClient.class);
        when(fileConfigService.getFileClient(10L)).thenReturn(client);
        when(client.getContent(".pending/avatar/test.txt", FileService.MAX_FILE_SIZE_BYTES)).thenReturn(content);
        FileCreateReqVO reqVO = randomPojo(FileCreateReqVO.class,
                o -> o.setConfigId(10L).setPath("avatar/test.txt"));

        assertServiceException(() -> fileService.createFile(reqVO), FILE_TYPE_INVALID);

        assertNull(fileMapper.selectById(reservation.getId()));
        verify(client).delete(".pending/avatar/test.txt");
        verify(client, never()).upload(any(), anyString(), anyString());
    }

    @Test
    public void testCreateFileByPresignedPath_rejectsSizeMismatchAndCleansUp() throws Exception {
        byte[] content = new byte[]{1, 2, 3};
        FileDO reservation = insertPendingUpload("avatar/test.bin", "test.bin", content.length + 1L, LOGIN_OWNER_KEY);
        FileClient client = mock(FileClient.class);
        when(fileConfigService.getFileClient(10L)).thenReturn(client);
        when(client.getContent(".pending/avatar/test.bin", FileService.MAX_FILE_SIZE_BYTES)).thenReturn(content);
        FileCreateReqVO reqVO = randomPojo(FileCreateReqVO.class,
                o -> o.setConfigId(10L).setPath("avatar/test.bin"));

        assertServiceException(() -> fileService.createFile(reqVO), FILE_UPLOAD_SIZE_MISMATCH);

        assertNull(fileMapper.selectById(reservation.getId()));
        verify(client).delete(".pending/avatar/test.bin");
    }

    @Test
    public void testCreateFileByPresignedPath_rejectsDifferentOwner() {
        insertPendingUpload("avatar/test.jpg", "test.jpg", 10L,
                UserTypeEnum.MEMBER.getValue() + ":" + LOGIN_USER_ID);
        FileCreateReqVO reqVO = randomPojo(FileCreateReqVO.class,
                o -> o.setConfigId(10L).setPath("avatar/test.jpg"));

        assertServiceException(() -> fileService.createFile(reqVO), FILE_UPLOAD_RESERVATION_INVALID);

        verify(fileConfigService, never()).getFileClient(anyLong());
    }

    @Test
    public void testCreateFileByPresignedPath_nameInvalid() throws Exception {
        byte[] content = ResourceUtil.readBytes("file/erweima.jpg");
        FileDO reservation = insertPendingUpload("avatar/test.jpg", "../test.jpg", content.length, LOGIN_OWNER_KEY);
        FileClient client = mock(FileClient.class);
        when(fileConfigService.getFileClient(10L)).thenReturn(client);
        when(client.getContent(".pending/avatar/test.jpg", FileService.MAX_FILE_SIZE_BYTES)).thenReturn(content);
        FileCreateReqVO reqVO = randomPojo(FileCreateReqVO.class,
                o -> o.setConfigId(10L).setPath("avatar/test.jpg"));

        assertServiceException(() -> fileService.createFile(reqVO), FILE_PATH_INVALID);
        assertNull(fileMapper.selectById(reservation.getId()));
        verify(client).delete(".pending/avatar/test.jpg");
    }

    @Test
    public void testCreateFileByPresignedPath_pathInvalid() {
        // 准备参数
        FileCreateReqVO reqVO = randomPojo(FileCreateReqVO.class, o -> {
            o.setPath("../test.jpg");
            o.setName("test.jpg");
        });

        // 调用，并断言异常
        assertServiceException(() -> fileService.createFile(reqVO), FILE_PATH_INVALID);
    }

    @Test
    public void testGenerateUploadPath_AllEnabled() {
        // 准备参数
        String name = "test.jpg";
        String directory = "avatar";
        FileServiceImpl.PATH_PREFIX_DATE_ENABLE = true;
        FileServiceImpl.PATH_SUFFIX_TIMESTAMP_ENABLE = true;

        // 调用
        String path = fileService.generateUploadPath(name, directory);

        // 断言
        // 格式为：avatar/yyyyMMdd/{32 位 UUID}/test.jpg
        assertTrue(path.startsWith(directory + "/"));
        // 包含日期格式：8 位数字，如 20240517
        assertTrue(path.matches(directory + "/\\d{8}/[0-9a-f]{32}/test\\.jpg"));
    }

    @Test
    public void testGenerateUploadPath_PrefixEnabled_SuffixDisabled() {
        // 准备参数
        String name = "test.jpg";
        String directory = "avatar";
        FileServiceImpl.PATH_PREFIX_DATE_ENABLE = true;
        FileServiceImpl.PATH_SUFFIX_TIMESTAMP_ENABLE = false;

        // 调用
        String path = fileService.generateUploadPath(name, directory);

        // 断言
        // 格式为：avatar/yyyyMMdd/test.jpg
        assertTrue(path.startsWith(directory + "/"));
        // 包含日期格式：8 位数字，如 20240517
        assertTrue(path.matches(directory + "/\\d{8}/test\\.jpg"));
    }

    @Test
    public void testGenerateUploadPath_PrefixDisabled_SuffixEnabled() {
        // 准备参数
        String name = "test.jpg";
        String directory = "avatar";
        FileServiceImpl.PATH_PREFIX_DATE_ENABLE = false;
        FileServiceImpl.PATH_SUFFIX_TIMESTAMP_ENABLE = true;

        // 调用
        String path = fileService.generateUploadPath(name, directory);

        // 断言
        // 格式为：avatar/{32 位 UUID}/test.jpg
        assertTrue(path.startsWith(directory + "/"));
        assertTrue(path.matches(directory + "/[0-9a-f]{32}/test\\.jpg"));
    }

    @Test
    public void testGenerateUploadPath_AllDisabled() {
        // 准备参数
        String name = "test.jpg";
        String directory = "avatar";
        FileServiceImpl.PATH_PREFIX_DATE_ENABLE = false;
        FileServiceImpl.PATH_SUFFIX_TIMESTAMP_ENABLE = false;

        // 调用
        String path = fileService.generateUploadPath(name, directory);

        // 断言
        // 格式为：avatar/test.jpg
        assertEquals(directory + "/" + name, path);
    }

    @Test
    public void testGenerateUploadPath_NoExtension() {
        // 准备参数
        String name = "test";
        String directory = "avatar";
        FileServiceImpl.PATH_PREFIX_DATE_ENABLE = true;
        FileServiceImpl.PATH_SUFFIX_TIMESTAMP_ENABLE = true;

        // 调用
        String path = fileService.generateUploadPath(name, directory);

        // 断言
        // 格式为：avatar/yyyyMMdd/{32 位 UUID}/test
        assertTrue(path.startsWith(directory + "/"));
        assertTrue(path.matches(directory + "/\\d{8}/[0-9a-f]{32}/test"));
    }

    @Test
    public void testGenerateUploadPath_DirectoryNull() {
        // 准备参数
        String name = "test.jpg";
        String directory = null;
        FileServiceImpl.PATH_PREFIX_DATE_ENABLE = true;
        FileServiceImpl.PATH_SUFFIX_TIMESTAMP_ENABLE = true;

        // 调用
        String path = fileService.generateUploadPath(name, directory);

        // 断言
        // 格式为：yyyyMMdd/{32 位 UUID}/test.jpg
        assertTrue(path.matches("\\d{8}/[0-9a-f]{32}/test\\.jpg"));
    }

    @Test
    public void testGenerateUploadPath_SuffixAsName_AllEnabled() {
        // 准备参数
        String name = "test.jpg";
        String directory = "avatar";
        FileServiceImpl.PATH_PREFIX_DATE_ENABLE = true;
        FileServiceImpl.PATH_SUFFIX_TIMESTAMP_ENABLE = true;
        FileServiceImpl.PATH_SUFFIX_AS_DIRECTORY = false;

        // 调用
        String path = fileService.generateUploadPath(name, directory);

        // 断言
        // 格式为：avatar/yyyyMMdd/test_{32 位 UUID}.jpg
        assertTrue(path.matches(directory + "/\\d{8}/test_[0-9a-f]{32}\\.jpg"));
    }

    @Test
    public void testGenerateUploadPath_SuffixAsName_PrefixDisabled() {
        // 准备参数
        String name = "test.jpg";
        String directory = "avatar";
        FileServiceImpl.PATH_PREFIX_DATE_ENABLE = false;
        FileServiceImpl.PATH_SUFFIX_TIMESTAMP_ENABLE = true;
        FileServiceImpl.PATH_SUFFIX_AS_DIRECTORY = false;

        // 调用
        String path = fileService.generateUploadPath(name, directory);

        // 断言
        // 格式为：avatar/test_{32 位 UUID}.jpg
        assertTrue(path.matches(directory + "/test_[0-9a-f]{32}\\.jpg"));
    }

    @Test
    public void testGenerateUploadPath_SuffixAsName_NoExtension() {
        // 准备参数
        String name = "test";
        String directory = "avatar";
        FileServiceImpl.PATH_PREFIX_DATE_ENABLE = true;
        FileServiceImpl.PATH_SUFFIX_TIMESTAMP_ENABLE = true;
        FileServiceImpl.PATH_SUFFIX_AS_DIRECTORY = false;

        // 调用
        String path = fileService.generateUploadPath(name, directory);

        // 断言
        // 格式为：avatar/yyyyMMdd/test_{32 位 UUID}
        assertTrue(path.matches(directory + "/\\d{8}/test_[0-9a-f]{32}"));
    }

    @Test
    public void testGenerateUploadPath_FileNameInvalid() {
        // 准备参数
        String name = "../test.jpg";
        String directory = "avatar";
        FileServiceImpl.PATH_PREFIX_DATE_ENABLE = false;
        FileServiceImpl.PATH_SUFFIX_TIMESTAMP_ENABLE = false;

        // 调用，并断言异常
        assertServiceException(() -> fileService.generateUploadPath(name, directory), FILE_PATH_INVALID);
    }

    @Test
    public void testGenerateUploadPath_DirectoryInvalid() {
        // 准备参数
        String name = "test.jpg";
        String directory = "../avatar";

        // 调用，并断言异常
        assertServiceException(() -> fileService.generateUploadPath(name, directory), FILE_PATH_INVALID);
    }

    @Test
    public void testGenerateUploadPath_DirectoryEmpty() {
        // 准备参数
        String name = "test.jpg";
        String directory = "";
        FileServiceImpl.PATH_PREFIX_DATE_ENABLE = true;
        FileServiceImpl.PATH_SUFFIX_TIMESTAMP_ENABLE = true;

        // 调用
        String path = fileService.generateUploadPath(name, directory);

        // 断言
        // 格式为：yyyyMMdd/{32 位 UUID}/test.jpg
        assertTrue(path.matches("\\d{8}/[0-9a-f]{32}/test\\.jpg"));
    }

    private FileDO insertPendingUpload(String path, String name, long expectedSize, String ownerKey) {
        FileDO reservation = new FileDO().setConfigId(10L).setPath(path).setName(name)
                .setUrl("https://cdn.example/" + path).setSize(-expectedSize);
        reservation.setUpdater(ownerKey);
        fileMapper.insert(reservation);
        return reservation;
    }

}
